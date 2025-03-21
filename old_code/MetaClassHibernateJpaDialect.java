package org.nanotek.repository.data;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.ResourceTransactionDefinition;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

public class MetaClassHibernateJpaDialect extends HibernateJpaDialect {

	public MetaClassHibernateJpaDialect() {
	}
	
	@Override
	public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition)
			throws PersistenceException, SQLException, TransactionException {

		SessionImplementor session = getSession(entityManager);

		if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
			session.getTransaction().setTimeout(definition.getTimeout());
		}

		boolean isolationLevelNeeded = (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT);
		Integer previousIsolationLevel = null;
		Connection preparedCon = null;

		if (isolationLevelNeeded || definition.isReadOnly()) {
			if ( ConnectionReleaseMode.ON_CLOSE.equals(
					session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode().getReleaseMode())) {
				preparedCon = session.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
				previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(preparedCon, definition);
			}
			else if (isolationLevelNeeded) {
				throw new InvalidIsolationLevelException(
						"HibernateJpaDialect is not allowed to support custom isolation levels: " +
						"make sure that its 'prepareConnection' flag is on (the default) and that the " +
						"Hibernate connection release mode is set to ON_CLOSE.");
			}
		}

		// Standard JPA transaction begin call for full JPA context setup...
		entityManager.getTransaction().begin();

		// Adapt flush mode and store previous isolation level, if any.
		FlushMode previousFlushMode = prepareFlushMode(session, definition.isReadOnly());
		if (definition instanceof ResourceTransactionDefinition rtd && rtd.isLocalResource()) {
			// As of 5.1, we explicitly optimize for a transaction-local EntityManager,
			// aligned with native HibernateTransactionManager behavior.
			previousFlushMode = null;
			if (definition.isReadOnly()) {
				session.setDefaultReadOnly(true);
			}
		}
		return new SessionTransactionData(
				session, previousFlushMode, (preparedCon != null), previousIsolationLevel, definition.isReadOnly());
	}

	private static class SessionTransactionData {

		private final SessionImplementor session;

		@Nullable
		private final FlushMode previousFlushMode;

		private final boolean needsConnectionReset;

		@Nullable
		private final Integer previousIsolationLevel;

		private final boolean readOnly;

		public SessionTransactionData(SessionImplementor session, @Nullable FlushMode previousFlushMode,
				boolean connectionPrepared, @Nullable Integer previousIsolationLevel, boolean readOnly) {

			this.session = session;
			this.previousFlushMode = previousFlushMode;
			this.needsConnectionReset = connectionPrepared;
			this.previousIsolationLevel = previousIsolationLevel;
			this.readOnly = readOnly;
		}

		public void resetSessionState() {
			if (this.previousFlushMode != null) {
				this.session.setHibernateFlushMode(this.previousFlushMode);
			}
			if (this.needsConnectionReset &&
					this.session.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected()) {
				Connection con = this.session.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
				DataSourceUtils.resetConnectionAfterTransaction(
						con, this.previousIsolationLevel, this.readOnly);
			}
		}
	}


	private static class HibernateConnectionHandle implements ConnectionHandle {

		private final SessionImplementor session;

		public HibernateConnectionHandle(SessionImplementor session) {
			this.session = session;
		}

		@Override
		public Connection getConnection() {
			return this.session.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
		}
	}

}

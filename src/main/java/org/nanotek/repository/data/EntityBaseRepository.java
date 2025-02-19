package org.nanotek.repository.data;

import org.springframework.transaction.annotation.Transactional;

public interface EntityBaseRepository<T , ID> extends EntityRepository<T, ID>{
	
	@Override
	@Transactional(transactionManager = "transactionManager")
	<S extends T> S saveAndFlush(S entity);
}

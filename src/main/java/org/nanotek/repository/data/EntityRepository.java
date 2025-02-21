package org.nanotek.repository.data;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface EntityRepository<T,ID> extends JpaRepositoryImplementation<T, ID> , QueryByExampleExecutor<T> {
}
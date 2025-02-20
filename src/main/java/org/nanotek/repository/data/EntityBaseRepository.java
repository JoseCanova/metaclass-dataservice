package org.nanotek.repository.data;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface EntityBaseRepository<T , ID> extends EntityRepository<T, ID>{
	
	@Override
	<S extends T> S saveAndFlush(S entity);
	
	@Override
	void deleteAll();
	
}

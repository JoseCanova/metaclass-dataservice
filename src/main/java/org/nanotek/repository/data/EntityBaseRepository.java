package org.nanotek.repository.data;

import org.nanotek.Base;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface EntityBaseRepository<T , ID> 
extends EntityRepository<T, ID> , Base<EntityBaseRepository<T,ID>>{
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	<S extends T> S saveAndFlush(S entity);
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	void deleteAll();
	
}

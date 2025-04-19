package org.nanotek;


import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nanotek.config.RepositoryClassBuilder;
import org.nanotek.config.RepositoryPair;
import org.nanotek.meta.model.rdbms.RdbmsIndex;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.BuilderMetaClass;
import org.nanotek.metaclass.BuilderMetaClassRegistry;
import org.nanotek.metaclass.ProcessedForeignKeyRegistry;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.metaclass.bytebuddy.attributes.AttributeBaseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;

/**
 * The Initializer needs a "redesign" now that the ForeignKeys and Index were added 
 * to the MetaClass configuration.
 * The Class construction now need to be decomposed into basic 4 steps.
 * First Step the Preparation that will construct the "Class Skeleton" 
 * that is simply the class definition without any internal structure.
 * Second step is to define the identity attributes of the class (for now considering a model with a simple 
 * primary key).
 * Third step is the configuration of attributes that are internal attributes 
 * of the "rdbms table" which means they are not dependent of
 */
public interface ApplicationInitializer 
extends ClassConfigurationInitializer{
	
	
	default void configureRepositoryClasses (List<Class<?>> theList
											,MetaClassVFSURLClassLoader byteArrayClassLoader,
											 MetaClassRegistry<?> metaClassRegistry ) throws Exception{
		
		theList.forEach(clazz ->{
    		metaClassRegistry.registryEntityClass(Class.class.<Class<Base<?>>>cast(clazz));
    		prepareRepositoryClass(clazz, byteArrayClassLoader, metaClassRegistry);});
//		System.exit(0);
	}
	

	/**
	 * This method classify the relation type between to classes in a relation-table considering the happy path
	 * which means the indexes were properly defined by the relational model administrator.
	 * @param indexes
	 * @return
	 */
		
		default Class<?> prepareRepositoryClass(Class<?> entityClass , 
				MetaClassVFSURLClassLoader classLoader,
				MetaClassRegistry<?> metaClassRegistry){
			
			Class<?> idClass = getIdClass(entityClass);
			RepositoryPair pair = RepositoryClassBuilder.prepareReppositoryForClass(entityClass, idClass);
			try {
				Class<?> repClass =  classLoader.defineClass(pair.repositoryName(), pair.unloaded().getBytes());
				Class<Base<?>> repBaseClass = Class.class.<Class<Base<?>>>cast(repClass);
				metaClassRegistry.registryRepositoryClass(Class.class.<Class<Base<?>>>cast(entityClass), repBaseClass);
				return repClass;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	    }
		

		
}

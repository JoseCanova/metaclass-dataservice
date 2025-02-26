package org.nanotek.test.config;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.nanotek.config.MetaClassClassesStore;
import org.nanotek.config.RepositoryClassesBuilder;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class MetaClassCustomRepositoryBean {


	
	
	@Bean
	@Primary
	@Qualifier(value="repositoryClassesMap")
	RepositoryClassesBuilder  repositoryClassesMap(
			@Autowired MetaClassVFSURLClassLoader classLoader , 
			@Autowired MetaClassClassesStore persistenceUnitClassesMap) {
		var repositoryClassesMap = new  RepositoryClassesBuilder  ();
		persistenceUnitClassesMap.forEach((x,y)->{
			Class<?> idClass = getIdClass(y);
			Class <?> repClass = repositoryClassesMap.prepareReppositoryForClass(y, idClass, classLoader);
			
			repositoryClassesMap.put(repClass.getTypeName() , repClass);
			
			System.err.println(y.getSimpleName());
		});
		return repositoryClassesMap;
	}
	
	
	private Class<?> getIdClass(Class<?> y) {
		return Stream.of(y.getDeclaredFields())
		.filter( f -> hasIdAnnotation(f))
		.map(f -> f.getType())
		.findFirst().orElseThrow();
	}
	
	private Boolean hasIdAnnotation(Field f) {
		return Stream.of(f.getAnnotations()).filter(a ->a.annotationType().equals(jakarta.persistence.Id.class)).count()==1;
	}

}

package org.nanotek.test.config;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.nanotek.config.MetaClassClassesStore;
import org.nanotek.config.MetaClassClassesStore;
import org.nanotek.config.RepositoryClassesBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class MetaClassCustomRepositoryBean {


	@Bean
	RepositoryClassesBuilder repositoryClassesBuilder() {
		
		return new RepositoryClassesBuilder();
	}
	
	
	@Bean
	@Primary
	@Qualifier(value="repositoryClassesMap")
	MetaClassClassesStore  repositoryClassesMap(
			@Autowired InjectionClassLoader classLoader , 
			@Autowired MetaClassClassesStore persistenceUnitClassesMap, 
			@Autowired RepositoryClassesBuilder repositoryClassesBuilder) {
		var repositoryClassesMap = new  MetaClassClassesStore  ();
		persistenceUnitClassesMap.forEach((x,y)->{
			Class<?> idClass = getIdClass(y);
			Class <?> repClass = repositoryClassesBuilder.prepareReppositoryForClass(y, idClass, classLoader);
			
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

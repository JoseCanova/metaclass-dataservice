package org.nanotek.test.config;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.nanotek.config.PersistenceUnityClassesMap;
import org.nanotek.config.RepositoryClassesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class MetaClassCustomRepositoryBean {


	@Bean
	@Primary
	@Qualifier(value="repositoryClassesMap")
	RepositoryClassesMap repositoryClassesMap(
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap) {
		var repositoryClassesMap = new RepositoryClassesMap();
		persistenceUnitClassesMap.forEach((x,y)->{
			Class<?> idClass = getIdClass(y);
			Class <?> repClass = repositoryClassesMap.prepareReppositoryForClass(y, idClass, classLoader);
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

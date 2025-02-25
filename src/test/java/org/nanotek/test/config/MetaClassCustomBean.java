package org.nanotek.test.config;


import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import org.nanotek.config.PersistenceUnityClassesMap;
import org.nanotek.config.RepositoryClassesBuilder;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

@Configuration
public class MetaClassCustomBean {

	@Bean
	@Primary
	PersistenceUnityClassesMap persistenceUnitClassesMap(@Autowired InjectionClassLoader injectionClassLoader) {
		PersistenceUnityClassesMap persistenceUnitClassesMap = new PersistenceUnityClassesMap();
		Class<?> clazz = metaClass(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz.getTypeName(),clazz);
		Class<?> clazz1 = metaClassNumeric(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz1.getTypeName(),clazz1);
		Class<?> clazz2 = metaClassDate(injectionClassLoader);
		persistenceUnitClassesMap.put(clazz2.getTypeName(),clazz2);
		return persistenceUnitClassesMap;
	}
	
	Class<?> metaClass(InjectionClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/metaclass.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	Class<?> metaClassNumeric(InjectionClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/metaclass_numeric.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
		
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	Class<?> metaClassDate(InjectionClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(getClass().getResourceAsStream("/meta_class_date.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	


	@Bean
	@Primary
	@Qualifier(value="repositoryClassesMap")
	RepositoryClassesBuilder repositoryClassesMap(
			@Autowired InjectionClassLoader classLoader , 
			@Autowired PersistenceUnityClassesMap persistenceUnitClassesMap) {
		var repositoryClassesMap = new RepositoryClassesBuilder();
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

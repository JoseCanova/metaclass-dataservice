package org.nanotek.test.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;
import org.nanotek.test.jpa.repositories.RepositoryClassBuilder;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

public class TestSaveMetaClass {

	public TestSaveMetaClass() {
	}

	public static void main(String[] args) {
		InjectionClassLoader ic = new  MultipleParentClassLoader(Thread.currentThread().getContextClassLoader() 
				, Arrays.asList(TestSaveMetaClass.class.getClassLoader() , 
						CrudMethodMetadata.class.getClassLoader() , 
						AbstractEntityManagerFactoryBean.class.getClassLoader())  , 
						false);
		TestSaveMetaClass tsm = new TestSaveMetaClass();
		tsm.metaClass(ic);
		tsm.metaClassDate(ic);
		tsm.metaClassNumeric(ic);
	}
	
	void metaClass(InjectionClassLoader injectionClassLoader) {
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
			Class<?> clazz =  Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			saveClazz(clazz , injectionClassLoader,eb.getBytes());
			RepositoryClassBuilder rb = new RepositoryClassBuilder();
			Class<?> rec = rb.prepareReppositoryForClass(clazz, String.class, injectionClassLoader);
			saveRepositoryClazz(rec , injectionClassLoader,rb.getBytes());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void saveRepositoryClazz(Class<?> clazz , ClassLoader classLoader, byte[] bs) throws IOException {
		String fileLocation = "/home/jose/git/metaclass-dataservice/target/classes/org/nanotek/data/repositories/";
		String fileName = fileLocation.concat(clazz.getSimpleName().concat(".class"));
		String classPath = clazz.getTypeName().replace('.', '/');// + ".class";
        InputStream classStream = classLoader.getResourceAsStream(classPath);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(bs);
        }
	}
	private void saveClazz(Class<?> clazz , ClassLoader classLoader, byte[] bs) throws IOException {
		String fileLocation = "/home/jose/git/metaclass-dataservice/target/classes/org/nanotek/data/";
		String fileName = fileLocation.concat(clazz.getSimpleName().concat(".class"));
		String classPath = clazz.getTypeName().replace('.', '/');// + ".class";
        InputStream classStream = classLoader.getResourceAsStream(classPath);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(bs);
        }
	}

	void metaClassNumeric(InjectionClassLoader injectionClassLoader) {
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
			Class<?> clazz =  Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			saveClazz(clazz , injectionClassLoader,eb.getBytes());
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	void metaClassNumericHundred(InjectionClassLoader injectionClassLoader,
//			PersistenceUnityClassesMap persistenceUnitClassesMap) {
//		ObjectMapper objectMapper = new ObjectMapper();
//    	List<JsonNode> list;
//		try {
//			list = objectMapper.readValue
//						(getClass().getResourceAsStream("/metaclass_hundred_numeric.json")
//								, List.class);
//			Object theNode = list.get(0);
//			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
//			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
//			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
//			Class.forName(loaded.getTypeName(), false, injectionClassLoader);
//			persistenceUnitClassesMap.put(loaded.getTypeName(), loaded);
//			} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	void metaClassDate(InjectionClassLoader injectionClassLoader) {
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
			Class<?> clazz = Class.forName(loaded.getTypeName(), false, injectionClassLoader);
			saveClazz(clazz , injectionClassLoader,eb.getBytes());
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

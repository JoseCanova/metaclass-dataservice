package org.nanotek;


import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nanotek.config.MetaClassRegistry;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassBuilder;
import org.nanotek.config.RepositoryPair;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassAttribute;
import org.nanotek.metaclass.BuilderMetaClass;
import org.nanotek.metaclass.BuilderMetaClassRegistry;
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
public interface ApplicationInitializer {
	
	public static final BuilderMetaClassRegistry builderMetaClassRegistry 
							= new BuilderMetaClassRegistry();
	
	public static void configureMetaClasses (String uriEndpont
											,MetaClassVFSURLClassLoader byteArrayClassLoader,
											MetaClassRegistry<?> metaClassRegistry ) throws Exception{
		
		List<RdbmsMetaClass> metaClasses = getMetaClasses(uriEndpont); 
		metaClasses.forEach(mc ->{
			mountRdbmsMetaClassConfiguration(mc);
			prepareSimpleAttributes(mc);
			prepareForeignAttributes(mc);
		});
		
		metaClasses.forEach(mc->{
			try {
					String key = mc.getTableName();
					BuilderMetaClass bmc=builderMetaClassRegistry.getBuilderMetaClass(key);
					Unloaded<?> unLoaded  = bmc.builder().make();
					String className = unLoaded.getTypeDescription().getActualName();
		    		Class<?> clazz = byteArrayClassLoader.defineClass(className, unLoaded.getBytes());
		    		metaClassRegistry.registryEntityClass(Class.class.<Class<Base<?>>>cast(clazz));
		    		prepareRepositoryClass(clazz, byteArrayClassLoader, metaClassRegistry);
			} catch (Exception e) {
	    		throw new RuntimeException(e);
	    	}
		});
		
	}
	
	public static void prepareSimpleAttributes(RdbmsMetaClass mc) {
	
				
				BuilderMetaClass bmc = builderMetaClassRegistry.getBuilderMetaClass(mc.getTableName());
				Builder <?> builder=bmc.builder();
				Builder <?> attributeBuilder = AttributeBaseBuilder
												.on().generateClassAttributes(mc , 
														builder);
				BuilderMetaClass abmc = new BuilderMetaClass(attributeBuilder,mc);
				builderMetaClassRegistry.registryBuilderMetaClass(mc.getTableName(), abmc);
	}

	public static void prepareForeignAttributes(RdbmsMetaClass mc) {
		BuilderMetaClass bmc = builderMetaClassRegistry.getBuilderMetaClass(mc.getTableName());
		Builder <?> builder=bmc.builder();
		Builder <?> foreignAttributeBuilder = AttributeBaseBuilder
				.on().generateForeignKeyClassAttributes(mc , 
						builder,builderMetaClassRegistry);
				BuilderMetaClass fabmc = new BuilderMetaClass(foreignAttributeBuilder,mc);
				builderMetaClassRegistry.registryBuilderMetaClass(mc.getTableName(), fabmc);
	}

	public static RdbmsEntityBaseBuddy mountRdbmsMetaClassConfiguration(RdbmsMetaClass mc) {
		var base = prepareEntityBaseBuddy(mc);
		var byteBuddy = base.generateByteBuddy();
		var baseBuilder = base.initializeInternalStatebuilder(byteBuddy, mc);
		BuilderMetaClass bmc = new BuilderMetaClass(baseBuilder,mc);
		
		builderMetaClassRegistry
						.registryBuilderMetaClass(mc.getTableName(), bmc);
		return base;
	}

	public static RdbmsEntityBaseBuddy prepareEntityBaseBuddy(RdbmsMetaClass mc) {
		return RdbmsEntityBaseBuddy.instance(mc);
	}

	public static List<RdbmsMetaClass> getMetaClasses(String uriEndpont) throws Exception{
			ObjectMapper mapper = new ObjectMapper();
			URI serverUri = new URI(uriEndpont);
			serverUri.toURL();
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<List> response = restTemplate.getForEntity(serverUri, List.class);
			List<?>	responseBody = response.getBody();
			List<RdbmsMetaClass> resultList = responseBody
				.stream()
				.map(e ->{
					return mapper.convertValue(e, RdbmsMetaClass.class);
				}).collect(Collectors.toList());
			return resultList;
		}
		
	/**
	 * This method is deprecated since the new 
	 * @deprecated 
	 * @param theClass
	 * @param classLoader
	 * @param metaClassRegistry
	 * @return
	 */
		public static Class<?> metaClass(RdbmsMetaClass theClass , 
										MetaClassVFSURLClassLoader classLoader , 
										MetaClassRegistry<?> metaClassRegistry) {
	    	try {
	    		RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
	    		Unloaded<?> loaded = eb.getBytesForClassLoader();
	    		String className = loaded.getTypeDescription().getActualName();
	    		Class<?> clazz = classLoader.defineClass(className, loaded.getBytes());
	    		metaClassRegistry.registryEntityClass(Class.class.<Class<Base<?>>>cast(clazz));
	    		return  clazz;	
	    	} catch (Exception e) {
	    		throw new RuntimeException(e);
	    	}
	    }
		
		public static Class<?> prepareRepositoryClass(Class<?> entityClass , 
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
		
		 private static Class<?> getIdClass(Class<?> y) {
				return Stream.of(y.getDeclaredFields())
				.filter( f -> hasIdAnnotation(f))
				.map(f -> f.getType())
				.findFirst().orElseThrow();
			}
		 
		 private static Boolean hasIdAnnotation(Field f) {
				return Stream.of(f.getAnnotations()).filter(a ->a.annotationType().equals(jakarta.persistence.Id.class)).count()==1;
			}

		
}

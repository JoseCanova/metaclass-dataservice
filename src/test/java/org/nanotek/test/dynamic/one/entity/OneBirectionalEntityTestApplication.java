package org.nanotek.test.dynamic.one.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.nanotek.Base;
import org.nanotek.TestRedefineClass;
import org.nanotek.config.MetaClassRegistry;
import org.nanotek.config.MetaClassVFSURLClassLoader;
import org.nanotek.config.RepositoryClassBuilder;
import org.nanotek.config.RepositoryPair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;


//TODO: Change the model definition (specify also new table model) to define a dynamic type for a one to one relation model.
@SpringBootApplication
public class OneBirectionalEntityTestApplication {

	public static final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
	
	public static final MetaClassVFSURLClassLoader byteArrayClassLoader  = new MetaClassVFSURLClassLoader 
																			(TestRedefineClass.class.getClassLoader() , 
																					false ,fileSystem);

	public static final MetaClassRegistry<?> metaClassRegistry  = new  MetaClassRegistry<>();
	
	@Bean
	@Qualifier(value="injectionClassLoader")
	InjectionClassLoader injectionClassLoader() {
		return byteArrayClassLoader;
	}
	
	@Bean
	@Primary
	MetaClassVFSURLClassLoader vfsClassLoader() {
		return byteArrayClassLoader;
	}
	
	@Bean 
	@Primary
	 MetaClassRegistry<?> metaClassRegistry(){
		return metaClassRegistry;
	}
		
	
	List<?> classes;
	public OneBirectionalEntityTestApplication() {
	}
	
	
	public static void main(String[] args) {
		OneBirectionalEntityTestApplication ba = new OneBirectionalEntityTestApplication();
		List<Class<?>> model = ba.createModel();
		assertTrue (model.size()==2);
		
		model.stream().forEach(c ->{
			prepareRepositoryClass(c, byteArrayClassLoader);
		});
		
		
		AnnotationConfigServletWebServerApplicationContext context  = 
        		(AnnotationConfigServletWebServerApplicationContext) 
        		new SpringApplication(new PathMatchingResourcePatternResolver(OneBirectionalEntityTestApplication.byteArrayClassLoader),
        				OneBirectionalEntityTestApplication.class, OneBirectionalJpaDataServiceConfiguration.class ).run(args);
	
	}
	
	public static Class<?> prepareRepositoryClass(Class<?> entityClass , 
			MetaClassVFSURLClassLoader classLoader){
		Class<?> idClass = getIdClass(entityClass);
		RepositoryPair pair = RepositoryClassBuilder.prepareReppositoryForClass(entityClass, idClass);
		try {
			Class<?> repClass =  classLoader.defineClass(pair.repositoryName(), pair.unloaded().getBytes());
			Class<Base<?>> repBaseClass = Class.class.<Class<Base<?>>>cast(repClass);
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

	//TODO: change model and annotation description names.
	private List<Class<?>> createModel() {
		
		String packageName = MetaClassVFSURLClassLoader.ENTITY_PATH.replaceAll("[/]", ".");
		
		TypeDefinition td = TypeDescription.Generic.Builder.parameterizedType(Base.class  ,Base.class).build();

		AnnotationDescription personAnnotationEntity = AnnotationDescription.
															Builder.ofType(Entity.class)
															.define("name", "Person").build();
		
		AnnotationDescription personAnnotationTable = AnnotationDescription
														.Builder.ofType(Table.class)
														.define("name", "person").build();

		AnnotationDescription petAnnotationEntity = AnnotationDescription.
														Builder.ofType(Entity.class)
														.define("name", "Pet").build();

		AnnotationDescription petAnnotationTable = AnnotationDescription
														.Builder.ofType(Table.class)
															.define("name", "pet").build();
		
		
		AnnotationDescription idAnnotationDescription = AnnotationDescription.Builder.ofType(Id.class).build();
		
		AnnotationDescription personKeyColumnAnnotationDescription = AnnotationDescription
																		.Builder.ofType(Column.class)
																		.define("name", "person_key")
																		.define("nullable", false).build();
		
		AnnotationDescription personNameColumnAnnotationDescription = AnnotationDescription
																		.Builder.ofType(Column.class)
																		.define("name", "person_name")
																		.define("nullable", false).build();
		
		AnnotationDescription petKeyColumnAnnotationDescription = AnnotationDescription
																			.Builder.ofType(Column.class)
																			.define("name", "pet_key")
																			.define("nullable", false).build();

		AnnotationDescription petNameColumnAnnotationDescription = AnnotationDescription
																			.Builder.ofType(Column.class)
																			.define("name", "pet_name")
																			.define("nullable", false).build();
		
		AnnotationDescription petPersonManyAnnotationDescription = AnnotationDescription
																		.Builder.ofType(ManyToOne.class).build();
		
		AnnotationDescription petPersonJoinAnnotationDescription = AnnotationDescription
																		.Builder.ofType(JoinColumn.class)
																		.define("name", "person_key")
																		.define("nullable", false).build();
		
        TypeDescription cascadeTypeTd = new TypeDescription.ForLoadedType(CascadeType.class);
        
        EnumerationDescription cascadeTypeEd = new EnumerationDescription.ForLoadedEnumeration(CascadeType.ALL);
        var av = AnnotationValue.ForDescriptionArray.of(cascadeTypeTd, new EnumerationDescription[]{cascadeTypeEd});
        
		//TODO:Verify how to include the enumeration CascadeType or other values than native.
		AnnotationDescription personPetOneAnnotationDescription = AnnotationDescription
																	.Builder.ofType(OneToMany.class)
																	.define("cascade", av )
																	.define("mappedBy", "person").build();
		
		Builder<?> personBuilder = new ByteBuddy()
				.subclass(td)
				.name(packageName.concat("Person"))
				.annotateType(personAnnotationEntity)
				.annotateType(personAnnotationTable)
				.defineProperty("personKey", java.lang.String.class)
				.annotateField(new AnnotationDescription[] {idAnnotationDescription,personKeyColumnAnnotationDescription})
				.defineProperty("personName", java.lang.String.class)
				.annotateField(new AnnotationDescription[] {personNameColumnAnnotationDescription});
			
			TypeDescription tdPerson = personBuilder.toTypeDescription();
			
			Class<?> dogClass = new ByteBuddy()
			.subclass(td)
				.name(packageName.concat("Pet"))
				.annotateType(petAnnotationEntity)
				.annotateType(petAnnotationTable)
				.defineProperty("petKey", java.lang.String.class)
				.annotateField(new AnnotationDescription[] {idAnnotationDescription,petKeyColumnAnnotationDescription})
				.defineProperty("petName", java.lang.String.class)
				.annotateField(new AnnotationDescription[] {petNameColumnAnnotationDescription})
				.defineProperty("person", tdPerson)
				.annotateField(new AnnotationDescription[] {petPersonManyAnnotationDescription,petPersonJoinAnnotationDescription})
			    .withHashCodeEquals()
				.withToString()
			    .make()
			    .load(byteArrayClassLoader).getLoaded();
			
			TypeDefinition pettd = TypeDescription.Generic.Builder.parameterizedType(java.util.Set.class  ,dogClass).build();
			
			Class<?> personClass = personBuilder
					.defineProperty("pets", pettd)
					.annotateField(new AnnotationDescription[] {personPetOneAnnotationDescription})
			.make()
			.load(byteArrayClassLoader).getLoaded();
		List<Class<?>> model = new ArrayList<>();
		model.add(personClass);
		model.add(dogClass);
		return model;
		
	}

}

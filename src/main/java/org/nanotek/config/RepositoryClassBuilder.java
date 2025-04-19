package org.nanotek.config;

import java.util.Optional;

import org.nanotek.MetaClassVFSURLClassLoader;
import org.nanotek.repository.data.EntityBaseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;

public class RepositoryClassBuilder   {

	public static final String basePackage = MetaClassVFSURLClassLoader.REPO_PATH.replaceAll("[/]", ".") ;
	

	public static RepositoryPair prepareReppositoryForClass(Class<?> clazz , Class<?> idClass){
		Generic typeDescription = TypeDescription.Generic.Builder
										.parameterizedType(EntityBaseRepository.class, clazz , idClass)
										.build()
										.asGenericType();
		Entity theEntity = clazz.getAnnotation(Entity.class);
		Optional.ofNullable(theEntity).orElseThrow();
		String repositoryName = basePackage.concat(theEntity.name()).concat("Repository");
		DynamicType.Unloaded<?> unloaded =   new ByteBuddy(ClassFileVersion.JAVA_V22)
//				.makeInterface(EntityBaseRepository.class)
				.makeInterface(typeDescription)
				.name(repositoryName)
				.annotateType( AnnotationDescription.Builder.ofType(Repository.class)
						.build())
				.annotateType( AnnotationDescription.Builder.ofType(Qualifier.class)
						.define("value",  theEntity.name().concat("Repository"))
						.build())//collectionResourceRel = "people", path = "people"
				.annotateType( AnnotationDescription.Builder.ofType(RepositoryRestResource.class)
						.define("collectionResourceRel",  theEntity.name().toLowerCase())
						.define("path", theEntity.name().toLowerCase())
						.build())
				.make();
			return new RepositoryPair(repositoryName,unloaded);
	}

}

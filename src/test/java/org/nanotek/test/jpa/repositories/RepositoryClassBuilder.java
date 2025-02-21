package org.nanotek.test.jpa.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType.Unloaded;

public class RepositoryClassBuilder {

	private byte[] bytes;

	public RepositoryClassBuilder() {
	}
	
	public Class<?> prepareReppositoryForClass(Class<?> clazz , Class<?> idClass, ClassLoader classLoader){
		Generic typeDescription = TypeDescription.Generic.Builder.parameterizedType( JpaRepository.class, clazz , idClass).build().asGenericType();
		Entity theEntity = clazz.getAnnotation(Entity.class);
		Optional.ofNullable(theEntity).orElseThrow();
		Unloaded<?> unloaded =   new ByteBuddy(ClassFileVersion.JAVA_V22)
//				.makeInterface(EntityBaseRepository.class)
				.makeInterface(typeDescription)
				.name( "org.nanotek.data.repositories." + theEntity.name() +"Repository")
				.annotateType( AnnotationDescription.Builder.ofType(Repository.class)
						.build())
				.annotateType( AnnotationDescription.Builder.ofType(Qualifier.class)
						.define("value",  theEntity.name()+"Repository")
						.build())
				.annotateType(		AnnotationDescription.Builder.ofType(RepositoryDefinition.class)
						.define("domainClass",clazz)
						.define("idClass", idClass)
						.build()
						)
				.make();
		this.bytes = unloaded.getBytes();
		return unloaded.load(classLoader).getLoaded();
	}

	public byte[] getBytes() {
		return bytes;
	}

}

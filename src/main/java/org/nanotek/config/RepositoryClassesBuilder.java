package org.nanotek.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;

public class RepositoryClassesBuilder   {

	private HashMap<String, Class<?>> entityStore;

	public RepositoryClassesBuilder(){
		constructMap() ;
	}

	private void constructMap() {
		entityStore = new HashMap<>();
	}


	public Class<?> prepareReppositoryForClass(Class<?> clazz , Class<?> idClass, MetaClassVFSURLClassLoader classLoader){
		Generic typeDescription = TypeDescription.Generic.Builder.parameterizedType(JpaRepository.class, clazz , idClass).build().asGenericType();
		Entity theEntity = clazz.getAnnotation(Entity.class);
		Optional.ofNullable(theEntity).orElseThrow();
		DynamicType.Unloaded<?> unloaded =   new ByteBuddy(ClassFileVersion.JAVA_V22)
//				.makeInterface(EntityBaseRepository.class)
				.makeInterface(typeDescription)
				.name( "org.nanotek.execution.config.spring.repositories." + theEntity.name() +"Repository")
				.annotateType( AnnotationDescription.Builder.ofType(Repository.class)
						.build())
				.annotateType( AnnotationDescription.Builder.ofType(Qualifier.class)
						.define("value",  theEntity.name()+"Repository")
						.build())
//				.annotateType(AnnotationDescription.Builder.ofType(RepositoryDefinition.class)
//						.define("domainClass",clazz)
//						.define("idClass", idClass)
//						.build()
//						)
				.make();
				
		Class<?> cd =			unloaded.load(classLoader).getLoaded();
		try {
			classLoader.saveClassFile(cd.getTypeName(), unloaded.getBytes());
			Class<?> theclazz = Class.forName(cd.getTypeName(),true , classLoader);
			System.err.println(cd.toGenericString());
			put(theEntity.name(), theclazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return cd;
	}

	public int size() {
		return entityStore.size();
	}

	public boolean isEmpty() {
		return entityStore.isEmpty();
	}

	public Class<?> get(Object key) {
		return entityStore.get(key);
	}

	public boolean containsKey(Object key) {
		return entityStore.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return entityStore.containsValue(value);
	}

	public Class<?> put(String key, Class<?> value) {
		return entityStore.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Class<?>> m) {
		entityStore.putAll(m);
	}

	public Class<?> remove(Object key) {
		return entityStore.remove(key);
	}

	public void clear() {
		entityStore.clear();
	}

	public Collection<Class<?>> values() {
		return entityStore.values();
	}

	public Set<Entry<String, Class<?>>> entrySet() {
		return entityStore.entrySet();
	}

	public int hashCode() {
		return entityStore.hashCode();
	}

	public String toString() {
		return entityStore.toString();
	}

	public boolean equals(Object o) {
		return entityStore.equals(o);
	}

	public Class<?> putIfAbsent(String key, Class<?> value) {
		return entityStore.putIfAbsent(key, value);
	}

	public boolean remove(Object key, Object value) {
		return entityStore.remove(key, value);
	}

	public boolean replace(String key, Class<?> oldValue, Class<?> newValue) {
		return entityStore.replace(key, oldValue, newValue);
	}

	public Class<?> replace(String key, Class<?> value) {
		return entityStore.replace(key, value);
	}

	public Class<?> getOrDefault(Object key, Class<?> defaultValue) {
		return entityStore.getOrDefault(key, defaultValue);
	}

	public void forEach(BiConsumer<? super String, ? super Class<?>> action) {
		entityStore.forEach(action);
	}

	public void replaceAll(BiFunction<? super String, ? super Class<?>, ? extends Class<?>> function) {
		entityStore.replaceAll(function);
	}

	public Class<?> computeIfAbsent(String key, Function<? super String, ? extends Class<?>> mappingFunction) {
		return entityStore.computeIfAbsent(key, mappingFunction);
	}

	public Class<?> computeIfPresent(String key,
			BiFunction<? super String, ? super Class<?>, ? extends Class<?>> remappingFunction) {
		return entityStore.computeIfPresent(key, remappingFunction);
	}

	public Class<?> compute(String key,
			BiFunction<? super String, ? super Class<?>, ? extends Class<?>> remappingFunction) {
		return entityStore.compute(key, remappingFunction);
	}

	public Class<?> merge(String key, Class<?> value,
			BiFunction<? super Class<?>, ? super Class<?>, ? extends Class<?>> remappingFunction) {
		return entityStore.merge(key, value, remappingFunction);
	}

}

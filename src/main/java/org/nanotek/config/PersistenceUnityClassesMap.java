package org.nanotek.config;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.bytebuddy.TypeCache;
import net.bytebuddy.TypeCache.Sort;

public class PersistenceUnityClassesMap implements Map<String , Class<?>>{

	private Map<String, Class<?>> entityStore=new HashMap<String , Class<?>>();
	
	TypeCache<String> typeCache ;
	
	public PersistenceUnityClassesMap() {
		typeCache = new TypeCache.WithInlineExpunction<>(Sort.SOFT); 
	}

	public int size() {
		return entityStore.size();
	}

	public boolean isEmpty() {
		return entityStore.isEmpty();
	}

	public boolean containsKey(Object key) {
		return entityStore.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return entityStore.containsValue(value);
	}

	public Class<?> get(Object key) {
		return entityStore.get(key);
	}

	public Class<?> put(String key, Class<?> value) {
		return entityStore.put(key, value);
	}

	public Class<?> remove(Object key) {
		return entityStore.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Class<?>> m) {
		entityStore.putAll(m);
	}

	public void clear() {
		entityStore.clear();
	}

	public Set<String> keySet() {
		return entityStore.keySet();
	}

	public Collection<Class<?>> values() {
		return entityStore.values();
	}

	public Set<Entry<String, Class<?>>> entrySet() {
		return entityStore.entrySet();
	}

	public boolean equals(Object o) {
		return entityStore.equals(o);
	}

	public int hashCode() {
		return entityStore.hashCode();
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

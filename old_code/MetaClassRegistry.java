package org.nanotek.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.nanotek.Base;

public class MetaClassRegistry<T extends Base<?>> {
	
	Map<UUID , Class<Base<?>>> classRegistry;
	
	Map<MultiKey<UUID> , Class<Base<?>>> repositoryRegistry;
	
	public MetaClassRegistry() {
		postConstruct();
	}
	
	private void postConstruct() {
		classRegistry = new DualHashBidiMap<UUID , Class<Base<?>>> ();
		repositoryRegistry = new DualHashBidiMap<MultiKey<UUID> , Class<Base<?>>> ();
	}

	public UUID registryEntityClass(Class<Base<?>> entityClass) {

		UUID uuid = Base.withUUID(entityClass);
		Optional.
		ofNullable(classRegistry.get(uuid))
		.orElse(classRegistry.put(uuid, entityClass));
		return uuid;
		
	}

	public Class<Base<?>> get(UUID uuid){
		throw new RuntimeException("Not yet implemented");
	}
	
	public Class<Base<?>> getEntityClass(UUID uuid){
		return Optional.ofNullable(classRegistry.get(uuid)).orElseThrow(EntityNotFoundException::new);
	}
	
	public Class<Base<?>> getRepositoryClass(UUID uuid) {
		return repositoryRegistry
					.keySet()
					.stream()
					.filter(mk -> mk.getKey(0).equals(uuid) || mk.getKey(1).equals(uuid))
					.limit(1)
					.map(mk -> repositoryRegistry.get(mk))
					.findFirst().orElseThrow(RepositoryNotFoundException::new);
	}
	
	public List<Class<Base<?>>> getEntityClasses() {
		return classRegistry
					.values()
					.stream()
					.collect(Collectors.toList());
	}
	
	public MultiKey<UUID> registryRepositoryClass(Class<Base<?>> entityClass , Class<Base<?>> repositoryClass){
		UUID entityUUID = Base.withUUID(entityClass);
		return Optional
			.ofNullable(classRegistry.get(entityUUID))
			.map(clazz -> {
				var repositoryUUID = Base.withUUID(repositoryClass);
				var key = new MultiKey<UUID>(entityUUID , repositoryUUID);
				repositoryRegistry.put(key , repositoryClass);
				return key;
			})
			.orElseThrow(EntityNotFoundException::new);
	}
	
	public static class EntityNotFoundException extends RuntimeException{
		public EntityNotFoundException() {
			super ("Entity Class Not in Registry");
		}
	}

	public static class RepositoryNotFoundException extends RuntimeException{
		public RepositoryNotFoundException() {
			super ("Repository Class Not in Registry");
		}
	}



}

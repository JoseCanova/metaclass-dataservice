package org.nanotek.config.spring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Map;

import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassFilePostProcessor;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;

public class MetaClassByteArrayClassLoader extends ByteArrayClassLoader {

	public Map<String,byte[]> byteArray;
	
	public MetaClassByteArrayClassLoader(ClassLoader parent, Map<String, byte[]> typeDefinitions) {
		super(parent, typeDefinitions);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions) {
		super(parent, sealed, typeDefinitions);
		byteArray = typeDefinitions;
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, Map<String, byte[]> typeDefinitions,
			PersistenceHandler persistenceHandler) {
		super(parent, typeDefinitions, persistenceHandler);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions,
			PersistenceHandler persistenceHandler) {
		super(parent, sealed, typeDefinitions, persistenceHandler);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, Map<String, byte[]> typeDefinitions,
			ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler,
			PackageDefinitionStrategy packageDefinitionStrategy) {
		super(parent, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions,
			ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler,
			PackageDefinitionStrategy packageDefinitionStrategy) {
		super(parent, sealed, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, Map<String, byte[]> typeDefinitions,
			ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler,
			PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
		super(parent, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy,
				classFilePostProcessor);
		// TODO Auto-generated constructor stub
	}

	public MetaClassByteArrayClassLoader(ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions,
			ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler,
			PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
		super(parent, sealed, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy,
				classFilePostProcessor);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		if(name.contains("org/nanotek/config/spring/repositories") || name.contains("org/nanotek/config/spring/data")){
			String nameResource = name.replace(".class", "").replaceAll("[/]", ".");
			byte[] bytes = byteArray.get(nameResource);
			if (bytes !=null)
				return new ByteArrayInputStream(bytes);
			}
		return super.getResourceAsStream(name);
	}
	
	@Override
	public URL getResource(String name) {
		if(name.contains("org/nanotek/config/spring/repositories") || name.contains("org/nanotek/config/spring/data")){
			String nameResource = name.replace(".class", "").replaceAll("[/]", ".");
			byte[] bytes = byteArray.get(nameResource);
			if (bytes !=null)
				try {
					return new URL("classpath:".concat(nameResource));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		return super.getResource(name);
	}

}

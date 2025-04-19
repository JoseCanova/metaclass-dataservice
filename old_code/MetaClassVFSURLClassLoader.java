package org.nanotek.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

/**
 */
public class MetaClassVFSURLClassLoader extends InjectionClassLoader {

	FileSystem fileSystem;
	
	public static final String REPO_PATH="jimfs/org/nanotek/config/spring/repositories/";
	public static final String ENTITY_PATH="org/nanotek/config/spring/data/";
	public static final String SERVICE_PATH="org/nanotek/config/spring/services/";
	
	
	public MetaClassVFSURLClassLoader(ClassLoader parent, boolean sealed, FileSystem fileSystem) {
		super(parent, false);
		this.fileSystem = fileSystem;
		postConstruct();
	}

	private void postConstruct() {
		try { 
			createEntityFileDirectory();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Map<String, Class<?>> doDefineClasses(Map<String, byte[]> typeDefinitions) throws ClassNotFoundException {
		System.err.println("do define classes");
		Map<String, Class<?>> types = new HashMap<String, Class<?>>();
		for (Map.Entry<String, byte[]> entry : typeDefinitions.entrySet()) {
			Class<?> clazz = defineClass(entry.getKey(), entry.getValue(), 0, entry.getValue().length);
			String typeName = clazz.getName();
			String simpleName = clazz.getSimpleName();
			types.put(entry.getKey(), clazz);
			try {
				saveFile(typeName,simpleName,entry.getValue());
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return types;
	}

	private void saveFile(String className, String simpleName, byte[] entityBytes2) {
    	try {
    		String  directoryString  = className.replaceAll("[.]", "/").replace(simpleName, "");
    		Path rootPath = fileSystem.getPath(directoryString, new String[0]);
        	Path classPath = rootPath.resolve(simpleName.concat(".class"));
        	if(!Files.exists(classPath, LinkOption.NOFOLLOW_LINKS))
        			Files.createFile(classPath, new FileAttribute[0]);
			Files.write(classPath, entityBytes2, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	private  void createEntityFileDirectory() throws IOException {
    		Path dataPath = fileSystem.getPath(ENTITY_PATH, new String[]{});
            Files.createDirectories(dataPath);
        	Path repoPath = fileSystem.getPath(REPO_PATH,new String[]{});
            Files.createDirectories(repoPath);
            Path servicePath = fileSystem.getPath(SERVICE_PATH,new String[]{});
            Files.createDirectories(servicePath);
    }
	
	@Override
	public InputStream getResourceAsStream(String name) {
//		System.err.println("getResourceAsStream " + name);
		Path thePath = fileSystem.getPath(name, new String[0]);
    	boolean bol = Files.exists(thePath, new LinkOption[0]);
    	InputStream is = null;
    	try {
    		if(bol)
			 is = Files.newInputStream(thePath, StandardOpenOption.READ);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bol && is !=null? is : super.getResourceAsStream(name);
	}

	@Override
	public URL getResource(String name) {
//		System.err.println("getResource " + name);
		Path thePath = fileSystem.getPath(name, new String[0]);
    	boolean bol = Files.exists(thePath, new LinkOption[0]);
    	URL theURL = null;
    	try {
    		if(bol)	
    			theURL = thePath.toUri().toURL();
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
		return bol && theURL!=null? theURL : super.getResource(name);
	}

   @Override
	public Enumeration<URL> getResources(String name) throws IOException {
	   if (name.equals(REPO_PATH))
	   {
		   List<URL> files =  new ArrayList<>();
		   System.err.println("getResoures Repositories " +  name);
		   Path theRepoPath = fileSystem.getPath(name, new String[0]);
		   if (Files.exists(theRepoPath, new LinkOption[0])) {
           	files.add(theRepoPath.toUri().toURL());	
           	return Collections.enumeration(files);
		   }
		   try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(theRepoPath)) {
	            for (Path file : directoryStream) {
	            	files.add(file.toUri().toURL());	
	            }
	            return Collections.enumeration(files);
	        }
	   }
	   
	   if (name.equals(ENTITY_PATH))
	   {
		   List<URL> files =  new ArrayList<>();
		   System.err.println("getResoures Data " +  name);
		   Path thePath = fileSystem.getPath(name, new String[0]);
		   if (Files.exists(thePath, new LinkOption[0])) {
           	files.add(thePath.toUri().toURL());	
		   }
		   try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(thePath)) {
	            for (Path file : directoryStream) {
	            	files.add(file.toUri().toURL());	
	            }
	            return Collections.enumeration(files);
	        }
	   }
	  
	   return super.getResources(name);
   }
}




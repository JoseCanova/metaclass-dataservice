package org.nanotek.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

/**
 */
public class MetaClassVFSURLClassLoader extends InjectionClassLoader {

	Map<String , FileObject> loadedClasses; 
	FileSystem fileSystem;

	public MetaClassVFSURLClassLoader(ClassLoader parent, boolean sealed, FileSystem fileSystem) {
		super(parent, false);
		this.fileSystem = fileSystem;
		postConstruct();
	}


	private void postConstruct() {
		try { 
			
			loadedClasses = new HashMap<>();
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
        	Files.createFile(classPath, new FileAttribute[0]);
			Files.write(classPath, entityBytes2, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	private  void createEntityFileDirectory() {
    	try {
        	Path orgPath = fileSystem.getPath("org", new String[]{});
        	Path nanoPath = fileSystem.getPath("org/nanotek", new String[]{});//config.spring.data
        	Path configPath = fileSystem.getPath("org/nanotek/config", new String[]{});
        	Path springPath = fileSystem.getPath("org/nanotek/config/spring",new String[]{});
        	Path dataPath = fileSystem.getPath("org/nanotek/config/spring/data",new String[]{});
        	Path repoPath = fileSystem.getPath("org/nanotek/config/spring/repositories",new String[]{});
			Files.createDirectory(orgPath);
			Files.createDirectory(nanoPath);
			Files.createDirectory(configPath);
			Files.createDirectory(springPath);
			Files.createDirectory(dataPath);
			Files.createDirectory(repoPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Override
	public InputStream getResourceAsStream(String name) {
		System.err.println("getResourceAsStream " + name);
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
		System.err.println("getResource " + name);
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
	   if (name.equals("org/nanotek/config/spring/repositories/"))
	   {
		   List<URL> files =  new ArrayList<>();
		   System.err.println("getResoures " +  name);
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
	  
	   return super.getResources(name);
   }
//
//	   List<FileObject> classes =  new ArrayList<>();
//	   loadedClasses
//	    .entrySet()
//	    .stream()
//	    .filter((k) -> k.getKey().contains(keyname) || keyname.contains(k.getKey()))
//	    .forEach (k -> {
//	    	FileObject foo = k.getValue();
//	    	System.err.println("contains " + foo);
//	    	classes.add(foo);
//	    });
//	   if(classes.size() == 0)
//	    return super.getResources(name);
//	   else 
//	   {	List <URL> urls = new ArrayList<>();
//		   classes.forEach(c -> {
//			   try {
////				   URI cc = new URI("file" , c.getURL().getPath(),c.getURL().getFile());
//				   urls.add(c.getURL());
//			   }catch (Exception e) {
//				   e.printStackTrace();
//			   }
//		   });
//		   return Collections.enumeration(urls);
//	   }
//	}
}




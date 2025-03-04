package org.nanotek.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class MetaClassVFSURLClassLoader extends URLClassLoader {

	static FileSystemManager fsManager;
	static FileObject fileObject; 
	static FileObject jarObject;
	
	Set<FileObject> files = new HashSet<FileObject>();
	
    public MetaClassVFSURLClassLoader(URL[] urls) {
        super(urls);
    }

    public MetaClassVFSURLClassLoader(URL[] urls, InjectionClassLoader injectionClassLoader) {
    	super(urls,injectionClassLoader);
    }

	public static MetaClassVFSURLClassLoader createVFSClassLoader(String vfsUrl, InjectionClassLoader injectionClassLoader) throws Exception {
        fsManager = VFS.getManager();
        fileObject = fsManager.resolveFile(vfsUrl);
        URL url = fileObject.getURL();
        return new MetaClassVFSURLClassLoader(new URL[]{url},injectionClassLoader);
    }

	public static MetaClassVFSURLClassLoader createVFSClassLoader(URL[] urls, InjectionClassLoader injectionClassLoader) throws Exception {

		fsManager = VFS.getManager();
        fileObject = fsManager.resolveFile("ram://");
        URL url = fileObject.getURL();
        
        return new MetaClassVFSURLClassLoader(urls,injectionClassLoader);
    }

	
	
	public URL getBaseURl() {
		try {
		fileObject = fsManager.resolveFile("ram://");
        return fileObject.getURL();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    public void saveClassFile(String typeName, byte[] bytes) throws IOException {
    	String filePath = "ram://".concat(typeName.replaceAll("[.]" , "/").concat(".class"));
    	System.out.println(filePath);
        FileObject fileObject = fsManager.resolveFile(filePath);
    	try  {
            // Write the byte array to the file
    		OutputStream outputStream = fileObject.getContent().getOutputStream();
            outputStream.write(bytes);
            outputStream.close();
            files.add(fileObject);   
        }catch(Exception ex) {
        	throw new RuntimeException(ex);
        }		
	}

    public OutputStream createJarFile() {
        try {
			FileObject fileObject = fsManager.resolveFile("ram://classes.jar");
			jarObject = fileObject;
			
    		return fileObject.getContent().getOutputStream();
		} catch (FileSystemException e) {
			e.printStackTrace();
        	throw new RuntimeException(e);
		}
    }
    
    
	public static FileSystemManager getFsManager() {
		return fsManager;
	}

	public Set<FileObject> getFiles() {
		return files;
	}

	public static FileObject getJarObject() {
		return jarObject;
	}

}
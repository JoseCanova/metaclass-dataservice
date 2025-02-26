package org.nanotek.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.bytebuddy.RdbmsEntityBaseBuddy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

public class VFSClassLoader extends URLClassLoader {

	static FileSystemManager fsManager;
	static FileObject fileObject; 
	static String baseUrl="/home/jose/git/metaclass-dataservice/target/test-classes/";
	
    public VFSClassLoader(URL[] urls) {
        super(urls);
    }

    public VFSClassLoader(URL[] urls, InjectionClassLoader injectionClassLoader) {
    	super(urls,injectionClassLoader);
    }

	public static VFSClassLoader createVFSClassLoader(String vfsUrl, InjectionClassLoader injectionClassLoader) throws Exception {
        fsManager = VFS.getManager();
        fileObject = fsManager.resolveFile(vfsUrl);
        URL url = fileObject.getURL();
        return new VFSClassLoader(new URL[]{url},injectionClassLoader);
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
    	try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            // Write the byte array to the file
            outputStream.write(bytes);
        }		
	}

}
package org.nanotek.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
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
	
    public VFSClassLoader(URL[] urls) {
        super(urls);
    }

    public static VFSClassLoader createVFSClassLoader(String vfsUrl) throws Exception {
        fsManager = VFS.getManager();
        fileObject = fsManager.resolveFile(vfsUrl);
        URL url = fileObject.getURL();
        return new VFSClassLoader(new URL[]{url});
    }

    public static Class<?> metaClassNumeric(VFSClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(VFSClassLoader.class.getResourceAsStream("/metaclass_numeric.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
			byte bytes[] = eb.getBytes();
			saveClassFile(loaded.getTypeName() , bytes);
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
			} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    public static Class<?> metaClass(VFSClassLoader injectionClassLoader) {
		ObjectMapper objectMapper = new ObjectMapper();
    	List<JsonNode> list;
		try {
			list = objectMapper.readValue
						(VFSClassLoader.class.getResourceAsStream("/metaclass.json")
								, List.class);
			Object theNode = list.get(0);
			RdbmsMetaClass theClass = objectMapper.convertValue(theNode,RdbmsMetaClass.class);
			RdbmsEntityBaseBuddy eb = RdbmsEntityBaseBuddy.instance(theClass);
			Class<?> loaded = eb.getLoadedClassInDefaultClassLoader(injectionClassLoader);
		     byte bytes[] = eb.getBytes();
		     saveClassFile(loaded.getTypeName() , bytes);
			return  Class.forName(loaded.getTypeName(), false, injectionClassLoader);	
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    private static void saveClassFile(String typeName, byte[] bytes) throws IOException {
    	String filePath = "ram://".concat(typeName.replaceAll("[.]" , "/").concat(".class"));
    	System.out.println(filePath);
        FileObject fileObject = fsManager.resolveFile(filePath);
    	try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            // Write the byte array to the file
            outputStream.write(bytes);
        }		
	}

	public static void main(String[] args) {
        try {
            // Example VFS URL: "file:///path/to/your/classes/"
            String vfsUrl = "ram://";
            VFSClassLoader classLoader = createVFSClassLoader(vfsUrl);
            Class<?> clazz = metaClass(classLoader);
//            Class<?> clazz = classLoader.loadClass("com.example.MyClass");
            System.out.println("Class loaded: " + clazz.getName());
            clazz = metaClassNumeric(classLoader);
            System.out.println("Class loaded: " + clazz.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
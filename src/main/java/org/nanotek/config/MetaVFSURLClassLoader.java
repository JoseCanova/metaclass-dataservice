package org.nanotek.config;

import java.util.List;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

/**
 * @deprecated
 */
public class MetaVFSURLClassLoader extends MultipleParentClassLoader {

	
	public MetaVFSURLClassLoader(List<? extends ClassLoader> arg1) {
		super(arg1);
	}
	
	
	
	public MetaVFSURLClassLoader(ClassLoader arg0, List<? extends ClassLoader> arg1, boolean arg2) {
		super(arg0, arg1, arg2);
	}



	public MetaVFSURLClassLoader(ClassLoader arg0, List<? extends ClassLoader> arg1) {
		super(arg0, arg1);
	}



	public static MetaVFSURLClassLoader createVFSClassLoader(List<? extends ClassLoader> parents) throws Exception {
        return new MetaVFSURLClassLoader(parents);
    }

	public static MetaVFSURLClassLoader createVFSClassLoader(InjectionClassLoader injectionClassLoader) throws Exception {

        return new MetaVFSURLClassLoader(List.of(injectionClassLoader));
    }
	
//    public void saveClassFile(String typeName, byte[] bytes) throws IOException {
//    	String filePath = "ram://".concat(typeName.replaceAll("[.]" , "/").concat(".class"));
//    	System.out.println(filePath);
//        FileObject fileObject = fsManager.resolveFile(filePath);
//    	try  {
//            // Write the byte array to the file
//    		OutputStream outputStream = fileObject.getContent().getOutputStream();
//            outputStream.write(bytes);
//            outputStream.close();
//            files.add(fileObject);   
//        }catch(Exception ex) {
//        	throw new RuntimeException(ex);
//        }		
//	}
//
//    public OutputStream createJarFile() {
//        try {
//			FileObject fileObject = fsManager.resolveFile("ram://classes.jar");
//			jarObject = fileObject;
//			
//    		return fileObject.getContent().getOutputStream();
//		} catch (FileSystemException e) {
//			e.printStackTrace();
//        	throw new RuntimeException(e);
//		}
//    }

}
package org.nanotek;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public interface ClassFileSerializer {

public static void saveEntityFile(Class<?> c, MetaClassVFSURLClassLoader bytearrayclassloader2) {
		
		String directoryString = "/home/jose/Documents/";
		
		String fileName =  c.getName().replaceAll("[.]","/").concat(".class");
		
		InputStream is = bytearrayclassloader2.getResourceAsStream(fileName);

		try 
		{

			byte[] classBytes = is.readAllBytes();
			var className = c.getName();
			var simpleName = c.getSimpleName();
			Path dirPath = Paths.get(directoryString, new String[] {});
			Files.createDirectories(dirPath);
			var classLocation  = directoryString.concat("/").concat(simpleName).concat(".class");
			Path classPath = Paths.get(classLocation, new String[] {});
			if(!Files.exists(classPath, LinkOption.NOFOLLOW_LINKS))
    			Files.createFile(classPath, new FileAttribute[0]);
			Files.write(classPath, classBytes, StandardOpenOption.WRITE);

			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
}

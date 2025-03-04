package org.nanotek.config.ramfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.vfs2.FileObject;

public class JarCreator {
    public static JarOutputStream createJarOutputStream(OutputStream stream) throws IOException {
        // Specify the output JAR file name
        try {
        	return new JarOutputStream(stream);
        }catch (Exception ex) {
        	throw new RuntimeException(ex);
        }
    }

    public static void addToJar(FileObject file, InputStream is, JarOutputStream jarOut) throws IOException {
        // Add a new entry to the JAR
        JarEntry entry = new JarEntry(file.getName().getPath());
        jarOut.putNextEntry(entry);

        // Write file contents to the JAR
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                jarOut.write(buffer, 0, bytesRead);
            }
        }catch (Exception ex) {
        	throw new RuntimeException(ex);
        }
        jarOut.closeEntry();
    }
}

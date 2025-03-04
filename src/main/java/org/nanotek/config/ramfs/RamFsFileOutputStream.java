package org.nanotek.config.ramfs;


import org.apache.commons.vfs2.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class RamFsFileOutputStream {
    public static void main(String[] args) throws Exception {
        // Initialize the FileSystemManager
        FileSystemManager fsManager = VFS.getManager();

        // Create a RAM filesystem
        String ramFsRoot = "ram:///";
        FileObject ramFs = fsManager.resolveFile(ramFsRoot);
        
        // Create a file in the RAM filesystem
        FileObject ramFile = ramFs.resolveFile("example.txt");
        ramFile.createFile();

        // Use getContent().getOutputStream() to write directly
        try (OutputStream outputStream = ramFile.getContent().getOutputStream()) {
            outputStream.write("Hello, RAM filesystem!".getBytes());
        }

        // (Optional) Obtain a local file representation if needed (if supported)

        System.out.println("File written in RAM filesystem.");
    }
}

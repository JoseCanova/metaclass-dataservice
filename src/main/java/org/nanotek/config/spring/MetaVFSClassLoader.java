package org.nanotek.config.spring;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.VFSClassLoader;

public class MetaVFSClassLoader extends VFSClassLoader {

	public MetaVFSClassLoader(FileObject file, FileSystemManager manager) throws FileSystemException {
		super(file, manager);
	}

	public MetaVFSClassLoader(FileObject[] files, FileSystemManager manager) throws FileSystemException {
		super(files, manager);
	}

	public MetaVFSClassLoader(FileObject file, FileSystemManager manager, ClassLoader parent)
			throws FileSystemException {
		super(file, manager, parent);
		// TODO Auto-generated constructor stub
	}

	public MetaVFSClassLoader(FileObject[] files, FileSystemManager manager, ClassLoader parent)
			throws FileSystemException {
		super(files, manager, parent);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
	protected Class<?> findClazz(String name) throws ClassNotFoundException {
		return findClass(name);
	}
	
	
}

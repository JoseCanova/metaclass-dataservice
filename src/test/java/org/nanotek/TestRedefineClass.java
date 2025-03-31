package org.nanotek;

import java.nio.file.FileSystem;

import org.nanotek.config.MetaClassVFSURLClassLoader;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Opcodes;

public class TestRedefineClass {
	
	public static final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
	
	public static final MetaClassVFSURLClassLoader byteArrayClassLoader  = new MetaClassVFSURLClassLoader 
																			(TestRedefineClass.class.getClassLoader() , 
																					false ,fileSystem);

	public static void main(String[] args) {
		
		String packageName="org/nanotek/config/spring/data/".replaceAll("[/]", ".");
		
		
		// Using Byte Buddy to define the Person class
		Class<?> PersonSuperClass = new ByteBuddy()
			.subclass(Object.class)
			.name(packageName.concat("PersonSuperClass"))
		    .make()
		    .load(byteArrayClassLoader).getLoaded();

		// Using Byte Buddy to define the Dog class
		Class<?> dogClass = new ByteBuddy()
		.subclass(Object.class)
			.name(packageName.concat("Dog"))
		    .defineField("owner", PersonSuperClass, Opcodes.ACC_PUBLIC)
		    .make()
		    .load(byteArrayClassLoader).getLoaded();
		
		Class<?> newPersonClass = new ByteBuddy()
		.subclass(PersonSuperClass)
		.name(packageName.concat("Person"))
		.defineField("pet", dogClass, Opcodes.ACC_PUBLIC)
		.make()
		.load(byteArrayClassLoader).getLoaded();
		
	}

}

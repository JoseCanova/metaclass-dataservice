package org.nanotek;

import java.nio.file.FileSystem;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.Opcodes;

public class TestRedefineClass {
	
	public static final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
	
	public static final MetaClassVFSURLClassLoader byteArrayClassLoader  = new MetaClassVFSURLClassLoader 
																			(TestRedefineClass.class.getClassLoader() , 
																					false ,fileSystem);

	public static void main(String[] args) {
		
		String packageName="org/nanotek/config/spring/data/".replaceAll("[/]", ".");
		
		
		Builder<Object> personBuilder = new ByteBuddy()
			.subclass(Object.class)
			.name(packageName.concat("Person"));
		
		TypeDescription td = personBuilder.toTypeDescription();
		
		Class<?> dogClass = new ByteBuddy()
		.subclass(Object.class)
			.name(packageName.concat("Dog"))
		    .defineField("owner", td, Opcodes.ACC_PUBLIC)
		    .withHashCodeEquals()
			.withToString()
		    .make()
		    .load(byteArrayClassLoader).getLoaded();
		
		Class<?> personClass = personBuilder
		.defineField("pet", dogClass, Opcodes.ACC_PUBLIC)
		.make()
		.load(byteArrayClassLoader).getLoaded();
		
		try {
			Class<?> theDogClass = Class.forName(dogClass.getName(), true, byteArrayClassLoader);
			Class<?> thePersonClass = Class.forName(personClass.getName(), true, byteArrayClassLoader);
			var theDog = theDogClass.newInstance();
			var thePerson = thePersonClass.newInstance();
			
			theDogClass.getField("owner").set(theDog, thePerson);
			thePersonClass.getField("pet").set(thePerson, theDog);
			System.err.println(theDog.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}

}

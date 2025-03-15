package org.nanotek;

import java.util.function.Supplier;

import org.joor.Reflect;

public class TestCompiler {

	public TestCompiler() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Supplier<String> supplier = Reflect.compile(
			    "com.example.HelloWorld",
			    "package com.example;\n" +
			    "class HelloWorld implements java.util.function.Supplier<String> {\n" +
			    "    public String get() {\n" +
			    "        return \"Hello World!\";\n" +
			    "    }\n" +
			    "}\n").create().get();

			// Prints "Hello World!"
			System.out.println(supplier.get());

	}

}

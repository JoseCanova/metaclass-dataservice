package org.nanotek.test;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.concurrent.Callable;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.InjectionStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class BootstrapAgent {

    public static void main(String[] args) throws Exception {
        premain(null, ByteBuddyAgent.install());
        var urlConnection = new Clazze();
        urlConnection.printValue("hello");
    }

    public static void premain(String arg, Instrumentation inst) throws Exception {
        File temp = Files.createTempDirectory("tmp").toFile();
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst).inject(Collections.singletonMap(
                new TypeDescription.ForLoadedType(MyInterceptor.class),
                ClassFileLocator.ForClassLoader.read(MyInterceptor.class)));
        new AgentBuilder.Default()
        		 .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
        		.with(new InjectionStrategy.UsingInstrumentation(inst,temp))
                .type(ElementMatchers.nameEndsWith("Clazze"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
					public Builder<?> transform(Builder<?> arg0, TypeDescription arg1, ClassLoader arg2,
							JavaModule arg3, ProtectionDomain arg4) {
						return arg0.method(ElementMatchers.named("printValue")).intercept(MethodDelegation.to(MyInterceptor.class));
					}
                }).installOn(inst);
    }

    public static class Clazze  {
    	public void printValue(String value) {
    		System.err.println(value);
    	}
    }
    
    public static class MyInterceptor {

        public static String intercept(@SuperCall Callable<String> zuper) throws Exception {
            System.out.println("Intercepted!");
            return zuper.call();
        }
    }
}
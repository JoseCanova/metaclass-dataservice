# metaclass-dataservice
#### dataservice experiment for  "entity model" generation of metaclass-bytebuddy.

### 04/03/2025
#### for now the experiment is failling to configure using @EnableAutoConfiguration the JPA Repositories, 


### 11/03/2025 - Problem solved with the appropriate FileSystemProvider
#### Serialized the class using jimfs (google/jimfs) and solved the problem for the ClassLoader used to inject the DynamicTypes (the proper name) , this leads to a small ammount of memory usage but with minimal impact since the classes are interfaces or extensions of a single implementation considering the "shall exist" a superset with common methods for a domain model and "specific issues" can be handled also with "interfaces and default methods".

### 15/03/2025 - Spring Data Rest and Repository Scanning on JimFS FileSystemProvider
#### A tweak was used to fix behavior on Repository...Delegate class responsible to scan repositories interfaces when spring-data-rest is added to a Spring Boot Configuration. The package  tree shall differ the package project root structure of the project which leads to the last "if" option on Repository..Delegate class.
#### Changed the package declaration to a package jims..org... and the repository interfaces were successfully scanned. Now the effort will center around starting a configuration before the SpringApplication(SpringBoot) AnnotationWeb...Context.   
#### The purpose is to use a simple spring boot configuration (not that simple) to execute the "database scan process - class generation process" and , with this prepare the ClassLoader for the WebApplicationContext (until now the test ran without any problem with a CustomClassLoader).

### Use  of Mapped Super Clases for class hierachy with tables with one-many one-one relation ships.
#### Next step is thiink how to solve efficiently the problem regarding FK (one-one one-many relationships), the mapped superclass seems to be the appropriate solution, first build classes with all atributes that are not (fk) as mappedsuperclaaes then create the child classes with the FK`s relation ships. There will be the need to filter the atributes that are part of the relation ships.    

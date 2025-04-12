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

### Use  of Mapped Super Clases for class hierachy with tables with one-many one-one relations.
#### Next step is think how to solve efficiently the problem regarding FK (one-one one-many relationships), the mapped superclass seems to be the appropriate solution, first build classes with all atributes that are not (fk) as mappedsuperclaaes then create the child classes with the FK`s relation ships. There will be the need to filter the atributes that are part of the relation ships.  

#### Next Milestone for upcoming week (since "dengue fever" is a giant headache and stay focused with such headache is not necessary) is to prepare the configuration of the classes based on metamodel provided by the microservice on project "schema-metaclass", it`s the best solution a resttemplate (without all complication of non blocking reactive programming), keep it simple since the configuration of the classes is already complicated, then after start with working on one-to-many,one-to-one relations, with superclasses is quite simple with a "table per class" inheritance strategy.  

#### 31-03-2025 - Mitigating ORM Associations.

#### First of all, mitigate the problem to understand what solution wil be feasible, a possible solution exists, a question was posted inquiring "how to generate" bidirectional relations with DynamicTypes ~~(not already loaded) which seems to be not possible and a ClassLoader chain will be hard to manage (since the redefinition of classes would require a ClassLoader chain)~~,	~~So the candidate model is "one of the sides" probably the "Parent Side" be a Mapped SuperClass, but such solution need to be tested on the imagined scenarios (bidirectional and non-bidirectional relations).~~

#### 01-04-2025 - Solution Partially Mitigated

##### Indeed, for sure if the class is loaded it`s will not be possible to alter adding new properties without redefine the class (which means another classloader) but it`s possible as answered by bytebuddy team to "postpone" the class creation, this opens a new strategy to think of how to construct the class model. Will first manually create some simple models to fix in mind the solution for the problem and also practice the solutions for bidrectional and unidirectional relations in the ORM model.
###### Bidirectional issues, one point to notice is that "toString,hashCodeEquals" will need to be reevaluated since it will lead to stackoverflow if implemented ad-hoc(another issue to analyze during manual tests).

###### Created an example with a Person-Pet model (without metaclass model) to evaluate the solution of bidirectional relationship classes using the TypeDescription from a DynamicType.Builder provided by byte-buddy team it worked as suggested, still have to investigate how to fill AnnotationDescriptions with values different from native values (like CascadeType from OneToMany annotation). 

##### Time for reorganize the project code.
##### The project need a redesign on of the "generation funcionalities" because the structure got broke in the aim to produce a result to understand the problems that need to be analyzed. First of all, it will be need to create a manager for model build, such management will be moved to "metaclass-bytebuddy" actually the management of the generation of the model is in this project which brokes the structure of the "metaclass-bytebuddy".
#### This project will remain as a "platform-testbed" since the knowledge of "maven-plugin" development is still in early-phases, and index scanning and management need an effort to achieve a basic goal that is generate many-many relations (this is cleary a big effort since that metaclasses graph need to be generated).

# metaclass-dataservice
dataservice experiment for  "entity model" generation of metaclass-bytebuddy.

04/03/2025
for now the experiment is failling to configure using @EnableAutoConfiguration the JPA Repositories, 

#The EntityManager and JpaRepositories are being manually configured or now,

#The problem as far I understood seems to be that the DynamicType aren`t visible by the Spring Class that loads 
the Reources (ResourceLoader, getResources method). This seems to be not being solved since is not considered an issue but a 
limitation of the instrumentation library. An alternative could be try to intercept (using the instrumentation library) the method on the Spring-Data-Commons that will load the resource.

#Will continue to configure the beans manually as being done in previous version since the main objective is to expose the database model for 
analysis (main objective).

#There are other alternatives like create the classes with a maven plugin and load it in a standar application ( the idea still need to be mitigated),
this alternative opens a better approach for a generic configuration but will be developed in another branch in future, with an advantage that will be gain 
a basic competence on maven-plugin (mojo) development but, still need to be mitigated. 

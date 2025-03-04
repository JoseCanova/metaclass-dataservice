# metaclass-dataservice
dataservice experiment for  "entity model" generation of metaclass-bytebuddy.

04/03/2025
for now the experiment is failling to configure using @EnableAutoConfiguration the JPA Repositories, 
#The EntityManager and JpaRepositories aare manually configured, 
#The problem as far I understood is due that the DynamicType aren`t visible by the Spring Class that loads 
the Reources (ResourceLoader, getResources method), since this class uses Java Module System to locate the class packages 
in the class loader.

#Will continue to configure the beans manually as being done in previous version since the main objective is to expose the database model for 
analysis (main objective).

#There are other alternatives like create the classes with a maven plugin and load it in a standar application ( the idea still need to be mitigated),
this alternative opens a better approach for a generic configuration but will be developed in another branch in future, with an advantage that will be gain 
a basic competence on maven-plugin (mojo) development but, still need to be mitigated. 

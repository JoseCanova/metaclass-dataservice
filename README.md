# metaclass-dataservice
dataservice experiment for  "entity model" generation of metaclass-bytebuddy.

### 04/03/2025
for now the experiment is failling to configure using @EnableAutoConfiguration the JPA Repositories, 


### 11/03/2025 - Problem solved with the appropriate FileSystemProvider
### Serialized the class using jimfs (google/jimfs) and solved the problem for the ClassLoader used to inject the DynamicTypes (the proper name) , this leads to a small ammount of memory usage but with minimal impact since the classes are interfaces or extensions of a single implementation considering the "shall exist" a superset with common methods for a domain model and "specific issues" can be handled also with "interfaces and default methods".


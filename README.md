
![Java](https://img.shields.io/badge/Java-1.8+-blue?logo=java)
![Maven](https://img.shields.io/badge/Build-Maven-orange?logo=apachemaven)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-brightgreen?logo=springboot)
![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey?logo=open-source-initiative)


## Quick start RPC in spring web ##

 - If proxy the interfaces from another repo, make sure turn on -parameters on that repo to keep parameter's name of methods. so the parameters of post/get can against methods on interfaces. 

```xml
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.13.0</version>
				<configuration>
					<compilerArgs>
						<arg>-parameters</arg>
					</compilerArgs>
				</configuration>
			</plugin>
		</plugins>
```

 - The class name and method will be translated to path string:

> AccountServiceImpl.login -> account/login.
> method name in the service should be different.

 - The parameters right on the method in types of primitives will be in the URL as query string.
 
> keeping the parameter names by [-parameters].

 - Configure serializer and deserializer if needed or leave it to default.
 
> See [RestClient.builder().messageConverters(HttpMessageConverter)](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/main/java/io/bitryon/spring/rmi/http/subscriber/AbstractInvokerClient.java#L103)

 - Examples: 
 
> Server side: [ExampleServiceProvider](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/test/java/io/bitryon/spring/rmi/http/prodiver/ExampleServiceProvider.java)

> Client side: [ExampleServiceSubscriber](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/test/java/io/bitryon/spring/rmi/http/subscriber/ExampleServiceSubscriber.java)
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

 - The parameters right on the method in types of primitives will be on the URL as query string.
 
> keeping the parameter names by [-parameters].

 - Configure serializer and deserializer if needed or leave if to default.
 
> See [RestClient.builder().messageConverters(HttpMessageConverter)](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/main/java/io/bitryon/spring/rmi/http/subscriber/AbstractInvokerClient.java#L103)

 - Examples: 
 
> Server side: [ExampleServiceProvider](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/test/java/io/bitryon/spring/rmi/http/prodiver/ExampleServiceProvider.java)

> Client side: [ExampleServiceSubscriber](https://github.com/FrankNPC/bitryon-spring-rmi-http/blob/main/src/test/java/io/bitryon/spring/rmi/http/subscriber/ExampleServiceSubscriber.java)
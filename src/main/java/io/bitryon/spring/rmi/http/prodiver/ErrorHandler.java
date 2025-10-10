package io.bitryon.spring.rmi.http.prodiver;

public interface ErrorHandler {
	default String handle(String errorMessage) {return null;}
	default String handle(Throwable thrownException) {return null;}
}

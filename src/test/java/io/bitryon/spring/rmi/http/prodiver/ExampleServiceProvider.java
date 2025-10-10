package io.bitryon.spring.rmi.http.prodiver;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import io.bitryon.spring.rmi.http.subscriber.ExampleServiceSubscriber;

@Configuration
@Import({
	AutoConfigurationServiceProvider.class,//import it so it scans all of beans noted with @Provider, and process the incoming traffic
})
public class ExampleServiceProvider {

	// Just an example
	@Provider("/api/") // /api/account/get_by_id
	@Service
	class AccountServiceImpl implements ExampleServiceSubscriber.AccountService {
		@Override
		public Object getById(long userId) { return null; } // it will be: HTTP GET account/get_by_id?userId=xxxx
		@Override
		public boolean login(String username, String password) { return true; } // it will be: HTTP POST account/login {username:xxx, password: xxx} 
	}
	
}

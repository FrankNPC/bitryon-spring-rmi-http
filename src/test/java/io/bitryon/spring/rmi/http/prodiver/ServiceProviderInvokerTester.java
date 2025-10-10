package io.bitryon.spring.rmi.http.prodiver;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceProviderInvokerTester {

	@Test
	public void test_parseValue() throws Exception {
		Object value1 = ServiceProviderInvoker.parseValue(String.class, null);
		Assertions.assertEquals(null, value1);
		
		Object value2 = ServiceProviderInvoker.parseValue(Void.class, "fewfewf");
		Assertions.assertEquals(null, value2);
		
		Object value3 = ServiceProviderInvoker.parseValue(String.class, "8943iugher893ghogih3");
		Assertions.assertEquals("8943iugher893ghogih3", value3);

		Object value4 = ServiceProviderInvoker.parseValue(String.class, "xxx/?a=b&c=d");
		Assertions.assertEquals("xxx/?a=b&c=d", value4);
		
		String url = URLEncoder.encode("xxx/?a=b&c=d", ServiceProviderInvoker.defaultCharSet);
		Object value5 = ServiceProviderInvoker.parseValue(String.class, url);
		Assertions.assertEquals("xxx/?a=b&c=d", value5);

		Object value6 = ServiceProviderInvoker.parseValue(Integer.class, 1231231);
		Assertions.assertEquals(1231231, value6);

		Object value7 = ServiceProviderInvoker.parseValue(Character.class, 1);
		Assertions.assertEquals('1', value7);

		Object value8 = ServiceProviderInvoker.parseValue(Long.class, 1231231);
		Assertions.assertEquals(1231231L, value8);
		
		Object value9 = ServiceProviderInvoker.parseValue(Map.class, new HashMap<>());
		Assertions.assertEquals(new HashMap<>(), value9);
	}

}

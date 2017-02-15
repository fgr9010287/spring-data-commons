package org.springframework.data.classloadersupport;

import java.lang.reflect.Method;
import org.mockito.MockSettings;
import org.mockito.ReturnValues;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by jschauder on 15/02/2017.
 */
public class ProxyAnswer implements Answer {

	private final Object delegate;

	public ProxyAnswer(Object delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object answer(InvocationOnMock invocation) throws Throwable {
		Method method = delegate.getClass().getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes());
		method.setAccessible(true);
		return method.invoke(delegate, invocation.getArguments());
	}
}

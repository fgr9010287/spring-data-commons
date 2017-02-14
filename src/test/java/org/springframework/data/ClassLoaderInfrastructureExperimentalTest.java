package org.springframework.data;


import static java.util.Arrays.*;
import static junit.framework.TestCase.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Just a spike to see what fancy stuff we can do  with classloaders
 * Created by jschauder on 14/02/2017.
 */
public class ClassLoaderInfrastructureExperimentalTest {

	@Test
	public void testWithOutJacksonBoolean() {

		Boolean isJacksonHere = new ClassLoaderBuilder<ExampleUnderTest>(ExampleUnderTest.class)
				.hiding("com.fasterxml.jackson")
				.run(this, new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return new ExampleUnderTest().isJacksonHere();
					}
				});

		assertFalse(isJacksonHere);
	}

	@Test
	public void testWithOutJacksonBooleanSecondSyntax() {
		Boolean isJacksonHere = new ClassLoaderBuilder<ExampleUnderTest>(ExampleUnderTest.class)
				.hiding("com.fasterxml.jackson")
				.create().isJacksonHere();

		assertFalse(isJacksonHere);
	}

	@Test
	public void testWithOutJacksonList() {

	}
}

class MyUtil {

	public static ClassLoaderBuilder load(Class aClassToLoad) {
		return new ClassLoaderBuilder(aClassToLoad);
	}
}

class ExampleUnderTest {

	public boolean isJacksonHere() {
		return ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", getClass().getClassLoader());
	}

	public List<Object> returnSomethingIfJacksonIsPresent() {
		if (isJacksonHere())
			return asList((Object) new ObjectMapper());
		else
			return Collections.emptyList();
	}
}
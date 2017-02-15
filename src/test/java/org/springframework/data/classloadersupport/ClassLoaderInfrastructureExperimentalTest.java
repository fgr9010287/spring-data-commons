/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.classloadersupport;


import static java.util.Arrays.*;
import static junit.framework.TestCase.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * test for trying out {@link ClassLoader} infrastructure for testing.
 *
 * @author Jens Schauder
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
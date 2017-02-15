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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.mockito.Mockito;

/**
 * is a builder for constructing instances loaded by a {@link FilteringClassLoader}.
 *
 * @author Jens Schauder
 */
public class ClassLoaderBuilder<S> {

	private final Class<S> classToLoad;
	private String hidden;

	public static <S> ClassLoaderBuilder<S> load(Class<S> classToLoad) {
		return new ClassLoaderBuilder<S>(classToLoad);
	}

	public ClassLoaderBuilder(Class<S> aClassToLoad) {

		classToLoad = aClassToLoad;
	}

	public ClassLoaderBuilder<S> hiding(String hidden) {
		this.hidden = hidden;
		return this;
	}

	public <T> T run(Object owner, Callable<T> callable) {
		try {
			FilteringClassLoader classLoader = new FilteringClassLoader(hidden);
			classLoader.excludeClass(owner.getClass().getName());

			Class<?> callableClass = classLoader.loadClass(callable.getClass().getName());
			Constructor<?> constructor = callableClass.getDeclaredConstructor(owner.getClass());
			constructor.setAccessible(true);
			Object reloadedCallable = constructor.newInstance(owner);

			Method call = callableClass.getMethod("call");
			call.setAccessible(true);
			return (T) call.invoke(reloadedCallable);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public S create() {
		try {
			FilteringClassLoader classLoader = new FilteringClassLoader(hidden);
			Class<?> loadedClass = classLoader.loadClass(classToLoad.getName());
			Constructor<?> constructor = loadedClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object instance = constructor.newInstance();
			return Mockito.mock(classToLoad, new ProxyAnswer(instance));
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

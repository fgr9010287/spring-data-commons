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
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * supports creation of tests that need to load classes with a {@link FilteringClassLoader}.
 *
 * @author Jens Schauder
 */
public class ClassLoaderRule implements MethodRule {

	public FilteringClassLoader classLoader;
	private ClassLoader originalContextClassLoader;
	private Object testInstance;

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		ClassLoaderConfiguration methodAnnotation = method.getAnnotation(ClassLoaderConfiguration.class);
		ClassLoaderConfiguration classAnnotation = method.getDeclaringClass().getAnnotation(ClassLoaderConfiguration.class);
		CombinedClassLoaderConfiguration combinedConfiguration = new CombinedClassLoaderConfiguration(methodAnnotation, classAnnotation);
		testInstance = target;
		classLoader = createClassLoader(combinedConfiguration, testInstance.getClass());
		originalContextClassLoader = Thread.currentThread().getContextClassLoader();

		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {

					base.evaluate();
				} finally {
					classLoader = null;
					Thread.currentThread().setContextClassLoader(originalContextClassLoader);
				}
			}
		};
	}


	public <T> T create(Class<T> classToCreate) {

		Thread.currentThread().setContextClassLoader(classLoader);
		T instance = createClass(classToCreate);
		setResourceLoader(instance);

		return instance;
	}

	private <T> void setResourceLoader(T instance) {
		if (instance instanceof ResourceLoaderAware) {
			ResourceLoader resourceLoader = mock(ResourceLoader.class);
			when(resourceLoader.getClassLoader()).thenReturn(classLoader);
			((ResourceLoaderAware) instance).setResourceLoader(resourceLoader);
		}
	}

	private <T> T createClass(Class<T> classToCreate) {

		Object instance = createInstance(classToCreate);

		return mock(classToCreate, new ProxyAnswer(instance));
	}

	private <T> Object createInstance(Class<T> classToCreate) {
		try {
			Class<?> loadedClass = classLoader.loadClass(classToCreate.getName());

			Object instance;
			try {
				Constructor<?> constructor = loadedClass.getDeclaredConstructor();
				constructor.setAccessible(true);
				instance = constructor.newInstance();
			} catch (NoSuchMethodException nsme) {
				Constructor<?>[] declaredConstructors = loadedClass.getDeclaredConstructors();
				Constructor<?> constructor = loadedClass.getDeclaredConstructor(testInstance.getClass());
				constructor.setAccessible(true);
				instance = constructor.newInstance(testInstance);
			}
			return instance;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T call(Callable<T> callable) {
		try {
			return ((Callable<T>) createInstance(callable.getClass())).call();
		} catch (RuntimeException re){
			throw re;
		} catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}


	private static FilteringClassLoader createClassLoader(CombinedClassLoaderConfiguration configuration, Class<?> testClass) {

		FilteringClassLoader classLoader = new FilteringClassLoader(mergeHidden(configuration));
		classLoader.excludeClass(testClass.getName());
		for (Class aClass : configuration.dontShadow) {
			classLoader.excludeClass(aClass.getName());
		}
		for (Package dontShadowPackage : configuration.dontShadowPackages) {
			classLoader.excludePackage(dontShadowPackage.getName());
		}

		return classLoader;
	}

	private static List<String> mergeHidden(CombinedClassLoaderConfiguration configuration) {
		List<String> hidden = new ArrayList<String>();

		for (Class aClass : configuration.hide) {
			hidden.add(aClass.getName());
		}

		for (Package aPackage : configuration.hidePackages) {
			hidden.add(aPackage.getName());
		}
		return hidden;
	}

	private static class CombinedClassLoaderConfiguration {

		final List<Class> dontShadow = new ArrayList<Class>();
		final List<Package> dontShadowPackages = new ArrayList<Package>();
		final List<Class> hide = new ArrayList<Class>();
		final List<Package> hidePackages = new ArrayList<Package>();

		CombinedClassLoaderConfiguration(ClassLoaderConfiguration methodAnnotation, ClassLoaderConfiguration classAnnotation) {

			mergeAnnotation(methodAnnotation);
			mergeAnnotation(classAnnotation);
		}

		private void mergeAnnotation(ClassLoaderConfiguration methodAnnotation) {
			if (methodAnnotation != null) {
				dontShadow.addAll(asList(methodAnnotation.dontShadow()));
				dontShadowPackages.addAll(convertToPackageNames(asList(methodAnnotation.dontShadowPackages())));
				hide.addAll(asList(methodAnnotation.hide()));
				hidePackages.addAll(convertToPackageNames(asList(methodAnnotation.hidePackages())));
			}
		}

		private static Collection<Package> convertToPackageNames(List<Class> classes) {
			List<Package> result = new ArrayList<Package>();
			for (Class aClass : classes) {
				result.add(aClass.getPackage());
			}
			return result;
		}
	}
}

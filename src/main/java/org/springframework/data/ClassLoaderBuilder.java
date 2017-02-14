package org.springframework.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by jschauder on 14/02/2017.
 */
public class ClassLoaderBuilder<S> {

	private final Class<S> classToLoad;
	private String hidden;

	public static <S> ClassLoaderBuilder<S> load(Class<S> classToLoad){
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
			Constructor<?>[] constructors = callableClass.getConstructors();
			System.out.println(Arrays.asList(constructors));
			Constructor<?> constructor = callableClass.getDeclaredConstructor(owner.getClass());
			constructor.setAccessible(true);
			Object reloadedCallable = constructor.newInstance(owner);

			Method call = callableClass.getMethod("call");
			call.setAccessible(true);
			return (T) call.invoke(reloadedCallable);
		} catch (RuntimeException e) {
			throw e;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public S create(){
		try {


			return classToLoad.newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

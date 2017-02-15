package org.springframework.data.classloadersupport;

import java.net.URLClassLoader;
import org.springframework.instrument.classloading.ShadowingClassLoader;

/**
 * Created by jschauder on 14/02/2017.
 */
public class FilteringClassLoader extends ShadowingClassLoader {

	private final String excludedClassNamePrefix;

	public FilteringClassLoader(String excludedClassNamePrefix) {
		super(URLClassLoader.getSystemClassLoader());
		this.excludedClassNamePrefix = excludedClassNamePrefix;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {

		if (name.startsWith(excludedClassNamePrefix)) {
			throw new ClassNotFoundException();
		}

		return super.loadClass(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		if (name.startsWith(excludedClassNamePrefix)) {
			throw new ClassNotFoundException();
		}

		return super.findClass(name);
	}
}

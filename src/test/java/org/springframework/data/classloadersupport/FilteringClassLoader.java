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

import java.net.URLClassLoader;
import java.util.Collection;
import org.springframework.instrument.classloading.ShadowingClassLoader;

/**
 * by default shadows most classes loaded, with the following exceptions:
 *
 * <ul>
 *     <li>those specified by {@link #excludeClass(String)} or
 * {@link #excludePackage(String)} or {@link ShadowingClassLoader#DEFAULT_EXCLUDED_PACKAGES}.
 * They get loaded by {@link ShadowingClassLoader#}</li>
 *
 * <li>those in {@link #hidden}, which will not get loaded at all</li>
 *
 * </ul>
 *
 * This {@link ClassLoader} is intended for testing code that depends on the presence/absence of certain classes.
 *
 * @author Jens Schauder
 */
public class FilteringClassLoader extends ShadowingClassLoader {

	private final Collection<String> hidden;

	public FilteringClassLoader(Collection<String> hidden) {
		super(URLClassLoader.getSystemClassLoader());
		this.hidden = hidden;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {

		checkIfHidden(name);
		return super.loadClass(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		checkIfHidden(name);
		return super.findClass(name);
	}

	private void checkIfHidden(String name) throws ClassNotFoundException {
		for (String prefix : hidden) {
			if (name.startsWith(prefix)) {
				throw new ClassNotFoundException();
			}
		}
	}
}

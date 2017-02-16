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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * configures the {@link ClassLoaderRule}.
 *
 * @author Jens Schauder
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassLoaderConfiguration {

	/**
	 * classes that will be loaded by the normal class loader, not by the {@link FilteringClassLoader}.
	 *
	 * @return list of classes not to shadow.
	 */
	Class[] dontShadow() default {};

	/**
	 * classes from packages that will be loaded by the normal class loader, not by the {@link FilteringClassLoader}.
	 *
	 * @return list of classes of which the package will not be shadowed.
	 */
	Class[] dontShadowPackages() default {};

	/**
	 * classes that will be hidden by the {@link FilteringClassLoader}.
	 *
	 * @return list of classes to hide.
	 */
	Class[] hide() default {};

	/**
	 * classes from packages that will be hidden by the {@link FilteringClassLoader}.
	 *
	 * @return list of classes of which the package will be hidden.
	 */
	Class[] hidePackages() default {};
}

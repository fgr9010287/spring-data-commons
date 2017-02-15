package org.springframework.data.web.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.classloadersupport.ClassLoaderBuilder;
import org.springframework.hateoas.Link;

/**
 * @author Jens Schauder
 */
public class SpringDataWebConfigurationImportSelectorUnitTest {

	@Test
	public void findsHateoasAwareIfPresent() {
		ClassLoaderBuilder<EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector> classLoaderBuilder = new ClassLoaderBuilder<EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector>(
				EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector.class)
				.exclude(AnnotationMetadata.class)
				.exclude(ResourceLoader.class);
		EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector selector =
				classLoaderBuilder
						.create();
		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		when(resourceLoader.getClassLoader()).thenReturn(classLoaderBuilder.getClassLoader());
		selector.setResourceLoader(resourceLoader);

		String[] imports = selector.selectImports(mock(AnnotationMetadata.class));

		assertThat(imports, hasItemInArray(HateoasAwareSpringDataWebConfiguration.class.getName()));
		assertThat(imports, not(hasItemInArray(SpringDataWebConfiguration.class.getName())));
	}

	@Test
	public void skipsHateoasAwareIfNotPresent() {
		ClassLoaderBuilder<EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector> classLoaderBuilder = new ClassLoaderBuilder<EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector>(
				EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector.class)
				.hiding(Link.class.getPackage().getName())
				.exclude(AnnotationMetadata.class)
				.exclude(ResourceLoader.class);
		Thread.currentThread().setContextClassLoader(classLoaderBuilder.getClassLoader());
		EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector selector =
				classLoaderBuilder
						.create();
		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		when(resourceLoader.getClassLoader()).thenReturn(classLoaderBuilder.getClassLoader());
		selector.setResourceLoader(resourceLoader);

		String[] imports = selector.selectImports(mock(AnnotationMetadata.class));

		assertThat(imports, not(hasItemInArray(HateoasAwareSpringDataWebConfiguration.class.getName())));
		assertThat(imports, hasItemInArray(SpringDataWebConfiguration.class.getName()));
	}
}
package org.springframework.data.web.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.classloadersupport.ClassLoaderConfiguration;
import org.springframework.data.classloadersupport.ClassLoaderRule;
import org.springframework.hateoas.Link;

/**
 * @author Jens Schauder
 */
@ClassLoaderConfiguration(
		dontShadow = {AnnotationMetadata.class, ResourceLoader.class}
)
public class SpringDataWebConfigurationImportSelectorUnitTest {

	@Rule public ClassLoaderRule classLoader = new ClassLoaderRule();

	@Test
	public void findsHateoasAwareIfPresent() {

		EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector selector =
				classLoader.create(EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector.class);

		String[] imports = selector.selectImports(mock(AnnotationMetadata.class));

		assertThat(imports, hasItemInArray(HateoasAwareSpringDataWebConfiguration.class.getName()));
		assertThat(imports, not(hasItemInArray(SpringDataWebConfiguration.class.getName())));
	}

	@Test
	@ClassLoaderConfiguration(
			hidePackages = {Link.class}
	)
	public void skipsHateoasAwareIfNotPresent() {

		EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector selector =
				classLoader.create(EnableSpringDataWebSupport.SpringDataWebConfigurationImportSelector.class);

		String[] imports = selector.selectImports(mock(AnnotationMetadata.class));

		assertThat(imports, not(hasItemInArray(HateoasAwareSpringDataWebConfiguration.class.getName())));
		assertThat(imports, hasItemInArray(SpringDataWebConfiguration.class.getName()));
	}
}
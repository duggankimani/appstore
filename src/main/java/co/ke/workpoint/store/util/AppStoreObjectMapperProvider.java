package co.ke.workpoint.store.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@Provider
public class AppStoreObjectMapperProvider implements
		ContextResolver<ObjectMapper> {

	/**
	 * TODO javadoc.
	 *
	 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
	 */

	final ObjectMapper defaultObjectMapper;

	public AppStoreObjectMapperProvider() {
		defaultObjectMapper = createDefaultMapper();
	}

	@Override
	public ObjectMapper getContext(final Class<?> type) {
			return defaultObjectMapper;
	}

	private static ObjectMapper createDefaultMapper() {
		final ObjectMapper result = new ObjectMapper();
		result.enable(SerializationFeature.INDENT_OUTPUT);
		result.setSerializationInclusion(Include.NON_NULL);
		result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return result;
	}

	private static AnnotationIntrospector createJaxbJacksonAnnotationIntrospector() {

		final AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(
				TypeFactory.defaultInstance());
		final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

		return AnnotationIntrospector.pair(jacksonIntrospector,
				jaxbIntrospector);
	}

}

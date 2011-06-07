/**
 * 
 */
package com.licenta.park.utils;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

/**
 * Custom {JacksonConverter} 
 * @author vladucu
 *
 */
public class JacksonCustomConverter extends JacksonConverter {

	/* (non-Javadoc)
	 * @see org.restlet.ext.jackson.JacksonConverter#create(org.restlet.data.MediaType, java.lang.Object)
	 */
	@Override
	protected <T> JacksonRepresentation<T> create(MediaType mediaType, T source) {
		ObjectMapper mapper = createMapper();
		JacksonRepresentation jr = new JacksonRepresentation(mediaType, source);
		jr.setObjectMapper(mapper);
		return jr;
	}
	
	/* (non-Javadoc)
	 * @see org.restlet.ext.jackson.JacksonConverter#create(org.restlet.representation.Representation, java.lang.Class)
	 */
	@Override
	protected <T> JacksonRepresentation<T> create(Representation source, Class<T> objectClass) {
		ObjectMapper mapper = createMapper();
		JacksonRepresentation<T> jr = new JacksonRepresentation<T>(source, objectClass);
		jr.setObjectMapper(mapper);
		return jr;
	}

	/* (non-Javadoc)
	 * @see org.restlet.ext.jackson.JacksonConverter#create(org.restlet.representation.Representation, java.lang.Class)
	 */
	private ObjectMapper createMapper() {
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		ObjectMapper mapper = new ObjectMapper(jsonFactory);
		mapper.enableDefaultTyping();
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);		
		//mapper.getSerializationConfig().addMixInAnnotations(target, mixinSource);
		//mapper.getDeserializationConfig().addMixInAnnotations(target, mixinSource);
		return mapper;
	}

}

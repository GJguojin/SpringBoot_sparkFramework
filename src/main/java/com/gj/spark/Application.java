package com.gj.spark;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gj.spark.structure.RequestMappingHandlerAdapter;
import com.gj.spark.utils.SparkJsonTransformer;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.registerShutdownHook();
		context.start();
	}
	
    @Bean
	@Primary
	public ObjectMapper jacksonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		
		//忽略值为null的属性
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
	    mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(Long.class, new ToStringSerializer());  
		module.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
		module.addDeserializer(Date.class, new DateDeserializer(new DateDeserializer(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss" ));
		mapper.registerModule(module);
		return mapper;
	}
    
    @Bean
    public SparkJsonTransformer getJsonTransformer(){
    	return new SparkJsonTransformer(jacksonObjectMapper());
    }
    
    @Bean
    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter(){
    	RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
    	return requestMappingHandlerAdapter;
    }

}
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
import com.gj.spark.utils.SparkJsonTransformer;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.registerShutdownHook();
		context.start();

/*		port(8080); // <- Uncomment this if you want spark to listen to port 5678 instead of the default 4567

		ClassCollection.scanClassSetByPackage("com.gj.spark.controller");
		Map<String, MethodPro> methodMap = ClassCollection.getMethodMap();
		Map<String, Class<?>> classMap = ClassCollection.getClassMap();
		Set<Class<?>> classSet = ClassCollection.getClassSet();
		Map<String, ArrayList<String>> methodNamesMap = ClassCollection.getMethodNamesMap();
		Map<Class<?>,Object> instanceMap = new HashMap<Class<?>,Object>();

		for (Map.Entry<String, MethodPro> entry : methodMap.entrySet()) {
			// Map.entry<Integer,String> 映射项（键-值对） 有几个方法：用上面的名字entry
			// entry.getKey() ;entry.getValue(); entry.setValue();
			// map.entrySet() 返回此映射中包含的映射关系的 Set视图。
			System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			MethodPro methodPro = entry.getValue();
			String urlStyle = methodPro.getUrlStyle();
			if ("GET".equals(urlStyle)) {
				get(entry.getKey(),new Route(){
					@Override
					public Object handle(Request request, Response response) throws Exception {
						
						Method method = methodPro.getMethod();
						Class<?> class1 = classMap.get(entry.getKey());
						if(!instanceMap.containsKey(class1)){
							instanceMap.put(class1, class1.newInstance());
						}
						ArrayList<String> argList = methodNamesMap.get(entry.getKey());
						Object[] args = new Object[argList.size()];
						if(argList != null){
							for(int i=0;i < argList.size(); i++){
								String temp = argList.get(i);
								String params = request.queryParamOrDefault(temp,null);
								args[i] =params;
							}
						}
					
						
						Object invoke = method.invoke(instanceMap.get(class1), args);
						return null;
					}
				});
			
			} else if ("POST".equals(urlStyle)) {
				post(entry.getKey(),new Route(){
					@Override
					public Object handle(Request request, Response response) throws Exception {
						Method method = methodPro.getMethod();
						Class<?> class1 = classMap.get(entry.getKey());
						if(!instanceMap.containsKey(class1)){
							instanceMap.put(class1, class1.newInstance());
						}
						Object invoke = method.invoke(instanceMap.get(class1), null);
						return null;
					}
				});

			}

		}*/
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

}
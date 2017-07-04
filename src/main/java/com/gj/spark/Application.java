package com.gj.spark;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.fastjson.JSON;
import com.gj.spark.classCollection.ClassCollection;
import com.gj.spark.structure.MethodPro;

import spark.Request;
import spark.Response;
import spark.Route;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.registerShutdownHook();
		context.start();

		port(8080); // <- Uncomment this if you want spark to listen to port 5678 instead of the default 4567

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
						return JSON.toJSONString(invoke);
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
						return JSON.toJSONString(invoke);
					}
				});

			}

		}
		/*
		get("/rest/test1", (request, response) -> {
			System.out.println("调用 test...");
			Thread.sleep(100); //
			return new Message("hello world!!");
		});
		 * get("/hello", (request, response) -> {
		 * return "Hello World!";
		 * });
		 * post("/hello", (request, response) ->
		 * "Hello World: " + request.body()
		 * );
		 * get("/private", (request, response) -> {
		 * response.status(401);
		 * return "Go Away!!!";
		 * });
		 * get("/users/:name", (request, response) -> "Selected user: " + request.params(":name"));
		 * get("/news/:section", (request, response) -> {
		 * response.type("text/xml");
		 * return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
		 * });
		 * get("/protected", (request, response) -> {
		 * halt(403, "I don't think so!!!");
		 * return null;
		 * });
		 * get("/redirect", (request, response) -> {
		 * response.redirect("/news/world");
		 * return null;
		 * });
		 * get("/", (request, response) -> "root");
		 */
	}

}
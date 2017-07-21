package com.gj.spark.config;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.head;
import static spark.Spark.options;
import static spark.Spark.patch;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.trace;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.gj.spark.annotation.RequestMapping;
import com.gj.spark.annotation.RequestMethod;
import com.gj.spark.asm.ReadMethodArgNameClassVisitor;
import com.gj.spark.controller.TestController;
import com.gj.spark.structure.SparkMethod;
import com.gj.spark.utils.SparkJsonTransformer;

import spark.Route;

@Component
public class RouteRegisterService implements ApplicationContextAware {
	
	private final static Set<String> FULLPATH = new HashSet<String>();

	@Autowired
	private SparkJsonTransformer jsonTransformer;

	private ApplicationContext applicationContext;

	private final static Logger logger = LoggerFactory.getLogger(RouteRegisterService.class);
	
	public void init() throws Exception {
		final Map<String, Object> controllerMap = applicationContext.getBeansWithAnnotation(Controller.class);
		for (final Object controllerObject : controllerMap.values()) {
			Class<? extends Object> controllerClass = controllerObject.getClass();
			ClassReader cr = new ClassReader(controllerClass.getName());
			Method[] methods = controllerClass.getDeclaredMethods();
			for (Method method : methods) {
				RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
				if (methodRequestMapping != null) {
					SparkMethod methodHandle = new SparkMethod(method,controllerClass,cr,applicationContext);
					List<String> fullPaths = methodHandle.getFullPaths();
					for(String fullPath:fullPaths){
						Route router = (request, response) -> {
							Object[] args = methodHandle.getMethodArgs(request,response);
							response.type("application/json");
							return method.invoke(controllerObject, args);
						};
						
						RequestMethod[] requestMethods = methodHandle.getRequestMethods();
						for (RequestMethod requestMethod : requestMethods) {
							String temp = requestMethod.name()+"_"+fullPath;
							if(FULLPATH.contains(temp)){
								throw new Exception("path "+temp+" has exists!");
							}else{
								FULLPATH.add(temp);
							}
							logger.debug("===========spark register router: {} {} -> {} ", requestMethod.name(), fullPath, controllerObject.getClass().getName() + "." + method.getName());
							bindRouteMethod(requestMethod, router, fullPath);
						}
					}
				}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private void bindRouteMethod(RequestMethod requestMethod, Route router, String path) {
		switch (requestMethod) {
		case GET:
			get(path, router, jsonTransformer);
			break;
		case POST:
			post(path, router, jsonTransformer);
			break;
		case PUT:
			put(path, router, jsonTransformer);
			break;
		case DELETE:
			delete(path, router, jsonTransformer);
			break;
		case OPTIONS:
			options(path, router, jsonTransformer);
			break;
		case TRACE:
			trace(path, router, jsonTransformer);
			break;
		case HEAD:
			head(path, router, jsonTransformer);
			break;
		case PATCH:
			patch(path, router, jsonTransformer);
			break;
		default:
			break;
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		TestController test = new TestController();
		String className = test.getClass().getName();
		ClassReader cr = new ClassReader(className);
		Method[] declaredMethods = test.getClass().getDeclaredMethods();
		for(Method method :declaredMethods ){
			ReadMethodArgNameClassVisitor classVisitor = new ReadMethodArgNameClassVisitor(method);
			cr.accept(classVisitor, 0);
			List<String> nameArg = classVisitor.argNames;
			nameArg.size();
		}
	}

}

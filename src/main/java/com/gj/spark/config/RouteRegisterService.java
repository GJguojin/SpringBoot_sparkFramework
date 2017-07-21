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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.gj.spark.utils.SparkJsonTransformer;

import spark.Route;
import spark.utils.StringUtils;

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
			// 类上面的路由
			String[] classPaths = new String[]{"/"};
			
			Class<? extends Object> controllerClass = controllerObject.getClass();
			String className = controllerClass.getName();
			RequestMapping classRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
			if(classRequestMapping != null){
				classPaths = classRequestMapping.path();
			}
			for (String classPath : classPaths) {
				if (!StringUtils.isBlank(classPath) && !classPath.startsWith("/")) {
					classPath = "/" + classPath;
				}
				
				ClassReader cr = new ClassReader(className);
				Method[] methods = controllerObject.getClass().getDeclaredMethods();
				for (Method method : methods) {
					ReadMethodArgNameClassVisitor classVisitor = new ReadMethodArgNameClassVisitor(method);
					cr.accept(classVisitor, 0);
					
					//方法参数名称
					List<String> argNames = classVisitor.argNames; 
					
					RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
					if (methodRequestMapping != null) {
						String[] methodPaths = methodRequestMapping.path();
						if(methodPaths.length == 0){
							methodPaths = methodRequestMapping.value();
						}
						if(methodPaths.length == 0){
							methodPaths = new String[]{"/"};
						}
						for (String methodPath : methodPaths) {
							if (!StringUtils.isBlank(methodPath) && !methodPath.startsWith("/")) {
								methodPath = "/" + methodPath;
							}
							// 完整路由
							String fullPath = (classPath.equals("/")?"":classPath) + methodPath;
							RequestMethod[] requestMethods = methodRequestMapping.method();
							// 绑定路由与方法的关联
							if (requestMethods == null || requestMethods.length == 0) {
								requestMethods = RequestMethod.values();
							}
							Route router = (req, res) -> {
								String[] partPaths = fullPath.split("/");
								HttpServletRequest request = req.raw();
								HttpServletResponse response = res.raw();
								
								Object[] args = new Object[argNames.size()];
								Class<?>[] parameterTypes = method.getParameterTypes();
								for(int i=0;i < argNames.size(); i++){
									String temp = argNames.get(i);
									Class<?> typeParameter = parameterTypes[i];
									String params = req.queryParamOrDefault(temp,null);
									if(typeParameter.equals(HttpServletRequest.class)){
										args[i] =req.raw();
									}else if(typeParameter.equals(HttpServletResponse.class)){
										args[i] = res.raw();
									}else if(typeParameter.equals(Integer.class)){
										args[i] =new Integer(params);
									}else{
										args[i] =params;
									}
									
								}
								res.type("application/json");
								return method.invoke(controllerObject, args);
							};
							for (RequestMethod requestMethod : requestMethods) {
								String temp = requestMethod.name()+"_"+fullPath;
								if(FULLPATH.contains(temp)){
									throw new Exception("path "+temp+" has exists!");
								}else{
									FULLPATH.add(temp);
								}
								logger.debug("register router: {} {} -> {} ", requestMethod.name(), fullPath, controllerObject.getClass().getName() + "." + method.getName());
								bindRouteMethod(requestMethod, router, fullPath);
							}
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

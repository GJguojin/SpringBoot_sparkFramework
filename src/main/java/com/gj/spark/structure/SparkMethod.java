package com.gj.spark.structure;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.springframework.context.ApplicationContext;

import com.gj.spark.annotation.RequestMapping;
import com.gj.spark.annotation.RequestMethod;
import com.gj.spark.asm.ReadMethodArgNameClassVisitor;

import spark.Request;
import spark.Response;


public class SparkMethod {
	
	private Class<?> clazz;

	public Method method;// 方法
	
	private RequestMapping requestMapping;

	private String name; // 方法名称

	private List<String> paths;// 方法路径

	private List<String> fullPaths;// 方法全路径

	private List<SparkMethodParameter> methodParameters = new ArrayList<SparkMethodParameter>(); // 方法参数
	
	private ApplicationContext applicationContext;
	
	public SparkMethod(Method method,Class<?> clazz,ApplicationContext applicationContext) throws Exception{
		new SparkMethod(method,clazz,null,applicationContext);
	}

	public SparkMethod(Method method,Class<?> clazz,ClassReader classReader,ApplicationContext applicationContext) throws Exception {
		this.applicationContext = applicationContext;
		this.clazz = clazz;
		this.method = method;
		if(classReader == null){
			classReader = new ClassReader(clazz.getName());
		}
		this.name = method.getName();
		this.requestMapping = method.getAnnotation(RequestMapping.class);
		if(requestMapping == null){
			throw new Exception("can not instance MethodHandle without annotation RequestMapping!");
		}
		
		String[] path = requestMapping.path();
		if(path.length == 0){
			path = requestMapping.value();
		}
		this.paths = dealPath(path);
		RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
		if(annotation != null){
			String[] parentPaths = annotation.path();
			if(parentPaths.length == 0){
				parentPaths = annotation.value();
			}
			if(parentPaths.length == 0){
				parentPaths = new String[]{"/"};
			}
			if(parentPaths.length == 0){
				fullPaths = paths;
			}else{
				this.fullPaths =new ArrayList<String>();
				for (String parentPath : parentPaths) {
					for(int j=0;j<paths.size();j++){
						fullPaths.add((parentPath.startsWith("/")?(parentPath.equals("/")?"":parentPath):(parentPath.equals("")?"":("/"+parentPath)))+paths.get(j));
					}
				}
			}
		}else{
			fullPaths = paths;
		}
		
		ReadMethodArgNameClassVisitor classVisitor = new ReadMethodArgNameClassVisitor(method);
		classReader.accept(classVisitor, 0);
		//方法参数名称
		List<String> argNames = classVisitor.argNames; 
		Parameter[] parameters = method.getParameters();
		if(argNames.size() != 0){
			for(int i=0;i<parameters.length;i++){
				methodParameters.add(new SparkMethodParameter(parameters[i],method,argNames.get(i),i,applicationContext));
			}
		}
	}


	
	private static List<String> dealPath(String[] paths){
		List<String> newPaths = new ArrayList<String>();
		if(paths.length == 0){
			newPaths.add("/");
		}else{
			for (String path : paths) {
				newPaths.add(path.startsWith("/")?path:("/"+path));
			}
		}
		return newPaths;
	}

	public List<String> getFullPaths() {
		return fullPaths;
	}

	/**
	 * 得到实例化后的方法参数数组
	 * @param request
	 * @return
	 */
	public Object[] getMethodArgs(Request request,Response response) {
		Object[] returnArgs = new Object[]{};
		int size = methodParameters.size();
		if(size != 0){
			returnArgs = new Object[size];
			for(int i=0;i<size;i++){
				returnArgs[i] = methodParameters.get(i).getParameterValue(request,response);
			}
		}
		return  returnArgs;
	}

	/**
	 * 得到声明的方法
	 * @return
	 */
	public RequestMethod[] getRequestMethods(){
		RequestMethod[] requestMethods = requestMapping.method();
		if (requestMethods == null || requestMethods.length == 0) {
			requestMethods = RequestMethod.values();
		}
		return requestMethods;
	}
}

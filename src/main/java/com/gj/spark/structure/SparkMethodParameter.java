package com.gj.spark.structure;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.gj.spark.controller.TestController;

import spark.Request;
import spark.Response;

public class SparkMethodParameter  {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	public Method method;
	
	private Parameter parameter;
	
	private String parameterName;  //参数名称
	
	public int num; //方法的第几个参数
	
	private ApplicationContext applicationContext;
	
	public SparkMethodParameter( Parameter parameter,Method method,String parameterName,int num,ApplicationContext applicationContext) {
		this.parameter = parameter;
		this.method = method;
		this.parameterName = parameterName;
		this.num = num;
		this.applicationContext = applicationContext;
	}

	/**
	 * 得到属相的值
	 * @param request
	 * @return
	 */
	public Object getParameterValue(Request request,Response response) {
		Object returnObj = null;
		RequestMappingHandlerAdapter adapter= applicationContext.getBean(RequestMappingHandlerAdapter.class);
		try {
			returnObj = adapter.getMethodArgumentResolverResult(this, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage(), e);
		}
		return returnObj;
	}
	
	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	public static void main(String[] args) {
		TestController test = new TestController();
		String className = test.getClass().getName();
		Method[] declaredMethods = test.getClass().getDeclaredMethods();
		Method method2 = declaredMethods[0];
		SparkMethodParameter haddle = new SparkMethodParameter(method2.getParameters()[1],method2,"s1",0,null);
		haddle.getParameterValue(null,null);
	}


}

package com.gj.spark.structure.resolver;

import com.gj.spark.structure.HandlerMethodArgumentResolver;
import com.gj.spark.structure.SparkMethodParameter;

import spark.Request;
import spark.Response;

public class BasicTypeMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(SparkMethodParameter sparkMethodParameter) {
		Class<?> typeParameter = sparkMethodParameter.getParameter().getType();
		if (int.class.isAssignableFrom(typeParameter)    || Integer.class.isAssignableFrom(typeParameter)  ||
			short.class.isAssignableFrom(typeParameter)  || Short.class.isAssignableFrom(typeParameter)    ||
			long.class.isAssignableFrom(typeParameter)   || Long.class.isAssignableFrom(typeParameter)     ||
			float.class.isAssignableFrom(typeParameter)  || Float.class.isAssignableFrom(typeParameter)    ||
			double.class.isAssignableFrom(typeParameter) || Double.class.isAssignableFrom(typeParameter)   ||
			char.class.isAssignableFrom(typeParameter)   || Character.class.isAssignableFrom(typeParameter)||
			byte.class.isAssignableFrom(typeParameter)   || Byte.class.isAssignableFrom(typeParameter)     ||
			boolean.class.isAssignableFrom(typeParameter)|| Boolean.class.isAssignableFrom(typeParameter)  ||
			String.class.isAssignableFrom(typeParameter)
		) {
			return true;
		}
		return false;
	}

	@Override
	public Object resolveArgument(SparkMethodParameter sparkMethodParameter, Request request, Response response) throws Exception {
		Class<?> typeParameter = sparkMethodParameter.getParameter().getType();
		String parameterName = sparkMethodParameter.getParameterName();
		String temp = request.queryParamOrDefault(parameterName, null);
		Object returnObj = null;
		if(temp == null){
			return returnObj;
		}
		if(int.class.isAssignableFrom(typeParameter)    || Integer.class.isAssignableFrom(typeParameter)){
			returnObj = new Integer(temp);
		}else if(short.class.isAssignableFrom(typeParameter)  || Short.class.isAssignableFrom(typeParameter)){
			returnObj = new Short(temp);
		}else if(long.class.isAssignableFrom(typeParameter)   || Long.class.isAssignableFrom(typeParameter)){
			returnObj = new Long(temp);
		}else if(float.class.isAssignableFrom(typeParameter)  || Float.class.isAssignableFrom(typeParameter) ){
			returnObj = new Float(temp);
		}else if(double.class.isAssignableFrom(typeParameter) || Double.class.isAssignableFrom(typeParameter)  ){
			returnObj = new Double(temp);
		}else if(char.class.isAssignableFrom(typeParameter)   || Character.class.isAssignableFrom(typeParameter)  ){
			returnObj = temp.toCharArray()[0];
		}else if(byte.class.isAssignableFrom(typeParameter)   || Byte.class.isAssignableFrom(typeParameter)   ){
			returnObj = new Byte(temp);
		}else if(boolean.class.isAssignableFrom(typeParameter)|| Boolean.class.isAssignableFrom(typeParameter)  ){
			returnObj = new Boolean(temp);
		}else{
			returnObj = temp;
		}
		return returnObj;
	}

}

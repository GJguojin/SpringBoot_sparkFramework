package com.gj.spark.structure.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gj.spark.structure.HandlerMethodArgumentResolver;
import com.gj.spark.structure.SparkMethodParameter;

import spark.Request;
import spark.Response;

public class ServletMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(SparkMethodParameter sparkMethodParameter) {
		Class<?> typeParameter = sparkMethodParameter.getParameter().getType();
		if (HttpServletRequest.class.isAssignableFrom(typeParameter) || HttpServletResponse.class.isAssignableFrom(typeParameter)) {
			return true;
		}
		return false;
	}

	@Override
	public Object resolveArgument(SparkMethodParameter parameter, Request request, Response response) throws Exception {
		Object returnObj = null;
		Class<?> typeParameter = parameter.getParameter().getType();
		if(HttpServletRequest.class.isAssignableFrom(typeParameter)){
			returnObj =request.raw();
		}else if(HttpServletResponse.class.isAssignableFrom(typeParameter)){
			returnObj = response.raw();
		}
		return returnObj;
	}

}

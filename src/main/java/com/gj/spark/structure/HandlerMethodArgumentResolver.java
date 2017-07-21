package com.gj.spark.structure;

import spark.Request;
import spark.Response;

public interface HandlerMethodArgumentResolver {
	
	/**
	 * 是否支持参数类型
	 * @param parameter
	 * @return
	 */
	boolean supportsParameter(SparkMethodParameter parameter);
	
	Object resolveArgument(SparkMethodParameter parameter, Request request, Response response) throws Exception;

}

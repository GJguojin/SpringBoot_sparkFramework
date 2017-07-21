package com.gj.spark.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import com.gj.spark.structure.resolver.BasicTypeMethodArgumentResolver;
import com.gj.spark.structure.resolver.ServletMethodArgumentResolver;

import spark.Request;
import spark.Response;

public class RequestMappingHandlerAdapter implements BeanFactoryAware, InitializingBean {
	
	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private HandlerMethodArgumentResolverComposite argumentResolvers;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}

	}

	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		// TODO Auto-generated method stub

	}
	
	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		//resolvers.add(new RequestParamMapMethodArgumentResolver());
		//resolvers.add(new PathVariableMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new ServletMethodArgumentResolver()); //HttpServletRequest与HttpServletResponse
		resolvers.add(new BasicTypeMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		return resolvers;
	}
	
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
		return this.customArgumentResolvers;
	}
	
	public void addCustomArgumentResolver(HandlerMethodArgumentResolver argumentResolver ){
		if(customArgumentResolvers == null){
			customArgumentResolvers = new LinkedList<HandlerMethodArgumentResolver>(); 
		}
		customArgumentResolvers.add(argumentResolver);
	}
	
	public Object getMethodArgumentResolverResult(SparkMethodParameter parameter,Request request,Response response) throws Exception{
		List<HandlerMethodArgumentResolver> resolvers = argumentResolvers.getResolvers();
		Object returnObject = null;
		for(HandlerMethodArgumentResolver resolver :resolvers){
			if(resolver.supportsParameter(parameter)){
				returnObject = resolver.resolveArgument(parameter, request, response);
				if(returnObject != null){
					break;
				}
			}
		}
		return returnObject;
	}
}

package com.gj.spark.utils;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

public class SparkJsonTransformer implements ResponseTransformer {
	
	private ObjectMapper mapper;
	
	
	public SparkJsonTransformer(ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}


	@Override
	public String render(Object model) throws Exception {
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, model);
		return sw.toString();
	}

}

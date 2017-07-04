package com.gj.spark.controller;

import com.gj.spark.annotation.Controller;
import com.gj.spark.annotation.MapURL;
import com.gj.spark.annotation.ResponseBody;
import com.gj.spark.structure.RequestMethod;

@Controller
@MapURL("/rest")
public class TestController {
	
	@ResponseBody
	@MapURL(value = "/test",RequestMethod = RequestMethod.GET)
	public Message  getTest(String s1,String s2){
		System.out.println("=============s1:"+s1);
		System.out.println("=============s2:"+s2);
		return new Message("hello world!!");
	}

	
    @ResponseBody
	@MapURL(value = "/getTest",RequestMethod = RequestMethod.GET)
	public Message  getTest1(){
		return new Message("hello everyone!!");
	}
}

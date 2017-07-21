package com.gj.spark.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.gj.spark.annotation.RequestMapping;
import com.gj.spark.annotation.RequestMethod;

@Controller
public class TestController {
	
	@RequestMapping(path = "/test",method = RequestMethod.GET)
	public Message  getTest(Integer s1,Integer s2,HttpServletRequest request){
		Enumeration<String> attributeNames = request.getAttributeNames();
		System.out.println("=============s1:"+s1);
		System.out.println("=============s2:"+s2);
		return new Message("hello world!!");
	}
	
	@RequestMapping(path = "/test1",method = RequestMethod.GET)
	public Message  getTest(String s1,String s2,String s3){
		System.out.println("=============s1:"+s1);
		System.out.println("=============s2:"+s2);
		return new Message("hello world!!");
	}

	
    @RequestMapping("/getTest")
	public Message  getTest1(){
		return new Message("hello everyone!!");
	}
}

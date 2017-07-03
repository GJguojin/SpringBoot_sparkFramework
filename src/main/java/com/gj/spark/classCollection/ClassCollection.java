package com.gj.spark.classCollection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gj.spark.annotation.Controller;
import com.gj.spark.annotation.MapURL;
import com.gj.spark.annotation.ResponseBody;
import com.gj.spark.structure.MethodPro;
import com.gj.spark.utils.Config;
import com.gj.spark.utils.FileUtils;
import com.gj.spark.utils.StringUtils;


public class ClassCollection {
	public static Map<String, MethodPro> methodMap;
	public static Set<Class<?>> classSet;
	public static Map<String, Class<?>> classMap;

	public static void scanClassSetByPackage(String packageName) {
		methodMap = new HashMap<String, MethodPro>();
		classMap = new HashMap<String, Class<?>>();
		classSet = new HashSet<Class<?>>();
		String filePath = Config.getProPath() + StringUtils.modifyPackagePath(packageName);
		FileUtils.getClassSet(filePath, classSet, packageName);
		for (Class<?> clazz : classSet) {
			if (clazz.isAnnotationPresent(Controller.class)) {
				String mapUrl ="";
				if(clazz.isAnnotationPresent(MapURL.class)){
					MapURL annotation = clazz.getAnnotation(MapURL.class);
					mapUrl += annotation.value();
				}
				
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					String finalUrl = mapUrl;
					if (method.isAnnotationPresent(MapURL.class)) {
						MapURL mapURL = method.getAnnotation(MapURL.class);
						finalUrl += mapURL.value();
						boolean b = false;
						if (method.isAnnotationPresent(ResponseBody.class)) {
							b = true;
						}
						MethodPro mp = new MethodPro(method, mapURL.value(), mapURL.RequestMethod(), b);
						methodMap.put(finalUrl, mp);
						classMap.put(finalUrl, clazz);

					}

				}
			}
		}
	}

	public static Set<Class<?>> getClassSet() {
		return classSet;
	}

	public static Map<String, MethodPro> getMethodMap() {
		return methodMap;
	}

	public static Map<String, Class<?>> getClassMap() {
		return classMap;
	}

	public static void main(String[] args) {
		ClassCollection.scanClassSetByPackage("org.aisframework.web.test");
	}
}

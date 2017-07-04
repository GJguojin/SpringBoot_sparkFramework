package com.gj.spark.classCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.ClassReader;

import com.gj.spark.annotation.Controller;
import com.gj.spark.annotation.MapURL;
import com.gj.spark.annotation.ResponseBody;
import com.gj.spark.asm.ReadMethodArgNameClassVisitor;
import com.gj.spark.structure.MethodPro;
import com.gj.spark.utils.Config;
import com.gj.spark.utils.FileUtils;
import com.gj.spark.utils.StringUtils;


public class ClassCollection {
	public static Map<String, MethodPro> methodMap;
	public static Set<Class<?>> classSet;
	public static Map<String, Class<?>> classMap;
	public static Map<String,ArrayList<String>> methodNamesMap ;

	public static void scanClassSetByPackage(String packageName) {
		methodMap = new HashMap<String, MethodPro>();
		classMap = new HashMap<String, Class<?>>();
		classSet = new HashSet<Class<?>>();
		methodNamesMap = new HashMap<String,ArrayList<String>>();
		String filePath = Config.getProPath() + StringUtils.modifyPackagePath(packageName);
		FileUtils.getClassSet(filePath, classSet, packageName);
		for (Class<?> clazz : classSet) {
			if (clazz.isAnnotationPresent(Controller.class)){
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
						try {
							ArrayList<String> methodNames = getMethodNames(clazz.getName(),method.getName());
							methodNamesMap.put(finalUrl, methodNames);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
						
					}
				}
			}
		}
	}
	
	public static ArrayList<String> getMethodNames(String className, String methodName) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		String cn = Config.getProPath() + className.replace(".", "/") + ".class";
		InputStream is = new FileInputStream(new File(cn));
		ClassReader cr = new ClassReader(is);
		ReadMethodArgNameClassVisitor classVisitor = new ReadMethodArgNameClassVisitor();
		cr.accept(classVisitor, 0);
		for (Entry<String, List<String>> entry : classVisitor.nameArgMap.entrySet()) {
			if (entry.getKey().equals(methodName)) {
				for (String s : entry.getValue()) {
					list.add(s);
				}
			}
		}
		return list;
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
	
	public static Map<String, ArrayList<String>> getMethodNamesMap() {
		return methodNamesMap;
	}

	public static void setMethodNamesMap(Map<String, ArrayList<String>> methodNamesMap) {
		ClassCollection.methodNamesMap = methodNamesMap;
	}

	public static void main(String[] args) {
		ClassCollection.scanClassSetByPackage("org.aisframework.web.test");
	}
}

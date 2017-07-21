package com.gj.spark.asm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ReadMethodArgNameClassVisitor extends ClassVisitor {

	public List<String> argNames = new ArrayList<String>();

	private Method method;

	public ReadMethodArgNameClassVisitor() {
		super(Opcodes.ASM5);
	}

	public ReadMethodArgNameClassVisitor(Method method) {
		super(Opcodes.ASM5);
		this.method = method;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		ReadMethodArgNameMethodVisitor visitor = new ReadMethodArgNameMethodVisitor(Opcodes.ASM5);
		if (name.equals(method.getName())) {
			Type methodType = Type.getMethodType(desc);
			Type[] argumentTypes = methodType.getArgumentTypes();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if(argumentTypes.length == parameterTypes.length){
				int len = argumentTypes.length;
				boolean isEquals = true;
				for(int i=0;i<len;i++){
					if(!argumentTypes[i].getClassName().equals(parameterTypes[i].getName())){
						isEquals = false;
						break;
					}
				}
				if(isEquals){
					visitor.argumentNames = argNames;
					visitor.argLen = len;
				}
			}
		}
		return visitor;
	}
}

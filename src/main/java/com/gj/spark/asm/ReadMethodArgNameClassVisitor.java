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
			
			if(sameType(argumentTypes,parameterTypes)){
				visitor.argumentNames = argNames;
				visitor.argLen = argumentTypes.length;
			}
		}
		return visitor;
	}
	
	/**
	 *
	 * <p>
	 * 比较参数类型是否一致
	 * </p>
	 *
	 * @param types
	 *            asm的类型({@link Type})
	 * @param clazzes
	 *            java 类型({@link Class})
	 * @return
	 */
	private static boolean sameType(Type[] types, Class<?>[] clazzes) {
		// 个数不同
		if (types.length != clazzes.length) {
			return false;
		}
		for (int i = 0; i < types.length; i++) {
			if (!Type.getType(clazzes[i]).equals(types[i])) {
				return false;
			}
		}
		return true;
	}
}

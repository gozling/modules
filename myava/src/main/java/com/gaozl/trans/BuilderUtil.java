package com.ls.dsbr.jsnc.trans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.MessageOrBuilder;
import com.ls.dsbr.common.ResultData;
import com.ls.dsbr.common.Results;
import com.ls.dsbr.log.Log;

public class BuilderUtil {

	final Logger logger = Log.getLogger();

	public static BuilderUtil c() {
		return new BuilderUtil();
	}

	public boolean hasValue(MessageOrBuilder message, Descriptors.FieldDescriptor fieldDescriptor) {
		boolean hasValue = false;
		if (fieldDescriptor.isRepeated()) {
			// 获取重复字段的元素数
			hasValue = message.getRepeatedFieldCount(fieldDescriptor) > 0;
		} else {
			hasValue = !fieldDescriptor.getJavaType().equals(JavaType.MESSAGE) || message.hasField(fieldDescriptor);
		}
		return hasValue;
	}
	public ResultData<TypeDesc> findTypeDesc(Object obj, String name) {
		return this.findTypeDesc(obj, name, true);
	}

	public ResultData<TypeDesc> findTypeDesc(Object obj, String name, boolean isRequired) {
		ResultData<TypeDesc> r = Results.create();
		try {
			Field field = obj.getClass().getDeclaredField(name);
			if (field != null) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
			}
			r.setData(new TypeDesc(field));
		} catch (NoSuchFieldException e) {
			if(!isRequired){
				r.setData(null);
			}else{
				logger.error("findTypeDesc error, field name {}", name);
				r.setException(e);
			}
		}catch ( SecurityException e){
			logger.error(e.getMessage());
			r.setException(e);
		}
		return r;
	}
	@SuppressWarnings("unused")
	private String secondaryName(String name) {
		System.out.println("first Name :" + name);
		return name.replaceAll("([A-Z][a-z]+)", "_$1").toLowerCase();
	}

	@SuppressWarnings("unused")
	private Method findMethod(Class<?> clazz, String name) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : clazz.getDeclaredMethods());
			for (Method method : methods) {
				if (name.equals(method.getName())) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}
}

package com.ls.dsbr.jsnc.trans;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.reflect.TypeToken;

public class TypeDesc {
	private Class<?> valueClz;
	private final Field field;

	public Class<?> getValueClz() {
		return valueClz;
	}

	public void setValueClz(Class<?> valueClz) {
		this.valueClz = valueClz;
	}

	public Field getField() {
		return field;
	}

	public TypeDesc(Field field) {
		Type type = field.getGenericType();
		if (type == null) {
			throw new NullPointerException("type null");
		}
		this.field = field;

		if (type instanceof Class) {
			this.valueClz = (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			if (((ParameterizedType) type).getRawType() == List.class) {
				TypeToken<?> resolveType = TypeToken.of(type).resolveType(List.class.getTypeParameters()[0]);
				this.valueClz = (Class<?>) resolveType.getType();
			}
			// ParameterizedType type1 = (ParameterizedType) type;
			// Type tmp = null;
			// if (type1.getRawType() == List.class &&
			// type1.getActualTypeArguments().length == 1) {
			// tmp = type1.getActualTypeArguments()[0];
			// if (tmp instanceof Class) {
			// valueClz = (Class<?>) tmp;
			// }
			// }
		}
	}
	public boolean mustValueClz() {
		if (this.valueClz == null) {
			throw new NullPointerException("不能识别的type");
		}
		return true;
	}

	public void setValue(Object bean, Object value) {
		try {
			this.getField().set(bean, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Object getValue(Object bean) {
		if (field != null) {
			try {
				return field.get(bean);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

}

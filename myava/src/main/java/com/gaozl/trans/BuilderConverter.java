package com.gaozl.trans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.MessageOrBuilder;
import com.ls.dsbr.common.Jsons;
import com.ls.dsbr.common.ResultData;
import com.ls.dsbr.common.Results;
import com.ls.dsbr.log.Log;

public class BuilderConverter {
	final Logger logger = Log.getLogger();

	private final BuilderUtil bUtil = new BuilderUtil();

	@SuppressWarnings("unchecked")
	public <T> ResultData<T> messageToJava(MessageOrBuilder message, Class<T> clz) {
		Preconditions.checkNotNull(message);
		ResultData<T> result = Results.create();
		List<Descriptors.FieldDescriptor> fields = message.getDescriptorForType().getFields();
		try {
			T t = clz.newInstance();
			for (Descriptors.FieldDescriptor fieldDescriptor : fields) {
				if (!bUtil.hasValue(message, fieldDescriptor)) {
					continue;
				}
				Object oldValue = message.getField(fieldDescriptor);
				String name = fieldDescriptor.getName();
				Object newValue = null;
				ResultData<TypeDesc> findR = bUtil.findTypeDesc(t, name, fieldDescriptor.isRequired());
				if (findR.isSuccess()) {
					TypeDesc typeDesc = findR.getData();
					/**
					 * data null锛�field isOption
					 */
					if (typeDesc == null) {
						continue;
					}
					if (typeDesc != null && typeDesc.mustValueClz()) {
						// object
						boolean isValueMessage = fieldDescriptor.getJavaType().equals(Descriptors.FieldDescriptor.JavaType.MESSAGE);
						if (fieldDescriptor.isRepeated()) {
							List<Object> destList = Lists.newArrayList();
							if (isValueMessage) {
								List<MessageOrBuilder> origList = (List<MessageOrBuilder>) oldValue;
								for (MessageOrBuilder org : origList) {
									ResultData<?> r = messageToJava(org, typeDesc.getValueClz());
									destList.add(r.getData());
								}
							} else {
								List<Object> origList = (List<Object>) oldValue;
								for (Object obj : origList) {
									destList.add(massageValueToJavaValue(obj, typeDesc.getValueClz()));
								}
							}
							newValue = destList;
						} else {
							if (isValueMessage) {
								ResultData<?> r2 = messageToJava((MessageOrBuilder) oldValue, typeDesc.getValueClz());
								if (r2.isSuccess()) {
									newValue = r2.getData();
								}
							} else {
								newValue = massageValueToJavaValue(oldValue, typeDesc.getValueClz());
							}
						}
						if (newValue != null) {
							typeDesc.setValue(t, newValue);
						}
					}
				} else {
					result.setException(findR.getException());
				}

			}
			result.setData(t);
		} catch (InstantiationException | IllegalAccessException e) {
			result.setException(e);
			e.printStackTrace();
		}
		return result;
	}

	public <T> Object massageValueToJavaValue(Object value, Class<T> clz) {

		if (clz.equals(Integer.class) || clz.equals(Long.class) || clz.equals(Float.class) || clz.equals(Double.class)
				|| clz.equals(Boolean.class) || clz.equals(int.class) || clz.equals(long.class) || clz.equals(float.class)
				|| clz.equals(double.class) || clz.equals(boolean.class)) {
			// 鍒ゆ柇clz鏃�濡傛灉鏄痓oolean, 浼拌鏄洿鎺ヨ繑鍥炵殑boolean type,鍥犳鍚屾椂鍖呭惈绫诲瀷鐨勫垽鏂�
			return value;
		} else if (clz.equals(String.class)) {
			if (value instanceof String) {
				return value;
			} else if (value instanceof Descriptors.EnumDescriptor) {
				return ((Descriptors.EnumDescriptor) value).getName();
			} else if (value instanceof Descriptors.EnumValueDescriptor) {
				return ((Descriptors.EnumValueDescriptor) value).getName();
			}
		} else if (clz.isEnum()) {
			try {
				if (value instanceof String) {
					return Jsons.i.fromJson(String.valueOf(value), clz);
				} else if (value instanceof Descriptors.EnumValueDescriptor) {
					return Jsons.i.fromJson(String.valueOf(value), clz);
				}
			} catch (Exception e) {
				return null;
			}
		} else if (clz.equals(BigDecimal.class)) {
			return new BigDecimal((double) value);
		} else if (clz.equals(byte[].class)) {
			return ((ByteString) value).toByteArray();
		}
		return null;

	}

	public <T extends Builder> ResultData<T> javaToMessage(Object bean, T builder) {
		ResultData<T> result = Results.create();
		if (bean == null) {
			throw new NullPointerException("bean null");
		}
		List<Descriptors.FieldDescriptor> fields = builder.getDescriptorForType().getFields();
		for (Descriptors.FieldDescriptor fieldDescriptor : fields) {
			String name = fieldDescriptor.getName();
			ResultData<TypeDesc> findR = bUtil.findTypeDesc(bean, name, fieldDescriptor.isRequired());
			if (findR.isSuccess()) {
				TypeDesc typeDesc = findR.getData();
				if (typeDesc == null) {
					continue;
				}
				Object oldValue = typeDesc.getValue(bean);
				if (oldValue != null) {
					boolean isValueMessage = fieldDescriptor.getJavaType().equals(Descriptors.FieldDescriptor.JavaType.MESSAGE);
					if (fieldDescriptor.isRepeated()) {
						if (oldValue instanceof List) {
							if (isValueMessage) {
								for (Object tmp : (List<?>) oldValue) {
									Message.Builder tmpBuilder = builder.newBuilderForField(fieldDescriptor);
									Builder data = javaToMessage(tmp, tmpBuilder).getData();
									builder.addRepeatedField(fieldDescriptor, data.build());
								}
							} else {
								for (Object tmp : (List<?>) oldValue) {
									builder.addRepeatedField(fieldDescriptor, javaValueToMessageValue(tmp, fieldDescriptor));
									continue;
								}
							}
						}
					} else {
						if (isValueMessage) {
							Message.Builder tmpBuilder = builder.newBuilderForField(fieldDescriptor);
							ResultData<Builder> r2 = javaToMessage(oldValue, tmpBuilder);
							if (r2.isSuccess()) {
								builder.setField(fieldDescriptor, r2.getData().build());
							}
						} else {
							Object newValue = javaValueToMessageValue(oldValue, fieldDescriptor);
							if (newValue != null) {
								builder.setField(fieldDescriptor, newValue);
							}
						}
					}
				}
			} else {
				result.setException(findR.getException());
			}
		}
		result.setData(builder);
		return result;
	}

	public Object javaValueToMessageValue(Object value, FieldDescriptor fieldDescriptor) {
		JavaType type = fieldDescriptor.getJavaType();
		if (type.equals(JavaType.BOOLEAN)) {
			return value;
		} else if (type.equals(JavaType.INT) || type.equals(JavaType.LONG)) {
			if (value instanceof BigInteger) {
				if (type.equals(JavaType.INT)) {
					return ((BigInteger) value).intValue();
				} else {
					return ((BigInteger) value).longValue();
				}
			}
			return value;
		} else if (type.equals(JavaType.FLOAT) || type.equals(JavaType.DOUBLE)) {
			if (value instanceof BigDecimal) {
				if (type.equals(JavaType.FLOAT)) {
					return ((BigDecimal) value).floatValue();
				} else {
					return ((BigDecimal) value).doubleValue();
				}
			}
			return value;
		} else if (type.equals(JavaType.STRING)) {
			return value.toString();

		} else if (type.equals(JavaType.ENUM)) {
			try {
				if (value instanceof String) {
					return fieldDescriptor.getEnumType().findValueByName((String) value);
				} else if (value.getClass().isEnum()) {
					return fieldDescriptor.getEnumType().findValueByName(((Enum<?>) value).name());
				}
			} catch (Exception e) {
			}
		} else if (type.equals(JavaType.BYTE_STRING)) {
			return ByteString.copyFrom(((byte[]) value));
		}
		return null;
	}

}

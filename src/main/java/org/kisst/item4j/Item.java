package org.kisst.item4j;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;

import org.kisst.item4j.seq.TypedSequence;
import org.kisst.item4j.struct.MapStruct;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public class Item {
	@SuppressWarnings("unchecked") public static <T> T cast(Object obj){ return (T) obj; } 

	
	public static String asString(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		return obj.toString();
	}
	public static Struct asStruct(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Struct) return (Struct) obj;
		if (obj instanceof Map) return new MapStruct(cast(obj));
		return new ReflectStruct(obj);
	}		
	public static Integer asInteger(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Integer) return (Integer) obj;
		if (obj instanceof Number) return ((Number)obj).intValue();
		return Integer.parseInt(asString(obj).trim());
	}		
	public static Long asLong(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Long) return (Long) obj;
		if (obj instanceof Number) return ((Number)obj).longValue();
		return Long.parseLong(asString(obj).trim());
	}		
	public static Byte asByte(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Byte) return (Byte) obj;
		if (obj instanceof Number) return ((Number)obj).byteValue();
		return Byte.parseByte(asString(obj).trim());
	}		
	public static Short asShort(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Short) return (Short) obj;
		if (obj instanceof Number) return ((Number)obj).shortValue();
		return Short.parseShort(asString(obj).trim());
	}		
	public static Float asFloat(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Float) return (Float) obj;
		if (obj instanceof Number) return ((Number)obj).floatValue();
		return Float.parseFloat(asString(obj).trim());
	}		
	public static Double asDouble(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Double) return (Double) obj;
		if (obj instanceof Number) return ((Number)obj).doubleValue();
		return Double.parseDouble(asString(obj).trim());
	}				
	public static Boolean asBoolean(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Boolean) return (Boolean) obj;
		return Boolean.parseBoolean(asString(obj).trim());
	}
	public static LocalDate asLocalDate(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalDate) return (LocalDate) obj;
		return LocalDate.parse(asString(obj).trim());
	}
	public static LocalTime asLocalTime(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalTime) return (LocalTime) obj;
		String str = asString(obj).trim();
		if (str==null || str.trim().length()==0)
			return null;
		return LocalTime.parse(str);
	}
	public static LocalDateTime asLocalDateTime(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof LocalDateTime) return (LocalDateTime) obj;
		return LocalDateTime.parse(asString(obj).trim());
	}
	public static Instant asInstant(Object obj) { 
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof Instant) return (Instant) obj;
		return Instant.parse(asString(obj).trim());
	}

	@SuppressWarnings("unchecked")
	public static <T> ImmutableSequence<T> asTypedSequence(Item.Factory factory, Class<T> type, Object obj) {
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (obj instanceof TypedSequence) return ImmutableSequence.smartCopy(factory, type,(TypedSequence<T>) obj);
		if (obj instanceof Collection)   return ImmutableSequence.smartCopy(factory, type, (Collection<T>) obj);
		throw new ClassCastException("Can not make a ItemSequence of type "+obj.getClass()+", "+obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T asType(Factory factory, Class<?> cls, Object obj) {
		if (obj==null || obj==ReflectionUtil.UNKNOWN_FIELD) return null; 
		if (cls.isAssignableFrom(obj.getClass()))
			return (T) obj;
		//System.out.println("Converting "+obj+" to "+cls);
		if (obj instanceof Struct) {
			Object result = factory.construct(cls,(Struct)obj);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		if (obj instanceof Map) {
			Struct struct=new MapStruct((Map<String,Object>)obj);
			Object result = factory.construct(cls,struct);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		if (obj instanceof String) {
			Object result = factory.construct(cls,(String) obj);
			//System.out.println("result is "+result.getClass()+" "+result);
			return cast(result);
		}
		//if (cls.isAssignableFrom(obj.getClass()))
		throw new IllegalArgumentException("Can not convert "+obj+" to "+cls);
	}
	
	
	public interface Factory {
		@SuppressWarnings("unchecked") default public <T> T cast(Object obj){ return (T) obj; } 
		public <T> T construct(Class<?> cls, Object data);
		
		public final static BasicFactory basicFactory=new BasicFactory();
		
		public class BasicFactory implements Factory {
			public<T> T construct(Class<?> cls, Object data){
				Constructor<?> c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{data.getClass()});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {data}));
				c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{this.getClass(), data.getClass()});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {this, data}));
				c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{String.class});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {data}));
				c = ReflectionUtil.getFirstCompatibleConstructor(cls, new Class<?>[]{this.getClass(), String.class});
				if (c!=null)
					return cast(ReflectionUtil.createObject(c, new Object[] {this, data}));
				throw new IllegalArgumentException("Unknown Constructor "+cls+" for data "+data.getClass()+":"+data);
			}
		}
	}
}

package org.kisst.item4j;

import org.kisst.item4j.struct.Struct;

public interface Schema {
	
	//public Field getField(String name);
	public Iterable<String> fieldNames();
	public Class<?> getJavaClass();
	
	public interface Field<FT> extends HasName {
		@Override public String getName();
		default public Object getObject(Struct data) {
			return data.getDirectFieldValue(getName());
		}
	}
	
	public class BasicField<FT> implements Schema.Field<FT> {
		public final Class<FT> javaClass;
		public final String name;
		public BasicField(Class<FT> type, String name) { this.javaClass=type; this.name=name; }
		
		@Override public String getName() { return this.name; }
		public boolean fieldExists(Struct data) { 
			Object d=data.getDirectFieldValue(name);
			return d!=null && d!=Struct.UNKNOWN_FIELD;
		}
		@Override public Object getObject(Struct data) { return data.getDirectFieldValue(name); }
		public Object getObject(Struct data, Object defaultValue) {
			Object result = data.getDirectFieldValue(name);
			if (result==null || result==Struct.UNKNOWN_FIELD)
				return defaultValue;
			return result;
		}
		//@Override public Class<FT> getJavaClass() { return type.getJavaClass(); }
	}
}
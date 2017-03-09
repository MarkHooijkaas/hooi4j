package org.kisst.item4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.Struct;

public abstract class SchemaBase implements Schema {
	
	public class StringField extends BasicField<String>{ 
		public StringField(String name) {	super(String.class, name); }
		public String getString(Struct s) { return Item.asString(s.getDirectFieldValue(name)); }
		public String getString(Struct s, String defaultValue) { return Item.asString(getObject(s,defaultValue)); }
	}
	public class BooleanField extends BasicField<Boolean> {
		public BooleanField(String name) { super(Boolean.class, name); }
		//public boolean getBoolean(Struct s, boolean defaultValue) { return Item.asBoolean(s.getDirectFieldValue(name)); }
		public boolean getBoolean(Struct s) { return Item.asBoolean(s.getDirectFieldValue(name)); }
		public boolean getBoolean(Struct s, boolean defaultValue) { return Item.asBoolean(getObject(s,defaultValue)); }
	}
	public class IntField extends BasicField<Integer> {
		public IntField(String name) { super(Integer.class, name); }
		public int getInt(Struct s) { return  Item.asInteger(s.getDirectFieldValue(name)); }
		public int getInt(Struct s, int defaultValue) { return  Item.asInteger(getObject(s, defaultValue)); }
	}
	public class LongField extends BasicField<Long> { 
		public LongField(String name) { super(Long.class, name); }
		public long getLong(Struct s) { return  Item.asLong(s.getDirectFieldValue(name)); }
		public long getLong(Struct s, long defaultValue) { return  Item.asLong(getObject(s, defaultValue)); }
	}
	public class LocalDateField extends BasicField<LocalDate> { 
		public LocalDateField(String name) { super(LocalDate.class, name); }
		public LocalDate getLocalDate(Struct s) { return  Item.asLocalDate(s.getDirectFieldValue(name,null)); }
	}
	public class LocalTimeField extends BasicField<LocalTime> { 
		public LocalTimeField(String name) { super(LocalTime.class, name); }
		public LocalTime getLocalTime(Struct s) { return  Item.asLocalTime(s.getDirectFieldValue(name,null)); }
	}
	public class LocalDateTimeField extends BasicField<LocalDateTime> { 
		public LocalDateTimeField(String name) { super(LocalDateTime.class, name); }
		public LocalDateTime getLocalDateTime(Struct s) { return  Item.asLocalDateTime(s.getDirectFieldValue(name,null)); }
	}
	public class InstantField extends BasicField<Instant> { 
		public InstantField(String name) { super(Instant.class, name); }
		public Instant getInstant(Struct s) { return  Item.asInstant(s.getDirectFieldValue(name,null)); }
		public Instant getInstant(Struct s, Instant defaultValue) { return  Item.asInstant(s.getDirectFieldValue(name,defaultValue)); }
		public Instant getInstantOrNow(Struct s) {
			Object obj = s.getDirectFieldValue(name);
			if (obj==null || obj==Struct.UNKNOWN_FIELD)
				return Instant.now();
			return Item.asInstant(obj);
		}
		
	}
	@SuppressWarnings("rawtypes")
	public class SequenceField<RT> extends BasicField<ImmutableSequence> {
		public final Class<RT> elementClass;
		public SequenceField(Class<RT> elementClass, String name) { 
			super(ImmutableSequence.class , name);
			this.elementClass=elementClass;
		} 
		public ImmutableSequence<RT> getSequence(Item.Factory factory, Struct data) {
			return (ImmutableSequence<RT>) Item.asTypedSequence(factory, elementClass, data.getDirectFieldValue(name));
		}
		public ImmutableSequence<RT> getSequenceOrEmpty(Item.Factory factory, Struct data) {
			ImmutableSequence<RT> result = (ImmutableSequence<RT>) Item.asTypedSequence(factory, elementClass, data.getDirectFieldValue(name,null));
			if (result==null)
				return Item.cast(ImmutableSequence.EMPTY);
			return result;
		}
	}
}
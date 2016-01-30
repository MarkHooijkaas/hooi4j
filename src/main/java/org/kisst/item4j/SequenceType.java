package org.kisst.item4j;

import org.kisst.item4j.ImmutableSequence;

@SuppressWarnings("rawtypes")
public class SequenceType<T> {//implements Type<ImmutableSequence> {
	public final Class<T> itemClass;
	public SequenceType(Class<T> type) {this.itemClass=type;} 

	public  Class<ImmutableSequence> getJavaClass() { return ImmutableSequence.class; }
	//public  Class<? extends T> getItemClass() { return itemtype.getJavaClass(); }
	//public  Type<T> getItemType() { return itemClass; }

	@SuppressWarnings("unchecked")
	public ImmutableSequence<T> convertFrom(Item.Factory factory, Object obj) {
		if (obj==null) return null; 
		if (obj instanceof ImmutableSequence) return (ImmutableSequence<T>) obj;
		//if (obj instanceof Collection)   return smartCopy(factory, itemtype, (Collection<?>) obj);
		throw new ClassCastException("Can not make a ItemSequence of type "+obj.getClass()+", "+obj);
	}		
}

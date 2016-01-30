package org.kisst.item4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

import org.kisst.item4j.seq.TypedSequence;


public class ImmutableSequence<T> implements TypedSequence<T>, RandomAccess {
	//private final Item.Factory factory;
	public final Class<T> elementClass;
	private final T[] array; 
	
	private ImmutableSequence(Item.Factory fact, Class<T> cls, T[] arr) {
		//this.factory = fact;
		this.elementClass=cls;
		this.array=arr;
	}
	private ImmutableSequence(Class<T> cls, T[] arr) { this(Item.Factory.basicFactory, cls, arr); }

	@Override public int size() { return array.length; }
	@Override public Object getObject(int index) { return array[index]; }
	public Class<?> getElementClass() { return elementClass; }


	public ImmutableSequence<T> subsequence(int start, int end) { return new ImmutableSequence<T>(elementClass, Arrays.copyOfRange(array, start,end)); }
	public ImmutableSequence<T> subsequence(int start) { return subsequence(start, size()); }
	//public ImmutableSequence<T> reverse() { return new ReverseSequence<T>(this); }
	public boolean contains(T elm) {
		if (elm==null) return false;
		for (T e: this)
			if (elm.equals(e))
				return true;
		return false;
	}

	@SafeVarargs
	public static <E> ImmutableSequence<E> of(Class<E> type, E ... elements) {
		return new ImmutableSequence<E>(type, Arrays.copyOf(elements,elements.length));
	}

	@FunctionalInterface
	public static interface StringExpression { public String calculateString(Object item); }
	
	public boolean hasItem(StringExpression expr, String key) { return findItemOrNull(expr, key)!=null; }
	public T findItemOrNull(StringExpression expr, String key) {
		if (key==null)
			return null;
		for (T item : this)
			if (key.equals(expr.calculateString(item)))
				return item;
		return null;
	}
	public ImmutableSequence<T> removeKeyedItem(StringExpression expr, String key) { // TODO: remove all for a key?
		int index=0;
		for (T item: this) {
			if (key.equals(expr.calculateString(item)))
				return remove(index);
			index++;
		}
		return this;
	}
	public ImmutableSequence<T> removeItem(T itemToRemove) { // TODO: remove all for a key?
		if (itemToRemove==null)
			return this;
		int index=0;
		for (T item: this) {
			if (itemToRemove.equals(item))
				return remove(index);
			index++;
		}
		return this;
	}

	
	
	@SuppressWarnings("unchecked")
	public static <E> ImmutableSequence<E> realCopy(Item.Factory factory, Class<E> type, TypedSequence<E> seq) {
		E[] arr = createArray(seq.size());
		int i=0; 
		for (Object obj: seq)
			arr[i++]= (E) Item.asType(factory, type, obj); 
		return new ImmutableSequence<>(type, arr);
	}
	public static <E> ImmutableSequence<E> realCopy(Item.Factory factory, Class<E> elementClass, Collection<? extends E> collection) {
		//System.out.println("Converting "+collection.getClass()+" to Immutable.Sequence of "+ReflectionUtil.smartClassName(elementClass));
		E[] arr = createArray(collection.size());
		int i=0; for (E obj : collection) 
			arr[i++]=Item.asType(factory, elementClass, obj);
		return new ImmutableSequence<E>(elementClass, arr);
	}

	@SuppressWarnings("unchecked")
	public static <E> ImmutableSequence<E> smartCopy(Item.Factory factory, Class<E> type, TypedSequence<E> seq) {
		if (seq==null) throw new NullPointerException("Can not make smartCopy of null");
		if (seq instanceof TypedSequence) {
			if (type==seq.getElementClass())
				return (ImmutableSequence<E>) seq;
			return ImmutableSequence.realCopy(factory, type,  seq);
		}
		if (seq instanceof Collection)
			return realCopy(factory, type,(Collection<E>) seq);
		throw new ClassCastException("Can not make a TypedSequence of type "+seq.getClass()+", "+seq);
	}
	public static <E> ImmutableSequence<E> smartCopy(Item.Factory factory, Class<E> type, Collection<? extends E> collection) {
		if (collection instanceof ImmutableSequence ) 
			return smartCopy(factory, type, collection); 
		return realCopy(factory, type, collection);
	}

	public ImmutableSequence<T> growTail(T tail) {
		T[] arr=Arrays.copyOf(array, array.length+1);
		arr[array.length]=tail;
		return new ImmutableSequence<T>(elementClass, arr); 
	}
	public ImmutableSequence<T> removeFirst() { return subsequence(1); }
	public ImmutableSequence<T> removeLast()  { return subsequence(0,size()-1); }
	public ImmutableSequence<T> remove(int index) { return remove(index, index+1); }
	public ImmutableSequence<T> remove(int begin, int end) {
		T[] arr= createArray(array.length-end+begin);
		int index=0;
		for (int i=0; i<array.length; i++) {
			if (i<begin || i>=end)
				arr[index++]=array[i];
		}
		return new ImmutableSequence<T>(elementClass, arr); 
	}


	@SuppressWarnings("unchecked")
	private static <E> E[] createArray(int length) { return (E[]) new Object[length]; }


	public static ImmutableSequence<?> EMPTY=new ImmutableSequence<Object>(null, new Object[0]);
}
package org.kisst.item4j.seq;

import java.util.Iterator;

public interface TypedSequence<T> extends Iterable<T>{
	public Class<?> getElementClass();
	public int size();
	public Object getObject(int index); 
	
	@SuppressWarnings("unchecked")
	default public T get(int index) { return (T) getObject(index); }
	default public Iterator<T> iterator() { return new IndexIterator<>(this); }
	default public ReverseIteratable<T> reverse() { return new ReverseIteratable<>(this); }
	
	default public String toFullString() {
		StringBuilder result=new StringBuilder("[");
		String sep="";
		for (Object obj:this) { result.append(sep+obj); sep=","; }
		return result.toString()+"]";
	}
		
	default void checkIndex(int index) {
		if (index<0)
			throw new IndexOutOfBoundsException("index "+index+" should be >=0");
		if (index>size())
			throw new IndexOutOfBoundsException("index "+index+" should be less or equal to size "+size());
	}
	
	public class IndexIterator<TT> implements Iterator<TT>{
		protected final TypedSequence<TT> seq;
		public IndexIterator(TypedSequence<TT> seq) { this.seq=seq; }
		protected int index=0;
		@Override public boolean hasNext() { return index<seq.size();}
		@Override public TT next() { return seq.get(index++); }
		@Override public void remove() { throw new RuntimeException("remove is not allowed on this list"); }
	}
	public final class ReverseIteratable<TT>  implements Iterable<TT>{
		private final TypedSequence<TT> seq;
		public ReverseIteratable(TypedSequence<TT> seq) {this.seq=seq; }
		@Override public Iterator<TT> iterator() { return new ReverseIterator<>(seq); }
	}
	public final class ReverseIterator<TT> extends IndexIterator<TT>{
		public ReverseIterator(TypedSequence<TT> seq) {super(seq); }
		@Override public TT next() { return seq.get(seq.size()-(++index)); }
	}
}

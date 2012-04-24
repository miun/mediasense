package manager.dht;

import java.util.HashMap;

public class DoubleSidedHashMap<E,T> {
	private HashMap<E,T> l;
	private HashMap<T,E> r;
	
	public DoubleSidedHashMap() {
		l = new HashMap<E,T>();
		r = new HashMap<T,E>();
	}
	
	public T getL(E e) {
		return l.get(e);
	}
	
	public E getR(T t) {
		return r.get(t);
	}
	
	public void put(E e , T t) {
		T tOld = l.put(e, t);
		if(tOld != null  && !t.equals(tOld)) {
			r.remove(tOld);
		}
		r.put(t, e); 
	}
	
	public T removeL(E e) {
		T rem = l.remove(e);
		if(rem != null) {
			r.remove(rem);
		}
		return rem;
	}
	
	public E removeR(T t) {
		E rem = r.remove(t);
		if(rem != null) {
			l.remove(rem);
		}
		return rem;
	}
	
	public boolean containsL(E e) {
		return l.containsKey(e);
	}
	
	public boolean containsR(T t) {
		return r.containsKey(t);
	}
}

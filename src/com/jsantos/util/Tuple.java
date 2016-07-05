package com.jsantos.util;

public class Tuple<T1, T2> {
	public T1 t1 = null;
	public T2 t2 = null;
	
	public Tuple(T1 t1, T2 t2){
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Tuple(){
	}
	
	public boolean isEmpty(){
		return (null==t1 && null==t2);
	}
}

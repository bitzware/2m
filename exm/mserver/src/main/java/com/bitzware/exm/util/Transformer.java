package com.bitzware.exm.util;

public interface Transformer<S, T> {

	T transform(S source);
	
}

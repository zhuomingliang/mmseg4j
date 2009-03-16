package com.chenlb.mmseg4j;

/**
 * 
 * 
 * @author chenlb 2009-3-16 下午09:15:30
 */
public abstract class Seg {

	protected Dictionary dic;
	
	public Seg(Dictionary dic) {
		super();
		this.dic = dic;
	}


	public abstract Chunk seg(Sentence sen);
}

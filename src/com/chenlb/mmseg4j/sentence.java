package com.chenlb.mmseg4j;


/**
 * 它是MMSeg分词算法中一个关键的概念。Chunk中包含依据上下文分出的一组词和相关的属性，包括长度(Length)、平均长度(Average Length)、标准差的平方(Variance)和自由语素度(Degree Of Morphemic Freedom)。
 * 
 * @author chenlb 2009-3-3 下午11:56:53
 */
public class Sentence {

	private char[] text;
	private int offest;

	public sentence(char[] text) {
		super();
		this.text = text;
	}

	public char[] getText() {
		return text;
	}

	public int getOffest() {
		return offest;
	}

	public void setOffest(int offest) {
		this.offest = offest;
	}
	
}

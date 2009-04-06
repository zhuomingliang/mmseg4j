package com.chenlb.mmseg4j.analysis;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Seg;

/**
 * mmseg 的 complex analyzer
 * 
 * @author chenlb 2009-3-16 下午10:08:16
 */
public class ComplexAnalyzer extends MMSegAnalyzer {

	public ComplexAnalyzer() {
		super();
	}

	public ComplexAnalyzer(String path) {
		super(path);
	}
	
	protected Seg newSeg() {
		return new ComplexSeg(dic);
	}
}

package com.chenlb.mmseg4j.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

/**
 * 
 * 
 * @author chenlb 2009-3-16 下午10:08:16
 */
public class ComplexAnalyzer extends Analyzer {

	private Seg seg;
	
	public ComplexAnalyzer() {
		seg = new ComplexSeg(new Dictionary());
	}
	
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(seg, reader);
		return ts;
	}

}

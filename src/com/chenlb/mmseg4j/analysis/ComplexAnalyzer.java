package com.chenlb.mmseg4j.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;

/**
 * 
 * 
 * @author chenlb 2009-3-16 下午10:08:16
 */
public class ComplexAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(new ComplexSeg(new Dictionary()), reader);
		return ts;
	}

}

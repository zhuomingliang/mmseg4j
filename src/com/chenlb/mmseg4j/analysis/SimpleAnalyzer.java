package com.chenlb.mmseg4j.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.SimpleSeg;

/**
 * 
 * 
 * @author chenlb 2009-3-16 下午10:08:13
 */
public class SimpleAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(new SimpleSeg(new Dictionary()), reader);
		return ts;
	}
}

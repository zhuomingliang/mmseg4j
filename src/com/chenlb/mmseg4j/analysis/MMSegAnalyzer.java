package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class MMSegAnalyzer extends Analyzer {

	protected Dictionary dic;
	
	public MMSegAnalyzer() {
		dic = new Dictionary();
	}
	
	/**
	 * @param path 词库路径
	 */
	public MMSegAnalyzer(String path) {
		dic = new Dictionary(path);
	}
	
	protected Seg newSeg() {
		return new ComplexSeg(dic);
	}
	
	@Override
	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		
		MMSegTokenizer mmSegTokenizer = (MMSegTokenizer) getPreviousTokenStream();
		if(mmSegTokenizer == null) {
			mmSegTokenizer = new MMSegTokenizer(newSeg(), reader);
			setPreviousTokenStream(mmSegTokenizer);
		} else {
			mmSegTokenizer.reset(reader);
		}
		
		return mmSegTokenizer;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(newSeg(), reader);
		return ts;
	}
}

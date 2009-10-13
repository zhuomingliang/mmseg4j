package com.chenlb.mmseg4j.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;

/**
 * 默认使用 max-word
 * 
 * @see {@link SimpleAnalyzer}, {@link ComplexAnalyzer}, {@link MaxWordAnalyzer}
 * 
 * @author chenlb
 */
public class MMSegAnalyzer extends Analyzer {

	protected Dictionary dic;
	
	/**
	 * @see Dictionary#getInstance()
	 */
	public MMSegAnalyzer() {
		dic = Dictionary.getInstance();
	}
	
	/**
	 * @param path 词库路径
	 * @see Dictionary#getInstance(String)
	 */
	public MMSegAnalyzer(String path) {
		dic = Dictionary.getInstance(path);
	}
	
	/**
	 * @param path 词库目录
	 * @see Dictionary#getInstance(File)
	 */
	public MMSegAnalyzer(File path) {
		dic = Dictionary.getInstance(path);
	}
	
	public MMSegAnalyzer(Dictionary dic) {
		super();
		this.dic = dic;
	}

	protected Seg newSeg() {
		return new MaxWordSeg(dic);
	}
	
	@Override
	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		
		SavedStreams streams = (SavedStreams) getPreviousTokenStream();
		if(streams == null) {
			streams = new SavedStreams();
			streams.mmsegTokenizer = new MMSegTokenizer(newSeg(), reader);
			streams.tokenFilter = new LowerCaseFilter(streams.mmsegTokenizer);
			setPreviousTokenStream(streams);
		} else {
			streams.mmsegTokenizer.reset(reader);
		}
		
		return streams.tokenFilter;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(newSeg(), reader);
		return ts;	//new LowerCaseFilter(ts);
	}
	
	private static final class SavedStreams {
		MMSegTokenizer mmsegTokenizer;
		TokenFilter tokenFilter;
	}
}

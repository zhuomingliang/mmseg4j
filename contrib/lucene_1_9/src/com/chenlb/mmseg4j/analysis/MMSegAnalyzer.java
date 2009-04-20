package com.chenlb.mmseg4j.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;

/**
 * 默认使用 max-word
 * 
 * @author chenlb
 */
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
	
	public MMSegAnalyzer(Dictionary dic) {
		super();
		this.dic = dic;
	}

	protected Seg newSeg() {
		return new MaxWordSeg(dic);
	}
	
	/*// lucene 底版本不支持
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
	
	private static final class SavedStreams {
		MMSegTokenizer mmsegTokenizer;
		TokenFilter tokenFilter;
	}
	*/

	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new MMSegTokenizer(newSeg(), reader);
		return new LowerCaseFilter(ts);
	}
	
	
}

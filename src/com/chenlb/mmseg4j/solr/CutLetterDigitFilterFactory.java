package com.chenlb.mmseg4j.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

import com.chenlb.mmseg4j.analysis.CutLetterDigitFilter;

/**
 * CutLetterDigitFilter 支持在 solr 上配置用。
 * 
 * @author chenlb 2010-12-17下午10:10:48
 */
public class CutLetterDigitFilterFactory extends BaseTokenFilterFactory {

	@Override
	public TokenStream create(TokenStream input) {
		
		return new CutLetterDigitFilter(input);
	}

}

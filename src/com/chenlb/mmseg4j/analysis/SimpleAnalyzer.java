package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

public class SimpleAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = new SimpleTokenizer(reader);
		return ts;
	}

	public static void main(String[] args) throws IOException {
		String txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		SimpleAnalyzer analyzer = new SimpleAnalyzer();
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		for(Token t= new Token(); (t=ts.next(t)) !=null;) {
			System.out.println(t);
		}
	}
}

package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

public class AnalyzerTest extends TestCase {

	String txt = "";
	protected void setUp() throws Exception {
		super.setUp();
		txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
	}

	public void testSimple() {
		SimpleAnalyzer analyzer = new SimpleAnalyzer();
		
		try {
			printlnToken(txt, analyzer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testComplex() {
		Analyzer analyzer = new ComplexAnalyzer();
		try {
			printlnToken(txt, analyzer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printlnToken(String txt, Analyzer analyzer) throws IOException {
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		for(Token t= new Token(); (t=ts.next(t)) !=null;) {
			System.out.println(t);
		}
	}
}

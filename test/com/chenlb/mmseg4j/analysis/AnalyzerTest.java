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
		txt = "２００９年ゥスぁま是中 ＡＢｃｃ国абвгαβγδ首次,我的ⅠⅡⅢ在chenёlbēū全国ㄦ范围ㄚㄞㄢ内①ē②㈠㈩⒈⒑发行地方政府债券，";
	}

	public void testSimple() {
		SimpleAnalyzer analyzer = new SimpleAnalyzer();
		//ēū
		//txt = "２００９年ゥスぁま是中ＡＢｃｃ国абвгαβγδ首次,我的ⅠⅡⅢ在chenёlbēū全国ㄦ范围ㄚㄞㄢ内①②㈠㈩⒈⒑发行地方政府债券，";
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
			//txt = "核心提示：3月13日上午，近3000名全国人大代表按下表决器，高票批准了温家宝总理代表国务院所作的政府工作报告。这份工作报告起草历时3个月，由温家宝总理亲自主持。";
			//printlnToken(txt, analyzer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printlnToken(String txt, Analyzer analyzer) throws IOException {
		System.out.println("---------\n"+txt);
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		for(Token t= new Token(); (t=ts.next(t)) !=null;) {
			System.out.println(t);
		}
	}
}

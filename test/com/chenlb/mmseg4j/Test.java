package com.chenlb.mmseg4j;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void test100Log() {
		int freq = 1034142;
		print100Log(freq);
		
		freq = 847332;
		print100Log(freq);
	}
	
	private void print100Log(int freq) {
		int my100Log = (int) (Math.log(freq) * 100);
		System.out.println(freq+" -> "+my100Log+" | "+(Math.log(freq) * 100));
	}
	
	public void testDicPath() throws URISyntaxException {
		URL url = Dictionary.class.getResource("/");
		String path = "";
		path = url.toURI().getRawPath();
		System.out.println(path);
		File f = new File(path+"data");
		System.out.println(f+" -> "+f.exists());
		
		
		path = url.toExternalForm();
		System.out.println(path);
		
		path = url.getPath();
		System.out.println(path);
		
		path = System.getProperty("user.dir");
		System.out.println(path);
	}
	
	public void testZhNumCodeP() {
		String num = "０１２３４５６７８９";
		String n = "0123456789";
		for(int i=0; i<num.length(); i++) {
			int cp = num.codePointAt(i);
			int ncp = n.codePointAt(i);
			System.out.println((char)cp+" -> "+cp+", "+(char)ncp+" -> "+ncp);
		}
	}
	
	public void testCodePAndType() {
		String str = "09０９☆§┍┄○一＄￥≈∑①②！中文【ゥスぁまēūㄇㄎноνπⅠⅡⅢ";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<str.length(); i++) {
			sb.setLength(0);
			int cp = str.codePointAt(i);
			sb.appendCodePoint(cp).append(" -> ").append(cp);
			sb.append(", type=").append(Character.getType(cp));
			System.out.println(sb);
		}
	}
}

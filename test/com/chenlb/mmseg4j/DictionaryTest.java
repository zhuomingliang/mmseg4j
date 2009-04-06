package com.chenlb.mmseg4j;

import java.io.File;

import junit.framework.TestCase;

import com.chenlb.mmseg4j.Dictionary.DicKey;

public class DictionaryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void loadDic() {
		Dictionary dic = new Dictionary();
		System.out.println("load match");
		dic = new Dictionary();
		System.out.println("load data");
		dic = new Dictionary("data");
		System.out.println("load sogou");
		dic = new Dictionary("sogou");
	}

	public void loadMultiDic() {
		Dictionary dic = new Dictionary();
		
		assertTrue(dic.match("白云山"));
	}
	
	public void testMatch() {
		Dictionary dic = new Dictionary();
		
		assertTrue(dic.match("词典"));
		
		assertFalse(dic.match("人个"));
		assertFalse(dic.match("三个人"));
		
		assertFalse(dic.match(""));
		assertFalse(dic.match("人"));

	}
	
	public void testFileHashCode() {
		File f = new File("data");
		File f1 = new File("M:/eclipse 3.3.2/workspace/mmseg4j/data");
		System.out.println(f.getAbsolutePath());
		System.out.println(f1);
		System.out.println(f.equals(f1));
	}
	
	public void testDicKey() {
		DicKey dk = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data");
		DicKey dk2 = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data/");
		
		assertFalse(dk.equals(dk2));
		assertTrue(dk2.equals(dk2));
		
		dk = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data/");
		assertTrue(dk.equals(dk2));
	}
}

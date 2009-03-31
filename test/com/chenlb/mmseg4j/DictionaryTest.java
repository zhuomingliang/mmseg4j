package com.chenlb.mmseg4j;

import java.io.File;

import com.chenlb.mmseg4j.Dictionary.DicKey;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void loadDic() {
		Dictionary dic = new Dictionary();
		dic = new Dictionary();
		dic = new Dictionary("data");
		dic = new Dictionary("sogou");
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
		DicKey dk = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data/chars.dic", "data/words.dic");
		DicKey dk2 = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data/chars.dic", "M:/eclipse 3.3.2/workspace/mmseg4j/data/words.dic");
		
		assertFalse(dk.equals(dk2));
		assertTrue(dk2.equals(dk2));
		
		dk = new DicKey("M:/eclipse 3.3.2/workspace/mmseg4j/data/chars.dic", "M:/eclipse 3.3.2/workspace/mmseg4j/data/words.dic");
		assertTrue(dk.equals(dk2));
	}
}

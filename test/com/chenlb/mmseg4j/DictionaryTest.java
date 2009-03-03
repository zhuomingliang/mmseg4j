package com.chenlb.mmseg4j;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void loadDic() {
		Dictionary dic = new Dictionary();
	}

	public void testMatch() {
		Dictionary dic = new Dictionary();
		
		assertTrue(dic.match("词典"));
		
		assertFalse(dic.match("人个"));
		assertFalse(dic.match("三个人"));
		
		assertFalse(dic.match(""));
		assertFalse(dic.match("人"));

	}
}

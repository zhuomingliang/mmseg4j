package com.chenlb.mmseg4j;

import java.io.IOException;

import com.chenlb.mmseg4j.example.Simple;

import junit.framework.TestCase;

public class SimpleSegTest extends TestCase {

	Simple segW;
	protected void setUp() throws Exception {
		segW = new Simple();
	}

	public void testEffect() throws IOException {
		String words = segW.segWords("研究生命起源", "|");
		assertEquals("研究生|命|起源", words);
	}
	
	public void testEffect1() throws IOException {
		String words = segW.segWords("为首要考虑", "|");
		assertEquals("为首|要|考虑", words);
	}
	
	public void testEffect2() throws IOException {
		String words = segW.segWords("眼看就要来了", "|");
		assertEquals("眼看|就要|来了", words);
	}
	
	public void testEffect3() throws IOException {
		String words = segW.segWords("中西伯利亚", "|");
		assertEquals("中西|伯利|亚", words);
	}
	
	public void testEffect4() throws IOException {
		String words = segW.segWords("国际化", "|");
		assertEquals("国际化", words);
	}
	
	public void testEffect5() throws IOException {
		String words = segW.segWords("化装和服装", "|");
		assertEquals("化装|和服|装", words);
	}
	
	public void testEffect6() throws IOException {
		String words = segW.segWords("中国人民银行", "|");
		assertEquals("中国人民银行", words);
	}
	
	/**
	 * 自扩展的词库文件
	 */
	public void testEffect7() throws IOException {
		String words = segW.segWords("白云山", "|");
		assertEquals("白云山", words);
	}
	
	public void testUnitEffect() throws IOException {
		String words = segW.segWords("2008年中有很多事情", "|");
		assertEquals("2008|年|中有|很多|事情", words);
	}
	
	public void testUnitEffect1() throws IOException {
		String words = segW.segWords("20分钟能完成", "|");
		assertEquals("20|分钟|能|完成", words);
	}
}

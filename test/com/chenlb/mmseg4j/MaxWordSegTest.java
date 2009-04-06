package com.chenlb.mmseg4j;

import java.io.IOException;

import junit.framework.TestCase;

import com.chenlb.mmseg4j.example.MaxWord;

public class MaxWordSegTest extends TestCase {

	MaxWord segW;
	protected void setUp() throws Exception {
		segW = new MaxWord();
	}

	public void testEffect() throws IOException {
		String words = segW.segWords("共和国", "|");
		assertEquals("共和|国", words);
	}
	
	public void testEffect1() throws IOException {
		String words = segW.segWords("中国人民银行", "|");
		assertEquals("中国|国人|人民|银行", words);
	}
	
	public void testEffect2() throws IOException {
		String words = segW.segWords("西伯利亚", "|");
		assertEquals("西|伯利|利亚", words);
	}
	
	public void testEffect3() throws IOException {
		String words = segW.segWords("中华人民共和国", "|");
		assertEquals("中华|华人|人民|共和|国", words);
	}
	
	public void testEffect4() throws IOException {
		String words = segW.segWords("羽毛球拍", "|");
		assertEquals("羽毛|球拍", words);
	}
	
	public void testEffect5() throws IOException {
		String words = segW.segWords("化装和服装", "|");
		assertEquals("化装|和|服装", words);
	}
	
	public void testEffect6() throws IOException {
		String words = segW.segWords("为什么", "|");
		assertEquals("为|什么", words);
	}
	
	public void testEffect7() throws IOException {
		String words = segW.segWords("很好听", "|");
		assertEquals("很好|好听", words);
	}
	
	public void testEffect8() throws IOException {
		String words = segW.segWords("强冷空气", "|");
		assertEquals("强|冷|空气", words);
	}
	
	/**
	 * 自扩展的词库文件
	 */
	public void testEffect9() throws IOException {
		String words = segW.segWords("白云山", "|");
		assertEquals("白云|云山", words);
	}
	
	public void testEffect10() throws IOException {
		String words = segW.segWords("清华大学", "|");
		assertEquals("清华|大学", words);
	}
	
	public void testEffect11() throws IOException {
		String words = segW.segWords("华南理工大学", "|");
		assertEquals("华南|理工|工大|大学", words);
	}
	
	public void testEffect12() throws IOException {
		String words = segW.segWords("广东工业大学", "|");
		assertEquals("广东|工业|大学", words);
	}
}

package com.chenlb.mmseg4j;

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
}

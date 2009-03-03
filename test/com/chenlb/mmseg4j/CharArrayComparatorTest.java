package com.chenlb.mmseg4j;

import junit.framework.TestCase;

public class CharArrayComparatorTest extends TestCase {

	CharArrayComparator cac = new CharArrayComparator();
	protected void setUp() throws Exception {
	}

	public void testCompare() {
		char[] a = {1,3,5,6,7};
		char[] b = {1,3,5,7,7};
		assertEquals(cac.compare(a, b), -1);
		
		a = new char[] {};
		b = new char[] {5};
		assertEquals(cac.compare(a, b), -1);
		
		a = new char[] {1,3,5,6,7};
		b = new char[] {1,3,5,6,7};
		assertEquals(cac.compare(a, b), 0);
		
		a = new char[] {};
		b = new char[] {};
		assertEquals(cac.compare(a, b), 0);
		
		a = new char[] {1,3,5,7};
		b = new char[] {1,3,5,6,7};
		assertEquals(cac.compare(a, b), 1);
	}
}

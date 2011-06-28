package com.chenlb.mmseg4j;

import java.io.File;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	private void printMemory() {
		Runtime rt = Runtime.getRuntime();
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		long max = rt.maxMemory();
		System.out.println(String.format("total=%d, free=%d, max=%d, use=%d", total/1024, free/1024, max/1024, (total-free)/1024));
	}
	
	public void testloadDicMemoryUse() {
		printMemory();
		Dictionary dic = Dictionary.getInstance();
		printMemory();
	}
	
	public void testloadDic() throws InterruptedException {
		Dictionary dic = Dictionary.getInstance();
		System.out.println("load match");
		dic = Dictionary.getInstance();
		
		Thread.sleep(100);
		
		dic.destroy();
		System.out.println("reload");
		dic = Dictionary.getInstance();
		dic.destroy();
		//dic = null;
		System.out.println("load data");
		dic = Dictionary.getInstance("data");
		dic.destroy();
		//dic = null;
		System.out.println("load sogou");
		dic = Dictionary.getInstance("sogou");
		dic.destroy();
		//dic = null;
	}

	public void testloadMultiDic() {
		Dictionary dic = Dictionary.getInstance();
		
		assertTrue(dic.match("白云山"));
	}
	
	public void testMatch() {
		Dictionary dic = Dictionary.getInstance();
		
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
		
		f1 = new File("data");
		System.out.println(f.equals(f1)+" -> "+f.hashCode()+", "+f1.hashCode());
		
		f1 = new File("./data");
		System.out.println(f.equals(f1));
	}
}

package com.chenlb.mmseg4j;

import junit.framework.TestCase;

public class ComplexSegTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSeg() {
		String txt = "";
		//txt = "研究生命起源";
		//txt = "为首要考虑";
		//txt = "眼看就要来了";
		//txt = "中西伯利亚";
		//txt = "人生三子";
		txt = "国际化";
		//txt = "中国";
		//txt = "我";
		txt = "各人发表关于受一股来自中西伯利亚的强冷空气影响";
		ComplexSeg.setShowChunk(true);
		ComplexSeg seg = new ComplexSeg(new Dictionary("sogou"));
		Sentence sen = new Sentence(txt.toCharArray(), 0);
		System.out.println();
		while(!sen.isFinish()) {
			Chunk chunk = seg.seg(sen);
			System.out.println(chunk+" -> "+chunk.getStartOffset());
		}
	}

}

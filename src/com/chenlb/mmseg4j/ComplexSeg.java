package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;

public class ComplexSeg {

	private Dictionary dic = new Dictionary();
	
	
	public void seg(String gbkStr) {
		int offset = 0;
		char[] chs = gbkStr.toCharArray();
		int[] w = new int[3];
		int[] offsets = new int[3];
		while(offset < chs.length) {
			//System.out.println();
			int maxLen = 3;
			offsets[0] = offset;
			for(w[0]=maxLen(chs, offsets[0]); w[0]>=0; w[0]--) {
				int idx = search(chs, offsets[0], w[0]);
				if(idx > -1 || w[0]==0) {	//idx > -1 找到, w[0]==0单个字
					offsets[1] = offsets[0]+1+w[0];
					for(w[1]=maxLen(chs, offsets[1]); w[1]>=0; w[1]--) {
						idx = search(chs, offsets[1], w[1]);
						if(idx > -1 || w[1]==0) {
							offsets[2] = offsets[1]+1+w[1];
							for(w[2]=maxLen(chs, offsets[2]); w[2]>=0; w[2]--) {
								idx = search(chs, offsets[2], w[2]);
								if(idx > -1 || w[2]==0) {	//有chunk
									Chunk ck = new Chunk();
									int len = 0;
									for(int i=0; i<3; i++) {
										len += w[i]+1;
										
										if(offsets[i] < chs.length) {
											ck.words[i] = new char[w[i]+1];
											System.arraycopy(chs, offsets[i], ck.words[i], 0, w[i]+1);
										}
									}
									if(len > maxLen) {
										maxLen = len;
									}
									//System.out.println(ck);
								}
							}
						}
					}
				}
			}
			offset+=maxLen;
		}
	}
	
	private int search(char[] chs, int offset, int len) {
		if(len == 0) {
			return -1;
		}
		CharNode cn = dic.head(chs[offset]);
		char[] subChs = new char[len];
		System.arraycopy(chs, offset+1, subChs, 0, len);
		return dic.search(cn, subChs);
	}
	
	private int maxLen(char[] chs, int offset) {
		if(offset >= chs.length) {
			return 0;
		}
		return Math.min(dic.head(chs[offset]).getMaxLen(), chs.length-offset-1);
	}
	
	public static void main(String[] args) {
		String 
		txt = "研究生命起源";
		txt = "眼看就要来了";
		txt = "中西伯利亚";
		txt = "人生三子";
		txt = "国际化";
		txt = "中国";
		txt = "我";
		txt = "受一股来自中西伯利亚的强冷空气影响";
		
		ComplexSeg cs = new ComplexSeg();
		cs.seg(txt);
	}

	private static class Chunk {
		char[][] words = new char[3][];

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(char[] word : words) {
				if(word != null) {
					sb.append(word).append('_');
				}
			}
			return sb.toString();
		}
		
		
	}
}

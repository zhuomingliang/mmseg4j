package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;

import com.chenlb.mmseg4j.rule.LargestAvgLenRule;
import com.chenlb.mmseg4j.rule.LargestSumDegreeFreedomRule;
import com.chenlb.mmseg4j.rule.MaxMatchRule;
import com.chenlb.mmseg4j.rule.Rule;
import com.chenlb.mmseg4j.rule.SmallestVarianceRule;


public class ComplexSeg {

	private Dictionary dic = new Dictionary();
	private MaxMatchRule mmr = new MaxMatchRule();
	private List<Rule> otherRules = new ArrayList<Rule>();
	
	public ComplexSeg() {
		otherRules.add(new LargestAvgLenRule());
		otherRules.add(new SmallestVarianceRule());
		otherRules.add(new LargestSumDegreeFreedomRule());
	}
	
	public Chunk seg(Sentence sen) {
		//int offset = 0;
		char[] chs = sen.getText();
		int[] w = new int[3];
		int[] offsets = new int[3];
		mmr.reset();
		if(sen.getOffset() < chs.length) {
			//System.out.println();
			int maxLen = 3;
			offsets[0] = sen.getOffset();
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
											if(w[i] == 0) {
												CharNode cn = dic.head(chs[offsets[i]]);
												if(cn !=null) {
													ck.degrees[i] = cn.getFreq();
												}
											}
										}
									}
									if(len > maxLen) {
										maxLen = len;
									}
									mmr.addChunk(ck);
									//System.out.println(ck);
								}
							}
						}
					}
				}
			}
			sen.addOffset(maxLen);
			List<Chunk> chunks = mmr.remainChunks();
			for(Rule rule : otherRules) {
				//System.out.println("-------filter before "+rule+"----------");
				//printChunk(chunks);
				if(chunks.size() > 1) {
					rule.reset();
					rule.addChunks(chunks);
					chunks = rule.remainChunks();
				} else {
					break;
				}
			}
			//System.out.println("-------remainChunks----------");
			//printChunk(chunks);
			if(chunks.size() > 0) {
				return chunks.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private void printChunk(List<Chunk> chunks) {// for debug
		for(Chunk ck : chunks) {
			System.out.println(ck+" -> "+ck.toFactorString());
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
		String txt = "";
		//txt = "研究生命起源";
		//txt = "为首要考虑";
		//txt = "眼看就要来了";
		//txt = "中西伯利亚";
		//txt = "人生三子";
		txt = "国际化";
		//txt = "中国";
		//txt = "我";
		//txt = "受一股来自中西伯利亚的强冷空气影响";
		
		ComplexSeg cs = new ComplexSeg();
		cs.seg(new Sentence(txt.toCharArray(), 0));
	}

/*	private static class Chunk {
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
	}*/
}

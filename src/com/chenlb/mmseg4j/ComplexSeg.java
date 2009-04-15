package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;

import com.chenlb.mmseg4j.Chunk.Word;
import com.chenlb.mmseg4j.rule.LargestAvgLenRule;
import com.chenlb.mmseg4j.rule.LargestSumDegreeFreedomRule;
import com.chenlb.mmseg4j.rule.MaxMatchRule;
import com.chenlb.mmseg4j.rule.Rule;
import com.chenlb.mmseg4j.rule.SmallestVarianceRule;


/**
 * 正向最大匹配, 加四个过虑规则的分词方式.
 * 
 * @author chenlb 2009-3-16 下午09:15:26
 */
public class ComplexSeg extends Seg{

	private MaxMatchRule mmr = new MaxMatchRule();
	private List<Rule> otherRules = new ArrayList<Rule>();
	
	private static boolean showChunk = false;
	
	public ComplexSeg(Dictionary dic) {
		super(dic);
		otherRules.add(new LargestAvgLenRule());
		otherRules.add(new SmallestVarianceRule());
		otherRules.add(new LargestSumDegreeFreedomRule());
	}
	
	public Chunk seg(Sentence sen) {
		char[] chs = sen.getText();
		int[] tailLen = new int[3];	//记录词的尾长
		int[] maxTailLen = new int[3];	//记录词尾部允许的长度
		CharNode[] cns = new CharNode[3];

		int[] offsets = new int[3];	//每个词在sen的开始位置
		mmr.reset();
		if(!sen.isFinish()) {	//sen.getOffset() < chs.length
			if(showChunk) {
				System.out.println();
			}
			int maxLen = 0;
			offsets[0] = sen.getOffset();
			/*
			 * 遍历所有不同词长,还不是从最大到0(w[0]=maxLen(chs, offsets[0]); w[0]>=0; w[0]--)
			 * 可以减少一部分多余的查找.
			 */
			for(int aLen : getLens(cns, 0, chs, offsets[0], maxTailLen, 0)) {
				if(aLen > maxTailLen[0]) {	//aLen不合格(是句子未处理的长度小于aLen)
					continue;
				}
				tailLen[0] = aLen;
				int idx = search(cns[0], chs, offsets[0], tailLen[0]);
				if(idx > -1 || tailLen[0]==0) {	//idx > -1 找到, tailLen[0]==0单个字
					offsets[1] = offsets[0]+1+tailLen[0];	//第二个词的开始位置
					for(int bLen : getLens(cns, 1, chs, offsets[1], maxTailLen, 1)) {
						if(bLen > maxTailLen[1]) {
							continue;
						}
						tailLen[1] = bLen;
						idx = search(cns[1], chs, offsets[1], tailLen[1]);
						if(idx > -1 || tailLen[1]==0) {
							offsets[2] = offsets[1]+1+tailLen[1];
							for(int cLen : getLens(cns, 2, chs, offsets[2], maxTailLen, 2)) {
								if(cLen > maxTailLen[2]) {
									continue;
								}
								tailLen[2] = cLen;
								idx = search(cns[2], chs, offsets[2], tailLen[2]);
								if(idx > -1 || tailLen[2]==0) {	//有chunk
									int sumChunkLen = 0;
									for(int i=0; i<3; i++) {
										sumChunkLen += tailLen[i]+1;
									}
									Chunk ck = null;
									if(sumChunkLen >= maxLen) {
										maxLen = sumChunkLen;	//下一个chunk块的开始位置增量
										ck = createChunk(sen, chs, tailLen, offsets, cns);
										mmr.addChunk(ck);
										
									}
									if(showChunk) {
										if(ck == null) {
											ck = createChunk(sen, chs, tailLen, offsets, cns);
											mmr.addChunk(ck);
										}
										System.out.println(ck);
									}
								}
							}
						}
					}
				}
			}
			sen.addOffset(maxLen);	//maxLen个字符已经处理完
			List<Chunk> chunks = mmr.remainChunks();
			for(Rule rule : otherRules) {	//其它规则过虑
				if(showChunk) {
					System.out.println("-------filter before "+rule+"----------");
					printChunk(chunks);
				}
				if(chunks.size() > 1) {
					rule.reset();
					rule.addChunks(chunks);
					chunks = rule.remainChunks();
				} else {
					break;
				}
			}
			if(showChunk) {
				System.out.println("-------remainChunks----------");
				printChunk(chunks);
			}
			if(chunks.size() > 0) {
				return chunks.get(0);
			}
		}
		return null;
	}

	private Chunk createChunk(Sentence sen, char[] chs, int[] tailLen, int[] offsets, CharNode[] cns) {
		Chunk ck = new Chunk();
		
		for(int i=0; i<3; i++) {

			if(offsets[i] < chs.length) {
				ck.words[i] = new Word(chs, sen.getStartOffset(), offsets[i], tailLen[i]+1);
				if(tailLen[i] == 0) {	//单字的要取得"字频计算出自由度"
					CharNode cn = cns[i];	//dic.head(chs[offsets[i]]);
					if(cn !=null) {
						ck.words[i].degree = cn.getFreq();
					}
				}
			}
		}
		return ck;
	}
	
	public static boolean isShowChunk() {
		return showChunk;
	}

	public static void setShowChunk(boolean showChunk) {
		ComplexSeg.showChunk = showChunk;
	}
}

package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;

import com.chenlb.mmseg4j.Chunk.Word;

/**
 * 最多分词. 在ComplexSeg基础上把长的词拆.
 * 
 * @author chenlb 2009-4-6 下午08:12:11
 */
public class MaxWordSeg extends ComplexSeg {
	
	public MaxWordSeg(Dictionary dic) {
		super(dic);
	}

	@Override
	public Chunk seg(Sentence sen) {
		
		Chunk chunk = super.seg(sen);
		if(chunk != null) {
			List<Word> cks = new ArrayList<Word>();
			for(int i=0; i<chunk.getCount(); i++) {
				Word word = chunk.words[i];

				if(word.getLength() < 3) {
					cks.add(word);
				}/* else if(word.getLength() == 3) {
					char[] chs = word.word;
					char[][] subW = new char[2][];
					int idx = search(chs, 0, 1, subW, 0);
					if(idx > -1) {
						cks.add(new Word(subW[0], word.startOffset));
						idx = search(chs, 1, 1, subW, 1);
						if(idx > -1) {
							cks.add(new Word(subW[1], word.startOffset+1));
						} else {	//后面的不是词, 把本身也加入.
							cks.add(word);
						}
					} else {
						cks.add(word);
					}
				}*/ else {
					char[] chs = word.word;
					char[][] subW = new char[chs.length][];
					int offset = 0, n = 0;
					int end = -1;	//上一次找到的位置
					for(; offset<chs.length-1; offset++) {
						int idx = search(chs, offset, 1, subW, n);
						if(idx > -1) {
							cks.add(new Word(subW[n], word.startOffset+offset));
							end = offset+2;
							n++;
						} else if(offset >= end) {	//有单字
							cks.add(new Word(new char[] {chs[offset]}, word.startOffset+offset));
							end = offset+1;
							
						}
					}
					if(end > -1 && end < chs.length) {
						cks.add(new Word(new char[] {chs[offset]}, word.startOffset+offset));
					}
					//cks.add(word);
				}

			}
			chunk.words = cks.toArray(new Word[cks.size()]);
			chunk.count = cks.size();
		}
		
		return chunk;
	}

}

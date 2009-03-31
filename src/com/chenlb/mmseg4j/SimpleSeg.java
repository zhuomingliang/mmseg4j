package com.chenlb.mmseg4j;

/**
 * 正向最大匹配的分词方式.
 * 
 * @author chenlb 2009-3-16 下午09:07:36
 */
public class SimpleSeg extends Seg{
	
	public SimpleSeg(Dictionary dic) {
		super(dic);
	}

	public Chunk seg(Sentence sen) {
		Chunk chunk = new Chunk();
		char[] chs = sen.getText();
		for(int k=0; k<3&&!sen.isFinish(); k++) {
			int offset = sen.getOffset();
			int maxLen = 0;
			int[] maxAvailableLen = {0};
			CharNode[] cns = new CharNode[1];
			char[][] cks = new char[1][];
			for(int len : getLens(cns, 0, chs, offset, maxAvailableLen, 0)) {
				if(len > maxAvailableLen[0]) {	//len不合格
					continue;
				}
				int idx = search(cns[0], chs, offset, len, cks, 0);
				if(idx > -1) {
					maxLen = len;
					break;
				}
				
			}

			//len == 0 说明没找到, 但还要单个输出
			/*char[] ck = new char[maxLen+1];
			System.arraycopy(chs, offset, ck, 0, maxLen+1);*/
			chunk.words[k] = cks[0];	//ck;
			if(k==0) {	//第一个词
				chunk.setStartOffset(sen.getStartOffset()+offset);
			}
			
			offset += maxLen + 1;
			sen.setOffset(offset);
		}
		
		return chunk;
	}
}

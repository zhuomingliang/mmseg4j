package com.chenlb.mmseg4j;

/**
 * 
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
			CharNode cn = dic.head(chs[offset]);
			int len = 0;
			if(cn != null) {
				for(int i=Math.min(cn.getMaxLen(),chs.length-offset-1); i>0; i--) {
					char[] subChs = new char[i];
					System.arraycopy(chs, offset+1, subChs, 0, i);
					int idx = dic.search(cn, subChs);
					if(idx > -1) {	//找到
						len = i;
						break;
					}
				}

				
				
			}
			//len == 0 说明没找到, 但还要单个输出
			char[] ck = new char[len+1];
			System.arraycopy(chs, offset, ck, 0, len+1);
			chunk.words[k] = ck;
			if(k==0) {
				chunk.setStartOffset(sen.getStartOffset()+offset);
			}
			
			offset += len + 1;
			sen.setOffset(offset);
		}
		
		return chunk;
	}
}

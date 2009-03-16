package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;

public class MMSegTokenizer extends Tokenizer {

	private MMSeg mmSeg;

	public MMSegTokenizer(Seg seg, Reader input) {
		super(input);
		mmSeg = new MMSeg(this.input, seg);
	}

	private Chunk chunk = null;
	private int wordIdx = 0;
	private int startOffset = 0;
	
	public Token next(Token reusableToken) throws IOException {
		Token token = null;
		if(chunk == null) {
			chunk = mmSeg.next();
			wordIdx = 0;
			
			if(chunk != null) {
				startOffset = chunk.getStartOffset();
			} else {
				return null;
			}
		}
		
		if(wordIdx < chunk.getCount()) {
			char[] word = chunk.getWords()[wordIdx++];
			
			token = reusableToken.reinit(word, 0, word.length, startOffset, startOffset+word.length);
			startOffset += word.length;
		}
		
		if(wordIdx >= chunk.getCount()) {
			chunk = null;
		}
		
		return token;
	}


}

package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Chunk.Word;

public class MMSegTokenizer extends Tokenizer {

	private MMSeg mmSeg;
	
/*	public MMSegTokenizer(Reader input) {
		super(input);
		init(new ComplexSeg(new Dictionary()));
	}

	public MMSegTokenizer(Reader input, Dictionary dic) {
		super(input);
		init(new ComplexSeg(dic));
	}*/
	
	public MMSegTokenizer(Seg seg, Reader input) {
		super(input);
		init(seg);
	}

	private void init(Seg seg) {
		mmSeg = new MMSeg(input, seg);
	}
	
	private Chunk chunk = null;
	private int wordIdx = 0;
	//private int startOffset = 0;
	
	public void reset(Reader input) throws IOException {
		super.reset(input);
		mmSeg.reset(this.input);
		chunk = null;
		wordIdx = 0;
		//startOffset = 0;
	}

	public Token next(Token reusableToken) throws IOException {
		Token token = null;
		if(chunk == null) {
			chunk = mmSeg.next();
			wordIdx = 0;
			
			if(chunk != null) {
				//startOffset = chunk.getStartOffset();
			} else {
				return null;
			}
		}
		
		if(wordIdx < chunk.getCount()) {
			Word word = chunk.getWords()[wordIdx++];
			//if(word != null) {
				token = reusableToken.reinit(word.getSen(), 0, word.getLength(), word.getStartOffset(), word.getEndOffset());
				//chunk = null;
				//break;
			//}
		}
		
		if(wordIdx >= chunk.getCount()) {
			chunk = null;
		}
		
		return token;
	}


}

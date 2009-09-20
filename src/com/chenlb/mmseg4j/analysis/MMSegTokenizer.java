package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

public class MMSegTokenizer extends Tokenizer {

	private MMSeg mmSeg;
	
	public MMSegTokenizer(Seg seg, Reader input) {
		super(input);
		mmSeg = new MMSeg(input, seg);
	}
	
	public void reset(Reader input) throws IOException {
		super.reset(input);
		mmSeg.reset(input);
	}

	public Token next(Token reusableToken) throws IOException {
		Token token = null;
		Word word = mmSeg.next();
		if(word != null) {
			token = reusableToken.reinit(word.getSen(), word.getWordOffset(), word.getLength(), word.getStartOffset(), word.getEndOffset(), word.getType());
		}
		
		return token;
	}

}

package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Simple;

public class SimpleTokenizer extends Tokenizer {

	private Simple simple;
	public SimpleTokenizer(Reader input) {
		this(new Dictionary(), input);
	}

	public SimpleTokenizer(Dictionary dic, Reader input) {
		super(input);
		simple = new Simple(dic, this.input);
	}

	private StringBuilder sb = new StringBuilder(16);
	public Token next(Token reusableToken) throws IOException {
		int word = simple.next(sb);
		if(word < 0) {
			return null;
		}
		reusableToken.reinit(sb.toString(), word, word+sb.length());
		return reusableToken;
	}


}

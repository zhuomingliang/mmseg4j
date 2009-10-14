package com.chenlb.mmseg4j.analysis;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.Word;

/**
 * 切分“字母和数”混在一起的过虑器。比如：mb991ch 切为 "mb 991 ch"
 * 
 * @author chenlb 2009-10-14 下午04:03:18
 */
public class CutLetterDigitFilter extends TokenFilter {

	protected Queue<Token> tokenQueue = new LinkedList<Token>();
	
	protected CutLetterDigitFilter(TokenStream input) {
		super(input);
	}

	public Token next(Token reusableToken) throws IOException {
		assert reusableToken != null;
		
		//先使用上次留下来的。
		Token nextToken = tokenQueue.poll();
		if(nextToken != null) {
			return nextToken;
		}
		
		nextToken = input.next(reusableToken);
		
		if(nextToken != null && 
				(Word.TYPE_LETTER_OR_DIGIT.equalsIgnoreCase(nextToken.type())
					|| Word.TYPE_DIGIT_OR_LETTER.equalsIgnoreCase(nextToken.type()))
				) {
			final char[] buffer = nextToken.termBuffer();
			final int length = nextToken.termLength();
			byte lastType = (byte) Character.getType(buffer[0]);	//与上次的字符是否同类
			int termBufferOffset = 0;
			int termBufferLength = 0;
			for(int i=0;i<length;i++) {
				byte type = (byte) Character.getType(buffer[i]);
				if(type <= Character.MODIFIER_LETTER) {
					type = Character.LOWERCASE_LETTER;
				}
				if(type != lastType) {	//与上一次的不同
					addToken(nextToken, termBufferOffset, termBufferLength, lastType);
					
					termBufferOffset += termBufferLength;
					termBufferLength = 0;
					
					lastType = type;
				}
				
				termBufferLength++;
			}
			if(termBufferLength > 0) {	//最后一次
				addToken(nextToken, termBufferOffset, termBufferLength, lastType);
			}
			nextToken = tokenQueue.poll();
		}
		
		return nextToken;
	}
	
	private void addToken(Token oriToken, int termBufferOffset, int termBufferLength, byte type) {
		Token token = new Token(oriToken.termBuffer(), termBufferOffset, termBufferLength, 
				oriToken.startOffset()+termBufferOffset, oriToken.startOffset()+termBufferOffset+termBufferLength);
		
		if(type == Character.DECIMAL_DIGIT_NUMBER) {
			token.setType(Word.TYPE_DIGIT);
		} else {
			token.setType(Word.TYPE_LETTER);
		}
		
		tokenQueue.offer(token);
	}

	public void close() throws IOException {
		super.close();
		tokenQueue.clear();
	}

	public void reset() throws IOException {
		super.reset();
		tokenQueue.clear();
	}
	
	
}

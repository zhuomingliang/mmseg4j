package com.chenlb.mmseg4j.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * lucene 3.0 从 TokenStream 得到 Token 比较麻烦。
 * 
 * @author chenlb 2010-10-7下午10:07:10
 */
public class TokenUtils {

	/**
	 * @param input
	 * @param reusableToken is null well new one auto.
	 * @return null - if not next token or input is null.
	 * @throws IOException
	 */
	public static Token nextToken(TokenStream input, Token reusableToken) throws IOException {
		if(input == null) {
			return null;
		}
		if(!input.incrementToken()) {
			return null;
		}
		
		TermAttribute termAtt = (TermAttribute)input.getAttribute(TermAttribute.class);
		OffsetAttribute offsetAtt = (OffsetAttribute)input.getAttribute(OffsetAttribute.class);
		TypeAttribute typeAtt = (TypeAttribute)input.getAttribute(TypeAttribute.class);
		
		if(reusableToken == null) {
			reusableToken = new Token();
		}
		
		reusableToken.clear();
		if(termAtt != null) {
			reusableToken.setTermBuffer(termAtt.termBuffer(), 0, termAtt.termLength());
		}
		if(offsetAtt != null) {
			reusableToken.setStartOffset(offsetAtt.startOffset());
			reusableToken.setEndOffset(offsetAtt.endOffset());
		}
		
		if(typeAtt != null) {
			reusableToken.setType(typeAtt.type());
		}
		
		return reusableToken;
	}
}

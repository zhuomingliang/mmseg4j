package com.chenlb.mmseg4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MMSeg {
	
	private Dictionary dic;
	private Reader reader;
	
	private StringBuilder bufSentence = new StringBuilder(256);
	private Sentence currentSentence;
	
	public MMSeg(Dictionary dic, Reader reader) {
		this.dic = dic;
		this.reader = new BufferedReader(reader, 2048);
	}

	private int readedIdx = -1;
	private int lastData = -1;
	private int readNext() throws IOException {
		int data = -1;
		if(lastData >=0) {
			data = lastData;
			lastData = -1;
		} else {
			readedIdx++;
			data = reader.read();
		}
		return data;
	}
	
	public int next(StringBuilder sb) throws IOException {
		sb.setLength(0);
		int startIdx = -1;
		//String word = null;
		if(currentSentence == null) {
			bufSentence.setLength(0);
			int data = -1;
			int lastType = -1;
			boolean read = true;
			boolean returnWord = false;
			while(read && (data=readNext()) != -1) {
				int type = Character.getType(data);
				switch(type) {
				case Character.UPPERCASE_LETTER:
				case Character.LOWERCASE_LETTER:
				case Character.TITLECASE_LETTER:
				case Character.MODIFIER_LETTER:
					if(lastType < 0 || isEnglishLetter(lastType)) {
						bufSentence.appendCodePoint(data);
						returnWord = true;
					} else {
						lastData = data;	//下次再用
						read = false;
					}
					lastType = type;
					break;
				case Character.OTHER_LETTER:
					if(lastType < 0 || isCJK(lastType)) {
						bufSentence.appendCodePoint(data);
						returnWord = false;
					} else {
						lastData = data;
						read = false;
					}
					lastType = type;
					break;
				case Character.DECIMAL_DIGIT_NUMBER:
					if(lastType < 0 || isDigit(lastType)) {
						bufSentence.appendCodePoint(data);
						returnWord = true;
					} else {
						lastData = data;
						read = false;
					}
					lastType = type;
					break;
				default :
					if(lastType >=0) {
						read = false;
					}
					lastType = -1;
				}
				
			}
			
			if(bufSentence.length() > 0) {
				startIdx = readedIdx - bufSentence.length();
				if(returnWord) {
					//word = bufChunk.toString();
					sb.append(bufSentence);
				} else {
					char[] chs = new char[bufSentence.length()];
					bufSentence.getChars(0, bufSentence.length(), chs, 0);
					currentSentence = new Sentence(chs, startIdx);
				}
				
			}/* else if(bufChunk.length() == 1) {
				return currentChunk.toString();
			}*/
		}
		
		if(currentSentence != null) {
			char[] chs = currentSentence.getText();
			int offset = currentSentence.getOffset();
			CharNode cn = dic.head(chs[offset]);
			if(cn != null && offset < chs.length) {
				int len = 0;
				for(int i=Math.min(cn.getMaxLen(),chs.length-offset-1); i>0; i--) {
					char[] subChs = new char[i];
					System.arraycopy(chs, offset+1, subChs, 0, i);
					int idx = dic.search(cn, subChs);
					if(idx > -1) {	//找到
						len = i;
						break;
					}
				}
				startIdx = readedIdx - chs.length + offset;
				//word = new String(chs, offset, len+1);
				sb.append(chs, offset, len+1);
				offset += len + 1;
			}
			currentSentence.setOffset(offset);
			if(offset >= chs.length) {
				currentSentence = null;
			}
		}
		
		return startIdx;
	}
	
	private boolean isCJK(int type) {
		return type == Character.OTHER_LETTER;
	}
	private boolean isDigit(int type) {
		return type == Character.DECIMAL_DIGIT_NUMBER;
	}
	private boolean isEnglishLetter(int type) {
		return type <= Character.MODIFIER_LETTER && type >= Character.UPPERCASE_LETTER;
	}
}

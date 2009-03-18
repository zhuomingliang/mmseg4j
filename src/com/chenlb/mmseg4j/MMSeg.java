package com.chenlb.mmseg4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MMSeg {
	
	private Reader reader;
	private Seg seg;
	
	private StringBuilder bufSentence = new StringBuilder(256);
	private Sentence currentSentence;
	
	public MMSeg(Reader reader, Seg seg) {
		this.reader = new BufferedReader(reader, 2048);
		this.seg = seg;
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
	
	public Chunk next() throws IOException {

		Chunk chunk = null;
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
						bufSentence.appendCodePoint(chineseNumberToDigit(data));
						returnWord = true;
					} else {
						lastData = data;
						read = false;
					}
					lastType = type;
					break;
				case Character.LETTER_NUMBER:
				case Character.OTHER_NUMBER:
					if(lastType < 0) {
						bufSentence.appendCodePoint(data);
						returnWord = true;
					}
					
					lastData = -1;
					read = false;
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
				int startIdx = readedIdx - bufSentence.length();
				if(returnWord) {
					chunk = new Chunk();
					chunk.words[0] = new char[bufSentence.length()];
					bufSentence.getChars(0, bufSentence.length(), chunk.words[0], 0);
					chunk.setStartOffset(startIdx);
					
					//sb.append(bufSentence);
					return chunk;
				} else {
					char[] chs = new char[bufSentence.length()];
					bufSentence.getChars(0, bufSentence.length(), chs, 0);
					currentSentence = new Sentence(chs, startIdx);
				}
			}
		}
		
		if(currentSentence != null) {
			chunk = seg.seg(currentSentence);
			if(currentSentence.isFinish()) {
				currentSentence = null;
			}
		}
		
		return chunk;
	}
	
	private int chineseNumberToDigit(int codePoint) {
		if(codePoint>=65296 && codePoint<=65305) {	//０-９
			codePoint -= 65248;
		}
		return codePoint;
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

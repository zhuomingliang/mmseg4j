package com.chenlb.mmseg4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.chenlb.mmseg4j.Chunk.Word;

public class MMSeg {
	
	private Reader reader;
	private Seg seg;
	
	private StringBuilder bufSentence = new StringBuilder(256);
	private Sentence currentSentence;
	
	public MMSeg(Reader input, Seg seg) {
		this.reader = new BufferedReader(input);
		this.seg = seg;
	}

	private int readedIdx = -1;
	private int lastData = -1;
	private int nextData = -1;
	
	public void reset(Reader input) {
		this.reader = input;
		currentSentence = null;
		bufSentence.setLength(0);
		readedIdx = -1;
		lastData = -1;
		nextData = -1;
	}
	
	private int readNext() throws IOException {
		int data = -1;
		if(nextData >=0) {
			data = nextData;
			nextData = -1;
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
			boolean alsoReturnNextData = false;
			while(read && (data=readNext()) != -1) {
				int type = Character.getType(data);
				switch(type) {
				case Character.UPPERCASE_LETTER:
				case Character.LOWERCASE_LETTER:
				case Character.TITLECASE_LETTER:
				case Character.MODIFIER_LETTER:
					/*
					 * 1. 0x410-0x44f -> А-я	//俄文
					 * 2. 0x391-0x3a9 -> Α-Ω	//希腊大写
					 * 3. 0x3b1-0x3c9 -> α-ω	//希腊小写
					 */
					if(lastType < 0 || isLetter(lastType)) {
						data = toAscii(data);
						boolean available = true;
						NationLetter nl = getNation(data);
						switch(nl) {
						case EN:
							read = isAsciiLetter(toAscii(lastData));
							break;
						case RA:
							read = isRussiaLetter(lastData);
							break;
						case GE:
							read = isGreeceLetter(lastData);
							break;
						default :
							read = false;
							available = false;	//
							type = -1;
						}
						read = lastType < 0 || read;
						returnWord = true;
						if(!read) {
							if(available) {
								nextData = data;
							}
							type = -1;	//单字处理
						} else if(available) {
							bufSentence.appendCodePoint(data);
						}
					} else {
						nextData = data;	//下次再用
						read = false;
					}
					
					lastType = type;
					break;
				case Character.OTHER_LETTER:
					/*
					 * 1. 0x3041-0x30f6 -> ぁ-ヶ	//日文(平|片)假名
					 * 2. 0x3105-0x3129 -> ㄅ-ㄩ	//注意符号
					 */
					if(lastType < 0 || isCJK(lastType)) {
						bufSentence.appendCodePoint(data);
						returnWord = false;
					} else {
						if(isDigit(lastType) && seg.isUnit(data)) {
							alsoReturnNextData = true;
						}
						nextData = data;
						read = false;
					}
					lastType = type;
					break;
				case Character.DECIMAL_DIGIT_NUMBER:
					if(lastType < 0 || isDigit(lastType)) {
						bufSentence.appendCodePoint(toAscii(data));
						returnWord = true;
					} else {
						nextData = data;
						read = false;
					}
					lastType = type;
					break;
				case Character.LETTER_NUMBER:
					// ⅠⅡⅢ 单分
					if(lastType < 0) {
						bufSentence.appendCodePoint(data);
						returnWord = true;
					} else {
						if(lastType != Character.LETTER_NUMBER) {//处理上次积累的, 当前的下一次再处理
							nextData = data;
						}
					}
					read = false;
					lastType = -1;
					break;
				case Character.OTHER_NUMBER:
					//①⑩㈠㈩⒈⒑⒒⒛⑴⑽⑾⒇ 连着用
					if(lastType < 0 || lastType == Character.OTHER_NUMBER) {
						bufSentence.appendCodePoint(data);
						returnWord = true;
					} else {
						nextData = data;
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
				lastData = data;
			}
			
			if(bufSentence.length() > 0) {
				int startIdx = readedIdx - bufSentence.length();
				if(returnWord) {
					chunk = new Chunk();
					char[] word = new char[bufSentence.length()];
					bufSentence.getChars(0, bufSentence.length(), word, 0);
					chunk.words[0] = new Word(word, startIdx);
					//chunk.setStartOffset(startIdx);
					if(alsoReturnNextData && nextData > 0) {	//目前只是 年月日
						char[] w = new char[1];
						w[0] = (char) nextData;
						chunk.words[1] = new Word(w, startIdx+word.length);
						nextData = -1;
					}
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
	
	
	/**
	 * 双角转单角
	 */
	private int toAscii(int codePoint) {
		if((codePoint>=65296 && codePoint<=65305)	//０-９
				|| (codePoint>=65313 && codePoint<=65338)	//Ａ-Ｚ
				|| (codePoint>=65345 && codePoint<=65370)	//ａ-ｚ
				) {	
			codePoint -= 65248;
		}
		return codePoint;
	}
	
	private boolean isAsciiLetter(int codePoint) {
		return (codePoint >= 'A' && codePoint <= 'Z') || (codePoint >= 'a' && codePoint <= 'z');
	}
	
	private boolean isRussiaLetter(int codePoint) {
		return (codePoint >= 'А' && codePoint <= 'я') || codePoint=='Ё' || codePoint=='ё';
	}
	
	private boolean isGreeceLetter(int codePoint) {
		return (codePoint >= 'Α' && codePoint <= 'Ω') || (codePoint >= 'α' && codePoint <= 'ω');
	}
	/**
	 * EN -> 英语
	 * RA -> 俄语
	 * GE -> 希腊
	 * 
	 */
	private static enum NationLetter {EN, RA, GE, UNKNOW};
	
	private NationLetter getNation(int codePoint) {
		if(isAsciiLetter(codePoint)) {
			return NationLetter.EN;
		}
		if(isRussiaLetter(codePoint)) {
			return NationLetter.RA;
		}
		if(isGreeceLetter(codePoint)) {
			return NationLetter.GE;
		}
		return NationLetter.UNKNOW;
	}
	
	private boolean isCJK(int type) {
		return type == Character.OTHER_LETTER;
	}
	private boolean isDigit(int type) {
		return type == Character.DECIMAL_DIGIT_NUMBER;
	}
	private boolean isLetter(int type) {
		return type <= Character.MODIFIER_LETTER && type >= Character.UPPERCASE_LETTER;
	}
}

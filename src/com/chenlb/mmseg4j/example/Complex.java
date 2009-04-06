package com.chenlb.mmseg4j.example;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Chunk.Word;

public class Complex {

	protected Dictionary dic;
	
	public Complex() {
		dic = new Dictionary();
	}

	public Complex(Dictionary dic) {
		this.dic = dic;
	}

	protected Seg getSeg() {
		return new ComplexSeg(dic);
	}
	
	public String segWords(Reader input, String wordSpilt) throws IOException {
		StringBuilder sb = new StringBuilder();
		Seg seg = getSeg();
		MMSeg mmSeg = new MMSeg(input, seg);
		Chunk chunk = null;
		boolean first = true;
		while((chunk=mmSeg.next())!=null) {
			for(int i=0; i<chunk.getCount(); i++) {
				Word word = chunk.getWords()[i];
				if(!first) {
					sb.append(wordSpilt);
				}
				String w = new String(word.getWord());
				sb.append(w);
				first = false;
			}
		}
		return sb.toString();
	}
	
	public String segWords(String txt, String wordSpilt) throws IOException {
		return segWords(new StringReader(txt), wordSpilt);
	}
	
	public static void main(String[] args) throws IOException {
		String txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		
		if(args.length > 0) {
			txt = args[0];
		}
		
		//txt = "today,…………i'am chenlb,<《公主小妹》>?我@$#%&*()$!!,";
		Complex complex = new Complex();
		System.out.println(complex.segWords(txt, " | "));
	}

}

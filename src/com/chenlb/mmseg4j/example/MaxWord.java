package com.chenlb.mmseg4j.example;

import java.io.IOException;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;

public class MaxWord extends Complex {

	public MaxWord() {
		super();
	}

	public MaxWord(Dictionary dic) {
		super(dic);
	}

	@Override
	protected Seg getSeg() {

		return new MaxWordSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		String txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		
		if(args.length > 0) {
			txt = args[0];
		}
		
		//txt = "today,…………i'am chenlb,<《公主小妹》>?我@$#%&*()$!!,";
		MaxWord maxWord = new MaxWord();
		System.out.println(maxWord.segWords(txt, " | "));
	}
}

package com.chenlb.mmseg4j.example;

import java.io.IOException;
import java.io.StringReader;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;

public class Complex {

	public static void main(String[] args) throws IOException {
		String txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		
		if(args.length > 0) {
			txt = args[0];
		}
		
		//txt = "today,…………i'am chenlb,<《公主小妹》>?我@$#%&*()$!!,";
		
		Dictionary dic = new Dictionary();
		Seg seg = null;
		seg = new SimpleSeg(dic);
		MMSeg mmSeg = new MMSeg(new StringReader(txt), seg);
		Chunk chunk = null;
		System.out.println();
		while((chunk=mmSeg.next())!=null) {
			int offset = chunk.getStartOffset();
			for(char[] word : chunk.getWords()) {
				if(word != null) {
					System.out.print(new String(word)+" | ");
					offset += word.length;
					//System.out.println(", "+offset);
				}
			}
		}
	}

}

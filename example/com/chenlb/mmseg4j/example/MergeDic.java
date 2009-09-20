package com.chenlb.mmseg4j.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Dictionary.FileLoading;
import com.chenlb.mmseg4j.example.DicTransform.WriterRow;

public class MergeDic {

	/**
	 * 尝试合并 sogou（去除了没有词性的） 与 rmmseg 的词库。
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("dic/word-with-attr.dic"));
		final Set<String> words = new TreeSet<String>();
		final int[] num = {0};
		FileLoading fl = new FileLoading() {

			public void row(String line, int n) {
				words.add(line.trim());
				num[0]++;
			}
			
		};
		
		Dictionary.load(fis, fl);
		
		fis = new FileInputStream(new File("dic/words-rmmseg.dic"));
		
		Dictionary.load(fis, fl);
		
		WriterRow wr = new WriterRow(new File("dic/words-marge-sogou-no-attr-and-rmmseg.dic"));
		for(String word : words) {
			wr.writerRow(word);
		}
		wr.close();
		System.out.println("rows="+num[0]+", size="+words.size()+", same="+(num[0]-words.size()));
	}

}

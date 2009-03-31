package com.chenlb.mmseg4j.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class DicTransform {

	/**
	 * 词库转换
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String words = "sogou/SogouLabDic.dic";
		String charset = "GBK";
		if(args.length > 0) {
			words = args[0];
		}
		File file = new File(words);
		File path = file.getParentFile();
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path, "words.dic")), "UTF-8"));
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset));
		String line = null;
		int n = 0, ava = 0;
		long start = System.currentTimeMillis();
		while((line = br.readLine()) != null) {
			n++;
			String[] w = line.split("\\s+");
			if(w.length > 0 && !"".equals(w[0]) &&!w[0].startsWith("#")) {
				ava++;
				writer.append(w[0]).append("\r\n");
			}
		}
		writer.close();
		System.out.println("----line="+n+", available="+ava+", time="+(System.currentTimeMillis()-start)+"ms");
	}

}

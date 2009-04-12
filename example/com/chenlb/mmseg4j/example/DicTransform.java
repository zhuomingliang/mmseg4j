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

	public static interface Transform {
		String transform(String line);
	}
	
	public static class DeFreq implements Transform {

		public String transform(String line) {
			String[] w = line.split("\\s+");
			if(w.length > 0 && !"".equals(w[0])) {

				return w[0];
			}
			return null;
		}
		
	}
	
	public static class TwoOrThreeChar extends DeFreq {

		public String transform(String line) {
			String word = super.transform(line);
			if(word != null && word.length() < 4) {
				
				return word;
			}
			return null;
		}
		
	}
	
	public int transform(File src, String srcCharset, File dist, Transform tf) throws IOException {
		WriterRow wr = new WriterRow(dist);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(src)), srcCharset));
		String line = null;
		int n = 0, ava = 0;
		long start = System.currentTimeMillis();
		while((line = br.readLine()) != null) {
			n++;
			if(line == null || "".equals(line) || line.startsWith("#")) {
				continue;
			}
			String li = tf.transform(line);
			if(li != null) {
				ava++;
				wr.writerRow(li);
			}
		}
		wr.close();
		System.out.println("----line="+n+", available="+ava+", time="+(System.currentTimeMillis()-start)+"ms");
		return ava;
	}
	
	public static class WriterRow {
		Writer writer;

		public WriterRow(File dist) {
			try {
				writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(dist), "UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		public void writerRow(String line) {
			try {
				writer.append(line).append("\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void writerRow(char[] chs) {
			try {
				for(char ch : chs) {
					writer.append(ch);
				}
				if(chs.length > 0) {
					writer.append("\r\n");
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		public void close() {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
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
		//File path = file.getParentFile();
		//File dist = new File("dic/words.dic");
		File dist = new File("dic/two-three-words.dic");
		DicTransform dt = new DicTransform();
		//只要词,不频率
		//dt.transform(file, charset, dist, new DeFreq());
		//只要两或三个字的词.
		dt.transform(file, charset, dist, new TwoOrThreeChar());
		
	}

}

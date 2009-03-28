package com.chenlb.mmseg4j.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;

public class PerformanceAnalyzer {

	/**
	 * -Dmode=simple, default is complex
	 * @param args args[0] txt path
	 * @author chenlb 2009-3-28 下午02:19:52
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			System.out.println("Usage:");
			System.out.println("\t-Dmode=simple, defalut is complex");
			System.out.println("\tPerformance <txt path> - is a directory that contain *.txt");
			return;
		}
		String mode = System.getProperty("mode", "complex");
		MMSegAnalyzer analyzer = null;

		if("simple".equals(mode)) {
			analyzer = new SimpleAnalyzer();
		} else {
			analyzer = new ComplexAnalyzer();
		}
		File path = new File(args[0]);
		File[] txts = path.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
			
				return name.endsWith(".txt");
			}
			
		});
		long time = 0;
		for(File txt : txts) {
			
			TokenStream ts = analyzer.tokenStream("text", new InputStreamReader(new FileInputStream(txt), "GBK"));
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(txt.getAbsoluteFile()+"."+mode+".word")));
			BufferedWriter bw = new BufferedWriter(osw);
			long start = System.currentTimeMillis();
			for(Token t= new Token(); (t=ts.next(t)) !=null;) {
				bw.append(new String(t.term())).append("\r\n");
			}
			time += System.currentTimeMillis() - start;
			bw.close();
		}
		System.out.println("use "+time+"ms");
	}

}

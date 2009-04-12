package com.chenlb.mmseg4j.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

public class Effect {

	private File path;
	private Analyzer analyzer;
	
	public Effect(Analyzer analyzer) {
		super();
		path = new File("txt");
		this.analyzer = analyzer;
	}

	public Effect(File path, Analyzer analyzer) {
		this(analyzer);
		this.path = path;
	}

	public void run(String outputChipName, int n) throws IOException {
		File[] txts = path.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
			
				return name.endsWith(".txt");
			}
			
		});
		long time = 0, size = 0;
		for(int i=0; i<n; i++) {
			for(File txt : txts) {
				FileInputStream ftxt = new FileInputStream(txt);
				int s = ftxt.available();
				size += s;
				BufferedReader reader = new BufferedReader(new InputStreamReader(ftxt, "GBK"));
				
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(txt.getAbsoluteFile()+"."+outputChipName+".word")));
				BufferedWriter bw = new BufferedWriter(osw);
				
				String line = null;
				long start = System.currentTimeMillis();
				while((line = reader.readLine()) != null) {
					bw.append("--------------------------").append("\r\n");;
					bw.append(line).append("\r\n");
					TokenStream ts = analyzer.tokenStream("text", new StringReader(line));
					for(Token t= new Token(); (t=ts.next(t)) !=null;) {
						bw.append(new String(t.term())).append(" | ");
					}
					bw.append("\r\n");
				}
				
				long t = System.currentTimeMillis() - start;
				time += t;
				System.out.println("size="+(s/1024)+"kb, use "+t+"ms, speed="+speed(s, t)+"kb/s, file="+txt.getName());
				bw.close();
			}
		}
		System.out.println("size="+(size/1024)+"kb, use "+time+"ms, speed="+speed(size, time)+"kb/s");
	}
	
	/**
	 * -Dmode=simple, default is complex
	 * @param args args[0] txt path
	 * @author chenlb 2009-3-28 下午02:19:52
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int n = 1;
		if(args.length < 1) {
			usage();
			return;
		}
		Properties analyzers = new Properties();
		analyzers.load(new FileInputStream(new File("analyzer.properties")));
		String mode = System.getProperty("mode", "complex");
		String a = System.getProperty("analyzer", "mmseg4j");
		Analyzer analyzer = null;
		String an = (String) analyzers.get(a);
		if(an != null) {
			analyzer = (Analyzer)Class.forName(an).newInstance();
			mode = a;
		} else {
			usage();
			return;
		}
		if(args.length > 1) {
			try {
				n = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				
			}
		}
		File path = new File(args[0]);
		System.out.println("analyzer="+analyzer.getClass().getName());
		Effect ef = new Effect(path, analyzer);
		ef.run(mode, n);
	}

	private static void usage() {
		System.out.println("Usage:");
		System.out.println("\t-Dmode=simple, defalut is complex");
		System.out.println("\t-Danalyzer=paoding, defalut is mmseg4j");
		System.out.println("\tEffect <txt path> - is a directory that contain *.txt");
	}
	
	private static double speed(long size, long time) {
		if(time == 0) {
			time = 1;
		}
		return (size*1000)/(time*1024);
	}

}

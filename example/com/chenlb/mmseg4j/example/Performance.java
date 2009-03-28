package com.chenlb.mmseg4j.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.chenlb.mmseg4j.Chunk;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;

public class Performance {

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
		Seg seg = null;
		Dictionary dic = new Dictionary();
		if("simple".equals(mode)) {
			seg = new SimpleSeg(dic);
		} else {
			seg = new ComplexSeg(dic);
		}
		File path = new File(args[0]);
		File[] txts = path.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
			
				return name.endsWith(".txt");
			}
			
		});
		long time = 0;
		for(File txt : txts) {
			MMSeg mmSeg = new MMSeg(new InputStreamReader(new FileInputStream(txt), "GBK"), seg);
			Chunk chunk = null;
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(txt.getAbsoluteFile()+"."+mode+".word")));
			BufferedWriter bw = new BufferedWriter(osw);
			long start = System.currentTimeMillis();
			while((chunk=mmSeg.next())!=null) {
				int offset = chunk.getStartOffset();
				for(char[] word : chunk.getWords()) {
					if(word != null) {
						bw.append(new String(word)).append("\r\n");
						offset += word.length;
						
					}
				}
			}
			time += System.currentTimeMillis() - start;
			bw.close();
		}
		System.out.println("use "+time+"ms");
	}

}

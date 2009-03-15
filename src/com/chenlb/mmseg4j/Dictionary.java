package com.chenlb.mmseg4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 词典类.<br/>
 * 保存单字与其频率,还有词库.
 * 
 * @author chenlb 2009-2-20 下午11:34:29
 */
public class Dictionary {

	private static Map<Character, CharNode> dict = new HashMap<Character, CharNode>();
	private static boolean isLoad = false;
	/**
	 * 默认从data/chars.dic,data/words.dic加载.
	 */
	public Dictionary() {
		this("data/chars.dic", "data/words.dic");
	}
	
	/**
	 * 词是一行一个.
	 * @param charsFile 单字文件,还带频率.
	 * @param wordsFile 词库文件
	 */
	public Dictionary(String charsFile, String wordsFile) {
		try {
			if(!isLoad) {
				init(charsFile, wordsFile);
				isLoad = true;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
	
	private void init(String charsFile, String wordsFile) throws IOException {
		int lineNum = 0;
		long s = now();
		lineNum = load(charsFile, new FileLoading() {	//单个字的

			public void row(String line, int n) {
				if(line == null || line.startsWith("#")) {
					return;
				}
				String[] w = line.split(" ");
				CharNode cn = new CharNode();
				switch(w.length) {
				case 2:
					try {
						cn.setFreq(Integer.parseInt(w[1]));//字频
					} catch(NumberFormatException e) {
						//eat...
					}
				case 1:
					
					dict.put(w[0].charAt(0), cn);
				}
			}
		});
		System.out.println("chars loaded time="+(now()-s)+", line="+lineNum);

		s = now();
		lineNum = load(wordsFile, new FileLoading() {//正常的词库

			public void row(String line, int n) {
				if(line == null || line.startsWith("#")) {
					return;
				}
				CharNode cn = dict.get(line.charAt(0));
				if(cn == null) {
					cn = new CharNode();
					dict.put(line.charAt(0), cn);
				}
				cn.addWordTail(tail(line));
			}
			
		});
		System.out.println("words loaded time="+(now()-s)+", line="+lineNum);
		
		//sort
		s = now();
		for(Entry<Character, CharNode> subSet : dict.entrySet()) {
			subSet.getValue().sort();
		}
		System.out.println("sort time="+(now()-s));
	}
	
	/**
	 * 加载词文件的模板
	 * @return 文件总行数
	 */
	private int load(String file, FileLoading loading) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(new File(file))), "UTF-8"));
		String line = null;
		int n = 0;
		while((line = br.readLine()) != null) {
			n++;
			loading.row(line, n);
		}
		return n;
	}
	
	/**
	 * 取得 str 除去第一个char的部分
	 * @author chenlb 2009-3-3 下午10:05:26
	 */
	private char[] tail(String str) {
		char[] cs = new char[str.length()-1];
		str.getChars(1, str.length(), cs, 0);
		return cs;
	}
	
	private static interface FileLoading {
		/**
		 * @param line 读出的一行
		 * @param n 当前第几行
		 * @author chenlb 2009-3-3 下午09:55:54
		 */
		void row(String line, int n);
	}
	
	/**
	 * word 能否在词库里找到
	 * @author chenlb 2009-3-3 下午11:10:45
	 */
	public boolean match(String word) {
		if(word == null || word.length() < 2) {
			return false;
		}
		CharNode cn = dict.get(word.charAt(0));
		return search(cn, tail(word)) >= 0;
	}
	
	public CharNode head(char ch) {
		return dict.get(ch);
	}
	
	public int search(CharNode node, char[] tail) {
		if(node != null) {
			return node.indexOf(tail);
		}
		return -1;
	}
}

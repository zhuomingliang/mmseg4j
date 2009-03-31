package com.chenlb.mmseg4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 词典类.<br/>
 * 保存单字与其频率,还有词库.
 * 
 * @author chenlb 2009-2-20 下午11:34:29
 */
public class Dictionary {

	static final Logger log = Logger.getLogger(Dictionary.class.getName());
	
	private Map<Character, CharNode> dict;// = new HashMap<Character, CharNode>();
	
	private static final DicKey defaultDicKey = new DicKey("", "");
	private static final Map<DicKey, Map<Character, CharNode>> dics = new ConcurrentHashMap<DicKey, Map<Character, CharNode>>();
	
	/**
	 * 加载chars.dic,words.dic文件.<p/>
	 * 查找目录顺序:
	 * <ol>
	 * <li>从mmseg.dic.path指定的目录中加载</li>
	 * <li>从user.dir/data目录</li>
	 * </ol>
	 */
	public Dictionary() {
		Map<Character, CharNode> dic = dics.get(defaultDicKey);
		if(dic == null) {
			String defPath = System.getProperty("mmseg.dic.path");
			log.info("look up in mmseg.dic.path="+defPath);
			if(defPath == null) {
				defPath = System.getProperty("user.dir")+"/data";
				log.info("look up in user.dir="+defPath);
				
			}
			
			File path = new File(defPath);
			//if(!path.exists()) {
			//	defPath = Dictionary.class.getResource("/data").getFile();
			//	log.info("look up in Dictionary.class '/data' path="+defPath);
				path = new File(defPath);
			//}
			init(path);
			
			dics.put(defaultDicKey, dict);
		} else {
			dict = dic;
		}
	}
	
	/**
	 * @param path 词典的目录
	 */
	public Dictionary(String path) {
		this(new File(path));
	}
	
	/**
	 * 词典的目录 
	 */
	public Dictionary(File path) {
		init(path);
	}
	
	private void init(File path) {
		try {
			File charsFile = new File(path, "chars.dic");
			File wordsFile = new File(path, "words.dic");
			DicKey dk = new DicKey(charsFile.getAbsolutePath(), wordsFile.getAbsolutePath());
			Map<Character, CharNode> dic = dics.get(dk);
			if(dic == null) {
				dic = init(charsFile, wordsFile);
				dics.put(dk, dic);
			}
			dict = dic;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
	
	private Map<Character, CharNode> init(File charsFile, File wordsFile) throws IOException {
		InputStream charsIn = null;
		if(charsFile.exists()) {
			charsIn = new FileInputStream(charsFile);
		} else {	//从 jar 里加载
			charsIn = this.getClass().getResourceAsStream("/data/chars.dic");
			charsFile = new File(this.getClass().getResource("/data/chars.dic").getFile());	//only for log
		}
		final Map<Character, CharNode> dic = new HashMap<Character, CharNode>();
		int lineNum = 0;
		long s = now();
		long ss = s;
		lineNum = load(charsIn, new FileLoading() {	//单个字的

			public void row(String line, int n) {
				if(line == null || line.startsWith("#") || line.length() < 1) {
					return;
				}
				String[] w = line.split(" ");
				CharNode cn = new CharNode();
				switch(w.length) {
				case 2:
					try {
						cn.setFreq((int)(Math.log(Integer.parseInt(w[1]))*100));//字频计算出自由度
					} catch(NumberFormatException e) {
						//eat...
					}
				case 1:
					
					dic.put(w[0].charAt(0), cn);
				}
			}
		});
		log.info("chars loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+charsFile);

		s = now();
		lineNum = load(new FileInputStream(wordsFile), new FileLoading() {//正常的词库

			public void row(String line, int n) {
				if(line == null || line.startsWith("#") || line.length() < 2) {
					return;
				}
				CharNode cn = dic.get(line.charAt(0));
				if(cn == null) {
					cn = new CharNode();
					dic.put(line.charAt(0), cn);
				}
				cn.addWordTail(tail(line));
			}
			
		});
		log.info("words loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+wordsFile);
		
		//sort
		s = now();
		for(Entry<Character, CharNode> subSet : dic.entrySet()) {
			subSet.getValue().sort();
		}
		log.info("sort time="+(now()-s)+"ms");
		log.info("load dic use time="+(now()-ss)+"ms");
		return dic;
	}
	
	/**
	 * 加载词文件的模板
	 * @return 文件总行数
	 */
	private int load(InputStream fin, FileLoading loading) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(fin), "UTF-8"));
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
		return search(cn, word.toCharArray()) >= 0;
	}
	
	public CharNode head(char ch) {
		return dict.get(ch);
	}
	
	public int search(CharNode node, char[] word) {
		if(node != null) {
			return node.indexOf(word);
		}
		return -1;
	}
	
	/**
	 * 仅仅用来观察词库.
	 */
	public Map<Character, CharNode> getDict() {
		return dict;
	}
	
	static class DicKey {
		String charsFile;
		String wordsFile;
		public DicKey(String charsFile, String wordsFile) {
			this.charsFile = charsFile;
			this.wordsFile = wordsFile;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj instanceof DicKey) {
				DicKey other = (DicKey) obj;
				return charsFile.equals(other.charsFile) && wordsFile.equals(other.wordsFile);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return 31*charsFile.hashCode() + 37*wordsFile.hashCode();
		}
		@Override
		public String toString() {
			return "[chars.dir="+charsFile+", words.dic="+wordsFile+"]";
		}
		
	}
}

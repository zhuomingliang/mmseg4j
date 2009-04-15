package com.chenlb.mmseg4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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

	private static final Logger log = Logger.getLogger(Dictionary.class.getName());
	
	private File dicPath;	//词库目录
	private Map<Character, CharNode> dict;// = new HashMap<Character, CharNode>();
	private Map<Character, Object> unit;	//单个字的单位
	
	private static File defalutPath = null;	//DicKey defaultDicKey = new DicKey("", "", Mode.MAX_WORD);
	private static final Map<File, Map<Character, CharNode>> dics = new ConcurrentHashMap<File, Map<Character, CharNode>>();
	private static final Map<File, Map<Character, Object>> units = new ConcurrentHashMap<File, Map<Character, Object>>();
	private static Map<Character, Object> defaultUnit = null;	//默认的单个字的单位
	
	/**
	 * 加载chars.dic,words.dic文件.<p/>
	 * 查找目录顺序:
	 * <ol>
	 * <li>从mmseg.dic.path指定的目录中加载</li>
	 * <li>从user.dir/data目录</li>
	 * </ol>
	 */
	public Dictionary() {
		init(getDefalutPath());
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
		dicPath = path;
		try {
			//DicKey dk = new DicKey(path.getAbsolutePath());
			Map<Character, CharNode> dic = dics.get(path);
			if(dic == null) {
				dic = loadDic(path);
				dics.put(path, dic);
			}
			dict = dic;
			//加载单字的单位文件
			Map<Character, Object> un = units.get(path);
			if(un == null) {
				File unitFile = new File(path, "units.dic");
				if(unitFile.exists()) {
					un = loadUnit(new FileInputStream(unitFile), unitFile);
				} else {
					un = getDefaultUnit();
				}
				units.put(path, un);
			}
			unit = un;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
	
	private Map<Character, CharNode> loadDic(/*File charsFile,*/ File wordsPath) throws IOException {
		InputStream charsIn = null;
		File charsFile = new File(wordsPath, "chars.dic");
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
				if(line.length() < 1) {
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

		File[] wordsFiles = wordsPath.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				
				return name.startsWith("words") && name.endsWith(".dic");
			}
			
		});
		
		for(File wordsFile : wordsFiles) {
			s = now();
			lineNum = load(new FileInputStream(wordsFile), new FileLoading() {//正常的词库

				public void row(String line, int n) {
					if(line.length() < 2) {
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
		}
		
		//sort
		s = now();
		for(Entry<Character, CharNode> subSet : dic.entrySet()) {
			subSet.getValue().sort();
		}
		log.info("sort time="+(now()-s)+"ms");
		log.info("load dic use time="+(now()-ss)+"ms");
		return dic;
	}
	
	private static Map<Character, Object> loadUnit(final InputStream fin, File unitFile) throws IOException {
		
		final Map<Character, Object> unit = new HashMap<Character, Object>(); 
		
		long s = now();
		int lineNum = load(fin, new FileLoading() {

			public void row(String line, int n) {
				if(line.length() != 1) {
					return;
				}
				unit.put(line.charAt(0), Dictionary.class);
			}
		});
		log.info("unit loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+unitFile);

		return unit;
	}
	
	/**
	 * 加载词文件的模板
	 * @return 文件总行数
	 */
	private static int load(InputStream fin, FileLoading loading) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(fin), "UTF-8"));
		String line = null;
		int n = 0;
		while((line = br.readLine()) != null) {
			if(line == null || line.startsWith("#")) {
				continue;
			}
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
		return search(cn, word.toCharArray(), 0, word.length()-1) >= 0;
	}
	
	public CharNode head(char ch) {
		return dict.get(ch);
	}
	
	/**
	 * sen[offset] 后 tailLen 长的词是否存在.
	 * @see CharNode#indexOf(char[], int, int)
	 * @author chenlb 2009-4-8 下午11:13:49
	 */
	public int search(CharNode node, char[] sen, int offset, int tailLen) {
		if(node != null) {
			return node.indexOf(sen, offset, tailLen);
		}
		return -1;
	}
	
	public boolean isUnit(Character ch) {
		return unit.containsKey(ch);
	}
	
	public static File getDefalutPath() {
		if(defalutPath == null) {
			String defPath = System.getProperty("mmseg.dic.path");
			log.info("look up in mmseg.dic.path="+defPath);
			if(defPath == null) {
				defPath = System.getProperty("user.dir")+"/data";
				log.info("look up in user.dir="+defPath);
				
			}
			
			defalutPath = new File(defPath);
		}
		return defalutPath;
	}
	
	/**
	 * 在jar包里的/data/unit.dic
	 * @author chenlb 2009-4-6 下午12:47:27
	 */
	public static Map<Character, Object> getDefaultUnit() {
		if(defaultUnit == null) {
			InputStream fin = Dictionary.class.getResourceAsStream("/data/units.dic");
			File unitFile = new File(Dictionary.class.getResource("/data/units.dic").getFile());
			try {
				defaultUnit = loadUnit(fin, unitFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return defaultUnit;
	}
	
	/**
	 * 仅仅用来观察词库.
	 */
	public Map<Character, CharNode> getDict() {
		return dict;
	}
	
	public File getDicPath() {
		return dicPath;
	}
	
	static class DicKey {
		
		/*String charsFile;*/
		String wordsFile;
		
		public DicKey(/*String charsFile,*/ String wordsFile) {
			/*this.charsFile = charsFile;*/
			this.wordsFile = wordsFile;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj instanceof DicKey) {
				DicKey other = (DicKey) obj;
				return /*charsFile.equals(other.charsFile) &&*/ wordsFile.equals(other.wordsFile);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return /*31*charsFile.hashCode() +*/ 37*wordsFile.hashCode();
		}
		@Override
		public String toString() {
			//return "[chars.dic="+charsFile+", words.dic="+wordsFile+"]";
			return "[words.dic="+wordsFile+"]";
		}
		
	}

}

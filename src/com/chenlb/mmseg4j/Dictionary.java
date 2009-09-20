package com.chenlb.mmseg4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 词典类. 词库目录单例模式.<br/>
 * 保存单字与其频率,还有词库.<br/>
 * 自动词典文件修改检测并加载(不是默认行为,需要dicPath/mmseg4j.properties设定检测的时间间隔).
 * 
 * @author chenlb 2009-2-20 下午11:34:29
 */
public class Dictionary {

	private static final Logger log = Logger.getLogger(Dictionary.class.getName());
	
	private File dicPath;	//词库目录
	private Map<Character, CharNode> dict;
	private Map<Character, Object> unit;	//单个字的单位
	
	private int wordsCheckInterval = 0;	//默认不使用检测功能.
	/** 记录 word 文件的最后修改时间 */
	private Map<File, Long> wordsLastTime = null;
	/** 词典检测加载线程 */
	private Timer wordCheckTimer = null;
	private long lastLoadTime = 0;

	/** 不要直接使用, 通过 {@link #getDefalutPath()} 使用*/
	private static File defalutPath = null;
	private static final ConcurrentHashMap<File, Dictionary> dics = new ConcurrentHashMap<File, Dictionary>();
	
	protected void finalize() throws Throwable {
		/*
		 * 使 class reload 的时也可以释放词库
		 */
		destroy();
	}
	
	/**
	 * 从默认目录加载词库文件.<p/>
	 * 查找默认目录顺序:
	 * <ol>
	 * <li>从系统属性mmseg.dic.path指定的目录中加载</li>
	 * <li>从classpath/data目录</li>
	 * <li>从user.dir/data目录</li>
	 * </ol>
	 * @see #getDefalutPath()
	 * @throws RuntimeException 没有找到默认目录
	 */
	public static Dictionary getInstance() {
		File path = getDefalutPath();
		return getInstance(path);
	}
	
	/**
	 * @param path 词典的目录
	 */
	public static Dictionary getInstance(String path) {
		return getInstance(new File(path));
	}
	
	/**
	 * @param path 词典的目录
	 */
	public static Dictionary getInstance(File path) {
		Dictionary dic = dics.get(path);
		if(dic == null) {
			dic = new Dictionary(path);
			dics.put(path, dic);
		}
		return dic;
	}
	
	/**
	 * 销毁, 释放资源. 此后此对像不再可用.
	 */
	void destroy() {
		if(wordCheckTimer != null) {
			wordCheckTimer.cancel();
		}
		
		clear(dicPath);
		
		dicPath = null;
		dict = null;
		unit = null;
	}
	
	/**
	 * @see Dictionary#clear(File)
	 */
	public static Dictionary clear(String path) {
		return clear(new File(path));
	}
	
	/**
	 * 从单例缓存中去除
	 * @param path
	 * @return 没有返回 null
	 */
	public static Dictionary clear(File path) {
		return dics.remove(path);
	}
	
	/**
	 * 词典的目录 
	 */
	private Dictionary(File path) {
		init(path);
		if(wordsCheckInterval > 0) {
			wordCheckTimer = new Timer("word-checker", true);
			wordCheckTimer.schedule(new WordCheckTask(), wordsCheckInterval, wordsCheckInterval);	//启动词典文件变更线程
		}
	}
	
	private void init(File path) {
		dicPath = path;
		wordsLastTime = new HashMap<File, Long>();
		
		loadConf();
		reload();	//加载词典
		
	}
	
	private static long now() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 只要 wordsXXX.dic的文件
	 * @return
	 */
	protected File[] listWordsFiles() {
		return dicPath.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				
				return name.startsWith("words") && name.endsWith(".dic");
			}
			
		});
	}
	
	private Map<Character, CharNode> loadDic(File wordsPath) throws IOException {
		InputStream charsIn = null;
		File charsFile = new File(wordsPath, "chars.dic");
		if(charsFile.exists()) {
			charsIn = new FileInputStream(charsFile);
			addLastTime(charsFile);	//chars.dic 也检测是否变更
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
		
		for(File wordsFile : listWordsFiles()) {//只要 wordsXXX.dic的文件
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
			addLastTime(wordsFile);	//检测是否修改用
			log.info("words loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+wordsFile);
		}
		
		log.info("load dic use time="+(now()-ss)+"ms");
		return dic;
	}
	
	private Map<Character, Object> loadUnit(File path) throws IOException {
		InputStream fin = null;
		File unitFile = new File(path, "units.dic");
		if(unitFile.exists()) {
			fin = new FileInputStream(unitFile);
			addLastTime(unitFile);
		} else {	//在jar包里的/data/unit.dic
			fin = Dictionary.class.getResourceAsStream("/data/units.dic");
			unitFile = new File(Dictionary.class.getResource("/data/units.dic").getFile());
		}
		
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
	public static int load(InputStream fin, FileLoading loading) throws IOException {
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
	
	public static interface FileLoading {
		/**
		 * @param line 读出的一行
		 * @param n 当前第几行
		 * @author chenlb 2009-3-3 下午09:55:54
		 */
		void row(String line, int n);
	}
	
	private class WordCheckTask extends TimerTask {
		
		public WordCheckTask() {
			if(log.isLoggable(Level.INFO)) {
				log.info("words file checker["+dicPath+"] started.");
			}
		}

		public void run() {
			if(wordsFileIsChange()) {
				if(log.isLoggable(Level.INFO)) {
					log.info("has some words file change! try reload ...");
				}

				reload();	//加载词库文件
			}
		}
	}; 
	
	/**
	 * 把 wordsFile 文件的最后更新时间加记录下来.
	 * @param wordsFile 非 null
	 */
	private synchronized void addLastTime(File wordsFile) {
		if(wordsFile != null) {
			wordsLastTime.put(wordsFile, wordsFile.lastModified());
		}
	}
	
	/**
	 * 词典文件是否有修改过
	 * @return
	 */
	public synchronized boolean wordsFileIsChange() {
		//检查是否有修改文件,包括删除的
		for(Entry<File, Long> flt : wordsLastTime.entrySet()) {
			File words = flt.getKey();
			if(!words.canRead()) {	//可能是删除了
				return true;
			}
			if(words.lastModified() > flt.getValue()) {	//更新了文件
				return true;
			}
		}
		//检查是否有新文件
		for(File wordsFile : listWordsFiles()) {
			if(!wordsLastTime.containsKey(wordsFile)) {	//有新词典文件
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 全新加载词库
	 */
	public synchronized void reload() {
		try {
			wordsLastTime.clear();
			dict = loadDic(dicPath);
			unit = loadUnit(dicPath);
			lastLoadTime = System.currentTimeMillis();
		} catch (IOException e) {
			throw new RuntimeException("reload dic error!", e);
		}
	}
	
	/**
	 * load config file mmseg4j.properties in dicPath
	 */
	private void loadConf() {
		//try load config file mmseg4j.properties in path
		File confF = new File(dicPath, "mmseg4j.properties");
		if(confF.isFile() && confF.canRead()) {
			Properties conf = new Properties();
			try {
				if(log.isLoggable(Level.INFO)) {
					log.info("try load conf from mmseg4j.properties in "+dicPath);
				}
				conf.load(new FileInputStream(confF));
				String intervalStr = conf.getProperty("words-check-interval");
				if(intervalStr != null) {
					int interval = Integer.parseInt(intervalStr);
					if(interval > 0) {
						wordsCheckInterval = interval;
					}
				}
			} catch (Exception e) {
				if(log.isLoggable(Level.WARNING)) {
					log.log(Level.WARNING, "error, load conf file mmseg4j.properties in "+dicPath, e);
				}
			}
		}
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
	
	public int maxMatch(char[] sen, int offset) {
		CharNode node = dict.get(sen[offset]);
		return maxMatch(node, sen, offset);
	}
	
	public int maxMatch(CharNode node, char[] sen, int offset) {
		if(node != null) {
			return node.maxMatch(sen, offset+1);
		}
		return 0;
	}
	
	public ArrayList<Integer> maxMatch(CharNode node, ArrayList<Integer> tailLens, char[] sen, int offset) {
		tailLens.clear();
		tailLens.add(0);
		if(node != null) {
			return node.maxMatch(tailLens, sen, offset+1);
		}
		return tailLens;
	}
	
	public boolean isUnit(Character ch) {
		return unit.containsKey(ch);
	}
	
	/**
	 * @throws RuntimeException 如果 defalut 不存在
	 */
	public static File getDefalutPath() {
		if(defalutPath == null) {
			String defPath = System.getProperty("mmseg.dic.path");
			log.info("look up in mmseg.dic.path="+defPath);
			if(defPath == null) {
				URL url = Dictionary.class.getClassLoader().getResource("data");
				if(url != null) {
					defPath = url.getFile();
					log.info("look up in classpath="+defPath);
				} else {
					defPath = System.getProperty("user.dir")+"/data";
					log.info("look up in user.dir="+defPath);
				}
				
			}
			
			defalutPath = new File(defPath);
			if(!defalutPath.exists()) {
				throw new RuntimeException("defalut dic path="+defalutPath+" not exist");
			}
		}
		return defalutPath;
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
	
	/** 最后加载词库的时间 */
	public long getLastLoadTime() {
		return lastLoadTime;
	}
}

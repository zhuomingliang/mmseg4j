package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有词都记录在第一个字的结点下.
 * 
 * @author chenlb 2009-2-20 下午11:30:14
 */
public class CharNode {

	private int freq = -1;	//Degree of Morphemic Freedom of One-Character, 单字才需要
	private int maxLen = 0;	//wordTail的最长

	
	//private CharArrayComparator cac = new CharArrayComparator();
	//private CharArraySearhComparator casc = new CharArraySearhComparator();

	private KeyTree ktWordTails = new KeyTree();
	private int wordNum = 0;
	
	public CharNode() {
		
	}
	
	public void addWordTail(char[] wordTail) {
		ktWordTails.add(wordTail);
		wordNum++;
		if(wordTail.length > maxLen) {
			maxLen = wordTail.length;
		}
	}
	public int getFreq() {
		return freq;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public void sort() {
		
	}
	
	public int wordNum() {
		return wordNum;
	}
	
	/**
	 * @param sen 句子, 一串文本.
	 * @param offset 词在句子中的位置
	 * @param tailLen 词尾的长度, 实际是去掉词的长度.
	 * @author chenlb 2009-4-8 下午11:10:30
	 */
	public int indexOf(char[] sen, int offset, int tailLen) {
		//return binarySearch(wordTails, sen, offset+1, tailLen, casc);
		return ktWordTails.match(sen, offset+1, tailLen) ? 1 : -1;
	}
	
	/**
	 * @param sen 句子, 一串文本.
	 * @param wordTailOffset 词在句子中的位置, 实际是 offset 后面的开始找.
	 * @return 返回词尾长, 没有就是 0
	 * @author chenlb 2009-4-10 下午10:45:51
	 */
	public int maxMatch(char[] sen, int wordTailOffset) {
		return ktWordTails.maxMatch(sen, wordTailOffset);
	}
	
	/**
	 * 
	 * @return 至少返回一个包括 0的int
	 * @author chenlb 2009-4-12 上午10:01:35
	 */
	public ArrayList<Integer> maxMatch(ArrayList<Integer> tailLens, char[] sen, int wordTailOffset) {
		return ktWordTails.maxMatch(tailLens, sen, wordTailOffset);
	}
	
	/**
	 * copy Collections.indexedBinarySearch
	 */
	@SuppressWarnings("unused")
	private static int binarySearch(ArrayList<char[]> l, char[] key, int offset, int len, CharArraySearhComparator c) {
		int low = 0;
		int high = l.size()-1;

		while (low <= high) {
		    int mid = (low + high) >> 1;
		    char[] midVal = l.get(mid);
		    int cmp = c.compare(midVal, key, offset, len);	//key第一个不算

		    if (cmp < 0)
			low = mid + 1;
		    else if (cmp > 0)
			high = mid - 1;
		    else
			return mid; // key found
		}
		return -(low + 1);  // key not found
	}
	
	public int getMaxLen() {
		return maxLen;
	}
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}

	public static class CharArrayComparator implements Comparator<char[]> {

		public int compare(char[] a, char[] b) {
			int len = Math.min(a.length, b.length);
			int i = 0;
			while(i<len) {
				if(a[i] > b[i]) {
					return 1;
				} else if(a[i] < b[i]) {
					return -1;
				}
				i++;
				//a[i] == b[i]
			}
			if(i < a.length) {
				return 1;
			} else if(i < b.length) {
				return -1;
			}
			return 0;
		}
	}
	
	public static class CharArraySearhComparator extends CharArrayComparator {

		public int compare(char[] a, char[] b, int bOffest, int bLen) {
			int aLen = a.length;//, bLen = bEnd-bOffest;
			int len = Math.min(aLen, bLen);
			int iA = 0, iB = bOffest;
			while(iA<len) {
				if(a[iA] > b[iB]) {
					return 1;
				} else if(a[iA] < b[iB]) {
					return -1;
				}
				iA++; iB++;
				
			}
			if(iA < aLen) {
				return 1;
			} else if(iA < bLen) {
				return -1;
			}
			return 0;
		}
	}
	
	public static class KeyTree {
		TreeNode head = new TreeNode(' ');
		
		public void add(char[] w) {
			if(w.length < 1) {
				return;
			}
			TreeNode p = head;
			for(int i=0; i<w.length; i++) {
				TreeNode n = p.subNode(w[i]);
				if(n == null) {
					n = new TreeNode(w[i]);
					p.born(w[i], n);
				}
				p = n;
			}
			p.alsoLeaf = true;
		}
		
		/**
		 * @return 返回匹配最长词的长度, 没有找到返回 0.
		 */
		public int maxMatch(char[] sen, int offset) {
			int idx = offset - 1;
			TreeNode node = head;
			for(int i=offset; i<sen.length; i++) {
				node = node.subNode(sen[i]);
				if(node != null) {
					if(node.isAlsoLeaf()) {
						idx = i; 
					}
				} else {
					break;
				}
			}
			return idx - offset + 1;
		}
		
		public ArrayList<Integer> maxMatch(ArrayList<Integer> tailLens, char[] sen, int offset) {
			TreeNode node = head;
			for(int i=offset; i<sen.length; i++) {
				node = node.subNode(sen[i]);
				if(node != null) {
					if(node.isAlsoLeaf()) {
						tailLens.add(i-offset+1); 
					}
				} else {
					break;
				}
			}
			return tailLens;
		}
		
		public boolean match(char[] sen, int offset, int len) {
			TreeNode node = head;
			for(int i=0; i<len; i++) {
				node = node.subNode(sen[offset+i]);
				if(node == null) {
					return false;
				}
			}
			return node.isAlsoLeaf();
		}
	}
	
	private static class TreeNode {
		char key;
		Map<Character, TreeNode> subNodes;
		boolean alsoLeaf;
		public TreeNode(char key) {
			this.key = key;
			subNodes = new HashMap<Character, TreeNode>();
		}
		
		public void born(char k, TreeNode sub) {
			subNodes.put(k, sub);
		}
		
		public TreeNode subNode(char k) {
			return subNodes.get(k);
		}
		public boolean isAlsoLeaf() {
			return alsoLeaf;
		}
	}
}

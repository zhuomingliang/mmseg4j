package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 所有词都记录在第一个字的结点下.
 * 
 * @author chenlb 2009-2-20 下午11:30:14
 */
public class CharNode {

	private ArrayList<char[]> wordTails = new ArrayList<char[]>();	//word除去一个字的部分
	private int freq = -1;	//Degree of Morphemic Freedom of One-Character, 单字才需要
	private int maxLen = 0;	//wordTail的最长
	private int[] lens;
	private SortedSet<Integer> setLens = new TreeSet<Integer>(new Comparator<Integer>() {

		public int compare(Integer a, Integer b) {
			
			return - a.compareTo(b);	//大到小排序
		}
		
	});	//所有不同的wordTail长度
	
	private CharArrayComparator cac = new CharArrayComparator();
	private CharArraySearhComparator casc = new CharArraySearhComparator();

	public CharNode() {
		setLens.add(0);	//没有尾部的结果, 方便生成 chunk
	}
	
	public void addWordTail(char[] wordTail) {
		wordTails.add(wordTail);
		if(wordTail.length > maxLen) {
			maxLen = wordTail.length;
		}
		setLens.add(wordTail.length);
	}
	public int getFreq() {
		return freq;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public void sort() {
		lens = new int[setLens.size()];
		int i = 0;
		for(Integer len : setLens) {
			lens[i++] = len;
		}
		setLens = null;
		Collections.sort(wordTails, cac);
	}
	
	public int wordNum() {
		return wordTails.size();
	}
	
	/**
	 * @param sen 句子, 一串文本.
	 * @param offset 词在句子中的位置
	 * @param tailLen 词尾的长度, 实际是去掉词的长度.
	 * @author chenlb 2009-4-8 下午11:10:30
	 */
	public int indexOf(char[] sen, int offset, int tailLen) {
		return binarySearch(wordTails, sen, offset+1, tailLen, casc);
	}
	
	/**
	 * copy Collections.indexedBinarySearch
	 */
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
	/**
	 * @return 所有不同词长的集,大到小顺序.
	 * @author chenlb 2009-3-29 上午01:17:22
	 */
	public int[] getLens() {
		return lens;
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
			int aLen = a.length;
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
}

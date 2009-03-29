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
	 * @return 能找到 >=0, 否则返回负数
	 * @author chenlb 2009-3-3 下午11:09:07
	 */
	public int indexOf(char[] wordTail) {
		return Collections.binarySearch(wordTails, wordTail, cac);
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
}

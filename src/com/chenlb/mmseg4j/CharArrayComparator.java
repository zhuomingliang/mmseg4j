package com.chenlb.mmseg4j;

import java.util.Comparator;

public class CharArrayComparator implements Comparator<char[]> {

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

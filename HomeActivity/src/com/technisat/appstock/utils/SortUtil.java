package com.technisat.appstock.utils;

import java.util.Arrays;
import java.util.Comparator;

public class SortUtil {
	public static void sort(int[][] ob, final int[] order) {
        Arrays.sort(ob, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                int[] one = (int[]) o1;
                int[] two = (int[]) o2;
                for (int i = 0; i < order.length; i++) {
                    int k = order[i];
                    if (one[k] > two[k]) {
                        return -1;
                    } else if (one[k] < two[k]) {
                        return 1;
                    } else {
                        continue;                     }
                }
                return 0;
            }
        });
    }
}

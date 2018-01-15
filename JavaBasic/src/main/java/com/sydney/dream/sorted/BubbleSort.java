package com.sydney.dream.sorted;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 冒泡排序
 */
public class BubbleSort<T> {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("ab");
        list.add("aa");
        list.add("ab");
        list.add("abc");
        list.add("abe");
        list.add("hello");
        list.add("abc");
        list.add("abc");
        String[] array =  new String[list.size()];
        list.toArray(array);
        bubbleSort(array);
        list.remove("");
        for(int i = 0;i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    public static void bubbleSort(String [] array) {
        long start = System.currentTimeMillis();
        int sizeOfList = array.length;
        for (int i = 0; i < sizeOfList - 1; i++) {
            for (int j = 1; j < sizeOfList - 1 - i; j++) {
                if (array[j - 1].compareTo(array[j]) > 0) {
                    String tmp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = tmp;
                }
            }
        }
        System.out.println("冒泡排序時間為：  " + (System.currentTimeMillis() - start));
    }

}

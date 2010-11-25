/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author bendaccache
 */
public class Main {

    public void sort1() {
        int[] x = new int[99999999];
        for (int i = 0; i < 99999999; i++) {
            x[i] = (int) Math.random();
        }
        int n = x.length;
        for (int pass = 1; pass < n; pass++) {  // count how many times
            // This next loop becomes shorter and shorter
            for (int i = 0; i < n - pass; i++) {
                if (x[i] > x[i + 1]) {
                    // exchange elements
                    int temp = x[i];
                    x[i] = x[i + 1];
                    x[i + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        TreeBuilder t = new TreeBuilder(new File(args[0]));
        t.BuildTree( Integer.parseInt(args[2]));
        t.showOnScreen(t.getTree());
        t.BuildConditionnalTree(args[1]);
        System.out.println("__________________Conditionnal Tree");
        t.showOnScreen(t.getTree());
        t.calculateTrees();
        
    }

    public void sort2() {//faster

        int[] x = new int[99999999];
        for (int i = 0; i < 99999999; i++) {
            x[i] = (int) Math.random();
        }
        int newLowest = 0;            // index of first comparison
        int newHighest = x.length - 1;  // index of last comparison

        while (newLowest < newHighest) {
            int highest = newHighest;
            int lowest = newLowest;
            newLowest = x.length;    // start higher than any legal index
            for (int i = lowest; i < highest; i++) {
                if (x[i] > x[i + 1]) {
                    // exchange elements
                    int temp = x[i];
                    x[i] = x[i + 1];
                    x[i + 1] = temp;
                    if (i < newLowest) {
                        newLowest = i - 1;
                        if (newLowest < 0)
                            newLowest = 0;
                    } else if (i > newHighest)
                        newHighest = i + 1;
                }
            }
        }
    }
}



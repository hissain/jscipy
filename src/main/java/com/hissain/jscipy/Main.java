package com.hissain.jscipy;

import com.hissain.jscipy.signal.Windows;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing Blackman window:");
        double[] blackmanWindow = Windows.blackman(4);
        System.out.println(Arrays.toString(blackmanWindow));

        // Expected: [0.0, 0.63, 0.63, 0.0] with some precision differences
    }
}

package com.example.evgenii.myapp2;

public class ArrayReverse {

    public static void main(String[] args) {

    }

    /**
     * reverse the given array in place * @param input
     * @param input
     */
    public static void reverse(String[] input) {

        // handling null, empty and one element array
        if (input == null || input.length <= 1) {
            return;
        }
        for (int i = 0; i < input.length / 2; i++) {
            String temp = input[i];
            // swap numbers
            input[i] = input[input.length - 1 - i];
            input[input.length - 1 - i] = temp;
        }

    }
}
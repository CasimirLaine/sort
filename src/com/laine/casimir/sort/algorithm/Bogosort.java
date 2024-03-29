package com.laine.casimir.sort.algorithm;

import java.util.Random;

public class Bogosort extends AbstractSortingAlgorithm {

    private final Random random = new Random();

    @Override
    protected void onSort() {
        while (true) {
            boolean inOrder = true;
            for (int index = 0; index < length() - 1; index++) {
                if (greater(index, index + 1)) {
                    inOrder = false;
                    break;
                }
            }
            if (inOrder) {
                break;
            }
            shuffle();
        }
    }

    private void shuffle() {
        for (int index = 0; index < length(); index++) {
            final int newIndex = random.nextInt(length());
            swap(index, newIndex);
        }
    }
}

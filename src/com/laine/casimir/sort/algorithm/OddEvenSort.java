package com.laine.casimir.sort.algorithm;

public class OddEvenSort extends AbstractSortingAlgorithm {

    @Override
    protected void onSort() {
        int offset = 0;
        while (true) {
            boolean isSorted = true;
            for (int index = offset; index + 1 < length(); index += 2) {
                if (greater(index, index + 1)) {
                    swap(index, index + 1);
                    isSorted = false;
                }
            }
            if (isSorted) {
                break;
            }
            offset ^= 1;
        }
    }
}

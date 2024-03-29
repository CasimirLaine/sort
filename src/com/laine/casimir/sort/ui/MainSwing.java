package com.laine.casimir.sort.ui;

import com.laine.casimir.sort.SortListener;
import com.laine.casimir.sort.SortingController;
import com.laine.casimir.sort.algorithm.AbstractSortingAlgorithm;
import com.laine.casimir.sort.algorithm.SortType;
import com.laine.casimir.sort.model.GenerationSettings;
import com.laine.casimir.sort.util.ArrayUtils;

import javax.swing.*;
import java.awt.*;

public final class MainSwing {

    private static final int DEFAULT_WINDOW_WIDTH = 500;
    private static final int DEFAULT_WINDOW_HEIGHT = 500;

    private final GenerationSettings generationSettings = new GenerationSettings();

    private final InfoPanel infoPanel = new InfoPanel();
    private final ControlPanel controlPanel = new ControlPanel(generationSettings);
    private final SortingPanel sortingPanel = new SortingPanel();

    private final SortingController sortingController = new SortingController(sortingPanel);

    public MainSwing() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Sort");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));
        frame.setMinimumSize(new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));
        controlPanel.getStopSortButton().setEnabled(false);
        controlPanel.getRefreshDataButton().addActionListener(e -> refreshData());
        controlPanel.getStartSortButton().addActionListener(e -> startSort());
        controlPanel.getStopSortButton().addActionListener(e -> stopSort());
        controlPanel.getSoundButton().addActionListener(
                e -> sortingController.setSoundEnabled(controlPanel.getSoundButton().isSelected()));
        frame.getContentPane().add(infoPanel, BorderLayout.NORTH);
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(sortingPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void refreshData() {
        final int[] array = ArrayUtils.generateRandomArray(
                generationSettings.getSize(),
                GenerationSettings.LOWER_BOUND,
                GenerationSettings.UPPER_BOUND
        );
        infoPanel.setArraySize(array.length);
        sortingController.setArray(array);
    }

    public void startSort() {
        infoPanel.setComparisons(0);
        infoPanel.setArrayAccesses(0);
        infoPanel.setSwaps(0);
        final Object selectedItem = controlPanel.getAlgorithmComboBox().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        final AbstractSortingAlgorithm sortingAlgorithm = SortType.createSortingAlgorithm(selectedItem.toString());
        if (sortingAlgorithm == null) {
            return;
        }
        sortingAlgorithm.addSortListener(new SortListener() {

            private int comparisonCount;
            private int arrayAccessCount;
            private int swapCount;

            @Override
            public void itemsSwapped(int fromIndex, int toIndex) {
                swapCount++;
                infoPanel.setSwaps(swapCount);
            }

            @Override
            public void onArrayAccess() {
                arrayAccessCount++;
                infoPanel.setArrayAccesses(arrayAccessCount);
            }

            @Override
            public void onComparison() {
                comparisonCount++;
                infoPanel.setComparisons(comparisonCount);
            }

            @Override
            public void onStartSort() {
                controlPanel.getStartSortButton().setEnabled(false);
                controlPanel.getRefreshDataButton().setEnabled(false);
                controlPanel.getStopSortButton().setEnabled(true);
            }

            @Override
            public void onStopSort() {
                sortingAlgorithm.removeSortListener(this);
                controlPanel.getStartSortButton().setEnabled(true);
                controlPanel.getRefreshDataButton().setEnabled(true);
                controlPanel.getStopSortButton().setEnabled(false);
            }
        });
        sortingController.start(sortingAlgorithm);
    }

    public void stopSort() {
        sortingController.stop();
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "True");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(MainSwing::new);
    }
}

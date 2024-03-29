package com.laine.casimir.sort;

import com.laine.casimir.sort.algorithm.AbstractSortingAlgorithm;
import com.laine.casimir.sort.sound.AbstractSortingSound;
import com.laine.casimir.sort.sound.ArrayAccessSound;
import com.laine.casimir.sort.sound.SoundSystem;
import com.laine.casimir.sort.sound.ValidateSound;
import com.laine.casimir.sort.ui.SortingPanel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SortingController implements SortListener {

    private final SoundSystem soundSystem = new SoundSystem();
    private final SortingPanel sortingPanel;
    private AbstractSortingAlgorithm sortingAlgorithm;
    private int[] array;

    private final ExecutorService sortingExecutor = Executors.newSingleThreadExecutor();
    private Future<?> sortingTask;

    public SortingController(SortingPanel sortingPanel) {
        this.sortingPanel = sortingPanel;
        soundSystem.mapSound(AbstractSortingSound.GET, new ArrayAccessSound());
        soundSystem.mapSound(AbstractSortingSound.VALIDATE, new ValidateSound());
    }

    public void start(AbstractSortingAlgorithm sortingAlgorithm) {
        this.sortingAlgorithm = sortingAlgorithm;
        sortingAlgorithm.setArray(array);
        sortingAlgorithm.addSortListener(this);
        sortingPanel.repaintAndWait();
        if (sortingTask == null || sortingTask.isDone() || sortingTask.isCancelled()) {
            sortingTask = sortingExecutor.submit(sortingAlgorithm);
        }
        soundSystem.createAllSounds();
    }

    public void stop() {
        if (sortingAlgorithm == null) {
            return;
        }
        sortingAlgorithm.stop();
        sortingAlgorithm.removeSortListener(this);
        if (sortingTask != null) {
            sortingTask.cancel(false);
        }
        sortingTask = null;
        soundSystem.stopAllSounds();
        sortingAlgorithm = null;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        soundSystem.setSoundEnabled(soundEnabled);
    }

    public void setArray(int[] array) {
        if (sortingTask != null && !sortingTask.isDone() && !sortingTask.isCancelled()) {
            return;
        }
        this.array = array;
        sortingPanel.setArray(array);
    }

    @Override
    public void pointersMoved(int[] indices) {
        soundSystem.playSound(AbstractSortingSound.GET);
        sortingPanel.setSelectedIndices(indices.clone());
        sortingPanel.repaintAndWait();
    }

    @Override
    public void indicesValidated(int[] indices) {
        soundSystem.playSound(AbstractSortingSound.VALIDATE);
        sortingPanel.setValidatedIndices(indices.clone());
        sortingPanel.repaintAndWait();
    }

    @Override
    public void onStartSort() {
        sortingPanel.setSelectedIndices(null);
        sortingPanel.setValidatedIndices(null);
        sortingPanel.repaintAndWait();
    }

    @Override
    public void onStopSort() {
        sortingPanel.repaintAndWait();
    }
}

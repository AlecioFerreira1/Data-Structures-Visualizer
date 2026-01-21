package com.data_structures_visualizer.visual.context.stack;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.models.text.ExplanationRepository;

public final class StackContext {
    private final Stack<Integer> stack;
    private final int valueToPush;
    private final double nodeWidth;
    private final ExplanationRepository explanationRepository;

    public StackContext(
        Stack<Integer> stack, int valueToPush, double height, ExplanationRepository explanationRepository
    ){
        this.stack = stack;
        this.valueToPush = valueToPush;
        this.nodeWidth = ListVisualizerConfig.squareSize * height;
        this.explanationRepository = explanationRepository;
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public int getValueToPush() {
        return valueToPush;
    }

    public double getNodeWidth() {
        return nodeWidth;
    }

    public ExplanationRepository getExplanationRepository(){
        return explanationRepository;
    }
}
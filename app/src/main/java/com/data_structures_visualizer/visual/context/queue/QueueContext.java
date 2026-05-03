package com.data_structures_visualizer.visual.context.queue;

import com.data_structures_visualizer.config.QueueVisualizerConfig;
import com.data_structures_visualizer.models.entities.Queue;
import com.data_structures_visualizer.models.text.ExplanationRepository;

public class QueueContext {
    private final Queue<Integer> queue;
    private final int valueToEnqueue;
    private final double nodeWidth;
    private final ExplanationRepository explanationRepository;
    private final double queueDelimiterWidth;
    private final double xOffset;

    public QueueContext(
        Queue<Integer> queue, int valueToEnqueue, double width, double height, 
        ExplanationRepository explanationRepository, double queueDelimiterWidth
    ){
        this.queue = queue;
        this.valueToEnqueue = valueToEnqueue;
        this.nodeWidth = QueueVisualizerConfig.squareSize * Math.min(height, width);
        this.explanationRepository = explanationRepository;
        this.queueDelimiterWidth = queueDelimiterWidth;
        this.xOffset = (1 + QueueVisualizerConfig.spacingBetweenNodes) * nodeWidth;
    }

    public Queue<Integer> getQueue() {
        return queue;
    }

    public int getValueToEnqueue() {
        return valueToEnqueue;
    }

    public double getNodeWidth() {
        return nodeWidth;
    }

    public ExplanationRepository getExplanationRepository(){
        return explanationRepository;
    }

    public double getQueueDelimiterWidth(){
        return queueDelimiterWidth;
    }

    public double getXoffset(){
        return xOffset;
    }
}

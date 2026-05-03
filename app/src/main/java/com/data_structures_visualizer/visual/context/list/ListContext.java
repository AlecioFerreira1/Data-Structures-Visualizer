package com.data_structures_visualizer.visual.context.list;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.entities.CircularLinkedList;
import com.data_structures_visualizer.models.entities.DoublyLinkedList;
import com.data_structures_visualizer.models.entities.SinglyLinkedList;
import com.data_structures_visualizer.models.text.ExplanationRepository;

public abstract class ListContext {
    protected final ListType listType;
    protected double xOffset;
    protected final SinglyLinkedList<Integer> singlyLinkedList;
    protected final DoublyLinkedList<Integer> doublyLinkedList;
    protected final CircularLinkedList<Integer> circularLinkedList;
    protected final ExplanationRepository explanationRepository;

    protected ListContext(
        ListType listType, double width, double height, SinglyLinkedList<Integer> singlyLinkedList,
        DoublyLinkedList<Integer> doublyLikedList, CircularLinkedList<Integer> circularLinkedList,
        ExplanationRepository explanationRepository
    ){
        final double size = Math.min(width, height);

        this.listType = listType;
        this.singlyLinkedList = singlyLinkedList;
        this.doublyLinkedList = doublyLikedList;
        this.circularLinkedList = circularLinkedList;
        this.xOffset = ListVisualizerConfig.squareSize * size * (1 + ListVisualizerConfig.spacingBetweenNodes);
        this.explanationRepository = explanationRepository;
    }

    public ListType getListType() {
        return listType;
    }

    public double getxOffset() {
        return xOffset;
    }

    public SinglyLinkedList<Integer> getSinglyLinkedList() {
        return singlyLinkedList;
    }

    public DoublyLinkedList<Integer> getDoublyLikedList() {
        return doublyLinkedList;
    }

    public CircularLinkedList<Integer> getCircularLinkedList() {
        return circularLinkedList;
    }

    public ExplanationRepository getExplanationRepository() {
        return explanationRepository;    
    }

    public void setXoffset(double xOffset){
        this.xOffset = xOffset;
    }
}

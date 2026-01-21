package com.data_structures_visualizer.visual.context.list;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.entities.CircularLinkedList;
import com.data_structures_visualizer.models.entities.DoublyLinkedList;
import com.data_structures_visualizer.models.entities.SinglyLinkedList;
import com.data_structures_visualizer.models.text.ExplanationRepository;

public final class InsertContext extends ListContext{
    private final int pos;
    private final int value;
    private final double nodeWidth;
    private final int intialListSize;

    public InsertContext(
        int pos, int value, ListType listType, double width, double height, int intialListSize,
        SinglyLinkedList<Integer> singlyLinkedList, DoublyLinkedList<Integer> doublyLinkedList, 
        CircularLinkedList<Integer> circularLinkedList, ExplanationRepository explanationRepository
    ){
        super(
            listType, width, height, singlyLinkedList, 
            doublyLinkedList, circularLinkedList, explanationRepository
        );

        final double size = Math.min(width, height);

        this.value = value;
        this.pos = pos;
        this.nodeWidth = ListVisualizerConfig.squareSize * size;
        this.intialListSize = intialListSize;
    }

    public int getPos() {
        return pos;
    }

    public int getValue() {
        return value;
    }

    public double getNodeWidth() {
        return nodeWidth;
    }

    public double getxOffset() {
        return xOffset;
    } 

    public int getInitialListSize(){
        return intialListSize;
    }
}
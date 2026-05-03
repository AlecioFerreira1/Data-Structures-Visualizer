package com.data_structures_visualizer.visual.operations.queue;

import java.util.ArrayList;

import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.visual.context.queue.QueueContext;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.scene.layout.AnchorPane;

public final class DequeueOperation {
    private final QueueContext context;
    private final AnchorPane visualization_area;
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrowLabel startLabel;
    private final ArrowLabel endLabel;

    public DequeueOperation(
        QueueContext context, AnchorPane visualization_area, ArrayList<VisualNode> nodes, 
        ArrayList<Arrow> arrows, ArrowLabel startLabel, ArrowLabel endLabel
    ){
        this.context = context;
        this.visualization_area = visualization_area;
        this.nodes = nodes;
        this.arrows = arrows;
        this.startLabel = startLabel;
        this.endLabel = endLabel;
    }

    public void build(AnimationTimeLine timeline){
        
    }
}

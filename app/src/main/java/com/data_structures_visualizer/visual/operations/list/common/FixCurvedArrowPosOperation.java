package com.data_structures_visualizer.visual.operations.list.common;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.visual.animation.CurvedArrowAnimator;
import com.data_structures_visualizer.visual.ui.CurvedArrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;

public final class FixCurvedArrowPosOperation {
    private final ArrayList<VisualNode> nodes;
    private final CurvedArrow curvedArrow;
    private final AnchorPane visualization_area;
    private final double xOffset;
    private final ListType listType;

    public FixCurvedArrowPosOperation(
        ArrayList<VisualNode> nodes, CurvedArrow curvedArrow, 
        AnchorPane visualization_area, double xOffset, ListType listType
    ){
        this.nodes = nodes;
        this.curvedArrow = curvedArrow;
        this.visualization_area = visualization_area;
        this.xOffset = xOffset;
        this.listType = listType;
    }

    private Animation buildCircularArrow(boolean forward){
        VisualNode first = nodes.get(0);
        VisualNode last = nodes.get(nodes.size() - 1);

        Bounds lastBounds  = last.getBoundsInParent();
        Bounds firstBounds = first.getBoundsInParent();

        double fromStartX = lastBounds.getMinX() + lastBounds.getWidth() / 2 - xOffset;
        double fromStartY = lastBounds.getMaxY();

        double toStartX = lastBounds.getMinX() + lastBounds.getWidth() / 2;
        double toStartY = lastBounds.getMaxY();

        double fromEndX = firstBounds.getMinX() + firstBounds.getWidth() / 2 - xOffset;
        double fromEndY = fromStartY;

        double toEndX = firstBounds.getMinX() + firstBounds.getWidth() / 2;
        double toEndY = fromEndY;

        double width  = visualization_area.getWidth();
        double height = visualization_area.getHeight();

        return forward
            ? CurvedArrowAnimator.animateEndPoints(
                fromStartX, fromStartY, fromEndX, fromEndY,
                toStartX, toStartY, toEndX, toEndY,
                1.5 * ListVisualizerConfig.speedVisualization,
                Math.min(width, height) * 0.02,
                curvedArrow, nodes.size() == 1
            )
            : CurvedArrowAnimator.animateEndPoints(
                toStartX, toStartY, toEndX, toEndY,
                fromStartX, fromStartY, fromEndX, fromEndY,
                1.5 * ListVisualizerConfig.speedVisualization,
                Math.min(width, height) * 0.02,
                curvedArrow, nodes.size() == 1
            );
    }

    public void build(AnimationTimeLine timeLine){
        if(listType != ListType.CIRCULAR) return;
        
        timeLine.addStep(new Step(
            () -> buildCircularArrow(true), 
            () -> buildCircularArrow(false)
        )); 
    }
}

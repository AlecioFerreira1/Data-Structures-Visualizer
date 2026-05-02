package com.data_structures_visualizer.visual.operations.list.common;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.visual.animation.AnimationUtils;
import com.data_structures_visualizer.visual.operations.Operation;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public final class RepositionNodesOperation {
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrayList<Arrow> prevArrows;
    private final ArrowLabel headLabel;
    private final ArrowLabel tailLabel;
    private final AnchorPane visualization_area;
    private final ListType listType;
    private final int listInitialSize;

    public RepositionNodesOperation(
        ArrayList<VisualNode> nodes, ArrayList<Arrow> arrows, ArrayList<Arrow> prevArrows, 
        ArrowLabel headLabel, ArrowLabel tailLabel, AnchorPane visualization_area, 
        int listInitialSize, ListType listType
    ){
        this.nodes = nodes;
        this.arrows = arrows;
        this.prevArrows = prevArrows;
        this.headLabel = headLabel;
        this.tailLabel = tailLabel;
        this.visualization_area = visualization_area;
        this.listType = listType;
        this.listInitialSize = listInitialSize;
    }

    public Animation build(int startIndex, double xOffset, Operation op){
        ArrayList<Node> toMove = new ArrayList<Node>();

        for(int i = startIndex; i < nodes.size(); ++i){
            toMove.add(nodes.get(i));

            if(i < arrows.size()){
                toMove.add(arrows.get(i));

                if(listType == ListType.DOUBLY && i < prevArrows.size()){
                    toMove.add(prevArrows.get(i));
                }
            }
        }

        if(visualization_area.getChildren().contains(headLabel) && startIndex == 0 && op != Operation.DELETE){
            toMove.add(headLabel);
        }

        if(visualization_area.getChildren().contains(tailLabel) && startIndex != listInitialSize){
            toMove.add(tailLabel);
        }

        return AnimationUtils.displacementEffect(
            toMove, ListVisualizerConfig.translateDuration, xOffset
        );
    }
}

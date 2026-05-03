package com.data_structures_visualizer.visual.layout;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.QueueVisualizerConfig;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.ArrowLabel.ArrowPosition;
import com.data_structures_visualizer.visual.ui.ExplanationTextRect;
import com.data_structures_visualizer.visual.ui.QueueDelimiter;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

public final class QueueLayoutManager {
    private final AnchorPane visualization_area;
    private QueueDelimiter queueDelimiter;
    private final ArrayList<VisualNode> nodes = new ArrayList<VisualNode>();
    private final ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    private ArrowLabel startLabel;
    private ArrowLabel endLabel;
    private ExplanationTextRect explanationTextRect;

    public QueueLayoutManager(AnchorPane visualization_area){
        this.visualization_area = visualization_area;
    }

    public void start(){
        startLabel = new ArrowLabel(
            QueueVisualizerConfig.spacingBetweenNodes * QueueVisualizerConfig.squareSize * 625, 
            "INÍCIO", 15
        );

        startLabel.setArrowPosition(ArrowPosition.BELOW);

        endLabel = new ArrowLabel(
            QueueVisualizerConfig.spacingBetweenNodes * QueueVisualizerConfig.squareSize * 625,
             "FIM", 15
        );

        endLabel.setArrowPosition(ArrowPosition.BELOW); 
    }

    public void fixVisualizationAreaLayout(double width, double height){
        LayoutManager.resizeExplanationRect(visualization_area, explanationTextRect, width, height);

        double updatedWidth = 0.8 * width;
        double updatedHeight = 0.15 * height;
        double updatedStroke = 0.004 * width;

        if(queueDelimiter == null){
            queueDelimiter = new QueueDelimiter(updatedWidth, updatedHeight, updatedStroke);
            visualization_area.getChildren().add(queueDelimiter);
        } 

        Platform.runLater(() -> {
            queueDelimiter.update(updatedWidth, updatedHeight, updatedStroke);
            AnchorPane.setTopAnchor(queueDelimiter, (height / 2) - (queueDelimiter.getSpacingBetweenLines() / 2));
            AnchorPane.setLeftAnchor(queueDelimiter, (width / 2) - (queueDelimiter.getWidth() / 2));

            double xOffset = 0.01 * width;
            double value = height < width ? height : width;

            for(int i = 0; i < nodes.size(); ++i){
                nodes.get(i).update(
                    value * QueueVisualizerConfig.squareSize, 
                    value * QueueVisualizerConfig.squareSize, value * 0.005
                );

                nodes.get(i).setTranslateX(0);
                nodes.get(i).setTranslateY(0);
                
                AnchorPane.setTopAnchor(
                    nodes.get(i), (height / 2) - (nodes.get(i).getRect().getHeight()) / 2
                );

                AnchorPane.setLeftAnchor(
                    nodes.get(i), 
                    xOffset + (width / 2) - (queueDelimiter.getWidth() / 2) +
                    ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodes.get(i).getRect().getWidth() * i)
                );

                final double nodeWidth = value * ListVisualizerConfig.squareSize;
                final double arrowLenght = ListVisualizerConfig.spacingBetweenNodes * nodeWidth;

                if(i < arrows.size()){
                    resizeArrow(arrows.get(i), arrowLenght, width, height);
                    
                    AnchorPane.setTopAnchor(
                        arrows.get(i), 
                        (height / 2) - (arrows.get(i).getBoundsInParent().getHeight() / 2)
                    );

                    AnchorPane.setLeftAnchor(
                        arrows.get(i), 
                        xOffset + (width / 2) - (queueDelimiter.getWidth() / 2) + nodeWidth +
                        ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodes.get(i).getRect().getWidth() * i)
                    );
                }
            }

            anchorArrowLabels(
                QueueVisualizerConfig.squareSize * width * 0.6, 
                QueueVisualizerConfig.squareSize * height / 3, 
                width, height
            );
        });
    }

    private void resizeArrow(Arrow arrow, double lenght, double width, double height){
        arrow.setStrokeWidth(height * 0.003);
        arrow.setLenght(lenght);
    }

    private void anchorArrowLabels(double arrowLenght, double fontSize, double width, double height){
        if(nodes.isEmpty()){
            visualization_area.getChildren().remove(startLabel);
            visualization_area.getChildren().remove(endLabel);
            return;
        }

        if(!visualization_area.getChildren().contains(startLabel)){
            visualization_area.getChildren().add(startLabel);
        }

        if(!visualization_area.getChildren().contains(endLabel)){
            visualization_area.getChildren().add(endLabel);
        }

        final double value = height < width ? height : width;
        final double nodeWidth = QueueVisualizerConfig.squareSize * value; 
        final double xOffset = 0.01 * width;
        final double labelsYoffset = 1.7;

        startLabel.setText("INÍCIO");
        startLabel.update(arrowLenght, startLabel.getText(), fontSize);
        startLabel.setTranslateX(0);

        if(nodes.size() == 1){
            visualization_area.getChildren().remove(endLabel);
            startLabel.setText("INÍCIO\nFIM");
        }

        if(visualization_area.getChildren().contains(endLabel)){
            endLabel.update(arrowLenght, "FIM", fontSize);
            endLabel.setTranslateX(0);

            AnchorPane.setTopAnchor(
                endLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
            );

            AnchorPane.setLeftAnchor(
                endLabel, (nodeWidth / 2 ) + (width / 2) - (queueDelimiter.getWidth() / 2) +
                ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodeWidth * (nodes.size() - 1)) 
            );
        }
            
        AnchorPane.setTopAnchor(
            startLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
        );

        AnchorPane.setLeftAnchor(
            startLabel, xOffset + (width / 2) - (queueDelimiter.getWidth() / 2)
        );
    }

    public void clearVisualization(){
        for(VisualNode node : nodes){
            visualization_area.getChildren().remove(node);
        }

        for(Arrow arrow : arrows){
            visualization_area.getChildren().remove(arrow);
        }

        visualization_area.getChildren().remove(startLabel);
        visualization_area.getChildren().remove(endLabel);
        nodes.clear();
    }  

    public ArrayList<VisualNode> getNodes(){
        return nodes;
    }

    public ArrayList<Arrow> getArrows(){
        return arrows;
    }

    public ArrowLabel getStartLabel(){
        return startLabel;
    }

    public ArrowLabel getEndLabel(){
        return endLabel;
    }

    public ExplanationTextRect getExplanationTextRect(){
        return explanationTextRect;
    }

    public QueueDelimiter getQueueDelimiter(){
        return queueDelimiter;
    }

    public void setExplanationTextRect(ExplanationTextRect rect){
        this.explanationTextRect = rect;
    }
}
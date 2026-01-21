package com.data_structures_visualizer.visual.layout;

import java.util.ArrayList;

import com.data_structures_visualizer.config.StackVisualizerConfig;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.ArrowLabel.ArrowPosition;
import com.data_structures_visualizer.visual.ui.ExplanationTextRect;
import com.data_structures_visualizer.visual.ui.StackBase;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.scene.layout.AnchorPane;

public final class StackLayoutManager {
    private final AnchorPane visualization_area;
    private StackBase stackBase;
    private final ArrayList<VisualNode> nodes = new ArrayList<VisualNode>();
    private ArrowLabel topLabel;
    private ExplanationTextRect explanationTextRect;

    public StackLayoutManager(AnchorPane visualization_area){
        this.visualization_area = visualization_area;
    }

    public void start(){
        createLabel();
    }

    private void createLabel(){
        topLabel = new ArrowLabel(StackVisualizerConfig.squareSize * 625 * 0.6, "TOPO", 15);
        topLabel.setArrowPosition(ArrowPosition.RIGHT);
        visualization_area.getChildren().add(topLabel);
    }

    public void fixVisualizationAreaLayout(double width, double height){
        LayoutManager.resizeExplanationRect(visualization_area, explanationTextRect, width, height);

        final double updatedWidth = 0.15 * width;
        final double updatedHeight = 0.8 * height;
        final double updatedStroke = 0.004 * width;

        if(stackBase == null){
            stackBase = new StackBase(updatedWidth, updatedHeight, updatedStroke);
            visualization_area.getChildren().add(stackBase);
        } 

        stackBase.update(updatedWidth, updatedHeight, updatedStroke);
        AnchorPane.setTopAnchor(stackBase, (height / 2) - (stackBase.getHeight() / 2));
        AnchorPane.setLeftAnchor(stackBase, (width / 2) - (stackBase.getWidth() / 2));

        double yOffset = 0.0005 * height;
        double initialHeight = updatedHeight + yOffset;
        
        for(int i = 0; i < nodes.size(); ++i){
            nodes.get(i).setTranslateX(0);
            nodes.get(i).setTranslateY(0);

            nodes.get(i).update(height * StackVisualizerConfig.squareSize, 
                height * StackVisualizerConfig.squareSize, height * 0.005
            );
            
            AnchorPane.setTopAnchor(
                nodes.get(i), 
                initialHeight - ((1 + StackVisualizerConfig.spacingBetweenNodes) * 
                                  nodes.get(i).getRect().getHeight() * i)
            );

            AnchorPane.setLeftAnchor(
                nodes.get(i), 
                (width / 2) - (nodes.get(i).getRect().getWidth()) / 2
            );
        }

        anchorTopLabel(width, height);
    }

    private void anchorTopLabel(double width, double height){
        if(nodes.size() == 0){
            visualization_area.getChildren().remove(topLabel);
            return;
        }

        if(!visualization_area.getChildren().contains(topLabel)){
            visualization_area.getChildren().add(topLabel);
        }

        double yOffset = 0.0005 * height;
        double initialHeight =  0.8 * height + yOffset;
        double arrowLenght = StackVisualizerConfig.squareSize * width * 1.02;
        double xOffset = 2.3 * arrowLenght;

        topLabel.update(arrowLenght, "TOPO", StackVisualizerConfig.squareSize * height / 3);
        topLabel.setTranslateX(0);
        topLabel.setTranslateY(0);
        
        AnchorPane.setTopAnchor(topLabel,
            initialHeight - ((1 + StackVisualizerConfig.spacingBetweenNodes) * height * 
            StackVisualizerConfig.squareSize * (nodes.size() - 1)) + 
            (height * StackVisualizerConfig.squareSize) / 2 - (0.01 * height)
        );
        
        AnchorPane.setLeftAnchor(topLabel,
            (width / 2) - (topLabel.getArrow().getBaseLenght() / 2) - xOffset
        );
    }

    public void clearVisualization(){
        for(VisualNode node : nodes){
            visualization_area.getChildren().remove(node);
        }

        visualization_area.getChildren().remove(topLabel);
        nodes.clear();
    }

    public AnchorPane getVisualization_area() {
        return visualization_area;
    }

    public StackBase getStackBase() {
        return stackBase;
    }

    public ArrayList<VisualNode> getNodes() {
        return nodes;
    }

    public ArrowLabel getTopLabel() {
        return topLabel;
    }

    public ExplanationTextRect getExplanationTextRect(){
        return explanationTextRect;
    }

    public void setExplanationTextRect(ExplanationTextRect rect){
        this.explanationTextRect = rect;
    }
}
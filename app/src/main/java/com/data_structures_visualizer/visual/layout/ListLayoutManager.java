package com.data_structures_visualizer.visual.layout;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.entities.CircularLinkedList;
import com.data_structures_visualizer.models.entities.DoublyLinkedList;
import com.data_structures_visualizer.models.entities.SinglyLinkedList;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.ArrowLabel.ArrowPosition;
import com.data_structures_visualizer.visual.ui.CurvedArrow;
import com.data_structures_visualizer.visual.ui.ExplanationTextRect;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

public final class ListLayoutManager {
    private AnchorPane visualization_area;
    private ListType listType;
    private final ArrayList<VisualNode> nodes = new ArrayList<VisualNode>();
    private final ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    private final ArrayList<Arrow> prevArrows = new ArrayList<Arrow>();
    private CurvedArrow curvedArrow;  
    private ArrowLabel headLabel;
    private ArrowLabel tailLabel;
    private final AnimationTimeLine animationTimeLine;
    private final SinglyLinkedList<Integer> singlyLinkedList;
    private final DoublyLinkedList<Integer> doublyLikedList;
    private final CircularLinkedList<Integer> circularLinkedList;
    private ExplanationTextRect explanationTextRect;
    
    public ListLayoutManager(
        AnchorPane visualization_area, AnimationTimeLine animationTimeLine, ListType listType,
        SinglyLinkedList<Integer> singlyLinkedList, DoublyLinkedList<Integer> doublyLikedList, 
        CircularLinkedList<Integer> circularLinkedList
    ){
        this.visualization_area = visualization_area;
        this.listType = listType;
        this.animationTimeLine = animationTimeLine;
        this.singlyLinkedList = singlyLinkedList;
        this.doublyLikedList = doublyLikedList;
        this.circularLinkedList = circularLinkedList;
    }

    public void build(){
        createtHeadAndTailLabels();
    }

    public void setListType(ListType listType){
        this.listType = listType;
    }
    
    private void createtHeadAndTailLabels(){
        headLabel = new ArrowLabel(
            ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize * 625, 
            "CABEÇA", 15
        );

        headLabel.setArrowPosition(ArrowPosition.BELOW);

        tailLabel = new ArrowLabel(
            ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize * 625, 
            "CAUDA", 15
        );

        tailLabel.setArrowPosition(ArrowPosition.BELOW);
    }

    public void fixVisualizationAreaLayout(double width, double height){
        LayoutManager.resizeExplanationRect(visualization_area, explanationTextRect, width, height);

        switch(listType) {
            case ListType.SINGLY:
                singlyListVisualization(width, height);
                break;
            
            case ListType.DOUBLY:
                doublyListVisualization(width, height);
                break;
        
            default:
                circularListVisualization(width, height);
                break;
        }
    }

    private void singlyListVisualization(double width, double height){
        removeCurvedArrow();

        for(Arrow arrow : prevArrows){
            visualization_area.getChildren().remove(arrow);
        }

        prevArrows.clear();

        final double value = height < width ? height : width;
        final double nodeWidth = value * ListVisualizerConfig.squareSize;
        final double arrowLenght = ListVisualizerConfig.spacingBetweenNodes * nodeWidth;

        anchorArrowLabels(
            ListVisualizerConfig.squareSize * width * 0.6, 
            ListVisualizerConfig.squareSize * height / 3, width, height
        );
        
        for(int i = 0; i < nodes.size(); ++i){
           nodes.get(i).update(nodeWidth, nodeWidth, value * 0.005);
           nodes.get(i).setTranslateX(0);
           nodes.get(i).setTranslateY(0);

            anchorNode(nodes.get(i), width, height, i);

            if(i < arrows.size()){
                resizeArrow(arrows.get(i), arrowLenght, width, height);
                    
                AnchorPane.setTopAnchor(
                    arrows.get(i), 
                    (height / 2) - (arrows.get(i).getBoundsInParent().getHeight() / 2)
                );

                AnchorPane.setLeftAnchor(
                    arrows.get(i), 
                    ((ListVisualizerConfig.xOffsetForNodes * width) + nodeWidth + 
                    ((1 + ListVisualizerConfig.spacingBetweenNodes) * nodeWidth) * i)
                );
            }
        }
    }

    private void doublyListVisualization(double width, double height){
        removeCurvedArrow();

        double value = height < width ? height : width;
        double nodeWidth = ListVisualizerConfig.squareSize * value;
        double arrowLenght = ListVisualizerConfig.spacingBetweenNodes * nodeWidth;

        anchorArrowLabels(
            ListVisualizerConfig.squareSize * width * 0.6, 
            ListVisualizerConfig.squareSize * height / 3, width, height
        );

        for(int i = 0; i < nodes.size(); ++i){
            nodes.get(i).update(
                value * ListVisualizerConfig.squareSize, 
                value * ListVisualizerConfig.squareSize, value * 0.005
            );

            nodes.get(i).setTranslateX(0);
            nodes.get(i).setTranslateY(0);
            anchorNode(nodes.get(i), width, height, i);

            if(i < arrows.size()){
                resizeArrow(arrows.get(i), arrowLenght, width, height);

                if(i >= prevArrows.size()){
                    prevArrows.add(i, new Arrow(arrows.get(i)));
                    prevArrows.get(i).setRotate(180);
                    visualization_area.getChildren().add(prevArrows.get(i));
                }

                resizeArrow(prevArrows.get(i), arrowLenght, width, height);
                    
                AnchorPane.setTopAnchor(
                    arrows.get(i), 
                    (height / 2) - (arrows.get(i).getBoundsInParent().getHeight() * 1.25)
                );

                AnchorPane.setLeftAnchor(
                    arrows.get(i), 
                    ((ListVisualizerConfig.xOffsetForNodes * width) + nodeWidth + 
                    ((1 + ListVisualizerConfig.spacingBetweenNodes) * nodeWidth) * i)
                );

                AnchorPane.setTopAnchor(
                    prevArrows.get(i), 
                    (height / 2) + (prevArrows.get(i).getBoundsInParent().getHeight() / 2)
                );

                AnchorPane.setLeftAnchor(
                    prevArrows.get(i), 
                    ((ListVisualizerConfig.xOffsetForNodes * width) + nodeWidth + 
                    ((1 + ListVisualizerConfig.spacingBetweenNodes) * nodeWidth) * i)
                );
            }
        }
    }

    private void removeCurvedArrow(){
        if(curvedArrow != null){
            visualization_area.getChildren().remove(curvedArrow);
            curvedArrow = null;
        }
    }

    private void circularListVisualization(double width, double height){
        singlyListVisualization(width, height);

        if(nodes.size() > 0){
            Platform.runLater(() -> {
                visualization_area.applyCss();
                visualization_area.layout();

                int lastNodeIndex = nodes.size() - 1;
                VisualNode last = nodes.get(lastNodeIndex);
                VisualNode first = nodes.get(0);
                double startX = last.getLayoutX() + (last.getRect().getWidth() / 2);
                double startY = last.getLayoutY() + last.getRect().getHeight();
                double endX = first.getLayoutX() + (first.getRect().getWidth() / 2);
                double endY = startY;
                double value = Math.min(width, height);

                if(curvedArrow == null){
                    curvedArrow = new CurvedArrow(startX, startY, endX, endY, 0.02 * value);
                    visualization_area.getChildren().add(curvedArrow);
                }

                curvedArrow.setTranslateX(0);
                curvedArrow.setTranslateY(0);

                if(nodes.size() == 1){
                    startX = last.getLayoutX() + last.getRect().getWidth();
                    startY = last.getLayoutY() + (last.getRect().getHeight() / 2);
                    curvedArrow.update(startX, startY, endX, endY, 0.02 * value, true);
                    return;
                }
                
                curvedArrow.update(startX, startY, endX, endY, 0.02 * value, false);
            });
        }
    }
    
    private void anchorNode(VisualNode node, double width, double height, int pos){
        AnchorPane.setTopAnchor(node, (height / 2) - (node.getRect().getHeight() / 2)); 
        AnchorPane.setLeftAnchor(node, 
            (ListVisualizerConfig.xOffsetForNodes * width) + 
            (((1 + ListVisualizerConfig.spacingBetweenNodes) * node.getRect().getWidth() * pos))
        );
    }

    private void resizeArrow(Arrow arrow, double lenght, double width, double height){
        arrow.setStrokeWidth(height * 0.003);
        arrow.setLenght(lenght);
    }

    public void clearVisualization(){
        visualization_area.getChildren().clear();
        nodes.clear();
        arrows.clear();
        curvedArrow = null;
        prevArrows.clear();
        animationTimeLine.clear();
        singlyLinkedList.clear();
        doublyLikedList.clear();
        circularLinkedList.clear();
        visualization_area.getChildren().add(explanationTextRect);
        explanationTextRect.clear();
    }

    private void anchorArrowLabels(double arrowLenght, double fontSize, double width, double height){
        if(nodes.isEmpty()){
            visualization_area.getChildren().remove(headLabel);
            visualization_area.getChildren().remove(tailLabel);
            return;
        }

        if(!visualization_area.getChildren().contains(headLabel)){
            visualization_area.getChildren().add(headLabel);
        }

        if(!visualization_area.getChildren().contains(tailLabel)){
            visualization_area.getChildren().add(tailLabel);
        }

        final double value = height < width ? height : width;
        final double nodeWidth = ListVisualizerConfig.squareSize * value; 
        final double xOffset = 0.01 * value;
        final double labelsYoffset = 1.7;

        headLabel.setText("CABEÇA");
        headLabel.setTranslateX(0);
        headLabel.update(arrowLenght, headLabel.getText(), fontSize);

        if(nodes.size() == 1){
            visualization_area.getChildren().remove(tailLabel);
            headLabel.setText("CABEÇA\nCAUDA");
        }

        if(visualization_area.getChildren().contains(tailLabel)){
            tailLabel.setTranslateX(0);
            tailLabel.update(arrowLenght, "CAUDA", fontSize);

            AnchorPane.setTopAnchor(
                tailLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
            );

            AnchorPane.setLeftAnchor(
                tailLabel, ListVisualizerConfig.xOffsetForNodes + (nodeWidth / 2) + 
                ((1 + ListVisualizerConfig.spacingBetweenNodes) * nodeWidth * (nodes.size() - 1)) - xOffset
            );
        }
            
        AnchorPane.setTopAnchor(
            headLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
        );

        AnchorPane.setLeftAnchor(
            headLabel, ListVisualizerConfig.xOffsetForNodes + (nodeWidth / 2) - xOffset
        );
    }

    public ArrayList<VisualNode> getNodes() {
        return nodes;
    }

    public ArrayList<Arrow> getArrows() {
        return arrows;
    }

    public ArrayList<Arrow> getPrevArrows() {
        return prevArrows;
    }

    public CurvedArrow getCurvedArrow() {
        return curvedArrow;
    }

    public ArrowLabel getHeadLabel() {
        return headLabel;
    }

    public ArrowLabel getTailLabel() {
        return tailLabel;
    }

    public SinglyLinkedList<Integer> getSinglyLinkedList() {
        return singlyLinkedList;
    }

    public DoublyLinkedList<Integer> getDoublyLikedList() {
        return doublyLikedList;
    }

    public CircularLinkedList<Integer> getCircularLinkedList() {
        return circularLinkedList;
    }

    public ExplanationTextRect getExplanationTextRect() {
        return explanationTextRect;
    } 

    public void setExplanationTextRect(ExplanationTextRect rect){
        this.explanationTextRect = rect;
    }
}
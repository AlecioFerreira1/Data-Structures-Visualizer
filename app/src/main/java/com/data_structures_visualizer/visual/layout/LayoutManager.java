package com.data_structures_visualizer.visual.layout;

import com.data_structures_visualizer.visual.ui.ExplanationTextRect;

import javafx.scene.layout.AnchorPane;

public final class LayoutManager {
    public static final double explanationRectWidthRatio = 0.27;
    public static final double explanationRectHeightRatio = 0.27;

    public static ExplanationTextRect createExplanationRect(AnchorPane visualization_area){
        ExplanationTextRect explanationTextRect = new ExplanationTextRect(
            LayoutManager.explanationRectWidthRatio * 800, 
            LayoutManager.explanationRectHeightRatio * 800
        );
        
        insertExplanationRect(visualization_area, explanationTextRect);

        return explanationTextRect;
    }

    public static void insertExplanationRect(
        AnchorPane visualization_area, ExplanationTextRect explanationTextRect
    ){
        visualization_area.getChildren().add(explanationTextRect);

        AnchorPane.setBottomAnchor(explanationTextRect, 5.0);
        AnchorPane.setRightAnchor(explanationTextRect, 5.0);
    }

    public static void resizeExplanationRect(
        AnchorPane visualization_area, ExplanationTextRect explanationTextRect,
        double width, double height
    ){

        double h = height * explanationRectHeightRatio;
        double w = width * explanationRectWidthRatio;

        explanationTextRect.getRect().setOpacity(0.5);

        explanationTextRect.setMinHeight(h);
        explanationTextRect.setPrefHeight(h);
        explanationTextRect.setMaxHeight(h);

        explanationTextRect.setMinWidth(w);
        explanationTextRect.setPrefWidth(w);
        explanationTextRect.setMaxWidth(w);

        AnchorPane.setBottomAnchor(explanationTextRect, 5.0);
        AnchorPane.setRightAnchor(explanationTextRect, 5.0);
    }
}

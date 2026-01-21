package com.data_structures_visualizer.visual.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public final class StackBase extends Group {
    private final Line left = new Line();
    private final Line basis = new Line();
    private final Line right = new Line();
    private double height;
    private double width;

    public StackBase(double width, double height, double strokeWidth){
        this.width = width;
        this.height = height;

        left.setStroke(Color.BLACK);
        basis.setStroke(Color.BLACK);
        right.setStroke(Color.BLACK);

        getChildren().addAll(left, basis, right);
        update(width, height, strokeWidth);
    }

    public void update(double width, double height, double strokeWidth){
        this.width = width;
        this.height = height;

        left.setStrokeWidth(strokeWidth);
        left.setStartX(0);
        left.setStartY(0);
        left.setEndX(0);
        left.setEndY(height);  

        basis.setStrokeWidth(strokeWidth);
        basis.setStartX(0);
        basis.setStartY(height);
        basis.setEndX(width);
        basis.setEndY(height); 

        right.setStrokeWidth(strokeWidth);
        right.setStartX(width);
        right.setStartY(0);
        right.setEndX(width);
        right.setEndY(height); 
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }
}

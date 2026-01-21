package com.data_structures_visualizer.visual.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class ArrowLabel extends Pane {
    public static enum ArrowPosition{
        RIGHT, BELOW, LEFT, ABOVE
    }

    private Arrow arrow;
    private Text text;
    private ArrowPosition position = ArrowPosition.RIGHT;

    public ArrowLabel(double lenght, String text, double fontSize){
        this.arrow = new Arrow(lenght);     
        this.text = new Text(text);  
        this.text.setFill(Color.RED);
        this.text.setFont(Font.font(fontSize));
        
        getChildren().addAll(this.text, arrow);
        layoutChildren();
    }

    public void setArrowPosition(ArrowPosition pos){
        position = pos;
        requestLayout();
    }

    @Override
    protected void layoutChildren(){
        final double textWidth = text.getLayoutBounds().getWidth();
        final double textHeight = text.getLayoutBounds().getHeight();
        final double arrowWidth = arrow.getBaseLenght();
        final double arrowHeight = 0.4 * arrowWidth;
        final double yOffset = 0.2 * arrowWidth;
        final double xOffset = 0.2 * arrowWidth;

        text.relocate(0, 0);

        switch(position){
            case RIGHT -> placeArrow(textWidth + xOffset, (textHeight / 2) - (arrowHeight / 2),  0);
            case LEFT -> {
                text.relocate(textWidth + xOffset, 0);
                placeArrow(-(textHeight + xOffset), (textHeight / 2) - (arrowHeight / 2), 180);
            }
            case ABOVE -> placeArrow((textWidth / 2) - (arrowWidth / 2), -(textHeight + arrowHeight + yOffset), 270);
            case BELOW -> placeArrow((textWidth / 2) - (arrowWidth / 2), textHeight + arrowHeight + yOffset, 90); 
            default -> placeArrow(textWidth, textHeight / 2, 0);
        }
    }

    private void placeArrow(double x, double y, double angle){
        arrow.setRotate(angle);
        arrow.relocate(x, y);
    }

    public void update(double lenght, String text, double fontSize){
        this.text.setText(text);
        this.text.setFont(Font.font(fontSize));
        this.arrow.setLenght(lenght);
        this.arrow.update();
        requestLayout();
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public String getText(){
        return this.text.getText();
    }

    public Arrow getArrow(){
        return arrow;
    }
}

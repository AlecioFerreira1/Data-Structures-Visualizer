package com.data_structures_visualizer.visual.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class ExplanationTextRect extends StackPane {
    private final Rectangle background;
    private final TextFlow textFlow;

    public ExplanationTextRect(double width, double height) {
        background = new Rectangle();
        background.setFill(Color.rgb(200, 200, 200));
        background.setStroke(Color.BLACK);
        background.setArcWidth(10);
        background.setArcHeight(10);

        textFlow = new TextFlow();
        textFlow.setPadding(new Insets(8));
        textFlow.setPrefWidth(width - 16);

        getChildren().addAll(background, textFlow);
        setPrefSize(width, height);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double w = getWidth();
        double h = getHeight();

        background.setWidth(w);
        background.setHeight(h);
        textFlow.prefWidthProperty().bind(widthProperty().subtract(5));
    }

    public Rectangle getRect(){
        return background;
    }

    public void setContent(Text... texts) {
        clear();
        textFlow.getChildren().setAll(texts);
    }

    public void clear(){
        textFlow.getChildren().clear();
    }
}

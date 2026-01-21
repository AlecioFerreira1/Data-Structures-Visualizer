package com.data_structures_visualizer.visual.animation;

import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public final class NodeAnimator {
    public static Animation highlight(Rectangle rect, int speedMillis, Color highlightColor){
        Color base = (Color) rect.getFill();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rect.fillProperty(), base) 
            ),
            new KeyFrame(Duration.millis(speedMillis),
                new KeyValue(rect.fillProperty(), highlightColor) 
            ),
            new KeyFrame(Duration.millis(speedMillis * 2),
                new KeyValue(rect.fillProperty(), base) 
            )
        );

        timeline.setCycleCount(1);
        return timeline;
    }    

    public static Animation animateMove(VisualNode node, double x, double y, double seconds){
        TranslateTransition tt = new TranslateTransition(Duration.seconds(seconds), node);
        tt.setToX(x);
        tt.setToY(y);
        return tt;
    }

    public static ParallelTransition emergeEffect(VisualNode node, double seconds, boolean spawn){
        ScaleTransition scaleTransition = new ScaleTransition(
            Duration.seconds(seconds), node
        );

        double minScale = 0.0001;
        double from = spawn ? minScale : 1;
        double to = spawn ? 1 : minScale;

        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setToX(to);
        scaleTransition.setToY(to);
        scaleTransition.setInterpolator(spawn ? Interpolator.EASE_OUT : Interpolator.EASE_IN);

        FadeTransition fadeTransition = AnimationUtils.fadeTransition(node, seconds, !spawn);

        return new ParallelTransition(scaleTransition, fadeTransition);
    }

    public static Animation animateFill(
        Rectangle rect, Color fromColor, Color toColor, int durationMillis, boolean repeat
    ){
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rect.fillProperty(), fromColor)
            ),
            new KeyFrame(Duration.millis(durationMillis),
                new KeyValue(rect.fillProperty(), toColor)
            )
        );

        if(repeat){
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);
        } 
    
        else{
            timeline.setCycleCount(1);
        }

        return timeline;
    }

    public static Animation pulseHighlight(Rectangle rect, Color highlightColor, int pulseMillis, int totalMillis){
        Color base = (Color) rect.getFill();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(rect.fillProperty(), base)
            ),
            new KeyFrame(Duration.millis(pulseMillis),
                new KeyValue(rect.fillProperty(), highlightColor)
            )
        );

        timeline.setAutoReverse(true);

        int cycles = Math.max(1, totalMillis / (pulseMillis * 2));

        if(cycles % 2 != 0)
            cycles++;

        timeline.setCycleCount(cycles);

        timeline.setOnFinished(e -> rect.setStroke(base));

        return timeline;
    }

    public static Animation animateFall(VisualNode node, double offsetY, double durationSeconds){
        TranslateTransition fall = new TranslateTransition(Duration.seconds(durationSeconds), node);

        fall.setFromY(node.getTranslateY());
        fall.setToY(node.getTranslateY() + offsetY);
        fall.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition settle = new TranslateTransition(Duration.seconds(0.08), node);

        settle.setFromY(node.getTranslateY() + offsetY);
        settle.setToY(node.getTranslateY() + offsetY + 3);
        settle.setAutoReverse(true);
        settle.setCycleCount(2);
        
        return new SequentialTransition(fall, settle);
    }
    

    //PODERÁ SER UTILIZADO EM VERSÕES FUTURAS
    // 
    // public static Animation animateCurvePath(
    //     VisualNode node, double offsetX, double offsetY, double arcHeight, double durationSeconds
    // ){
    //     Path path = new Path();

    //     path.getElements().add(new MoveTo(0, 0));

    //     path.getElements().add(new QuadCurveTo(
    //         offsetX / 2, -arcHeight,
    //         offsetX, offsetY
    //     ));

    //     PathTransition pathTransition = new PathTransition(Duration.seconds(durationSeconds), path, node);
    //     pathTransition.setInterpolator(Interpolator.EASE_OUT);

    //     ScaleTransition scale = new ScaleTransition(Duration.seconds(0.2), node);
    //     scale.setFromX(0.8);
    //     scale.setFromY(0.8);
    //     scale.setToX(1);
    //     scale.setToY(1);

    //     FadeTransition fade = new FadeTransition(Duration.seconds(0.2), node);
    //     fade.setFromValue(0.7);
    //     fade.setToValue(1);

    //     return new ParallelTransition(pathTransition, scale, fade);
    // }
}

package com.data_structures_visualizer.visual.operations.list.common;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.visual.operations.Operation;
import com.data_structures_visualizer.visual.ui.ArrowLabel;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public final class FixArrowLabelsPosOperation {
    private final ArrowLabel headLabel; 
    private final ArrowLabel tailLabel; 
    private final double xOffset; 
    private final int pos; 
    private final int listInitialSize;

    public FixArrowLabelsPosOperation(
        ArrowLabel headLabel, ArrowLabel tailLabel, double xOffset, int pos, int listInitialSize
    ){
        this.headLabel = headLabel;
        this.tailLabel = tailLabel;
        this.xOffset = xOffset;
        this.pos = pos;
        this.listInitialSize = listInitialSize;
    }

    public void build(AnimationTimeLine timeLine, boolean shiftRight, Operation op){
        TranslateTransition translateHead = new TranslateTransition(
            Duration.seconds(ListVisualizerConfig.translateDuration / 3), headLabel  
        );

        TranslateTransition translateTail = new TranslateTransition(
            Duration.seconds(ListVisualizerConfig.translateDuration / 3), tailLabel  
        );

        TranslateTransition undoTranslateHead = new TranslateTransition(
            Duration.seconds(ListVisualizerConfig.translateDuration / 3), headLabel  
        );

        TranslateTransition undoTranslateTail = new TranslateTransition(
            Duration.seconds(ListVisualizerConfig.translateDuration / 3), tailLabel  
        );

        double i = shiftRight ? 1 : -1;

        translateHead.setByX(-xOffset * i);
        translateTail.setByX(xOffset * i);
        undoTranslateHead.setByX(xOffset * i);
        undoTranslateTail.setByX(-xOffset * i);

        if(pos == 0 && op == Operation.INSERT){
            timeLine.addStep(new Step(
                () -> translateHead,
                () -> undoTranslateHead
            ));

            return;
        }

        if(pos == listInitialSize && op == Operation.INSERT || 
          (pos == listInitialSize - 1 && op == Operation.DELETE)
        ){
            timeLine.addStep(new Step(
                () -> translateTail,
                () -> undoTranslateTail
            ));
        }
    }
}

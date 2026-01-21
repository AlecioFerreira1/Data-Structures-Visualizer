package com.data_structures_visualizer.visual.operations.stack;

import java.util.ArrayList;

import com.data_structures_visualizer.config.StackVisualizerConfig;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public final class StackOperationsUtils {
    public static double calculateYoffset(ArrayList<VisualNode> nodes, AnchorPane visualization_area){
        VisualNode node = nodes.get(nodes.size() - 1);

        double initialHeight = 0.8 * visualization_area.getHeight();

        double expectedHeight = initialHeight - ((1 + StackVisualizerConfig.spacingBetweenNodes)
                * node.getRect().getHeight() * (nodes.size() - 1));

        return Math.abs(node.getBoundsInParent().getHeight() - expectedHeight);   
    }

    public static Animation translateTopLabel(ArrowLabel topLabel, double yOffset){
        TranslateTransition tt = new TranslateTransition(
            Duration.seconds(StackVisualizerConfig.translateDuration / 3), topLabel
        );

        double currentY = topLabel.getTranslateY();

        tt.setFromY(currentY);
        tt.setToY(currentY + yOffset);

        return tt;
    }
}

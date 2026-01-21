package com.data_structures_visualizer.visual.operations.stack;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.StackVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.color.Colors;
import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.context.stack.StackContext;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.StackBase;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class PopOperation {
    private final StackContext context;
    private final ArrayList<VisualNode> nodes; 
    private final AnchorPane visualization_area;
    private final ArrowLabel topLabel;
    private final Stack<VisualNode> removedNodes = new Stack<VisualNode>(null);

    public PopOperation(
        StackContext context, AnchorPane visualization_area, ArrowLabel topLabel,
        ArrayList<VisualNode> nodes, StackBase stackBase
    ){
        this.context = context;
        this.nodes = nodes;
        this.visualization_area = visualization_area;
        this.topLabel = topLabel;
    }

    public void build(AnimationTimeLine timeLine){
        addHighlightTopStep(timeLine);
        addPopAnimationStep(timeLine);
        addUpdateTopStep(timeLine);
        addRemoveNodeStep(timeLine);
    } 

    private void addHighlightTopStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("Desempilhando um elemento da pilha.")
            )
        );
        
        timeLine.addStep(new Step(
            () -> {
                Rectangle rect = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    rect, (Color) rect.getFill(), Colors.remove, 
                    (int) (StackVisualizerConfig.speedVisualization * 1000), false
                );
            }, 
            () -> {
                Rectangle rect = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    rect, (Color) rect.getFill(), Colors.node, 
                    (int) (StackVisualizerConfig.speedVisualization * 1000), false
                );
            }
        ));
    }

    private void addPopAnimationStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("Desempilhando o {remove:topo}.")
            )
        );

        timeLine.addStep(new Step(
            () -> {
                VisualNode node = nodes.get(nodes.size() - 1);
                
                return NodeAnimator.animateFall(
                    node, -StackOperationsUtils.calculateYoffset(nodes, visualization_area), 
                    StackVisualizerConfig.translateDuration
                );
            },
            () -> {
                VisualNode node = nodes.get(nodes.size() - 1);

                return NodeAnimator.animateFall(
                    node, StackOperationsUtils.calculateYoffset(nodes, visualization_area), 
                    StackVisualizerConfig.translateDuration
                );
            }
        ));
    }

    private void addRemoveNodeStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("Removendo o {remove:nó} da pilha.")
            )
        );

        double speed = ListVisualizerConfig.speedVisualization;
        int value = Integer.parseInt(nodes.get(nodes.size() - 1).getText());

        timeLine.addStep(new Step(
            () -> {
                VisualNode node = nodes.get(nodes.size() - 1);
                Animation animation = NodeAnimator.emergeEffect(node, 3 * speed, false);

                context.getStack().pop();
                nodes.remove(nodes.size() - 1);
                removedNodes.push(node);

                animation.setOnFinished(e -> {
                    visualization_area.getChildren().remove(node);
                });

                return animation;
            }, 
            () ->{
                VisualNode node = removedNodes.pop();

                context.getStack().push(value);
                nodes.add(node); 
                visualization_area.getChildren().add(node);

                return NodeAnimator.emergeEffect(node, 3 * speed, true);
            }
        ));
    }

    private void addUpdateTopStep(AnimationTimeLine timeLine){
        if(nodes.size() <= 1) return;

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("O topo da pilha passa a ser o nó {node:nó %s}.", 
                nodes.get(nodes.size() - 2).getText())
            )
        );

        double yOffset = context.getNodeWidth() * (1.0 + StackVisualizerConfig.spacingBetweenNodes);

        timeLine.addStep(new Step(
            () -> StackOperationsUtils.translateTopLabel(topLabel, yOffset),
            () -> StackOperationsUtils.translateTopLabel(topLabel, -yOffset)
        ));
    }
}

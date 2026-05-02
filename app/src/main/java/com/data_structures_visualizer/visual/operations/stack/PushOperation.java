package com.data_structures_visualizer.visual.operations.stack;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.StackVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.color.Colors;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.context.stack.StackContext;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.StackBase;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class PushOperation {
    private final StackContext context; 
    private final ArrayList<VisualNode> nodes; 
    private final AnchorPane visualization_area;
    private final ArrowLabel topLabel;
    private final StackBase stackBase;
    final double StartHeightRatio = 1.11; 

    public PushOperation(
        StackContext context, AnchorPane visualization_area, ArrowLabel topLabel,
        ArrayList<VisualNode> nodes, StackBase stackBase
    ){
        this.context = context;
        this.nodes = nodes;
        this.visualization_area = visualization_area;
        this.topLabel = topLabel;
        this.stackBase = stackBase;
    }

    public void build(AnimationTimeLine timeLine){
        addCreateNodeStep(timeLine);
        addPushAnimationStep(timeLine);
        addUpdateTopStep(timeLine);
        addFinalStep(timeLine);
    }
    
    private void addCreateNodeStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("Criando o {new_node:nó %d}.", context.getValueToPush())
            )
        );

        timeLine.addStep(new Step(
            () -> createNode(context.getNodeWidth()), 
            () -> undoCreateNode()
        ));   
    }

    private void addPushAnimationStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("Empilhando o {new_node:nó %d}.", context.getValueToPush())
            )
        );

        timeLine.addStep(new Step(
            () -> {
                VisualNode node = nodes.get(nodes.size() - 1);
                
                return NodeAnimator.animateFall(
                    node, StackOperationsUtils.calculateYoffset(nodes, visualization_area), 
                    StackVisualizerConfig.translateDuration
                );
            },
            () -> {
                VisualNode node = nodes.get(nodes.size() - 1);

                return NodeAnimator.animateFall(
                    node, -StackOperationsUtils.calculateYoffset(nodes, visualization_area), 
                    StackVisualizerConfig.translateDuration
                );
            }
        ));
    }

    private void addUpdateTopStep(AnimationTimeLine timeLine){
        if(topLabel == null) return;

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("O topo da pilha agora é o novo {new_node:nó %d}.", 
                context.getValueToPush())
            )
        );

        double yOffset = context.getNodeWidth() * (1.0 + StackVisualizerConfig.spacingBetweenNodes);

         timeLine.addStep(new Step(
            () -> StackOperationsUtils.translateTopLabel(topLabel, -yOffset),
            () -> StackOperationsUtils.translateTopLabel(topLabel, yOffset)
        ));
    }

    private void addFinalStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                String.format("O novo {inserted:nó %d} foi adicionado à pilha.", context.getValueToPush())
            )
        );

        timeLine.addStep(new Step(
            () -> {
                Rectangle rect = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    rect, (Color) rect.getFill(), Colors.node, 
                    (int) (StackVisualizerConfig.speedVisualization * 1000), false
                );
            },
            () -> {
                Rectangle rect = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    rect, (Color) rect.getFill(), Colors.newNode, 
                    (int) (StackVisualizerConfig.speedVisualization * 1000), false
                );
            }
        ));
    }

    private Animation createNode(double size){
        VisualNode node = new VisualNode(size, size, String.valueOf(context.getValueToPush()));

        final double width = visualization_area.getWidth();
        final double height = visualization_area.getHeight();

        nodes.add(node);

        node.setOpacity(0);
        node.getRect().setStrokeWidth(Math.min(width, height) * 0.005);
        node.getRect().setFill(Colors.newNode);
        node.setScaleX(0);
        node.setScaleY(0);

        visualization_area.getChildren().add(node);
        context.getStack().push(context.getValueToPush());

        double leftAnchorValue = (width / 2) - (node.getRect().getWidth() / 2);
        double topAnchorValue = (height / 2) - (StartHeightRatio * (stackBase.getHeight() / 2));

        AnchorPane.setTopAnchor(node, topAnchorValue);
        AnchorPane.setLeftAnchor(node, leftAnchorValue);

        Transition animation = NodeAnimator.emergeEffect(
            node, 3 * ListVisualizerConfig.speedVisualization, true
        );

        animation.setOnFinished(e -> { node.getRect().setStroke(Color.BLACK); } );
        return animation;
    }

    private Animation undoCreateNode(){
        VisualNode node = nodes.get(nodes.size() - 1);

        context.getStack().pop();
        nodes.remove(node);

        Animation animation = NodeAnimator.emergeEffect(
            node, 3 * ListVisualizerConfig.speedVisualization, false
        );

        animation.setOnFinished(e -> visualization_area.getChildren().remove(node));
        return animation;
    }
}
package com.data_structures_visualizer.visual.operations.queue;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.color.Colors;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.visual.animation.ArrowAnimator;
import com.data_structures_visualizer.visual.animation.ArrowAnimator.DrawArrowDirection;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.context.queue.QueueContext;
import com.data_structures_visualizer.visual.operations.Operation;
import com.data_structures_visualizer.visual.operations.list.common.RepositionNodesOperation;
import com.data_structures_visualizer.visual.context.queue.DequeueExecutionContext;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class DequeueOperation {
    private final QueueContext context;
    private final DequeueExecutionContext exec = new DequeueExecutionContext();
    private final AnchorPane visualization_area;
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrowLabel startLabel;
    private final ArrowLabel endLabel;

    public DequeueOperation(
        QueueContext context, AnchorPane visualization_area, ArrayList<VisualNode> nodes, 
        ArrayList<Arrow> arrows, ArrowLabel startLabel, ArrowLabel endLabel
    ){
        this.context = context;
        this.visualization_area = visualization_area;
        this.nodes = nodes;
        this.arrows = arrows;
        this.startLabel = startLabel;
        this.endLabel = endLabel;
    }

    public void build(AnimationTimeLine timeLine){
        addHiglightStartStep(timeLine);
        addDesconnectNodeStep(timeLine);
        addRemoveNodeStep(timeLine);
        addFixLayoutStep(timeLine);
        addNotifyChangesStep(timeLine);
    }

    private void addHiglightStartStep(AnimationTimeLine timeLine){
        String queueStartValue = nodes.get(0).getText();

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
            "Desenfileirando o {highlight_to_remove:(nó " +  queueStartValue + ")}.\n"
        ));

        timeLine.addStep(new Step(
            () -> {
                Rectangle startRectangle = nodes.get(0).getRect(); 

                return NodeAnimator.animateFill(
                    startRectangle, (Color) startRectangle.getFill(), Color.GOLD, 1000, false
                );
            }, 
            () -> {
                Rectangle startRectangle = nodes.get(0).getRect(); 

                return NodeAnimator.animateFill(
                    startRectangle, (Color) startRectangle.getFill(), Colors.node, 1000, false
                );
            }
        ));
    }

    private void addDesconnectNodeStep(AnimationTimeLine timeLine){
        double speed = ListVisualizerConfig.speedVisualization;

        if(arrows.size() <= 1) return;

        String queueStartValue = nodes.get(0).getText();
        String newStartValue = nodes.get(1).getText();
        
        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
            "O início ({highlight_to_remove:nó " +  queueStartValue + "}) deixa de apontar para o " +
            "{node:nó " + newStartValue + "}.\n"
        ));

        timeLine.addStep(new Step(
            () -> {
                Animation removeNextArrows = new SequentialTransition(
                    ArrowAnimator.animateOut(arrows.get(0), speed * 1, DrawArrowDirection.BACKWARD)
                );

                exec.getRemovedArrows().push(arrows.get(0));
                arrows.remove(0);

                removeNextArrows.setOnFinished(e -> {
                    visualization_area.getChildren().remove(arrows.get(0));
                });

                return removeNextArrows;
            },
            () -> undoRemoveArrows()
        ));
    }

    private void addRemoveNodeStep(AnimationTimeLine timeLine){
        double speed = ListVisualizerConfig.speedVisualization;
        String nodeValue = nodes.get(0).getText();

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
            "Removendo o nó {highlight_to_remove:" + nodeValue + "}."
        ));

        timeLine.addStep(new Step(
            () -> {
                VisualNode node = nodes.get(0);
                Animation animation = NodeAnimator.emergeEffect(node, 3 * speed, false);

                context.getQueue().dequeue();
                exec.getRemovedNodes().push(node);
                nodes.remove(node);

                animation.setOnFinished(e -> {
                    visualization_area.getChildren().remove(node);
                });

                return animation;
            }, 
            () -> {
                VisualNode node = exec.getRemovedNodes().pop();

                nodes.add(0, node); 
                visualization_area.getChildren().add(node);
                context.getQueue().enqueue(Integer.parseInt(node.getText()));

                Animation animation = NodeAnimator.emergeEffect(node, 3 * speed, true);

                animation.setOnFinished(e -> {
                    visualization_area.getChildren().add(node);
                });

                return animation;
            }
        ));
    }

    private void addFixLayoutStep(AnimationTimeLine timeLine){
        if(nodes.size() <= 1) return;

        String newStartValue = nodes.get(1).getText();

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
            "O início passa a ser o nó {node: " + newStartValue + "}.\n")
        );

        timeLine.addStep(new Step(
            () -> {
                RepositionNodesOperation repositionNodes = new RepositionNodesOperation(
                    nodes, arrows, null, startLabel, endLabel, 
                    visualization_area, nodes.size(), ListType.SINGLY
                );

                return repositionNodes.build(0, -context.getXoffset(), Operation.DELETE);
            },
            () -> {
                RepositionNodesOperation repositionNodes = new RepositionNodesOperation(
                    nodes, arrows, null, startLabel, endLabel, 
                    visualization_area, nodes.size(), ListType.SINGLY
                );

                return repositionNodes.build(0, context.getXoffset(), Operation.DELETE);
            }
        ));
    }

    private void addNotifyChangesStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
                "O nó {node:" + String.valueOf(nodes.get(0).getText()) + "} foi desenfileirado.\n"
            )
        );

        timeLine.addStep(new Step(
            () -> {
                Rectangle newStartNode = nodes.get(0).getRect();

                return NodeAnimator.animateFill(
                    newStartNode, (Color) newStartNode.getFill(), Colors.node, 1500, false
                );
            },
            () -> {
                Rectangle newStartNode = nodes.get(0).getRect();

                return NodeAnimator.animateFill(
                    newStartNode, (Color) newStartNode.getFill(), Colors.newNode, 1500, false
                );
            }
        ));
    }

    private Animation undoRemoveArrows(){
        double speed = ListVisualizerConfig.speedVisualization;
        Arrow arrow = exec.getRemovedArrows().pop();

        Animation animation = ArrowAnimator.animateIn(arrow, speed, DrawArrowDirection.FORWARD);

        animation.setOnFinished(e -> {
            if(arrow != null){
                arrows.add(0, arrow);
                visualization_area.getChildren().add(arrow);
            }
        });
                
        return animation;
    }
}
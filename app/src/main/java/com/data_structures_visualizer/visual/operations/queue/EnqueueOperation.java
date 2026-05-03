package com.data_structures_visualizer.visual.operations.queue;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.QueueVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.color.Colors;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.visual.animation.ArrowAnimator;
import com.data_structures_visualizer.visual.animation.ArrowAnimator.DrawArrowDirection;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.context.queue.QueueContext;
import com.data_structures_visualizer.visual.context.queue.EnqueueExecutionContext;
import com.data_structures_visualizer.visual.operations.Operation;
import com.data_structures_visualizer.visual.operations.list.common.FixArrowLabelsPosOperation;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class EnqueueOperation {
    private final QueueContext context;
    private final EnqueueExecutionContext exec = new EnqueueExecutionContext();
    private final AnchorPane visualization_area;
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrowLabel startLabel;
    private final ArrowLabel endLabel;
    private VisualNode newNode;

    public EnqueueOperation(
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
        addCreateNodeStep(timeLine);  
        addTranslateNodeStep(timeLine);
        addConnectNodeStep(timeLine);
        addNotifyChangesStep(timeLine);
        addFixLabelsStep(timeLine);
    }

    private void addCreateNodeStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Criando o nó {new_node:" + context.getValueToEnqueue() + "}."
            )
        );

        timeLine.addStep(new Step(
            () -> createNode(context.getNodeWidth(), context.getValueToEnqueue()),
            () -> undoCreateNode()
        ));
    }

    private void addTranslateNodeStep(AnimationTimeLine timeLine){
        final double height = visualization_area.getHeight();

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Criando o nó {new_node:" + context.getValueToEnqueue() + "}."
            )
        );

        timeLine.addStep(new Step(
            () -> NodeAnimator.animateMove(
                newNode, 0, (height / 2) - (context.getNodeWidth() / 2) - (0.2 * height), 
                QueueVisualizerConfig.translateDuration
            ),
            () -> NodeAnimator.animateMove(
                newNode, 0, -((height / 2) - (context.getNodeWidth() / 2) - (0.2 * height)),
                QueueVisualizerConfig.translateDuration
            )
        ));

        translateNodeToFinalPos(timeLine);
    }

    private void addConnectNodeStep(AnimationTimeLine timeLine){
        if(nodes.isEmpty()) return;

        String lastNode = nodes.get(nodes.size() - 1).getText();

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
                "O fim ({node:nó " + lastNode + "}) passa a apontar para o {new_node:novo} nó {new_node:" + 
                String.valueOf(context.getValueToEnqueue()) + "}.\n" 
            )
        );

        timeLine.addStep(new Step(
            () -> createArrow(QueueVisualizerConfig.spacingBetweenNodes * context.getNodeWidth()), 
            () -> undoCreateArrow()   
        ));
    }

    private void addNotifyChangesStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
                "O nó {node:" + String.valueOf(context.getValueToEnqueue()) + "} foi enfileirado.\n"
            )
        );

        timeLine.addStep(new Step(
            () -> {
                Rectangle newNode = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    newNode, (Color) newNode.getFill(), Colors.node, 1500, false
                );
            },
            () -> {
                Rectangle newNode = nodes.get(nodes.size() - 1).getRect();

                return NodeAnimator.animateFill(
                    newNode, (Color) newNode.getFill(), Colors.newNode, 1500, false
                );
            }
        ));
    }

    private void addFixLabelsStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), "O novo nó se torna o novo fim.")
        );

        new FixArrowLabelsPosOperation(
            startLabel, endLabel, context.getXoffset(), nodes.size(), nodes.size()
        ).build(timeLine, true, Operation.INSERT);
    }

    private Animation createNode(double size, int value){
        newNode = new VisualNode(size, size, String.valueOf(value));

        final double startHeight = 0.2 * visualization_area.getHeight();
        final double width = visualization_area.getWidth();
        final double height = visualization_area.getHeight();
        final double xOffset = 0.03 * width;

        final double leftAnchorValue = xOffset + (width / 2) - (context.getQueueDelimiterWidth() / 2) +
                    ((1 + QueueVisualizerConfig.spacingBetweenNodes) * context.getNodeWidth() * 
                    (QueueVisualizerConfig.queueMaxLimit));

        context.getQueue().enqueue(value);
        nodes.addLast(newNode);

        newNode.getRect().setStrokeWidth(Math.min(width, height) * 0.005);
        newNode.getRect().setFill(Colors.newNode);
        newNode.setOpacity(0);
        newNode.setScaleX(0);
        newNode.setScaleY(0);
        
        visualization_area.getChildren().add(newNode);
        exec.getCreatedNodes().push(newNode);

        AnchorPane.setTopAnchor(newNode, startHeight);
        AnchorPane.setLeftAnchor(newNode, leftAnchorValue);

        SequentialTransition st = new SequentialTransition(
            NodeAnimator.emergeEffect(newNode, 3 * ListVisualizerConfig.speedVisualization, true),
            NodeAnimator.highlight(newNode.getRect(), 700, Color.GOLD)
        );  

        st.setOnFinished(e -> { newNode.getRect().setStroke(Color.BLACK); });

        return st;
    }

    private Animation undoCreateNode(){
        VisualNode node = exec.getCreatedNodes().pop();

        nodes.remove(node);
        context.getQueue().dequeue();

        Animation animation = NodeAnimator.emergeEffect(
            node, 3 * ListVisualizerConfig.speedVisualization, false
        );

        animation.setOnFinished(e -> visualization_area.getChildren().remove(node));
        return animation;
    }

    private void translateNodeToFinalPos(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
                "Enfileirando o nó {new_node:" + String.valueOf(context.getValueToEnqueue()) + "}.\n"
            )
        );

        final double height = visualization_area.getHeight();
        final double width = visualization_area.getWidth();
         
        final double leftAnchorValue = (0.03 * width) + (width / 2) - (context.getQueueDelimiterWidth() / 2) +
                    ((1 + QueueVisualizerConfig.spacingBetweenNodes) * context.getNodeWidth() * 
                    (QueueVisualizerConfig.queueMaxLimit));
        
        final double targetAnchorValue = (width * 0.01) + (width / 2) - (context.getQueueDelimiterWidth() / 2) +
                    ((1 + QueueVisualizerConfig.spacingBetweenNodes) * context.getNodeWidth() * nodes.size());
        
        final double translationVal = Math.abs(leftAnchorValue - targetAnchorValue);

        timeLine.addStep(new Step(
            () -> NodeAnimator.animateMove(
                newNode, -translationVal, ((height / 2) - (context.getNodeWidth() / 2) - (0.2 * height)), 
                QueueVisualizerConfig.translateDuration
            ), 
            () -> NodeAnimator.animateMove(
                newNode, translationVal, -((height / 2) - (context.getNodeWidth() / 2) - (0.2 * height)), 
                QueueVisualizerConfig.translateDuration
            )
        ));
    }

    private Animation createArrow(double lenght){
        final Arrow arrow = new Arrow(lenght);
        final double height = visualization_area.getHeight();
        final double width = visualization_area.getWidth();
        final double xOffset = 0.01 * width;
        
        arrows.add(arrow);
        visualization_area.getChildren().add(arrow);
        exec.getCreatedArrows().push(arrow);

        double leftAnchorVal = xOffset + (width / 2) - (context.getQueueDelimiterWidth() / 2) + context.getNodeWidth() +
                               ((1 + QueueVisualizerConfig.spacingBetweenNodes) * context.getNodeWidth() * (nodes.size() - 2));

        AnchorPane.setTopAnchor(arrow, (height / 2) - (arrow.getBoundsInParent().getHeight() / 2));
        AnchorPane.setLeftAnchor(arrow, leftAnchorVal);

        return ArrowAnimator.animateIn(arrow, 1, DrawArrowDirection.FORWARD);
    }

    private Animation undoCreateArrow(){
        Arrow arrow = exec.getCreatedArrows().pop();

        arrows.remove(arrow);

        Animation removeArrowNext = ArrowAnimator.animateOut(arrow, 1, DrawArrowDirection.BACKWARD);
        removeArrowNext.setOnFinished(e -> { visualization_area.getChildren().remove(arrow); });

        return removeArrowNext;
    }
}
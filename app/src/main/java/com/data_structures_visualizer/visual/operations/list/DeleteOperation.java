package com.data_structures_visualizer.visual.operations.list;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.visual.animation.AnimationUtils;
import com.data_structures_visualizer.visual.animation.ArrowAnimator;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.animation.ArrowAnimator.DrawArrowDirection;
import com.data_structures_visualizer.visual.context.list.DeleteContext;
import com.data_structures_visualizer.visual.context.list.DeleteExecutionContext;
import com.data_structures_visualizer.visual.operations.Operation;
import com.data_structures_visualizer.visual.operations.list.common.FixArrowLabelsPosOperation;
import com.data_structures_visualizer.visual.operations.list.common.FixCurvedArrowPosOperation;
import com.data_structures_visualizer.visual.operations.list.common.RepositionNodesOperation;
import com.data_structures_visualizer.visual.operations.list.common.TransverseAndHighlightOperation;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.CurvedArrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class DeleteOperation {
    private final DeleteContext context;
    private final DeleteExecutionContext exec = new DeleteExecutionContext();
    private final AnchorPane visualization_area;
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrayList<Arrow> prevArrows;
    private final CurvedArrow curvedArrow;
    private final ArrowLabel headLabel;
    private final ArrowLabel tailLabel;

    public DeleteOperation(
        DeleteContext context, AnchorPane visualization_area, ArrayList<VisualNode> nodes,
        ArrayList<Arrow> arrows, ArrayList<Arrow> prevArrows, CurvedArrow curvedArrow, 
        ArrowLabel headLabel, ArrowLabel tailLabel 
    ){
        this.context = context;
        this.visualization_area = visualization_area;
        this.nodes = nodes;
        this.arrows = arrows;
        this.prevArrows = prevArrows;
        this.curvedArrow = curvedArrow;
        this.headLabel = headLabel;
        this.tailLabel = tailLabel;
    }

    public void build(AnimationTimeLine timeLine){
        addTransversalStep(timeLine);

        if(context.getIndexToRemove() == -1) return;

        addDesconnectNodeStep(timeLine);
        addRemoveNodeStep(timeLine);
        addEstablishConnectionsStep(timeLine);
        addFixLayoutStep(timeLine);
    }

    private void addTransversalStep(AnimationTimeLine timeLine){
        int index = context.getSinglyLinkedList().indexOf(context.getValue());
         
        if(context.removeByIndex()) index = context.getValue();
        
        new TransverseAndHighlightOperation(
            nodes, timeLine, context.getExplanationRepository()
        ).build(index == -1 ? nodes.size() : index, context.getValue());

        context.setIndexToRemove(index);

        Color green = Color.rgb(0, 255, 25);

        if(index != -1){
            timeLine.addStep(new Step(
                () -> applyHighLights(Color.ORANGE, Color.GOLD, Color.BLUE),  
                () -> applyHighLights(green, green, green)
            ));
        }
    }

    private Animation applyHighLights(Color prevRectColor, Color targetRectColor, Color nextRectColor){
        int index = context.getIndexToRemove();

        if(nodes.isEmpty()) return AnimationUtils.emptyAnimation();

        if(index == nodes.size())
            index--;

        final Rectangle targetRect = nodes.get(index < 0 ? 0 : index).getRect();
        Rectangle prevRect = nodes.get(index - 1 >= 0 ? index - 1 : 0).getRect();
        Rectangle nextRect = nodes.get(index + 1 < nodes.size() ? index + 1 : 0).getRect();
        double speed = ListVisualizerConfig.speedVisualization;
        
        return new SequentialTransition(
            NodeAnimator.animateFill(
                targetRect, (Color) targetRect.getFill(), targetRectColor, (int) (700 * speed), false
            ),
            index - 1 >= 0 ? NodeAnimator.animateFill(
                prevRect, (Color) prevRect.getFill(), prevRectColor, (int) (700 * speed), false
            ) : AnimationUtils.emptyAnimation(),
            index + 1 < nodes.size() ? NodeAnimator.animateFill(
                nextRect, (Color) nextRect.getFill(), nextRectColor, (int) (700 * speed), false
            ) : AnimationUtils.emptyAnimation()
        );
    }

    private void addDesconnectNodeStep(AnimationTimeLine timeLine){
        desconnectPrevArrows(timeLine);
        desconnectNextArrows(timeLine);
    }

    private void desconnectPrevArrows(AnimationTimeLine timeLine){
        int index = context.getIndexToRemove();
        double speed = ListVisualizerConfig.speedVisualization;

        if(index - 1 < 0) return;

        timeLine.addStep(new Step(
            () -> {
                Animation animation = new SequentialTransition(
                    ArrowAnimator.animateOut(arrows.get(index - 1), speed * 1, DrawArrowDirection.BACKWARD), 
                    context.getListType() == ListType.DOUBLY ?
                    ArrowAnimator.animateOut(prevArrows.get(index - 1), speed * 1, DrawArrowDirection.FORWARD) :
                    AnimationUtils.emptyAnimation()
                );

                if(index == nodes.size() - 1){
                    exec.getRemovedArrows().push(arrows.get(index - 1));

                    animation.setOnFinished(e -> {
                        visualization_area.getChildren().remove(arrows.get(index - 1));

                        if(context.getListType() == ListType.DOUBLY)
                            visualization_area.getChildren().remove(prevArrows.get(index - 1));
                    });

                    arrows.remove(index - 1);

                    if(context.getListType() == ListType.DOUBLY){
                        exec.getRemovedPrevArrows().push(prevArrows.get(index - 1));
                        prevArrows.remove(index - 1);
                    }
                }

                return animation;
            },
            () -> {
                if(index == nodes.size() - 1) 
                    return undoRemoveArrows();

                return new SequentialTransition(
                    context.getListType() == ListType.DOUBLY ?
                    ArrowAnimator.animateIn(prevArrows.get(index - 1), speed * 1, DrawArrowDirection.BACKWARD) :
                        AnimationUtils.emptyAnimation(),
                    ArrowAnimator.animateIn(arrows.get(index - 1), speed * 1, DrawArrowDirection.FORWARD)
                );
            }
        ));
    }

    private void desconnectNextArrows(AnimationTimeLine timeLine){
        int index = context.getIndexToRemove();
        double speed = ListVisualizerConfig.speedVisualization;

        if(index >= arrows.size()) return;

        timeLine.addStep(new Step(
            () -> {
                Animation removeNextArrows = AnimationUtils.emptyAnimation();

                removeNextArrows = new SequentialTransition(
                    ArrowAnimator.animateOut(arrows.get(index), speed * 1, DrawArrowDirection.BACKWARD), 
                    context.getListType() == ListType.DOUBLY ?
                    ArrowAnimator.animateOut(prevArrows.get(index), speed * 1, DrawArrowDirection.BACKWARD) :
                    AnimationUtils.emptyAnimation()
                );

                exec.getRemovedArrows().push(arrows.get(index));

                removeNextArrows.setOnFinished(e -> {
                    visualization_area.getChildren().remove(arrows.get(index));
        
                    if(context.getListType() == ListType.DOUBLY){
                        visualization_area.getChildren().remove(prevArrows.get(index));
                    }
                });

                arrows.remove(index);

                if(context.getListType() == ListType.DOUBLY){
                    exec.getRemovedPrevArrows().push(prevArrows.get(index));
                    prevArrows.remove(index);
                }

                return removeNextArrows;
            },
            () -> undoRemoveArrows()
        ));
    }

    private Animation undoRemoveArrows(){
        double speed = ListVisualizerConfig.speedVisualization;
        int index = context.getIndexToRemove();

        Arrow arrow = exec.getRemovedArrows().pop();
        Arrow prevArrow = exec.getRemovedPrevArrows().pop();

        Animation animation = new SequentialTransition(
            ArrowAnimator.animateIn(arrow, speed * 1, DrawArrowDirection.FORWARD), 
            context.getListType() == ListType.DOUBLY ?
            ArrowAnimator.animateIn(prevArrow, speed * 1, DrawArrowDirection.BACKWARD) :
            AnimationUtils.emptyAnimation()
        );

        animation.setOnFinished(e -> {
            if(prevArrow != null){
                prevArrows.add(index, prevArrow);
                visualization_area.getChildren().add(prevArrow);
            }

            if(arrow != null){
                arrows.add(index, arrow);
                visualization_area.getChildren().add(arrow);
            }
        });
                
        return animation;
    }

    private void addRemoveNodeStep(AnimationTimeLine timeLine){
        double speed = ListVisualizerConfig.speedVisualization;

        timeLine.addStep(new Step(
            () -> {
                VisualNode node = nodes.get(context.getIndexToRemove());
                Animation animation = NodeAnimator.emergeEffect(node, 3 * speed, false);

                context.getSinglyLinkedList().removeItem(context.getIndexToRemove());
                context.getDoublyLikedList().removeItem(context.getIndexToRemove());
                context.getCircularLinkedList().removeItem(context.getIndexToRemove());

                exec.getRemovedNodes().push(node);
                nodes.remove(node);

                animation.setOnFinished(e -> {
                    visualization_area.getChildren().remove(node);
                });

                return animation;
            }, 
            () ->{
                VisualNode node = exec.getRemovedNodes().pop();

                nodes.add(context.getIndexToRemove(), node); 
                visualization_area.getChildren().add(node);

                Integer value = Integer.parseInt(node.getText());

                context.getSinglyLinkedList().insertOnPos(value, context.getIndexToRemove());
                context.getDoublyLikedList().insertOnPos(value, context.getIndexToRemove());
                context.getCircularLinkedList().insertOnPos(value, context.getIndexToRemove());

                return NodeAnimator.emergeEffect(node, 3 * speed, true);
            }
        ));
    }

    private void addEstablishConnectionsStep(AnimationTimeLine timeLine){
        double speed = ListVisualizerConfig.speedVisualization;
        Color green = Color.rgb(0, 255, 25);
        
        timeLine.addStep(new Step(
            () -> {
                int index = Math.max(context.getIndexToRemove() - 1, 0);

                RepositionNodesOperation repositionNodes = new RepositionNodesOperation(
                    nodes, arrows, prevArrows, headLabel, tailLabel, visualization_area, 
                    nodes.size(), context.getListType()
                );

                if(index < arrows.size() && context.getIndexToRemove() != 0){
                    return new SequentialTransition(
                        repositionNodes.build(context.getIndexToRemove(), -context.getxOffset(), Operation.DELETE),
                        ArrowAnimator.animateIn(arrows.get(index), 1 * speed, DrawArrowDirection.FORWARD),
                        context.getListType() == ListType.DOUBLY ? 
                        ArrowAnimator.animateIn(prevArrows.get(index), 1 * speed, DrawArrowDirection.FORWARD) 
                        : AnimationUtils.emptyAnimation(),
                        applyHighLights(green, green, green)
                    );
                }

                return new SequentialTransition(
                    repositionNodes.build(context.getIndexToRemove(), -context.getxOffset(), Operation.DELETE),
                    applyHighLights(green, green, green)
                );
            },
            () -> {
                int index = Math.max(context.getIndexToRemove() - 1, 0);

                RepositionNodesOperation repositionNodes = new RepositionNodesOperation(
                    nodes, arrows, prevArrows, headLabel, tailLabel, visualization_area, 
                    nodes.size(), context.getListType()
                );
                
                if(index < arrows.size() && context.getIndexToRemove() != 0){
                    return new SequentialTransition(
                        applyHighLights(Color.ORANGE, Color.GOLD, Color.BLUE),
                        context.getListType() == ListType.DOUBLY ? 
                        ArrowAnimator.animateOut(prevArrows.get(index), 1 * speed, DrawArrowDirection.BACKWARD) 
                        : AnimationUtils.emptyAnimation(),
                        ArrowAnimator.animateOut(arrows.get(index), 1 * speed, DrawArrowDirection.BACKWARD),
                        repositionNodes.build(context.getIndexToRemove(), context.getxOffset(), Operation.DELETE)
                    );
                }

                return new SequentialTransition(
                    applyHighLights(Color.ORANGE, Color.GOLD, Color.BLUE),
                    repositionNodes.build(context.getIndexToRemove(), context.getxOffset(), Operation.DELETE)
                );
            }
        ));
    }

    private void addFixLayoutStep(AnimationTimeLine timeLine){
        FixArrowLabelsPosOperation fixLayout = new FixArrowLabelsPosOperation(
            headLabel, tailLabel, context.getxOffset(), context.getIndexToRemove(), nodes.size()
        );

        fixLayout.build(timeLine, false, Operation.DELETE);

        FixCurvedArrowPosOperation fixCurvedArrow = new FixCurvedArrowPosOperation(
            nodes, curvedArrow, visualization_area, context.getxOffset(), context.getListType()
        );

        fixCurvedArrow.build(timeLine);
    }
}
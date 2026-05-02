package com.data_structures_visualizer.visual.operations.list;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.text.ExplanationText;
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
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.CurvedArrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
        addNotifyChangesStep(timeLine);
    }

    private void addTransversalStep(AnimationTimeLine timeLine){
        int index = context.getSinglyLinkedList().indexOf(context.getValue());
         
        if(context.removeByIndex()) index = context.getValue();

        context.setIndexToRemove(index);

        buildExplanationBeforeTransversalStep(timeLine.size());
        
        new SearchOperation(
            context.getSinglyLinkedList(), nodes, 
            !context.removeByIndex() ? context.getValue() : context.getSinglyLinkedList().get(index), 
            context.getExplanationRepository()
        ).build(timeLine);

        buildExplanationAfterTransversalStep(timeLine.size());

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

        final int listLength = context.getSinglyLinkedList().lenght();
        final Rectangle targetRect = nodes.get(index >= listLength ? 0 : index).getRect();
        Rectangle prevRect = nodes.get(index - 1 >= 0 ? index - 1 : 0).getRect();
        Rectangle nextRect = nodes.get(index + 1 < nodes.size() ? index + 1 : 0).getRect();
        double speed = ListVisualizerConfig.speedVisualization;
        
        return new SequentialTransition(
            index < listLength ? NodeAnimator.animateFill(
                targetRect, (Color) targetRect.getFill(), targetRectColor, (int) (700 * speed), false
            ) : AnimationUtils.emptyAnimation(),
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

        buildExplanationForDesconnectArrows(timeLine.size(), true);

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
                if(index == nodes.size() - 1) return undoRemoveArrows();

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

        buildExplanationForDesconnectArrows(timeLine.size(), false);

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
        String nodeValue = context.getSinglyLinkedList().get(context.getIndexToRemove()).toString();

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
            "Removendo o nó {highlight_to_remove:" + nodeValue + "}."
        ));

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
            () -> {
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
        final double speed = ListVisualizerConfig.speedVisualization;
        final Color green = Color.rgb(0, 255, 25);

        buildExplanationForEstablishConnections(timeLine.size());
        
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
        buildExplanationForFixlayout(timeLine.size());

        new FixArrowLabelsPosOperation(
            headLabel, tailLabel, context.getxOffset(), context.getIndexToRemove(), nodes.size()
        ).build(timeLine, false, Operation.DELETE);

        if(context.getCircularLinkedList().lenght() > 1){
            if(context.getIndexToRemove() == context.getCircularLinkedList().lenght() - 1 || context.getIndexToRemove() == 0){
                context.getExplanationRepository().addExplanation(timeLine.size(), 
                    new ExplanationText(timeLine.size(), 
                        context.getListType() == ListType.CIRCULAR ? 
                        "A cauda volta a apontar para a cabeça." : ""
                    )
                );
            }

            new FixCurvedArrowPosOperation(
                nodes, curvedArrow, visualization_area, context.getxOffset(), context.getListType()
            ).build(timeLine);
        }
    }

    private void buildExplanationBeforeTransversalStep(int timeLineSize){
        if(context.getIndexToRemove() == -1) return;

        if(context.getIndexToRemove() != 0){ 
            int valueToDelete = context.getSinglyLinkedList().get(context.getIndexToRemove());

            context.getExplanationRepository().addExplanation(0, 
                new ExplanationText(0, 
                    "Primeiro, é feita uma busca da nó a ser removido: valor {node:" 
                    + String.valueOf(valueToDelete) + "}.\n"
                )
            );   
        }

        else{
            context.getExplanationRepository().addExplanation(0, 
                new ExplanationText(0, "Remoção na cabeça.\n")
            );
        }
    }

    private void buildExplanationAfterTransversalStep(int timeLineSize){
        if(context.getIndexToRemove() == -1) return;

        if(context.getIndexToRemove() == nodes.size() - 1){
            context.getExplanationRepository().addExplanation(timeLineSize, 
                new ExplanationText(timeLineSize, "Remoção na cauda.\n")
            );
        }

        if(context.getIndexToRemove() != 0){
            context.getExplanationRepository().addExplanation(timeLineSize, 
                new ExplanationText(timeLineSize, 
                    "Nó {prev:anterior} (Valor {prev:" + nodes.get(context.getIndexToRemove() - 1).getText() + "})."
                )
            );
        }

        if(context.getIndexToRemove() + 1 < nodes.size()){
            context.getExplanationRepository().addExplanation(timeLineSize, 
                new ExplanationText(timeLineSize, 
                    "{next:Próximo } nó: (Valor {next:" + 
                    String.valueOf(nodes.get(context.getIndexToRemove() + 1).getText()) + "})." 
                )
            );
        }
    }

    private void buildExplanationForDesconnectArrows(int timeLineSize, boolean prev){
        int index = context.getIndexToRemove();
        String nodeValue = "";
        String explanation = "";
    
        if(prev){
            String prevNodeValue = context.getSinglyLinkedList().get(index - 1).toString();
            nodeValue = context.getSinglyLinkedList().get(index).toString();
            explanation = "O ponteiro prox. do nó {prev: " + prevNodeValue + "}" +
                    " deixa de apontar para o nó {highlight_to_remove:" + nodeValue + "}";

            if(context.getListType() == ListType.DOUBLY){
                explanation += " e o ponteiro ant. do nó {highlight_to_remove:" + nodeValue + "} "
                            + "deixa de apontar para o nó {prev:" + prevNodeValue + "}";
            }
        }

        else{
            String nextNodeValue = context.getSinglyLinkedList().get(index + 1).toString();
            nodeValue = context.getSinglyLinkedList().get(index).toString();
            explanation = "O ponteiro prox. do nó {highlight_to_remove: " + nodeValue + "}" +
                    " deixa de apontar para o nó {next:" + nextNodeValue + "}";

            if(context.getListType() == ListType.DOUBLY){
                explanation += " e o ponteiro ant. do nó {next:" + nextNodeValue + "} "
                            + "deixa de apontar para o nó {highlight_to_remove:" + nodeValue + "}";
            }
        }

        context.getExplanationRepository().addExplanation(timeLineSize, 
            new ExplanationText(timeLineSize, explanation)
        );

        explanation += ".";
    }

    private void buildExplanationForEstablishConnections(int timeLineSize){
        String explanation = "";
        int index = context.getIndexToRemove();

        if(index != 0 && index != context.getSinglyLinkedList().lenght() - 1){
            String prevNodeValue = context.getSinglyLinkedList().get(index - 1).toString();
            String nextNodeValue = context.getSinglyLinkedList().get(index + 1).toString();

            explanation += "O ponteiro prox. do nó {prev:" + prevNodeValue + "} passa a apontar para o nó "
                        +  "{next:" + nextNodeValue + "}";

            if(context.getListType() == ListType.DOUBLY){
                explanation += "e o ponteiro ant. do nó {next:" + nextNodeValue + "} passa a apontar para o nó "
                        +  "{prev:" + prevNodeValue + "}";
            }

            explanation += ".";
        }

        context.getExplanationRepository().addExplanation(timeLineSize, 
            new ExplanationText(timeLineSize, explanation)
        );
    }

    private void buildExplanationForFixlayout(int timeLineSize){
        String explanation = "";
        int index = context.getIndexToRemove();

        if(index == 0 && context.getSinglyLinkedList().lenght() > 1){
            explanation += "O nó {node:" + context.getSinglyLinkedList().get(1).toString() + "} "
                        + "passa a ser a nova cabeça.";
        }

        else if(index == context.getSinglyLinkedList().lenght() - 1 && context.getSinglyLinkedList().lenght() > 1){
            explanation += "O nó {node:" + context.getSinglyLinkedList().get(index).toString() + "} "
                        +  "passa a ser a nova cauda.";
        }

        context.getExplanationRepository().addExplanation(timeLineSize, 
            new ExplanationText(timeLineSize, explanation)
        );
    }

    private void addNotifyChangesStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Nó ({remove:" + context.getValue() + "}) removido."
            )
        );

        timeLine.addStep(new Step(
            () -> new PauseTransition(Duration.seconds(2)), 
            () -> new PauseTransition(Duration.seconds(2))
        ));
    }
}  
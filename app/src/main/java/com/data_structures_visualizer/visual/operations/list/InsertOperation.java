package com.data_structures_visualizer.visual.operations.list;

import java.util.ArrayList;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.controllers.ListVisualizerController.ListType;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.animation.Step;
import com.data_structures_visualizer.models.color.Colors;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.visual.animation.AnimationUtils;
import com.data_structures_visualizer.visual.animation.ArrowAnimator;
import com.data_structures_visualizer.visual.animation.NodeAnimator;
import com.data_structures_visualizer.visual.animation.ArrowAnimator.DrawArrowDirection;
import com.data_structures_visualizer.visual.context.list.InsertContext;
import com.data_structures_visualizer.visual.context.list.InsertExecutionContext;
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
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class InsertOperation {
    private final InsertContext context;
    private final InsertExecutionContext exec = new InsertExecutionContext();
    private final AnchorPane visualization_area;
    private final ArrayList<VisualNode> nodes;
    private final ArrayList<Arrow> arrows;
    private final ArrayList<Arrow> prevArrows;
    private final CurvedArrow curvedArrow;
    private VisualNode newNode;
    private final ArrowLabel headLabel;
    private final ArrowLabel tailLabel;

    public InsertOperation(
        InsertContext context, AnchorPane visualization_area, ArrayList<VisualNode> nodes,
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
        addRemoveArrowSteps(timeLine);
        addCreateNodeStep(timeLine);
        addTranslateNodeStep(timeLine);
        addStepToConnectPrevArrow(timeLine);
        addStepToConnectNextArrow(timeLine);
        addNotifyChangesStep(timeLine);
        addFixLabelsStep(timeLine);
        addStepToMoveCurvedArrowIfNeeded(timeLine);
    }

    private Animation createNode(double size, int value, int pos){
        newNode = new VisualNode(size, size, String.valueOf(value));

        final double startHeight = 0.2 * visualization_area.getHeight();
        final double width = visualization_area.getWidth();
        final double height = visualization_area.getHeight();

        context.getSinglyLinkedList().insertOnPos(value, pos);
        context.getDoublyLikedList().insertOnPos(value, pos);
        context.getCircularLinkedList().insertOnPos(value, pos);

        nodes.add(context.getPos(), newNode);
        newNode.getRect().setStrokeWidth(Math.min(width, height) * 0.005);
        newNode.getRect().setFill(Colors.newNode);
        newNode.setOpacity(0);
        newNode.setScaleX(0);
        newNode.setScaleY(0);
        
        visualization_area.getChildren().add(newNode);
        exec.getCreatedNodes().push(newNode);

        AnchorPane.setTopAnchor(newNode, startHeight);
        AnchorPane.setLeftAnchor(
            newNode, (ListVisualizerConfig.xOffsetForNodes * width) + 
            (size * (1.0 + ListVisualizerConfig.spacingBetweenNodes) * pos)
        );

        SequentialTransition st = new SequentialTransition(
            NodeAnimator.emergeEffect(newNode, 3 * ListVisualizerConfig.speedVisualization, true),
            NodeAnimator.highlight(newNode.getRect(), pos, Colors.node)
        );  

        st.setOnFinished(e -> { newNode.getRect().setStroke(Color.BLACK); });

        return st;
    }

    private Animation undoCreateNode(){
        VisualNode node = exec.getCreatedNodes().pop();

        nodes.remove(node);

        context.getSinglyLinkedList().removeItem(context.getPos());
        context.getDoublyLikedList().removeItem(context.getPos());
        context.getCircularLinkedList().removeItem(context.getPos());

        Animation animation = NodeAnimator.emergeEffect(
            node, 3 * ListVisualizerConfig.speedVisualization, false
        );

        animation.setOnFinished(e -> visualization_area.getChildren().remove(node));
        return animation;
    }

    private Animation createArrows(double lenght, int pos){
        final Arrow arrow = new Arrow(lenght);
        final double height = visualization_area.getHeight();
        final double width = visualization_area.getWidth();
        
        arrows.add(arrow);
        visualization_area.getChildren().add(arrow);
        exec.getCreatedArrows().push(arrow);

        double leftAnchorVal = ((ListVisualizerConfig.xOffsetForNodes * width) + context.getNodeWidth() + 
                               ((1 + ListVisualizerConfig.spacingBetweenNodes) * context.getNodeWidth()) * pos);

        if(context.getListType() == ListType.DOUBLY){
            Arrow prevArrow = new Arrow(lenght);

            prevArrow.setRotate(180);
            prevArrows.add(prevArrow);

            visualization_area.getChildren().add(prevArrow);
            exec.getCreatedPrevArrows().push(prevArrow);
            
            AnchorPane.setTopAnchor(arrow, (height / 2) - (arrow.getBoundsInParent().getHeight() * 1.25));
            AnchorPane.setLeftAnchor(arrow, leftAnchorVal);

            AnchorPane.setTopAnchor(prevArrow, (height / 2) + (arrow.getBoundsInParent().getHeight() / 2));
            AnchorPane.setLeftAnchor(prevArrow, leftAnchorVal);

            return new SequentialTransition(
                ArrowAnimator.animateIn(prevArrow, 1 * ListVisualizerConfig.speedVisualization, DrawArrowDirection.FORWARD),
                ArrowAnimator.animateIn(
                    arrow, 1 * ListVisualizerConfig.speedVisualization, DrawArrowDirection.FORWARD
                )
            );
        }

        AnchorPane.setTopAnchor(arrow, (height / 2) - (arrow.getBoundsInParent().getHeight() / 2));
        AnchorPane.setLeftAnchor(arrow, leftAnchorVal);

        return ArrowAnimator.animateIn(arrow, 1 * ListVisualizerConfig.speedVisualization, DrawArrowDirection.FORWARD);
    }

    private Animation undoCreateArrows(){
        Arrow arrow = exec.getCreatedArrows().pop();
        Arrow prevArrow = exec.getCreatedPrevArrows().pop();

        arrows.remove(arrow);
        prevArrows.remove(prevArrow);

        if(prevArrow != null){
            Animation removeArrows = new SequentialTransition(
                ArrowAnimator.animateOut(arrow, 1, DrawArrowDirection.BACKWARD),
                ArrowAnimator.animateOut(prevArrow, 1, DrawArrowDirection.FORWARD)
            );

            removeArrows.setOnFinished(e -> {
                visualization_area.getChildren().remove(arrow);
                visualization_area.getChildren().remove(prevArrow);
            });
        } 

        Animation removeArrowNext = ArrowAnimator.animateOut(arrow, 1, DrawArrowDirection.BACKWARD);
        removeArrowNext.setOnFinished(e -> { visualization_area.getChildren().remove(arrow); });

        return removeArrowNext;
    }

    private void addTransversalStep(AnimationTimeLine timeLine){
        buildExplanationBeforeTransversalStep(timeLine);
        
        new TransverseAndHighlightOperation(
            nodes, timeLine, context.getExplanationRepository()
        ).build(context.getPos() - 1, context.getValue());

        double speed = ListVisualizerConfig.speedVisualization;

        buildExplanationAfterTransversalStep(timeLine);

        timeLine.addStep(new Step(
            () -> {
                Rectangle prevNodeRect = context.getPos() - 1 < 0 ? null :
                                         nodes.get(context.getPos() - 1).getRect();
                
                Rectangle nextNodeRect = context.getPos() >= nodes.size() ? null :
                                         nodes.get(context.getPos()).getRect();

                return new SequentialTransition(
                    prevNodeRect != null ? NodeAnimator.animateFill(
                        prevNodeRect, (Color) prevNodeRect.getFill(), 
                        Colors.prev, (int) (700 * speed), false
                    ) : AnimationUtils.emptyAnimation(),
                    nextNodeRect != null ? NodeAnimator.animateFill(
                        nextNodeRect, (Color) nextNodeRect.getFill(), 
                        Colors.next, (int) (700 * speed), false
                    ) : AnimationUtils.emptyAnimation()
                );
            },
            () -> {
                Rectangle prevNodeRect = context.getPos() - 1 < 0 ? null :
                                         nodes.get(context.getPos() - 1).getRect();
                
                Rectangle nextNodeRect = context.getPos() >= nodes.size() ? null :
                                         nodes.get(context.getPos()).getRect();

                return new SequentialTransition(
                    nextNodeRect != null ? NodeAnimator.animateFill(
                        nextNodeRect, (Color) nextNodeRect.getFill(), 
                        Colors.node, (int) (700 * speed), false
                    ) : AnimationUtils.emptyAnimation(),
                    prevNodeRect != null ? NodeAnimator.animateFill(
                        prevNodeRect, (Color) prevNodeRect.getFill(), 
                        Colors.node, (int) (700 * speed), false
                    ) : AnimationUtils.emptyAnimation()
                );
            } 
        ));
    }

    private void buildExplanationBeforeTransversalStep(AnimationTimeLine timeLine){
        if(context.getPos() != 0){
            if(context.getPos() == nodes.size()){
                context.getExplanationRepository().addExplanation(0, 
                    new ExplanationText(
                    0, "Inserção na cauda." 
                ));
            }

            context.getExplanationRepository().addExplanation(0, 
                new ExplanationText(
                0, "Primeiro, é feita uma busca da região de inserção para inserir o novo nó {new_node:" 
                + String.valueOf(context.getValue()) + "}.\n"
            ));   
        }

        else{
            context.getExplanationRepository().addExplanation(0, 
                new ExplanationText(
                0, "Inserção na cabeça.\n" 
            ));
        }
    }

    private void buildExplanationAfterTransversalStep(AnimationTimeLine timeLine){
        if(context.getPos() != 0){
            context.getExplanationRepository().addExplanation(timeLine.size() == 0 ? 1 : timeLine.size(), 
                new ExplanationText(timeLine.size() == 0 ? 1 : timeLine.size(), 
                    "Região de inserção encontrada. \n" 
                    + "Nó {prev:anterior} (Valor {prev:" + nodes.get(context.getPos() - 1).getText() + "})"
                )
            );
        }

        if(context.getPos() < nodes.size()){
            context.getExplanationRepository().addExplanation(
                timeLine.size() == 0 ? 1 : timeLine.size(), 
                new ExplanationText(timeLine.size() == 0 ? 1 : timeLine.size(), 
                    "{next:Próximo } nó: (Valor {next:" + 
                    String.valueOf(nodes.get(context.getPos()).getText()) + "})" 
                )
            );
        }
    }

    private void addRemoveArrowSteps(AnimationTimeLine timeLine){
        if(context.getPos() == 0 || context.getPos() == context.getInitialListSize()){
            context.getExplanationRepository().addExplanation(
                timeLine.size(),
                new ExplanationText(timeLine.size(), "")
            );

            return;
        }
        
        buildExplanationForRemoveArrowsStep(timeLine);

        timeLine.addStep(new Step(
            () -> {
                double speed = ListVisualizerConfig.speedVisualization;

                if(context.getListType() == ListType.DOUBLY){
                    return new SequentialTransition(
                        ArrowAnimator.animateOut(
                            arrows.get(context.getPos() - 1), speed, 
                            DrawArrowDirection.BACKWARD
                        ),
                        ArrowAnimator.animateOut(
                            prevArrows.get(context.getPos() - 1), 
                            speed, DrawArrowDirection.FORWARD
                        )
                    );
                }

                return ArrowAnimator.animateOut(
                    arrows.get(context.getPos() - 1), speed, DrawArrowDirection.FORWARD
                );
            },
            () -> {
                double speed = ListVisualizerConfig.speedVisualization;

                if(context.getListType() == ListType.DOUBLY){
                    return new SequentialTransition(
                        ArrowAnimator.animateIn(arrows.get(context.getPos() - 1), speed, DrawArrowDirection.FORWARD),
                        ArrowAnimator.animateIn(prevArrows.get(context.getPos() - 1), speed, DrawArrowDirection.BACKWARD)
                    );
                }

                return ArrowAnimator.animateIn(
                    arrows.get(context.getPos() - 1), speed, DrawArrowDirection.BACKWARD
                );
            }
        ));
    }

    private void buildExplanationForRemoveArrowsStep(AnimationTimeLine timeLine){
        String prevValue = nodes.get(context.getPos() - 1).getText();
        String nextValue = nodes.get(context.getPos()).getText();

        String explanation = "O ponteiro prox. do nó {prev:anterior} ({prev:"
                             + prevValue + "}) deixa de apontar para o nó {next:" + nextValue + "}.";

        if(context.getListType() == ListType.DOUBLY){
           explanation = "O ponteiro prox. do nó {prev:anterior} ({prev:"
                         + prevValue + "}) deixa de apontar para o nó {value:" + nextValue + "}.\n" 
                         + "O ponteiro ant. do nó {next:próximo} ({next:"+ nextValue + "}) "
                         + "deixa de apontar para o nó ({prev:"+ prevValue + "})."; 
        }

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), explanation)
        );
    }

    private void addCreateNodeStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Criando o nó {new_node:" + context.getValue() + "}."
            )
        );

        final double xOffset = context.getPos() == nodes.size() ? 0 : context.getxOffset();  

        final RepositionNodesOperation repositionNodes = new RepositionNodesOperation(
            nodes, arrows, prevArrows, headLabel, tailLabel, visualization_area, 
            context.getInitialListSize(), context.getListType()
        );

        timeLine.addStep(new Step(
            () -> {
                return new SequentialTransition(
                    new ParallelTransition(
                        repositionNodes.build(context.getPos(), xOffset, Operation.INSERT)
                    ),
                    createNode(context.getNodeWidth(), context.getValue(), context.getPos())
                );
            },
            () -> {
                return new SequentialTransition(
                    undoCreateNode(),
                    new ParallelTransition(
                        repositionNodes.build(context.getPos(), -xOffset, Operation.INSERT)
                    )
                );
            }
        ));
    }

    private void addTranslateNodeStep(AnimationTimeLine timeLine){
        final double height = visualization_area.getHeight();

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Criando o nó {new_node:" + context.getValue() + "}."
            )
        );

        timeLine.addStep(new Step(
            () -> NodeAnimator.animateMove(
                newNode, 0, (height / 2) - (context.getNodeWidth() / 2) - (0.2 * height), 
                ListVisualizerConfig.translateDuration
            ),
            () -> NodeAnimator.animateMove(
                newNode, 0, -((height / 2) - (context.getNodeWidth() / 2) - (0.2 * height)),
                ListVisualizerConfig.translateDuration
            )
        ));
    }

    private void addStepToConnectPrevArrow(AnimationTimeLine timeLine){
        if(context.getPos() == 0) return;   

        double speed = 1 * ListVisualizerConfig.speedVisualization;

        buildExplanationForStepToConnectPrevArrow(timeLine);
        
        timeLine.addStep(new Step(
            () -> {
                Animation resetFillColorFomPrevNode = NodeAnimator.animateFill(
                    nodes.get(context.getPos() - 1).getRect(), 
                    (Color) nodes.get(context.getPos() - 1).getRect().getFill(), 
                     Colors.node, 
                    (int) (300 * ListVisualizerConfig.speedVisualization) , false
                );

                if(context.getPos() - 1 >= arrows.size()){
                    return new SequentialTransition(
                        createArrows(
                            context.getNodeWidth() * ListVisualizerConfig.spacingBetweenNodes,
                            context.getPos() - 1
                        ), resetFillColorFomPrevNode, 
                        NodeAnimator.animateFill(
                            nodes.get(context.getPos()).getRect(), (Color) nodes.get(context.getPos()).getRect().getFill(), 
                            Colors.node, (int) (700 * speed), false
                        )
                    );
                }

                Animation arrowAnimation = ArrowAnimator.animateIn(
                    arrows.get(context.getPos() - 1), speed, DrawArrowDirection.FORWARD
                );

                if(context.getListType() == ListType.DOUBLY){
                    return new SequentialTransition(
                        arrowAnimation,
                        ArrowAnimator.animateIn(
                            prevArrows.get(context.getPos() - 1), speed, DrawArrowDirection.FORWARD
                        ), resetFillColorFomPrevNode
                    );
                }

                return new SequentialTransition(arrowAnimation, resetFillColorFomPrevNode);
            },
            () -> {
                Animation resetFillColor = NodeAnimator.animateFill(
                    nodes.get(context.getPos() - 1).getRect(), 
                    (Color) nodes.get(context.getPos() - 1).getRect().getFill(), Colors.prev, 
                    (int) (300 * ListVisualizerConfig.speedVisualization) , false
                );

                if(!exec.getCreatedArrows().isEmpty()){
                    return new SequentialTransition(
                        resetFillColor, 
                        undoCreateArrows()
                    );
                }

                Animation arrowAnimation = ArrowAnimator.animateOut(
                    arrows.get(context.getPos() - 1), speed, DrawArrowDirection.BACKWARD
                );

                if(context.getListType() == ListType.DOUBLY){
                    return new SequentialTransition(
                        resetFillColor,
                        ArrowAnimator.animateOut(
                            prevArrows.get(context.getPos() - 1), speed, DrawArrowDirection.FORWARD
                        ), arrowAnimation
                    );
                }

                return new SequentialTransition(resetFillColor, arrowAnimation);
            }
        ));   
    }

    private void buildExplanationForStepToConnectPrevArrow(AnimationTimeLine timeLine){
        String prevValue = nodes.get(context.getPos() - 1).getText();

        String explanation = "O ponteiro prox. do nó {prev:anterior} de valor ({prev:"
                             + prevValue + "}) aponta para o novo nó ({new_node:" + context.getValue() + "}).";

        if(context.getListType() == ListType.DOUBLY){
           explanation = "O ponteiro prox. do nó {prev:anterior} de valor ({prev:"
                         + prevValue + "}) aponta para o {new_node:novo nó} ({new_node:" + context.getValue() + "})"
                         + " e o ponteiro ant. do {new_node:novo nó} passa a apontar para o nó {prev:anterior}" 
                         + "({new_node:" + context.getValue() + "}).";

        }

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), explanation)
        );
    }

    private void addStepToConnectNextArrow(AnimationTimeLine timeLine){
        if(context.getPos() >= context.getInitialListSize()) return;

        double speed = ListVisualizerConfig.speedVisualization;

        buildExplanationForStepToConnectNextArrow(timeLine);

        timeLine.addStep(new Step(
            () -> {
                Animation toCreateArrows =  createArrows(
                    context.getNodeWidth() * ListVisualizerConfig.spacingBetweenNodes, 
                    context.getPos()
                );

                if(context.getPos() + 1 < nodes.size()){
                    Rectangle nextNodeRect = nodes.get(context.getPos() + 1).getRect();

                    return new SequentialTransition(
                        toCreateArrows, 
                        NodeAnimator.animateFill(
                            nextNodeRect, (Color) nextNodeRect.getFill(), 
                            Colors.node, (int) (700 * speed), false
                        )
                    );
                }

                return toCreateArrows;
            },
            () -> {
                if(context.getPos() + 1 < nodes.size()){
                    Rectangle nextNodeRect = nodes.get(context.getPos() + 1).getRect();

                    return new SequentialTransition(
                        NodeAnimator.animateFill(
                            nextNodeRect, (Color) nextNodeRect.getFill(), 
                            Colors.next, (int) (700 * speed), false
                        ),
                        undoCreateArrows()
                    );
                }

                return undoCreateArrows();
            }
        ));
    }

    private void buildExplanationForStepToConnectNextArrow(AnimationTimeLine timeLine){
        String nextValue = nodes.get(context.getPos() + 1).getText();

        String explanation = "O ponteiro prox. do {new_node:novo nó} ({new_node:"
                             + context.getValue() + "}) aponta para o {next:próximo} nó ({next:" + nextValue + "}).";

        if(context.getListType() == ListType.DOUBLY){
           explanation = "O ponteiro prox. do {new_node:novo nó} ({new_node:"
                         + context.getValue() + "}) aponta para o {next:próximo} nó ({next:" + nextValue + "}) "
                         + "e o ponteiro ant. do {next:próximo nó} passa a apontar para o {new_node:novo nó}"
                         +  "({new_node:" + context.getValue() + "}).\n";

        }

        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), explanation)
        );
    }

    private void addNotifyChangesStep(AnimationTimeLine timeLine){
        context.getExplanationRepository().addExplanation(
            timeLine.size(),
            new ExplanationText(timeLine.size(), 
                "Nó ({inserted:" + context.getValue() + "}) adicionado à lista."
            )
        );

        timeLine.addStep(new Step(
            () -> {
                Rectangle newNode = nodes.get(context.getPos()).getRect();

                return NodeAnimator.animateFill(
                    newNode, (Color) newNode.getFill(), 
                    Colors.node, 800, false
                );
            }, 
            () -> {
                Rectangle newNode = nodes.get(context.getPos()).getRect();

                return NodeAnimator.animateFill(
                    newNode, (Color) newNode.getFill(), Colors.node, 800, false
                );
            }
        ));
    }

    private void addFixLabelsStep(AnimationTimeLine timeLine){
        FixArrowLabelsPosOperation fixArrowLabels = new FixArrowLabelsPosOperation(
            headLabel, tailLabel, context.getxOffset(), context.getPos(), context.getInitialListSize()
        );

        String explantion = "";

        if(context.getPos() == context.getInitialListSize()){
            explantion = "O novo nó ({inserted:" + context.getValue() + "}) passa a ser a nova cauda.";
        }

        if(context.getPos() == 0){
            explantion = "O novo nó ({inserted:" + context.getValue() + "}) passa a ser a nova cabeça.";
        }

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), explantion)
        );

        fixArrowLabels.build(timeLine, true, Operation.INSERT);
    }

    private void addStepToMoveCurvedArrowIfNeeded(AnimationTimeLine timeLine){
        FixCurvedArrowPosOperation fixCurvedArrow = new FixCurvedArrowPosOperation(
            nodes, curvedArrow, visualization_area, context.getxOffset(), context.getListType()
        );

        context.getExplanationRepository().addExplanation(timeLine.size(), 
            new ExplanationText(timeLine.size(), 
                context.getListType() == ListType.CIRCULAR ? 
                "A cauda volta a apontar para a cabeça." : 
                ""
            )
        );

        fixCurvedArrow.build(timeLine);
    }
}
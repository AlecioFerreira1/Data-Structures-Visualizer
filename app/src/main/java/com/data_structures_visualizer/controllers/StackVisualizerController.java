package com.data_structures_visualizer.controllers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.data_structures_visualizer.config.StackVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.models.text.ExplanationRepository;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.util.DialogFactory;
import com.data_structures_visualizer.util.SceneManager;
import com.data_structures_visualizer.util.Util;
import com.data_structures_visualizer.visual.context.stack.StackContext;
import com.data_structures_visualizer.visual.layout.LayoutManager;
import com.data_structures_visualizer.visual.layout.StackLayoutManager;
import com.data_structures_visualizer.visual.operations.stack.PopOperation;
import com.data_structures_visualizer.visual.operations.stack.PushOperation;
import com.data_structures_visualizer.visual.text.ExplanationTextParser;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public final class StackVisualizerController {
    @FXML
    private AnchorPane visualization_area;
    @FXML
    private Slider speed_visualization_slider;
    @FXML
    private Label speed_visualization_label;
    @FXML 
    private Button list_btn;
    @FXML
    private Button stack_btn; 
    @FXML
    private Button queue_btn;
    @FXML
    private Button create_btn;
    @FXML 
    private Button push_btn;
    @FXML 
    private Button pop_btn;
    @FXML     
    private Button clear_btn;
    @FXML
    private ProgressIndicator visualization_progress;
    @FXML
    private Button reset_btn;
    @FXML
    private Button step_backward_btn;
    @FXML
    private Button pause_btn;
    @FXML
    private Button step_forward_btn;
    @FXML
    private Button advance_btn;

    private StackLayoutManager stackLayoutManager;

    private final ExplanationRepository explanationRepository = new ExplanationRepository();
    private final AnimationTimeLine animationTimeLine = new AnimationTimeLine();

    private final Stack<Integer> stack = new Stack<Integer>(null);

    @FXML
    public void initialize(){
        startLayoutManager();
        putStartExemple();
        handleToScreenChange();
        setupOperations();
        setupControlButtons();
        setupListeners();
    }

    private void startLayoutManager(){
        stackLayoutManager = new StackLayoutManager(visualization_area);
        stackLayoutManager.start();
        stackLayoutManager.setExplanationTextRect(LayoutManager.createExplanationRect(visualization_area));
    }

    private void putStartExemple(){
        for(int i = 0; i < 9; ++i){  
            stack.push(i);

            stackLayoutManager.getNodes().add(i, new VisualNode(
                625 * StackVisualizerConfig.squareSize, 
                625 * StackVisualizerConfig.squareSize, 
                Integer.toString(i))
            );

            visualization_area.getChildren().add(stackLayoutManager.getNodes().get(i));
        }
    }

    private void setupListeners(){
        visualization_area.layoutBoundsProperty().addListener((obs, odlVal, newVal) -> {
            stackLayoutManager.fixVisualizationAreaLayout(newVal.getWidth(), newVal.getHeight());
        });

        speed_visualization_slider.valueProperty().addListener((obs, oldValue, newVal) -> {
            speed_visualization_label.setText(String.format("%.1f", 1 + newVal.doubleValue() / 100) + "x");
            StackVisualizerConfig.speedVisualization = newVal.doubleValue();
        });

        visualization_progress.progressProperty().bind(animationTimeLine.progressProperty());

        animationTimeLine.setOnFinished(() -> {
            stackLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(), visualization_area.getHeight()
            );
        });

        animationTimeLine.setOnStepChanged(index -> {
            List<ExplanationText> explanations = explanationRepository.get(index);

            if(!explanations.isEmpty()) {
                stackLayoutManager.getExplanationTextRect().setContent(
                    ExplanationTextParser.parse(explanations)
                );
            } 
            
            else{
                stackLayoutManager.getExplanationTextRect().clear();
            }
        });
    }

    private void handleToScreenChange(){
        list_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/ListVisualizerScreen.fxml");
        });

        queue_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/QueueVisualizerScreen.fxml");
        });
    }

    private void setupOperations(){
        create_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o tamanho da pilha: ", 
                null, (Integer lenght, Integer v) -> {
                createStack(lenght);

                stackLayoutManager.
                fixVisualizationAreaLayout(visualization_area.getWidth(), visualization_area.getHeight());
            });
        });

        push_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o valor para empilhar: ", 
                null, 
                (Integer value, Integer v) -> pushNode(value)
            );
        });

        pop_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show(
                "Deseja desempilhar um nó?",  () -> popNode()
            );
        });

        clear_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show(
                "Tem certeza que deseja limpar a área de visualização?", () -> {
                stackLayoutManager.clearVisualization();
            });
        });
    }

    private void setupControlButtons(){
        reset_btn.setOnAction(e -> {
            animationTimeLine.reset();
        });
        
        step_backward_btn.setOnAction(e -> {
            animationTimeLine.playPrevious(1.0);
        });
        
        pause_btn.setOnAction(e -> {
            if(animationTimeLine.isPlaying()){
                animationTimeLine.pause();   
            }
            
            else{
                animationTimeLine.play();
            }
        });
        
        step_forward_btn.setOnAction(e -> {
            animationTimeLine.playNext();
        });
        
        advance_btn.setOnAction(e -> {
            animationTimeLine.playFast();
        });
    }

    private void createStack(int lenght){
        if(lenght > StackVisualizerConfig.stackMaxLimit){
            Util.showAlert(
                "Não foi possível criar a pilha.",
                String.format("Tamanho máximo permitido: %d", StackVisualizerConfig.stackMaxLimit),
                AlertType.CONFIRMATION
            );

            return;
        }

        stackLayoutManager.clearVisualization();

        for(int i = 0; i < lenght; ++i){
            Integer randInt = ThreadLocalRandom.current().nextInt(0, 9999);

            stack.push(randInt);
            stackLayoutManager.getNodes().add(new VisualNode(625 * StackVisualizerConfig.squareSize, 
                625 * StackVisualizerConfig.squareSize, String.valueOf(randInt)
            ));

            visualization_area.getChildren().add(stackLayoutManager.getNodes().get(i));
        }
    }

    private boolean emptyStackMessage(){
        if(stack.isEmpty()){
            Util.showAlert(
                "Pilha vazia!", 
                "Não há elementos para desempilhar.", 
                AlertType.CONFIRMATION
            );

            return true;
        }

        return false;
    }

    private StackContext createStackContext(Integer value){
        return new StackContext(
            stack, value, visualization_area.getHeight(), explanationRepository
        );
    }

    private boolean validatePush(){
        if(stackLayoutManager.getNodes().size() >= StackVisualizerConfig.stackMaxLimit){
            Util.showAlert(
                "Pilha cheia!", 
                String.format("A pilha atingiu a quantidade máxima: %d", StackVisualizerConfig.stackMaxLimit),
                AlertType.CONFIRMATION
            );

            return false;
        }

        return true;
    }

    private void pushNode(Integer value){
        if(!validatePush()) return;

        animationTimeLine.clear();
        explanationRepository.clear();

        StackContext context = createStackContext(value);

        PushOperation op = new PushOperation(
            context, visualization_area, stackLayoutManager.getTopLabel(), 
            stackLayoutManager.getNodes(), stackLayoutManager.getStackBase()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }

    private void popNode(){
        if(emptyStackMessage()) return;

        animationTimeLine.clear();
        explanationRepository.clear();

        StackContext context = createStackContext(-1);
        
        PopOperation op = new PopOperation(
            context, visualization_area, stackLayoutManager.getTopLabel(), 
            stackLayoutManager.getNodes(), stackLayoutManager.getStackBase()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }
}
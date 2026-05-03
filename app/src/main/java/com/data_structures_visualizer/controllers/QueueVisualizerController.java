package com.data_structures_visualizer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.QueueVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.entities.Queue;
import com.data_structures_visualizer.models.text.ExplanationRepository;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.util.DialogFactory;
import com.data_structures_visualizer.util.SceneManager;
import com.data_structures_visualizer.util.Util;
import com.data_structures_visualizer.visual.context.queue.QueueContext;
import com.data_structures_visualizer.visual.layout.LayoutManager;
import com.data_structures_visualizer.visual.layout.QueueLayoutManager;
import com.data_structures_visualizer.visual.operations.queue.DequeueOperation;
import com.data_structures_visualizer.visual.operations.queue.EnqueueOperation;
import com.data_structures_visualizer.visual.text.ExplanationTextParser;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public final class QueueVisualizerController {
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
    private Button enqueue_btn;
    @FXML 
    private Button dequeue_btn;
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

    private QueueLayoutManager queueLayoutManager;

    private final ExplanationRepository explanationRepository = new ExplanationRepository();
    private final AnimationTimeLine animationTimeLine = new AnimationTimeLine();

    private final Queue<Integer> queue = new Queue<Integer>(null);

    @FXML
    public void initialize(){
        startLayoutManager();
        putStartExample();
        handleToScreenChange();
        setupOperations();
        setupControlButtons();
        setupListeners();
    }

    private void startLayoutManager(){
        queueLayoutManager = new QueueLayoutManager(visualization_area);
        queueLayoutManager.start();
        queueLayoutManager.setExplanationTextRect(LayoutManager.createExplanationRect(visualization_area));
    }

    private void putStartExample(){
        final ArrayList<VisualNode> nodes = queueLayoutManager.getNodes();
        final ArrayList<Arrow> arrows = queueLayoutManager.getArrows();

        for(int i = 0; i < 11; ++i){  
            nodes.add(i, new VisualNode(
                625 * QueueVisualizerConfig.squareSize, 
                625 * QueueVisualizerConfig.squareSize, Integer.toString(i)
            ));

            if(i < 10){
                arrows.add(i, new Arrow(
                    ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize 
                ));

                visualization_area.getChildren().add(arrows.get(i));
            }

            visualization_area.getChildren().add(nodes.get(i));
        }
    }

    private void handleToScreenChange(){
        list_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/ListVisualizerScreen.fxml");
        });

        stack_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/StackVisualizerScreen.fxml");
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

    private void setupListeners(){
        visualization_area.layoutBoundsProperty().addListener((obs, odlVal, newVal) -> {
            if(newVal.getWidth() > 0 && newVal.getHeight() > 0){
                queueLayoutManager.fixVisualizationAreaLayout(newVal.getWidth(), newVal.getHeight());
            }
        });

        speed_visualization_slider.valueProperty().addListener((obs, oldValue, newVal) -> {
            speed_visualization_label.setText(String.format("%.1f", 1 + newVal.doubleValue() / 100) + "x");
        });

        visualization_progress.progressProperty().bind(animationTimeLine.progressProperty());

        animationTimeLine.setOnFinished(() -> {
            queueLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(),
                visualization_area.getHeight()
            );
        });

        animationTimeLine.setOnStepChanged(index -> {
            List<ExplanationText> explanations = explanationRepository.get(index);

            if(!explanations.isEmpty()) {
                queueLayoutManager.getExplanationTextRect().setContent(
                    ExplanationTextParser.parse(explanations)
                );
            } 
            
            else{
                queueLayoutManager.getExplanationTextRect().clear();
            }
        });
    }

    private void setupOperations(){
        create_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o tamanho da fila: ", null, (Integer lenght, Integer v) -> {
                createQueue(lenght);
                queueLayoutManager.fixVisualizationAreaLayout(
                    visualization_area.getWidth(), visualization_area.getHeight()
                );
            });
        });

        enqueue_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o valor para enfileirar: ", null, (Integer value, Integer v) -> {
                enqueue(value);
            });
        });

        dequeue_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show("Deseja desenfileirar um nó?", () -> dequeue());
        });

        clear_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show("Tem certeza que deseja limpar a área de visualização?", () -> {
                queueLayoutManager.clearVisualization();
            });
        });
    }

    private void createQueue(int lenght){
        if(lenght > QueueVisualizerConfig.queueMaxLimit){
            Util.showAlert(
                "Não foi possível criar a fila.",
                String.format("Tamanho máximo permitido: %d", QueueVisualizerConfig.queueMaxLimit),
                AlertType.CONFIRMATION
            );
            
            return;
        }

        queueLayoutManager.clearVisualization();
        
        final ArrayList<VisualNode> nodes = queueLayoutManager.getNodes();
        final ArrayList<Arrow> arrows = queueLayoutManager.getArrows();

        for(int i = 0; i < lenght; ++i){
            Integer randInt = ThreadLocalRandom.current().nextInt(0, 9999);

            queue.enqueue(randInt);
            nodes.add(new VisualNode(
                625 * QueueVisualizerConfig.squareSize, 
                625 * QueueVisualizerConfig.squareSize, 
                String.valueOf(randInt)
            ));

            if(i < lenght - 1){
                arrows.add(new Arrow(
                    (1 + QueueVisualizerConfig.spacingBetweenNodes) * QueueVisualizerConfig.squareSize 
                ));

                visualization_area.getChildren().add(arrows.get(i));
            }

            visualization_area.getChildren().add(nodes.get(i));
        }
    }

    private QueueContext createQueueContext(Integer value){
        return new QueueContext(
            queue, value, visualization_area.getWidth(), 
            visualization_area.getHeight(), explanationRepository,
            queueLayoutManager.getQueueDelimiter().getWidth()
        );
    }

    private void enqueue(Integer value){
        if(queueLayoutManager.getNodes().size() == QueueVisualizerConfig.queueMaxLimit){
            Util.showAlert(
                "Quantidade máxima de elementos permitidos atingida!",
                "Não possível enfileirar o elemento. A quantidade máxima foi atingida: "
                + QueueVisualizerConfig.queueMaxLimit + "\n", 
                AlertType.WARNING
            );

            return;
        }

        animationTimeLine.clear();
        explanationRepository.clear();

        final QueueContext context = createQueueContext(value); 
        
        final EnqueueOperation op = new EnqueueOperation(
            context, visualization_area, queueLayoutManager.getNodes(), queueLayoutManager.getArrows(), 
            queueLayoutManager.getStartLabel(), queueLayoutManager.getEndLabel()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }

    private void dequeue(){
        if(queueLayoutManager.getNodes().size() == 0){
            Util.showAlert(
                "Fila Vazia", "A fila está vazia.\n", AlertType.WARNING
            );

            return;
        }

        animationTimeLine.clear();
        explanationRepository.clear();

        QueueContext context = createQueueContext(-1);
        DequeueOperation op = new DequeueOperation(
            context, visualization_area, queueLayoutManager.getNodes(), queueLayoutManager.getArrows(), 
            queueLayoutManager.getStartLabel(), queueLayoutManager.getEndLabel()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }
}
package com.data_structures_visualizer.controllers;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.config.QueueVisualizerConfig;
import com.data_structures_visualizer.models.entities.Queue;
import com.data_structures_visualizer.util.DialogFactory;
import com.data_structures_visualizer.util.SceneManager;
import com.data_structures_visualizer.util.Util;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.ArrowLabel;
import com.data_structures_visualizer.visual.ui.ArrowLabel.ArrowPosition;
import com.data_structures_visualizer.visual.ui.QueueDelimiter;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private QueueDelimiter queueDelimiter;
    private final ArrayList<VisualNode> nodes = new ArrayList<VisualNode>();
    private final ArrayList<Arrow> arrows = new ArrayList<Arrow>();

    private ArrowLabel startLabel;
    private ArrowLabel endLabel;

    private final Queue<Integer> queue = new Queue<Integer>(null);

    @FXML
    public void initialize(){
        startLabel = new ArrowLabel(
            QueueVisualizerConfig.spacingBetweenNodes * QueueVisualizerConfig.squareSize * 625, 
            "INÍCIO", 15
        );

        startLabel.setArrowPosition(ArrowPosition.BELOW);

        endLabel = new ArrowLabel(
            QueueVisualizerConfig.spacingBetweenNodes * QueueVisualizerConfig.squareSize * 625,
             "FIM", 15
        );

        endLabel.setArrowPosition(ArrowPosition.BELOW);

        for(int i = 0; i < 12; ++i){  
            nodes.add(i, new VisualNode(
                625 * QueueVisualizerConfig.squareSize, 
                625 * QueueVisualizerConfig.squareSize, Integer.toString(i)
            ));

            if(i < 11){
                arrows.add(i, new Arrow(
                    ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize 
                ));

                visualization_area.getChildren().add(arrows.get(i));
            }

            visualization_area.getChildren().add(nodes.get(i));
        } 

        handleToScreenChange();
        setupOperations();

        visualization_area.layoutBoundsProperty().addListener((obs, odlVal, newVal) -> {
            if(newVal.getWidth() > 0 && newVal.getHeight() > 0){
                fixVisualizationAreaLayout(newVal.getWidth(), newVal.getHeight());
            }
        });

        speed_visualization_slider.valueProperty().addListener((obs, oldValue, newVal) -> {
            speed_visualization_label.setText(String.format("%.1f", 1 + newVal.doubleValue() / 100) + "x");
        });
    }

    private void handleToScreenChange(){
        list_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/ListVisualizerScreen.fxml");
        });

        stack_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/StackVisualizerScreen.fxml");
        });
    }

    private void fixVisualizationAreaLayout(double width, double height){
        double updatedWidth = 0.8 * width;
        double updatedHeight = 0.15 * height;
        double updatedStroke = 0.004 * width;

        if(queueDelimiter == null){
            queueDelimiter = new QueueDelimiter(updatedWidth, updatedHeight, updatedStroke);
            visualization_area.getChildren().add(queueDelimiter);
        } 

        Platform.runLater(() -> {
            queueDelimiter.update(updatedWidth, updatedHeight, updatedStroke);
            AnchorPane.setTopAnchor(queueDelimiter, (height / 2) - (queueDelimiter.getSpacingBetweenLines() / 2));
            AnchorPane.setLeftAnchor(queueDelimiter, (width / 2) - (queueDelimiter.getWidth() / 2));

            double xOffset = 0.01 * width;
            double value = height < width ? height : width;

            for(int i = 0; i < nodes.size(); ++i){
                nodes.get(i).update(
                    value * QueueVisualizerConfig.squareSize, 
                    value * QueueVisualizerConfig.squareSize, value * 0.005
                );
                
                AnchorPane.setTopAnchor(
                    nodes.get(i), (height / 2) - (nodes.get(i).getRect().getHeight()) / 2
                );

                AnchorPane.setLeftAnchor(
                    nodes.get(i), 
                    xOffset + (width / 2) - (queueDelimiter.getWidth() / 2) +
                    ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodes.get(i).getRect().getWidth() * i)
                );

                final double nodeWidth = value * ListVisualizerConfig.squareSize;
                final double arrowLenght = ListVisualizerConfig.spacingBetweenNodes * nodeWidth;

                if(i < arrows.size()){
                    resizeArrow(arrows.get(i), arrowLenght, width, height);
                    
                    AnchorPane.setTopAnchor(
                        arrows.get(i), 
                        (height / 2) - (arrows.get(i).getBoundsInParent().getHeight() / 2)
                    );

                    AnchorPane.setLeftAnchor(
                        arrows.get(i), 
                        xOffset + (width / 2) - (queueDelimiter.getWidth() / 2) + nodeWidth +
                        ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodes.get(i).getRect().getWidth() * i)
                    );
                }
            }

            anchorArrowLabels(
                QueueVisualizerConfig.squareSize * width * 0.6, 
                QueueVisualizerConfig.squareSize * height / 3, 
                width, height
            );
        });
    }

    private void resizeArrow(Arrow arrow, double lenght, double width, double height){
        arrow.setStrokeWidth(height * 0.003);
        arrow.setLenght(lenght);
    }

    private void setupOperations(){
        create_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o tamanho da fila: ", null, (Integer lenght, Integer v) -> {
                createQueue(lenght);
                fixVisualizationAreaLayout(visualization_area.getWidth(), visualization_area.getHeight());
            });
        });

        enqueue_btn.setOnAction(e -> {

        });

        dequeue_btn.setOnAction(e -> {

        });

        clear_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show("Tem certeza que deseja limpar a área de visualização?", () -> {
                clearVisualization();
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

        clearVisualization();

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
   
    private void clearVisualization(){
        for(VisualNode node : nodes){
            visualization_area.getChildren().remove(node);
        }

        for(Arrow arrow : arrows){
            visualization_area.getChildren().remove(arrow);
        }

        visualization_area.getChildren().remove(startLabel);
        visualization_area.getChildren().remove(endLabel);
        nodes.clear();
    }

    private void anchorArrowLabels(double arrowLenght, double fontSize, double width, double height){
        if(nodes.isEmpty()){
            visualization_area.getChildren().remove(startLabel);
            visualization_area.getChildren().remove(endLabel);
            return;
        }

        if(!visualization_area.getChildren().contains(startLabel)){
            visualization_area.getChildren().add(startLabel);
        }

        if(!visualization_area.getChildren().contains(endLabel)){
            visualization_area.getChildren().add(endLabel);
        }

        final double value = height < width ? height : width;
        final double nodeWidth = QueueVisualizerConfig.squareSize * value; 
        final double xOffset = 0.01 * width;
        final double labelsYoffset = 1.7;

        startLabel.setText("INÍCIO");
        startLabel.update(arrowLenght, startLabel.getText(), fontSize);

        if(nodes.size() == 1){
            visualization_area.getChildren().remove(endLabel);
            startLabel.setText("INÍCIO\nFIM");
        }

        if(visualization_area.getChildren().contains(endLabel)){
            endLabel.update(arrowLenght, "FIM", fontSize);

            AnchorPane.setTopAnchor(
                endLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
            );

            AnchorPane.setLeftAnchor(
                endLabel, (nodeWidth / 2 ) + (width / 2) - (queueDelimiter.getWidth() / 2) +
                ((1 + QueueVisualizerConfig.spacingBetweenNodes) * nodeWidth * (nodes.size() - 1)) 
            );
        }
            
        AnchorPane.setTopAnchor(
            startLabel, ((height / 2) - (nodeWidth / 2)) - ((1 + labelsYoffset) * arrowLenght)
        );

        AnchorPane.setLeftAnchor(
            startLabel, xOffset + (width / 2) - (queueDelimiter.getWidth() / 2)
        );
    }   
}

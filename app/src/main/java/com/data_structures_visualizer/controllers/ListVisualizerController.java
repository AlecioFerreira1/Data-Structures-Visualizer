package com.data_structures_visualizer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.data_structures_visualizer.config.ListVisualizerConfig;
import com.data_structures_visualizer.models.animation.AnimationTimeLine;
import com.data_structures_visualizer.models.entities.CircularLinkedList;
import com.data_structures_visualizer.models.entities.DoublyLinkedList;
import com.data_structures_visualizer.models.entities.SinglyLinkedList;
import com.data_structures_visualizer.models.text.ExplanationRepository;
import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.util.DialogFactory;
import com.data_structures_visualizer.util.SceneManager;
import com.data_structures_visualizer.util.Util;
import com.data_structures_visualizer.visual.context.list.DeleteContext;
import com.data_structures_visualizer.visual.context.list.InsertContext;
import com.data_structures_visualizer.visual.layout.LayoutManager;
import com.data_structures_visualizer.visual.layout.ListLayoutManager;
import com.data_structures_visualizer.visual.operations.list.DeleteOperation;
import com.data_structures_visualizer.visual.operations.list.InsertOperation;
import com.data_structures_visualizer.visual.operations.list.SearchOperation;
import com.data_structures_visualizer.visual.text.ExplanationTextParser;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class ListVisualizerController {
    @FXML
    private AnchorPane visualization_area;
    @FXML
    private Slider speed_visualization_slider;
    @FXML
    private Label speed_visualization_label;
    @FXML
    private Button singly_linked_list_btn;
    @FXML
    private Button doubly_linked_list_btn;
    @FXML
    private Button circular_linked_list_btn;
    @FXML 
    private Button list_btn;
    @FXML
    private Button stack_btn; 
    @FXML
    private Button queue_btn;
    @FXML
    private Button create_btn;
    @FXML 
    private Button insert_node_btn;
    @FXML 
    private Button delete_node_btn;
    @FXML 
    private Button search_value_btn;
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

    private ListLayoutManager listLayoutManager;
    private Button selectedButton;
    private ListType listType = ListType.SINGLY;

    private final ExplanationRepository explanationRepository = new ExplanationRepository();
    private AnimationTimeLine animationTimeLine = new AnimationTimeLine();

    private final SinglyLinkedList<Integer> singlyLinkedList = new SinglyLinkedList<Integer>(null);
    private final DoublyLinkedList<Integer> doublyLikedList = new DoublyLinkedList<Integer>(null);
    private final CircularLinkedList<Integer> circularLinkedList = new CircularLinkedList<Integer>(null);

    public enum ListType{
        SINGLY, DOUBLY, CIRCULAR
    }

    private static class SelectionWindowDialog{
        public static void show(String message, Runnable opt1, Runnable opt2, Runnable opt3){
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            Label msg = new Label(message);
            msg.setFont(Font.font(16));
            msg.setTextFill(Color.BLACK);

            Button option1 = new Button("Inserir no início");
            option1.setFont(Font.font(14));
            option1.setTextFill(Color.BLACK);
            option1.getStyleClass().add("standard-dark-gray-button");
         
            Button option2 = new Button("Inserir no fim");
            option2.setFont(Font.font(14));
            option2.setTextFill(Color.BLACK);
            option2.getStyleClass().add("standard-dark-gray-button");

            Button option3 = new Button("Inserir numa posição específica");
            option3.setFont(Font.font(14));
            option3.setTextFill(Color.BLACK);
            option3.getStyleClass().add("standard-dark-gray-button");

            option1.setOnAction(e -> {
                opt1.run();
                stage.close();
            });

            option2.setOnAction(e -> {
                opt2.run();
                stage.close();
            });

            option3.setOnAction(e -> {
                opt3.run();
                stage.close();
            });

            VBox layout = new VBox(20, msg, option1, option2, option3);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(30));

            Scene scene = new Scene(layout);
            scene.getStylesheets().add(ListVisualizerController.class.getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();
        }
    }

    @FXML
    public void initialize(){
        startLayoutManager();
        putStartExample();
        selectButton(singly_linked_list_btn);
        handleToSelectionListType();
        handleToScreenChange();
        setupOperations();
        setupControlButtons();
        setupListeners();
    }

    private void startLayoutManager(){
        listLayoutManager = new ListLayoutManager(
            visualization_area, animationTimeLine, listType, singlyLinkedList,
            doublyLikedList, circularLinkedList
        );

        listLayoutManager.build();
        listLayoutManager.setExplanationTextRect(LayoutManager.createExplanationRect(visualization_area));
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
            listLayoutManager.fixVisualizationAreaLayout(newVal.getWidth(), newVal.getHeight());
        });

        speed_visualization_slider.valueProperty().addListener((obs, oldValue, newVal) -> {
            speed_visualization_label.setText(String.format("%.1f", 1 + newVal.doubleValue() / 100) + "x");
            ListVisualizerConfig.speedVisualization = newVal.doubleValue();
        });

        visualization_progress.progressProperty().bind(animationTimeLine.progressProperty());

        animationTimeLine.setOnFinished(() -> {
            listLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(),
                visualization_area.getHeight()
            );
        });

        animationTimeLine.setOnStepChanged(index -> {
            List<ExplanationText> explanations = explanationRepository.get(index);

            if(!explanations.isEmpty()) {
                listLayoutManager.getExplanationTextRect().setContent(
                    ExplanationTextParser.parse(explanations)
                );
            } 
            
            else{
                listLayoutManager.getExplanationTextRect().clear();
            }
        });
    }

    private void putStartExample(){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();
        ArrayList<Arrow> arrows = listLayoutManager.getArrows();

        for(int i = 0; i < 10; ++i){ 
            singlyLinkedList.pushBack(i);
            doublyLikedList.pushBack(i);
            circularLinkedList.pushBack(i);

            nodes.add(i, new VisualNode(625 * ListVisualizerConfig.squareSize, 
                625 * ListVisualizerConfig.squareSize, Integer.toString(i))
            );

            visualization_area.getChildren().add(nodes.get(i));

            if(i < 9){
                arrows.add(i, new Arrow(
                    ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize
                ));
                
                visualization_area.getChildren().add(arrows.get(i));
            }
        } 
    }

    private void handleToSelectionListType(){
        singly_linked_list_btn.setOnAction(e -> {
            selectButton(singly_linked_list_btn);

            listType = ListType.SINGLY;
            listLayoutManager.setListType(listType);

            listLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(), visualization_area.getHeight()
            );

            animationTimeLine.clear();
            explanationRepository.clear();
        });

        doubly_linked_list_btn.setOnAction(e -> {
            selectButton(doubly_linked_list_btn);

            listType = ListType.DOUBLY;
            listLayoutManager.setListType(listType);

            listLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(), visualization_area.getHeight()
            );

            animationTimeLine.clear();
            explanationRepository.clear();
        });

        circular_linked_list_btn.setOnAction(e -> {
            selectButton(circular_linked_list_btn);

            listType = ListType.CIRCULAR;
            listLayoutManager.setListType(listType);

            listLayoutManager.fixVisualizationAreaLayout(
                visualization_area.getWidth(), visualization_area.getHeight()
            );
            
            animationTimeLine.clear();
            explanationRepository.clear();
        });
    }

    private void selectButton(Button btn){
        if(selectedButton != null){
            selectedButton.setTextFill(Color.BLACK);
        }

        btn.setTextFill(Color.WHITE);
        selectedButton = btn;
    }

    private void handleToScreenChange(){
        stack_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/StackVisualizerScreen.fxml");
        });

        queue_btn.setOnAction(e -> {
            SceneManager.changeScene("/fxml/QueueVisualizerScreen.fxml");
        });
    }

    private void setupOperations(){
        setupCreateList();
        setupInsertNode();
        setupDeleteNode();
        setupSearchNode();
        setupClearVisualization();
    }

    private void setupCreateList(){
        create_btn.setOnAction(e -> {
            DialogFactory.showInputDialog(
                "Insira o tamanho da lista: ", null, (Integer lenght, Integer v) -> {
                createList(lenght);
                listLayoutManager.fixVisualizationAreaLayout(
                    visualization_area.getWidth(), 
                    visualization_area.getHeight()
                );
            });
        });
    }
    
    private void setupInsertNode(){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();

        insert_node_btn.setOnAction(e -> {
            SelectionWindowDialog.show(
                "Selecione o tipo de inserção: ", 
                () -> {
                    DialogFactory.showInputDialog(
                        "Insira o valor para inserir na lista:", null,
                        (Integer value, Integer v) -> { insertNode(value, 0); }
                    );
                }, 
                () -> {
                    DialogFactory.showInputDialog(
                        "Insira o valor para inserir na lista:", null,
                        (Integer value, Integer v) -> { insertNode(value, nodes.size()); }
                    );
                },
                () -> {
                    DialogFactory.showInputDialog(
                        "Insira o valor para inserir na lista:", 
                        "Insira a posição para inserir na lista: ",
                        (Integer value, Integer pos) -> { insertNode(value, pos); }
                    );
                }
            );
        });
    }

    private void setupDeleteNode(){
        delete_node_btn.setOnAction(e -> {
            DialogFactory.showVerticalChoiceDialog(
                "Remover nó da lista.", "Remover por valor", 
                () -> {
                    DialogFactory.showInputDialog(
                        "Insira o valor para remover da lista: ", null, 
                        (Integer value, Integer v) -> { deleteNode(value, false); }
                    );
                }, "Remover por índice", 
                () -> {
                     DialogFactory.showInputDialog(
                        "Insira o índice do elemento para remover: ", null, 
                        (Integer index, Integer v) -> { deleteNode(index, true); }
                    );
                }
            );
        });
    }

    private void setupSearchNode(){
        search_value_btn.setOnAction(e -> {
            DialogFactory.showInputDialog("Insira o valor para buscar: ", 
                null, (Integer value, Integer v) -> { searchValue(value); }
            );
        });
    }

    private void setupClearVisualization(){
        clear_btn.setOnAction(e -> {
            DialogFactory.ConfirmDialog.show(
                "Tem certeza que deseja limpar a área de visualização?", () -> {
                listLayoutManager.clearVisualization();
            });
        });
    }

    private void createList(int lenght){
        if(lenght > ListVisualizerConfig.listMaxLimit){
            Util.showAlert(
                "Não foi possível criar a lista.",
                String.format("Tamanho máximo permitido: %d", ListVisualizerConfig.listMaxLimit),
                AlertType.CONFIRMATION
            );

            return;
        }

        listLayoutManager.clearVisualization();

        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();
        ArrayList<Arrow> arrows = listLayoutManager.getArrows();

        for(int i = 0; i < lenght; ++i){
            Integer randInt = ThreadLocalRandom.current().nextInt(0, 9999);

            singlyLinkedList.pushBack(randInt);
            doublyLikedList.pushBack(randInt);
            circularLinkedList.pushBack(randInt);
            
            nodes.add(new VisualNode(
                625 * ListVisualizerConfig.squareSize, 625 * ListVisualizerConfig.squareSize, 
                String.valueOf(randInt)
            ));

            visualization_area.getChildren().add(nodes.get(i));

            if(i < (lenght - 1)){
                arrows.add(new Arrow(ListVisualizerConfig.spacingBetweenNodes * ListVisualizerConfig.squareSize));
                visualization_area.getChildren().add(arrows.get(i));
            }
        }
    }

    private boolean validadeIndex(int index){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();

        if(index < 0 || index > (nodes.size() - 1)){
            Util.showAlert(
                "Indíce inválido!",
                String.format("O valor do índice deve estar entre 0 e %d.", nodes.size() - 1),
                AlertType.CONFIRMATION
            );

            return false;
        }

        return true;
    }

    private boolean validadeInsertion(int pos){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();

        if((nodes.size() + 1) > ListVisualizerConfig.listMaxLimit){
            Util.showAlert(
                "Lista cheia!",
                String.format("A lista atingiu o valor máximo: %d", ListVisualizerConfig.listMaxLimit),
                AlertType.CONFIRMATION
            );

            return false;
        }

        if(pos > nodes.size()){
            return validadeIndex(pos);
        }

        return true;
    }

    private boolean validadeDeletion(boolean removeByIndex, int index){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();

        if(nodes.isEmpty()){
            Util.showAlert(
                "Lista vazia!",
                "Não há elementos na lista para remover.",
                AlertType.CONFIRMATION
            );

            return false;
        }

        if(removeByIndex && !validadeIndex(index)){
            return false;
        }

        return true;
    }

    private InsertContext buildInsertContext(int value, int pos){
        ArrayList<VisualNode> nodes = listLayoutManager.getNodes();

        return new InsertContext(
            pos, value, listType, visualization_area.getWidth(), 
            visualization_area.getHeight(), nodes.size(), singlyLinkedList,
            doublyLikedList, circularLinkedList, explanationRepository
        );
    }

    private void insertNode(int value, int pos){
        animationTimeLine.clear();
        explanationRepository.clear();
        
        if(!validadeInsertion(pos)) return;

        InsertContext insertContext = buildInsertContext(value, pos);

        InsertOperation op = new InsertOperation(
            insertContext, visualization_area, listLayoutManager.getNodes(), 
            listLayoutManager.getArrows(), listLayoutManager.getPrevArrows(), 
            listLayoutManager.getCurvedArrow(), listLayoutManager.getHeadLabel(), 
            listLayoutManager.getTailLabel()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }

    private DeleteContext buildDeleteContext(int value, boolean removeByIndex){
        return new DeleteContext(
            value, listType, visualization_area.getWidth(), 
            visualization_area.getHeight(), listLayoutManager.getNodes().size(), 
            removeByIndex, singlyLinkedList, doublyLikedList, circularLinkedList,
            explanationRepository
        );
    }

    private void deleteNode(int value, boolean removeByIndex){
        animationTimeLine.clear();
        explanationRepository.clear();

        if(!validadeDeletion(removeByIndex, value)) return;

        DeleteContext deleteContext = buildDeleteContext(value, removeByIndex);

        DeleteOperation op = new DeleteOperation(
            deleteContext, visualization_area, listLayoutManager.getNodes(), 
            listLayoutManager.getArrows(), listLayoutManager.getPrevArrows(), 
            listLayoutManager.getCurvedArrow(), listLayoutManager.getHeadLabel(), 
            listLayoutManager.getTailLabel()
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }

    private void searchValue(int value){
        animationTimeLine.clear();
        explanationRepository.clear();

        SearchOperation op = new SearchOperation(
            singlyLinkedList, listLayoutManager.getNodes(), value, explanationRepository
        );

        op.build(animationTimeLine);
        animationTimeLine.play();
    }
}
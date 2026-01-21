package com.data_structures_visualizer.controllers;

import java.util.function.BiConsumer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MiniInputDialogController {
    @FXML
    private Label message_label;
    @FXML
    private TextField input_text_field;
    @FXML
    private Button send_button;
    @FXML
    private Button cancel_button;
    @FXML
    private HBox other_input_line;
    @FXML
    private Label message_label2;
    @FXML 
    TextField input_text_field2;
    
    private String message;
    private String message2;
    private final int maxDigits = 4;
    private final String warningMessage = String.format("A entrada deve ser numérica com o máximo de %d dígitos.", maxDigits);
    private Stage stage;
    private BiConsumer<Integer, Integer> onConfirm;

    @FXML
    public void initialize(){
        other_input_line.setManaged(false);

        cancel_button.setOnAction(e -> {
            stage.close();
        });

        send_button.setOnAction(e -> {
            handleWithInputValidation();
        });
    }

    public void setMessage(String message){
        this.message = message;
        message_label.setText(message);
    }

    public void setOtherMessage(String message){
        message2 = message;
        other_input_line.setManaged(message != null);
        other_input_line.setVisible(message != null);
        message_label2.setText(message);
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setOnconfirm(BiConsumer<Integer, Integer> onConfirm){
        this.onConfirm = onConfirm;
    }

    public Boolean validateInput(String text){
        return (text.length() <= maxDigits && text.matches("\\d+"));
    }

    public void handleWithInputValidation(){
        String inputText = input_text_field.getText();
        String inputText2 = input_text_field2.getText();

        if(validateInput(inputText) && (validateInput(inputText2) || !other_input_line.isManaged())){
            onConfirm.accept(
                Integer.parseInt(inputText), 
                Integer.parseInt(inputText2.isEmpty() ? "0" : inputText2)
            );
            
            stage.close();
        }

        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("Valor inválido!");
            alert.setContentText(warningMessage);
            alert.showAndWait();
        }
    }
}

package com.data_structures_visualizer.util;

import java.util.function.BiConsumer;

import com.data_structures_visualizer.controllers.MiniInputDialogController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class DialogFactory {
    public static class ConfirmDialog{
        public static void show(String message, Runnable onYes){
            Stage stage = new Stage();

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            Label msg = new Label(message);
            msg.setFont(Font.font(15));
            msg.setTextFill(Color.BLACK);

            Button yes = new Button("Sim");
            yes.setFont(Font.font(14));
            yes.setTextFill(Color.BLACK);
            yes.getStyleClass().add("standard-dark-gray-button");

            Button no = new Button("Não");
            no.setFont(Font.font(14));
            no.setTextFill(Color.BLACK);
            no.getStyleClass().add("standard-dark-gray-button");

            yes.setOnAction(e -> {
                onYes.run();
                stage.close();
            });

            no.setOnAction(e -> { stage.close(); });

            HBox options = new HBox(10, yes, no);
            VBox layout = new VBox(15, msg, options);
            
            options.setAlignment(Pos.CENTER);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Scene scene = new Scene(layout);
            scene.getStylesheets().add(DialogFactory.class.getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();
        }
    } 

    public static void showInputDialog(String message, String message2,  BiConsumer<Integer, Integer> onConfirm){
        try {
            FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource("/fxml/MiniInputDialog.fxml"));
            Parent root = loader.load();

            MiniInputDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            controller.setMessage(message);
            controller.setOtherMessage(message2);
            controller.setStage(stage);
            controller.setOnconfirm(onConfirm);

            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();   
        }
    }

    public static void showVerticalChoiceDialog(
        String message, String btn1Text, Runnable opt1, String btn2Text, Runnable opt2
    ) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        Label msg = new Label(message);
        msg.setFont(Font.font(16));

        Button b1 = new Button(btn1Text);
        Button b2 = new Button(btn2Text);

        b1.getStyleClass().add("standard-dark-gray-button");
        b2.getStyleClass().add("standard-dark-gray-button");

        b1.setOnAction(e -> { opt1.run(); stage.close(); });
        b2.setOnAction(e -> { opt2.run(); stage.close(); });

        VBox layout = new VBox(15, msg, b1, b2);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(
            DialogFactory.class.getResource("/css/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.showAndWait();    
    }
}

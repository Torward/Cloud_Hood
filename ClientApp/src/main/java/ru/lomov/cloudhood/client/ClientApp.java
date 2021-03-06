package ru.lomov.cloudhood.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent mainParent = FXMLLoader.load(getClass().getResource("/ru/lomov/cloudhood/client/cloudhood_view.fxml"));
        Scene scene = new Scene(mainParent);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("CLOUDHOOD");
        stage.getIcons().add(
                new Image(
                        ClientApp.class.getResourceAsStream("/ru/lomov/cloudhood/client/img/Icon.png")));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


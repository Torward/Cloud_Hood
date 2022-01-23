package ru.lomov.cloudhood.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/ru/lomov/cloudhood/client/cloudhood_view.fxml"));
        stage.setScene(new Scene(parent));
        stage.setTitle("CLOUDHOOD");
        stage.getIcons().add(
                new Image(
                        ClientApp.class.getResourceAsStream("/ru/lomov/cloudhood/client/img/Icon.png")));
        stage.show();
    }
}


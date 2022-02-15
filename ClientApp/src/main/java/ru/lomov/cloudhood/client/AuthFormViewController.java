package ru.lomov.cloudhood.client;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthFormViewController implements Initializable {
    @FXML
    private TextField loginRegField;
    @FXML
    private PasswordField passwordRegField;
    @FXML
    private PasswordField passwordRegFieldRe;
    @FXML
    private TextField nicknameRegField;
    @FXML
    private Button signUpBTN;
    @FXML
    private PasswordField passwordLoginField;
    @FXML
    private TextField nicknameLoginField;
    @FXML
    private Button signInBTN;
    @FXML
    private Button codeEnterBTN;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button signUpFormBTN;
    @FXML
    private Button signInFormBTN;
    private Parent fxml;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Итерация play();");
    }

    
    /*
     * Анимация форм---------------------------------------------------------------------------------------------------------------------------
     * */
    public void openSignInForm() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), contentPane);
        translateTransition.setToX(contentPane.getLayoutX() * 17.5);
        translateTransition.play();
        translateTransition.setOnFinished((e) -> {
            try {
                contentPane.getChildren().removeAll();
                fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/lomov/cloudhood/client/signin_form_view.fxml")));
                contentPane.getChildren().addAll(fxml);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    public void openSignUpForm() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), contentPane);
        translateTransition.setToX(0);
        translateTransition.play();
        translateTransition.setOnFinished((e) -> {
            try {
                contentPane.getChildren().removeAll();
                fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/lomov/cloudhood/client/signup_form_view.fxml")));
                contentPane.getChildren().addAll(fxml);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}

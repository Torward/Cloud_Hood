package ru.lomov.cloudhood.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button share_btn;
    @FXML
    private Button delete_btn;
    @FXML
    private GridPane serverPane;
    @FXML
    private AnchorPane dragAndDropPane;
    @FXML
    private Label text;
    @FXML
    private Button download_btn;
    @FXML
    private Button upload_btn;
    @FXML
    private TextArea log_area;
    @FXML
    private AnchorPane person;
    @FXML
    private ImageView ava;
    @FXML
    private Label nickname;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickname.setUnderline(true);
        nickname.setText("Егорыч");
        serverPane.addRow(3 ,new ImageView());
        serverPane.addColumn(3,new ImageView());
        for (int i = 0; i < serverPane.getColumnCount(); i++) {
            serverPane.add(getImagePane(),3,3);
        }


    }

    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
        }
    }

    public void dropped(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        File file = files.get(0);
        if (file != null) {
            sendFile(file);
            files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    public void sendDAnaD(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void chooseToUpload(ActionEvent actionEvent) {
        File serverRoot = new File("server/serverFiles");
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(serverRoot);
        File file = fileChooser.showOpenDialog(secondaryStage);
        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    private void sendFile(File file) {
    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(secondaryStage);
        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    public void fileChooseM(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(secondaryStage);
        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    public  ImageView getImagePane(){
        Image image = new Image("/ru/lomov/cloudhood/client/img/folder.png", 50.0D, 40.0D,false,false);
//        ImageView imageView = new ImageView(image);
        return new ImageView(image);
    }

    public void share(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }
    //graphics

    public void hover(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #F2F4F2;");
        text.setStyle("-fx-background-color: #F2F4F2;");
    }

    public void objectFree(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #FFFFFF;");
        text.setStyle("-fx-background-color:#FFFFFF;");
    }

    public void hoverDownloadBTN(MouseEvent mouseEvent) {
        download_btn.setStyle("-fx-background-color: #6abcd0;");
    }

    public void hoverSendBTN(MouseEvent mouseEvent) {
        upload_btn.setStyle("-fx-background-color: #6abcd0;");
    }

    public void BTNFree(MouseEvent mouseEvent) {
        download_btn.setStyle("-fx-background-color: #4b78bb;");
    }

    public void sendBTNFree(MouseEvent mouseEvent) {
        upload_btn.setStyle("-fx-background-color: #4b78bb;");
    }

    public void hoverDeleteBTN(MouseEvent mouseEvent) {
        delete_btn.setStyle("-fx-background-color: #6abcd0;");
    }

    public void deleteBTNFree(MouseEvent mouseEvent) {
        delete_btn.setStyle("-fx-background-color: #4b78bb;");
    }

    public void shareBTNFree(MouseEvent mouseEvent) {
        share_btn.setStyle("-fx-background-color: #4b78bb;");
    }

    public void hoverShareBTN(MouseEvent mouseEvent) {
        share_btn.setStyle("-fx-background-color: #6abcd0;");
    }
}
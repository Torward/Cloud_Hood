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
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private Button share_btn;
    @FXML
    private Button delete_btn;
    @FXML
    private TilePane serverPane;
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


    private final Network network = new Network();
    private Socket socket;

//    public static final byte SIGNAL_SEND_FILE = 17;
//    public static final byte SIGNAL_SEND_COMMAND = 27;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickname.setUnderline(true);
        nickname.setText("Егорыч");
        network.connect();
        socket = network.getSocket();

    }

    // Командные методы-----------------------------------------------

    public void dropped(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        File fileToSend = files.get(0);
        if (fileToSend != null) {
            try {
                network.sendFile(fileToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void sendDAnaD(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void chooseToUpload(ActionEvent actionEvent) {
    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File fileToSend = fileChooser.showOpenDialog(secondaryStage);
        if (fileToSend != null) {
            try {
                network.sendFile(fileToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<File> files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void fileChooseM(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File fileToSend = fileChooser.showOpenDialog(secondaryStage);
        if (fileToSend != null) {
            try {
                network.sendFile(fileToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<File> files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void share(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }

    //--------------------------------------------------------------------------
    //graphics

    public ImageView getImagePane(File file) {

        Image image = new Image("/ru/lomov/cloudhood/client/img/folder.png", 50.0D, 40.0D, false, false);
//        ImageView imageView = new ImageView(image);
        return new ImageView(image);
    }

    public void hover(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #F2F4F2;");
        text.setStyle("-fx-background-color: #F2F4F2;");
    }

    public void objectFree(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #FFFFFF;");
        text.setStyle("-fx-background-color:#FFFFFF;");
    }


    //логгирование

    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
        }
    }

    private void printLog(TextArea textArea, String log) {

        textArea.appendText(log + "\n");
    }

}

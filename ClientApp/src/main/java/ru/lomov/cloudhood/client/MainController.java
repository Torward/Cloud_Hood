package ru.lomov.cloudhood.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private ImageView reloadImg;
    @FXML
    private TableView<FileInfo> serverView;
    @FXML
    private TableColumn<FileInfo, String> name;
    @FXML
    private TableColumn<FileInfo, String> size;
    @FXML
    private TableColumn<FileInfo, String> date;
    @FXML
    private TableColumn<FileInfo, ImageView> image;
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


    private ClientNetworkNetty network;
    private Socket socket;

    private static final String DELETE = "delete";
    private static final String SHARE = "share";
    private static final String RECEIVE = "sendMeFile";
    private static final String RELOAD = "##sendMeListOfFiles";

    private ObservableList<FileInfo> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nickname.setUnderline(true);
        nickname.setText("Егорыч");
        network = new ClientNetworkNetty();
        name.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("date"));
       initReload();
    }

    // Командные методы-----------------------------------------------

    public void dropped(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        File fileToSend = files.get(0);
        if (fileToSend != null) {

            network.sendMessage(fileToSend);

            files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void sendDAnaD(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File fileToSend = fileChooser.showOpenDialog(secondaryStage);
        if (fileToSend != null) {

            network.sendMessage(fileToSend);

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
            network.sendMessage(fileToSend);

            List<File> files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void share(ActionEvent actionEvent) {
//        try {
//            network.sendTextMsg(SHARE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void delete(ActionEvent actionEvent) {
        String fileName = serverView.getSelectionModel().getSelectedItem().getName();
        network.deleteFile(fileName);

    }

    public void download() throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem().getName();
        network.getFile(fileName);
    }

    public void initReload() {
        network.initRefreshFileList();
        list = FXCollections.observableArrayList(network.getList());
        System.out.println("Element in collection: " + list.size());
        serverView.setItems(list);
        serverView.comparatorProperty();
    }
    public void reload() {
        network.refreshFileList();
        list = FXCollections.observableArrayList(network.getList());
        System.out.println("Element in collection: " + list.size());
        serverView.setItems(list);
        serverView.comparatorProperty();
    }

    //--------------------------------------------------------------------------
    //graphics

    public ImageView getImagePane(File file) {
        Image image = new Image("/ru/lomov/cloudhood/client/img/folder.png", 50.0D, 40.0D, false, false);
        return new ImageView(image);
    }

    public void hover(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #ffffff65;");
        text.setStyle("-fx-background-color: #ffffff65;" +
                "-fx-text-fill:black;");
    }

    public void objectFree(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #4b78bb;");
        text.setStyle("-fx-background-color:#4b78bb;");
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

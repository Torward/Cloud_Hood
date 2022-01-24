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
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
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
    DataInputStream in;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickname.setUnderline(true);
        nickname.setText("Егорыч");
        network.connect();
        socket = network.getSocket();

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
//        File serverRoot = new File("ServerApp/serverFiles");
//        FileChooser fileChooser = new FileChooser();
//        Stage secondaryStage = new Stage();
//        fileChooser.setTitle("Выберете файл для отправки");
//        fileChooser.setInitialDirectory(serverRoot);
//        File file = fileChooser.showOpenDialog(secondaryStage);
//        if (file != null) {
//            sendFile(file);
//            List<File> files = Collections.singletonList(file);
//            printLog(log_area, files);
//        }
    }

    // Командные методы

    private void sendFile(File file) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    //network.connect();
                    String fileName = file.getName();
                    DataOutputStream sendMsgStream = new DataOutputStream(socket.getOutputStream());
                    sendMsgStream.writeUTF(fileName);
                    byte[] byteArray = new byte[(int) file.length()];
                    FileInputStream fIn = new FileInputStream(file.getPath());
                    OutputStream out = socket.getOutputStream();
//                    long s;
//                    s= file.length();
//                    System.out.println(s);
//                    int sp = (int)(s/512);
//                    if ((s % 512) != 0) sp++;
                    // Здесь код прогрессбара(file.getPath(),sp);
                    //BufferedOutputStream bOut = new BufferedOutputStream(socket.getOutputStream());
                    Thread.sleep(500);
                    //sendMsgStream.writeInt(fileName.length());
                    while (true) {
                        while ((fIn.read(byteArray)) >= 0) {
                            // int len = fIn.read(byteArray);
                            out.write(byteArray);
                            //  Прогрессбар.progressInStream(1);
                        }
                        fIn.close();
                        out.close();
//                        sendMsgStream.flush();
//

                    }

                } catch (FileNotFoundException e) {
                    System.err.println("Файл не найден!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                printLog(log_area, "Файл отправлен");
                //рефреш
            }
        };
        new Thread(r).start();
    }

    public void receiveMsg(String filename) {
        try {
            long s;
            s = Long.parseLong(in.readUTF());

            System.out.println("File size: " + s);
            byte[] byteArray = new byte[1024];
            new File("ClientApp/clientFiles").mkdir();
            File f = new File("./clientFiles/" + filename);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            int sp = (int) (s / 1024);
            if (s % 1024 != 0) sp++;
            // Main.mainFrame.createProgressPanel(filename, sp);
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            while (s > 0) {
                int i = bis.read(byteArray);
                fos.write(byteArray, 0, i);
                //  Main.mainFrame.progressInStream(1);
                s -= i;
            }
            fos.close();
        } catch (IOException e) {
            System.err.println("Recieve IO Error");
        }
        // new JOptionPane().showMessageDialog(null, "Recieved " + filename);
    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        // fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
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
        //fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File file = fileChooser.showOpenDialog(secondaryStage);
        if (file != null) {
            sendFile(file);
            List<File> files = Collections.singletonList(file);
            printLog(log_area, files);
        }
    }

    public ImageView getImagePane() {
        Image image = new Image("/ru/lomov/cloudhood/client/img/folder.png", 50.0D, 40.0D, false, false);
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

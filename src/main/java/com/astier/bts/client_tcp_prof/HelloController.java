package com.astier.bts.client_tcp_prof;

import com.astier.bts.client_tcp_prof.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import static javafx.scene.paint.Color.*;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;
    public TextField TextFieldIP;
    public TextField TextFieldPort;
    public TextField TextFieldRequette;
    public Circle voyant;
    public TextArea TextAreaReponses;
    static public TCP tcp;
    static boolean enRun = false;
    String adresse,port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voyant.setFill(RED);
        connecter.setOnAction(event -> {
            try{
                connecter();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        deconnecter.setOnAction(event -> {
           try{
               deconnecter();
           }catch (Exception e){
               throw new RuntimeException(e);
           }
        });
        button.setOnAction(event -> {
            try{
                envoyer();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });


    }


    private void envoyer() throws InterruptedException {

    }

    private void deconnecter() throws InterruptedException {

    }

    private void connecter() throws UnknownHostException {
        adresse = TextFieldIP.getText();
        port = TextFieldPort.getText();
        if (adresse.isEmpty()||port.isEmpty()){
            TextAreaReponses.appendText("Erreur : veuillez entrer un adresse et un port\n");
            return;
        }
        int portInt = Integer.parseInt(port);
        InetAddress serveur = InetAddress.getByName(adresse);
        tcp = new TCP(serveur, portInt, this);
        tcp.connection();
        tcp.start();
        enRun = true;
        voyant.setFill(GREEN);
        TextAreaReponses.appendText("Connexion en cours\n");
    }

}
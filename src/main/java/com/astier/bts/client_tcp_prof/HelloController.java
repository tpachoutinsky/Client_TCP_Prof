package com.astier.bts.client_tcp_prof;

import com.astier.bts.client_tcp_prof.aes.Aes_cbc;
import com.astier.bts.client_tcp_prof.aes.Outils;
import com.astier.bts.client_tcp_prof.tcp.TCP;
import com.astier.bts.client_tcp_prof.tcp.TCPBin;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    static public TCPBin tcp;
    static boolean enRun = false;
    String adresse,port;
    public Aes_cbc aes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        voyant.setFill(RED);
        try {
            chargerConfigAES();
        } catch (Exception e) {
            TextAreaReponses.appendText("Erreur config AES : " + e.getMessage() + "\n");
        }

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

    private void chargerConfigAES() {
        try {
            // Lecture du fichier JSON dans /resources
            InputStream is = getClass().getResourceAsStream("/configuration_json.json");

            if (is == null) {
                TextAreaReponses.appendText("Erreur : fichier JSON introuvable dans /resources\n");
                return;
            }

            String json = new String(is.readAllBytes());

            // Extraction manuelle des champs
            String keyStr = json.split("\"motDePasse\"")[1]
                    .split(":")[1]
                    .replace("\"", "")
                    .replace(",", "")
                    .trim();

            String ivStr = json.split("\"iv\"")[1]
                    .split(":")[1]
                    .replace("\"", "")
                    .replace("}", "")
                    .trim();

            // Normalisation en 16 octets via ta classe Outils
            byte[] key = Outils.normalizeChaine(keyStr, 16);
            byte[] iv  = Outils.normalizeChaine(ivStr, 16);

            // Initialisation AES
            aes = new Aes_cbc(key, iv);

            TextAreaReponses.appendText("AES chargé depuis /resources\n");

        } catch (Exception e) {
            TextAreaReponses.appendText("Erreur config AES : " + e.getMessage() + "\n");
        }
    }






    private void envoyer() {
        String requette = TextFieldRequette.getText();

        if (requette.isEmpty()) {
            TextAreaReponses.appendText("Requête vide\n");
            return;
        }

        if (!enRun || tcp == null) {
            TextAreaReponses.appendText("Non connecté\n");
            return;
        }

        try {
            tcp.requette(requette);
            TextAreaReponses.appendText("Requête envoyée : " + requette + "\n");

        } catch (Exception e) {
            TextAreaReponses.appendText("Erreur lors de l'envoi : " + e.getMessage() + "\n");
        }
    }



    private void deconnecter() throws InterruptedException {
        try {
            if (tcp != null && enRun) {
                tcp.deconnection();
                enRun = false;
                voyant.setFill(RED);
                TextAreaReponses.appendText("Déconnexion effectuée\n");
            }
        } catch (Exception e) {
            TextAreaReponses.appendText("Erreur lors de la déconnexion : " + e.getMessage() + "\n");
        }
    }

    private void connecter() throws UnknownHostException, InterruptedException {
        adresse = TextFieldIP.getText();
        port = TextFieldPort.getText();
        if (adresse.isEmpty()||port.isEmpty()){
            TextAreaReponses.appendText("Erreur : veuillez entrer un adresse et un port\n");
            return;
        }
        int portInt = Integer.parseInt(port);
        InetAddress serveur = InetAddress.getByName(adresse);
        tcp = new TCPBin(serveur, portInt, this);

        tcp.connection();
        tcp.start();
        enRun=true;
        voyant.setFill(GREEN);

    }

}
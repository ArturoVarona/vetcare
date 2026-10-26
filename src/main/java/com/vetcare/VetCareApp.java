
package com.vetcare;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VetCareApp extends Application {

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("VetCare");
        titulo.setStyle(
            "-fx-font-size: 32px; -fx-font-weight: bold;"
        );

        Label subtitulo = new Label(
            "Sistema de gestión veterinaria"
        );

        Button iniciarSesion = new Button("Iniciar sesión");

        VBox contenido = new VBox(
            15, titulo, subtitulo, iniciarSesion
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Scene escena = new Scene(contenido, 600, 400);

        stage.setTitle("VetCare - Clínica Veterinaria");
        stage.setScene(escena);
        stage.setMinWidth(450);
        stage.setMinHeight(300);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.vetcare;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.repository.UserRepository;
import com.vetcare.repository.jdbc.JdbcUserRepository;
import com.vetcare.service.AuthService;
import com.vetcare.service.SupabaseAuthService;
import com.vetcare.ui.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();            // Singleton

        UserRepository userRepository = new JdbcUserRepository(db);          // detalle concreto
        AuthService authService = new AuthService(new SupabaseAuthService(), userRepository);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        loader.setControllerFactory(type -> new LoginController(authService));

        stage.setScene(new Scene(loader.load()));
        stage.setTitle("VetCare · Iniciar sesión");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
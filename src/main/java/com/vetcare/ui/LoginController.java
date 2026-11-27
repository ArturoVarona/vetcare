package com.vetcare.ui;

import com.vetcare.domain.User;
import com.vetcare.exception.AuthenticationException;
import com.vetcare.exception.BusinessRule;
import com.vetcare.exception.DataAccessException;
import com.vetcare.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void onLogin() {
        lblError.setStyle("-fx-text-fill: #c0392b;");
        lblError.setText("");
        try {
            User user = authService.login(txtUsername.getText(), txtPassword.getText());
            // Temporal: cuando exista el menú principal, aquí se navega a él.
            lblError.setStyle("-fx-text-fill: #1e8449;");
            lblError.setText("Bienvenido, " + user.getFullName() + " (" + user.getRole() + ")");
        } catch (AuthenticationException | BusinessRule e) {
            lblError.setText(e.getMessage());
        } catch (DataAccessException e) {
            lblError.setText("No hay conexión con la base de datos. Intente de nuevo.");
        } catch (RuntimeException e) {
            lblError.setText("Ocurrió un error inesperado. Intente de nuevo.");
        }
    }
}
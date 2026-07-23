package com.login;

import javax.swing.JOptionPane;
import java.awt.Component;

import com.login.exceptions.CredencialesInvalidasException;
import com.login.exceptions.CuentaBloqueadaException;

public class LoginUsuario {
    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASENA_VALIDA = "123456";
    private static final int INTENTOS_MAXIMOS = 3;

    private LoginService loginService;
    private JOptionPaneWrapper jOptionPaneWrapper;

    public LoginUsuario(LoginService loginService, JOptionPaneWrapper jOptionPaneWrapper) {
        this.loginService = loginService;
        this.jOptionPaneWrapper = jOptionPaneWrapper;
    }

    public LoginUsuario() {
        this(new LoginService(USUARIO_VALIDO, CONTRASENA_VALIDA, INTENTOS_MAXIMOS),
                new JOptionPaneWrapper());
    }

    public static void main(String[] args) {
        new LoginUsuario().ejecutar();
    }

    public void ejecutar() {
        jOptionPaneWrapper.showMessageDialog(
                null,
                "Bienvenido al Sistema de Login\n" +
                        "Tienes " + INTENTOS_MAXIMOS + " intentos para ingresar.",
                "Sistema de Autenticación",
                JOptionPane.INFORMATION_MESSAGE
        );

        while (!loginService.isBloqueado()) {
            String usuario = jOptionPaneWrapper.showInputDialog(
                    null,
                    "Ingrese su nombre de usuario:",
                    "Login - Intento " + (loginService.getIntentos() + 1) + "/" + INTENTOS_MAXIMOS,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (usuario == null) {
                jOptionPaneWrapper.showMessageDialog(
                        null,
                        "Operación cancelada por el usuario.",
                        "Cancelado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String contrasena = jOptionPaneWrapper.showInputDialog(
                    null,
                    "Ingrese su contraseña:",
                    "Login - Intento " + (loginService.getIntentos() + 1) + "/" + INTENTOS_MAXIMOS,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (contrasena == null) {
                jOptionPaneWrapper.showMessageDialog(
                        null,
                        "Operación cancelada por el usuario.",
                        "Cancelado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                LoginService.ResultadoLogin resultado = loginService.intentarLogin(usuario, contrasena);

                if (resultado.isExitoso()) {
                    jOptionPaneWrapper.showMessageDialog(
                            null,
                            resultado.getMensaje(),
                            "Login Exitoso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            } catch (CredencialesInvalidasException e) {
                jOptionPaneWrapper.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (CuentaBloqueadaException e) {
                jOptionPaneWrapper.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Cuenta Bloqueada",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            } catch (RuntimeException e) {
                jOptionPaneWrapper.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
    }

    public static class JOptionPaneWrapper {
        public void showMessageDialog(Component parentComponent, Object message,
                                      String title, int messageType) {
            JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
        }

        public String showInputDialog(Component parentComponent, Object message,
                                      String title, int messageType) {
            return JOptionPane.showInputDialog(parentComponent, message, title, messageType);
        }
    }
}
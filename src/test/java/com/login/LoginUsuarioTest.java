package com.login;

import com.login.exceptions.CredencialesInvalidasException;
import com.login.exceptions.CuentaBloqueadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JOptionPane;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de LoginUsuario - Cobertura 100%")
class LoginUsuarioTest {

    @Mock
    private LoginService loginService;

    @Mock
    private LoginUsuario.JOptionPaneWrapper jOptionPaneWrapper;

    private LoginUsuario loginUsuario;

    @BeforeEach
    void setUp() {
        loginUsuario = new LoginUsuario(loginService, jOptionPaneWrapper);
    }

    @Nested
    @DisplayName("Pruebas de login exitoso")
    class LoginExitosoTest {

        @Test
        @DisplayName("Debería mostrar mensaje de bienvenida al iniciar")
        void deberiaMostrarBienvenida() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("admin", "123456");
            when(loginService.intentarLogin("admin", "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "Login exitoso", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Bienvenido al Sistema de Login"),
                    eq("Sistema de Autenticación"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería mostrar mensaje de éxito con credenciales correctas")
        void deberiaMostrarMensajeExito() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("admin", "123456");
            when(loginService.intentarLogin("admin", "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "¡Acceso concedido! Bienvenido admin", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Acceso concedido"),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería mostrar el nombre de usuario en el mensaje de éxito")
        void deberiaMostrarNombreUsuario() throws Exception {
            // Arrange
            String usuario = "admin";
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(usuario, "123456");
            when(loginService.intentarLogin(usuario, "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "¡Acceso concedido! Bienvenido " + usuario, false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    mensajeCaptor.capture(),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
            assertThat(mensajeCaptor.getValue()).contains(usuario);
        }

        @Test
        @DisplayName("Debería llamar a loginService con las credenciales correctas")
        void deberiaLlamarLoginServiceConCredencialesCorrectas() throws Exception {
            // Arrange
            String usuario = "admin";
            String contrasena = "123456";

            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(usuario, contrasena);
            when(loginService.intentarLogin(usuario, contrasena))
                    .thenReturn(new LoginService.ResultadoLogin(true, "Exitoso", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(loginService).intentarLogin(usuario, contrasena);
        }
    }

    @Nested
    @DisplayName("Pruebas de login fallido")
    class LoginFallidoTest {

        @Test
        @DisplayName("Debería mostrar mensaje de error con credenciales incorrectas")
        void deberiaMostrarMensajeError() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user", "wrongpass");
            when(loginService.intentarLogin("user", "wrongpass"))
                    .thenThrow(new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("incorrectos"),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería mostrar los intentos restantes en mensaje de error")
        void deberiaMostrarIntentosRestantes() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user", "wrongpass");
            when(loginService.intentarLogin("user", "wrongpass"))
                    .thenThrow(new CredencialesInvalidasException("Intentos restantes: 2"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    mensajeCaptor.capture(),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
            assertThat(mensajeCaptor.getValue()).contains("Intentos restantes: 2");
        }

        @Test
        @DisplayName("Debería permitir múltiples intentos fallidos")
        void deberiaPermitirMultiplesIntentosFallidos() throws Exception {
            // Arrange
            when(loginService.isBloqueado())
                    .thenReturn(false, false, false, true);
            when(loginService.getIntentos())
                    .thenReturn(0, 1, 2);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user1", "pass1", "user2", "pass2", "user3", "pass3");

            when(loginService.intentarLogin("user1", "pass1"))
                    .thenThrow(new CredencialesInvalidasException("Intento 1 fallido"));
            when(loginService.intentarLogin("user2", "pass2"))
                    .thenThrow(new CredencialesInvalidasException("Intento 2 fallido"));
            when(loginService.intentarLogin("user3", "pass3"))
                    .thenThrow(new CuentaBloqueadaException("Cuenta bloqueada"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(loginService, times(3)).intentarLogin(anyString(), anyString());
            verify(jOptionPaneWrapper, times(3)).showMessageDialog(
                    isNull(),
                    anyString(),
                    anyString(),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas de bloqueo")
    class BloqueoTest {

        @Test
        @DisplayName("Debería mostrar mensaje de bloqueo después de 3 intentos fallidos")
        void deberiaMostrarMensajeBloqueo() throws Exception {
            // Arrange
            when(loginService.isBloqueado())
                    .thenReturn(false, false, false, true);
            when(loginService.getIntentos())
                    .thenReturn(0, 1, 2);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user1", "pass1", "user2", "pass2", "user3", "pass3");

            when(loginService.intentarLogin("user1", "pass1"))
                    .thenThrow(new CredencialesInvalidasException("Intento 1"));
            when(loginService.intentarLogin("user2", "pass2"))
                    .thenThrow(new CredencialesInvalidasException("Intento 2"));
            when(loginService.intentarLogin("user3", "pass3"))
                    .thenThrow(new CuentaBloqueadaException("Cuenta bloqueada por exceso de intentos"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("bloqueada"),
                    eq("Cuenta Bloqueada"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }

        @Test
        @DisplayName("No debería permitir intentos después del bloqueo")
        void noDeberiaPermitirIntentosDespuesBloqueo() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(true);

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper, never()).showInputDialog(
                    any(),
                    anyString(),
                    anyString(),
                    anyInt()
            );
        }
    }

    @Nested
    @DisplayName("Pruebas de cancelación")
    class CancelacionTest {

        @Test
        @DisplayName("Debería manejar cancelación al ingresar usuario")
        void deberiaManejarCancelacionUsuario() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(null);

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("cancelada"),
                    eq("Cancelado"),
                    eq(JOptionPane.WARNING_MESSAGE)
            );
            verify(loginService, never()).intentarLogin(anyString(), anyString());
        }

        @Test
        @DisplayName("Debería manejar cancelación al ingresar contraseña")
        void deberiaManejarCancelacionContrasena() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("admin")
                    .thenReturn(null);

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("cancelada"),
                    eq("Cancelado"),
                    eq(JOptionPane.WARNING_MESSAGE)
            );
            verify(loginService, never()).intentarLogin(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Pruebas de flujo completo")
    class FlujoCompletoTest {

        @Test
        @DisplayName("Debería manejar el flujo completo: bienvenida -> login exitoso")
        void deberiaManejarFlujoCompletoExitoso() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("admin", "123456");
            when(loginService.intentarLogin("admin", "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "¡Acceso concedido!", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Acceso concedido"),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería manejar el flujo: bienvenida -> error -> éxito")
        void deberiaManejarFlujoErrorExito() throws Exception {
            // Arrange
            when(loginService.isBloqueado())
                    .thenReturn(false, false, true);
            when(loginService.getIntentos())
                    .thenReturn(0, 1);

            doReturn("user", "pass", "admin", "123456")
                    .when(jOptionPaneWrapper).showInputDialog(any(), anyString(), anyString(), anyInt());

            when(loginService.intentarLogin("user", "pass"))
                    .thenThrow(new CredencialesInvalidasException("Error en primer intento"));
            when(loginService.intentarLogin("admin", "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "¡Acceso concedido!", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Error"),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Acceso concedido"),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
            verify(loginService, times(2)).intentarLogin(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Pruebas de cobertura adicional - 100%")
    class CoberturaAdicionalTest {

        @Test
        @DisplayName("Debería usar el constructor por defecto")
        void deberiaUsarConstructorPorDefecto() throws Exception {
            // Act
            LoginUsuario loginUsuarioDefault = new LoginUsuario();

            // Assert
            assertThat(loginUsuarioDefault).isNotNull();
            assertThat(loginUsuarioDefault).hasFieldOrProperty("loginService");
            assertThat(loginUsuarioDefault).hasFieldOrProperty("jOptionPaneWrapper");
        }

        @Test
        @DisplayName("Debería manejar bloqueo después de excepción")
        void deberiaManejarBloqueoDespuesDeExcepcion() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user", "wrong");
            when(loginService.intentarLogin("user", "wrong"))
                    .thenThrow(new CuentaBloqueadaException("Cuenta bloqueada"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Cuenta bloqueada"),
                    eq("Cuenta Bloqueada"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería manejar excepción genérica de login")
        void deberiaManejarExcepcionGenerica() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user", "pass");
            when(loginService.intentarLogin("user", "pass"))
                    .thenThrow(new CredencialesInvalidasException("Error inesperado"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Error inesperado"),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería manejar RuntimeException en ejecutar")
        void deberiaManejarRuntimeException() throws Exception {
            // Arrange
            when(loginService.isBloqueado()).thenReturn(false, true);
            when(loginService.getIntentos()).thenReturn(0);
            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user", "pass");
            when(loginService.intentarLogin("user", "pass"))
                    .thenThrow(new RuntimeException("Error inesperado en el sistema"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Error inesperado en el sistema"),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería manejar múltiples intentos con cancelación intermedia")
        void deberiaManejarMultiplesIntentosConCancelacion() throws Exception {
            // Arrange
            when(loginService.isBloqueado())
                    .thenReturn(false, false, false);
            when(loginService.getIntentos())
                    .thenReturn(0, 1);

            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user1", "pass1")  // Primer intento
                    .thenReturn(null);              // Segundo intento - cancelación

            when(loginService.intentarLogin("user1", "pass1"))
                    .thenThrow(new CredencialesInvalidasException("Usuario incorrecto"));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(loginService, times(1)).intentarLogin(anyString(), anyString());
            verify(jOptionPaneWrapper, times(1)).showMessageDialog(
                    isNull(),
                    contains("cancelada"),
                    eq("Cancelado"),
                    eq(JOptionPane.WARNING_MESSAGE)
            );
        }

        @Test
        @DisplayName("Debería manejar el flujo completo con múltiples intentos fallidos y éxito")
        void deberiaManejarFlujoCompletoConMultiplesIntentos() throws Exception {
            // Arrange
            when(loginService.isBloqueado())
                    .thenReturn(false, false, false, true);
            when(loginService.getIntentos())
                    .thenReturn(0, 1, 2);

            when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn("user1", "pass1")
                    .thenReturn("user2", "pass2")
                    .thenReturn("admin", "123456");

            when(loginService.intentarLogin("user1", "pass1"))
                    .thenThrow(new CredencialesInvalidasException("Intento 1 fallido"));
            when(loginService.intentarLogin("user2", "pass2"))
                    .thenThrow(new CredencialesInvalidasException("Intento 2 fallido"));
            when(loginService.intentarLogin("admin", "123456"))
                    .thenReturn(new LoginService.ResultadoLogin(true, "¡Acceso concedido!", false));

            // Act
            loginUsuario.ejecutar();

            // Assert
            verify(loginService, times(3)).intentarLogin(anyString(), anyString());
            verify(jOptionPaneWrapper, times(2)).showMessageDialog(
                    isNull(),
                    anyString(),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Acceso concedido"),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
        }
    }
}
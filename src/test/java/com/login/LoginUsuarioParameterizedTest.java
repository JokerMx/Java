package com.login;

import com.login.exceptions.CredencialesInvalidasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.JOptionPane;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas parametrizadas")
class LoginUsuarioParameterizedTest {

    @Mock
    private LoginService loginService;

    @Mock
    private LoginUsuario.JOptionPaneWrapper jOptionPaneWrapper;

    private LoginUsuario loginUsuario;

    @BeforeEach
    void setUp() {
        loginUsuario = new LoginUsuario(loginService, jOptionPaneWrapper);
    }

    @ParameterizedTest
    @CsvSource({
            "admin, 123456, true",
            "admin, wrongness, false",
            "user, 123456, false",
            "user, wrongness, false"
    })
    @DisplayName("Debería validar múltiples combinaciones de credenciales")
    void deberiaValidarMultiplesCombinaciones(String usuario, String contrasena, boolean expectedSuccess) throws Exception {
        // Arrange - IMPORTANTE: usar thenReturn(false, true) para evitar loop infinito
        when(loginService.isBloqueado()).thenReturn(false, true);
        when(loginService.getIntentos()).thenReturn(0);

        when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                .thenReturn(usuario, contrasena);

        if (expectedSuccess) {
            when(loginService.intentarLogin(usuario, contrasena))
                    .thenReturn(new LoginService.ResultadoLogin(true, "Exitoso", false));
        } else {
            when(loginService.intentarLogin(usuario, contrasena))
                    .thenThrow(new CredencialesInvalidasException("Fallido"));
        }

        // Act
        loginUsuario.ejecutar();

        // Assert
        if (expectedSuccess) {
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    contains("Exitoso"),
                    eq("Login Exitoso"),
                    eq(JOptionPane.INFORMATION_MESSAGE)
            );
        } else {
            verify(jOptionPaneWrapper).showMessageDialog(
                    isNull(),
                    anyString(),
                    eq("Error de Autenticación"),
                    eq(JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    @ParameterizedTest
    @MethodSource("proveedorDatosLogin")
    @DisplayName("Debería probar escenarios complejos")
    void deberiaProbarEscenariosComplejos(String usuario, String contrasena,
                                          boolean loginExitoso, String mensajeEsperado) throws Exception {
        // Arrange - IMPORTANTE: usar thenReturn(false, true) para evitar loop infinito
        when(loginService.isBloqueado()).thenReturn(false, true);
        when(loginService.getIntentos()).thenReturn(0);

        when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                .thenReturn(usuario, contrasena);

        if (loginExitoso) {
            when(loginService.intentarLogin(usuario, contrasena))
                    .thenReturn(new LoginService.ResultadoLogin(true, mensajeEsperado, false));
        } else {
            when(loginService.intentarLogin(usuario, contrasena))
                    .thenThrow(new CredencialesInvalidasException(mensajeEsperado));
        }

        // Act
        loginUsuario.ejecutar();

        // Assert
        verify(jOptionPaneWrapper).showMessageDialog(
                isNull(),
                contains(mensajeEsperado),
                anyString(),
                anyInt()
        );
    }

    static Stream<Arguments> proveedorDatosLogin() {
        return Stream.of(
                Arguments.of("admin", "123456", true, "Acceso concedido"),
                Arguments.of("admin", "wrong", false, "incorrectos"),
                Arguments.of("", "123456", false, "incorrectos"),
                Arguments.of("admin", "", false, "incorrectos"),
                Arguments.of("   ", "   ", false, "incorrectos"),
                Arguments.of("admin123", "123456", false, "incorrectos"),
                Arguments.of("admin", "1234567", false, "incorrectos")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "user", "guest", "test"})
    @DisplayName("Debería mostrar el nombre de usuario en los mensajes")
    void deberiaMostrarNombreUsuarioEnMensajes(String usuario) throws Exception {
        // Arrange - IMPORTANTE: usar thenReturn(false, true) para evitar loop infinito
        when(loginService.isBloqueado()).thenReturn(false, true);
        when(loginService.getIntentos()).thenReturn(0);

        when(jOptionPaneWrapper.showInputDialog(any(), anyString(), anyString(), anyInt()))
                .thenReturn(usuario, "123456");
        when(loginService.intentarLogin(usuario, "123456"))
                .thenReturn(new LoginService.ResultadoLogin(true, "Bienvenido " + usuario, false));

        // Act
        loginUsuario.ejecutar();

        // Assert
        verify(jOptionPaneWrapper).showMessageDialog(
                isNull(),
                contains(usuario),
                eq("Login Exitoso"),
                eq(JOptionPane.INFORMATION_MESSAGE)
        );
    }
}
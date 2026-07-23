package com.login.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas de las excepciones personalizadas")
class LoginExceptionTest {

    @Test
    @DisplayName("Debería crear LoginException con mensaje")
    void deberiaCrearLoginExceptionConMensaje() {
        // Arrange
        String mensaje = "Error de login";

        // Act
        LoginException exception = new LoginException(mensaje);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
    }

    @Test
    @DisplayName("Debería crear LoginException con mensaje y causa")
    void deberiaCrearLoginExceptionConMensajeYCausa() {
        // Arrange
        String mensaje = "Error de login";
        Throwable causa = new RuntimeException("Causa original");

        // Act
        LoginException exception = new LoginException(mensaje, causa);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debería crear CredencialesInvalidasException con mensaje")
    void deberiaCrearCredencialesInvalidasExceptionConMensaje() {
        // Arrange
        String mensaje = "Credenciales inválidas";

        // Act
        CredencialesInvalidasException exception = new CredencialesInvalidasException(mensaje);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("Debería crear CredencialesInvalidasException con mensaje y causa")
    void deberiaCrearCredencialesInvalidasExceptionConMensajeYCausa() {
        // Arrange
        String mensaje = "Credenciales inválidas";
        Throwable causa = new RuntimeException("Causa original");

        // Act
        CredencialesInvalidasException exception = new CredencialesInvalidasException(mensaje, causa);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debería crear CuentaBloqueadaException con mensaje")
    void deberiaCrearCuentaBloqueadaExceptionConMensaje() {
        // Arrange
        String mensaje = "Cuenta bloqueada";

        // Act
        CuentaBloqueadaException exception = new CuentaBloqueadaException(mensaje);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("Debería crear CuentaBloqueadaException con mensaje y causa")
    void deberiaCrearCuentaBloqueadaExceptionConMensajeYCausa() {
        // Arrange
        String mensaje = "Cuenta bloqueada";
        Throwable causa = new RuntimeException("Causa original");

        // Act
        CuentaBloqueadaException exception = new CuentaBloqueadaException(mensaje, causa);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debería lanzar LoginException en un flujo real")
    void deberiaLanzarLoginExceptionEnFlujoReal() {
        // Act & Assert
        LoginException exception = assertThrows(LoginException.class, () -> {
            throw new LoginException("Error simulado");
        });

        assertThat(exception.getMessage()).isEqualTo("Error simulado");
    }

    @Test
    @DisplayName("Debería lanzar CredencialesInvalidasException en un flujo real")
    void deberiaLanzarCredencialesInvalidasExceptionEnFlujoReal() {
        // Act & Assert
        CredencialesInvalidasException exception = assertThrows(CredencialesInvalidasException.class, () -> {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        });

        assertThat(exception.getMessage()).isEqualTo("Credenciales inválidas");
    }

    @Test
    @DisplayName("Debería lanzar CuentaBloqueadaException en un flujo real")
    void deberiaLanzarCuentaBloqueadaExceptionEnFlujoReal() {
        // Act & Assert
        CuentaBloqueadaException exception = assertThrows(CuentaBloqueadaException.class, () -> {
            throw new CuentaBloqueadaException("Cuenta bloqueada");
        });

        assertThat(exception.getMessage()).isEqualTo("Cuenta bloqueada");
    }
}
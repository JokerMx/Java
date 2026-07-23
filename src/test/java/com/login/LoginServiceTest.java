package com.login;

import com.login.exceptions.CredencialesInvalidasException;
import com.login.exceptions.CuentaBloqueadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas del Servicio de Login - Cobertura 100%")
class LoginServiceTest {

    private LoginService loginService;
    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASENA_VALIDA = "123456";
    private static final int INTENTOS_MAXIMOS = 3;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(USUARIO_VALIDO, CONTRASENA_VALIDA, INTENTOS_MAXIMOS);
    }

    @Nested
    @DisplayName("Pruebas de Login Exitoso")
    class LoginExitosoTest {

        @Test
        @DisplayName("Debería permitir acceso con credenciales correctas")
        void deberiaPermitirAccesoConCredencialesCorrectas() throws Exception {
            // Act
            LoginService.ResultadoLogin resultado = loginService.intentarLogin("admin", "123456");

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .matches(r -> r.isExitoso())
                    .matches(r -> !r.isBloqueado())
                    .matches(r -> r.getMensaje().contains("Acceso concedido"));

            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.isBloqueado()).isFalse();
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);
        }

        @Test
        @DisplayName("Debería resetear contador de intentos después de login exitoso")
        void deberiaResetearContadorDespuesDeLoginExitoso() throws Exception {
            // Arrange - Dos intentos fallidos (capturando excepciones)
            try {
                loginService.intentarLogin("user1", "pass1");
            } catch (CredencialesInvalidasException e) {
                // Esperado
            }
            try {
                loginService.intentarLogin("user2", "pass2");
            } catch (CredencialesInvalidasException e) {
                // Esperado
            }
            assertThat(loginService.getIntentos()).isEqualTo(2);

            // Act
            LoginService.ResultadoLogin resultado = loginService.intentarLogin("admin", "123456");

            // Assert
            assertThat(resultado.isExitoso()).isTrue();
            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Pruebas de Login Fallido")
    class LoginFallidoTest {

        @Test
        @DisplayName("Debería lanzar CredencialesInvalidasException con usuario incorrecto")
        void deberiaLanzarExcepcionConUsuarioIncorrecto() {
            // Act & Assert
            CredencialesInvalidasException exception = assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("usuario_incorrecto", "123456")
            );

            assertThat(exception.getMessage()).contains("incorrectos");
            assertThat(loginService.getIntentos()).isEqualTo(1);
            assertThat(loginService.getIntentosRestantes()).isEqualTo(2);
        }

        @Test
        @DisplayName("Debería lanzar CredencialesInvalidasException con contraseña incorrecta")
        void deberiaLanzarExcepcionConContrasenaIncorrecta() {
            // Act & Assert
            CredencialesInvalidasException exception = assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("admin", "contrasena_incorrecta")
            );

            assertThat(exception.getMessage()).contains("incorrectos");
            assertThat(loginService.getIntentos()).isEqualTo(1);
        }

        @ParameterizedTest
        @CsvSource({
                "admin, 12345",
                "admin, ",
                ", 123456"
        })
        @DisplayName("Debería lanzar excepción con diferentes combinaciones incorrectas")
        void deberiaLanzarExcepcionConCombinacionesIncorrectas(String usuario, String contrasena) {
            // Act & Assert
            assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin(usuario, contrasena)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas de Bloqueo")
    class BloqueoTest {

        @Test
        @DisplayName("Debería bloquear después de 3 intentos fallidos")
        void deberiaBloquearDespuesDeTresIntentosFallidos() {
            // Act & Assert
            for (int i = 0; i < 3; i++) {
                int intento = i;
                if (i < 2) {
                    CredencialesInvalidasException exception = assertThrows(
                            CredencialesInvalidasException.class,
                            () -> loginService.intentarLogin("user" + intento, "pass" + intento)
                    );
                    assertThat(exception.getMessage()).contains("incorrectos");
                    assertThat(loginService.getIntentos()).isEqualTo(i + 1);
                } else {
                    CuentaBloqueadaException exception = assertThrows(
                            CuentaBloqueadaException.class,
                            () -> loginService.intentarLogin("user" + intento, "pass" + intento)
                    );
                    assertThat(exception.getMessage()).contains("bloqueada");
                    assertThat(exception.getMessage()).contains("3 intentos");
                }
            }

            assertThat(loginService.isBloqueado()).isTrue();
            assertThat(loginService.getIntentos()).isEqualTo(3);
            assertThat(loginService.getIntentosRestantes()).isZero();
        }

        @Test
        @DisplayName("No debería permitir intentos después del bloqueo")
        void noDeberiaPermitirIntentosDespuesDeBloqueo() {
            // Arrange - Bloquear la cuenta
            for (int i = 0; i < 3; i++) {
                try {
                    loginService.intentarLogin("user" + i, "pass" + i);
                } catch (Exception e) {
                    // Ignorar
                }
            }
            assertThat(loginService.isBloqueado()).isTrue();

            // Act & Assert
            CuentaBloqueadaException exception = assertThrows(
                    CuentaBloqueadaException.class,
                    () -> loginService.intentarLogin("admin", "123456")
            );

            assertThat(exception.getMessage()).contains("bloqueada");
            assertThat(loginService.getIntentos()).isEqualTo(3);
            assertThat(loginService.getIntentosRestantes()).isZero();
        }
    }

    @Nested
    @DisplayName("Pruebas de Validación de Entrada")
    class ValidacionEntradaTest {

        @ParameterizedTest
        @NullSource
        @DisplayName("Debería lanzar excepción con usuario null")
        void deberiaLanzarExcepcionConUsuarioNull(String usuario) {
            // Act & Assert
            CredencialesInvalidasException exception = assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin(usuario, "123456")
            );

            assertThat(exception.getMessage()).contains("no pueden ser nulos");
            assertThat(loginService.getIntentos()).isZero();
        }

        @Test
        @DisplayName("Debería lanzar excepción con contraseña null")
        void deberiaLanzarExcepcionConContrasenaNull() {
            // Act & Assert
            CredencialesInvalidasException exception = assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("admin", null)
            );

            assertThat(exception.getMessage()).contains("no pueden ser nulos");
            assertThat(loginService.getIntentos()).isZero();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t", "\n"})
        @DisplayName("Debería lanzar excepción con credenciales vacías")
        void deberiaLanzarExcepcionConCredencialesVacias(String credencialVacia) {
            // Act & Assert
            assertThrows(
                    CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin(credencialVacia, "123456")
            );
            assertThat(loginService.getIntentos()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Pruebas de Estado")
    class EstadoTest {

        @Test
        @DisplayName("Debería mantener el estado correcto después de múltiples operaciones")
        void deberiaMantenerEstadoCorrecto() throws Exception {
            // Arrange - Estado inicial
            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.isBloqueado()).isFalse();
            assertThat(loginService.getIntentosMaximos()).isEqualTo(3);
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);

            // Act - Primer intento fallido
            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("user1", "pass1"));
            assertThat(loginService.getIntentos()).isEqualTo(1);
            assertThat(loginService.getIntentosRestantes()).isEqualTo(2);

            // Act - Segundo intento fallido
            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("user2", "pass2"));
            assertThat(loginService.getIntentos()).isEqualTo(2);
            assertThat(loginService.getIntentosRestantes()).isEqualTo(1);

            // Act - Login exitoso
            LoginService.ResultadoLogin resultado = loginService.intentarLogin("admin", "123456");

            // Assert
            assertThat(resultado.isExitoso()).isTrue();
            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.isBloqueado()).isFalse();
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);
        }

        @Test
        @DisplayName("Debería resetear correctamente el estado")
        void deberiaResetearCorrectamente() {
            // Arrange - Modificar estado
            for (int i = 0; i < 3; i++) {
                try {
                    loginService.intentarLogin("user" + i, "pass" + i);
                } catch (Exception e) {
                    // Ignorar
                }
            }

            assertThat(loginService.isBloqueado()).isTrue();
            assertThat(loginService.getIntentos()).isEqualTo(3);

            // Act
            loginService.reset();

            // Assert
            assertThat(loginService.isBloqueado()).isFalse();
            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Pruebas de Sensibilidad")
    class SensibilidadTest {

        @Test
        @DisplayName("Debería ser sensible a mayúsculas y minúsculas")
        void deberiaSerSensibleAMayusculas() throws Exception {
            // Act & Assert
            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("Admin", "123456"));

            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("admin", "1234567"));

            assertDoesNotThrow(() -> loginService.intentarLogin("admin", "123456"));
        }

        @Test
        @DisplayName("Debería considerar espacios en blanco")
        void deberiaConsiderarEspaciosEnBlanco() {
            // Act & Assert
            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin(" admin ", "123456"));
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Límite")
    class CasosLimiteTest {

        @Test
        @DisplayName("Debería manejar intentos exactos al límite")
        void deberiaManejarIntentosExactosAlLimite() {
            // Act & Assert
            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("user1", "pass1"));
            assertThat(loginService.getIntentos()).isEqualTo(1);

            assertThrows(CredencialesInvalidasException.class,
                    () -> loginService.intentarLogin("user2", "pass2"));
            assertThat(loginService.getIntentos()).isEqualTo(2);

            CuentaBloqueadaException exception = assertThrows(
                    CuentaBloqueadaException.class,
                    () -> loginService.intentarLogin("user3", "pass3")
            );

            assertThat(exception.getMessage()).contains("bloqueada");
            assertThat(loginService.isBloqueado()).isTrue();
            assertThat(loginService.getIntentos()).isEqualTo(3);
            assertThat(loginService.getIntentosRestantes()).isZero();
        }

        @Test
        @DisplayName("Debería manejar múltiples resets consecutivos")
        void deberiaManejarMultiplesResetsConsecutivos() {
            // Act
            loginService.reset();
            loginService.reset();
            loginService.reset();

            // Assert
            assertThat(loginService.isBloqueado()).isFalse();
            assertThat(loginService.getIntentos()).isZero();
            assertThat(loginService.getIntentosRestantes()).isEqualTo(3);
        }
    }
}
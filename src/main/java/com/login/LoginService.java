package com.login;

import com.login.exceptions.CredencialesInvalidasException;
import com.login.exceptions.CuentaBloqueadaException;

public class LoginService {
    private final String usuarioValido;
    private final String contrasenaValida;
    private final int intentosMaximos;
    private int intentos;
    private boolean bloqueado;

    public LoginService(String usuarioValido, String contrasenaValida, int intentosMaximos) {
        this.usuarioValido = usuarioValido;
        this.contrasenaValida = contrasenaValida;
        this.intentosMaximos = intentosMaximos;
        this.intentos = 0;
        this.bloqueado = false;
    }

    public ResultadoLogin intentarLogin(String usuario, String contrasena)
            throws CredencialesInvalidasException, CuentaBloqueadaException {

        if (usuario == null || contrasena == null) {
            throw new CredencialesInvalidasException("Usuario y contraseña no pueden ser nulos");
        }

        if (bloqueado) {
            throw new CuentaBloqueadaException("Cuenta bloqueada. Contacte al administrador.");
        }

        if (usuario.equals(usuarioValido) && contrasena.equals(contrasenaValida)) {
            intentos = 0;
            bloqueado = false;
            return new ResultadoLogin(true, "Acceso concedido. Bienvenido " + usuario, false);
        }

        intentos++;

        if (intentos >= intentosMaximos) {
            bloqueado = true;
            throw new CuentaBloqueadaException(
                    "Cuenta bloqueada. " + intentos + " intentos fallidos. Contacte al administrador."
            );
        }

        int intentosRestantes = intentosMaximos - intentos;
        throw new CredencialesInvalidasException(
                "Usuario o contraseña incorrectos. Intentos restantes: " + intentosRestantes
        );
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public int getIntentos() {
        return intentos;
    }

    public int getIntentosRestantes() {
        return intentosMaximos - intentos;
    }

    public int getIntentosMaximos() {
        return intentosMaximos;
    }

    public void reset() {
        this.intentos = 0;
        this.bloqueado = false;
    }

    public static class ResultadoLogin {
        private final boolean exitoso;
        private final String mensaje;
        private final boolean bloqueado;

        public ResultadoLogin(boolean exitoso, String mensaje, boolean bloqueado) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.bloqueado = bloqueado;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public boolean isBloqueado() {
            return bloqueado;
        }
    }
}
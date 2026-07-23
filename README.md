# Sistema de Login - Java

Sistema de autenticación de usuarios desarrollado en Java con interfaz gráfica Swing, que implementa un servicio de login con control de intentos y bloqueo de cuenta.

## Características

- Validación de credenciales con mensajes personalizados
- Control de intentos fallidos (3 intentos máximos)
- Bloqueo de cuenta al exceder intentos permitidos
- Interfaz gráfica con `JOptionPane`
- Arquitectura en capas (presentación, servicio, excepciones)
- Cobertura de pruebas con JaCoCo

## Tecnologías

- Java 21
- Maven
- JUnit 5.10.2
- Mockito 5.14.2
- AssertJ 3.27.3
- JaCoCo 0.8.12

## Estructura del Proyecto

```
src/
├── main/java/com/login/
│   ├── LoginUsuario.java           # Clase principal con interfaz gráfica
│   ├── LoginService.java           # Lógica de autenticación
│   └── exceptions/
│       ├── LoginException.java     # Excepción base
│       ├── CredencialesInvalidasException.java
│       └── CuentaBloqueadaException.java
└── test/java/com/login/
    ├── LoginUsuarioTest.java
    ├── LoginUsuarioParameterizedTest.java
    ├── LoginServiceTest.java
    └── exceptions/
        └── LoginExceptionTest.java
```

## Credenciales de Acceso

| Campo  | Valor   |
|--------|---------|
| Usuario| admin   |
| Clave  | 123456  |

## Construcción

```bash
mvn clean compile
```

## Pruebas

```bash
mvn test
```

## Reporte de Cobertura

```bash
mvn clean test jacoco:report
```

El reporte se genera en `target/site/jacoco/index.html`.

### Resultados de Cobertura - JaCoCo

| Métrica | Total | Cobertos | Faltantes | Cobertura |
|---------|-------|----------|-----------|-----------|
| Instructions | 311 | 293 | 18 | 94% |
| Branches | 20 | 19 | 1 | 95% |
| Lines | 91 | 86 | 5 | 95% |
| Complexity | 34 | 30 | 4 | 88% |
| Methods | 24 | 21 | 3 | 88% |
| Classes | 7 | 7 | 0 | 100% |

### Cobertura por Clase

| Clase | Instructions | Branches | Lines | Complexity | Methods |
|-------|-------------|----------|-------|------------|---------|
| LoginService | 100% | 100% | 100% | 100% | 100% |
| LoginService.ResultadoLogin | 100% | n/a | 100% | 100% | 100% |
| LoginException | 100% | n/a | 100% | 100% | 100% |
| CuentaBloqueadaException | 100% | n/a | 100% | 100% | 100% |
| CredencialesInvalidasException | 100% | n/a | 100% | 100% | 100% |
| LoginUsuario | 96% | 88% | 95% | 75% | 75% |
| LoginUsuario.JOptionPaneWrapper | 20% | 0% | 25% | 33% | 33% |

## Ejecución

```bash
mvn exec:java -Dexec.mainClass=com.login.LoginUsuario
```

O compilar y ejecutar el JAR:

```bash
mvn package
java -jar target/login-sistema-1.0-SNAPSHOT.jar
```

## Arquitectura

```
LoginUsuario (GUI)
    │
    ▼
LoginService (Lógica de negocio)
    │
    ▼
Excepciones personalizadas
```

### Clases Principales

- **LoginUsuario**: Punto de entrada, maneja la interfaz gráfica y coordina el flujo de login
- **LoginService**: Contiene la lógica de validación, control de intentos y bloqueo
- **JOptionPaneWrapper**: Abstracción de la interfaz gráfica para facilitar pruebas con mocks
- **ResultadoLogin**: Clase interna que encapsula el resultado de un intento de login

### Jerarquía de Excepciones

```
LoginException (base)
├── CredencialesInvalidasException
└── CuentaBloqueadaException
```

## Licencia

MIT

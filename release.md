# Release - Sistema de Login v1.0.0

**Fecha:** 2026-07-23  
**Versión:** 1.0.0  
**Artefacto Maven:** login-sistema-1.0-SNAPSHOT  
**Java:** 21

## Descripción

Primera versión estable del sistema de autenticación de usuarios con interfaz gráfica Swing. Incluye validación de credenciales, control de intentos fallidos y bloqueo automático de cuenta.

## Cambios en esta versión

### Features
- Validación de credenciales con mensajes personalizados
- Control de intentos fallidos (3 intentos máximos)
- Bloqueo automático de cuenta al exceder el límite de intentos
- Interfaz gráfica basada en `JOptionPane`
- Servicio de login desacoplado de la interfaz gráfica
- Jerarquía de excepciones personalizadas

### Mejoras
- Cobertura de pruebas del 94% en instrucciones y 95% en ramas
- Arquitectura en capas (presentación, servicio, excepciones)
- Abstracción de interfaz gráfica mediante `JOptionPaneWrapper` para facilitar pruebas con mocks
- Pruebas unitarias con JUnit 5, Mockito y AssertJ

### Correcciones
- No aplica (primera versión)

## Requisitos previos

| Requisito | Versión |
|-----------|---------|
| Java      | 21      |
| Maven     | 3.8+    |

## Instalación

### Compilación

```bash
mvn clean compile
```

### Pruebas

```bash
mvn test
```

### Generar reporte de cobertura

```bash
mvn clean test jacoco:report
```

El reporte se genera en `target/site/jacoco/index.html`.

### Empaquetar

```bash
mvn package
```

## Ejecución

### Por Maven

```bash
mvn exec:java -Dexec.mainClass=com.login.LoginUsuario
```

### Por JAR

```bash
java -jar target/login-sistema-1.0-SNAPSHOT.jar
```

## Credenciales de acceso

| Campo | Valor  |
|-------|--------|
| Usuario | admin |
| Clave | 123456 |

## Configuración

Las credenciales y el máximo de intentos pueden modificarse en `LoginUsuario.java` o instanciando `LoginService` con parámetros personalizados:

```java
LoginService loginService = new LoginService("usuario", "clave", 3);
```

## Próximamente

- Persistencia de usuarios
- Registro de intentos y bloqueos
- Internacionalización (i18n)
- Logging estructurado

## Notas

- La cuenta se bloquea después de 3 intentos fallidos.
- El usuario puede cancelar el flujo de login en cualquier momento.
- El estado del servicio puede resetearse llamando a `loginService.reset()`.

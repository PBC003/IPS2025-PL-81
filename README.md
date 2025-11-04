# IPS2025 · Gestión de Club Deportivo (PL81)

Aplicación de escritorio para la **gestión integral de clubes deportivos**: incidencias, cuotas/recibos, reservas e intercambio con la federación. El proyecto se desarrolla con **Java (JDK 21)**, arquitectura **MVC con Swing**, **JDBC** y base de datos **SQLite** (entorno local). Sigue un proceso **incremental** con prácticas Scrum.

> Inspirado en la estructura de proyecto Maven y buenas prácticas de pruebas/automatización del proyecto plantilla “[samples-test-dev](https://github.com/javiertuya/samples-test-dev)”.

---

## Tabla de contenidos

- [IPS2025 · Gestión de Club Deportivo (PL81)](#ips2025--gestión-de-club-deportivo-pl81)
  - [Tabla de contenidos](#tabla-de-contenidos)
  - [Resumen del sistema](#resumen-del-sistema)
  - [Características principales](#características-principales)
  - [Stack y requisitos](#stack-y-requisitos)
  - [Arquitectura](#arquitectura)
  - [Estructura del proyecto](#estructura-del-proyecto)
  - [Configuración y ejecución](#configuración-y-ejecución)
    - [1) Variables de entorno / propiedades](#1-variables-de-entorno--propiedades)
    - [2) Compilar y ejecutar](#2-compilar-y-ejecutar)
  - [Base de datos](#base-de-datos)
  - [Pruebas y calidad](#pruebas-y-calidad)
  - [Flujo de trabajo (Git)](#flujo-de-trabajo-git)
  - [Roadmap (alto nivel)](#roadmap-alto-nivel)
  - [Roles](#roles)

---

## Resumen del sistema

El sistema busca **optimizar la administración del club**, reduciendo errores manuales y centralizando la información. Se prioriza la **trazabilidad**, la modularidad y el cumplimiento de **RGPD**.

---

## Características principales

- **Gestión de incidencias**: alta / seguimiento / resolución de incidencias reportadas por socios.
- **Gestión económica**: generación de **recibos mensuales**, agrupación en **lotes** y **exportación** en formato bancario.
- **Reservas y competiciones**: control de reservas e **intercambio con federación** (actor externo).
- **Escritorio (Swing)** con arquitectura **MVC** y capas de datos/negocio/presentación claramente separadas.
- **Maven** para compilación, dependencias, javadoc y reports.

---

## Stack y requisitos

- **Lenguaje**: Java **21** (JDK 21)
- **IDE**: Eclipse / IntelliJ (compatible con Maven)
- **Build**: Apache **Maven 3.9+**
- **UI**: Swing
- **Datos**: **SQLite** en local (JDBC)
- **SO**: Windows / Linux / macOS


---

## Arquitectura

Arquitectura en **tres capas**:

1. **Presentación (Swing)**: ventanas, controladores y listeners.
2. **Negocio**: servicios y reglas para incidencias, cuotas/lotes, reservas.
3. **Datos (JDBC)**: DAOs y mapeos a entidades.

---

## Estructura del proyecto

Estructura estándar Maven:

```
/src
  /main
    /java          # código de aplicación (MVC, DAOs, servicios, modelos)
    /resources     # SQL de inicialización, configuración
  /test
    /java          # pruebas unitarias
target/            # binarios, reports y sitio generado por Maven
pom.xml
```

---

## Configuración y ejecución

### 1) Variables de entorno / propiedades

Crea `src/main/resources/application.properties` con tus credenciales:

```properties
db.url=jdbc:sqlite://localhost:3306/club
db.driver=org.sqlite.jdbc.Driver
```

### 2) Compilar y ejecutar

```bash
# Compilar todo (incluye javadoc y tests)
mvn install

# Ejecutar sólo tests
mvn test

# Generar artefactos sin tests (por ejemplo, para probar rápido la app)
mvn install -DskipTests=true
```

> En Eclipse: **Maven → Update Project** y **Run As → Maven install**. Verifica que el proyecto usa **JDK** y no un **JRE**.

---

## Base de datos

- **Inicialización**: incluye scripts SQL de esquema/datos en `src/main/resources/sql/` para crear tablas.
- **Acceso**: **JDBC** directamente desde DAOs y utilidades comunes.

---

## Pruebas y calidad

- **JUnit** para pruebas unitarias.
- **Informes** generados en `target/` (Surefire, Jacoco, javadoc).

Comandos útiles:

```bash
# Todas las pruebas y verificación (reports)
mvn verify

# Solo javadoc del proyecto (publicable como site)
mvn javadoc:javadoc
```

---

## Flujo de trabajo (Git)

- Ramas por funcionalidad y **Pull Request** hacia `main`.
- Commits pequeños y descriptivos.

---

## Roadmap (alto nivel)

1. **Sprint 1**: Gestion de Incidencias y Generacion de Recibos
2. **Sprint 2**:
3. **Sprint 3**:

---

## Roles

- **Product Owner**: prioriza backlog y asegura el valor de negocio.
- **Scrum Master**: facilita el proceso y elimina impedimentos.
- **Equipo de desarrollo**: diseño, codificación, pruebas y documentación.
- **Directivo/Administrador (usuario)**: gestión económica, lotes y revisión de incidencias.
- **Socio (usuario)**: registro y seguimiento de incidencias; reservas.
- **Federación (actor externo)**: recibe exportaciones (sin acceso directo).

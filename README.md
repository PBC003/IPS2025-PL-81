# IPS2025 · Gestión de Club Deportivo (PL81)

Aplicación de escritorio (Java Swing) para la **gestión integral de un club**: altas de incidencias, seguimiento y cierre; gestión económica con **recibos mensuales**, creación de **lotes de cobro** y **exportación** a fichero; **sistema inteligente de reservas de instalaciones con previsión meteorológica** y **gestión de asambleas** del club. El proyecto sigue una arquitectura en **capas (Modelo · DAO · Servicio · Controlador · UI)** y se apoya en **SQLite** (embebida) para persistencia.

> Proyecto académico de IPS 2025 · PL81. Estructura basada en Maven y buenas prácticas (tests, cobertura, javadoc).

---

## Estado actual (06·nov·2025)

- **BD SQLite** autogenerada a partir de scripts (`schema.sql` + `data.sql`).
  - Tablas principales: `Users`, `Incident_type`, `Location`, `Incident`, `Receipt`, `Receipt_batch`.
  - Tablas adicionales para **reservas** y **asambleas** (ver `sql/schema.sql`).
  - Reglas importantes:
    - `Users.iban` **UNIQUE**; `Users.email` **UNIQUE**.
    - `Receipt`: **un recibo por usuario y mes** (`UNIQUE(user_id, charge_month)`).
    - Estados:
      - `Incident.status ∈ {OPEN, ASSIGNED, WAITING_REPLY, CLOSED}`.
      - `Receipt.status ∈ {GENERATED, PAID, CANCELED, REISSUED}`.
      - `Receipt_batch.status ∈ {GENERATED, EXPORTED, CANCELED}`.
    - `Location` distingue entre **instalaciones interiores y exteriores** para aplicar reglas meteorológicas.
    - Reglas de negocio de **reservas**: sin solapes por instalación/franja y cancelación/bloqueo según umbrales de temperatura y precipitaciones.
- **Capa de datos (DAO)** y **servicios** implementados para **Usuarios, Incidencias, Recibos, Lotes, Reservas y Asambleas**.
- **UI Swing** funcional:
  - **Login** → selección de usuario → **Menú principal**.
  - Ventana **Incidencias** (listado, alta, estados, filtro por tipo/localización).
  - Ventana **Recibos** (creación individual por usuario/mes/concepto).
  - Ventana **Lotes de recibos**:
    - Listado de recibos **sin lote** y asignación a un lote.
    - **Exportación** a fichero (CSV simple configurable por servicio).
  - Ventana **Reservas de instalaciones**:
    - Selección de instalación, fecha y franja horaria mediante **calendario gráfico**.
    - Elección de duración (60/120 minutos) y visualización de disponibilidad.
    - Indicadores visuales de **previsión meteorológica** y bloqueo de reservas en caso de malas condiciones en instalaciones exteriores.
  - Ventana **Asambleas**:
    - Creación, edición y detalle de asambleas.
    - Gestión de **puntos del orden del día** y **acta** asociada.
- **Servicios auxiliares**:
  - `ReceiptExportService`: genera archivo de exportación con sumatorio e integración básica.
  - Servicio de **previsión meteorológica** encapsulado (consulta la previsión diaria de temperatura y precipitaciones para la localización del club).
  - `Database`/`DbUtil`: inicialización **idempotente** de BD desde `application.properties`.
- **Testing**:
  - Conjunto de **tests unitarios y de integración** para servicios de incidencias, recibos, lotes, reservas y asambleas.
  - Integración con **JaCoCo** para medir cobertura y apoyo a CI (workflow Maven).


---

## Stack y requisitos

- **JDK**: 8+ (pom configura `maven.compiler.source/target` a 1.8).
- **Maven**: 3.8+
- **SQLite JDBC**: `org.xerial:sqlite-jdbc:3.50.3.0`
- **Logging**: SLF4J + log4j (puente `slf4j-reload4j`).
- **UI**: Swing + MigLayout
- **Testing**: JUnit 4 + JUnitParams; cobertura con **JaCoCo**.

---

## Estructura del proyecto

```
src/
  main/
    java/ips/
      club/
        app/ClubApp.java                 # Punto de entrada
        controller/                      # Controladores de UI
        dao/                             # Acceso a datos (JDBC)
        dto/                             # DTOs para la UI
        model/                           # Entidades y enums
        service/                         # Reglas de negocio
        ui/                              # Ventanas Swing
      util/                              # Database, DbUtil, excepciones
    resources/
      application.properties             # driver/url SQLite
      sql/
        schema.sql                       # DDL completo
        data.sql                         # Datos de ejemplo
test/
  java/ips/...                           # Tests JUnit
exports/
  batch/
uploads/
  assemblies/
pom.xml
```

---

## Configuración

Crea/ajusta `src/main/resources/application.properties`:

```properties
datasource.driver=org.sqlite.JDBC
datasource.url=jdbc:sqlite:ClubDB.db
```

- La BD se crea en el **primer arranque** (si no existe) ejecutando `sql/schema.sql` y se precarga con `sql/data.sql`.
- Para reinicializar manualmente, borra `ClubDB.db` y vuelve a ejecutar la app.

---

## Cómo compilar y ejecutar

```bash
# 1) Compilar + tests + cobertura
mvn -q clean test

# 2) Generar JAR y javadoc de pruebas
mvn -q install

# 3) Ejecutar desde IDE: Main = ips.club.app.ClubApp
java -cp target/IPS2025-PL-81-*.jar:target/dependency/* ips.club.app.ClubApp
```

En el repositorio hay configurado un **workflow de CI** que ejecuta los tests Maven en cada push/PR sobre la rama principal.

---

## Funcionalidad actual (detallada)

### Incidencias

- Alta de incidencia asociada a **usuario** y **tipo** (`Incident_type`).
- Estados soportados: `OPEN`, `ASSIGNED`, `WAITING_REPLY`, `CLOSED`.
- **Localización** opcional (`Location`) visible cuando el tipo es *Instalaciones*.
- Listado con formateo y mapeo de códigos → nombres, sin duplicar la lógica en el modelo.

### Recibos

- Creación de recibos **individuales** por usuario:
  - `amount_cents`, `issue_date`, `value_date`, `charge_month` (formato `YYYYMM`), `concept`.
  - Validación por servicio y restricción de **un recibo/mes/usuario**.
- Estados: `GENERATED`, `PAID`, `CANCELED`, `REISSUED`.
- Gestión específica de recibos **reliquidados**:
  - No se añaden automáticamente a nuevos lotes.
  - Pueden seleccionarse manualmente si están marcados como *reissued*.

### Lotes de recibos

- **Crear lote** con `charge_month`, `bank_entity`, `file_name`.
- Agregar recibos **sin lote** (incluyendo los reliquidados) a un lote determinado mediante selección manual.
- **Exportación** a fichero (CSV simple) y actualización de:
  - `Receipt_batch.total_amount`
  - `Receipt_batch.receipts_cnt`
  - Cambio de `status` a `EXPORTED`.
- Política actual: el nombre de fichero se **introduce tal cual** (sin forzar extensión).

### Reservas de instalaciones

- Gestión de **reservas de instalaciones** del club sobre las localizaciones definidas en `Location`.
- Selección de:
  - Usuario, instalación, fecha y franja horaria mediante **calendario**.
  - Duración de la reserva (60 o 120 minutos).
- Reglas de negocio:
  - No se permiten reservas solapadas para la misma instalación.
  - Diferenciación entre **instalaciones interiores y exteriores** mediante un indicador en `Location`.
  - Para instalaciones **exteriores**:
    - Se consulta la **previsión meteorológica diaria** (temperatura y precipitaciones).
    - Se bloquean o cancelan reservas cuando se superan ciertos umbrales de temperatura/lluvia.
- La UI muestra la previsión para el día seleccionado, facilitando la demostración y explicación del comportamiento del sistema inteligente.

### Asambleas del club

- **Creación de asambleas** con:
  - Título, fecha programada, estado, descripción.
  - Definición de los **puntos del orden del día** desde el propio formulario de creación/edición.
- **Detalle de asamblea**:
  - Visualización de todos los datos básicos y de los puntos del orden del día asociados.
  - Campo de **Acta** (documento subido) y botón **“Abrir acta”**:
    - El botón solo aparece cuando hay un acta asociada.
- Permite mantener el histórico de asambleas con sus acuerdos y documentación vinculada.

---

## Decisiones de diseño

- **Capas claras**: los controladores son delgados; la lógica reside en **servicios**.
- **Validación**: reglas de negocio en servicios antes de delegar en DAO.
- **SQLite** para desarrollo/docencia: cero dependencias externas; scripts versionados.
- **Enums** para estados (`IncidentStatus`, `ReceiptStatus`, `ReceiptBatchStatus`).
- **UI** desacoplada: ventanas (`ui/*`) no acceden directamente a JDBC.

---

## Roadmap próximo (shortlist)

- [ ] Implementación del sistema de competiciones para automatizar el registro y envío a la federación
- [ ] Hacer la plataforma auditable y que cumpla con el RGPD
- [ ] Como directivo quiero poder consultar los datos económicos del club


---

## Contribuciones (workflow básico)

1. Crea rama a partir de `main` (IPS-xxxxx).
2. Commits pequeños y con mensaje claro.
3. **PR** con descripción.
4. *Squash & merge* tras revisión.

---

## Créditos

- Estructura inspirada en [samples-test-dev](https://github.com/javiertuya/samples-test-dev)”.

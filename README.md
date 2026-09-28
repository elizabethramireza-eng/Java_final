# 🚆 TecnoMovil Data — Motor Analítico Funcional de Transporte Urbano

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=java)
![Paradigm](https://img.shields.io/badge/Paradigma-Programación%20Funcional-blue?style=flat-square)
![Institution](https://img.shields.io/badge/Institución-IU%20Digital%20de%20Antioquia-green?style=flat-square)

Sistemas de procesamiento analítico y de concurrencia desarrollado en **Java 17+** para la ciudad de TecnoValle. El proyecto transforma la arquitectura legacy imperativa en un motor basado en **Funciones Puras**, **Inmutabilidad** y **Streams API**, garantizando la evaluación libre de efectos secundarios y apta para ejecuciones de alto rendimiento en entornos multicore.

---

## 📋 Tabla de Contenidos
- [Contexto del Problema](#-contexto-del-problema)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Análisis de Funcionalidades](#-análisis-de-funcionalidades)
- [Fundamentos de Programación Funcional](#-fundamentos-de-programación-funcional)
- [Integrantes del Equipo](#-integrantes-del-equipo)

---

## 🌆 Contexto del Problema

El sistema de transporte masivo de TecnoValle gestiona diariamente la movilidad de más de 450.000 usuarios. La solución legacy generaba cuellos de botella severos, bloqueos por condiciones de carrera (race conditions) y retrasos de hasta 2 horas en el cálculo del cierre operativo.

**TecnoMovil Data** resuelve esto procesando transmisiones de eventos heterogéneos (`entrada` / `salida`) mediante colecciones inmutables y agregaciones declarativas en paralelo.

---

## 📁 Estructura del Proyecto

```text
Java_final/
├── src/
│   ├── model/
│   │   └── RegistroTransito.java       # Record inmutable de datos
│   ├── services/
│   │   └── ProcesadorFuncional.java    # Motor de cálculo con Streams y Lambdas
│   └── Main.java                       # Punto de entrada y simulación
├── README.md                           # Documentación del proyecto
└── EA2_Informe_TecnoMovilData.pdf      # Documento técnico en formato APA 7
```

---

## 🛠️ Requisitos Previos

* **Java Development Kit (JDK):** Versión 17 o superior.
* **IDE Recomendado:** Visual Studio Code, IntelliJ IDEA o Eclipse.
* **Consola / Terminal:** Bash, Zsh o PowerShell.

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/elizabethramireza-eng/Java_final
cd JAVA_FINAL
```

### 2. Compilar el proyecto
Desde la raíz del proyecto (o la carpeta `src`), compila todos los archivos `.java`:

```bash
javac -d bin src/model/*.java src/services/*.java src/Main.java
```

### 3. Ejecutar la aplicación
```bash
java -cp bin Main
```

---

## 📊 Análisis de Funcionalidades

El servicio `ProcesadorFuncional` resuelve las 6 tareas requeridas por la alcaldía:

| Tarea | Operación Analítica | Método / Técnica Utilizada |
| :--- | :--- | :--- |
| **a** | Afluencia por Estación | `.filter()` de entradas + `.collect(Collectors.groupingBy(..., counting()))` |
| **b** | Horas Pico | Agrupamiento temporal agrupado por la hora del día (`timestamp.getHour()`) |
| **c** | Rutas Más Utilizadas | Agregación por ruta y ordenamiento descendente con `LinkedHashMap` |
| **d** | Patrones de Viaje | Secuenciación cronológica de estaciones transitadas por usuario (`idUsuario`) |
| **e** | Tiempo Promedio de Traslado | Cálculo de `Duration.between()` entre eventos consecutivos mediante `flatMapToDouble` |
| **f** | Detección de Sobrecarga | Clasificación condicional (`CRÍTICA` / `OK`) según umbral configurable de usuarios |

---

## 💡 Fundamentos de Programación Funcional

1. **Inmutabilidad Estricta (`record`):** Los registros de datos no expónen métodos modificadores (*setters*). Ninguna operación altera la fuente de datos original.
2. **Funciones Puras:** Las funciones analíticas devuelven resultados deterministas basados únicamente en los parámetros de entrada, sin variables globales ni efectos secundarios.
3. **Procesamiento Perezoso (Lazy Evaluation):** Los pipelines de `java.util.stream.Stream` no ejecutan las transformaciones hasta que se invoca una operación terminal (`.collect()`, `.reduce()`).
4. **Paralelización Transparente:** La arquitectura permite sustituir `.stream()` por `.parallelStream()` para procesar millones de registros distribuidos en múltiples hilos de CPU sin riesgos de sincronización.

---

# Compilador Cmini (TP1 & TP2) - Técnicas de Compilación

Este proyecto implementa un compilador simplificado para un subconjunto de C, denominado **Cmini**, desarrollado en **Java + ANTLR4** como parte de la materia *Técnicas de Compilación*.

## 📌 TP1 - Gramática y Árbol Sintáctico
En el **TP1** se definió la gramática en ANTLR y se implementó un programa que:
- Reconoce funciones, sentencias de control y expresiones.
- Genera e imprime el **árbol sintáctico** en consola.

**Archivos principales:**
- `src/main/antlr4/tc/grammar/Cmini.g4` → gramática ANTLR.
- `src/main/java/tc/App.java` → ejecuta el parser.
- `src/main/java/tc/errors/BaseErrorListener.java` → captura errores sintácticos.

## 📌 TP2 - Tabla de Símbolos y Análisis Semántico
En el **TP2** se extiende el compilador para:
1. Construir una **Tabla de Símbolos** con contextos.
2. Detectar **errores semánticos**:
   - Doble declaración del mismo identificador.
   - Uso de identificador no declarado.
   - Uso de identificador sin inicializar.
   - Identificador declarado pero no usado.
   - Tipos de datos incompatibles.
3. Generar reportes de errores:
   - `reports/syntax.txt` → errores léxicos/sintácticos.
   - `reports/semantic.txt` → errores semánticos.
   - `reports/symbols.txt` → tabla de símbolos completa.

**Clases principales:**
- `tc.symbols` → `TablaDeSimbolos`, `Contexto`, `ID`, `Variable`, `Tipo`.
- `tc.semantics.MiListener` → análisis semántico.
- `tc.semantics.ErrorReporter` → reporte de errores.
- `tc.errors.BaseErrorListener` → errores sintácticos.

---

## ▶️ Ejecución y pruebas

### 1. Compilar y generar fuentes ANTLR
```bash
mvn clean generate-sources compile
```

### 2. Ejecutar el compilador con un archivo de prueba
```bash
mvn exec:java -Dexec.args="samples/ok_semantic.c"
```

- En consola: se imprime el **árbol sintáctico**.
- En `reports/`: se generan los reportes.

### 3. Archivos de prueba
- `samples/ok_semantic.c` → código válido, sin errores.
- `samples/bad_semantic.c` → errores semánticos (doble declaración, no declarado, etc.).
- `samples/bad_syntax.c` → errores de parsing (falta de `;`, paréntesis, etc.).

### 4. Resultados esperados
- `syntax.txt` → errores de sintaxis (vacío si no hay).
- `semantic.txt` → lista de errores semánticos detectados.
- `symbols.txt` → historial de contextos y variables (tipo, valor, usada/no usada, inicializada/no inicializada).

---

## 📂 Ejemplo de uso

```bash
mvn exec:java -Dexec.args="samples/bad_semantic.c"
```

Salida esperada en `reports/semantic.txt`:
```
L3:9 - Uso de un identificador sin inicializar: 'r'
L6:9 - Doble declaración del mismo identificador: 'a'
L7:5 - Uso de un identificador no declarado: 'b'
L9:9 - Tipos de datos incompatibles en asignación a 'k': INT = FLOAT
L11:13 - Uso de un identificador sin inicializar: 'm'
```

---

## 📘 Informes de Defensa
- **TP1:** `TP1_Defensa_Cmini.pdf`
- **TP2:** `TP2_Defensa_Cmini.pdf`

Incluyen teoría, explicación de clases y ejemplos de ejecución.

---

## 👨‍💻 Autor
- Proyecto realizado por **Agustín López** como parte de la materia *Técnicas de Compilación*.

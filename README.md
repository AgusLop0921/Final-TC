# Proyecto TC-COMPILER

Este proyecto implementa un compilador sencillo en Java con ANTLR4, siguiendo las etapas clásicas: análisis léxico, sintáctico, semántico y generación de código intermedio.  
Incluye visualización de árboles en formato DOT/PNG y reportes de errores/advertencias.

---

## 📂 Estructura del proyecto

```
TC-COMPILER
├── reports/         # Salidas de AST en DOT/PNG
├── samples/         # Archivos de prueba en C
├── src/main/java/tc
│   ├── grammar/     # Gramática ANTLR (.g4) + clases generadas
│   ├── errors/      # Manejo de errores
│   ├── intermediate/# Código intermedio (TAC)
│   ├── semantics/   # Chequeos semánticos
│   ├── symbols/     # Tabla de símbolos
│   ├── utils/       # Utilidades (reportes, etc.)
│   └── App.java     # Entrada principal del compilador
```

---

## 🛠️ Herramientas utilizadas

- **ANTLR4**: generación automática de Lexer, Parser, Listener y Visitor.  
- **Maven**: gestión de dependencias y compilación.  
- **Graphviz (dot)**: generación de imágenes PNG a partir de archivos DOT.  
- **Visual Studio Code** como IDE.  
- **Prettier Java Plugin**: extensión para formatear código Java.  

---

## ▶️ Instructivo paso a paso

### 1. Instalación
Asegurarse de tener instalado:
- Java JDK 17+  
- Maven  
- ANTLR4  
- Graphviz (`dot`)  
- Prettier Java Plugin en VS Code  

### 2. Generar clases de ANTLR4
Compilar la gramática y generar automáticamente las clases del parser y lexer:
```bash
mvn clean compile
```

### 3. Ejecutar un archivo de prueba
Ejecutar el compilador con un archivo `.c` de entrada:
```bash
mvn exec:java -Dexec.args="samples/final-profe/ejemplo_correcto.c"
```

### 4. Generar PNG del AST
Una vez generado el archivo DOT, convertirlo a PNG con Graphviz:
```bash
dot -Tpng reports/ok_semantic_ast.dot -o reports/ok_semantic_ast.png
```

### 5. Revisar resultados
- Archivos DOT/PNG → `reports/`  
- Logs intermedios → raíz del proyecto  

---

## ✅ Ejemplo de entrada SIN errores

### Código de prueba
```c
// Variables globales
int contadorGlobal;
double valorPi;
char inicial;
bool activo;

// Función simple que retorna valor
int sumar(int a, int b) {
    int resultado;
    resultado = a + b;
    contadorGlobal = contadorGlobal + 1;
    return resultado;
}

// Función main
int main() {
    int estado = sumar(3,2);
    int numeros[3];
    
    // Inicializar variables globales
    contadorGlobal = numeros;
    valorPi = 3.14;
    inicial = 'M';
    
    return estado;
}
```

### Salida esperada
- ✔️ Análisis léxico y sintáctico completados sin errores.  
- ✔️ AST generado correctamente.  
- ✔️ Análisis semántico: todas las variables declaradas, sin errores.  

📸 Ejemplo de salida (imagen sin errores):  
*(ver `reports/ok_semantic_ast.png`)*  

---

## ❌ Ejemplo de entrada CON errores

### Código de prueba
```c
// Variables globales
int variableGlobal;
int variableGlobal; // ERROR: Variable duplicada en el mismo ámbito
double valorGlobal;
char caracterGlobal;

// Función con múltiples errores
int miFuncion(int parametro1, double parametro2) {
    int variableLocal;
    int variableLocal; // ERROR: Variable duplicada en función
    
    // Variables nunca utilizadas (WARNING)
    int variableNoUsada1;
    string variableNoUsada2;
    double variableNoUsada3;
    
    parametro1 = 100; // OK
    variableLocal = parametro1 + 5; // OK
    
    // ERROR: Asignación a variable no declarada
    variableFantasma = 42;
    
    // ERROR: Asignación a una función
    miFuncion = 10;
    
    // OK: usar variable global
    valorGlobal = 3.14;
    
    return variableLocal;
}

void funcionVoid() {
    int x;
    int y;
    int z; // WARNING: declarada pero no usada
    
    x = 10; 
    y = x + 5; 
    
    // ERROR: variable no declarada
    w = x + y;
}

// Función main
int main() {
    int resultado;
    int valor;
    
    resultado = miFuncion(5, 3.14);
    valor = resultado + 10;
    
    // ERROR: variable no declarada
    variableFinal = valor;
    
    // OK: usar variable global
    variableGlobal = valor;
    
    return resultado;
}
```

### Salida esperada
- ✔️ Análisis léxico y sintáctico correctos.  
- ❌ Errores semánticos detectados:  
  - Duplicación de `variableGlobal`.  
  - Duplicación de `variableLocal`.  
  - Uso de variable no declarada `variableFantasma`.  
  - Asignación a función `miFuncion`.  
  - Uso de variable no declarada `w`.  
  - Uso de variable no declarada `variableFinal`.  
- ⚠️ Advertencias: variables declaradas pero no utilizadas (`variableNoUsada1`, `variableNoUsada2`, `variableNoUsada3`, `z`).  

📸 Ejemplo de salida (imagen con errores):  
*(ver `reports/ejemplo_multiples_errores_ast.png`)*  

---

## 📌 Conclusión

El proyecto permitió aplicar de forma práctica los conceptos de la materia: desde gramática y ANTLR hasta análisis semántico y generación de código intermedio.

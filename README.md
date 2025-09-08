# Cmini Compiler

Proyecto académico de **Técnicas de Compilación**.\
Implementación progresiva de un compilador para un subconjunto reducido
del lenguaje C (**Cmini**), utilizando **ANTLR4** y **Java (Maven)**.

## 📌 Requerimientos

-   **Java 17** o superior\
-   **Apache Maven 3.9+**\
-   **VS Code** con extensiones recomendadas:
    -   *Extension Pack for Java*
    -   *ANTLR4 grammar syntax support*

## ⚙️ Configuración inicial

Clonar el repositorio y compilar:

``` bash
git clone https://github.com/<tu-usuario>/tc-compiler.git
cd tc-compiler

mvn clean generate-sources compile
```

## ▶️ Ejecución

Ejemplo con archivo de prueba incluido:

``` bash
mvn exec:java -Dexec.args="samples/ok_hello.c"
```

Salida esperada (árbol sintáctico simplificado):

    (program (functionDecl int main ( ) (block { (statement return 0 ;) })))

## 📂 Estructura del proyecto

    tc-compiler/
    ├─ pom.xml                # Configuración Maven (Java + ANTLR4)
    ├─ README.md              # Este archivo
    ├─ samples/               # Archivos de prueba .c
    │   └─ ok_hello.c
    ├─ src/
    │   ├─ main/
    │   │   ├─ java/tc/App.java
    │   │   └─ antlr4/tc/grammar/Cmini.g4
    │   └─ test/             
    └─ target/                # Archivos generados por Maven

------------------------------------------------------------------------

✍️ Autor: *Sergio Agustin Lopez*

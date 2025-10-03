// ==== Archivo de prueba con errores y warnings ====
// Mezcla de errores semánticos, sintácticos y warnings para tu compilador

// --- Globales (duplicado) ---
int g;
int g;               // ERROR semántico: variable global duplicada
double dGlobal;
char chGlobal;
bool flagGlobal;

// --- Prototipo simple correcto ---
int suma(int a, int b);

// --- Función con parámetros duplicados ---
int foo(int a, int a) {   // ERROR semántico: parámetro duplicado
    int localSinUsar;     // WARNING: declarada pero no usada (se reporta al salir del bloque)
    int x;                // Se usa sin inicializar más abajo

    // Uso antes de inicializar
    int r = x + 1;        // ERROR semántico: x sin inicializar

    // Asignar a función
    foo = 10;             // ERROR semántico: no se puede asignar a una función

    // Tipos incompatibles
    int k = 'c';          // ERROR semántico: INT <- CHAR (sin cast)
    char c = 5;           // ERROR semántico: CHAR <- INT (sin cast)

    // Arrays
    int arr[3];
    int y;
    y = arr;              // ERROR semántico: asignar array sin índice al RHS
    arr[true] = 1;        // ERROR semántico: índice no INT (BOOL)

    return r;
}

// --- Implementación simple de suma ---
int suma(int a, int b) {
    return a + b;
}

// --- main con varios casos ---
int main() {
    int res;
    int tmp;
    int vec[3];
    int noUsada;          // WARNING: no usada en el bloque

    // OK: asignaciones correctas
    g = 0;
    dGlobal = 3.14;       // según tu política de tipos: 
                          // - si es estricta → ERROR (DOUBLE <- FLOAT)
                          // - si permitís promociones → OK

    chGlobal = 'Z';

    // Uso de array
    vec[0] = 10;
    vec[1] = 20
    vec[2] = 30;          // ERROR sintáctico: falta ';' en la línea anterior (parser debe recuperarse)

    // Uso de variable no declarada
    zFantasma = 5;        // ERROR semántico: no declarada

    // else huérfano para forzar recuperación
    else {                // ERROR sintáctico: 'else' sin 'if' previo
        int sombra;       // WARNING: no usada en este bloque
    }

    // Llamada correcta a función
    res = suma(vec[0], g);

    // Tipos incompatibles (según política)
    dGlobal = tmp;        // si estricta: ERROR (DOUBLE <- INT); si con promociones: OK

    return res;
}
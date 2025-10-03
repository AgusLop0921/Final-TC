// Archivo de prueba SIN errores - SÚPER SIMPLE
// Compatible con la gramática básica

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
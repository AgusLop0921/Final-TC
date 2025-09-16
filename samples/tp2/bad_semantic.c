int cuadrado(int x) {
    int r;
    r = r * x;       // uso sin inicializar
    return r;
}

int main() {
    int a;
    int a = 5;       // doble declaración en mismo contexto
    b = 3;           // no declarado
    float f = 4.2;
    int k = f;       // incompatible (int <- float no permitido)
    int m;
    int n = m + 1;   // uso de m sin inicializar
    return 0;
}

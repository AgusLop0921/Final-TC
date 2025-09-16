int suma(int a, int b) {
    int r = a + b;
    return r;
}

float promedio(int a, int b, int c) {
    int s = suma(a, b);
    s = suma(s, c);
    float p = s / 3.0;
    return p;
}

int main() {
    int x = 5;
    int y = 10;
    int z = suma(x, y);

    if (z > 10) {
        float pr = promedio(x, y, z);
        z = z + (int)pr;
    }

    int i = 0;
    while (i < 3) {
        z = suma(z, i);
        i = i + 1;
    }

    for (int j = 0; j < 2; j = j + 1) {
        int tmp = suma(j, z);
        z = tmp;
    }

    return z;
}

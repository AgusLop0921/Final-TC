int suma(int a, int b) {
    int r = a + b;
    return r;
}

int main() {
    int x = 5;
    int y = 10;
    int z = suma(x, y);
    if (z > 10) {
        z = z - 1;
    } else {
        z = z + 1;
    }
    while (z < 20) {
        z = z + 2;
    }
    for (int i = 0; i < 3; i = i + 1) {
        z = z + i;
    }
    return z;
}

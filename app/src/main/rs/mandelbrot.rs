#pragma version(1)
#pragma rs_fp_full
#pragma rs java_package_name(io.github.pshegger.playground.rsperformance)

int width, height;
float left, right, top, bottom;
int maxIterations = 128;

static uchar4 getColor(int i) {
    float hue = 360 * (float) i / maxIterations;
    float sat = 1;
    float val = (i < maxIterations) ? 1 : 0;
    float c = val * sat;
    float k = hue / 60;
    float kmod = fmod(k, 2);
    float kmodAbs = (kmod - 1 < 0) ? -(kmod - 1) : (kmod - 1);
    float x = c * (1 - kmodAbs);
    float r1 = 0, g1 = 0, b1 = 0;

    if(k>=0 && k<=1) { r1=c; g1=x; }
    if(k>1 && k<=2)  { r1=x; g1=c; }
    if(k>2 && k<=3)  { g1=c; b1=x; }
    if(k>3 && k<=4)  { g1=x; b1=c; }
    if(k>4 && k<=5)  { r1=x; b1=c; }
    if(k>5 && k<=6)  { r1=c; b1=x; }

    float m = val - c;
    return rsPackColorTo8888(r1 + m, g1 + m, b1 + m);
}

uchar4 RS_KERNEL mandelbrot(uchar4 in, uint32_t x, uint32_t y) {
    float nzr, nzi;
    float zr = 0, zi = 0;
    float cr = left + (right - left) * (x / (double) width);
    float ci = top + (bottom - top) * (y / (double) height);

    int i = 0;
    while (i < maxIterations && zr * zr + zi * zi < 4) {
        nzr = zr * zr - zi * zi + cr;
        nzi = 2 * zr * zi + ci;
        zr = nzr;
        zi = nzi;
        i++;
    }

    return getColor(i);
}

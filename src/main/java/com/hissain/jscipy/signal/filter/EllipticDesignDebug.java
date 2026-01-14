package com.hissain.jscipy.signal.filter;

/**
 * Debug program to print elliptic filter design values.
 * Compare these with Python debug_elliptic.py output.
 */
public class EllipticDesignDebug {

    public static void main(String[] args) {
        int N = 4;
        double rp = 1.0;
        double rs = 20.0;

        System.out.println("===== Elliptic Filter Design Debug =====");
        System.out.println("N=" + N + ", rp=" + rp + "dB, rs=" + rs + "dB\n");

        // Step 1: Epsilon values
        double eps = Math.sqrt(Math.pow(10, rp / 10.0) - 1);
        double eps1 = Math.sqrt(Math.pow(10, rs / 10.0) - 1);
        System.out.println("1. Epsilon values:");
        System.out.printf("   eps:  %.15e%n", eps);
        System.out.printf("   eps1: %.15e%n%n", eps1);

        // Step 2: Discrimination factor k1
        double k1 = eps / eps1;
        double k1p = Math.sqrt(1 - k1 * k1);
        System.out.println("2. Discrimination factor:");
        System.out.printf("   k1:  %.15e%n", k1);
        System.out.printf("   k1': %.15e%n%n", k1p);

        // Step 3: Nome calculation - pass k1, k1p directly (not squared)
        double K_k1 = EllipticFunctions.ellipk(k1);
        double K_k1p = EllipticFunctions.ellipk(k1p);
        double q1 = Math.exp(-Math.PI * K_k1p / K_k1);
        double qN = Math.pow(q1, N);
        System.out.println("3. Nome calculation:");
        System.out.printf("   K(k1):   %.15e%n", K_k1);
        System.out.printf("   K(k1'):  %.15e%n", K_k1p);
        System.out.printf("   q1:      %.15e%n", q1);
        System.out.printf("   qN:      %.15e%n%n", qN);

        // Step 4: Compute k from nome (matching Python exactly)
        double sum = 0.0;
        for (int m = 0; m < 20; m++) {
            sum += Math.pow(qN, m * (m + 1));
        }
        double k = 4 * Math.sqrt(qN) * (1 + sum);
        double kp = Math.sqrt(1 - k * k);
        System.out.println("4. Elliptic modulus from nome:");
        System.out.printf("   k:  %.15e%n", k);
        System.out.printf("   k': %.15e%n%n", kp);

        // Step 5: Complete elliptic integrals (using the computed k)
        double K = EllipticFunctions.ellipk(k * k);
        double Kp = EllipticFunctions.ellipk(kp * kp);
        System.out.println("5. Complete elliptic integrals:");
        System.out.printf("   K(k^2):  %.15e%n", K);
        System.out.printf("   K(kp^2): %.15e%n%n", Kp);

        // Step 6: v0 parameter
        double asinh_eps_inv = Math.log(1.0 / eps + Math.sqrt(1.0 / (eps * eps) + 1));
        double v0 = (asinh_eps_inv / N) / K;
        System.out.println("6. v0 parameter:");
        System.out.printf("   asinh(1/eps): %.15e%n", asinh_eps_inv);
        System.out.printf("   v0:           %.15e%n%n", v0);

        // Step 7: Poles and zeros
        System.out.println("7. Poles and Zeros:");
        int pairs = N / 2;
        for (int i = 0; i < pairs; i++) {
            double ui = (2.0 * i + 1.0) / N;
            System.out.println("\nPair " + (i + 1) + ":");
            System.out.printf("   ui = %.15e%n", ui);

            // Zero
            double[] sncdu = EllipticFunctions.ellipjAtK(ui, k * k);
            double cd_u = sncdu[1] / sncdu[2];
            double zero_imag = 1.0 / (k * cd_u);

            System.out.printf("   sn(ui*K, k^2) = %.15e%n", sncdu[0]);
            System.out.printf("   cn(ui*K, k^2) = %.15e%n", sncdu[1]);
            System.out.printf("   dn(ui*K, k^2) = %.15e%n", sncdu[2]);
            System.out.printf("   cd(ui*K, k)   = %.15e%n", cd_u);
            System.out.printf("   Zero: j*%.15e%n", zero_imag);

            // Pole
            double v0Kp = v0 * Kp;
            double[] sncdv = EllipticFunctions.ellipj(v0Kp, kp * kp);
            double cd_v = sncdv[1] / sncdv[2];

            System.out.printf("   v0*Kp = %.15e%n", v0Kp);
            System.out.printf("   sn(v0*Kp, kp^2) = %.15e%n", sncdv[0]);
            System.out.printf("   cn(v0*Kp, kp^2) = %.15e%n", sncdv[1]);
            System.out.printf("   dn(v0*Kp, kp^2) = %.15e%n", sncdv[2]);
            System.out.printf("   cd(v0*Kp, kp)   = %.15e%n", cd_v);

            double denom = 1.0 + (sncdu[2] * sncdv[0]) * (sncdu[2] * sncdv[0]);
            double pole_re = -cd_u * cd_v / denom;
            double pole_im = (sncdu[0] / sncdu[2]) * cd_v / denom;

            System.out.printf("   Pole: %.15e + j*%.15e%n", pole_re, pole_im);
        }
    }
}

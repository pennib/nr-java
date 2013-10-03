
package com.snuggy.nr.chapter06;

import static com.snuggy.nr.chapter06.Static.*;
import static com.snuggy.nr.util.Static.*;
import static java.lang.Math.*;

import com.snuggy.nr.util.*;

public class Beta extends Gauleg18 {

    // Object for incomplete beta function. Gauleg18 provides coefficients
    // for Gauss-Legendre quadrature.
    private static final int SWITCH = 3000; // When to switch to quadrature
    // method.
    // private static final double EPS, FPMIN; // See end of struct for
    // initializations.
    private static final double EPS = EPS(); // numeric_limits<Doub>::epsilon();
    private static final double FPMIN = Double.MIN_VALUE / EPS; // numeric_limits<Doub>::min()/EPS;

    public double betai(final double a, final double b, final double x) throws NRException {
        // Returns incomplete beta function Ix.a; b/ for positive a and b,
        // and x between 0 and 1.
        double bt;
        if (a <= 0.0 || b <= 0.0)
            throw new NRException("Bad a or b in routine betai");
        if (x < 0.0 || x > 1.0)
            throw new NRException("Bad x in routine betai");
        if (x == 0.0 || x == 1.0)
            return x;
        if (a > SWITCH && b > SWITCH)
            return betaiapprox(a, b, x);
        bt = exp(gammln(a + b) - gammln(a) - gammln(b) + a * log(x) + b * log(1.0 - x));
        if (x < (a + 1.0) / (a + b + 2.0))
            return bt * betacf(a, b, x) / a;
        else
            return 1.0 - bt * betacf(b, a, 1.0 - x) / b;
    }

    public double betacf(final double a, final double b, final double x) {
        // Evaluates continued fraction for incomplete beta function by
        // modified Lentz�s method (5.2). User should not call directly.
        int m, m2;
        double aa, c, d, del, h, qab, qam, qap;
        qab = a + b; // These q�s will be used in factors that
        qap = a + 1.0; // occur in the coefficients (6.4.6).
        qam = a - 1.0;
        c = 1.0; // First step of Lentz�s method.
        d = 1.0 - qab * x / qap;
        if (abs(d) < FPMIN)
            d = FPMIN;
        d = 1.0 / d;
        h = d;
        for (m = 1; m < 10000; m++) {
            m2 = 2 * m;
            aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d; // One step (the even one) of the recur
            if (abs(d) < FPMIN)
                d = FPMIN; // rence.
            c = 1.0 + aa / c;
            if (abs(c) < FPMIN)
                c = FPMIN;
            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d; // Next step of the recurrence (the odd
            if (abs(d) < FPMIN)
                d = FPMIN; // one).
            c = 1.0 + aa / c;
            if (abs(c) < FPMIN)
                c = FPMIN;
            d = 1.0 / d;
            del = d * c;
            h *= del;
            if (abs(del - 1.0) <= EPS)
                break; // Are we done?
        }
        return h;
    }

    public double betaiapprox(final double a, final double b, final double x) throws NRException {
        // Incomplete beta by quadrature. Returns Ix.a; b/. User should not
        // call directly.
        int j;
        double xu, t, sum, ans;
        double a1 = a - 1.0, b1 = b - 1.0, mu = a / (a + b);
        double lnmu = log(mu), lnmuc = log(1. - mu);
        t = sqrt(a * b / (SQR(a + b) * (a + b + 1.0)));
        if (x > a / (a + b)) { // Set how far to integrate into the tail:
            if (x >= 1.0)
                return 1.0;
            xu = MIN(1., MAX(mu + 10. * t, x + 5.0 * t));
        } else {
            if (x <= 0.0)
                return 0.0;
            xu = MAX(0., MIN(mu - 10. * t, x - 5.0 * t));
        }
        sum = 0;
        for (j = 0; j < 18; j++) { // Gauss-Legendre.
            t = x + (xu - x) * y[j];
            sum += w[j] * exp(a1 * (log(t) - lnmu) + b1 * (log(1 - t) - lnmuc));
        }
        ans = sum * (xu - x) * exp(a1 * lnmu - gammln(a) + b1 * lnmuc - gammln(b) + gammln(a + b));
        return ans > 0.0 ? 1.0 - ans : -ans;
    }

    public double invbetai(final double p, final double a, final double b) throws NRException {
        // Inverse of incomplete beta function. Returns x such that Ix.a; b/ D
        // p for argument p between 0 and 1.
        final double EPS = 1.e-8;
        double pp, t, u, err, x, al, h, w, afac, a1 = a - 1., b1 = b - 1.;
        int j;
        if (p <= 0.)
            return 0.;
        else if (p >= 1.)
            return 1.;
        else if (a >= 1. && b >= 1.) { // Set initial guess. See text.
            pp = (p < 0.5) ? p : 1. - p;
            t = sqrt(-2. * log(pp));
            x = (2.30753 + t * 0.27061) / (1. + t * (0.99229 + t * 0.04481)) - t;
            if (p < 0.5)
                x = -x;
            al = (SQR(x) - 3.) / 6.;
            h = 2. / (1. / (2. * a - 1.) + 1. / (2. * b - 1.));
            w = (x * sqrt(al + h) / h) - (1. / (2. * b - 1) - 1. / (2. * a - 1.)) * (al + 5. / 6. - 2. / (3. * h));
            x = a / (a + b * exp(2. * w));
        } else {
            double lna = log(a / (a + b)), lnb = log(b / (a + b));
            t = exp(a * lna) / a;
            u = exp(b * lnb) / b;
            w = t + u;
            if (p < t / w)
                x = pow(a * w * p, 1. / a);
            else
                x = 1. - pow(b * w * (1. - p), 1. / b);
        }
        afac = -gammln(a) - gammln(b) + gammln(a + b);
        for (j = 0; j < 10; j++) {
            if (x == 0. || x == 1.)
                return x; // a or b too small for accurate calcu
            err = betai(a, b, x) - p; // lation.
            t = exp(a1 * log(x) + b1 * log(1. - x) + afac);
            u = err / t; // Halley:
            x -= (t = u / (1. - 0.5 * MIN(1., u * (a1 / x - b1 / (1. - x)))));
            if (x <= 0.)
                x = 0.5 * (x + t); // Bisect if x tries to go neg or > 1.
            if (x >= 1.)
                x = 0.5 * (x + t + 1.);
            if (abs(t) < EPS * x && j > 0)
                break;
        }
        return x;
    }

}

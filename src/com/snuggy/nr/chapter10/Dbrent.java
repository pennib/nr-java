
package com.snuggy.nr.chapter10;

import static com.snuggy.nr.refs.Refs.*;
import static com.snuggy.nr.util.Static.*;
import static java.lang.Math.*;

import com.snuggy.nr.chapter09.*;
import com.snuggy.nr.refs.*;
import com.snuggy.nr.util.*;

public class Dbrent extends Bracketmethod {

    // Brent�s method to find a minimum, modified to use derivatives.
    @SuppressWarnings("unused")
    private double xmin, fmin;
    private final double tol;

    public Dbrent() {
        this(3.0e-8);
    }

    public Dbrent(final double toll) {
        tol = (toll);
    }

    public <T extends Funcd> double minimize(final T funcd) throws NRException {
        // Given a functor funcd that computes a function and also its
        // derivative
        // function df, and given a bracketing triplet of abscissas ax, bx,
        // cx [such that bx is between ax and cx, and f(bx) is less than both
        // f(ax) and f(cx)], this routine isolates the minimum to a fractional
        // precision of about tol using a modification of Brent�s method that
        // uses derivatives. The abscissa of the minimum is returned as xmin,
        // and the minimum function value is returned as min, the returned
        // function value.
        final int ITMAX = 100;
        final double ZEPS = EPS() /* numeric_limits<double>::epsilon() */* 1.0e-3;
        boolean ok1, ok2; // Will be used as flags for whether pro-
        double a, b, d = 0.0, d1, d2, du, e = 0.0; // posed steps
                                                               // are acceptable
                                                               // or not.
        double fu, olde, tol1, tol2, u, u1, u2, xm;
        $double v = $(0.0), w = $(0.0), x = $(0.0);
        $double dv = $(0.0), dw = $(0.0), dx = $(0.0);
        $double fv = $(0.0), fw = $(0.0), fx = $(0.0);
        // Comments following will point out only differences from the routine
        // in Brent. Read that routine first.
        a = (ax.$() < cx.$() ? ax.$() : cx.$());
        b = (ax.$() > cx.$() ? ax.$() : cx.$());
        $(x, $(w, $(v, bx)));
        $(fw, $(fv, $(fx, funcd.eval(x.$()))));
        $(dw, $(dv, $(dx, funcd.df(x.$())))); // All our housekeeping chores are doubled
        // by the necessity of moving aorund derivative values as well
        // as function values.
        for (int iter = 0; iter < ITMAX; iter++) {
            xm = 0.5 * (a + b);
            tol1 = tol * abs(x.$()) + ZEPS;
            tol2 = 2.0 * tol1;
            if (abs(x.$() - xm) <= (tol2 - 0.5 * (b - a))) {
                fmin = fx.$();
                return xmin = x.$();
            }
            if (abs(e) > tol1) {
                d1 = 2.0 * (b - a); // Initialize these d�s to an out-of-bracket
                d2 = d1; // value.
                if (dw != dx)
                    d1 = (w.$() - x.$()) * dx.$() / (dx.$() - dw.$()); // Secant method with one
                                                   // point.
                if (dv != dx)
                    d2 = (v.$() - x.$()) * dx.$() / (dx.$() - dv.$()); // And the other.
                // Which of these two estimates of d shall we take? We will
                // insist that
                // they be within the bracket, and on the side pointed to by the
                // derivative at x:
                u1 = x.$() + d1;
                u2 = x.$() + d2;
                ok1 = (a - u1) * (u1 - b) > 0.0 && dx.$() * d1 <= 0.0;
                ok2 = (a - u2) * (u2 - b) > 0.0 && dx.$() * d2 <= 0.0;
                olde = e; // Movement on the step before last.
                e = d;
                if (ok1 || ok2) { // Take only an acceptable d, and if
                    // both are acceptable, then take the smallest one.
                    if (ok1 && ok2)
                        d = (abs(d1) < abs(d2) ? d1 : d2);
                    else if (ok1)
                        d = d1;
                    else
                        d = d2;
                    if (abs(d) <= abs(0.5 * olde)) {
                        u = x.$() + d;
                        if (u - a < tol2 || b - u < tol2)
                            d = SIGN(tol1, xm - x.$());
                    } else { // Bisect, not golden section.
                        d = 0.5 * (e = (dx.$() >= 0.0 ? a - x.$() : b - x.$()));
                        // Decide which segment by the sign of the derivative.
                    }
                } else {
                    d = 0.5 * (e = (dx.$() >= 0.0 ? a - x.$() : b - x.$()));
                }
            } else {
                d = 0.5 * (e = (dx.$() >= 0.0 ? a - x.$() : b - x.$()));
            }
            if (abs(d) >= tol1) {
                u = x.$() + d;
                fu = funcd.eval(u);
            } else {
                u = x.$() + SIGN(tol1, d);
                fu = funcd.eval(u);
                if (fu > fx.$()) { // If the minimum step in the downhill
                    // direction takes us uphill, then we are done.
                    fmin = fx.$();
                    return xmin = x.$();
                }
            }
            du = funcd.df(u); // Now all the housekeeping, sigh.
            if (fu <= fx.$()) {
                if (u >= x.$())
                    a = x.$();
                else
                    b = x.$();
                mov3(v, fv, dv, w.$(), fw.$(), dw.$());
                mov3(w, fw, dw, x.$(), fx.$(), dx.$());
                mov3(x, fx, dx, u, fu, du);
            } else {
                if (u < x.$())
                    a = u;
                else
                    b = u;
                if (fu <= fw.$() || w == x) {
                    mov3(v, fv, dv, w.$(), fw.$(), dw.$());
                    mov3(w, fw, dw, u, fu, du);
                } else if (fu < fv.$() || v == x || v == w) {
                    mov3(v, fv, dv, u, fu, du);
                }
            }
        }
        throw new NRException("Too many iterations in routine dbrent");
    }
}

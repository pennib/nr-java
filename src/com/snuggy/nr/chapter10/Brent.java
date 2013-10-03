
package com.snuggy.nr.chapter10;

import static com.snuggy.nr.refs.Refs.*;
import static com.snuggy.nr.util.Static.*;
import static java.lang.Math.*;

import com.snuggy.nr.refs.*;
import com.snuggy.nr.util.*;

public class Brent extends Bracketmethod {

    // Brent�s method to find a minimum.
    @SuppressWarnings("unused")
    private double xmin, fmin;
    private final double tol;

    public Brent() {
        this(3.0e-8);
    }

    public Brent(final double toll) {
        tol = (toll);
    }

    public <T extends Func_Doub_To_Doub> double minimize(final T func) throws NRException {
        // Given a function or functor f, and given a bracketing triplet of
        // abscissas ax, bx, cx (such that bx is between ax and cx, and f(bx)
        // is less than both f(ax) and f(cx)), this routine isolates the
        // minimum to a fractional precision of about tol using Brent�s method.
        // The abscissa of the minimum is returned as xmin, and the function
        // value at the minimum is returned as min, the returned function value.
        final int ITMAX = 100;
        final double CGOLD = 0.3819660;
        // final double ZEPS=numeric_limits<Doub>::epsilon()*1.0e-3;
        final double ZEPS = EPS() * 1.0e-3;
        // Here ITMAX is the maximum allowed number of iterations; CGOLD is the
        // golden ratio; and ZEPS is a small number that protects against
        // trying to achieve fractional accuracy for a minimum that happens
        // to be exactly zero.
        double a, b, d = 0.0, etemp;
        $double fu = $(0.0), fv = $(0.0), fw = $(0.), fx = $(0.0);
        double p, q, r, tol1, tol2;
        $double u = $(0.0), v = $(0.0), w = $(0.0), x = $(0.0);
        double xm;
        double e = 0.0; // This will be the distance moved on
        // the step before last.
        a = (ax.$() < cx.$() ? ax.$() : cx.$()); // a and b must be
                                                             // in ascending
                                                             // order,
        b = (ax.$() > cx.$() ? ax.$() : cx.$()); // but input
                                                             // abscissas need
                                                             // not be.
        $(x, $(w, $(v, bx))); // Initializations...
        $(fw, $(fv, $(fx, func.eval(x.$()))));
        for (int iter = 0; iter < ITMAX; iter++) { // Main program loop.
            xm = 0.5 * (a + b);
            tol2 = 2.0 * (tol1 = tol * abs(x.$()) + ZEPS);
            if (abs(x.$() - xm) <= (tol2 - 0.5 * (b - a))) { // Test for done
                                                                // here.
                fmin = fx.$();
                return xmin = x.$();
            }
            if (abs(e) > tol1) { // Construct a trial parabolic fit.
                r = (x.$() - w.$()) * (fx.$() - fv.$());
                q = (x.$() - v.$()) * (fx.$() - fw.$());
                p = (x.$() - v.$()) * q - (x.$() - w.$()) * r;
                q = 2.0 * (q - r);
                if (q > 0.0)
                    p = -p;
                q = abs(q);
                etemp = e;
                e = d;
                if (abs(p) >= abs(0.5 * q * etemp) || p <= q * (a - x.$()) || p >= q * (b - x.$()))
                    d = CGOLD * (e = (x.$() >= xm ? a - x.$() : b - x.$()));
                // The above conditions determine the acceptability of the
                // parabolic
                // fit. Here we take the golden section step into the larger of
                // the two segments.
                else {
                    d = p / q; // Take the parabolic step.
                    $(u, x.$() + d);
                    if (u.$() - a < tol2 || b - u.$() < tol2)
                        d = SIGN(tol1, xm - x.$());
                }
            } else {
                d = CGOLD * (e = (x.$() >= xm ? a - x.$() : b - x.$()));
            }
            $(u, (abs(d) >= tol1 ? x.$() + d : x.$() + SIGN(tol1, d)));
            $(fu, func.eval(u.$()));
            // This is the one function evaluation per iteration.
            if (fu.$() <= fx.$()) { // Now decide what to do with our func
                if (u.$() >= x.$())
                    a = x.$();
                else
                    b = x.$(); // tion evaluation.
                shft3(v, w, x, u.$()); // Housekeeping follows:
                shft3(fv, fw, fx, fu.$());
            } else {
                if (u.$() < x.$())
                    a = u.$();
                else
                    b = u.$();
                if (fu.$() <= fw.$() || w.$() == x.$()) {
                    $(v, w);
                    $(w, u);
                    $(fv, fw);
                    $(fw, fu);
                } else if (fu.$() <= fv.$() || v.$() == x.$() || v.$() == w.$()) {
                    $(v, u);
                    $(fv, fu);
                }
            } // Done with housekeeping. Back for
        } // another iteration.
        throw new NRException("Too many iterations in brent");
    }

}

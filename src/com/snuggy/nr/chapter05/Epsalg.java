
package com.snuggy.nr.chapter05;

import static com.snuggy.nr.util.Static.*;
import static java.lang.Math.*;

public class Epsalg {

    // Convergence acceleration of a sequence by the  algorithm. Initialize
    // by calling the constructor with arguments nmax, an upper bound on the
    // number of terms to be summed, and epss, the desired accuracy. Then make
    // successive calls to the function next, with argument the next partial
    // sum of the sequence. The current estimate of the limit of the sequence
    // is returned by next. The flag cnvgd is set when convergence is detected.

    private final double[] e; // Workspace.
    private int n, ncv;
    protected boolean cnvgd;
    private double eps, small, big, lastval, lasteps; // Numbers near machine
                                                      // underflow and

    // overflow limits.

    public Epsalg(final int nmax, final double epss) {
        e = doub_vec(nmax);
        n = (0);
        ncv = (0);
        cnvgd = (false);
        eps = (epss);
        lastval = (0.0);
        small = Double.MIN_VALUE; // numeric_limits<Doub>::min()*10.0;
        big = Double.MAX_VALUE; // numeric_limits<Doub>::max();
    }

    public double next(final double sum) {
        double diff, temp1, temp2, val;
        e[n] = sum;
        temp2 = 0.0;
        for (int j = n; j > 0; j--) {
            temp1 = temp2;
            temp2 = e[j - 1];
            diff = e[j] - temp2;
            if (abs(diff) <= small)
                e[j - 1] = big;
            else
                e[j - 1] = temp1 + 1.0 / diff;
        }
        n++;
        val = ((n & 1) != 0) ? e[0] : e[1]; // Cases of n even or odd.
        if (abs(val) > 0.01 * big)
            val = lastval;
        lasteps = abs(val - lastval);
        if (lasteps > eps)
            ncv = 0;
        else
            ncv++;
        if (ncv >= 3)
            cnvgd = true;
        return (lastval = val);
    }

}

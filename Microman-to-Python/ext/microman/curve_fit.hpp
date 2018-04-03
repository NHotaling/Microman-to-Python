#ifndef CURVE_FIT_H
#define CURVE_FIT_H


#include <vector>


struct CurveResult {
    double offset;
    double factor;
    double sum_residuals_sqr;
    double r2;
};


int do_fit(const std::vector<double> &x_data,
           const std::vector<double> &y_data,
           CurveResult &output);


#endif /* CURVE_FIT_H */

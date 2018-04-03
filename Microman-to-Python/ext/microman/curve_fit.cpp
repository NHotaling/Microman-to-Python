#include "curve_fit.hpp"

#include <cmath>


int do_fit(const std::vector<double> &x_data,
           const std::vector<double> &y_data,
           CurveResult &output) {
    if (x_data.size() != y_data.size()) {
        return 1;
    }

    double num_points = (double) x_data.size();

    double sum_x = 0.0, sum_x2 = 0.0;
    double sum_y = 0.0, sum_y2 = 0.0;
    double sum_xy = 0.0;

    for (auto x = x_data.begin(), y = y_data.begin();
         x != x_data.end();
         ++x, ++y) {
        sum_x += *x;
        sum_x2 += *x * *x;
        sum_xy += *x * *y;
        sum_y += *y;
        sum_y2 += *y * *y;
    }

    double factor = (sum_xy - sum_x * sum_y / num_points) /
        (sum_x2 - sum_x * sum_x / num_points);
    if (std::isnan(factor) || std::isinf(factor)) {
        factor = 0;
    }

    double offset = (sum_y - factor * sum_x) / num_points;
    output.offset = offset;

    double sum_residuals_sqr = (factor * factor) * sum_x2 +
        num_points * (offset * offset) + sum_y2 +
        2 * factor * offset * sum_x -
        2 * factor * sum_xy -
        2 * offset * sum_y;

    double eps = 2e-15 * ((factor * factor) * sum_x2 +
                          num_points * (offset * offset) + sum_y2);
    sum_residuals_sqr = std::max(sum_residuals_sqr, eps);

    output.sum_residuals_sqr = sum_residuals_sqr;
    output.factor = factor;

    double sum_mean_diff_sqr = sum_y2 - sum_y * sum_y / num_points;
    double r2 = 0.0;
    if (sum_mean_diff_sqr > 0.0) {
        r2 = 1.0 - sum_residuals_sqr / sum_mean_diff_sqr;
    }
    output.r2 = r2;

    return 0;
}

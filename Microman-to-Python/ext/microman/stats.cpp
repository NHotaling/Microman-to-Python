#include "Stdafx.h"

#include <cmath>
#include <cstdint>
#include <math.h>
#include <vector>

#include "image.hpp"
#include "curve_fit.hpp"


template<class T>
class ImageStats {
    Image<T>* m_image;
    Image<float> m_std_image;
    Image<float> m_mean_image;
    bool m_calc_frame_dev_and_mean;
    std::vector<double> m_max_pixel_intensity;
    std::vector<double> m_min_pixel_intensity;
    std::vector<double> m_intensity_set;
    std::vector<double> m_dev_set;
    std::vector<double> m_exposure_set;
    CurveResult m_std_est_curve;
    bool m_has_std_est_curve;
    double m_best_exposure;
    double m_best_exposure_intensity;

public:
    ImageStats(Image<T> *im) :
        m_image(im),
        m_std_image(im->width(), im->height(), 1, 1, im->nframes()),
        m_mean_image(im->width(), im->height(), 1, 1, im->nframes())
    { }

private:
    void get_exposure_intensity_curve(CurveResult &curve) {
        get_frame_dev_and_mean();

        int pos = 2;
        while (std::abs(m_dev_set[pos] - std_est(m_intensity_set[pos])) /
               m_dev_set[pos] < 0.05) {
            pos++;
            if (pos >= m_dev_set.size()) {
                break;
            }
        }

        pos--;

        do_fit(m_exposure_set, m_intensity_set, curve);
    }

public:
    /*
     *  This function calculates the number of images required to make sure that the average pixel
     *  instensity value will be within 2 pixels of the actual pixel intensity. This calculation is
     *  based on the equation for estimating the number of samples requied for a 95% confidence
     *  interval.
     *
     *  n = (Z*sigma/E)^2
     *  Z - Z-statistic for a condifence interval (~2 for 95% confidence)
     *  E - Error bounds of the confidence interval
     *  sigma - Standard deviation of pixel intensity
     *
     *  For the sake of simplicity, Z = 2 and E = 2 pixels, so that n = sigma^2.
     */
    int num_blank_samples(double exposure) {
        if (m_image->nframes() == 1) {
            return 0;
        }

        CurveResult curve;
        get_exposure_intensity_curve(curve);
        double intensity = curve.offset + curve.factor * exposure;
        double dev = std_est(intensity);

        return (int) (dev*dev);
    }

    /*
     *  This method estimates the best possible exposure value. The best possible exposure value
     *  is the time in milliseconds where the average pixel intensity value of all pixels in an
     *  image are at least 2 standard deviations below the saturation point of the camera.
     */
    double best_exposure() {
        if (m_best_exposure != 0) {
            return m_best_exposure;
        }

        CurveResult curve;
        get_exposure_intensity_curve(curve);

        double bit_depth = std::pow(2, m_image.depth);
        m_best_exposure = (bit_depth - 3 * std_est(bit_depth) - curve.offset) / curve.factor;
        m_best_exposure_intensity = curve.offset + curve.factor * m_best_exposure;

        return m_best_exposure;
    }

    double best_exposure_intensity() {
        if (m_best_exposure_intensity == 0) {
            best_exposure();
        }

        return m_best_exposure_intensity;
    }

    double abs_sigma(int i_intensity) {
        double ln = log(10.0);
        double intensity = (double) i_intensity;
        double best_int;
        double std;

        get_frame_dev_and_mean();

        if (m_image->nframes() == 1) {
            best_int = m_intensity_set[0];
            std = m_dev_set[0];
        } else {
            best_int = best_exposure_intensity();
            std = std_est(best_int);
        }

        double sigma_I = std::pow(std_est(intensity) / (intensity * ln), 2);
        double sigma_Io = std::pow(std / (best_int * ln), 2);
        double sigma_A = std::sqrt(sigma_I + sigma_Io);

        return sigma_A;
    }

    int min_conf_pix(int num_exp) {
        get_frame_dev_and_mean();

        double sqrt_N = std::sqrt((double) num_exp);
        double z = 1.96;
        int intensity;
        if (m_image->nframes() == 1) {
            intensity = (int) m_intensity_set[0];
        } else {
            intensity = best_exposure_intensity();
        }
        double test = z * abs_sigma(intensity) / sqrt_N;
        while (test < 0.01 && intensity > 0) {
            test = z * abs_sigma(--intensity) / sqrt_N;
        }

        return intensity;
    }

    Image<T> *get_frame_dev_and_mean() {
        if (m_calc_frame_dev_and_mean) {
            return m_std_image;
        }

        int flen = m_image->width() * m_image->height();
        std::vector<float> t_pixel;
        t_pixel.resize(flen);
        std::vector<double> pixel_mean;
        pixel_mean.resize(flen);
        std::vector<double> pixel_dev;
        pixel_dev.resize(flen);

        for (int i = 0; i < m_image->nframes(); ++i) {
            std::fill(pixel_mean.begin(), pixel_mean.end(), 0);
            std::fill(pixel_dev.begin(), pixel_dev.end(), 0);
            for (int j = 0; j < m_image->nslices(); ++j) {
                auto p = m_image->get_plane(1, j, i);
                for (int k = 0; k < flen; ++k) {
                    t_pixel[k] = p.data()[k];
                }
                for (int k = 0; k < flen; ++k) {
                    pixel_dev[k] += t_pixel[k] * t_pixel[k];
                    pixel_mean[k] += t_pixel[k];
                }
            }

            auto std_plane = m_std_image.get_plane(1, 1, i);
            auto mean_plane = m_mean_image.get_plane(1, 1, i);

            for (int j = 0; j < flen; ++j) {
                pixel_mean[j] /= (double) m_image->nslices();
                pixel_dev[j] /= (double) m_image->nslices();
                pixel_dev[j] = std::sqrt(pixel_dev[j] - pixel_mean[j]*pixel_mean[j]);
                std_plane.data()[j] = (float) pixel_mean[j];
                mean_plane.data()[j] = (float) pixel_dev[j];
            }
        }

        m_max_pixel_intensity.resize(m_image->nframes());
        m_min_pixel_intensity.resize(m_image->nframes());
        m_intensity_set.resize(m_image->nframes());
        m_dev_set.resize(m_image->nframes());

        for (int i = 0; i < m_image->nframes(); ++i) {
            auto mean_plane = m_mean_image.get_plane(i);
            auto std_plane = m_std_image.get_plane(i);
            m_max_pixel_intensity[i] = mean_plane.get_max();
            m_min_pixel_intensity[i] = mean_plane.get_min();
            m_intensity_set[i] = mean_plane.get_mean();
            m_dev_set[i] = std_plane.get_std();
        }

        m_calc_frame_dev_and_mean = true;

        return &m_std_image;
    }



    double std_est(double intensity) {
        if (!m_has_std_est_curve) {
            get_frame_dev_and_mean();
            if (m_image->nframes() < 3) {
                return 0.0;
            }

            std::vector<double> sqrt_pixels;
            std::vector<double> std_pixels;
            sqrt_pixels.resize(3);
            std_pixels.resize(3);
            for (int i = 0; i < 3; ++i) {
                sqrt_pixels[i] = std::sqrt(m_intensity_set[i]);
                std_pixels[i] = m_dev_set[i];
            };

            do_fit(sqrt_pixels, std_pixels, m_std_est_curve);
            m_has_std_est_curve = true;
        }

        return (std::sqrt(intensity) *
                m_std_est_curve.factor +
                m_std_est_curve.offset);
    }

    int run_stats() {
        return 42;
    }
};


#ifdef __cplusplus
extern "C" {
#endif
    int common_run_stats(unsigned char *data, int width, int height, int depth,
                         int nslices, int nchannels, int nframes) {
        switch (depth) {
        case 8: {
            auto im = Image<unsigned char>(data, width, height, nslices, nchannels, nframes);
            auto stats = ImageStats<unsigned char>(&im);
            return stats.run_stats();
        }
        case 12:
        case 14:
        case 16: {
            auto im = Image<unsigned short>((unsigned short *)data, width, height, nslices, nchannels, nframes);
            auto stats = ImageStats<unsigned short>(&im);
            return stats.run_stats();
        }
        default:
            return 0;
        }
    }
#ifdef __cplusplus
}
#endif

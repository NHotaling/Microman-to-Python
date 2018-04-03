#ifndef IMAGE_H
#define IMAGE_H

#include <stdlib.h>

template<class T>
class ImagePlane {
    T *m_data;
    int m_width;
    int m_height;
    bool m_own_data;

public:
    ImagePlane(T* data, int width, int height) :
        m_data(data), m_width(width), m_height(height), m_own_data(false) {

    }

    ImagePlane(int width, int height) :
        m_data(0), m_width(width), m_height(height), m_own_data(true) {
        m_data = calloc(width * height, sizeof(T));
    }

    ~ImagePlane() {
        if (m_own_data) {
            free(m_data);
        }
    }

    T* data() const { return m_data; }
    int width() const { return m_width; }
    int height() const { return m_height; }
    int length() const { return m_width * m_height; }

    T get_max() const {
        T m = m_data[0];
        for (int i = 1; i < length(); ++i) {
            if (m_data[i] > m) {
                m = m_data[i];
            }
        }
        return m;
    }

    T get_min() const {
        T m = m_data[0];
        for (int i = 1; i < length(); ++i) {
            if (m_data[i] < m) {
                m = m_data[i];
            }
        }
        return m;
    }

    T get_mean() const {
        T m = 0;
        for (int i = 0; i < length(); ++i) {
            m += m_data[i];
        }
        m /= (T)length();
        return m;
    }

    T get_std() const {
        T m = 0;
        for (int i = 0; i < length(); ++i) {
            m += m_data[i] * m_data[i];
        }
        m /= (T)length();
        m = sqrt(abs(m));
        return m;
    }
};


template<class T>
class Image {
    T *m_data;
    int m_width;
    int m_height;
    int m_nslices;
    int m_nchannels;
    int m_nframes;
    bool m_own_data;

public:
    Image(T *data, int width, int height, int nslices, int nchannels, int nframes) :
        m_data(data),
        m_width(width),
        m_height(height),
        m_nslices(nslices),
        m_nchannels(nchannels),
        m_nframes(nframes),
        m_own_data(false) {

    }

    Image(int width, int height, int nslices, int nchannels, int nframes) :
        m_width(width),
        m_height(height),
        m_nslices(nslices),
        m_nchannels(nchannels),
        m_nframes(nframes),
        m_own_data(true) {
        m_data = (T *)calloc(width * height * nslices * nchannels * nframes, sizeof(T));
    }

    ~Image() {
        if (m_own_data) {
            free(m_data);
        }
    }

    T* data() const { return m_data; }
    int width() const { return m_width; }
    int height() const { return m_height; }
    int nslices() const { return m_nslices; }
    int nchannels() const { return m_nchannels; }
    int nframes() const { return m_nframes; }

    ImagePlane<T> const get_plane(int channel, int slice, int frame) {
        return ImagePlane<T>::ImagePlane(
                m_data + frame * width * height * nslices * nchannels * sizeof(T) +
                channel * width * height * nslices * sizeof(T) +
                slice * width * height * sizeof(T),
                m_width, m_height);
    }

    ImagePlane<T> const get_plane(int stack) {
        return ImagePlane<T>::ImagePlane(
                m_data + stack * width * height * sizeof(T),
                m_width, m_height);
    }
};

#endif /* IMAGE_H */

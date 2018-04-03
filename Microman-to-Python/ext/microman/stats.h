#ifndef STATS_H
#define STATS_H

#ifdef __cplusplus
extern "C" {
#endif
    int common_run_stats(unsigned char *data, int width, int height, int depth,
                         int nslices, int nchannels, int nframes);
#ifdef __cplusplus
}
#endif

#endif /* STATS_H */

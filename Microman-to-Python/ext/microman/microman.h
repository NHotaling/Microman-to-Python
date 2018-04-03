// microman.h

#pragma once

#include "stats.h"

using namespace System;

namespace microman {

	public ref class Stats
	{
    public:
        static int run_stats(array<System::Byte>^ data, int width, int height,
                             int depth, int nslices, int nchannels,
                             int nframes) {
            pin_ptr<System::Byte> p = &data[0];
            unsigned char* pby = p;
            return common_run_stats(pby, width, height, depth, nslices,
                                    nchannels, nframes);
        }
	};
}

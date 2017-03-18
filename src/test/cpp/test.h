#ifndef HONEST_PROFILER_TEST_H
#define HONEST_PROFILER_TEST_H

#ifdef __APPLE__
#   include <UnitTest++/UnitTest++.h>
#else
#   include <UnitTest++.h>
#endif

#if __GNUC__ == 4 && __GNUC_MINOR__ < 6
#	define DISABLE_CPP11
#endif

#endif
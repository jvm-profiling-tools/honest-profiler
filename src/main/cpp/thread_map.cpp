#include "thread_map.h"

#ifdef CONCURRENT_MAP_TBB

TRACE_DEFINE_BEGIN(TbbMap, kTraceTbbMapTotal)
    TRACE_DEFINE("TBB Hash Map put")
    TRACE_DEFINE("TBB Hash Map get")
    TRACE_DEFINE("TBB Hash Map remove")
TRACE_DEFINE_END(TbbMap, kTraceTbbMapTotal);

#endif

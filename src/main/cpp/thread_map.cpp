#include "thread_map.h"

#include <sys/syscall.h>
#include <unistd.h>

#ifdef __APPLE__
#include <mach/mach.h>
#endif

// taken from Wine's get_unix_tid
int gettid() {
  int ret = -1;
#if defined(__linux__)
  ret = syscall(SYS_gettid);
#elif defined(__APPLE__)
  //ret = pthread_getthreadid_np();
  ret = mach_thread_self();
  mach_port_deallocate(mach_task_self(), ret);
#elif defined(__NetBSD__)
  ret = _lwp_self();
#elif defined(__FreeBSD__)
  long lwpid;
  thr_self(&lwpid);
  ret = lwpid;
#elif defined(__DragonFly__)
  ret = lwp_gettid();
#else
  ret = pthread_self();
#endif
  return ret;
}

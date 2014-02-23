#include <signal.h>
#include <fstream>

#include "globals.h"
#include "stacktraces.h"
#include "processor.h"
#include "log_writer.h"

#include <boost/iostreams/stream.hpp>
#include <boost/iostreams/device/mapped_file.hpp>

#ifndef PROFILER_H
#define PROFILER_H

namespace io = boost::iostreams;
using std::ofstream;

typedef io::stream_buffer<io::mapped_file_sink> mapped_buffer;

class SignalHandler {
public:
  SignalHandler() {}

  struct sigaction SetAction(void (*sigaction)(int, siginfo_t *, void *));

  bool SetSigprofInterval(int sec, int usec);

private:
  DISALLOW_COPY_AND_ASSIGN(SignalHandler);
};

class Profiler final {
public:
  explicit Profiler(jvmtiEnv *jvmti) : jvmti_(jvmti) {
    // main object graph instantiated here
    // these objects all live for the lifecycle of the program
    const size_t size = 10 * 1024 * 1024;
    const char* fileName = "log.hpl";

    // too large to stack allocate
    char* zeroingBuffer = new char[size];
    memset(zeroingBuffer, 0, size);
    ofstream file("log.hpl", ofstream::out | ofstream::binary);
    file.write(zeroingBuffer, size);
    file.close();
    delete zeroingBuffer;

    std::cout << "opened file" << std::endl;

    static io::mapped_file_sink sink;
    sink.open(fileName, size, 0);
    mapFile = new mapped_buffer(sink);

    logFile = new ostream(mapFile);
    writer = new LogWriter(*logFile, &Profiler::lookupFrameInformation, jvmti);
    buffer = new CircularQueue(*writer);
    processor = new Processor(jvmti, *writer, *buffer);
  }

  bool start(JNIEnv *jniEnv);

  void stop();

  void DumpToFile(FILE *file);

  void handle(int signum, siginfo_t *info, void *context);

  ~Profiler() {
    delete buffer;
    delete logFile;
    delete writer;
    delete processor;
    delete mapFile;
  }
  ;

private:
  jvmtiEnv *jvmti_;

  mapped_buffer *mapFile;

  ostream *logFile;

  LogWriter *writer;

  CircularQueue *buffer;

  Processor *processor;

  SignalHandler handler_;

  static int failures_[kNumCallTraceErrors + 1]; // they are indexed from 1

  static bool lookupFrameInformation(const JVMPI_CallFrame &frame,
                                     jvmtiEnv *jvmti,
                                     MethodListener &logWriter);

  DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H

#ifndef TRACE_H
#define TRACE_H

/** Simple tracing framework ported from @preshing's Turf Trace_Counters
 *  URL: https://github.com/preshing/turf
 */

#include <iostream>

#ifdef ENABLE_TRACING
    #define TRACE_HISTORY_SZ (1<<6)
#else
    #define TRACE_HISTORY_SZ 1
#endif

class TraceGroup {
public:
    struct Counter {
        std::atomic<int> count;
        const char* str;

        Counter(int i, const char* d) : count(i), str(d) {
        }

        Counter(const Counter &cnt) : count(cnt.count.load()), str(cnt.str) {
        }
    };

private:
    const char* m_name;
    Counter* m_counters;
    int m_numCounters;

public:
    TraceGroup(const char* name, Counter* counters, int numCounters)
        : m_name(name), m_counters(counters), m_numCounters(numCounters) {
    }

    void dump() {
        std::cout << "--------------- " << m_name << std::endl;
    	for (int i = 0; i < m_numCounters; i++) {
            std::cout << "#### " << m_counters[i].str << ": " << m_counters[i].count.load(std::memory_order_relaxed) << std::endl;
    	}
    }

    void dumpIfUsed() {
		for (int i = 0; i < m_numCounters; i++) {
        	if (m_counters[i].count.load(std::memory_order_relaxed)) {
            	dump();
            	break;
        	}
    	}
    }

    void reset() {
        for (int i = 0; i < m_numCounters; i++) {
            m_counters[i].count.store(0, std::memory_order_relaxed);
        }
    }
};

class HistoryRecorder {
public:
    HistoryRecorder(TraceGroup::Counter* counters) : m_current(0), m_counters(counters) {
    }

    void record(int traceid) {
        m_history[m_current++ & (TRACE_HISTORY_SZ - 1)] = traceid;
    }

    void reset() {
        m_current = 0;
    }

    void dump() {
        std::cout << "--------------- THREAD HISTORY DUMP" << std::endl;
        int last = m_current - 1;
        for (int i = last; i > (last > TRACE_HISTORY_SZ ? last - TRACE_HISTORY_SZ : 0); i--) {
            std::cout << "#### " <<  m_counters[m_history[i & (TRACE_HISTORY_SZ - 1)]].str << std::endl;
        }
    }

private:
    unsigned int m_current;
    int m_history[TRACE_HISTORY_SZ];
    TraceGroup::Counter* m_counters;
};

#ifndef ENABLE_TRACING
    #define TRACE(group, index) 	            do {} while(0)
    
    #define TRACE_DECLARE(group, count)         extern TraceGroup::Counter Trace_##group[count]; \
                                                extern TraceGroup TraceGroup_##group;

    #define TRACE_DEFINE_BEGIN(group, count)    TraceGroup::Counter Trace_##group[count] = {
    #define TRACE_DEFINE(desc)                      TraceGroup::Counter(0, desc),
    #define TRACE_DEFINE_END(group, count)      }; \
                                                TraceGroup TraceGroup_##group(#group, Trace_##group, count);
#else
    #define TRACE(group, index) 	            Trace_##group[index].count.fetch_add(1, std::memory_order_relaxed); \
                                                History_##group.record(index)

    #define TRACE_DECLARE(group, count)         extern TraceGroup::Counter Trace_##group[count]; \
                                                extern TraceGroup TraceGroup_##group; \
                                                extern thread_local HistoryRecorder History_##group;                              

    #define TRACE_DEFINE_BEGIN(group, count)    TraceGroup::Counter Trace_##group[count] = {
    #define TRACE_DEFINE(desc)                      TraceGroup::Counter(0, desc),
    #define TRACE_DEFINE_END(group, count)      }; \
                                                TraceGroup TraceGroup_##group(#group, Trace_##group, count); \
                                                thread_local HistoryRecorder History_##group(Trace_##group);
#endif

#endif
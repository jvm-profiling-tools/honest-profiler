#ifndef TRACE_H
#define TRACE_H

/** Simple tracing framework ported from @preshing's Turf Trace_Counters
 *  URL: https://github.com/preshing/turf
 */

#include <iostream>

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
    	printf("--------------- %s\n", m_name);
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

#define TRACE_DECLARE(group, count)      extern TraceGroup::Counter Trace_##group[count]; extern TraceGroup TraceGroup_##group;
#define TRACE_DEFINE_BEGIN(group, count) TraceGroup::Counter Trace_##group[count] = {
#define TRACE_DEFINE(desc)                   TraceGroup::Counter(0, desc),
#define TRACE_DEFINE_END(group, count)   }; \
                                         TraceGroup TraceGroup_##group(#group, Trace_##group, count);

#ifndef ENABLE_TRACING
	#define TRACE(group, index) 	     do {} while(0)
#else
	#define TRACE(group, index) 	     Trace_##group[index].count.fetch_add(1, std::memory_order_relaxed)
#endif

#endif
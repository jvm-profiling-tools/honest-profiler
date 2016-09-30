#include "test.h"
#include "../../main/cpp/globals.h"
#include <thread>
#include <chrono>
#include <vector>
#include <atomic>
#include <unistd.h>

TEST(ClockService) {
	timespec t1, t2;
    time_t begin, end;

    TimeUtils::init();

    begin = std::time(0);

    usleep(2000); // 2 ms

    TimeUtils::current_utc_time(&t1);

    usleep(2000);

    TimeUtils::current_utc_time(&t2);

    usleep(2000);

    end = std::time(0);

    CHECK(begin <= t1.tv_sec && t1.tv_sec <= t2.tv_sec && t2.tv_sec <= end);
}

TEST(ClockConcurrent) {
	constexpr int cnt = 2;
	std::atomic<time_t> arr[cnt];
	std::vector<std::thread> vec;

	time_t begin, end;

	TimeUtils::init();

	begin = std::time(0);

	for (int i = 0; i < cnt; i++) {
		vec.push_back(std::thread([i, &arr]() { 
			timespec t;
			TimeUtils::current_utc_time(&t);
			arr[i].store(t.tv_sec, std::memory_order_release);
		}));
	}

	for (int i = 0; i < cnt; i++)
		vec[i].join();

	end = std::time(0);

	for (int i = 0; i < cnt; i++) {
		time_t t = arr[i].load(std::memory_order_consume);
		CHECK(begin <= t && t <= end);
	}
}
#include "concurrent_map.h"

namespace map {

TRACE_DEFINE_BEGIN(LFMap, kTraceLFMapTotal)
    TRACE_DEFINE("[LockFreeMapPrimitives::find] Item not found")
    TRACE_DEFINE("[LockFreeMapPrimitives::find] Item found")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Max number of jumps reached")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Update bucket value")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Update value race detected")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Can't allocate a cell, map is too full")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Allocate fresh bucket")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Allocation race detected")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Set bucket value")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] Set value race detected")
    TRACE_DEFINE("[LockFreeMapPrimitives::insertOrUpdate] No empty cell in the neighbourhood")
    TRACE_DEFINE("[LockFreeMapProvider::remove] Item not found")
    TRACE_DEFINE("[LockFreeMapProvider::remove] Remove bucket's value")
    TRACE_DEFINE("[LockFreeMapProvider::remove] Remove value race detected (giving up)")
    TRACE_DEFINE("[LockFreeMapProvider::migrationStart#1] Conflicting migration found")
    TRACE_DEFINE("[LockFreeMapProvider::migrationStart#2] Conflicting migration found")
    TRACE_DEFINE("[Migration::run] Can't participate in job that already ended")
    TRACE_DEFINE("[Migration::run] Migration interrupted by end of job")
    TRACE_DEFINE("[Migration::run] Overflow detected during migration")
    TRACE_DEFINE("[Migration::run] No more blocks to migrate")
    TRACE_DEFINE("[Migration::run] Not the last thread")
    TRACE_DEFINE("[Migration::run] Overflow migration already started")
    TRACE_DEFINE("[Migration::migrateRange] Unallocated cell migration flag")
    TRACE_DEFINE("[Migration::migrateRange] Unallocated cell insert race")
    TRACE_DEFINE("[Migration::migrateRange] Allocated cell value insert race")
    TRACE_DEFINE("[Migration::migrateRange] Allocated cell migration flag")
    TRACE_DEFINE("[Migration::migrateRange] Racing erase when migrating allocated bucket")
TRACE_DEFINE_END(LFMap, kTraceLFMapTotal);

const GC::EpochType GC::kEpochInitial = -1;

GC DefaultGC;

}
SHELL:=/bin/bash
UNAME:=$(shell uname | tr '[A-Z]' '[a-z]')

ifeq ($(UNAME), darwin)
  READLINK_ARGS:=""
  PLATFORM_WARNINGS:=-Weverything -Wno-c++98-compat-pedantic -Wno-padded \
	-Wno-missing-prototypes
  PLATFORM_COPTS:=-std=c++11 -stdlib=libc++ -DTARGET_RT_MAC_CFM=0
  HEADERS:=Headers
  CC=clang++
  LDFLAGS=-Wl,-fatal_warnings -Wl,-std=c++11 -Wl,-stdlib=libc++
  BITS?=32
  ifeq ($(BITS), 64)
    # Why is this not $!$#@ defined?
    PLATFORM_COPTS+=-D__LP64__=1
  endif
else ifeq ($(UNAME), linux)
  READLINK_ARGS:="-f"
  PLATFORM_COPTS:=-mfpmath=sse -std=gnu++0x
  PLATFORM_WARNINGS:=-Wframe-larger-than=16384 -Wno-unused-but-set-variable \
    -Wunused-but-set-parameter -Wvla -Wno-conversion-null \
    -Wno-builtin-macro-redefined
  HEADERS:=include
  CC=g++
  LDFLAGS=-Wl,--fatal-warnings
endif

JAVA_HOME := $(shell \
	[[ -n "$${JAVA_HOME}" ]] || \
	  JAVA_HOME=$$(dirname $$(readlink $(READLINK_ARGS) $$(which java)))/../; \
	[[ "$${JAVA_HOME}" =~ /jre/ ]] && JAVA_HOME=$${JAVA_HOME}/../; \
	[[ -n "$${JAVA_HOME}" ]] || (echo "Cannot find JAVA_HOME" && exit) ; \
	echo $${JAVA_HOME})
AGENT=liblagent.so
LIBS=-ldl -lboost_iostreams
TEST_LIBS=-lUnitTest++ $(LIBS)
BUILD_DIR ?= $(shell mkdir build 2> /dev/null ; echo build)
TEST_BUILD_DIR ?= $(shell mkdir build-test 2> /dev/null ; echo build-test)
SRC_DIR:=${PWD}/src/main/cpp
TEST_DIR:=${PWD}/src/test/cpp
OPT?=-O2
GLOBAL_WARNINGS=-Wall -Werror -Wformat-security -Wno-char-subscripts \
	-Wno-sign-compare -Wno-strict-overflow -Wwrite-strings -Wnon-virtual-dtor \
	-Woverloaded-virtual
GLOBAL_COPTS=-fdiagnostics-show-option \
	-fno-omit-frame-pointer -fno-strict-aliasing -funsigned-char \
	-fno-asynchronous-unwind-tables -msse2 -g \
	-D__STDC_FORMAT_MACROS
COPTS:=$(PLATFORM_COPTS) $(GLOBAL_COPTS) $(PLATFORM_WARNINGS) \
	$(GLOBAL_WARNINGS) $(OPT)

# TODO: consider re-adding in production -fno-exceptions

INCLUDES=-I$(JAVA_HOME)/$(HEADERS) -I$(JAVA_HOME)/$(HEADERS)/$(UNAME) 
TEST_INCLUDES=$(INCLUDES) -I/usr/include/unittest++

# LDFLAGS+=-Wl,--export-dynamic-symbol=Agent_OnLoad

SOURCES=$(wildcard $(SRC_DIR)/*.cc)
_OBJECTS=$(SOURCES:.cc=.pic.o)
OBJECTS = $(patsubst $(SRC_DIR)/%,$(BUILD_DIR)/%,$(_OBJECTS))

TEST_SOURCES=$(wildcard $(TEST_DIR)/*.cc)
_TEST_OBJECTS=$(TEST_SOURCES:.cc=.pic.o)
TEST_OBJECTS = $(patsubst $(TEST_DIR)/%,$(TEST_BUILD_DIR)/%,$(_TEST_OBJECTS)) $(OBJECTS)

$(BUILD_DIR)/%.pic.o: $(SRC_DIR)/%.cc
	$(CC) $(INCLUDES) $(COPTS) -Fvisibility=hidden -fPIC -c $< -o $@

$(TEST_BUILD_DIR)/%.pic.o: $(TEST_DIR)/%.cc
	$(CC) $(TEST_INCLUDES) $(COPTS) -Fvisibility=hidden -fPIC -c $< -o $@

$(AGENT): $(OBJECTS)
	$(CC) $(COPTS) -shared -o $(BUILD_DIR)/$(AGENT) \
	  -Bsymbolic $(OBJECTS) $(LIBS)

test: $(TEST_OBJECTS)
	$(CC) $(COPTS) -o $(TEST_BUILD_DIR)/test \
	  -Bsymbolic $(TEST_OBJECTS) $(TEST_LIBS)
	$(TEST_BUILD_DIR)/test

all: $(AGENT)

clean:
	rm -rf $(BUILD_DIR)/*
	rm -rf $(TEST_BUILD_DIR)/*

format:
	clang-format-3.4 -style=llvm -i ${SRC_DIR}/*
	clang-format-3.4 -style=llvm -i ${TEST_DIR}/*


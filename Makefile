SHELL:=/bin/bash
UNAME:=$(shell uname | tr '[A-Z]' '[a-z]')

LDFLAGS:= 
ifeq ($(UNAME), darwin)
  READLINK_ARGS:=""
  PLATFORM_WARNINGS:= 
  PLATFORM_COPTS:=-DTARGET_RT_MAC_CFM=0
  HEADERS:=Headers
  CXX=clang++
  BITS?=32
  ifeq ($(BITS), 64)
    # Why is this not $!$#@ defined?
    PLATFORM_COPTS+=-D__LP64__=1
  endif
	INCLUDES=
else ifeq ($(UNAME), linux)
  READLINK_ARGS:=""
  PLATFORM_COPTS:= 
  PLATFORM_WARNINGS:= 
  HEADERS:=include
	# include of gcc 4.8 headers specifically to work around
	# https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=729933
	# equivalent in ubuntu 13.10
	INCLUDES=-I/usr/include/i386-linux-gnu/c++/4.8/
endif

# compiler specific options
ifeq ($(CXX), clang++)
  CXX_COPTS:=-std=c++11 -DTARGET_RT_MAC_CFM=0
	CXX_WARNINGS:=-Weverything -Wno-c++98-compat-pedantic -Wno-padded \
		-Wno-missing-prototypes
  LDFLAGS+=-Wl,-fatal_warnings -Wl,-std=c++11 -Wl,-stdlib=libc++
else ifeq ($(CXX), g++)
	CXX_COPTS:=-mfpmath=sse -std=gnu++0x
  CXX_WARNINGS:=-Wframe-larger-than=16384 -Wno-unused-but-set-variable \
    -Wunused-but-set-parameter -Wvla -Wno-conversion-null \
    -Wno-builtin-macro-redefined
  LDFLAGS+=-Wl,--fatal-warnings
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
COPTS:=$(PLATFORM_COPTS) $(CXX_COPTS) $(GLOBAL_COPTS) $(PLATFORM_WARNINGS) \
	$(CXX_WARNINGS) $(GLOBAL_WARNINGS) $(OPT)

# TODO: consider re-adding in production -fno-exceptions

INCLUDES+=-I /usr/include/i386-linux-gnu/c++/4.8/ -I$(JAVA_HOME)/$(HEADERS) -I$(JAVA_HOME)/$(HEADERS)/$(UNAME) 
TEST_INCLUDES=$(INCLUDES) -I/usr/include/unittest++

# LDFLAGS+=-Wl,--export-dynamic-symbol=Agent_OnLoad

SOURCES=$(wildcard $(SRC_DIR)/*.cpp)
_OBJECTS=$(SOURCES:.cpp=.pic.o)
OBJECTS = $(patsubst $(SRC_DIR)/%,$(BUILD_DIR)/%,$(_OBJECTS))

TEST_SOURCES=$(wildcard $(TEST_DIR)/*.cpp)
_TEST_OBJECTS=$(TEST_SOURCES:.cpp=.pic.o)
TEST_OBJECTS = $(patsubst $(TEST_DIR)/%,$(TEST_BUILD_DIR)/%,$(_TEST_OBJECTS)) $(OBJECTS)

$(BUILD_DIR)/%.pic.o: $(SRC_DIR)/%.cpp
	$(CXX) $(INCLUDES) $(COPTS) -Fvisibility=hidden -fPIC -c $< -o $@

$(TEST_BUILD_DIR)/%.pic.o: $(TEST_DIR)/%.cpp
	$(CXX) $(TEST_INCLUDES) $(COPTS) -Fvisibility=hidden -fPIC -c $< -o $@

$(AGENT): $(OBJECTS)
	$(CXX) $(COPTS) -shared -o $(BUILD_DIR)/$(AGENT) \
	  -Bsymbolic $(OBJECTS) $(LIBS)

test: $(TEST_OBJECTS)
	$(CXX) $(COPTS) -o $(TEST_BUILD_DIR)/test \
	  -Bsymbolic $(TEST_OBJECTS) $(TEST_LIBS)
	$(TEST_BUILD_DIR)/test

all: $(AGENT)

clean:
	rm -rf $(BUILD_DIR)/*
	rm -rf $(TEST_BUILD_DIR)/*

format:
	clang-format-3.4 -style=llvm -i ${SRC_DIR}/*
	clang-format-3.4 -style=llvm -i ${TEST_DIR}/*


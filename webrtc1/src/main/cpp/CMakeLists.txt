# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

project("webrtc")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic")

include_directories(src/main/cpp)
include_directories(src/main/cpp/include)

add_definitions(-DFIXED_POINT -DUSE_KISS_FFT -DHAVE_CONFIG_H)

file(GLOB_RECURSE WEBRTC_SRC
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.c"
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.cpp"
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.cc"
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.cxx"
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.m"
        "${CMAKE_SOURCE_DIR}/src/main/cpp/*.mm"
)

add_library( # Sets the name of the library.
        WRtcAudio

        # Sets the library as a shared library.
        SHARED

        # Sources collected from src/main/cpp/webrtc
        ${WEBRTC_SRC}
)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        WRtcAudio

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

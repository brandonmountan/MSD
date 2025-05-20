project(A6_Parallels_OpenMP)
set(CMAKE_CXX_STANDARD 20)
# Manually set flags for OpenMP on macOS
if(APPLE)
    # Set OpenMP include and library paths for macOS
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Xpreprocessor -fopenmp")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -I/opt/homebrew/opt/libomp/include")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -L/opt/homebrew/opt/libomp/lib")
    # Manually link OpenMP runtime
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L/opt/homebrew/opt/libomp/lib -lomp")
else()
    # For other platforms, link OpenMP with -fopenmp
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")
endif()
# Add your source files here
add_executable(A6_Parallels_OpenMP main.cpp)
# Add OpenMP explicitly to the target libraries (on non-Apple systems)
if(NOT APPLE)
    find_package(OpenMP REQUIRED)
    target_link_libraries(A6_Parallels_OpenMP OpenMP::OpenMP_CXX)
endif()
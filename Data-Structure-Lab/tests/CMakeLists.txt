cmake_minimum_required(VERSION 3.8)
project(tests)

add_subdirectory(lib/googletest-release-1.8.0)
add_subdirectory(menu_tests)
add_subdirectory(performance_analysis_tests)
add_subdirectory(basic_graph_tests)
add_subdirectory(labyrinth_tests)
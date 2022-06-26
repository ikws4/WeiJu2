cmake_minimum_required(VERSION 3.10.1)

project(jsitter)

include_directories(src)
include_directories(tree-sitter/lib/src tree-sitter/lib/include)

find_library(log-lib log)

add_library(jsitter SHARED src/jsitter.cc tree-sitter/lib/src/lib.c)

# Grammars
add_library(tslua SHARED grammars/tree-sitter-lua/src/parser.c grammars/tree-sitter-lua/src/scanner.c)

target_link_libraries(jsitter tslua ${log-lib})

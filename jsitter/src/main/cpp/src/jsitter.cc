#include <jni.h>
#include <tree_sitter/api.h>
#include <android/log.h>


/*****************/
/* section - LOG */
/*****************/
#define LOG_TAG "jsitter"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);


/*********************/
/* section - initial */
/*********************/

static jclass nodeClass;
static jfieldID nodeIdField;
static jfieldID nodeTreePtrField;
static jfieldID nodeContext0Field;
static jfieldID nodeContext1Field;
static jfieldID nodeContext2Field;
static jfieldID nodeContext3Field;
static jfieldID nodeEndByte;
static jfieldID nodeEndRow;
static jfieldID nodeEndColumn;

static jclass queryMatchClass;
static jfieldID queryMatchId;
static jfieldID queryMatchPatternIndex;
static jfieldID queryMatchCaptures;

static jclass queryCaptureClass;
static jfieldID queryCaptureName;
static jfieldID queryCaptureNode;

#define _loadClass(VARIABLE, NAME)                  \
{                                                   \
    jclass temp;                                    \
    temp = env->FindClass(NAME);                    \
    VARIABLE = (jclass) env->NewGlobalRef(temp);    \
    env->DeleteLocalRef(temp);                      \
}

#define _loadField(VARIABLE, CLASS, NAME, TYPE)     \
{                                                   \
    VARIABLE = env->GetFieldID(CLASS, NAME, TYPE);  \
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    _loadClass(nodeClass, "io/ikws4/jsitter/TSNode")
    _loadField(nodeIdField, nodeClass, "id", "J")
    _loadField(nodeTreePtrField, nodeClass, "treePtr", "J")
    _loadField(nodeContext0Field, nodeClass, "context0", "I")
    _loadField(nodeContext1Field, nodeClass, "context1", "I")
    _loadField(nodeContext2Field, nodeClass, "context2", "I")
    _loadField(nodeContext3Field, nodeClass, "context3", "I")
    _loadField(nodeEndByte, nodeClass, "endByte", "I")
    _loadField(nodeEndRow, nodeClass, "endRow", "I")
    _loadField(nodeEndColumn, nodeClass, "endColumn", "I")

    _loadClass(queryMatchClass, "io/ikws4/jsitter/TSQueryMatch")
    _loadField(queryMatchId, queryMatchClass, "id","I")
    _loadField(queryMatchPatternIndex, queryMatchClass, "patternIndex","I")
    _loadField(queryMatchCaptures, queryMatchClass, "captures",
               "[Lio/ikws4/jsitter/TSQueryCapture;")

    _loadClass(queryCaptureClass, "io/ikws4/jsitter/TSQueryCapture")
    _loadField(queryCaptureName, queryCaptureClass, "name", "Ljava/lang/String;")
    _loadField(queryCaptureNode, queryCaptureClass, "node", "Lio/ikws4/jsitter/TSNode;")

    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);

    env->DeleteGlobalRef(nodeClass);
    env->DeleteGlobalRef(queryMatchClass);
    env->DeleteGlobalRef(queryCaptureClass);
}

jobject marshalNode(JNIEnv *env, TSNode node) {
    jobject obj = env->AllocObject(nodeClass);
    TSPoint end_point = ts_node_end_point(node);

    env->SetLongField(obj, nodeIdField, (jlong) node.id);
    env->SetLongField(obj, nodeTreePtrField, (jlong) node.tree);
    env->SetIntField(obj, nodeContext0Field, node.context[0]);
    env->SetIntField(obj, nodeContext1Field, node.context[1]);
    env->SetIntField(obj, nodeContext2Field, node.context[2]);
    env->SetIntField(obj, nodeContext3Field, node.context[3]);
    env->SetIntField(obj, nodeEndByte, ts_node_end_byte(node));
    env->SetIntField(obj, nodeEndRow, end_point.row);
    env->SetIntField(obj, nodeEndColumn, end_point.column);
    return obj;
}

TSNode unmarshalNode(jlong id, jlong tree_ptr,
                     jint context0, jint context1, jint context2,
                     jint context3) {
    return (TSNode) {
            {
                    static_cast<uint32_t>(context0),
                    static_cast<uint32_t>(context1),
                    static_cast<uint32_t>(context2),
                    static_cast<uint32_t>(context3)
            },
            reinterpret_cast<const void *>(id),
            reinterpret_cast<const TSTree *>(tree_ptr)
    };
}

jobject marshalQueryCapture(JNIEnv *env, TSQuery *query, TSQueryCapture capture) {
    jobject obj = env->AllocObject(queryCaptureClass);
    uint32_t length = 0;
    env->SetObjectField(obj, queryCaptureName, env->NewStringUTF(
            ts_query_capture_name_for_id(query, capture.index, &length)));
    env->SetObjectField(obj, queryCaptureNode, marshalNode(env, capture.node));
    return obj;
}

jobject marshalQueryMatch(JNIEnv *env, TSQuery *query, TSQueryMatch match) {
    jobject obj = env->AllocObject(queryMatchClass);
    jobjectArray captures = env->NewObjectArray(match.capture_count, queryCaptureClass, nullptr);
    for (int i = 0; i < match.capture_count; ++i) {
        env->SetObjectArrayElement(captures, i, marshalQueryCapture(env, query, match.captures[i]));
    }
    env->SetIntField(obj, queryMatchId, match.id);
    env->SetIntField(obj, queryMatchPatternIndex, match.pattern_index);
    env->SetObjectField(obj, queryMatchCaptures, captures);
    return obj;
}

/*************************/
/* Section - TSLangauges */
/*************************/

extern "C"
TSLanguage *tree_sitter_java();

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TSLanguages_java(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(tree_sitter_java());
}


/**********************/
/* Section - TSParser */
/**********************/

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_parserNew(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(ts_parser_new());
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_parserDelete(JNIEnv *env, jclass clazz, jlong parser_ptr) {
    ts_parser_delete(reinterpret_cast<TSParser *>(parser_ptr));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_parserSetLanguage(JNIEnv *env, jclass clazz, jlong parser_ptr,
                                                   jlong language_ptr) {
    return ts_parser_set_language(reinterpret_cast<TSParser *>(parser_ptr),
                                  reinterpret_cast<const TSLanguage *>(language_ptr));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_parserParseString(JNIEnv *env, jclass clazz, jlong parser_ptr,
                                                   jlong old_tree_ptr, jstring source,
                                                   jint length) {
    const char *source_chars = env->GetStringUTFChars(source, nullptr);
    TSTree *tree_ptr = ts_parser_parse_string(reinterpret_cast<TSParser *>(parser_ptr),
                                              reinterpret_cast<const TSTree *>(old_tree_ptr),
                                              source_chars, length);
    env->ReleaseStringUTFChars(source, source_chars);
    return reinterpret_cast<jlong>(tree_ptr);
}

/********************/
/* Section - TSTree */
/********************/

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCopy(JNIEnv *env, jclass clazz, jlong tree_ptr) {
    return reinterpret_cast<jlong>(ts_tree_copy(reinterpret_cast<const TSTree *>(tree_ptr)));
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeDelete(JNIEnv *env, jclass clazz, jlong tree_ptr) {
    ts_tree_delete(reinterpret_cast<TSTree *>(tree_ptr));
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeRootNode(JNIEnv *env, jclass clazz, jlong tree_ptr) {
    return marshalNode(env, ts_tree_root_node(reinterpret_cast<const TSTree *>(tree_ptr)));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeLanguage(JNIEnv *env, jclass clazz, jlong tree_ptr) {
    return (jlong) ts_tree_language(reinterpret_cast<const TSTree *>(tree_ptr));
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeEdit(JNIEnv *env, jclass clazz, jlong tree_ptr,
                                          jint start_byte, jint old_end_byte, jint new_end_byte,
                                          jint start_row, jint start_column, jint old_end_row,
                                          jint old_end_column, jint new_end_row,
                                          jint new_end_column) {
    TSInputEdit input_edit = (TSInputEdit) {
            static_cast<uint32_t>(start_byte),
            static_cast<uint32_t>(old_end_byte),
            static_cast<uint32_t>(new_end_byte),
            (TSPoint) {
                    static_cast<uint32_t>(start_row),
                    static_cast<uint32_t>(start_column)
            },
            (TSPoint) {
                    static_cast<uint32_t>(old_end_row),
                    static_cast<uint32_t>(old_end_column)
            },
            (TSPoint) {
                    static_cast<uint32_t>(new_end_row),
                    static_cast<uint32_t>(new_end_column)
            }
    };
    ts_tree_edit(reinterpret_cast<TSTree *>(tree_ptr), &input_edit);
}


/********************/
/* Section - TSNode */
/********************/

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeParant(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                            jint context0, jint context1, jint context2,
                                            jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    TSNode parent = ts_node_parent(node);
    if (ts_node_is_null(parent)) return nullptr;
    return marshalNode(env, parent);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeChild(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                           jint context0, jint context1, jint context2,
                                           jint context3, jint index) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    TSNode child = ts_node_child(node, index);
    if (ts_node_is_null(child)) return nullptr;
    return marshalNode(env, child);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeChildCount(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                                jint context0, jint context1, jint context2,
                                                jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_child_count(node);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeNamedChild(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                                jint context0, jint context1, jint context2,
                                                jint context3, jint index) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    TSNode child = ts_node_named_child(node, index);
    if (ts_node_is_null(child)) return nullptr;
    return marshalNode(env, child);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeNamedChildCount(JNIEnv *env, jclass clazz, jlong id,
                                                     jlong tree_ptr, jint context0, jint context1,
                                                     jint context2, jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_named_child_count(node);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeType(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                          jint context0, jint context1, jint context2,
                                          jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    const char* type = ts_node_type(node);
    return env->NewStringUTF(type);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeSymbol(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                          jint context0, jint context1, jint context2,
                                          jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_symbol(node);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeIsNamed(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                             jint context0, jint context1, jint context2,
                                             jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_is_named(node);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeIsMissing(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                               jint context0, jint context1, jint context2,
                                               jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_is_missing(node);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeHasError(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                              jint context0, jint context1, jint context2,
                                              jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    return ts_node_has_error(node);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeString(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                            jint context0, jint context1, jint context2,
                                            jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    char* string = ts_node_string(node);
    jstring res = env->NewStringUTF(string);
    free(string);
    return res;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeDescendantForRange(JNIEnv *env, jclass clazz, jlong id,
                                                        jlong tree_ptr, jint context0, jint context1,
                                                        jint context2, jint context3, jint start_row,
                                                        jint start_column, jint end_row,
                                                        jint end_column) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    TSPoint start = (TSPoint) {static_cast<uint32_t>(start_row), static_cast<uint32_t>(start_column)};
    TSPoint end = (TSPoint) {static_cast<uint32_t>(end_row), static_cast<uint32_t>(end_column)};
    TSNode root = ts_node_descendant_for_point_range(node, start, end);
    if (ts_node_is_null(root)) return nullptr;
    return marshalNode(env, root);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_nodeNamedDescendantForRange(JNIEnv *env, jclass clazz, jlong id,
                                                        jlong tree_ptr, jint context0, jint context1,
                                                        jint context2, jint context3, jint start_row,
                                                        jint start_column, jint end_row,
                                                        jint end_column) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    TSPoint start = (TSPoint) {static_cast<uint32_t>(start_row), static_cast<uint32_t>(start_column)};
    TSPoint end = (TSPoint) {static_cast<uint32_t>(end_row), static_cast<uint32_t>(end_column)};
    TSNode root = ts_node_named_descendant_for_point_range(node, start, end);
    if (ts_node_is_null(root)) return nullptr;
    return marshalNode(env, root);
}


/************************/
/* Section - TreeCursor */
/************************/

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorNew(JNIEnv *env, jclass clazz, jlong id, jlong tree_ptr,
                                               jint context0, jint context1, jint context2,
                                               jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    auto *cursor = new TSTreeCursor(ts_tree_cursor_new(node));
    return reinterpret_cast<jlong>(cursor);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorDelete(JNIEnv *env, jclass clazz,
                                                  jlong tree_cursor_ptr) {
    ts_tree_cursor_delete(reinterpret_cast<TSTreeCursor *>(tree_cursor_ptr));
    delete reinterpret_cast<TSTreeCursor *>(tree_cursor_ptr);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorCurrentNode(JNIEnv *env, jclass clazz,
                                                       jlong tree_cursor_ptr) {
    return marshalNode(env, ts_tree_cursor_current_node(
            reinterpret_cast<const TSTreeCursor *>(tree_cursor_ptr)));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorGotoParent(JNIEnv *env, jclass clazz,
                                                      jlong tree_cursor_ptr) {
    return ts_tree_cursor_goto_parent(reinterpret_cast<TSTreeCursor *>(tree_cursor_ptr));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorGotoNextSibling(JNIEnv *env, jclass clazz,
                                                           jlong tree_cursor_ptr) {
    return ts_tree_cursor_goto_next_sibling(reinterpret_cast<TSTreeCursor *>(tree_cursor_ptr));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_ikws4_jsitter_TreeSitter_treeCursorGotoFirstChild(JNIEnv *env, jclass clazz,
                                                          jlong tree_cursor_ptr) {
    return ts_tree_cursor_goto_first_child(reinterpret_cast<TSTreeCursor *>(tree_cursor_ptr));
}

/*******************/
/* Section - Query */
/*******************/

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryNew(JNIEnv *env, jclass clazz, jlong language_ptr,
                                          jstring source, jint length) {
    uint32_t error_offset;
    TSQueryError error_type;
    const char *source_chars = env->GetStringUTFChars(source, nullptr);
    TSQuery *query_ptr = ts_query_new(reinterpret_cast<const TSLanguage *>(language_ptr),
                                      source_chars,
                                      length, &error_offset, &error_type);
    env->ReleaseStringUTFChars(source, source_chars);
    if (query_ptr == nullptr) {
        LOGE("queryNew nullptr error: error_offset at %d, error_type is %d", error_offset,
             error_type)
    }
    return reinterpret_cast<jlong>(query_ptr);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryDelete(JNIEnv *env, jclass clazz, jlong query_ptr) {
    ts_query_delete(reinterpret_cast<TSQuery *>(query_ptr));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorNew(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(ts_query_cursor_new());
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorDelete(JNIEnv *env, jclass clazz,
                                                   jlong query_cursor_ptr) {
    ts_query_cursor_delete(reinterpret_cast<TSQueryCursor *>(query_cursor_ptr));
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorExec(JNIEnv *env, jclass clazz, jlong query_cursor_ptr,
                                                 jlong query_ptr,
                                                 jlong id, jlong tree_ptr, jint context0,
                                                 jint context1, jint context2, jint context3) {
    TSNode node = unmarshalNode(id, tree_ptr, context0, context1, context2, context3);
    ts_query_cursor_exec(reinterpret_cast<TSQueryCursor *>(query_cursor_ptr),
                         reinterpret_cast<const TSQuery *>(query_ptr), node);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorSetByteRange(JNIEnv *env, jclass clazz,
                                                         jlong query_cursor_ptr, jint start_byte,
                                                         jint end_byte) {
    ts_query_cursor_set_byte_range(reinterpret_cast<TSQueryCursor *>(query_cursor_ptr), start_byte,
                                   end_byte);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorSetPointRange(JNIEnv *env, jclass clazz,
                                                          jlong query_cursor_ptr, jint start_row,
                                                          jint start_column, jint end_row,
                                                          jint end_column) {
    TSPoint start_point = (TSPoint) {
            static_cast<uint32_t>(start_row),
            static_cast<uint32_t>(start_column)
    };

    TSPoint end_point = (TSPoint) {
            static_cast<uint32_t>(end_row),
            static_cast<uint32_t>(end_column)
    };

    ts_query_cursor_set_point_range(reinterpret_cast<TSQueryCursor *>(query_cursor_ptr),
                                    start_point, end_point);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_ikws4_jsitter_TreeSitter_queryCursorNextMatch(JNIEnv *env, jclass clazz,
                                                      jlong query_cursor_ptr, jlong query_ptr) {
    TSQueryMatch match;
    bool is_matched = ts_query_cursor_next_match(
            reinterpret_cast<TSQueryCursor *>(query_cursor_ptr), &match);
    if (!is_matched) return nullptr;
    return marshalQueryMatch(env, reinterpret_cast<TSQuery *>(query_ptr), match);
}
package io.ikws4.weiju.xposed.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.io.IOException;
import java.io.StringWriter;

public class json extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable json = new LuaTable();
        json.set("parse", new _parse());
        json.set("stringify", new _stringify());
        return json;
    }

    private class _parse extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                return element(JsonParser.parseString(arg.checkjstring()));
            } catch (JsonSyntaxException e) {
                throw new LuaError(e);
            }
        }

        private LuaValue element(JsonElement element) {
            if (element.isJsonObject()) {
                return object(element.getAsJsonObject());
            } else if (element.isJsonArray()) {
                return array(element.getAsJsonArray());
            } else if (element.isJsonPrimitive()) {
                return primitive(element.getAsJsonPrimitive());
            } else {
                return NIL;
            }
        }

        private LuaTable object(JsonObject object) {
            LuaTable table = tableOf();
            for (var entry : object.entrySet()) {
                table.rawset(entry.getKey(), element(entry.getValue()));
            }
            return table;
        }

        private LuaTable array(JsonArray array) {
            LuaTable table = tableOf();
            for (int i = 0; i < array.size(); i++) {
                table.rawset(i + 1, element(array.get(i)));
            }
            return table;
        }

        private LuaValue primitive(JsonPrimitive primitive) {
            if (primitive.isBoolean()) {
                return valueOf(primitive.getAsBoolean());
            } else if (primitive.isNumber()) {
                return valueOf(primitive.getAsDouble());
            } else if (primitive.isString()) {
                return valueOf(primitive.getAsString());
            } else {
                return NIL;
            }
        }
    }

    private class _stringify extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String json;
            try {
                StringWriter stringWriter = new StringWriter();
                JsonWriter jsonWriter = new JsonWriter(stringWriter);
                jsonWriter.setLenient(true);
                jsonWriter.setIndent("  ");
                jsonWriter.setSerializeNulls(true);
                Streams.write(value(arg), jsonWriter);
                json  = stringWriter.toString();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
            return valueOf(json);
        }

        private JsonElement value(LuaValue value) {
            if (value.isstring()) {
                return new JsonPrimitive(value.checkjstring());
            } else if (value.isnumber()) {
                return new JsonPrimitive(value.checkdouble());
            } else if (value.isboolean()) {
                return new JsonPrimitive(value.checkboolean());
            } else if (value.istable()) {
                return table(value.checktable());
            } else {
                return JsonNull.INSTANCE;
            }
        }

        private JsonElement table(LuaTable table) {
            LuaValue[] keys = table.keys();
            boolean isArray = true;
            for (int i = 0; i < keys.length; i++) {
                if (!keys[i].islong() || keys[i].checklong() != (i + 1)) {
                    isArray = false;
                    break;
                }
            }

            if (isArray) {
                JsonArray array = new JsonArray();
                for (LuaValue key : keys) {
                    array.add(value(table.get(key)));
                }
                return array;
            } else {
                JsonObject object = new JsonObject();
                for (LuaValue key : keys) {
                    object.add(key.checkjstring(), value(table.get(key)));
                }
                return object;
            }
        }
    }
}

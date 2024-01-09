package io.github.marchliu.intheshell.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jaskell.util.Try;

import java.util.List;
import java.util.Map;

public class JsonUtils {
    final
    ObjectMapper mapper;

    final
    TypeReference<Map<String, Object>> mapReference;

    final
    TypeReference<List<Object>> listReference;

    public JsonUtils() {
        this.mapper = new ObjectMapper();
        this.mapReference = new TypeReference<Map<String, Object>>() {
        };
        this.listReference = new TypeReference<List<Object>>() {
        };
    }

    public <T> Try<T> parseJson(String content, TypeReference<T> reference) {
        try {
            return Try.success(mapper.readValue(content, reference));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public <T> Try<T> parseJson(String content, Class<T> clazz) {
        try {
            return Try.success(mapper.readValue(content, clazz));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public Try<Map<String, Object>> toMap(String content) {
        try {
            return Try.success(mapper.readValue(content, mapReference));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public Try<JsonNode> toNode(String content) {
        try {
            return Try.success(mapper.readValue(content, JsonNode.class));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public Try<Map<String, Object>> objToMap(Object obj) {
        return writeToString(obj).flatMap(this::toMap);
    }

    public <T> Try<T> mapToObj(Map<String, Object> map, Class<T> clazz) {
        return writeToString(map).flatMap(str -> parseJson(str, clazz));
    }


    public Try<List<Object>> toList(String content) {
        try {
            return Try.success(mapper.readValue(content, listReference));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public Try<String> writeToString(Object obj) {
        try {
            return Try.success(mapper.writeValueAsString(obj));
        } catch (Exception err) {
            return Try.failure(err);
        }
    }

    public Try<JsonNode> readByJsonPath(String content, String jpath) {
        return Try.tryIt(() -> {
            var tree = mapper.readTree(content);
            return tree.at(jpath);
        });
    }

    public Try<JsonNode> byJsonPath(Object object, String jpath) {
        return writeToString(object)
                .flatMap(content -> readByJsonPath(content, jpath));
    }

    public ObjectNode createObject() {
        return mapper.createObjectNode();
    }

    public ArrayNode createArray() {
        return mapper.createArrayNode();
    }
}

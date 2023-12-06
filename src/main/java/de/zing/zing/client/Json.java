package de.zing.zing.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Json {

    public static <T> T getFromFile(String module, String variable, Class<T> valueType) {
        return deserialize(ZingClient.MOD_NAME + "/config.json", new String[]{module, variable}, valueType);
    }

    public static <T> void writeToFile(String module, String variable, T data) {
        createDirIfNotExists(ZingClient.MOD_NAME);
        serialize(ZingClient.MOD_NAME + "/config.json", new String[]{module, variable}, data);
    }

    protected static void createDirIfNotExists(String dir) {
        File directory = new File(dir);
        if (!directory.exists()) {
            boolean ignored = directory.mkdirs();
        }
    }

    public static <T> T deserialize(String filePath, String[] path, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return getEmptyObject(valueType);
            }

            JsonNode currentNode = objectMapper.readTree(file);

            for (String field : path) {
                if (currentNode.has(field)) {
                    currentNode = currentNode.get(field);
                } else {
                    return getEmptyObject(valueType);
                }
            }

            T v = objectMapper.treeToValue(currentNode, valueType);
            if (v == null) {
                return getEmptyObject(valueType);
            } else {
                return v;
            }

        } catch (IOException e) {
            ZingClient.LOGGER.error(e.toString());

            return getEmptyObject(valueType);
        }
    }

    private static <T> T getEmptyObject(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            //TODO: boolean...
            ZingClient.LOGGER.error(e.toString());
            return null;
        }
    }

    public static <T> void serialize(String filePath, String[] path, T data) {
        ArrayList<String> fieldPath = Lists.newArrayList(Arrays.stream(path).iterator());
        String last = fieldPath.get(fieldPath.size() - 1);
        fieldPath.remove(fieldPath.size() - 1);
        try {
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode rootNode;

            File file = new File(filePath);
            if (file.exists() && file.length() > 0) {
                rootNode = objectMapper.readTree(file);
            } else {
                rootNode = objectMapper.createObjectNode();
            }

            JsonNode currentNode = rootNode;
            for (String field : fieldPath) {
                if (!currentNode.has(field)) {
                    ((ObjectNode) currentNode).set(field, objectMapper.createObjectNode());
                }
                currentNode = currentNode.get(field);
            }

            if (!currentNode.isTextual()) {
                ((ObjectNode) currentNode).set(last, objectMapper.valueToTree(data));
            }
            objectMapper.writeValue(file, rootNode);


        } catch (IOException e) {
            ZingClient.LOGGER.error(e.toString());
        }
    }
}
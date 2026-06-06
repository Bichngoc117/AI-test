package com.framework.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * JsonDataReader — Đọc test data từ JSON files cho data-driven tests.
 *
 * <p>Hỗ trợ đọc từ:
 * <ul>
 *   <li>File path tuyệt đối</li>
 *   <li>Classpath resource (src/test/resources)</li>
 * </ul>
 * </p>
 */
public class JsonDataReader {

    private static final Logger log = LogManager.getLogger(JsonDataReader.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private JsonDataReader() {
        // Utility class
    }

    /**
     * Đọc JSON file thành object của type T.
     *
     * @param filePath đường dẫn file JSON (absolute hoặc relative từ project root)
     * @param clazz    class type cần map
     * @param <T>      generic type
     * @return object đã deserialize
     */
    public static <T> T readObject(String filePath, Class<T> clazz) {
        File file = resolveFile(filePath);
        try {
            T result = mapper.readValue(file, clazz);
            log.debug("Read JSON object [{}] from: {}", clazz.getSimpleName(), filePath);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file [" + filePath + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Đọc JSON array thành List<T>.
     *
     * @param filePath đường dẫn file JSON
     * @param clazz    element class type
     * @param <T>      generic type
     * @return List các objects
     */
    public static <T> List<T> readList(String filePath, Class<T> clazz) {
        File file = resolveFile(filePath);
        try {
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            List<T> result = mapper.readValue(file, listType);
            log.debug("Read JSON list of [{}] ({} items) from: {}", clazz.getSimpleName(), result.size(), filePath);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON list from [" + filePath + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Đọc JSON từ classpath resource.
     *
     * @param resourcePath path trong classpath (e.g., "test-data/users.json")
     * @param clazz        class type
     * @param <T>          generic type
     * @return object đã deserialize
     */
    public static <T> T readFromClasspath(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Classpath resource not found: " + resourcePath);
            }
            T result = mapper.readValue(is, clazz);
            log.debug("Read JSON [{}] from classpath: {}", clazz.getSimpleName(), resourcePath);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read classpath resource [" + resourcePath + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Đọc JSON array từ classpath resource thành List<T>.
     */
    public static <T> List<T> readListFromClasspath(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Classpath resource not found: " + resourcePath);
            }
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            List<T> result = mapper.readValue(is, listType);
            log.debug("Read JSON list of [{}] ({} items) from classpath: {}", clazz.getSimpleName(), result.size(), resourcePath);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read classpath list [" + resourcePath + "]: " + e.getMessage(), e);
        }
    }

    private static File resolveFile(String filePath) {
        File file = new File(filePath);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), filePath);
        }
        if (!file.exists()) {
            throw new RuntimeException("JSON file not found: " + file.getAbsolutePath());
        }
        return file;
    }
}

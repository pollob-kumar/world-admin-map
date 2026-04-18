package io.github.pollob_kumar.worldadmin.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pollob_kumar.worldadmin.exception.GeoDataLoadException;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class JsonDataLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<AdminLevel1> loadLevel1(String resourcePath) {
        return loadList(resourcePath, new TypeReference<List<AdminLevel1>>() {});
    }

    public List<AdminLevel2> loadLevel2(String resourcePath) {
        return loadList(resourcePath, new TypeReference<List<AdminLevel2>>() {});
    }

    public List<AdminLevel3> loadLevel3(String resourcePath) {
        return loadList(resourcePath, new TypeReference<List<AdminLevel3>>() {});
    }

    private <T> List<T> loadList(String resourcePath, TypeReference<List<T>> type) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("Resource path must not be null or blank.");
        }

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new GeoDataLoadException("Resource not found: " + resourcePath);
            }
            List<T> data = MAPPER.readValue(inputStream, type);
            return Collections.unmodifiableList(data);
        } catch (IOException ex) {
            throw new GeoDataLoadException("Failed to load resource: " + resourcePath, ex);
        }
    }
}

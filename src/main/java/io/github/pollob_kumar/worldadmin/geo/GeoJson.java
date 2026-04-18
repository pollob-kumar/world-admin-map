package io.github.pollob_kumar.worldadmin.geo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pollob_kumar.worldadmin.enums.AdminLevel;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GeoJson {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toFeature(AdminUnit unit) {
        Objects.requireNonNull(unit, "unit must not be null.");
        return writeJson(buildFeature(unit));
    }

    public static String toFeatureCollection(List<? extends AdminUnit> units) {
        Objects.requireNonNull(units, "units must not be null.");
        Map<String, Object> collection = new LinkedHashMap<>();
        collection.put("type", "FeatureCollection");
        List<Map<String, Object>> features = new ArrayList<>();
        for (AdminUnit unit : units) {
            features.add(buildFeature(unit));
        }
        collection.put("features", features);
        return writeJson(collection);
    }

    private static Map<String, Object> buildFeature(AdminUnit unit) {
        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", List.of(unit.getLon(), unit.getLat()));
        feature.put("geometry", geometry);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", unit.getId());
        properties.put("name", unit.getName());
        properties.put("level", resolveLevel(unit));
        if (unit instanceof AdminLevel2) {
            properties.put("parentId", ((AdminLevel2) unit).getParentId());
        } else if (unit instanceof AdminLevel3) {
            properties.put("parentId", ((AdminLevel3) unit).getParentId());
        }
        feature.put("properties", properties);
        return feature;
    }

    private static AdminLevel resolveLevel(AdminUnit unit) {
        if (unit instanceof AdminLevel1) {
            return AdminLevel.LEVEL_1;
        }
        if (unit instanceof AdminLevel2) {
            return AdminLevel.LEVEL_2;
        }
        return AdminLevel.LEVEL_3;
    }

    private static String writeJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize GeoJSON.", ex);
        }
    }
}

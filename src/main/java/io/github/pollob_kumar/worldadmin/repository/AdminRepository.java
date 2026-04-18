package io.github.pollob_kumar.worldadmin.repository;

import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AdminRepository {
    private final List<AdminLevel1> level1List;
    private final Map<String, AdminLevel1> level1ById;
    private final Map<String, AdminLevel2> level2ById;
    private final Map<String, AdminLevel3> level3ById;
    private final Map<String, List<AdminLevel2>> level2ByParent;
    private final Map<String, List<AdminLevel3>> level3ByParent;

    public AdminRepository(
            List<AdminLevel1> level1List,
            List<AdminLevel2> level2List,
            List<AdminLevel3> level3List
    ) {
        Objects.requireNonNull(level1List, "level1List must not be null.");
        Objects.requireNonNull(level2List, "level2List must not be null.");
        Objects.requireNonNull(level3List, "level3List must not be null.");

        this.level1List = List.copyOf(level1List);
        this.level1ById = Collections.unmodifiableMap(buildLevel1ById(level1List));
        this.level2ById = Collections.unmodifiableMap(buildLevel2ById(level2List));
        this.level3ById = Collections.unmodifiableMap(buildLevel3ById(level3List));
        this.level2ByParent = Collections.unmodifiableMap(buildLevel2ByParent(level2List));
        this.level3ByParent = Collections.unmodifiableMap(buildLevel3ByParent(level3List));
    }

    public List<AdminLevel1> getAllLevel1() {
        return level1List;
    }

    public Optional<AdminLevel1> findLevel1ById(String id) {
        return Optional.ofNullable(level1ById.get(id));
    }

    public List<AdminLevel2> findLevel2ByParent(String level1Id) {
        return level2ByParent.getOrDefault(level1Id, Collections.emptyList());
    }

    public Optional<AdminLevel2> findLevel2ById(String id) {
        return Optional.ofNullable(level2ById.get(id));
    }

    public List<AdminLevel3> findLevel3ByParent(String level2Id) {
        return level3ByParent.getOrDefault(level2Id, Collections.emptyList());
    }

    public Optional<AdminLevel3> findLevel3ById(String id) {
        return Optional.ofNullable(level3ById.get(id));
    }

    public Optional<AdminUnit> searchByName(String name) {
        String normalized = normalize(name);
        for (AdminLevel1 unit : level1List) {
            if (matches(normalized, unit.getName())) {
                return Optional.of(unit);
            }
        }
        for (AdminLevel2 unit : level2ById.values()) {
            if (matches(normalized, unit.getName())) {
                return Optional.of(unit);
            }
        }
        for (AdminLevel3 unit : level3ById.values()) {
            if (matches(normalized, unit.getName())) {
                return Optional.of(unit);
            }
        }
        return Optional.empty();
    }

    public List<AdminUnit> searchAll(String name) {
        String normalized = normalize(name);
        List<AdminUnit> results = new ArrayList<>();
        for (AdminLevel1 unit : level1List) {
            if (matches(normalized, unit.getName())) {
                results.add(unit);
            }
        }
        for (AdminLevel2 unit : level2ById.values()) {
            if (matches(normalized, unit.getName())) {
                results.add(unit);
            }
        }
        for (AdminLevel3 unit : level3ById.values()) {
            if (matches(normalized, unit.getName())) {
                results.add(unit);
            }
        }
        return List.copyOf(results);
    }

    private static Map<String, AdminLevel1> buildLevel1ById(List<AdminLevel1> list) {
        Map<String, AdminLevel1> map = new HashMap<>();
        for (AdminLevel1 unit : list) {
            map.put(unit.getId(), unit);
        }
        return map;
    }

    private static Map<String, AdminLevel2> buildLevel2ById(List<AdminLevel2> list) {
        Map<String, AdminLevel2> map = new HashMap<>();
        for (AdminLevel2 unit : list) {
            map.put(unit.getId(), unit);
        }
        return map;
    }

    private static Map<String, AdminLevel3> buildLevel3ById(List<AdminLevel3> list) {
        Map<String, AdminLevel3> map = new HashMap<>();
        for (AdminLevel3 unit : list) {
            map.put(unit.getId(), unit);
        }
        return map;
    }

    private static Map<String, List<AdminLevel2>> buildLevel2ByParent(List<AdminLevel2> list) {
        Map<String, List<AdminLevel2>> grouped = new HashMap<>();
        for (AdminLevel2 unit : list) {
            grouped.computeIfAbsent(unit.getParentId(), key -> new ArrayList<>()).add(unit);
        }

        Map<String, List<AdminLevel2>> result = new HashMap<>();
        for (Map.Entry<String, List<AdminLevel2>> entry : grouped.entrySet()) {
            result.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return result;
    }

    private static Map<String, List<AdminLevel3>> buildLevel3ByParent(List<AdminLevel3> list) {
        Map<String, List<AdminLevel3>> grouped = new HashMap<>();
        for (AdminLevel3 unit : list) {
            grouped.computeIfAbsent(unit.getParentId(), key -> new ArrayList<>()).add(unit);
        }

        Map<String, List<AdminLevel3>> result = new HashMap<>();
        for (Map.Entry<String, List<AdminLevel3>> entry : grouped.entrySet()) {
            result.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return result;
    }

    private static boolean matches(String normalizedName, String candidate) {
        if (normalizedName == null || candidate == null) {
            return false;
        }
        return candidate.toLowerCase(Locale.ROOT).equals(normalizedName);
    }

    private static String normalize(String name) {
        if (name == null) {
            return null;
        }
        return name.trim().toLowerCase(Locale.ROOT);
    }
}

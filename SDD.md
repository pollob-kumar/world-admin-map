# Software Design Document (SDD)

---
**Project Name:** World Admin Map  
**Version:** 1.0.0  
**Date:** 2026-04-14  
**Prepared by:** Pollob Kumar  
**Based on SRS Version:** 1.1.1

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Architecture Overview](#2-system-architecture-overview)
3. [Layer Responsibilities](#3-layer-responsibilities)
4. [Class-Level Design](#4-class-level-design)
   - 4.1 [Model Layer](#41-model-layer)
   - 4.2 [Enum Layer](#42-enum-layer)
   - 4.3 [Repository Layer](#43-repository-layer)
   - 4.4 [Country Loader Layer (Strategy Pattern)](#44-country-loader-layer-strategy-pattern)
   - 4.5 [Factory Layer](#45-factory-layer)
   - 4.6 [Service Layer](#46-service-layer)
   - 4.7 [Facade Layer](#47-facade-layer)
   - 4.8 [Geo Utility Layer](#48-geo-utility-layer)
5. [Interface Contracts](#5-interface-contracts)
6. [JSON Schema Definition](#6-json-schema-definition)
7. [Data Flow Diagrams](#7-data-flow-diagrams)
8. [Error Handling Strategy](#8-error-handling-strategy)
9. [Thread Safety Design](#9-thread-safety-design)
10. [Caching and Initialization Strategy](#10-caching-and-initialization-strategy)
11. [Design Patterns Summary](#11-design-patterns-summary)
12. [Dependency Graph](#12-dependency-graph)
13. [Revision History](#13-revision-history)

---

## 1. Introduction

### 1.1 Purpose

This Software Design Document (SDD) describes the internal design and implementation strategy of the `world-admin-map` Java library. While the SRS defines *what* the system must do, this document defines *how* it will be done — covering architecture, class design, interface contracts, data flow, error handling, and thread safety.

### 1.2 Scope

This document covers the complete design of version `1.0.0` of the library, which includes full support for Bangladesh (BD) and stub support for India (IN) and USA (US).

### 1.3 Intended Audience

- Library developer (Pollob Kumar)
- Future contributors
- AI coding assistants used during development

### 1.4 Key Design Goals

| Goal | Strategy |
|------|----------|
| Zero external runtime dependency | Pure Java + bundled JSON resources |
| Country-agnostic extensibility | Strategy Pattern via `CountryLoader` interface |
| Fast lookup | `HashMap` indexing at load time |
| Thread safety | `ConcurrentHashMap` + lazy singleton initialization |
| Clean public API | Facade Pattern via `GeoAdmin.java` |

---

## 2. System Architecture Overview

The library is organized into 7 distinct layers. Each layer has a single responsibility and communicates only with its adjacent layers.

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT / DEVELOPER                   │
└───────────────────────────┬─────────────────────────────┘
                            │ calls
┌───────────────────────────▼─────────────────────────────┐
│              FACADE LAYER  (GeoAdmin.java)               │
│         Single entry point. Delegates to Service.        │
└───────────────────────────┬─────────────────────────────┘
                            │ delegates to
┌───────────────────────────▼─────────────────────────────┐
│             SERVICE LAYER  (GeoService.java)             │
│       Business logic. Queries AdminRepository.           │
└───────────────────────────┬─────────────────────────────┘
                            │ uses
┌───────────────────────────▼─────────────────────────────┐
│          REPOSITORY LAYER  (AdminRepository.java)        │
│     In-memory store. Populated by JsonDataLoader.        │
└────────────────┬──────────────────────────┬─────────────┘
                 │ loaded by                │ uses
┌────────────────▼────────────┐  ┌──────────▼──────────────┐
│  COUNTRY LOADER LAYER       │  │  JSON DATA LOADER        │
│  (BangladeshLoader, etc.)   │  │  (JsonDataLoader.java)   │
│  Strategy Pattern           │  │  Reads classpath JSON    │
└────────────────┬────────────┘  └─────────────────────────┘
                 │ created by
┌────────────────▼────────────────────────────────────────┐
│             FACTORY LAYER  (CountryFactory.java)         │
│     Returns correct CountryLoader for a CountryCode.     │
└─────────────────────────────────────────────────────────┘

 SUPPORTING LAYERS (used across the stack):
 ┌─────────────────────┐  ┌──────────────────────────────┐
 │  MODEL LAYER        │  │  GEO UTILITY LAYER           │
 │  AdminLevel1/2/3    │  │  GeoJson.java, BoundingBox   │
 └─────────────────────┘  └──────────────────────────────┘
```

---

## 3. Layer Responsibilities

| Layer | Class(es) | Responsibility |
|-------|-----------|----------------|
| Facade | `GeoAdmin` | Public API. All external calls go through here. |
| Service | `GeoService` | Applies business rules. Validates input, searches, filters. |
| Repository | `AdminRepository`, `JsonDataLoader` | Loads and holds all data in memory using HashMap indexes. |
| Country Loader | `CountryLoader`, `BangladeshLoader` | Per-country logic for locating and loading JSON resource files. |
| Factory | `CountryFactory` | Maps `CountryCode` → correct `CountryLoader` implementation. |
| Model | `AdminUnit`, `AdminLevel1/2/3` | Pure data classes. No logic. |
| Enum | `AdminLevel`, `CountryCode` | Type-safe constants for levels and country codes. |
| Geo Utility | `GeoJson`, `BoundingBox` | Coordinate utilities and bounding box calculation. |

---

## 4. Class-Level Design

### 4.1 Model Layer

---

#### `AdminUnit.java` (Abstract base class)

**Package:** `io.github.pollob_kumar.worldadmin.model`

**Purpose:** Shared fields and behaviour for all admin levels.

```java
public abstract class AdminUnit {
    private final String id;
    private final String name;
    private final double lat;
    private final double lon;

    protected AdminUnit(String id, String name, double lat, double lon) { ... }

    public String getId()   { return id; }
    public String getName() { return name; }
    public double getLat()  { return lat; }
    public double getLon()  { return lon; }

    @Override public String toString() { ... }
    @Override public boolean equals(Object o) { ... }  // based on id
    @Override public int hashCode()    { ... }         // based on id
}
```

**Rules:**
- Immutable after construction (all fields `final`)
- `equals` and `hashCode` based on `id` only
- No setters — library is read-only

---

#### `AdminLevel1.java`

**Package:** `io.github.pollob_kumar.worldadmin.model`

**Purpose:** Represents a Division / State / Province (top-level admin unit).

```java
public final class AdminLevel1 extends AdminUnit {
    public AdminLevel1(String id, String name, double lat, double lon) {
        super(id, name, lat, lon);
    }
}
```

**Example data mapping:**

| JSON field | Java field | Example |
|------------|------------|---------|
| `id`       | `id`       | `"BD10"` |
| `name`     | `name`     | `"Barishal"` |
| `lat`      | `lat`      | `22.3928` |
| `lon`      | `lon`      | `90.4064` |

---

#### `AdminLevel2.java`

**Package:** `io.github.pollob_kumar.worldadmin.model`

**Purpose:** Represents a District / Region. Has a reference to its parent Level 1.

```java
public final class AdminLevel2 extends AdminUnit {
    private final String parentId;   // ID of parent AdminLevel1

    public AdminLevel2(String id, String name, String parentId, double lat, double lon) {
        super(id, name, lat, lon);
        this.parentId = parentId;
    }

    public String getParentId() { return parentId; }
}
```

---

#### `AdminLevel3.java`

**Package:** `io.github.pollob_kumar.worldadmin.model`

**Purpose:** Represents an Upazila / Union / City. Has a reference to its parent Level 2.

```java
public final class AdminLevel3 extends AdminUnit {
    private final String parentId;   // ID of parent AdminLevel2

    public AdminLevel3(String id, String name, String parentId, double lat, double lon) {
        super(id, name, lat, lon);
        this.parentId = parentId;
    }

    public String getParentId() { return parentId; }
}
```

---

### 4.2 Enum Layer

---

#### `CountryCode.java`

**Package:** `io.github.pollob_kumar.worldadmin.enums`

**Purpose:** Type-safe country code constants. Prevents string typos at compile time.

```java
public enum CountryCode {
    BD,   // Bangladesh
    IN,   // India (stub in v1.0.0)
    US    // United States (stub in v1.0.0)
}
```

---

#### `AdminLevel.java`

**Package:** `io.github.pollob_kumar.worldadmin.enums`

**Purpose:** Represents hierarchy levels. Used for search operations.

```java
public enum AdminLevel {
    LEVEL_1,   // Division / State
    LEVEL_2,   // District / Region
    LEVEL_3    // Union / City
}
```

---

### 4.3 Repository Layer

---

#### `JsonDataLoader.java`

**Package:** `io.github.pollob_kumar.worldadmin.repository`

**Purpose:** Reads JSON files from the classpath (`resources/data/`) and deserializes them into model objects using Jackson.

```java
public class JsonDataLoader {

    /**
     * Loads a list of AdminLevel1 objects from a classpath JSON resource.
     * @param resourcePath  e.g. "/data/bd/bd_admin1.json"
     * @return Unmodifiable list of AdminLevel1
     * @throws GeoDataLoadException if file not found or JSON is malformed
     */
    public List<AdminLevel1> loadLevel1(String resourcePath) { ... }

    /**
     * Loads a list of AdminLevel2 objects from a classpath JSON resource.
     */
    public List<AdminLevel2> loadLevel2(String resourcePath) { ... }

    /**
     * Loads a list of AdminLevel3 objects from a classpath JSON resource.
     */
    public List<AdminLevel3> loadLevel3(String resourcePath) { ... }
}
```

**Implementation notes:**
- Uses `ObjectMapper` from Jackson (`com.fasterxml.jackson.databind`)
- Resource loaded via `getClass().getResourceAsStream(resourcePath)`
- Returns `Collections.unmodifiableList(...)` to enforce immutability
- Throws custom `GeoDataLoadException` (unchecked) on failure

---

#### `AdminRepository.java`

**Package:** `io.github.pollob_kumar.worldadmin.repository`

**Purpose:** In-memory data store. Holds all loaded data in indexed `HashMap`s for O(1) lookup.

```java
public class AdminRepository {

    // Primary storage — indexed by ID
    private final Map<String, AdminLevel1> level1ById;     // key: AdminLevel1.id
    private final Map<String, AdminLevel2> level2ById;     // key: AdminLevel2.id
    private final Map<String, AdminLevel3> level3ById;     // key: AdminLevel3.id

    // Relationship indexes — for parent-child queries
    private final Map<String, List<AdminLevel2>> level2ByParent;  // key: parentId (Level1 id)
    private final Map<String, List<AdminLevel3>> level3ByParent;  // key: parentId (Level2 id)

    public AdminRepository(
        List<AdminLevel1> level1List,
        List<AdminLevel2> level2List,
        List<AdminLevel3> level3List
    ) {
        // Builds all maps at construction time
    }

    // --- Query Methods ---

    public List<AdminLevel1> getAllLevel1() { ... }

    public Optional<AdminLevel1> findLevel1ById(String id) { ... }

    public List<AdminLevel2> findLevel2ByParent(String level1Id) { ... }

    public Optional<AdminLevel2> findLevel2ById(String id) { ... }

    public List<AdminLevel3> findLevel3ByParent(String level2Id) { ... }

    public Optional<AdminLevel3> findLevel3ById(String id) { ... }

    /**
     * Case-insensitive search across all levels.
     * Returns first match found. Searches Level1 → Level2 → Level3.
     */
    public Optional<AdminUnit> searchByName(String name) { ... }
}
```

**Internal map construction (constructor logic):**
```
level1ById        = { "BD10" → AdminLevel1, "BD20" → AdminLevel1, ... }
level2ById        = { "BD1004" → AdminLevel2, ... }
level3ById        = { "BD10040009" → AdminLevel3, ... }
level2ByParent    = { "BD10" → [AdminLevel2, AdminLevel2, ...], ... }
level3ByParent    = { "BD1004" → [AdminLevel3, AdminLevel3, ...], ... }
```

---

### 4.4 Country Loader Layer (Strategy Pattern)

---

#### `CountryLoader.java` (Interface)

**Package:** `io.github.pollob_kumar.worldadmin.country`

**Purpose:** Defines the contract that every country-specific loader must fulfill. Adding a new country = implementing this interface.

```java
public interface CountryLoader {

    /**
     * Returns the CountryCode this loader handles.
     */
    CountryCode getCountryCode();

    /**
     * Loads and returns a fully populated AdminRepository for this country.
     * Called once during initialization. Result is cached by GeoService.
     *
     * @return AdminRepository populated with all 3 levels of data
     * @throws GeoDataLoadException if JSON files cannot be loaded
     */
    AdminRepository load();
}
```

---

#### `BangladeshLoader.java`

**Package:** `io.github.pollob_kumar.worldadmin.country`

**Purpose:** Bangladesh-specific implementation of `CountryLoader`. Knows the exact resource paths for BD data files.

```java
public class BangladeshLoader implements CountryLoader {

    private static final String ADMIN1_PATH = "/data/bd/bd_admin1.json";
    private static final String ADMIN2_PATH = "/data/bd/bd_admin2.json";
    private static final String ADMIN3_PATH = "/data/bd/bd_admin3.json";

    private final JsonDataLoader jsonDataLoader;

    public BangladeshLoader() {
        this.jsonDataLoader = new JsonDataLoader();
    }

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.BD;
    }

    @Override
    public AdminRepository load() {
        List<AdminLevel1> level1 = jsonDataLoader.loadLevel1(ADMIN1_PATH);
        List<AdminLevel2> level2 = jsonDataLoader.loadLevel2(ADMIN2_PATH);
        List<AdminLevel3> level3 = jsonDataLoader.loadLevel3(ADMIN3_PATH);
        return new AdminRepository(level1, level2, level3);
    }
}
```

---

#### `IndiaLoader.java` (Stub)

```java
public class IndiaLoader implements CountryLoader {

    @Override
    public CountryCode getCountryCode() { return CountryCode.IN; }

    @Override
    public AdminRepository load() {
        throw new UnsupportedOperationException(
            "India data is not yet available in this version."
        );
    }
}
```

---

#### `USALoader.java` (Stub)

```java
public class USALoader implements CountryLoader {

    @Override
    public CountryCode getCountryCode() { return CountryCode.US; }

    @Override
    public AdminRepository load() {
        throw new UnsupportedOperationException(
            "USA data is not yet available in this version."
        );
    }
}
```

---

### 4.5 Factory Layer

---

#### `CountryFactory.java`

**Package:** `io.github.pollob_kumar.worldadmin.factory`

**Purpose:** Returns the correct `CountryLoader` instance for a given `CountryCode`. Centralizes loader registration.

```java
public class CountryFactory {

    private static final Map<CountryCode, CountryLoader> LOADERS;

    static {
        Map<CountryCode, CountryLoader> map = new EnumMap<>(CountryCode.class);
        map.put(CountryCode.BD, new BangladeshLoader());
        map.put(CountryCode.IN, new IndiaLoader());
        map.put(CountryCode.US, new USALoader());
        LOADERS = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the CountryLoader for the given country code.
     *
     * @param code  CountryCode enum value
     * @return      Corresponding CountryLoader implementation
     * @throws UnsupportedCountryException if no loader is registered
     */
    public static CountryLoader getLoader(CountryCode code) {
        CountryLoader loader = LOADERS.get(code);
        if (loader == null) {
            throw new UnsupportedCountryException("No loader found for: " + code);
        }
        return loader;
    }
}
```

---

### 4.6 Service Layer

---

#### `GeoService.java`

**Package:** `io.github.pollob_kumar.worldadmin.service`

**Purpose:** Business logic layer. Manages the lifecycle of `AdminRepository` instances per country. Applies lazy initialization with caching.

```java
public class GeoService {

    // Cache: one repository per country, loaded on first access
    private final Map<CountryCode, AdminRepository> repositoryCache =
        new ConcurrentHashMap<>();

    /**
     * Gets or loads the repository for the given country.
     * Thread-safe lazy initialization via ConcurrentHashMap.computeIfAbsent().
     */
    private AdminRepository getRepository(CountryCode code) {
        return repositoryCache.computeIfAbsent(code, c ->
            CountryFactory.getLoader(c).load()
        );
    }

    // ── Level 1 ─────────────────────────────────────────────────────────────

    /**
     * Returns all Level 1 admin units (Divisions) for a country.
     */
    public List<AdminLevel1> getAllLevel1(CountryCode code) {
        return getRepository(code).getAllLevel1();
    }

    // ── Level 2 ─────────────────────────────────────────────────────────────

    /**
     * Returns all Level 2 admin units (Districts) under a given Level 1 ID.
     *
     * @param code      Country code
     * @param level1Id  ID of parent Level 1 unit
     * @return          List of matching Level 2 units, or empty list if none
     */
    public List<AdminLevel2> getLevel2(CountryCode code, String level1Id) {
        validateId(level1Id);
        return getRepository(code).findLevel2ByParent(level1Id);
    }

    // ── Level 3 ─────────────────────────────────────────────────────────────

    /**
     * Returns all Level 3 admin units (Upazilas) under a given Level 2 ID.
     */
    public List<AdminLevel3> getLevel3(CountryCode code, String level2Id) {
        validateId(level2Id);
        return getRepository(code).findLevel3ByParent(level2Id);
    }

    // ── Search ───────────────────────────────────────────────────────────────

    /**
     * Searches by name across all levels for a given country.
     * Case-insensitive. Returns first match.
     */
    public Optional<AdminUnit> searchByName(CountryCode code, String name) {
        validateName(name);
        return getRepository(code).searchByName(name);
    }

    // ── Validation ───────────────────────────────────────────────────────────

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be null or blank.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank.");
        }
    }
}
```

---

### 4.7 Facade Layer

---

#### `GeoAdmin.java`

**Package:** `io.github.pollob_kumar.worldadmin`

**Purpose:** The single public entry point for all library consumers. Wraps `GeoService` and provides a clean, simple API.

```java
public class GeoAdmin {

    private final GeoService geoService;

    // ── Singleton (optional usage pattern for consumers) ──────────────────

    private static volatile GeoAdmin instance;

    public static GeoAdmin getInstance() {
        if (instance == null) {
            synchronized (GeoAdmin.class) {
                if (instance == null) {
                    instance = new GeoAdmin();
                }
            }
        }
        return instance;
    }

    // ── Constructor ────────────────────────────────────────────────────────

    public GeoAdmin() {
        this.geoService = new GeoService();
    }

    // ── Public API Methods ─────────────────────────────────────────────────

    /**
     * Returns all Level 1 admin units (Divisions/States) for a country.
     *
     * @param code  CountryCode (e.g. CountryCode.BD)
     * @return      Unmodifiable list of AdminLevel1
     */
    public List<AdminLevel1> getAllLevel1(CountryCode code) {
        return geoService.getAllLevel1(code);
    }

    /**
     * Returns all Level 2 admin units (Districts) under a given Level 1 ID.
     *
     * @param code      CountryCode
     * @param level1Id  ID of the parent Level 1 unit (e.g. "BD10")
     * @return          List of AdminLevel2, or empty list if no match
     */
    public List<AdminLevel2> getLevel2(CountryCode code, String level1Id) {
        return geoService.getLevel2(code, level1Id);
    }

    /**
     * Returns all Level 3 admin units (Upazilas/Cities) under a given Level 2 ID.
     *
     * @param code      CountryCode
     * @param level2Id  ID of the parent Level 2 unit (e.g. "BD1004")
     * @return          List of AdminLevel3, or empty list if no match
     */
    public List<AdminLevel3> getLevel3(CountryCode code, String level2Id) {
        return geoService.getLevel3(code, level2Id);
    }

    /**
     * Searches for an admin unit by name (case-insensitive) across all levels.
     *
     * @param code  CountryCode
     * @param name  Name to search for
     * @return      Optional containing matched AdminUnit, or empty if not found
     */
    public Optional<AdminUnit> searchByName(CountryCode code, String name) {
        return geoService.searchByName(code, name);
    }
}
```

**Example usage (from SRS):**
```java
GeoAdmin geo = new GeoAdmin();

// All divisions of Bangladesh
List<AdminLevel1> divisions = geo.getAllLevel1(CountryCode.BD);

// All districts under Barishal (BD10)
List<AdminLevel2> districts = geo.getLevel2(CountryCode.BD, "BD10");

// All upazilas under Barguna (BD1004)
List<AdminLevel3> upazilas = geo.getLevel3(CountryCode.BD, "BD1004");

// Search by name
Optional<AdminUnit> result = geo.searchByName(CountryCode.BD, "Amtali");
```

---

### 4.8 Geo Utility Layer

---

#### `BoundingBox.java`

**Package:** `io.github.pollob_kumar.worldadmin.geo`

**Purpose:** Represents a rectangular geographic bounding box. Used for coordinate range checks.

```java
public class BoundingBox {
    private final double minLat;
    private final double maxLat;
    private final double minLon;
    private final double maxLon;

    public BoundingBox(double minLat, double maxLat, double minLon, double maxLon) { ... }

    /**
     * Returns true if the given point is within this bounding box.
     */
    public boolean contains(double lat, double lon) { ... }

    // Getters for all four bounds
}
```

---

#### `GeoJson.java`

**Package:** `io.github.pollob_kumar.worldadmin.geo`

**Purpose:** Utility for GeoJSON-format output. Converts model objects to GeoJSON Feature format (future use / extensibility).

```java
public class GeoJson {

    /**
     * Converts an AdminUnit to a GeoJSON Feature string.
     * Format: {"type":"Feature","geometry":{"type":"Point","coordinates":[lon,lat]},"properties":{...}}
     */
    public static String toFeature(AdminUnit unit) { ... }

    /**
     * Converts a list of AdminUnits to a GeoJSON FeatureCollection string.
     */
    public static String toFeatureCollection(List<? extends AdminUnit> units) { ... }
}
```

---

## 5. Interface Contracts

### 5.1 `CountryLoader` Interface Contract

| Method | Return Type | Throws | Notes |
|--------|-------------|--------|-------|
| `getCountryCode()` | `CountryCode` | — | Must never return null |
| `load()` | `AdminRepository` | `GeoDataLoadException` | Must return fully populated repository |

**Rules:**
- `load()` must be idempotent (safe to call multiple times)
- `load()` must not return `null`
- All data must be validated before building `AdminRepository`

### 5.2 `AdminRepository` Query Contract

| Method | Returns when found | Returns when not found |
|--------|--------------------|------------------------|
| `getAllLevel1()` | List of all Level1 | Empty list (never null) |
| `findLevel1ById(id)` | `Optional.of(unit)` | `Optional.empty()` |
| `findLevel2ByParent(id)` | List of matching Level2 | Empty list (never null) |
| `findLevel2ById(id)` | `Optional.of(unit)` | `Optional.empty()` |
| `findLevel3ByParent(id)` | List of matching Level3 | Empty list (never null) |
| `findLevel3ById(id)` | `Optional.of(unit)` | `Optional.empty()` |
| `searchByName(name)` | `Optional.of(unit)` | `Optional.empty()` |

> **Rule:** No method in the repository ever returns `null`. Lists return empty; single lookups return `Optional.empty()`.

---

## 6. JSON Schema Definition

All JSON files follow a flat array structure. Each object maps directly to a model class.

### 6.1 `bd_admin1.json` — AdminLevel1 Array

```json
[
  {
    "id": "BD10",
    "name": "Barishal",
    "lat": 22.3928,
    "lon": 90.4064
  },
  {
    "id": "BD20",
    "name": "Chattogram",
    "lat": 22.3569,
    "lon": 91.7832
  }
]
```

**Field rules:**

| Field | Type | Required | Rule |
|-------|------|----------|------|
| `id` | String | Yes | Unique globally. Format: `{CC}{2-digit-number}` e.g. `BD10` |
| `name` | String | Yes | Non-empty. Unique within same level |
| `lat` | double | Yes | Valid latitude: -90.0 to 90.0 |
| `lon` | double | Yes | Valid longitude: -180.0 to 180.0 |

---

### 6.2 `bd_admin2.json` — AdminLevel2 Array

```json
[
  {
    "id": "BD1004",
    "name": "Barguna",
    "parentId": "BD10",
    "lat": 22.1282,
    "lon": 90.1101
  }
]
```

**Additional field:**

| Field | Type | Required | Rule |
|-------|------|----------|------|
| `parentId` | String | Yes | Must reference an existing `AdminLevel1.id` |

---

### 6.3 `bd_admin3.json` — AdminLevel3 Array

```json
[
  {
    "id": "BD10040009",
    "name": "Amtali",
    "parentId": "BD1004",
    "lat": 22.1465,
    "lon": 90.2700
  }
]
```

**Additional field:**

| Field | Type | Required | Rule |
|-------|------|----------|------|
| `parentId` | String | Yes | Must reference an existing `AdminLevel2.id` |

---

### 6.4 Jackson Field Mapping

The `JsonDataLoader` uses Jackson's `@JsonProperty` annotation to map JSON fields to constructor parameters.

```java
// Example for AdminLevel2 deserialization
@JsonCreator
public AdminLevel2(
    @JsonProperty("id")       String id,
    @JsonProperty("name")     String name,
    @JsonProperty("parentId") String parentId,
    @JsonProperty("lat")      double lat,
    @JsonProperty("lon")      double lon
) { ... }
```

---

## 7. Data Flow Diagrams

### 7.1 First Call Flow (Cold Load)

When a consumer calls `geo.getLevel2(CountryCode.BD, "BD10")` for the first time:

```
Developer
   │
   ▼
GeoAdmin.getLevel2(BD, "BD10")
   │
   ▼
GeoService.getLevel2(BD, "BD10")
   │
   ├─ repositoryCache.get(BD) → NULL (first call)
   │
   ▼
CountryFactory.getLoader(BD)
   │
   ▼
BangladeshLoader.load()
   │
   ├─ JsonDataLoader.loadLevel1("/data/bd/bd_admin1.json")
   ├─ JsonDataLoader.loadLevel2("/data/bd/bd_admin2.json")
   └─ JsonDataLoader.loadLevel3("/data/bd/bd_admin3.json")
         │
         ▼
      Reads classpath resource
      Jackson parses JSON array
      Returns List<AdminLevelX>
         │
         ▼
new AdminRepository(level1List, level2List, level3List)
   │
   ├─ Builds level1ById HashMap
   ├─ Builds level2ById HashMap
   ├─ Builds level3ById HashMap
   ├─ Builds level2ByParent HashMap
   └─ Builds level3ByParent HashMap
         │
         ▼
repositoryCache.put(BD, repository)
         │
         ▼
repository.findLevel2ByParent("BD10")
   │
   └─ HashMap lookup → returns List<AdminLevel2>
         │
         ▼
GeoService → GeoAdmin → Developer
```

### 7.2 Subsequent Call Flow (Warm Cache)

```
Developer
   │
   ▼
GeoAdmin.getLevel2(BD, "BD10")
   │
   ▼
GeoService.getLevel2(BD, "BD10")
   │
   ├─ repositoryCache.get(BD) → FOUND (cached)
   │
   ▼
repository.findLevel2ByParent("BD10")
   │  O(1) HashMap lookup
   ▼
Returns List<AdminLevel2> immediately
```

---

## 8. Error Handling Strategy

### 8.1 Custom Exceptions

| Exception Class | Extends | When Thrown |
|-----------------|---------|-------------|
| `GeoDataLoadException` | `RuntimeException` | JSON file not found, unreadable, or malformed |
| `UnsupportedCountryException` | `RuntimeException` | `CountryFactory` receives an unregistered `CountryCode` |

Both are **unchecked exceptions** (extend `RuntimeException`) to keep the API clean.

```java
// GeoDataLoadException.java
public class GeoDataLoadException extends RuntimeException {
    public GeoDataLoadException(String message) { super(message); }
    public GeoDataLoadException(String message, Throwable cause) { super(message, cause); }
}

// UnsupportedCountryException.java
public class UnsupportedCountryException extends RuntimeException {
    public UnsupportedCountryException(String message) { super(message); }
}
```

### 8.2 Error Scenarios and Responses

| Scenario | Behaviour |
|----------|-----------|
| JSON resource file missing from classpath | Throw `GeoDataLoadException` with file path in message |
| JSON is malformed (invalid syntax) | Wrap Jackson exception in `GeoDataLoadException` |
| `getLevel2()` called with unknown `level1Id` | Return empty `List` (no exception) |
| `searchByName()` finds no match | Return `Optional.empty()` (no exception) |
| `null` or blank ID passed to service | Throw `IllegalArgumentException` immediately |
| Country stub called (IN, US in v1.0.0) | Throw `UnsupportedOperationException` with clear message |

### 8.3 Where Validation Happens

```
Input → GeoAdmin (no validation)
           ↓
        GeoService (validates: null/blank IDs and names)
           ↓
        AdminRepository (no validation — trusts Service)
           ↓
        JsonDataLoader (validates: file access, JSON parse)
```

---

## 9. Thread Safety Design

### 9.1 Requirements

- The library must support **concurrent read operations** from multiple threads
- Data must never be mutated after loading
- Repository loading must happen at most **once per country** even under concurrent access

### 9.2 Implementation

**Model objects** (`AdminLevel1/2/3`) — Thread-safe because all fields are `final`.

**`AdminRepository`** — Thread-safe after construction. All internal maps are populated once in the constructor and never modified again. Uses `Collections.unmodifiableMap()` wrappers.

**`GeoService` cache** — Uses `ConcurrentHashMap.computeIfAbsent()` for thread-safe lazy loading:

```java
private final Map<CountryCode, AdminRepository> repositoryCache =
    new ConcurrentHashMap<>();

private AdminRepository getRepository(CountryCode code) {
    return repositoryCache.computeIfAbsent(code, c ->
        CountryFactory.getLoader(c).load()
    );
}
```

`computeIfAbsent()` guarantees that `load()` is called **at most once per key** even under concurrent access, without requiring explicit `synchronized` blocks.

**`GeoAdmin` singleton** — Uses **double-checked locking** pattern:

```java
private static volatile GeoAdmin instance;

public static GeoAdmin getInstance() {
    if (instance == null) {
        synchronized (GeoAdmin.class) {
            if (instance == null) {
                instance = new GeoAdmin();
            }
        }
    }
    return instance;
}
```

The `volatile` keyword ensures visibility across threads.

### 9.3 Thread Safety Summary

| Component | Strategy | Safe for concurrent reads? |
|-----------|----------|---------------------------|
| `AdminLevel1/2/3` | Immutable fields | ✅ Yes |
| `AdminRepository` | Unmodifiable maps | ✅ Yes |
| `GeoService` cache | `ConcurrentHashMap` | ✅ Yes |
| `GeoAdmin` singleton | Double-checked locking | ✅ Yes |
| `JsonDataLoader` | Stateless | ✅ Yes |

---

## 10. Caching and Initialization Strategy

### 10.1 Strategy: Lazy Loading per Country

Data is **not loaded at application startup**. Loading happens on the first access to a specific country's data.

```
First call  → GeoService checks cache → MISS → BangladeshLoader.load() → cache result
All future  → GeoService checks cache → HIT  → return cached repository immediately
```

**Reason:** A developer may only use Bangladesh data. Loading India and USA data unnecessarily would waste memory and increase startup time.

### 10.2 Cache Location

The cache lives in `GeoService.repositoryCache`:

```java
private final Map<CountryCode, AdminRepository> repositoryCache = new ConcurrentHashMap<>();
```

One `AdminRepository` instance per country. Never evicted. Held for the lifetime of the `GeoService` instance.

### 10.3 Memory Estimate

| Country | Admin1 | Admin2 | Admin3 | Approx. Memory |
|---------|--------|--------|--------|----------------|
| Bangladesh | 8 | 64 | ~500 | < 2 MB |

This is well within acceptable limits for a Java library.

---

## 11. Design Patterns Summary

| Pattern | Applied In | Why |
|---------|------------|-----|
| **Facade** | `GeoAdmin.java` | Hides internal complexity. One class for the consumer. |
| **Strategy** | `CountryLoader` interface + implementations | Each country has its own loading logic. Easy to add new countries. |
| **Factory** | `CountryFactory.java` | Centralizes loader registration. Decouples `GeoService` from concrete loaders. |
| **Repository** | `AdminRepository.java` | Separates data access from business logic. Standard data layer pattern. |
| **Singleton** | `GeoAdmin.getInstance()` | Optional reuse pattern for consumers who prefer a shared instance. |

---

## 12. Dependency Graph

```
GeoAdmin
  └── GeoService
        ├── CountryFactory
        │     ├── BangladeshLoader
        │     │     └── JsonDataLoader
        │     ├── IndiaLoader (stub)
        │     └── USALoader (stub)
        └── AdminRepository
              ├── AdminLevel1
              ├── AdminLevel2
              └── AdminLevel3

Supporting:
  CountryCode   (used by: CountryLoader, CountryFactory, GeoService, GeoAdmin)
  AdminLevel    (used by: GeoService)
  BoundingBox   (used by: GeoJson)
  GeoJson       (used by: future exporters)
```

**Dependency direction rule:** Dependencies flow downward only. No layer references a layer above it.

---

## 13. Revision History

| Version | Date | Author | Description                                              |
|---------|------|--------|----------------------------------------------------------|
| 1.0.0 | 2026-04-14 | Pollob Kumar | Initial SDD, covers full architecture for v1.0.0 release |

---

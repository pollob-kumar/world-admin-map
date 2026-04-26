# World Admin Map

A production-ready Java library that provides structured administrative geographic data — divisions, districts, and upazilas — for multiple countries including latitude and longitude coordinates. It is packaged as a Maven artifact and ships with JSON data bundled inside the JAR, so no external database or API is required.

> Currently supported: **BD (Bangladesh)**
> Roadmap: India (IN), USA (US), and more

---

## Features

* Zero external runtime dependencies
* Administrative levels: Division (ADM1) → District (ADM2) → Upazila (ADM3)
* Country-agnostic, pluggable architecture — add new countries with a single loader
* Ready for Maven Central publishing
* Country-level access via a single facade API (`GeoAdmin`)
* In-memory, read-only repository with fast lookups
* Search by ID or name, plus parent-child traversal
* GeoJSON export (Point features and feature collections)
* Minimal dependencies (Jackson + JUnit for tests)
* Thread-safe for concurrent read operations

---

## How it works (high-level flow)

1. `GeoAdmin` is the public entry point.
2. `GeoService` validates input and routes requests.
3. `CountryFactory` selects the correct `CountryLoader` for a `CountryCode`.
4. The loader uses `JsonDataLoader` to read classpath JSON from `src/main/resources/data/<cc>/`.
5. `AdminRepository` builds in-memory indexes for fast queries and is cached per country.

Data is loaded once per country and reused across calls. All models are immutable and read-only.

---

## Requirements

* Java 8 or higher
* Maven 3+

---

## Installation

### ⚠️ Important

This library is not yet published to Maven Central. It will be available after the first official release.

For now, you can use it locally by building and installing it into your local Maven repository.

### Option 1: Install locally (recommended for development)

```bash
mvn clean install
```

After that, you can use it in other projects via your local `.m2` repository.

### Maven (after official release)

```xml
<dependency>
    <groupId>io.github.pollob-kumar</groupId>
    <artifactId>world-admin-map</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Maven (development snapshot option)

```xml
<version>1.0.0-SNAPSHOT</version>
```

### Gradle (after official release)

```gradle
implementation 'io.github.pollob-kumar:world-admin-map:1.0.0'
```

---

## Quick start

```java
import io.github.pollob_kumar.worldadmin.GeoAdmin;
import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import java.util.List;

GeoAdmin geo = GeoAdmin.create();

List<AdminLevel2> districts = geo.getLevel2(CountryCode.BD, "BD10");
AdminUnit first = districts.get(0);

String geoJson = geo.toGeoJson(first);
```

---

## Public API (summary)

* `getAllLevel1(CountryCode code)`
* `getLevel2(CountryCode code, String level1Id)`
* `getLevel3(CountryCode code, String level2Id)`
* `getById(CountryCode code, String id)`
* `searchByName(CountryCode code, String name)`
* `searchAll(CountryCode code, String name)`
* `getSupportedCountries()`
* `toGeoJson(AdminUnit unit)`
* `toGeoJson(List<? extends AdminUnit> units)`

---

## Supported countries

* **BD (Bangladesh)**: data available
* **IN (India)**: loader exists but not fully enabled (planned)
* **US (USA)**: loader exists but not fully enabled (planned)

---

## Data model (summary)

* **AdminLevel1**: `id`, `name`, `lat`, `lon`
* **AdminLevel2**: `id`, `name`, `parentId`, `lat`, `lon`
* **AdminLevel3**: `id`, `name`, `parentId`, `lat`, `lon`

---

## Build & test

```bash
mvn clean package
mvn test
```

---

## Documentation

* `CONTRIBUTING.md` — contribution workflow and branch policy
* `DIRECTORY.md` — repository structure
* `SRS.md` — requirements and scope
* `SDD.md` — design details and architecture
* `LICENSE` — license information
* `NOTICE` — attribution and notices

---

## Author

**Pollob Kumar**
- GitHub: [@pollob-kumar](https://github.com/pollob-kumar)

---

> This gap is filled — there was no dedicated Java library for administrative geographic data before. For developers who want reliable geo data without any extra hassle.


# Software Requirements Specification (SRS)

---
**Project Name:** World Admin Map  
**Version:** 1.1.2  
**Date:** 2026-04-19  
**Prepared by:** Pollob Kumar

### Artifact Purpose:
A Java-based library to provide structured administrative geographical data (countries → states/divisions → districts → cities/unions) with latitude and longitude support.
This library is designed for distribution via Maven Central Repository.

---

# 1. Project Overview

This project is a world administrative map that provides structured geographical data such as divisions, districts, and unions with latitude and longitude.
This project is a lightweight Java library that provides hierarchical administrative map data with minimal dependencies (Jackson for JSON parsing).
It allows developers to access structured location data programmatically without requiring a database or external API.
The library will be published on Maven Central for global usage.

---

# 2. Objective

- Provide reusable Java library for geographical admin data
- Keep runtime dependencies minimal (Jackson for JSON parsing + JSON resources)
- Support multiple countries in scalable format
- Make it Maven Central compatible
- Provide clean, stable API for developers
- Ensure backward compatibility for future versions

---

# 3. Maven Library Requirements

## 3.1 Maven Coordinates
- groupId: io.github.pollob-kumar 
- artifactId: world-admin-map
- version: 1.0.0 (release)

## 3.2 Packaging Type
- jar

## 3.3 Java Version
- Java 17

## 3.4 Build Tool
- Maven

---

# 4. Core Features

- Retrieve all Level 1 administrative regions by country code
- Retrieve Level 2 regions by parent ID and country code
- Retrieve Level 3 regions by parent ID and country code
- Get an administrative unit by ID
- Search location by name (first match) and fetch all matches
- Fetch latitude and longitude from model getters
- Convert units to GeoJSON feature/feature collection (Point geometry)
- Get supported country codes
- Lightweight in-memory data loading
- Thread-safe access
- No database required

---

# 5. Data Model Design

### AdminLevel1 (Division / State)
- id (String) → Example: BD10
- name (String)
- lat (double)
- lon (double)

### AdminLevel2 (District / Region)
- id (String)
- name (String)
- parentId (AdminLevel1 ID)
- lat (double)
- lon (double)

### AdminLevel3 (Union / City / Area)
- id (String)
- name (String)
- parentId (AdminLevel2 ID)
- lat (double)
- lon (double)

---

# 6. Internal Package Structure

```
world-admin-map/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/github/pollob_kumar/worldadmin/
│   │   │                              ├── GeoAdmin.java             ← Facade (Main API entry)
│   │   │                              │
│   │   │                              ├── model/                    ← Data Layer
│   │   │                              │   ├── AdminUnit.java
│   │   │                              │   ├── AdminLevel1.java
│   │   │                              │   ├── AdminLevel2.java
│   │   │                              │   └── AdminLevel3.java
│   │   │                              │
│   │   │                              ├── enums/
│   │   │                              │   ├── AdminLevel.java
│   │   │                              │   └── CountryCode.java
│   │   │                              │
│   │   │                              ├── exception/
│   │   │                              │   ├── GeoDataLoadException.java
│   │   │                              │   └── UnsupportedCountryException.java
│   │   │                              │
│   │   │                              ├── repository/               ← Data Access Layer
│   │   │                              │   ├── AdminRepository.java
│   │   │                              │   └── JsonDataLoader.java
│   │   │                              │
│   │   │                              ├── service/                  ← Business Logic Layer
│   │   │                              │   └── GeoService.java
│   │   │                              │
│   │   │                              ├── country/                  ← Strategy Pattern
│   │   │                              │   ├── CountryLoader.java
│   │   │                              │   ├── BangladeshLoader.java
│   │   │                              │   ├── IndiaLoader.java
│   │   │                              │   └── USALoader.java
│   │   │                              │
│   │   │                              ├── factory/                  ← Factory Layer
│   │   │                              │   └── CountryFactory.java
│   │   │                              │
│   │   │                              ├── geo/                      ← Utility
│   │   │                              │   ├── GeoJson.java
│   │   │                              │   └── BoundingBox.java
│   │   │
│   │   └── resources/
│   │       └── data/
│   │           ├── bd/
│   │           │   ├── bd_admin1.json
│   │           │   ├── bd_admin2.json
│   │           │   └── bd_admin3.json
│   │           │
│   │           ├── in/
│   │           │   ├── in_admin1.json
│   │           │   ├── in_admin2.json
│   │           │   └── in_admin3.json
│   │           │
│   │           └── us/
│   │               ├── usa_admin1.json
│   │               ├── usa_admin2.json
│   │               └── usa_admin3.json
│   │
│   └── test/
│       └── java/
│           └── io/github/pollob_kumar/worldadmin/
│                                      ├── GeoServiceTest.java
│                                      └── BangladeshTest.java
│
├── CONTRIBUTING.md
├── DIRECTORY.md
├── LICENSE
├── NOTICE
├── pom.xml
├── README.md
├── SDD.md
└── SRS.md
```

# 7. Data Model Design (Example Data)

### AdminLevel1 (Country / State / Division)
```json
{
  "id": "BD10",
  "name": "Barishal",
  "lat": 22.3928,
  "lon": 90.4064
}
```
### AdminLevel2 (District / Region)
```json
{
"id": "BD1004",
"name": "Barguna",
"parentId": "BD10",
"lat": 22.1282,
"lon": 90.1101
}
```
### AdminLevel3 (Union / City / Area)
```json
{
  "id": "BD10040009",
  "name": "Amtali",
  "parentId": "BD1004",
  "lat": 22.1465,
  "lon": 90.2700
}
````

---

# 8. Public API Design (Basic)

## Methods:
### Get all level 1 (by country)
```java
List<AdminLevel1> getAllLevel1(CountryCode code);
```
### Get level 2 by parent (by country)
```java
List<AdminLevel2> getLevel2(CountryCode code, String level1Id);
```
### Get level 3 by parent (by country)
```java
List<AdminLevel3> getLevel3(CountryCode code, String level2Id);
```
### Get by ID
```java
Optional<AdminUnit> getById(CountryCode code, String id);
```
### Search by name (first match)
```java
Optional<AdminUnit> searchByName(CountryCode code, String name);
```
### Search by name (all matches)
```java
List<AdminUnit> searchAll(CountryCode code, String name);
```
### Supported countries
```java
List<CountryCode> getSupportedCountries();
```
### GeoJSON export
```java
String toGeoJson(AdminUnit unit);
String toGeoJson(List<? extends AdminUnit> units);
```

---

# 9. Rules & Constraints

* All IDs must be unique globally
* Must follow ISO-like country codes (BD, US, IN)
* No duplicate names within same hierarchy level
* Parent-child relationship must be valid
* JSON files must be immutable after packaging
* No runtime file modification allowed

---

# 10. Multi-Country Support Strategy

System exposes country codes:

* Bangladesh (BD) — data available
* USA (US) — loader placeholder (throws UnsupportedCountryException)
* India (IN) — loader placeholder (throws UnsupportedCountryException)

Currently supports Bangladesh only; India/USA are planned.

Each country is isolated in separate JSON files:

Example:

* bd_admin1.json
* us_admin1.json
* in_admin1.json

System must dynamically load based on country code.

---

# 11. Branch Strategy

All feature branches are created from `develop` and merged back via Pull Request.

| # | Branch Name                        | What Will Be                                                                                                                   | Depends On |
|---|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|---|
| 1 | `pollob/project-setup`             | `pom.xml`, `.gitignore`                                                                                                        | — |
| 2 | `feature/core-models`              | `model/AdminUnit.java`, `AdminLevel1.java`, `AdminLevel2.java`, `AdminLevel3.java`, `enums/AdminLevel.java`, `CountryCode.java` | `feature/project-setup` |
| 3 | `feature/exception-layer`          | `exception/GeoDataLoadException.java`, `UnsupportedCountryException.java`                                                      | `feature/project-setup` |
| 4 | `feature/repository-layer`         | `repository/AdminRepository.java`, `JsonDataLoader.java`                                                                       | `feature/core-models`, `feature/exception-layer` |
| 5 | `feature/json-resources`           | `resources/data/bd/`, `in/`, `us/` — All `.json` data files                                                                    | `feature/repository-layer` |
| 6 | `feature/country-loader-interface` | `country/CountryLoader.java` (interface only)                                                                                  | `feature/core-models` |
| 7 | `feature/bangladesh-loader`        | `country/BangladeshLoader.java`                                                                                                | `feature/country-loader-interface`, `feature/json-resources` |
| 8 | `feature/india-loader`             | `country/IndiaLoader.java`                                                                                                     | `feature/country-loader-interface`, `feature/exception-layer` |
| 9 | `feature/usa-loader`               | `country/USALoader.java`                                                                                                       | `feature/country-loader-interface`, `feature/exception-layer` |
| 10 | `feature/country-factory`          | `factory/CountryFactory.java`                                                                                                  | `feature/bangladesh-loader`, `feature/india-loader`, `feature/usa-loader` |
| 11 | `feature/geo-utils`                | `geo/GeoJson.java`, `BoundingBox.java`                                                                                         | `feature/core-models` |
| 12 | `feature/service-layer`            | `service/GeoService.java`                                                                                                      | `feature/country-factory`, `feature/geo-utils` |
| 13 | `feature/facade-api`               | `GeoAdmin.java`                                                                                                                | `feature/service-layer` |
| 14 | `test/unit-tests`                  | `GeoServiceTest.java`, `BangladeshTest.java`                                                                                   | `feature/facade-api` |
| 15 | `docs/sdd`                         | `SDD.md`                                                                                                                       | `feature/facade-api` |
| 16 | `docs/update-all-docs`             | `CONTRIBUTING.md`, `DIRECTORY.md`, `LICENSE`, `NOTICE`, `README.md`, `SDD.md`, `SRS.md`                                        | `feature/project-setup` |

**Note:**
- Now India/USA loader is not needed, start with Bangladesh. `feature/india-loader` and `feature/usa-loader` will now be stubs/placeholders.

---

# 12. Future Scope

* Maven plugin for auto data validation
* Spring Boot starter version
* GraphQL support
* Real-time API version
* Database integration (optional module)

---

# 13. Non-Functional Requirements

* Performance: Fast data retrieval. O(1) or O(log n) lookup preferred using HashMap indexing
* Scalability: Easy to add new countries
* Memory: Load once at startup (lazy initialization allowed)
* Thread Safety: Must support concurrent read operations
* Reliability: No external API dependency. Accurate data
* Maintainability: Clean and readable code

---

# 14. AI Development Rule

* Always follow this SRS strictly
* Do not change data structure
* Maintain naming conventions
* Generate clean and modular code
* Must not change package structure
* Must not modify data model fields
* Must ensure Maven compatibility

---

# 15. Maven Central Requirements

- Proper pom.xml configuration
- Source JAR and Javadoc JAR required
- GPG signed artifacts
- Semantic versioning
- No SNAPSHOT releases for Maven Central (development builds may use -SNAPSHOT)

---

# 16. External Integration Overview
The system will be distributed as a reusable Java library through Maven Central Repository.
Developers can easily integrate the library into their projects using a standard dependency configuration.

### Dependency Configuration (Maven)
```xml
<dependency>
    <groupId>io.github.pollob-kumar</groupId>
    <artifactId>world-admin-map</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Importing the Library
After adding the dependency, developers can access the system using the main API class:
```java
import io.github.pollob_kumar.worldadmin.GeoAdmin;
```
---

# 17. Testing Requirements
- Unit tests using JUnit 5
- Test coverage ≥ 80%
- Validation tests for JSON loading
- Edge case handling tests

---

# 18. Dependencies Policy

**Allowed:**

* Jackson (JSON parsing) 
* JUnit (testing)

**Not allowed:**

* Heavy frameworks (Spring Core in base library)
* External runtime services
* Database drivers

---

# 19. Assumptions

* Data is pre-collected and validated
* JSON format is stable
* Library will be used read-only
* Library is read-only
* No runtime mutation allowed

---

# 20. Revision History

| Version | Date       | Author       | Description                                                                                                                                     |
|---------|------------| ------------ |-------------------------------------------------------------------------------------------------------------------------------------------------|
| 1.0     | 2026-04-12 | Pollob Kumar | Initial version of SRS document                                                                                                                 |
| 1.0.1   | 2026-04-13 | Pollob Kumar | updated SRS with CONTRIBUTING.md and external <br/> integration overview.                                                                       |
| 1.1.1   | 2026-04-13 | Pollob Kumar | added Branch Strategy                                                                                                                           | 
| 1.1.2   | 2026-04-17 | Pollob Kumar | aligned SRS with current API, dependencies, and country loaders and updated Directory, Branch Strategy to include exception layer dependencies. |

----

# 21. LICENSE

- Apache 2.0

---


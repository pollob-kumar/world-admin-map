
# Software Requirements Specification (SRS)

---
**Project Name:** World Admin Map  
**Version:** 1.0.1  
**Date:** 2026-04-13  
**Prepared by:** Pollob Kumar

### Artifact Purpose:
A Java-based library to provide structured administrative geographical data (countries → states/divisions → districts → cities/unions) with latitude and longitude support.
This library is designed for distribution via Maven Central Repository.

---

# 1. Project Overview

This project is a world administrative map that provides structured geographical data such as divisions, districts, and unions with latitude and longitude.
This project is a lightweight, dependency-free Java library that provides hierarchical administrative map data.
It allows developers to access structured location data programmatically without requiring a database or external API.
The library will be published on Maven Central for global usage.

---

# 2. Objective

- Provide reusable Java library for geographical admin data
- Ensure zero external runtime dependency (pure Java + JSON resources)
- Support multiple countries in scalable format
- Make it Maven Central compatible
- Provide clean, stable API for developers
- Ensure backward compatibility for future versions

---

# 3. Maven Library Requirements

## 3.1 Maven Coordinates
- groupId: io.github.pollob-kumar 
- artifactId: world-admin-map
- version: 1.0.0

## 3.2 Packaging Type
- jar

## 3.3 Java Version
- Java 11+ (recommended: Java 17)

## 3.4 Build Tool
- Maven

---

# 4. Core Features

- Retrieve all Level 1 administrative regions (countries/states/divisions)
- Retrieve Level 2 regions by parent ID
- Retrieve Level 3 regions by parent ID
- Search location by name
- Fetch latitude and longitude
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
├── pom.xml
├── README.md
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
### Get all level 1
```java
List<AdminLevel1> getAllCountries();
```
### Get level 2 by parent
```java
List<AdminLevel2> getLevel2(String level1Id);
```
### Get level 3 by parent
```java
List<AdminLevel3> getLevel3(String level2Id);
```
### Search by name
```java
Optional<Object> searchByName(String name);
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

System must support:

* Bangladesh (BD)
* USA (US)
* India (IN)

Each country is isolated in separate JSON files:

Example:

* bd_admin1.json
* us_admin1.json
* in_admin1.json

System must dynamically load based on country code.

---

# 11. Future Scope

* Maven plugin for auto data validation
* Spring Boot starter version
* GraphQL support
* GeoJSON export support
* Real-time API version
* Database integration (optional module)

---

# 12. Non-Functional Requirements

* Performance: Fast data retrieval. O(1) or O(log n) lookup preferred using HashMap indexing
* Scalability: Easy to add new countries
* Memory: Load once at startup (lazy initialization allowed)
* Thread Safety: Must support concurrent read operations
* Reliability: No external API dependency. Accurate data
* Maintainability: Clean and readable code

---

# 13. AI Development Rule

* Always follow this SRS strictly
* Do not change data structure
* Maintain naming conventions
* Generate clean and modular code
* Must not change package structure
* Must not modify data model fields
* Must ensure Maven compatibility

---

# 14. Maven Central Requirements

- Proper pom.xml configuration
- Source JAR and Javadoc JAR required
- GPG signed artifacts
- Semantic versioning
- No SNAPSHOT releases

---

# 15. External Integration Overview
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

# 16. Testing Requirements
- Unit tests using JUnit 5
- Test coverage ≥ 80%
- Validation tests for JSON loading
- Edge case handling tests

---

# 17. Dependencies Policy

**Allowed:**

* Jackson (JSON parsing) 
* JUnit (testing)

**Not allowed:**

* Heavy frameworks (Spring Core in base library)
* External runtime services
* Database drivers

---

# 18. Assumptions

* Data is pre-collected and validated
* JSON format is stable
* Library will be used read-only
* Library is read-only
* No runtime mutation allowed

---

# 19. Revision History

| Version | Date       | Author       | Description                                                               |
|---------|------------| ------------ |---------------------------------------------------------------------------|
| 1.0     | 2026-04-12 | Pollob Kumar | Initial version of SRS document                                           |
| 1.0.1   | 2026-04-13 | Pollob Kumar | updated SRS with CONTRIBUTING.md and external <br/> integration overview. |

----

# 20. LICENSE

- Apache 2.0

---


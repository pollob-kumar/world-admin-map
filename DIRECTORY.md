# Folder structure of the World Admin Map.

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
├── DIRECTORY.md
├── LICENSE
├── pom.xml
├── README.md
└── SRS.md
```



GroupId: io.github.pollob-kumar
<br>
ArtifactId: world-admin-map

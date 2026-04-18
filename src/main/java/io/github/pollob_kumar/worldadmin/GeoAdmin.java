package io.github.pollob_kumar.worldadmin;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.geo.GeoJson;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import io.github.pollob_kumar.worldadmin.service.GeoService;
import java.util.List;
import java.util.Optional;

public class GeoAdmin {
    private final GeoService geoService;

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

    public static GeoAdmin create() {
        return new GeoAdmin();
    }

    public GeoAdmin() {
        this.geoService = new GeoService();
    }

    public List<AdminLevel1> getAllLevel1(CountryCode code) {
        return geoService.getAllLevel1(code);
    }

    public List<AdminLevel2> getLevel2(CountryCode code, String level1Id) {
        return geoService.getLevel2(code, level1Id);
    }

    public List<AdminLevel3> getLevel3(CountryCode code, String level2Id) {
        return geoService.getLevel3(code, level2Id);
    }

    public Optional<AdminUnit> getById(CountryCode code, String id) {
        return geoService.getById(code, id);
    }

    public Optional<AdminUnit> searchByName(CountryCode code, String name) {
        return geoService.searchByName(code, name);
    }

    public List<AdminUnit> searchAll(CountryCode code, String name) {
        return geoService.searchAll(code, name);
    }

    public List<CountryCode> getSupportedCountries() {
        return geoService.getSupportedCountries();
    }

    public String toGeoJson(AdminUnit unit) {
        return GeoJson.toFeature(unit);
    }

    public String toGeoJson(List<? extends AdminUnit> units) {
        return GeoJson.toFeatureCollection(units);
    }
}

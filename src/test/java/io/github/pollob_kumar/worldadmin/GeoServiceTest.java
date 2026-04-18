package io.github.pollob_kumar.worldadmin;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.exception.UnsupportedCountryException;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeoServiceTest {
    @Test
    void getLevel2RejectsBlankId() {
        GeoAdmin geo = GeoAdmin.create();
        Assertions.assertThrows(IllegalArgumentException.class, () -> geo.getLevel2(CountryCode.BD, " "));
    }

    @Test
    void getByIdFindsKnownUnit() {
        GeoAdmin geo = GeoAdmin.create();
        Optional<AdminUnit> unit = geo.getById(CountryCode.BD, "BD10");
        Assertions.assertTrue(unit.isPresent());
        Assertions.assertEquals("BD10", unit.get().getId());
    }

    @Test
    void searchAllReturnsMatches() {
        GeoAdmin geo = GeoAdmin.create();
        List<AdminLevel2> level2 = geo.getLevel2(CountryCode.BD, "BD10");
        Assertions.assertFalse(level2.isEmpty());
        String name = level2.get(0).getName();
        List<AdminUnit> results = geo.searchAll(CountryCode.BD, name);
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    void unsupportedCountryThrows() {
        GeoAdmin geo = GeoAdmin.create();
        Assertions.assertThrows(UnsupportedCountryException.class, () -> geo.getAllLevel1(CountryCode.IN));
    }
}

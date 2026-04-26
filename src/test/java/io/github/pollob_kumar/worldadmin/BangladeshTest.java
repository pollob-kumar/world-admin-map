package io.github.pollob_kumar.worldadmin;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BangladeshTest {
    @Test
    void loadsBangladeshLevel1Data() {
        GeoAdmin geo = GeoAdmin.create();
        List<AdminLevel1> level1 = geo.getAllLevel1(CountryCode.BD);
        Assertions.assertFalse(level1.isEmpty());
    }
}

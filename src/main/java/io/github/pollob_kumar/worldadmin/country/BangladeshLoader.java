package io.github.pollob_kumar.worldadmin.country;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import io.github.pollob_kumar.worldadmin.repository.AdminRepository;
import io.github.pollob_kumar.worldadmin.repository.JsonDataLoader;
import java.util.List;

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

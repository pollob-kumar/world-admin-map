package io.github.pollob_kumar.worldadmin.factory;

import io.github.pollob_kumar.worldadmin.country.BangladeshLoader;
import io.github.pollob_kumar.worldadmin.country.CountryLoader;
import io.github.pollob_kumar.worldadmin.country.IndiaLoader;
import io.github.pollob_kumar.worldadmin.country.USALoader;
import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.exception.UnsupportedCountryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CountryFactory {
    private static final Map<CountryCode, CountryLoader> LOADERS;

    static {
        Map<CountryCode, CountryLoader> map = new EnumMap<>(CountryCode.class);
        map.put(CountryCode.BD, new BangladeshLoader());
        map.put(CountryCode.IN, new IndiaLoader());
        map.put(CountryCode.US, new USALoader());
        LOADERS = Collections.unmodifiableMap(map);
    }

    public static CountryLoader getLoader(CountryCode code) {
        CountryLoader loader = LOADERS.get(code);
        if (loader == null) {
            throw new UnsupportedCountryException("No loader found for: " + code);
        }
        return loader;
    }

    public static List<CountryCode> getSupportedCountries() {
        return Collections.unmodifiableList(new ArrayList<>(LOADERS.keySet()));
    }
}

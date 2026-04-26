package io.github.pollob_kumar.worldadmin.country;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.repository.AdminRepository;

public interface CountryLoader {
    CountryCode getCountryCode();

    AdminRepository load();
}

package io.github.pollob_kumar.worldadmin.country;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.exception.UnsupportedCountryException;
import io.github.pollob_kumar.worldadmin.repository.AdminRepository;

public class IndiaLoader implements CountryLoader {
    @Override
    public CountryCode getCountryCode() {
        return CountryCode.IN;
    }

    @Override
    public AdminRepository load() {
        throw new UnsupportedCountryException(
                "India (IN) data is not yet available. Planned for a future release."
        );
    }
}

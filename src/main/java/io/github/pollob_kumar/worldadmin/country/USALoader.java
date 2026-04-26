package io.github.pollob_kumar.worldadmin.country;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.exception.UnsupportedCountryException;
import io.github.pollob_kumar.worldadmin.repository.AdminRepository;

public class USALoader implements CountryLoader {
    @Override
    public CountryCode getCountryCode() {
        return CountryCode.US;
    }

    @Override
    public AdminRepository load() {
        throw new UnsupportedCountryException(
                "USA (US) data is not yet available. Planned for a future release."
        );
    }
}

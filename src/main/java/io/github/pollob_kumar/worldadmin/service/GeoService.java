package io.github.pollob_kumar.worldadmin.service;

import io.github.pollob_kumar.worldadmin.enums.CountryCode;
import io.github.pollob_kumar.worldadmin.factory.CountryFactory;
import io.github.pollob_kumar.worldadmin.model.AdminLevel1;
import io.github.pollob_kumar.worldadmin.model.AdminLevel2;
import io.github.pollob_kumar.worldadmin.model.AdminLevel3;
import io.github.pollob_kumar.worldadmin.model.AdminUnit;
import io.github.pollob_kumar.worldadmin.repository.AdminRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GeoService {
    private final Map<CountryCode, AdminRepository> repositoryCache = new ConcurrentHashMap<>();

    private AdminRepository getRepository(CountryCode code) {
        if (code == null) {
            throw new IllegalArgumentException("Country code must not be null.");
        }
        return repositoryCache.computeIfAbsent(code, c -> CountryFactory.getLoader(c).load());
    }

    public List<AdminLevel1> getAllLevel1(CountryCode code) {
        return getRepository(code).getAllLevel1();
    }

    public List<AdminLevel2> getLevel2(CountryCode code, String level1Id) {
        validateId(level1Id);
        return getRepository(code).findLevel2ByParent(level1Id);
    }

    public List<AdminLevel3> getLevel3(CountryCode code, String level2Id) {
        validateId(level2Id);
        return getRepository(code).findLevel3ByParent(level2Id);
    }

    public Optional<AdminUnit> searchByName(CountryCode code, String name) {
        validateName(name);
        return getRepository(code).searchByName(name);
    }

    public List<AdminUnit> searchAll(CountryCode code, String name) {
        validateName(name);
        return getRepository(code).searchAll(name);
    }

    public Optional<AdminUnit> getById(CountryCode code, String id) {
        validateId(id);
        AdminRepository repo = getRepository(code);
        Optional<AdminUnit> result = repo.findLevel1ById(id).map(unit -> (AdminUnit) unit);
        if (result.isPresent()) {
            return result;
        }
        result = repo.findLevel2ById(id).map(unit -> (AdminUnit) unit);
        if (result.isPresent()) {
            return result;
        }
        return repo.findLevel3ById(id).map(unit -> (AdminUnit) unit);
    }

    public List<CountryCode> getSupportedCountries() {
        return CountryFactory.getSupportedCountries();
    }

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be null or blank.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank.");
        }
    }
}

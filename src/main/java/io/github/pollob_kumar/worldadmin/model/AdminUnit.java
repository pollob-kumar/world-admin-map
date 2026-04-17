package io.github.pollob_kumar.worldadmin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AdminUnit {
    private final String id;
    private final String name;
    private final double lat;
    private final double lon;

    protected AdminUnit(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AdminUnit)) {
            return false;
        }
        AdminUnit that = (AdminUnit) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id='" + id + "', name='" + name + "', lat=" + lat + ", lon=" + lon + "}";
    }
}

package io.github.pollob_kumar.worldadmin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AdminLevel1 extends AdminUnit {
    @JsonCreator
    public AdminLevel1(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon
    ) {
        super(id, name, lat, lon);
    }
}

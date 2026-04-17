package io.github.pollob_kumar.worldadmin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AdminLevel2 extends AdminUnit {
    private final String parentId; // AdminLevel1 id (Division/State id)

    @JsonCreator
    public AdminLevel2(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("parentId") String parentId,
            @JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon
    ) {
        super(id, name, lat, lon);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }
}

package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BranchResponse(

        @JsonProperty("name")
        String name,
        @JsonProperty("last_Sha")
        String lastSha

) {}

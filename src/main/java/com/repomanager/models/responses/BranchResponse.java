package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record BranchResponse(

        @JsonProperty("name")
        String name,
        @JsonProperty("last_sha")
        String lastSha

) {}

package com.repomanager.models.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DefaultErrorResponse(

        @JsonProperty("status")
        int status,
        @JsonProperty("Message")
        String message

) {}

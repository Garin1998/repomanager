package com.repomanager.models.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DefaultExceptionResponse(

        @JsonProperty("status")
        int status,
        @JsonProperty("Message")
        String message

) {}

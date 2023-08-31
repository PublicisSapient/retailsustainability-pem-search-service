package com.publicis.sapient.p2p.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDto {

    int statusCode;
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;
}

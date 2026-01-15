package com.gozzerks.taskflow.domain.dto;

public record ErrorResponse(int status,
                            String message,
                            String details
) {

}

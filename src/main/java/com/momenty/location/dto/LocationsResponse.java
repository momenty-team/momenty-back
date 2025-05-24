package com.momenty.location.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.location.domain.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record LocationsResponse(
        @Schema(description = "위치 목록", required = true)
        List<LocationsDto> locations
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record LocationsDto (
            @Schema(description = "위치 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "위도", example = "21.1", required = true)
            Double latitude,

            @Schema(description = "경도", example = "21.1", required = true)
            Double longitude,

            @Schema(description = "생성날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt
    ) {}

    public static LocationsResponse of (List<Location> locations) {
        List<LocationsDto> locationsDtos = locations.stream()
                .map(location -> new LocationsDto(
                        location.getId(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getCreatedAt()
                )).toList();
        return new LocationsResponse(locationsDtos);
    }
}

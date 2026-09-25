package ru.relicarium.pledge.application.dto.response;

import java.util.List;

public record PledgePageResponse(
        List<PledgeResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}

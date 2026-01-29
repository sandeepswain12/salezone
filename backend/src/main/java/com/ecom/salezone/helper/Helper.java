package com.ecom.salezone.helper;

import com.ecom.salezone.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper<V> {

    private static final Logger log =
            LoggerFactory.getLogger(Helper.class);

    /**
     * Converts Page<U> into PageableResponse<V>
     */
    public static <U, V> PageableResponse<V> getPageableResponse(
            Page<U> page,
            Class<V> type,
            String logkey) {

        // Entry log – mapping operation
        log.info("[{}] Mapping pageable response | page={} size={}",
                logkey, page.getNumber(), page.getSize());

        List<U> entities = page.getContent();

        log.debug("[{}] Page content fetched | entityCount={}",
                logkey, entities.size());

        // Map entity list to DTO list
        List<V> dtoList = entities.stream()
                .map(object -> new ModelMapper().map(object, type))
                .collect(Collectors.toList());

        log.debug("[{}] Entities mapped to DTOs | dtoCount={}",
                logkey, dtoList.size());

        // Build pageable response
        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        // Exit log – final response info
        log.info(
                "[{}] Pageable response created | pageNumber={} totalPages={} lastPage={}",
                logkey,
                response.getPageNumber(),
                response.getTotalPages(),
                response.isLastPage()
        );

        return response;
    }
}

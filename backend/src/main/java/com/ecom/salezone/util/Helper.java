package com.ecom.salezone.util;

import com.ecom.salezone.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that provides helper methods used across the
 * SaleZone application.
 *
 * Currently this class contains helper functions for converting
 * Spring Data {@link org.springframework.data.domain.Page} objects
 * into custom {@link PageableResponse} DTO responses.
 *
 * This helps standardize pagination responses returned by APIs.
 *
 * Logging with logKey is used to trace request-level operations
 * for debugging and monitoring.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public class Helper<V> {

    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    /**
     * Converts a Spring Data {@link Page} of entities into
     * a {@link PageableResponse} containing DTO objects.
     *
     * The method maps entity objects to DTOs using ModelMapper
     * and copies pagination metadata such as page number,
     * total pages, and total elements.
     *
     * @param page   Spring Data page containing entity objects
     * @param type   DTO class type for mapping
     * @param logkey unique request identifier used for logging
     * @param <U>    Entity type
     * @param <V>    DTO type
     * @return pageable response containing mapped DTOs
     */
    public static <U, V> PageableResponse<V> getPageableResponse(
            Page<U> page,
            Class<V> type,
            String logkey) {

        // Entry log – mapping operation
        log.info("LogKey: {} - Mapping pageable response | page={} size={}",
                logkey, page.getNumber(), page.getSize());

        List<U> entities = page.getContent();

        log.debug("LogKey: {} - Page content fetched | entityCount={}",
                logkey, entities.size());

        // Map entity list to DTO list
        List<V> dtoList = entities.stream()
                .map(object -> new ModelMapper().map(object, type))
                .collect(Collectors.toList());

        log.debug("LogKey: {} - Entities mapped to DTOs | dtoCount={}",
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
                "LogKey: {} - Pageable response created | pageNumber={} totalPages={} lastPage={}",
                logkey,
                response.getPageNumber(),
                response.getTotalPages(),
                response.isLastPage()
        );

        return response;
    }
}
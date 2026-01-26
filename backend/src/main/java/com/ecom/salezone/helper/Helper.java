package com.ecom.salezone.helper;

import com.ecom.salezone.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper<V> {

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static <U, V> PageableResponse<V> getPageableResponse(
            Page<U> page,
            Class<V> type,
            String logkey) {

        logger.info("[{}] START MAPPING PAGE → PAGEABLE_RESPONSE", logkey);
        logger.debug("[{}] PAGE DETAILS | number={} size={} totalElements={} totalPages={}",
                logkey,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());

        List<U> entity = page.getContent();
        logger.debug("[{}] PAGE CONTENT FETCHED | entityCount={}", logkey, entity.size());

        List<V> userDtos = entity.stream()
                .map(object -> new ModelMapper().map(object, type))
                .collect(Collectors.toList());

        logger.debug("[{}] ENTITY LIST MAPPED TO DTO LIST | dtoCount={}",
                logkey, userDtos.size());

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(userDtos);
        response.setPageNumber(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        logger.info("[{}] PAGEABLE_RESPONSE CREATED SUCCESSFULLY | pageNumber={} pageSize={} lastPage={}",
                logkey,
                response.getPageNumber(),
                response.getPageSize(),
                response.isLastPage());

        return response;
    }
}

package com.ecom.salezone.helper;

import com.ecom.salezone.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper<V> {

    public static <U,V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type,String logkey){
        Logger logger = LoggerFactory.getLogger(Helper.class);
        logger.info("{} : MAPPED PAGE TO PAGEABLE_RESPONSE PAGE : {} ", logkey, page);
        List<U> entity = page.getContent();
        logger.info("{} : ENTITY : {} ", logkey, entity);
        List<V> userDtos = entity.stream().map(object -> new ModelMapper().map(object,type)).collect(Collectors.toList());
        logger.info("{} : USERDTOS : {} ", logkey, userDtos);
        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(userDtos);
        response.setPageNumber(page.getNumber()+1);
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        logger.info("{} : MAPPED COMPLETED : {} ", logkey, response);
        return response;
    }
}

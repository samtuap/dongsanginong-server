package org.samtuap.inong.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

@Data
@AllArgsConstructor
public class BaseResponse<T> {
    private long totalResults;
    private List<SearchHit<T>> hits;
}

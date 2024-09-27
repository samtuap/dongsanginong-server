package org.samtuap.inong.search.document;

import org.samtuap.inong.domain.farm.entity.Farm;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Document(indexName = "farm")
public class FarmDocument {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String farmName;

    @Field(type = FieldType.Text)
    private String farmIntro;

    public static FarmDocument convertToDocument(Farm farm) {
        return FarmDocument.builder()
                .id(farm.getId().toString())
                .farmName(farm.getFarmName())
                .farmIntro(farm.getFarmIntro())
                .build();
    }
}

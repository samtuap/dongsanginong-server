package org.samtuap.inong.search.document;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

    @Field(type = FieldType.Integer)
    private Long sellerId;

    @Field(type = FieldType.Text)
    private String farmName;

    @Field(type = FieldType.Text)
    private String bannerImageUrl;

    @Field(type = FieldType.Text)
    private String profileImageUrl;

    @Field(type = FieldType.Text)
    private String farmIntro;

    @Field(type = FieldType.Integer)
    private Long favoriteCount;

    @Field(type = FieldType.Integer)
    private Long orderCount;
}

//package org.samtuap.inong.search.document;
//
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.*;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@ToString
//@Builder
//@Document(indexName = "package_product")
//public class PackageProductDocument {
//
//    @Id
//    @Field(name = "id", type = FieldType.Keyword)
//    private String id;
//
//    @Field(type = FieldType.Text)
//    private String farmId;
//
//    @Field(type = FieldType.Text)
//    private String packageName;
//
//    @Field(type = FieldType.Integer)
//    private Integer delivery_cycle;
//
//    @Field(type = FieldType.Integer)
//    private Long price;
//}

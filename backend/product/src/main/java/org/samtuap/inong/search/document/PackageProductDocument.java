package org.samtuap.inong.search.document;

import lombok.*;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Document(indexName = "package_product")
public class PackageProductDocument {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String packageName;

    @Field(type = FieldType.Text)
    private String productDescription;

    public static PackageProductDocument convertToDocument(PackageProduct product) {
        return PackageProductDocument.builder()
                .id(product.getId().toString())
                .packageName(product.getPackageName())
                .productDescription(product.getProductDescription())
                .build();
    }
}
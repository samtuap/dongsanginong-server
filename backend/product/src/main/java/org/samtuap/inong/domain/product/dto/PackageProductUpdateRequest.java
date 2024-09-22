package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.service.PackageProductImageService;

import java.util.List;

@Builder
public record PackageProductUpdateRequest(
        String packageName,
        Integer deliveryCycle,
        Long price,
        List<String> deleteImageUrls,
        List<String> newImageUrls) {

    public void updatePackageProduct(PackageProduct packageProduct, PackageProductImageService imageService) {
        if (this.packageName != null) {
            packageProduct.updatePackageName(this.packageName);
        }
        if (this.deliveryCycle != null) {
            packageProduct.updateDeliveryCycle(this.deliveryCycle);
        }
        if (this.price != null) {
            packageProduct.updatePrice(this.price);
        }

        if (this.deleteImageUrls != null && !this.deleteImageUrls.isEmpty()) {
            imageService.deleteImages(packageProduct, this.deleteImageUrls());
        }
        if (this.newImageUrls != null && !this.newImageUrls.isEmpty()) {
            imageService.saveImages(packageProduct, this.newImageUrls());
        }
    }
}

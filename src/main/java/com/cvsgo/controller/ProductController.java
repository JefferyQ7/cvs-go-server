package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.ProductFilterResponseDto;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public SuccessResponse<List<ProductResponseDto>> getProductList(@LoginUser User user,
        @ModelAttribute ProductSearchRequestDto request, Pageable pageable) {
        return SuccessResponse.from(productService.getProductList(user, request, pageable));
    }

    @GetMapping("/{productId}")
    public SuccessResponse<ProductDetailResponseDto> readProduct(@LoginUser User user,
        @PathVariable Long productId) {
        return SuccessResponse.from(productService.readProduct(user, productId));
    }

    @GetMapping("/filter")
    public SuccessResponse<ProductFilterResponseDto> getProductFilter() {
        return SuccessResponse.from(productService.getProductFilter());
    }

}

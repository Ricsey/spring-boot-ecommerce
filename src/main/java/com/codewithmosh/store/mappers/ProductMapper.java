package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.CreateNewProductResponse;
import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.dtos.UpdateProductRequest;
import com.codewithmosh.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);
    Product toEntity(CreateNewProductResponse productDto);

    void update(UpdateProductRequest request, @MappingTarget Product product);
}

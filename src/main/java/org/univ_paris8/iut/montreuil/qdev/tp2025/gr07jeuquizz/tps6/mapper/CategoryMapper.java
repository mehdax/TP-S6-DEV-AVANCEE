package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.mapper;

import org.mapstruct.Mapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.CategoryDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);

    Category toEntity(CategoryDTO categoryDTO);
}

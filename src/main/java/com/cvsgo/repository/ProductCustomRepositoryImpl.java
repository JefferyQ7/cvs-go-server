package com.cvsgo.repository;

import static com.cvsgo.entity.QEvent.event;
import static com.cvsgo.entity.QProduct.product;
import static com.cvsgo.entity.QProductBookmark.productBookmark;
import static com.cvsgo.entity.QProductLike.productLike;
import static com.cvsgo.entity.QReview.review;
import static com.cvsgo.entity.QSellAt.sellAt;

import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchFilter;
import com.cvsgo.dto.product.QProductResponseDto;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.QEvent;
import com.cvsgo.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ProductResponseDto> searchByFilter(User user, ProductSearchFilter filter,
        Pageable pageable) {
        List<ProductResponseDto> results = queryFactory
            .select(new QProductResponseDto(
                product.id.as("productId"),
                product.name.as("productName"),
                product.price.as("productPrice"),
                product.imageUrl.as("productImageUrl"),
                product.category.id.as("categoryId"),
                product.manufacturer.name.as("manufacturerName"),
                ExpressionUtils.as(JPAExpressions.selectOne().from(productLike)
                    .where(product.id.eq(productLike.product.id)
                        .and(productLike.user.id.eq(user.getId()))).limit(1), "isLike"),
                ExpressionUtils.as(JPAExpressions.selectOne().from(productBookmark)
                    .where(product.id.eq(productBookmark.product.id)
                        .and(productBookmark.user.id.eq(user.getId()))).limit(1), "isBookmark"),
                ExpressionUtils.as(JPAExpressions.select(review.count()).from(review)
                    .where(product.id.eq(review.product.id)), "reviewCount"),
                ExpressionUtils.as(JPAExpressions.select(review.rating.avg()).from(review)
                    .where(product.id.eq(review.product.id)), "reviewRating")
            ))
            .from(product)
            .leftJoin(sellAt).on(sellAt.product.id.eq(product.id))
            .leftJoin(event).on(event.product.id.eq(product.id))
            .where(
                eqConvenienceStore(filter.getConvenienceStore()),
                eqCategory(filter.getCategory()),
                eqEvent(filter.getEvent()),
                priceLesserEqual(filter.getPrice()),
                priceGreaterOrEqual(filter.getPrice())
            )
            .groupBy(product.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private BooleanBuilder eqConvenienceStore(List<ConvenienceStore> convenienceStoreFilter) {
        BooleanBuilder builder = new BooleanBuilder();
        for (ConvenienceStore convenienceStore : convenienceStoreFilter) {
            builder.or(sellAt.convenienceStore.name.eq(convenienceStore.getName()));
        }
        return builder;
    }

    private BooleanBuilder eqCategory(List<Category> categoryFilter) {
        BooleanBuilder builder = new BooleanBuilder();
        for (Category category : categoryFilter) {
            builder.or(product.category.name.eq(category.getName()));
        }
        return builder;
    }

    private BooleanBuilder eqEvent(List<EventType> eventTypeFilter) {
        BooleanBuilder builder = new BooleanBuilder();
        for (EventType eventType : eventTypeFilter) {
            builder.or(QEvent.event.eventType.eq(eventType.getValue()));
        }
        return builder;
    }

    private BooleanExpression priceLesserEqual(List<Integer> priceFilter) {
        if (priceFilter.isEmpty()) {
            return null;
        }
        return product.price.loe(priceFilter.get(1));
    }

    private BooleanExpression priceGreaterOrEqual(List<Integer> priceFilter) {
        if (priceFilter.isEmpty()) {
            return null;
        }
        return product.price.goe(priceFilter.get(0));
    }
}

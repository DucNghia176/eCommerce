package ecommerce.productservice.service.impl;

import ecommerce.apicommon1.config.TokenInfo;
import ecommerce.productservice.client.OrderClient;
import ecommerce.productservice.dto.request.CreateRatingRequest;
import ecommerce.productservice.dto.response.RatingResponse;
import ecommerce.productservice.entity.Product;
import ecommerce.productservice.entity.Rating;
import ecommerce.productservice.repository.ProductRepository;
import ecommerce.productservice.repository.RatingRepository;
import ecommerce.productservice.service.RatingService;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final TokenInfo tokenInfo;
    private final ProductRepository productRepository;
    private final OrderClient orderClient;

    @Transactional
    @Override
    public RatingResponse createRating(CreateRatingRequest request) {
        Long userId = tokenInfo.getUserId();

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException("Không có sản phẩm"));

        boolean purchased = orderClient.hasPurchased(userId, request.getProductId());
        if (!purchased) {
            throw new ForbiddenException("Bạn cần mua sản phẩm này trước khi đánh giá");
        }

        boolean exists = ratingRepository.existsByUserIdAndProductId(userId, request.getProductId());
        if (exists) {
            throw new IllegalStateException("Bạn đã đánh giá sản phẩm này rồi");
        }

        Rating rating = Rating.builder()
                .userId(userId)
                .productId(request.getProductId())
                .score(request.getScore())
                .comments(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        ratingRepository.save(rating);

        Double avgScore = ratingRepository.getAverageScoreByProductId(rating.getProductId());
        Long totalReviews = ratingRepository.countByProductId(rating.getProductId());

        product.setAverageRating(avgScore);
        product.setTotalReviews(totalReviews);
        productRepository.save(product);

        return RatingResponse.builder()
                .userId(userId)
                .productId(rating.getProductId())
                .score(rating.getScore())
                .comment(rating.getComments())
                .createdDate(rating.getCreatedAt())
                .build();
    }
}

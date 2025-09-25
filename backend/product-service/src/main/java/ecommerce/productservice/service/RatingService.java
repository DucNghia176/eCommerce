package ecommerce.productservice.service;

import ecommerce.productservice.dto.request.CreateRatingRequest;
import ecommerce.productservice.dto.response.RatingResponse;

public interface RatingService {
    RatingResponse createRating(CreateRatingRequest request);

}

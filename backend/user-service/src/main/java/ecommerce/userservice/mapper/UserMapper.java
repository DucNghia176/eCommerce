package ecommerce.userservice.mapper;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserRequest;
import ecommerce.userservice.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(Users users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "1")
    @Mapping(target = "isLock", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    Users toEntity(UserRequest request);
}

package ecommerce.userservice.mapper;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;
import ecommerce.userservice.entity.UserAcc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAccMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userInfo", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isLock", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    UserAcc toEntity(UserCreateRequest userCreateRequest);

    UserCreateResponse toResponse(UserAcc userAcc);

    @Mapping(source = "userInfo.fullName", target = "fullName")
    @Mapping(source = "userInfo.avatar", target = "avatar")
    @Mapping(source = "userInfo.gender", target = "gender")
    @Mapping(source = "userInfo.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "userInfo.address", target = "address")
    @Mapping(source = "userInfo.phone", target = "phone")
    UserResponse toDto(UserAcc userAcc);
}

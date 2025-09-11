package ecommerce.userservice.mapper;

import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.entity.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userAcc", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    void updateUserInfoFromDto(UserInfoUpdateRequest dto, @MappingTarget UserInfo entity);

    @Mapping(source = "userAcc.username", target = "username")
    @Mapping(source = "userAcc.password", target = "password")
    @Mapping(source = "userAcc.email", target = "email")
    @Mapping(source = "userAcc.role", target = "role")
    @Mapping(source = "userAcc.isLock", target = "isLock")
    @Mapping(source = "userAcc.createdAt", target = "createdAt")
    @Mapping(source = "userAcc.updatedAt", target = "updatedAt")
    UserResponse toDto(UserInfo userInfo);
}

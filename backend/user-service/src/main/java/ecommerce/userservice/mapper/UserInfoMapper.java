package ecommerce.userservice.mapper;

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
}

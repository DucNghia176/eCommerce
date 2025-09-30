package ecommerce.userservice.mapper;

import ecommerce.apicommon1.model.response.UserResponse;
import ecommerce.userservice.dto.request.UserCreateRequest;
import ecommerce.userservice.dto.respone.UserCreateResponse;
import ecommerce.userservice.entity.UserAcc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserAccMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userInfo", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isLock", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "googleId", ignore = true)
    @Mapping(target = "facebookId", ignore = true)
    UserAcc toEntity(UserCreateRequest userCreateRequest);


    @Mapping(target = "role", expression = "java(convertRolesToString(userAcc))")
    UserCreateResponse toResponse(UserAcc userAcc);

    @Mapping(source = "userInfo.fullName", target = "fullName")
    @Mapping(source = "userInfo.avatar", target = "avatar")
    @Mapping(source = "userInfo.gender", target = "gender")
    @Mapping(source = "userInfo.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "userInfo.address", target = "address")
    @Mapping(source = "userInfo.phone", target = "phone")
    @Mapping(target = "roles", expression = "java(convertRolesToString(userAcc))")
    UserResponse toDto(UserAcc userAcc);

//    default List<String> mapRoles(Set<Role> roles) {
//        if (roles == null) return List.of();
//        return roles.stream()
//                .map(r -> r.getRoleName().name()) // Enum -> String
//                .toList();
//    }

    default String convertRolesToString(UserAcc userAcc) {
        if (userAcc.getRoles() == null) return null;
        return userAcc.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .collect(Collectors.joining(","));
    }
}

package ecommerce.userservice.service.impl;

import ecommerce.apicommon1.config.TokenInfo;
import ecommerce.apicommon1.model.response.UserResponse;
import ecommerce.apicommon1.model.status.GenderStatus;
import ecommerce.apicommon1.model.status.RoleStatus;
import ecommerce.userservice.client.OrderClient;
import ecommerce.userservice.client.PaymentClient;
import ecommerce.userservice.dto.request.AddRoleRequest;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.dto.respone.UserIdName;
import ecommerce.userservice.dto.respone.UserOrderDetail;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import ecommerce.userservice.entity.Role;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import ecommerce.userservice.mapper.UserAccMapper;
import ecommerce.userservice.mapper.UserInfoMapper;
import ecommerce.userservice.repository.*;
import ecommerce.userservice.service.CloudinaryService;
import ecommerce.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final CloudinaryService cloudinaryService;
    private final TokenInfo tokenInfo;
    private final UserInfoRepository userInfoRepository;
    private final OrderClient orderClient;
    private final PaymentClient paymentClient;
    private final Executor contextAwareExecutor;
    private final UserAccRepository userAccRepository;
    private final UserAccMapper userAccMapper;
    private final UserInfoMapper userInfoMapper;
    private final RoleRepository roleRepository;
    private final JDBC jDBC;
    private final JDBCNamed jDBCNamed;
    private final StoreRepo storeRepo;

    @Override
    public UserResponse getUserInfoById() {
        Long id = tokenInfo.getUserId();
        UserAcc users = userAccRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy user với id = " + id));
        return userAccMapper.toDto(users);
    }

    @Override
    public UserResponse updateUser(UserInfoUpdateRequest request, MultipartFile avatarFile) {
        Long id = tokenInfo.getUserId();
        UserAcc userAcc = userAccRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng với ID: " + id));

        UserInfo userInfo = userAcc.getUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUserAcc(userAcc);
            userAcc.setUserInfo(userInfo);
        }

        userInfoMapper.updateUserInfoFromDto(request, userInfo);
        // Xử lý ảnh đại diện
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String oldAvatarUrl = userInfo.getAvatar();
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                String publicId = cloudinaryService.extractPublicId(oldAvatarUrl);
                if (publicId != null) {
                    cloudinaryService.deleteFile(publicId);
                }
            }
            // Upload ảnh mới
            String avatarUrl = cloudinaryService.uploadFile(avatarFile);
            userInfo.setAvatar(avatarUrl);
        }

        UserAcc savedUserAcc = userAccRepository.save(userAcc);

        return userAccMapper.toDto(savedUserAcc);

    }

    @Override
    public UserResponse toggleUserLock(Long id) {
        UserAcc user = userAccRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng với ID: " + id));


        int newStatus = user.getIsLock() == 1 ? 0 : 1;
        user.setIsLock(newStatus);
        user.setUpdatedAt(LocalDateTime.now());

        return userAccMapper.toDto(userAccRepository.save(user));
    }

//    @Override
//    public ApiResponse<Page<UserResponse>> getAllUsers(int page, int size, Integer isLock) {
//        try {
//            Pageable pageable = PageRequest.of(page, size);
//            Page<UserAcc> usersPage;
//
//            // Nếu có lọc theo isLock
//            if (isLock == null) {
//                usersPage = userAccRepository.findAll(pageable);
//            } else {
//                usersPage = userAccRepository.findByIsLock(isLock, pageable);
//            }
//
//            Page<UserResponse> responses = usersPage.map(userAccMapper::toDto);
//
//            return ApiResponse.<Page<UserResponse>>builder()
//                    .code(200)
//                    .message("Lấy danh sách người dùng thành công")
//                    .data(responses)
//                    .build();
//
//        } catch (Exception e) {
//            log.error("Lỗi khi lấy danh sách người dùng: {}", e.getMessage(), e);
//            return ApiResponse.<Page<UserResponse>>builder()
//                    .code(500)
//                    .message("Đã xảy ra lỗi khi lấy danh sách người dùng")
//                    .data(null)
//                    .build();
//        }
//    }


//    public ApiResponse<CountResponse> count() {
//        Object[] result = (Object[]) userAccRepository.countUsersStatus();
//        Long all = ((Number) result[0]).longValue();
//        Long active = ((Number) result[1]).longValue();
//        Long inactive = ((Number) result[2]).longValue();
//
//        CountResponse count = CountResponse.builder()
//                .all(all)
//                .active(active)
//                .inactive(inactive)
//                .build();
//
//        return ApiResponse.<CountResponse>builder()
//                .code(200)
//                .message("Lấy dữ liệu thành công")
//                .data(count)
//                .build();
//    }

    @Override
    public Map<Long, String> extractIds(List<Long> ids) {
        List<UserIdName> users = userInfoRepository.findByIdIn(ids);

        return users.stream()
                .collect(Collectors.toMap(UserIdName::getId, UserIdName::getFullName));

    }

    @Override
    public Page<UserOrdersResponse> getUsersTOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserOrdersResponse> responses = userInfoRepository.findAllUsersIn(pageable);

        List<Long> userIds = responses.getContent()
                .stream()
                .map(UserOrdersResponse::getId)
                .toList();
        CompletableFuture<Map<Long, Long>> quantityFuture = CompletableFuture.supplyAsync(() ->
                orderClient.extractOrderQuantity(userIds), contextAwareExecutor);

        CompletableFuture<Map<Long, BigDecimal>> amountFuture = CompletableFuture.supplyAsync(() ->
                paymentClient.extractAmount(userIds), contextAwareExecutor);

        CompletableFuture.allOf(quantityFuture, amountFuture).join();

        Map<Long, Long> quantity = quantityFuture.join();
        Map<Long, BigDecimal> amount = amountFuture.join();

        responses.getContent().forEach(item -> {
            item.setTotalOrders(quantity.getOrDefault(item.getId(), 0L));
            item.setTotalAmount(amount.getOrDefault(item.getId(), BigDecimal.ZERO));
        });

        return responses;
    }

    @Override
    public UserOrderDetail getUserOrderDetail(Long id) {
        UserAcc user = userAccRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng với ID: " + id));

        long daysJoined = 0;
        if (user.getCreatedAt() != null) {
            daysJoined = ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDate.now());
        }

        return UserOrderDetail.builder()
                .id(id)
                .fullName(user.getUserInfo().getFullName())
                .email(user.getEmail())
                .avatar(user.getUserInfo().getAvatar())
                .phone(user.getUserInfo().getPhone())
                .address(user.getUserInfo().getAddress())
                .isLock(user.getIsLock())
                .daysJoined(daysJoined)
                .userOrderDetailResponse(orderClient.findOrdersDetailByUserId(id))
                .build();
    }

    @Override
    public UserResponse deleteUser(Long id) {
        UserAcc userAcc = userAccRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục với ID: " + id));

        userAcc.setIsActive(0);
        userAccRepository.save(userAcc);
        return userAccMapper.toDto(userAcc);
    }

    @Override
    public UserResponse addRoleToUser(AddRoleRequest request) {
        UserAcc userAcc = userAccRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng userId = " + request.getUserId()));


        List<Role> rolesToAdd = request.getRoleName().stream()
                .map(name -> {
                    RoleStatus roleStatus = RoleStatus.valueOf(name.toUpperCase());
                    return roleRepository.findByRoleName(roleStatus)
                            .orElseThrow(() -> new NoSuchElementException(
                                    "Role " + name + " không tồn tại"
                            ));
                })
                .toList();

        // Lọc ra những role user đã có
        List<Role> existingRoles = rolesToAdd.stream()
                .filter(userAcc.getRoles()::contains)
                .toList();

        if (!existingRoles.isEmpty()) {
            String roleNames = existingRoles.stream()
                    .map(r -> r.getRoleName().name())
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Người dùng đã có các role: " + roleNames
            );
        }

        userAcc.getRoles().addAll(rolesToAdd);
        userAccRepository.save(userAcc);

        return userAccMapper.toDto(userAcc);
    }

    @Override
    public List<UserResponse> searchUsersJPA(String name, GenderStatus gender, Integer isLock, String email) {
        List<UserAcc> users = userAccRepository.findAll();

        List<UserResponse> responses = users.stream().map(user -> {
            String roles = user.getRoles().stream()
                    .map(role -> role.getRoleName().toString()) // enum -> String
                    .collect(Collectors.joining(","));
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getUserInfo().getFullName(),
                    user.getEmail(),
                    roles,
                    user.getIsLock(),
                    user.getUserInfo().getAvatar(),
                    user.getUserInfo().getGender().toString(),
                    user.getUserInfo().getDateOfBirth(),
                    user.getUserInfo().getAddress(),
                    user.getUserInfo().getPhone(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }).collect(Collectors.toList());
        return responses;

    }

    @Override
    public List<UserResponse> searchUsersJDBC(String name, GenderStatus gender, Integer isLock, String email) {
        return jDBC.searchUsers(name, gender, isLock, email);
    }

    @Override
    public List<UserResponse> searchUsersJdbcNamed(String name, GenderStatus gender, Integer isLock, String email) {
        return jDBCNamed.searchUsers(name, gender, isLock, email);
    }

    @Override
    public List<UserResponse> searchUsersStore(String fullName, GenderStatus gender, Integer isLock, String email) {
        try {
            return storeRepo.searchUsers(fullName, gender, isLock, email);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi gọi stored procedure SP_SEARCH_USERS", e);
        }
    }

    @Override
    public List<UserResponse> searchUsersSpec(String fullName, GenderStatus gender, Integer isLock, String email) {
        List<UserAcc> users = userAccRepository.findAll(
                UserSpecification.searchUsers(fullName, gender, isLock, email)
        );

        return users.stream().map(u -> {
            String roles = u.getRoles().stream()
                    .map(r -> r.getRoleName().name()) // enum -> String
                    .collect(Collectors.joining(","));

            return new UserResponse(
                    u.getId(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getUserInfo().getFullName(),
                    u.getEmail(),
                    roles,
                    u.getIsLock(),
                    u.getUserInfo().getAvatar(),
                    u.getUserInfo().getGender().name(),
                    u.getUserInfo().getDateOfBirth(),
                    u.getUserInfo().getAddress(),
                    u.getUserInfo().getPhone(),
                    u.getCreatedAt(),
                    u.getUpdatedAt()
            );
        }).collect(Collectors.toList());
    }
}
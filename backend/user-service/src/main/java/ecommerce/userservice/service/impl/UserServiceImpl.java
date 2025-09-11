package ecommerce.userservice.service.impl;

import ecommerce.aipcommon.config.TokenInfo;
import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserResponse;
import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.userservice.client.OrderClient;
import ecommerce.userservice.client.PaymentClient;
import ecommerce.userservice.dto.request.UserInfoUpdateRequest;
import ecommerce.userservice.dto.respone.UserIdName;
import ecommerce.userservice.dto.respone.UserOrderDetail;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import ecommerce.userservice.entity.UserAcc;
import ecommerce.userservice.entity.UserInfo;
import ecommerce.userservice.mapper.UserAccMapper;
import ecommerce.userservice.mapper.UserInfoMapper;
import ecommerce.userservice.repository.UserAccRepository;
import ecommerce.userservice.repository.UserInfoRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Override
    public ApiResponse<UserResponse> getUserInfoById() {
        try {
            Long id = tokenInfo.getUserId();
            UserAcc users = userAccRepository.findById(id)
                    .orElse(null);

            if (users == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }
            UserResponse response = userAccMapper.toDto(users);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Lấy thông tin người dùng thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi lấy người dùng theo ID: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy thông tin người dùng")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> updateUser(UserInfoUpdateRequest request, MultipartFile avatarFile) {
        try {
            Long id = tokenInfo.getUserId();
            UserAcc userAcc = userAccRepository.findById(id)
                    .orElse(null);
            if (userAcc == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }

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

            UserResponse response = userAccMapper.toDto(savedUserAcc);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Cập nhật thành công ")
                    .data(response)
                    .build();
        } catch (IllegalArgumentException e) {
            return ApiResponse.<UserResponse>builder()
                    .code(400)
                    .message("Giới tính không hợp lệ. Chỉ được phép 'Nam' hoặc 'Nữ'")
                    .data(null)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống:")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> toggleUserLock(Long id) {
        try {
            UserAcc user = userAccRepository.findById(id).orElse(null);
            if (user == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }

            int newStatus = user.getIsLock() == 1 ? 0 : 1;
            user.setIsLock(newStatus);
            user.setUpdatedAt(LocalDateTime.now());

            UserResponse response = userAccMapper.toDto(userAccRepository.save(user));

            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message(newStatus == 1 ? "Tài khoản đã bị khóa" : "Tài khoản đã được mở khóa")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi đảo trạng thái khóa: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponse> toggleUserRole(Long id) {
        try {
            UserAcc userAcc = userAccRepository.findById(id).orElse(null);
            if (userAcc == null) {
                return ApiResponse.<UserResponse>builder()
                        .code(404)
                        .message("Không tìm thấy người dùng với ID: " + id)
                        .data(null)
                        .build();
            }

            RoleStatus newRole = userAcc.getRole() == RoleStatus.ADMIN
                    ? RoleStatus.USER
                    : RoleStatus.ADMIN;

            userAcc.setRole(newRole);
            UserResponse response = userAccMapper.toDto(userAccRepository.save(userAcc));
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Cập nhật quyền thành: " + newRole)
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi chuyển quyền: {}", e.getMessage(), e);
            return ApiResponse.<UserResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống")
                    .data(null)
                    .build();
        }
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
    public ApiResponse<Page<UserOrdersResponse>> getUsersTOrders(int page, int size) {
        try {
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

            return ApiResponse.<Page<UserOrdersResponse>>builder()
                    .code(200)
                    .message("Lấy thành công")
                    .data(responses)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Page<UserOrdersResponse>>builder()
                    .code(500)
                    .message("Đã xảy ra lỗi khi lấy danh sách người dùng" + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<UserOrderDetail> getUserOrderDetail(Long id) {
        try {
            UserAcc user = userAccRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Không tìm thấy user " + id));

            UserOrderDetail response = UserOrderDetail.builder()
                    .id(id)
                    .fullName(user.getUserInfo().getFullName())
                    .email(user.getEmail())
                    .phone(user.getUserInfo().getPhone())
                    .address(user.getUserInfo().getAddress())
                    .isLock(user.getIsLock())
                    .userOrderDetailResponse(orderClient.findOrdersDetailByUserId(id))
                    .build();

            return ApiResponse.<UserOrderDetail>builder()
                    .code(200)
                    .data(response)
                    .message("Thành công")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<UserOrderDetail>builder()
                    .code(500)
                    .data(null)
                    .message("Lỗi hệ thống " + e.getMessage())
                    .build();
        }
    }
}
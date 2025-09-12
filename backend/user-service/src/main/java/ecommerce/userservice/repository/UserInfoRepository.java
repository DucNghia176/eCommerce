package ecommerce.userservice.repository;

import ecommerce.userservice.dto.respone.UserIdName;
import ecommerce.userservice.dto.respone.UserOrdersResponse;
import ecommerce.userservice.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    @Query("""
            select new ecommerce.userservice.dto.respone.UserOrdersResponse(ui.id, ui.fullName, ui.avatar, ui.address, ua.isLock)
            from UserInfo ui
            join ui.userAcc ua
            where ua.role = 'USER' AND ua.isActive = 1
            """)
    Page<UserOrdersResponse> findAllUsersIn(Pageable pageable);

    List<UserIdName> findByIdIn(List<Long> id);

}

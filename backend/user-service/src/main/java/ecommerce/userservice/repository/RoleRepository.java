package ecommerce.userservice.repository;

import ecommerce.aipcommon.model.status.RoleStatus;
import ecommerce.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleStatus roleName);
}

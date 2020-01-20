package com.example.polls.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.polls.entity.Role;
import com.example.polls.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
  Optional<Role> findByName(RoleName roleName);
}

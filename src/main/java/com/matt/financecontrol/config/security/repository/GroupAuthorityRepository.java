package com.matt.financecontrol.config.security.repository;

import com.matt.financecontrol.config.security.entity.GroupAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupAuthorityRepository extends JpaRepository<GroupAuthority, UUID>, JpaSpecificationExecutor<GroupAuthority> {

}

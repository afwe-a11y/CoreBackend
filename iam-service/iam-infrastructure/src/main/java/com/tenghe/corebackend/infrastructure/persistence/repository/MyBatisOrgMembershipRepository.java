package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.mapper.UserOrgMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.UserOrgPo;
import com.tenghe.corebackend.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.interfaces.OrgMembershipRepositoryPort;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisOrgMembershipRepository implements OrgMembershipRepositoryPort {
    private static final String IDENTITY_INTERNAL = "INTERNAL";
    private static final String STATUS_NORMAL = "NORMAL";

    private final UserOrgMapper userOrgMapper;
    private final IdGeneratorPort idGenerator;

    public MyBatisOrgMembershipRepository(UserOrgMapper userOrgMapper, IdGeneratorPort idGenerator) {
        this.userOrgMapper = userOrgMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    public void addMembership(Long organizationId, Long userId) {
        if (exists(organizationId, userId)) {
            return;
        }
        UserOrgPo membership = new UserOrgPo();
        membership.setId(idGenerator.nextId());
        membership.setOrgId(organizationId);
        membership.setUserId(userId);
        membership.setIdentityType(IDENTITY_INTERNAL);
        membership.setStatus(STATUS_NORMAL);
        membership.setJoinTime(Instant.now());
        membership.setCreateTime(Instant.now());
        membership.setDeleted(0);
        userOrgMapper.insert(membership);
    }

    @Override
    public boolean exists(Long organizationId, Long userId) {
        return userOrgMapper.exists(organizationId, userId, IDENTITY_INTERNAL) > 0;
    }

    @Override
    public List<Long> listUserIdsByOrganizationId(Long organizationId) {
        return userOrgMapper.listUserIdsByOrgId(organizationId, IDENTITY_INTERNAL);
    }

    @Override
    public List<Long> listOrganizationIdsByUserId(Long userId) {
        return userOrgMapper.listOrgIdsByUserId(userId, IDENTITY_INTERNAL);
    }

    @Override
    public void softDeleteByOrganizationId(Long organizationId) {
        userOrgMapper.softDeleteByOrgId(organizationId, IDENTITY_INTERNAL);
    }

    @Override
    public void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId) {
        userOrgMapper.softDeleteByOrgIdAndUserId(organizationId, userId, IDENTITY_INTERNAL);
    }

    @Override
    public long countByOrganizationId(Long organizationId) {
        return userOrgMapper.countByOrgId(organizationId, IDENTITY_INTERNAL);
    }
}

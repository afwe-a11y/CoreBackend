package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.mapper.UserOrgMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.UserOrgPo;
import com.tenghe.corebackend.interfaces.ExternalMembershipRepositoryPort;
import com.tenghe.corebackend.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.model.ExternalMembership;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisExternalMembershipRepository implements ExternalMembershipRepositoryPort {
    private static final String IDENTITY_EXTERNAL = "EXTERNAL";
    private static final String STATUS_NORMAL = "NORMAL";

    private final UserOrgMapper userOrgMapper;
    private final IdGeneratorPort idGenerator;

    public MyBatisExternalMembershipRepository(UserOrgMapper userOrgMapper, IdGeneratorPort idGenerator) {
        this.userOrgMapper = userOrgMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    public void addExternalMembership(Long organizationId, Long userId, Long sourceOrganizationId) {
        if (exists(organizationId, userId)) {
            return;
        }
        UserOrgPo membership = new UserOrgPo();
        membership.setId(idGenerator.nextId());
        membership.setOrgId(organizationId);
        membership.setUserId(userId);
        membership.setIdentityType(IDENTITY_EXTERNAL);
        membership.setStatus(STATUS_NORMAL);
        membership.setJoinTime(Instant.now());
        membership.setCreateTime(Instant.now());
        membership.setDeleted(0);
        userOrgMapper.insert(membership);
    }

    @Override
    public boolean exists(Long organizationId, Long userId) {
        return userOrgMapper.exists(organizationId, userId, IDENTITY_EXTERNAL) > 0;
    }

    @Override
    public ExternalMembership findActiveByUserId(Long userId) {
        return toModel(userOrgMapper.findExternalByUserId(userId));
    }

    @Override
    public List<ExternalMembership> listByOrganizationId(Long organizationId) {
        return userOrgMapper.listExternalByOrgId(organizationId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteByOrganizationId(Long organizationId) {
        userOrgMapper.softDeleteByOrgId(organizationId, IDENTITY_EXTERNAL);
    }

    @Override
    public void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId) {
        userOrgMapper.softDeleteByOrgIdAndUserId(organizationId, userId, IDENTITY_EXTERNAL);
    }

    @Override
    public long countByOrganizationId(Long organizationId) {
        return userOrgMapper.countByOrgId(organizationId, IDENTITY_EXTERNAL);
    }

    private ExternalMembership toModel(UserOrgPo po) {
        if (po == null) {
            return null;
        }
        ExternalMembership membership = new ExternalMembership();
        membership.setOrganizationId(po.getOrgId());
        membership.setUserId(po.getUserId());
        membership.setSourceOrganizationId(po.getSourceOrgId());
        membership.setCreatedAt(po.getJoinTime());
        membership.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        return membership;
    }
}

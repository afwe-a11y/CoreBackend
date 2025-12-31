package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.infrastructure.persistence.mapper.OrganizationMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.OrganizationPo;
import com.tenghe.corebackend.interfaces.OrganizationRepositoryPort;
import com.tenghe.corebackend.model.Organization;
import com.tenghe.corebackend.model.enums.OrganizationStatusEnum;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisOrganizationRepository implements OrganizationRepositoryPort {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRIMARY_ADMIN = "primaryAdminDisplay";
    private static final String KEY_CONTACT_NAME = "contactName";
    private static final String KEY_CONTACT_PHONE = "contactPhone";
    private static final String KEY_CONTACT_EMAIL = "contactEmail";

    private final OrganizationMapper organizationMapper;
    private final JsonCodec jsonCodec;

    public MyBatisOrganizationRepository(OrganizationMapper organizationMapper, JsonCodec jsonCodec) {
        this.organizationMapper = organizationMapper;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationPo po = toPo(organization);
        organizationMapper.insert(po);
        return organization;
    }

    @Override
    public Organization update(Organization organization) {
        OrganizationPo po = toPo(organization);
        organizationMapper.update(po);
        return organization;
    }

    @Override
    public Organization findById(Long id) {
        return toModel(organizationMapper.findById(id));
    }

    @Override
    public Organization findByName(String name) {
        return toModel(organizationMapper.findByName(name));
    }

    @Override
    public Organization findByCode(String code) {
        return toModel(organizationMapper.findByCode(code));
    }

    @Override
    public List<Organization> listAll() {
        return organizationMapper.listAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private Organization toModel(OrganizationPo po) {
        if (po == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(po.getId());
        organization.setName(po.getOrgName());
        organization.setCode(po.getOrgCode());
        organization.setStatus(OrganizationStatusEnum.fromValue(po.getStatus()));
        organization.setCreatedAt(po.getCreateTime());
        organization.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        applyDescription(organization, po.getDescription());
        return organization;
    }

    private OrganizationPo toPo(Organization organization) {
        OrganizationPo po = new OrganizationPo();
        po.setId(organization.getId());
        po.setOrgName(organization.getName());
        po.setOrgCode(organization.getCode());
        po.setStatus(organization.getStatus() == null ? null : organization.getStatus().name());
        po.setCreateTime(organization.getCreatedAt());
        po.setDeleted(organization.isDeleted() ? 1 : 0);
        po.setDescription(encodeDescription(organization));
        return po;
    }

    private void applyDescription(Organization organization, String rawDescription) {
        Map<String, Object> payload = jsonCodec.readMap(rawDescription);
        if (payload.isEmpty()) {
            organization.setDescription(rawDescription);
            return;
        }
        organization.setDescription(stringValue(payload.get(KEY_DESCRIPTION)));
        organization.setPrimaryAdminDisplay(stringValue(payload.get(KEY_PRIMARY_ADMIN)));
        organization.setContactName(stringValue(payload.get(KEY_CONTACT_NAME)));
        organization.setContactPhone(stringValue(payload.get(KEY_CONTACT_PHONE)));
        organization.setContactEmail(stringValue(payload.get(KEY_CONTACT_EMAIL)));
    }

    private String encodeDescription(Organization organization) {
        boolean hasExtras = organization.getPrimaryAdminDisplay() != null
                || organization.getContactName() != null
                || organization.getContactPhone() != null
                || organization.getContactEmail() != null;
        if (!hasExtras) {
            return organization.getDescription();
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        if (organization.getDescription() != null) {
            payload.put(KEY_DESCRIPTION, organization.getDescription());
        }
        if (organization.getPrimaryAdminDisplay() != null) {
            payload.put(KEY_PRIMARY_ADMIN, organization.getPrimaryAdminDisplay());
        }
        if (organization.getContactName() != null) {
            payload.put(KEY_CONTACT_NAME, organization.getContactName());
        }
        if (organization.getContactPhone() != null) {
            payload.put(KEY_CONTACT_PHONE, organization.getContactPhone());
        }
        if (organization.getContactEmail() != null) {
            payload.put(KEY_CONTACT_EMAIL, organization.getContactEmail());
        }
        return jsonCodec.writeValue(payload);
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}

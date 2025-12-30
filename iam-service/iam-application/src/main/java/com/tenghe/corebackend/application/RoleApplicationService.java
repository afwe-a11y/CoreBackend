package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.BatchRoleMemberCommand;
import com.tenghe.corebackend.application.command.ConfigureRolePermissionsCommand;
import com.tenghe.corebackend.application.command.CreateRoleCommand;
import com.tenghe.corebackend.application.command.UpdateRoleCommand;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.RoleDetailResult;
import com.tenghe.corebackend.application.service.result.RoleListItemResult;
import com.tenghe.corebackend.application.service.result.RoleMemberResult;
import java.util.List;

public interface RoleApplicationService {

    PageResult<RoleListItemResult> listRoles(Long appId, String keyword, Integer page, Integer size);

    Long createRole(CreateRoleCommand command);

    RoleDetailResult getRoleDetail(Long roleId);

    void updateRole(UpdateRoleCommand command);

    void deleteRole(Long roleId);

    List<Long> getRolePermissions(Long roleId);

    void configureRolePermissions(ConfigureRolePermissionsCommand command);

    PageResult<RoleMemberResult> listRoleMembers(Long roleId, Long organizationId, String keyword, Integer page, Integer size);

    void batchAddMembers(BatchRoleMemberCommand command);

    void batchRemoveMembers(BatchRoleMemberCommand command);
}

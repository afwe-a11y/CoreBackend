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

/**
 * 角色管理应用服务接口
 * <p>
 * 提供角色的增删改查、权限配置、成员管理等功能。
 * 角色属于应用，聚合一组权限。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>角色按所属应用分组</li>
 *   <li>角色名称：必填，同一应用内唯一</li>
 *   <li>角色编码：必填，全局唯一</li>
 *   <li>预置角色（如"超级管理员"、"组织管理员"）：名称不可编辑，状态不可切换</li>
 *   <li>删除角色：如果角色已分配给用户，必须先移除成员才能删除</li>
 *   <li>删除：仅支持软删除</li>
 * </ul>
 */
public interface RoleApplicationService {

    /**
     * 分页查询角色列表
     * <p>
     * 按应用分组查询角色，支持关键词筛选。
     * </p>
     * 
     * @param appId 应用ID（必填）
     * @param keyword 搜索关键词（角色名称/编码模糊匹配）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含角色ID、名称、编码、描述、是否预置、创建时间
     */
    PageResult<RoleListItemResult> listRoles(Long appId, String keyword, Integer page, Integer size);

    /**
     * 创建角色
     * 
     * @param command 创建命令，包含应用ID、角色名称、角色编码、描述
     * @return 新创建的角色ID
     * @throws BusinessException 应用不存在、角色名称在应用内已存在、角色编码已存在
     */
    Long createRole(CreateRoleCommand command);

    /**
     * 获取角色详情
     * 
     * @param roleId 角色ID
     * @return 角色详情，包含基本信息、所属应用、已配置的权限ID列表
     * @throws BusinessException 角色不存在
     */
    RoleDetailResult getRoleDetail(Long roleId);

    /**
     * 更新角色信息
     * <p>
     * 预置角色的名称不可修改。
     * </p>
     * 
     * @param command 更新命令，包含角色ID、角色名称、描述
     * @throws BusinessException 角色不存在、预置角色不可修改名称、角色名称在应用内已存在
     */
    void updateRole(UpdateRoleCommand command);

    /**
     * 删除角色（软删除）
     * <p>
     * 如果角色已分配给用户，必须先移除所有成员才能删除。
     * 预置角色不可删除。
     * </p>
     * 
     * @param roleId 角色ID
     * @throws BusinessException 角色不存在、预置角色不可删除、角色下存在成员
     */
    void deleteRole(Long roleId);

    /**
     * 配置角色权限
     * <p>
     * 权限树数据源必须是角色所属应用的"包含权限子集"，而非全局权限集。
     * </p>
     * 
     * @param command 配置命令，包含角色ID、权限ID列表
     * @throws BusinessException 角色不存在、权限不在应用的包含权限范围内
     */
    void configureRolePermissions(ConfigureRolePermissionsCommand command);

    /**
     * 分页查询角色成员列表
     * <p>
     * 查询拥有该角色的用户列表，按组织筛选。
     * </p>
     * 
     * @param roleId 角色ID
     * @param organizationId 组织ID（必填）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含用户ID、用户名、姓名
     * @throws BusinessException 角色不存在
     */
    PageResult<RoleMemberResult> listRoleMembers(Long roleId, Long organizationId, Integer page, Integer size);

    /**
     * 批量添加角色成员
     * <p>
     * 为指定角色批量添加用户。候选用户范围受操作者权限约束。
     * </p>
     * 
     * @param command 批量命令，包含角色ID、组织ID、用户ID列表
     * @throws BusinessException 角色不存在、组织不存在、用户不存在
     */
    void batchAddMembers(BatchRoleMemberCommand command);

    /**
     * 批量移除角色成员
     * <p>
     * 从指定角色批量移除用户。
     * </p>
     * 
     * @param command 批量命令，包含角色ID、组织ID、用户ID列表
     * @throws BusinessException 角色不存在、组织不存在
     */
    void batchRemoveMembers(BatchRoleMemberCommand command);
}

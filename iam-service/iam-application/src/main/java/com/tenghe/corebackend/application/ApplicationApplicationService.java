package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateApplicationCommand;
import com.tenghe.corebackend.application.command.UpdateApplicationCommand;
import com.tenghe.corebackend.application.service.result.ApplicationDetailResult;
import com.tenghe.corebackend.application.service.result.ApplicationListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;

/**
 * 应用管理应用服务接口
 * <p>
 * 提供应用的增删改查功能。
 * 应用定义了"包含权限集"，组织可配置使用哪些应用，角色属于应用。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>应用名称：必填</li>
 *   <li>应用编码：必填，全局唯一</li>
 *   <li>包含权限：必填，至少选择1个权限（从全局权限集中选择）</li>
 *   <li>编辑风险：移除已分配给角色的权限时，需阻止移除并返回明确提示</li>
 *   <li>删除应用：如果应用下存在角色，必须先删除角色才能删除应用</li>
 *   <li>删除：仅支持软删除</li>
 * </ul>
 */
public interface ApplicationApplicationService {

    /**
     * 分页查询应用列表
     * <p>
     * 支持按应用名称/编码关键词筛选。
     * </p>
     * 
     * @param keyword 搜索关键词（应用名称/编码模糊匹配）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含应用ID、名称、编码、描述、状态、创建时间
     */
    PageResult<ApplicationListItemResult> listApplications(String keyword, Integer page, Integer size);

    /**
     * 创建应用
     * 
     * @param command 创建命令，包含应用名称、应用编码、描述、包含权限ID列表
     * @return 新创建的应用ID
     * @throws BusinessException 应用编码已存在、包含权限为空
     */
    Long createApplication(CreateApplicationCommand command);

    /**
     * 获取应用详情
     * 
     * @param applicationId 应用ID
     * @return 应用详情，包含基本信息、包含权限ID列表
     * @throws BusinessException 应用不存在
     */
    ApplicationDetailResult getApplicationDetail(Long applicationId);

    /**
     * 更新应用信息
     * <p>
     * 移除权限时的风险规则：如果被移除的权限已分配给应用下的角色，需阻止移除。
     * </p>
     * 
     * @param command 更新命令，包含应用ID、应用名称、描述、状态、包含权限ID列表
     * @throws BusinessException 应用不存在、移除的权限已分配给角色
     */
    void updateApplication(UpdateApplicationCommand command);

    /**
     * 删除应用（软删除）
     * <p>
     * 如果应用下存在角色，必须先删除所有角色才能删除应用。
     * </p>
     * 
     * @param applicationId 应用ID
     * @throws BusinessException 应用不存在、应用下存在角色
     */
    void deleteApplication(Long applicationId);
}

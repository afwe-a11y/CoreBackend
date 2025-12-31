package com.tenghe.corebackend.iam.application;

import com.tenghe.corebackend.iam.application.command.AssignAdminCommand;
import com.tenghe.corebackend.iam.application.command.CreateOrganizationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateOrganizationCommand;
import com.tenghe.corebackend.iam.application.service.result.DeleteOrganizationInfoResult;
import com.tenghe.corebackend.iam.application.service.result.OrganizationDetailResult;
import com.tenghe.corebackend.iam.application.service.result.OrganizationListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.service.result.UserSummaryResult;
import java.util.List;

/**
 * 组织管理应用服务接口
 * <p>
 * 提供组织的增删改查、管理员指派等功能。
 * 组织是数据边界，定义了可使用的应用范围。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>组织名称：必填，1-50字符，全局唯一（包括所有状态）</li>
 *   <li>组织编码：必填，全局唯一，仅允许字母/数字/下划线</li>
 *   <li>描述：选填，创建时最大200字符，编辑时最大400字符</li>
 *   <li>可使用应用：组织可配置0..N个可使用的应用</li>
 *   <li>联系人：选填，手机号需11位格式，邮箱需有效格式</li>
 *   <li>删除：仅支持软删除，需先处理组织下的成员</li>
 * </ul>
 */
public interface OrganizationApplicationService {

    /**
     * 分页查询组织列表
     * <p>
     * 支持按组织名称/编码关键词筛选。
     * </p>
     * 
     * @param keyword 搜索关键词（组织名称/编码模糊匹配）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含组织ID、名称、编码、管理员、创建时间、状态
     */
    PageResult<OrganizationListItemResult> listOrganizations(String keyword, Integer page, Integer size);

    /**
     * 创建组织
     * 
     * @param command 创建命令，包含名称、编码、描述、可使用应用ID列表
     * @return 新创建的组织ID（雪花算法生成，19-21位）
     * @throws BusinessException 组织名称已存在、组织编码已存在、编码格式不合法
     */
    Long createOrganization(CreateOrganizationCommand command);

    /**
     * 获取组织详情
     * 
     * @param organizationId 组织ID
     * @return 组织详情，包含基本信息、可使用应用、联系人信息
     * @throws BusinessException 组织不存在
     */
    OrganizationDetailResult getOrganizationDetail(Long organizationId);

    /**
     * 更新组织信息
     * <p>
     * 移除应用时的风险规则：如果被移除的应用下有用户已授权的角色，需阻止移除或执行清理策略。
     * </p>
     * 
     * @param command 更新命令，包含组织ID、名称、描述、可使用应用、状态、联系人信息
     * @throws BusinessException 组织不存在、名称已被占用、移除的应用下存在已授权角色
     */
    void updateOrganization(UpdateOrganizationCommand command);

    /**
     * 删除组织（软删除）
     * <p>
     * 执行逻辑删除，同时清理组织下的成员关系和角色授权。
     * </p>
     * 
     * @param organizationId 组织ID
     * @throws BusinessException 组织不存在
     */
    void deleteOrganization(Long organizationId);

    /**
     * 获取删除前的确认信息
     * <p>
     * 返回组织名称和关联的用户数量，供前端展示删除确认提示。
     * </p>
     * 
     * @param organizationId 组织ID
     * @return 删除信息，包含组织名称、用户数量
     * @throws BusinessException 组织不存在
     */
    DeleteOrganizationInfoResult getDeleteInfo(Long organizationId);

    /**
     * 搜索管理员候选人
     * <p>
     * 从平台用户中搜索可作为组织管理员的候选人。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param keyword 搜索关键词（用户名/姓名模糊匹配）
     * @return 候选用户列表
     * @throws BusinessException 组织不存在
     */
    List<UserSummaryResult> searchAdminCandidates(Long organizationId, String keyword);

    /**
     * 指派组织管理员
     * <p>
     * 选择一个已存在的平台用户，授予其组织管理员角色。
     * 管理员名称将显示在组织列表中。
     * </p>
     * 
     * @param command 指派命令，包含组织ID、用户ID
     * @throws BusinessException 组织不存在、用户不存在、管理员名称不合法
     */
    void assignAdmin(AssignAdminCommand command);
}

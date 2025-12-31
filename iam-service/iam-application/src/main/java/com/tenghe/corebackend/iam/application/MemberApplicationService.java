package com.tenghe.corebackend.iam.application;

import com.tenghe.corebackend.iam.application.command.CreateInternalMemberCommand;
import com.tenghe.corebackend.iam.application.command.LinkExternalMemberCommand;
import com.tenghe.corebackend.iam.application.command.UpdateInternalMemberCommand;
import com.tenghe.corebackend.iam.application.service.result.CreateInternalMemberResult;
import com.tenghe.corebackend.iam.application.service.result.ExternalMemberListItemResult;
import com.tenghe.corebackend.iam.application.service.result.InternalMemberListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.service.result.UserSummaryResult;
import java.util.List;

/**
 * 组织成员管理应用服务接口
 * <p>
 * 提供组织内部成员和外部成员的管理功能。
 * </p>
 * 
 * <h3>成员类型</h3>
 * <ul>
 *   <li>内部成员：属于该组织的用户账号，可在此创建/编辑/删除</li>
 *   <li>外部成员：属于其他组织的用户账号，关联到当前组织以获取访问权限</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>内部成员创建时自动生成初始密码并发送邮件</li>
 *   <li>外部成员仅支持关联/取消关联，不能修改其基本信息</li>
 *   <li>删除成员时同时清理该成员在组织下的角色授权</li>
 * </ul>
 */
public interface MemberApplicationService {

    /**
     * 分页查询内部成员列表
     * 
     * @param organizationId 组织ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含成员ID、用户名、姓名、手机、邮箱、账号类型、角色、状态
     * @throws BusinessException 组织不存在
     */
    PageResult<InternalMemberListItemResult> listInternalMembers(Long organizationId, Integer page, Integer size);

    /**
     * 创建内部成员
     * <p>
     * 在组织内创建新的用户账号，系统自动生成初始密码并通过邮件发送。
     * </p>
     * 
     * @param command 创建命令，包含组织ID、用户名、姓名、手机、邮箱、账号类型、角色选择
     * @return 创建结果，包含用户ID和初始密码
     * @throws BusinessException 组织不存在、用户名已存在、邮箱已存在
     */
    CreateInternalMemberResult createInternalMember(CreateInternalMemberCommand command);

    /**
     * 更新内部成员信息
     * <p>
     * 用户名不可修改，可修改姓名、手机、邮箱、账号类型、角色等。
     * </p>
     * 
     * @param command 更新命令，包含组织ID、用户ID、姓名、手机、邮箱、账号类型、角色选择
     * @throws BusinessException 组织不存在、用户不存在、用户不属于该组织、邮箱已被占用
     */
    void updateInternalMember(UpdateInternalMemberCommand command);

    /**
     * 禁用内部成员
     * <p>
     * 将成员状态设为禁用，禁用后该成员无法登录和访问系统。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param userId 用户ID
     * @throws BusinessException 组织不存在、用户不存在、用户不属于该组织
     */
    void disableInternalMember(Long organizationId, Long userId);

    /**
     * 删除内部成员（软删除）
     * <p>
     * 执行逻辑删除，同时清理该成员在组织下的角色授权。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param userId 用户ID
     * @throws BusinessException 组织不存在、用户不存在、用户不属于该组织
     */
    void deleteInternalMember(Long organizationId, Long userId);

    /**
     * 分页查询外部成员列表
     * 
     * @param organizationId 组织ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果，包含成员ID、用户名、姓名、手机、邮箱、所属组织名称
     * @throws BusinessException 组织不存在
     */
    PageResult<ExternalMemberListItemResult> listExternalMembers(Long organizationId, Integer page, Integer size);

    /**
     * 搜索外部成员候选人
     * <p>
     * 从其他组织的用户中搜索可关联为外部成员的候选人。
     * 已关联的用户不会出现在候选列表中。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param keyword 搜索关键词（用户名/姓名模糊匹配）
     * @return 候选用户列表
     * @throws BusinessException 组织不存在
     */
    List<UserSummaryResult> searchExternalCandidates(Long organizationId, String keyword);

    /**
     * 关联外部成员
     * <p>
     * 将其他组织的用户关联到当前组织，使其可以访问当前组织的资源。
     * </p>
     * 
     * @param command 关联命令，包含组织ID、用户ID、角色选择
     * @throws BusinessException 组织不存在、用户不存在、用户已关联
     */
    void linkExternalMember(LinkExternalMemberCommand command);

    /**
     * 取消关联外部成员
     * <p>
     * 移除外部成员与当前组织的关联关系，同时清理其在组织下的角色授权。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param userId 用户ID
     * @throws BusinessException 组织不存在
     */
    void unlinkExternalMember(Long organizationId, Long userId);
}

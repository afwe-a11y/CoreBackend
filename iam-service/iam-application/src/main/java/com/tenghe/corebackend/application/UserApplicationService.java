package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateUserCommand;
import com.tenghe.corebackend.application.command.UpdateUserCommand;
import com.tenghe.corebackend.application.command.UserQueryCommand;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserDetailResult;
import com.tenghe.corebackend.application.service.result.UserListItemResult;

/**
 * 用户管理应用服务接口
 * <p>
 * 提供用户的增删改查、状态管理等功能。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>用户名：必填，≤20字符，全局唯一，仅允许字母+数字</li>
 *   <li>邮箱：必填，有效格式，全局唯一</li>
 *   <li>手机号：选填，有效格式</li>
 *   <li>组织：必填，支持多选</li>
 *   <li>角色：必填，角色选择依赖于已选组织可使用的应用</li>
 *   <li>创建成功后：系统通过邮件发送初始密码</li>
 *   <li>删除：仅支持软删除（逻辑删除）</li>
 * </ul>
 */
public interface UserApplicationService {

    /**
     * 分页查询用户列表
     * <p>
     * 支持按用户名/ID、状态、组织、角色等条件筛选。
     * </p>
     * 
     * @param query 查询条件，包含关键词、状态、组织ID列表、角色编码列表、分页参数
     * @return 分页结果，包含用户ID、用户名、姓名、手机、邮箱、关联组织、角色、创建时间、状态
     */
    PageResult<UserListItemResult> listUsers(UserQueryCommand query);

    /**
     * 创建用户
     * <p>
     * 创建新用户账号，系统自动生成初始密码并通过邮件发送给用户。
     * </p>
     * 
     * @param command 创建命令，包含用户名、姓名、手机、邮箱、组织ID列表、角色选择列表
     * @return 新创建的用户ID
     * @throws BusinessException 用户名已存在、邮箱已存在、组织不存在、角色不存在
     */
    Long createUser(CreateUserCommand command);

    /**
     * 获取用户详情
     * 
     * @param userId 用户ID
     * @return 用户详情，包含基本信息、关联组织、角色选择
     * @throws BusinessException 用户不存在
     */
    UserDetailResult getUserDetail(Long userId);

    /**
     * 更新用户信息
     * <p>
     * 用户名不可修改。组织变更时，自动移除被移除组织下的所有角色授权。
     * </p>
     * 
     * @param command 更新命令，包含用户ID、姓名、手机、邮箱、组织ID列表、角色选择列表
     * @throws BusinessException 用户不存在、邮箱已被占用
     */
    void updateUser(UpdateUserCommand command);

    /**
     * 切换用户状态（启用/禁用）
     * <p>
     * 禁用用户后，该用户将被全局拦截，无法访问任何功能。
     * </p>
     * 
     * @param userId 用户ID
     * @param status 目标状态：NORMAL(正常) / DISABLED(停用)
     * @throws BusinessException 用户不存在、无效的状态值
     */
    void toggleUserStatus(Long userId, String status);

    /**
     * 删除用户（软删除）
     * <p>
     * 执行逻辑删除，同时清理用户的组织成员关系和角色授权。
     * </p>
     * 
     * @param userId 用户ID
     * @throws BusinessException 用户不存在
     */
    void deleteUser(Long userId);
}

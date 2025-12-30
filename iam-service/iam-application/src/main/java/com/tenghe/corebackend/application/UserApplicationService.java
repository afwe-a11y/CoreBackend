package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateUserCommand;
import com.tenghe.corebackend.application.command.UpdateUserCommand;
import com.tenghe.corebackend.application.command.UserQueryCommand;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserDetailResult;
import com.tenghe.corebackend.application.service.result.UserListItemResult;

public interface UserApplicationService {

    PageResult<UserListItemResult> listUsers(UserQueryCommand query);

    Long createUser(CreateUserCommand command);

    UserDetailResult getUserDetail(Long userId);

    void updateUser(UpdateUserCommand command);

    void toggleUserStatus(Long userId, String status);

    void deleteUser(Long userId);

    void resetPassword(Long userId);
}

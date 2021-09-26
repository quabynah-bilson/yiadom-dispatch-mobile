package com.yiadom.dispatch.repository.user;

import com.yiadom.dispatch.model.BaseUser;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.repository.RepositoryCallback;

public interface BaseUserRepository {

    void createUser(BaseUser user, UserType type, RepositoryCallback<String> callback);
}

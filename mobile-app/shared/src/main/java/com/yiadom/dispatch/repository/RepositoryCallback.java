package com.yiadom.dispatch.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * callback interface for repositories
 *
 * @param <T> is the desired output
 */
public interface RepositoryCallback<T> {
    public void success(@Nullable T data);

    public void loading();

    public void error(@NonNull String message);

}

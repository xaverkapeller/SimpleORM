package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.entities.RemoveParameters;
import com.github.wrdlbrnft.simpleorm.Remover;
import com.github.wrdlbrnft.simpleorm.entities.SaveParameters;
import com.github.wrdlbrnft.simpleorm.Saver;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
interface TransactionResolver<T> {
    Saver<T> commit(SaveParameters<T> parameters);
    Remover<T> commit(RemoveParameters<T> parameters);
}

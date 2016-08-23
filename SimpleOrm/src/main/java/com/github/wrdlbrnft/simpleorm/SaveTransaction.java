package com.github.wrdlbrnft.simpleorm;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface SaveTransaction<T> {
    SaveTransaction<T> entity(T entity);
    SaveTransaction<T> entities(List<T> entity);
    Saver<T> commit();
}

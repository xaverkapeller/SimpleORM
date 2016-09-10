package com.github.wrdlbrnft.simpleorm.processor.builder.openhelper;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/09/16
 */
interface CreateQueries {
    List<Query> getTableQueries();
    List<Query> getTriggerQueries();
}

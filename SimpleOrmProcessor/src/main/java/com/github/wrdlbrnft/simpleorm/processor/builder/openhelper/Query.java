package com.github.wrdlbrnft.simpleorm.processor.builder.openhelper;

import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.variables.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/09/16
 */
interface Query {
    CodeElement execute(Variable manager);
}

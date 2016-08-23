package com.github.wrdlbrnft.simpleorm.processor.builder.entityremover;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/07/16
 */

public class EntityRemoverBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public EntityRemoverBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(EntityInfo entityInfo) {
        final Type entityType = Types.of(entityInfo.getEntityElement());

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setExtendedType(Types.generic(SimpleOrmTypes.ENTITY_REMOVER.asType(), entityType));

        builder.addMethod(new Method.Builder()
                .setName("remove")
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamProvider;
                    private Variable mParamParameters;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamProvider = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER.asType()));
                        parameters.add(mParamParameters = Variables.of(Types.generic(SimpleOrmTypes.REMOVE_PARAMETERS.asType(), entityType)));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {

                    }
                })
                .build());

        return builder.build();
    }
}

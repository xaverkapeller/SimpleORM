package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases;

import com.github.wrdlbrnft.codebuilder.util.ProcessingHelper;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.simpleorm.annotations.ChangePassword;
import com.github.wrdlbrnft.simpleorm.annotations.Database;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidChangePasswordMethod;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidChildTableNameException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidDatabaseException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidDatabaseNameException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidRepositoryMethodException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.InvalidTableNameException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions.MultipleChangePasswordMethods;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityAnalyzer;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InvalidEntityException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
public class DatabaseAnalyzer {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final ProcessingHelper mProcessingHelper;
    private final EntityAnalyzer mEntityAnalyzer;
    private final TypeElement mRepositoryType;
    private final Set<String> mTableNames = new HashSet<>();

    public DatabaseAnalyzer(ProcessingEnvironment processingEnv) {
        mProcessingEnvironment = processingEnv;
        mProcessingHelper = ProcessingHelper.from(processingEnv);
        mEntityAnalyzer = new EntityAnalyzer(processingEnv);
        mRepositoryType = SimpleOrmTypes.REPOSITORY.asTypeElement(processingEnv);
    }

    public List<DatabaseInfo> analyze(List<TypeElement> databaseElements) {
        final Set<String> databaseNames = new HashSet<>();
        final List<DatabaseInfo> infos = new ArrayList<>(databaseElements.size());
        for (TypeElement element : databaseElements) {
            try {
                final DatabaseInfo databaseInfo = analyze(element);
                if (!databaseNames.add(databaseInfo.getDatabaseName())) {
                    throw new InvalidDatabaseNameException("You have defined two databases with the same name. Each database has to have a unique name.", element);
                }
                infos.add(databaseInfo);
            } catch (InvalidDatabaseException | InvalidEntityException e) {
                mProcessingEnvironment.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.getMessage(),
                        e.getElement()
                );
            }
        }
        return infos;
    }

    private DatabaseInfo analyze(TypeElement databaseElement) {
        final Database database = databaseElement.getAnnotation(Database.class);

        mTableNames.clear();
        final List<RepositoryInfo> repositories = new ArrayList<>();
        final Set<EntityInfo> entityInfos = new HashSet<>();
        ExecutableElement changePasswordMethod = null;
        for (Element member : databaseElement.getEnclosedElements()) {
            if (member.getKind() != ElementKind.METHOD) {
                continue;
            }

            final ExecutableElement method = (ExecutableElement) member;
            final List<? extends VariableElement> parameters = method.getParameters();

            if (method.getAnnotation(ChangePassword.class) != null) {
                if (changePasswordMethod != null) {
                    throw new MultipleChangePasswordMethods("The database " + databaseElement.getSimpleName() + " has multiple methods annotated with @ChangePassword! Each encrypted database can only have one method which can be used to change the password!", method);
                }

                if (!database.encrypted()) {
                    throw new InvalidChangePasswordMethod("Only encrypted databases can have a method for changing the password!", method);
                }

                if (parameters.size() > 1) {
                    throw new InvalidChangePasswordMethod("The method " + method.getSimpleName() + " has more than one parameter. Methods used for changing the password have to have only one parameter of type char[] or String which is the new password for the database.", method);
                }

                final VariableElement parameter = parameters.get(0);
                if (!mProcessingHelper.isSameType(parameter.asType(), mProcessingHelper.getTypeMirror(char[].class)) && !mProcessingHelper.isSameType(parameter.asType(), mProcessingHelper.getTypeMirror(String.class))) {
                    throw new InvalidChangePasswordMethod("The parameter of the method " + method.getSimpleName() + " is invalid. New passwords need to be supplied either as char[] or String.", method);
                }

                changePasswordMethod = method;
                continue;
            }

            if (!parameters.isEmpty()) {
                throw new InvalidRepositoryMethodException("The database method " + method.getSimpleName() + "() has one or more parameters! These methods are not allowed to have any parameters.", method);
            }

            final TypeMirror returnType = method.getReturnType();
            if (!mProcessingHelper.isSameType(mRepositoryType.asType(), returnType)) {
                throw new InvalidRepositoryMethodException("The database method " + method.getSimpleName() + "() does not return a Repository! These methods have to return a Repository with an entity as its generic type parameter.", method);
            }

            final List<TypeMirror> typeParameters = Utils.getTypeParameters(returnType);
            if (typeParameters.isEmpty()) {
                throw new InvalidRepositoryMethodException("The database method " + method.getSimpleName() + "() returns a Repository without a type parameter! You have to specify one of your entities as its type parameters.", method);
            }

            final TypeMirror entityType = typeParameters.get(0);
            final TypeElement entityElement = mProcessingHelper.getTypeElement(entityType);

            if (entityElement.getAnnotation(Entity.class) == null) {
                throw new InvalidRepositoryMethodException("The entity referenced by the method " + method.getSimpleName() + "() is missing the @Entity annotation! You have to annotate your entities properly.", method);
            }

            final EntityInfo entityInfo = mEntityAnalyzer.analyze(entityElement);
            entityInfos.add(entityInfo);
            if (!mTableNames.add(entityInfo.getTableName())) {
                throw new InvalidTableNameException("The database " + databaseElement.getSimpleName() + " already contains another entity with the same name declared in its @Entity annotation! Those names have to be unique for each database!", method);
            }
            appendChildEntities(entityInfos, entityInfo);

            repositories.add(new RepositoryInfoImpl(entityElement, entityInfo, method));
        }

        return new DatabaseInfoImpl(database.name(), database.version(), changePasswordMethod, database.encrypted(), databaseElement, repositories, entityInfos);
    }

    private void appendChildEntities(Set<EntityInfo> entityInfos, EntityInfo entityInfo) {
        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                final EntityInfo childInfo = columnInfo.getChildEntityInfo();
                if (entityInfos.add(childInfo)) {
                    appendChildEntities(entityInfos, childInfo);
                }
            }
        }
    }
}

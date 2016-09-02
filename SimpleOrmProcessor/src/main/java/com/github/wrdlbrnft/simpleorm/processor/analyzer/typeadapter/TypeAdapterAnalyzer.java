package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter;

import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.util.ProcessingHelper;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.simpleorm.annotations.ColumnTypeAdapter;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.exceptions.TypeAdapterException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.exceptions.UnsupportedTypeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public class TypeAdapterAnalyzer {

    private final ProcessingEnvironment mProcessingEnv;
    private final ProcessingHelper mHelper;
    private final TypeElement mValueConverterElement;
    private final TypeMirror mListTypeMirror;
    private final Map<TypeMirror, ColumnType> mTypeMap;
    private final Map<String, TypeAdapterResult> mInfoMap = new HashMap<>();

    private final TypeAdapterManager mManager = new TypeAdapterManager() {
        @Override
        public TypeAdapterResult resolve(TypeMirror type) throws UnsupportedTypeException {
            final String fullClassName = type.toString();
            final TypeAdapterResult info = mInfoMap.get(fullClassName);
            if (info == null) {
                final TypeElement typeElement = mHelper.getTypeElement(type);
                if (typeElement.getAnnotation(Entity.class) != null) {
                    return new TypeAdapterResultImpl(Collections.<TypeAdapterInfo>emptyList(), ColumnType.ENTITY, type, TypeAdapterResult.Type.OBJECT);
                }
                if (mHelper.isSameType(mListTypeMirror, type)) {
                    final List<TypeMirror> listParameters = Utils.getTypeParameters(type);
                    if (listParameters.size() != 1) {
                        throw new UnsupportedTypeException("You need to specify a proper type parameter for your List column!", typeElement);
                    }
                    final TypeMirror componentType = listParameters.get(0);
                    final TypeElement componentElement = mHelper.getTypeElement(componentType);
                    if (componentElement.getAnnotation(Entity.class) != null) {
                        return new TypeAdapterResultImpl(Collections.<TypeAdapterInfo>emptyList(), ColumnType.ENTITY, componentType, TypeAdapterResult.Type.LIST);
                    }
                }
                throw new UnsupportedTypeException("Failed to find adapter for type " + typeElement.getSimpleName(), typeElement);
            }
            return info;
        }
    };

    public TypeAdapterAnalyzer(ProcessingEnvironment processingEnv) {
        mProcessingEnv = processingEnv;
        mHelper = new ProcessingHelper(processingEnv);
        mValueConverterElement = SimpleOrmTypes.VALUE_CONVERTER.asTypeElement(processingEnv);
        mListTypeMirror = mHelper.getTypeMirror(List.class);
        mTypeMap = new MapBuilder<TypeMirror, ColumnType>()
                .put(mHelper.getTypeMirror(int.class), ColumnType.PRIMITIVE_INT)
                .put(mHelper.getTypeMirror(Integer.class), ColumnType.INT)
                .put(mHelper.getTypeMirror(long.class), ColumnType.PRIMITIVE_LONG)
                .put(mHelper.getTypeMirror(Long.class), ColumnType.LONG)
                .put(mHelper.getTypeMirror(boolean.class), ColumnType.PRIMITIVE_BOOLEAN)
                .put(mHelper.getTypeMirror(Boolean.class), ColumnType.BOOLEAN)
                .put(mHelper.getTypeMirror(float.class), ColumnType.PRIMITIVE_FLOAT)
                .put(mHelper.getTypeMirror(Float.class), ColumnType.FLOAT)
                .put(mHelper.getTypeMirror(double.class), ColumnType.PRIMITIVE_DOUBLE)
                .put(mHelper.getTypeMirror(Double.class), ColumnType.DOUBLE)
                .put(mHelper.getTypeMirror(String.class), ColumnType.STRING)
                .build();

        for (TypeMirror typeMirror : mTypeMap.keySet()) {
            final ColumnType type = mTypeMap.get(typeMirror);
            final TypeAdapterResultImpl value = new TypeAdapterResultImpl(Collections.<TypeAdapterInfo>emptyList(), type, typeMirror, TypeAdapterResult.Type.OBJECT);
            mInfoMap.put(typeMirror.toString(), value);
        }

        mInfoMap.put(Date.class.getCanonicalName(), new TypeAdapterResultImpl(
                Collections.<TypeAdapterInfo>singletonList(new TypeAdapterInfoImpl(SimpleOrmTypes.DATE_TYPE_ADAPTER.asTypeElement(processingEnv), mHelper.getTypeMirror(Long.class), mHelper.getTypeMirror(Date.class))),
                ColumnType.DATE,
                mHelper.getTypeMirror(Date.class), TypeAdapterResult.Type.OBJECT));

        mInfoMap.put(Calendar.class.getCanonicalName(), new TypeAdapterResultImpl(
                Arrays.<TypeAdapterInfo>asList(
                        new TypeAdapterInfoImpl(SimpleOrmTypes.CALENDAR_TYPE_ADAPTER.asTypeElement(processingEnv), mHelper.getTypeMirror(Date.class), mHelper.getTypeMirror(Calendar.class)),
                        new TypeAdapterInfoImpl(SimpleOrmTypes.DATE_TYPE_ADAPTER.asTypeElement(processingEnv), mHelper.getTypeMirror(Long.class), mHelper.getTypeMirror(Date.class))
                ),
                ColumnType.DATE,
                mHelper.getTypeMirror(Calendar.class), TypeAdapterResult.Type.OBJECT));
    }

    public TypeAdapterManager analyze(RoundEnvironment roundEnv) {
        final List<TypeElement> adapterElements = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ColumnTypeAdapter.class)) {
            adapterElements.add((TypeElement) element);
        }
        return analyze(adapterElements);
    }

    private TypeAdapterManager analyze(List<TypeElement> adapterElements) {
        final List<TypeAdapterInfo> adapterInfos = new ArrayList<>();
        for (TypeElement element : adapterElements) {
            try {
                final TypeAdapterInfo info = analyze(element);
                adapterInfos.add(info);
            } catch (TypeAdapterException e) {
                mProcessingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.getMessage(),
                        e.getElement()
                );
            }
        }

        for (TypeAdapterInfo adapterInfo : adapterInfos) {
            try {
                final TypeMirror type = adapterInfo.getToType();
                final TypeAdapterResult result = resolve(type, adapterInfo, adapterInfos, new ArrayList<TypeAdapterInfo>());
                mInfoMap.put(type.toString(), result);
            } catch (TypeAdapterException e) {
                mProcessingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.getMessage(),
                        e.getElement()
                );
            }
        }

        return mManager;
    }

    private TypeAdapterResult resolve(TypeMirror type, TypeAdapterInfo adapterInfo, List<TypeAdapterInfo> allAdapters, List<TypeAdapterInfo> resolvedAdapters) {
        resolvedAdapters.add(adapterInfo);

        final TypeMirror fromType = adapterInfo.getFromType();
        final ColumnType columnType = mTypeMap.get(fromType);
        if (columnType == null) {
            final TypeAdapterInfo adapter = findAdapterFor(allAdapters, fromType);
            return resolve(type, adapter, allAdapters, resolvedAdapters);
        }

        return new TypeAdapterResultImpl(resolvedAdapters, columnType, type, TypeAdapterResult.Type.OBJECT);
    }

    private TypeAdapterInfo findAdapterFor(List<TypeAdapterInfo> allAdapters, TypeMirror type) {
        for (TypeAdapterInfo adapter : allAdapters) {
            if (mHelper.isSameType(adapter.getToType(), type)) {
                return adapter;
            }
        }
        final TypeElement typeElement = mHelper.getTypeElement(type);
        throw new TypeAdapterException("Cannot find a type adapter for type: " + typeElement.getSimpleName(), typeElement);
    }

    private TypeAdapterInfo analyze(TypeElement element) {
        if (element.getKind() != ElementKind.CLASS) {
            throw new TypeAdapterException("You can only annotate a fully implemented class with @ColumnTypeAdapter.", element);
        }

        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new TypeAdapterException("You can only annotate a fully implemented class with @ColumnTypeAdapter.", element);
        }

        if (!Utils.isSubTypeOf(mProcessingEnv, element.asType(), mValueConverterElement.asType())) {
            throw new TypeAdapterException("Classes you annotate with @ColumnTypeAdapter have to implement the ValueConverter interface.", element);
        }

        final List<TypeMirror> typeParameters = Utils.getTypeParametersOfInterface(mProcessingEnv, element.asType(), mValueConverterElement.asType());
        if (typeParameters.size() != 2) {
            throw new TypeAdapterException("Your type adapter did not define type arguments for the interface ValueConverter.", element);
        }

        final TypeMirror fromType = typeParameters.get(0);
        final TypeMirror toType = typeParameters.get(1);
        return new TypeAdapterInfoImpl(element, fromType, toType);
    }

    private static class TypeAdapterInfoImpl implements TypeAdapterInfo {

        private final TypeElement mAdapterElement;
        private final TypeMirror mFromType;
        private final TypeMirror mToType;

        private TypeAdapterInfoImpl(TypeElement adapterElement, TypeMirror fromType, TypeMirror toType) {
            mAdapterElement = adapterElement;
            mFromType = fromType;
            mToType = toType;
        }

        @Override
        public TypeElement getAdapterElement() {
            return mAdapterElement;
        }

        @Override
        public TypeMirror getFromType() {
            return mFromType;
        }

        @Override
        public TypeMirror getToType() {
            return mToType;
        }
    }

    private static class TypeAdapterResultImpl implements TypeAdapterResult {

        private final List<TypeAdapterInfo> mAdapters;
        private final ColumnType mColumnType;
        private final TypeMirror mTypeMirror;
        private final Type mType;

        private TypeAdapterResultImpl(List<TypeAdapterInfo> adapters, ColumnType columnType, TypeMirror typeMirror, Type type) {
            mAdapters = adapters;
            mColumnType = columnType;
            mTypeMirror = typeMirror;
            mType = type;
        }

        @Override
        public List<TypeAdapterInfo> getAdapters() {
            return mAdapters;
        }

        @Override
        public ColumnType getColumnType() {
            return mColumnType;
        }

        @Override
        public TypeMirror getTypeMirror() {
            return mTypeMirror;
        }

        @Override
        public Type getResultType() {
            return mType;
        }
    }
}

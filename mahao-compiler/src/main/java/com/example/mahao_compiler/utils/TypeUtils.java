package com.example.mahao_compiler.utils;

import static com.example.mahao_compiler.utils.Consts.BOOLEAN;
import static com.example.mahao_compiler.utils.Consts.CHAR;
import static com.example.mahao_compiler.utils.Consts.DOUBEL;
import static com.example.mahao_compiler.utils.Consts.FLOAT;
import static com.example.mahao_compiler.utils.Consts.INTEGER;
import static com.example.mahao_compiler.utils.Consts.LONG;
import static com.example.mahao_compiler.utils.Consts.STRING;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeUtils {

    private Types types;
    private TypeMirror parceleableType;
    private TypeMirror serializableType;

    public TypeUtils(Types type, Elements elements) {
        this.types = type;
        parceleableType = elements.getTypeElement(Consts.PARCELABLE).asType();
        serializableType = elements.getTypeElement(Consts.SERIALIZABLE).asType();
    }

    /**
     * @param element raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case Consts.BYTE:
                return TypeKind.BYTE.ordinal();
            case Consts.SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                if (types.isSubtype(typeMirror, parceleableType)) {
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }

}

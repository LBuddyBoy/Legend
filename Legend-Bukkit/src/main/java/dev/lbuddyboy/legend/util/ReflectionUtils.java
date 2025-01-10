package dev.lbuddyboy.legend.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static Field findByType(Class<?> owner, Class<?> fieldType) throws Exception {
        Field[] fields = owner.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == fieldType) return field;
        }
        return null;
    }

    @SneakyThrows
    public static Object getFieldValue(final Object object, final String name, final Object defaultValue, final boolean declared) {

        final Field field = ReflectionUtils.getField(object, name, declared);

        field.setAccessible(true);
        final Object value = field.get(object);
        field.setAccessible(false);
        return value == null ? defaultValue : value;
    }

    @SneakyThrows
    public static Object getFieldValue(final Object object, final String name, final Object defaultValue) {
        return ReflectionUtils.getFieldValue(object, name, defaultValue, true);
    }

    public static Object getFieldValue(final Object object, final String name, final boolean declared) {
        return ReflectionUtils.getFieldValue(object, name, null, declared);
    }

    public static Object getFieldValue(final Object object, final String name) {
        return ReflectionUtils.getFieldValue(object, name, null, true);
    }

    @SneakyThrows
    public static void setFieldValue(final Object object, final String name, final Object value, final boolean declared) {

        final Field field = ReflectionUtils.getField(object, name, declared);

        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    @SneakyThrows
    public static void setFieldValue(final Object object, final String name, final Object value) {
        ReflectionUtils.setFieldValue(object, name, value, true);
    }


    @SneakyThrows
    private static Field getField(final Object object, final String name, final boolean declared) {
        return declared ? object.getClass().getDeclaredField(name) : object.getClass().getField(name);
    }

    public static List<Field> getFields(final Class<?> clazz, final boolean accessible, final boolean declared) {

        final List<Field> fields = new ArrayList<>();

        if (declared) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        } else {
            fields.addAll(Arrays.asList(clazz.getFields()));
        }

        return fields.stream().peek(field -> field.setAccessible(accessible)).collect(Collectors.toList());
    }

}

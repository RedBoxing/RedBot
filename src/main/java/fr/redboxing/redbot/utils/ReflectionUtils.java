package fr.redboxing.redbot.utils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {
    private static VarHandle MODIFIERS;

    public static void setFinalField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        int mods = field.getModifiers();
        if(Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }

        field.set(instance, value);
    }

    public static void setStaticFinalField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        int mods = field.getModifiers();
        if(Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }

        field.set(null, value);
    }

    static {
        try {
            MODIFIERS = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup()).findVarHandle(Field.class, "modifiers", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

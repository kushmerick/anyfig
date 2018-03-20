package io.osowa.anyfig;

import com.google.gson.Gson;

// coerce any value into another type, with special treatment for null (zero for numeric types and
// false for booleans) and parsing of strings as JSON for creating arbitrary objects

public class Coercer {

    private static final Gson GSON = new Gson();

    public <T> T coerce(Object value, Class<T> clazz) {
        // handle null, with special treatment for numbers & booleans
        if (value == null) {
            if (isNumeric(clazz)) {
                return (T) cast(0, (Class<? extends Number>) clazz);
            }
            if (isBoolean(clazz)) {
                return (T) new Boolean(false);
            }
            if (isCharacter(clazz)) {
                return (T) new Character((char)0);
            }
            return null;
        }
        // value is already an acceptable type
        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        // coerce to a number
        if (isNumeric(clazz)) {
            String s = value.toString();
            Number number = 0; // special case: coerce empty string to zero
            if (!s.isEmpty()) {
                number = isNumeric(value) ? (Number) value : Double.parseDouble(s);
            }
            return (T) cast(number, (Class<? extends Number>) clazz);
        }
        // coerce to a boolean
        if (isBoolean(clazz)) {
            Boolean bool = isBoolean(value) ? (Boolean) value : Boolean.parseBoolean(value.toString());
            return (T) bool;
        }
        // coerce to char
        if (isCharacter(clazz)) {
            if (isCharacter(value)) {
                return (T) new Character((char)value);
            } else {
                String s = value.toString();
                char chars[] = { 0 };
                if (!s.isEmpty()) {
                    s.getChars(0, 1, chars, 0);
                }
                return (T) new Character(chars[0]);
            }
        }
        // interpret everything else as JSON
        return GSON.fromJson(value.toString(), clazz);
    }

    private boolean isNumeric(Object value) {
        return isNumeric(value.getClass());
    }

    private boolean isNumeric(Class<?> clazz) {
        return
            clazz == Byte.class || clazz == Byte.TYPE ||
            clazz == Short.class || clazz == Short.TYPE ||
            clazz == Integer.class || clazz == Integer.TYPE ||
            clazz == Long.class || clazz == Long.TYPE ||
            clazz == Float.class || clazz == Float.TYPE ||
            clazz == Double.class || clazz == Double.TYPE;
    }

    private <T extends Number> Number cast(Number number, Class<T> clazz) {
        if (clazz == Byte.class || clazz == Byte.TYPE) {
            return new Byte(number.byteValue());
        }
        if (clazz == Short.class || clazz == Short.TYPE) {
            return new Short(number.shortValue());
        }
        if (clazz == Integer.class || clazz == Integer.TYPE) {
            return new Integer(number.intValue());
        }
        if (clazz == Long.class || clazz == Long.TYPE) {
            return new Long(number.longValue());
        }
        if (clazz == Float.class || clazz == Float.TYPE) {
            return new Float(number.floatValue());
        }
        return new Double(number.doubleValue());
    }

    private boolean isBoolean(Object value) {
        return isBoolean(value.getClass());
    }

    private boolean isBoolean(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Boolean.TYPE;
    }

    private boolean isCharacter(Object value) {
        return isCharacter(value.getClass());
    }

    private boolean isCharacter(Class<?> clazz) {
        return clazz == Character.class || clazz == Character.TYPE;
    }

}

package io.osowa.anyfig.tests;

import com.google.gson.Gson;

import org.junit.Test;

import io.osowa.anyfig.Coercer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CoercerTest {

    private static final Coercer COERCER = new Coercer();

    private static final double EPSILON = 1e-5;
    private static final Gson GSON = new Gson();

    private static class Shape {
        String color;
        Shape(String color) {
            this.color = color;
        }
        public boolean equals(Object that) {
            return ((Shape)that).color.equals(this.color);
        }
    }
    private static class Square extends Shape {
        float size;
        Square(String color, float size) {
            super(color);
            this.size = size;
        }
        public boolean equals(Object that) {
            return super.equals(that) && ((Square)that).size == this.size;
        }
    }

    @Test
    public void testCoerceNull() {
        {
            byte b = COERCER.coerce(null, Byte.class);
            assertEquals(0, b);
        } {
            byte b = COERCER.coerce(null, Byte.TYPE);
            assertEquals(0, b);
        } {
            short s = COERCER.coerce(null, Short.class);
            assertEquals(0, s);
        } {
            short s = COERCER.coerce(null, Short.TYPE);
            assertEquals(0, s);
        } {
            int i = COERCER.coerce(null, Integer.class);
            assertEquals(0, i);
        } {
            int i = COERCER.coerce(null, Integer.TYPE);
            assertEquals(0, i);
        } {
            long l = COERCER.coerce(null, Long.class);
            assertEquals(0, l);
        } {
            long l = COERCER.coerce(null, Long.TYPE);
            assertEquals(0, l);
        } {
            float f = COERCER.coerce(null, Float.class);
            assertEquals(0, f, EPSILON);
        } {
            float f = COERCER.coerce(null, Float.TYPE);
            assertEquals(0, f, EPSILON);
        } {
            double d = COERCER.coerce(null, Double.class);
            assertEquals(0, d, EPSILON);
        } {
            double d = COERCER.coerce(null, Double.TYPE);
            assertEquals(0, d, EPSILON);
        } {
            boolean b = COERCER.coerce(null, Boolean.class);
            assertFalse(b);
        } {
            boolean b = COERCER.coerce(null, Boolean.TYPE);
            assertFalse(b);
        } {
            char c = COERCER.coerce(null, Character.class);
            assertEquals(0, c);
        } {
            char c = COERCER.coerce(null, Character.TYPE);
            assertEquals(c, 0);
        } {
            String s = COERCER.coerce(null, String.class);
            assertNull(s);
        } {
            Shape s = COERCER.coerce(null, Shape.class);
            assertNull(s);
        }
    }

    @Test
    public void testCoerceFromChar() {
        {
            char c = COERCER.coerce('x', Character.class);
            assertEquals('x', c);
        } {
            char c = COERCER.coerce('x', Character.TYPE);
            assertEquals('x', c);
        }
    }

    @Test(expected = NumberFormatException.class)
    public void testCannotCoerceToNumber() {
        COERCER.coerce("foo", Float.class);
    }

    @Test
    public void testCoerceCharToChar() {
        char c = COERCER.coerce('x', Character.class);
    }

    @Test
    public void testCoerceFromString() {
        double dval = 12.34;
        String sval = "" + dval;
        {
            byte b = COERCER.coerce(sval, Byte.class);
            assertEquals(12, b);
        } {
            byte b = COERCER.coerce(sval, Byte.TYPE);
            assertEquals(12, b);
        } {
            short s = COERCER.coerce(sval, Short.class);
            assertEquals(12, s);
        } {
            short s = COERCER.coerce(sval, Short.TYPE);
            assertEquals(12, s);
        } {
            int i = COERCER.coerce(sval, Integer.class);
            assertEquals(12, i);
        } {
            int i = COERCER.coerce(sval, Integer.TYPE);
            assertEquals(12, i);
        } {
            float f = COERCER.coerce(sval, Float.class);
            assertEquals(12.34, f, EPSILON);
        } {
            float f = COERCER.coerce(sval, Float.TYPE);
            assertEquals(12.34, f, EPSILON);
        } {
            double d = COERCER.coerce(sval, Double.class);
            assertEquals(12.34, d, EPSILON);
        } {
            double d = COERCER.coerce(sval, Double.TYPE);
            assertEquals(12.34, d, EPSILON);
        } {
            boolean b = COERCER.coerce("maybe", Boolean.class);
            assertFalse(b);
        } {
            boolean b = COERCER.coerce("maybe", Boolean.TYPE);
            assertFalse(b);
        } {
            char c = COERCER.coerce("first", Character.class);
            assertEquals('f', c);
        } {
            char c = COERCER.coerce("first", Character.TYPE);
            assertEquals('f', c);
        } {
            Square square = new Square("red", 12.34f);
            Square square2 = COERCER.coerce(GSON.toJson(square), Square.class);
            assertEquals(GSON.toJson(square), GSON.toJson(square2));
        }
    }

    @Test
    public void testCoerceFromNumber() {
        double dval = 12.34;
        {
            byte b = COERCER.coerce(dval, Byte.class);
            assertEquals(12, b);
        } {
            byte b = COERCER.coerce(dval, Byte.TYPE);
            assertEquals(12, b);
        } {
            short s = COERCER.coerce(dval, Short.class);
            assertEquals(12, s);
        } {
            short s = COERCER.coerce(dval, Short.TYPE);
            assertEquals(12, s);
        } {
            int i = COERCER.coerce(dval, Integer.class);
            assertEquals(12, i);
        } {
            int i = COERCER.coerce(dval, Integer.TYPE);
            assertEquals(12, i);
        } {
            float f = COERCER.coerce(dval, Float.class);
            assertEquals(12.34, f, EPSILON);
        } {
            float f = COERCER.coerce(dval, Float.TYPE);
            assertEquals(12.34, f, EPSILON);
        } {
            double d = COERCER.coerce(dval, Double.class);
            assertEquals(12.34, d, EPSILON);
        } {
            double d = COERCER.coerce(dval, Double.TYPE);
            assertEquals(12.34, d, EPSILON);
        }
    }

    @Test
    public void testCoerceToSuperclass() {
        {
            int i = 1;
            Number n = COERCER.coerce(i, Number.class);
            assertEquals(n, i);
        } {
            Square square = new Square("red", 12.34f);
            assertTrue(square == COERCER.coerce(square, Shape.class));
        }
    }

}

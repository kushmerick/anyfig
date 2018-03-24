package io.osowa.anyfig;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PACKAGE})
public @interface Configurable {

    // should Anyfig completely ignore this field
    boolean ignore() default false;

    // ^ ^ ^ above are relevant everywhere (FIELD, TYPE or PACKAGE)
    // v v v below are relevant only for FIELD; silently ignored for TYPE or PACKAGE

    // literals: @annotation methods can not default to null or return Object, which is seriously inconvenient
    // for modeling literals; we get around this with three methods `literal` (should the field be configured
    // to a literal?), and `NULL` and `value` which capture all possible string literals (which are subsequently
    // coerced to the field's actual type) as follows:
    //   if value() is the empty string,
    //     if NULL() is true --> value is literal null
    //     else              --> value is literal ""
    //   else                --> value is value() # note that if value() is not the empty string, then NULL is ignored
    boolean literal() default false;
    boolean NULL() default false;
    String value() default "";

    // the constant from which to retrieve the value, which is automagically coerced
    // to the correct type; may be either fully qualified or unqualified (in which case the
    // annotated field's class is assumed); defaults to "DEFAULT_SOME_FIELD"
    String constant() default "";

    // the command-line argument from which to retrieve the value; defaults to "--someField"
    String argument() default "";

    // the property from which to retrieve the value; defaults to checking both "-DsomeField"
    // or "-Dsome.pkg.SomeClass.someField"
    String property() default "";

    // the environment variable from which to retrieve the value; defaults to "SOME_FIELD"
    String envvar() default "";

    // optional remote HTTP API for reading/writing configurable fields:
    // the key for setting the value; defaults to "some.pkg.SomeClass.someField"
    String remote() default "";
    // block this field from being set by the remote API
    boolean blockremote() default false;

    // when using the built-in logger short-cut callbacks, do not log the old/new values
    boolean redact() default false;

    public static final Configurable DEFAULT = new Configurable() {
        @Override public Class<? extends Annotation> annotationType() {
            return Configurable.class;
        }
        @Override public boolean ignore() {
            return false;
        }
        @Override public boolean literal() {
            return false;
        }
        @Override public boolean NULL() {
            return false;
        }
        @Override public String value() {
            return "";
        }
        @Override public String constant() {
            return "";
        }
        @Override public String argument() {
            return "";
        }
        @Override public String property() {
            return "";
        }
        @Override public String envvar() {
            return "";
        }
        @Override public String remote() {
            return "";
        }
        @Override public boolean blockremote() {
            return false;
        }
        @Override public boolean redact() {
            return false;
        }
    };

}

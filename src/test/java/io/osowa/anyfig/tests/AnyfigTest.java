package io.osowa.anyfig.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.ConfigurationException;
import io.osowa.anyfig.Delta;
import io.osowa.anyfig.EnvVarMechanism;
import io.osowa.anyfig.Failure;
import io.osowa.anyfig.PropertyMechanism;
import io.osowa.anyfig.other.TestOtherPackageCallbacks;
import io.osowa.anyfig.tests.subpackage.TestSubpackageCallbacks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnyfigTest {

    private Anyfig anyfig;
    private List<Delta> deltas;
    private List<Failure> failures;
    private final Consumer<Delta> callback = delta -> deltas.add(delta);
    private final Consumer<Failure> failureCallback = failure -> failures.add(failure);

    @Before
    public void setup() {
        anyfig = new Anyfig();
        deltas = new ArrayList<>();
        failures = new ArrayList<>();
    }

    @After
    public void teardown() throws Exception {
        anyfig.close();
    }

    @Test
    public void testDoNotConfigureStaticFinal() {
        anyfig.configure(callback, failureCallback, TestDoNotConfigureStaticFinal.class);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
    }
    static class TestDoNotConfigureStaticFinal {
        static final int FIELD = 1;
    }

    @Test
    public void testDoNotConfigureFinal() {
        TestDoNotConfigureFinal test = new TestDoNotConfigureFinal();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
    }
    static class TestDoNotConfigureFinal {
        final int field = 2;
    }

    @Test
    public void testDoNotConfigureStaticIgnored() {
        anyfig.configure(callback, failureCallback, TestDoNotConfigureStaticIgnored.class);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(1, TestDoNotConfigureStaticIgnored.field);
    }
    static class TestDoNotConfigureStaticIgnored {
        static final int DEFAULT_FIELD = 2;
        @Configurable(ignore = true)
        static int field = 1;
    }

    @Test
    public void testDoNotConfigureIgnored() {
        TestDoNotConfigureIgnored test = new TestDoNotConfigureIgnored();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(1, test.field);
    }
    static class TestDoNotConfigureIgnored {
        static final int DEFAULT_FIELD = 2;
        @Configurable(ignore = true)
        int field = 1;
    }

    @Test
    public void testDoNotConfigureIgnoredClass() {
        anyfig.configure(callback, failureCallback, TestDoNotConfigureIgnoredClass.class);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(1, TestDoNotConfigureIgnoredClass.field);
    }
    @Configurable(ignore = true)
    static class TestDoNotConfigureIgnoredClass {
        static final int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testPreserveInitialValues() {
        anyfig.configure(callback, failureCallback, TestPreserveInitialValues.class);
        assertEquals(1, TestPreserveInitialValues.field);
    }
    private static class TestPreserveInitialValues {
        private static int field = 1;
    }

    @Test
    public void testHandleStaticNoValue() {
        anyfig.configure(callback, failureCallback, TestHandleStaticNoValue.class);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(0, TestHandleStaticNoValue.field);
    }
    private static class TestHandleStaticNoValue {
        static int field;
    }

    @Test
    public void testHandleNoValue() {
        TestHandleNoValue test = new TestHandleNoValue();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(0, test.field);
    }
    private static class TestHandleNoValue {
        int field;
    }

    @Test
    public void testHandleStaticWithValue() {
        anyfig.configure(callback, failureCallback, TestHandleStaticWithValue.class);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(1, TestHandleStaticWithValue.field);
    }
    private static class TestHandleStaticWithValue {
        static int field = 1;
    }

    @Test
    public void testHandleWithValue() {
        TestHandleWithValue test = new TestHandleWithValue();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertTrue(failures.isEmpty());
        assertEquals(1, test.field);
    }
    private static class TestHandleWithValue {
        int field = 1;
    }

    @Test
    public void testHandleStaticLiteral() {
        anyfig.configure(callback, failureCallback, TestHandleStaticLiteral.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(2, TestHandleStaticLiteral.field);
    }
    private static class TestHandleStaticLiteral {
        @Configurable(literal = true, value = "2")
        private static int field = 1;
    }

    @Test
    public void testHandleLiteral() {
        TestHandleLiteral test = new TestHandleLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(2, test.field);
    }
    private static class TestHandleLiteral {
        @Configurable(literal = true, value = "2")
        private int field = 1;
    }

    @Test
    public void testHandleStaticBoolLiteral() {
        anyfig.configure(callback, failureCallback, TestHandleStaticBoolLiteral.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertTrue(TestHandleStaticBoolLiteral.field);
    }
    private static class TestHandleStaticBoolLiteral {
        @Configurable(literal = true, value = "true")
        private static boolean field = false;
    }

    @Test
    public void testHandleBoolLiteral() {
        TestHandleBoolLiteral test = new TestHandleBoolLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertTrue(test.field);
    }
    private static class TestHandleBoolLiteral {
        @Configurable(literal = true, value = "true")
        private boolean field = false;
    }

    @Test
    public void testHandleCharLiteral() {
        TestHandleCharLiteral test = new TestHandleCharLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals('f', test.field);
    }
    private static class TestHandleCharLiteral {
        @Configurable(literal = true, value = "foo")
        private char field;
    }

    @Test
    public void testHandleStringLiteral() {
        TestHandleStringLiteral test = new TestHandleStringLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals("foo", test.field);
    }
    private static class TestHandleStringLiteral {
        @Configurable(literal = true, value = "foo")
        private String field;
    }

    @Test
    public void testHandleObjLiteral() {
        TestHandleObjLiteral test = new TestHandleObjLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(1, test.field.foo);
    }
    private static class TestHandleObjLiteral {
        private class Obj {
            int foo;
        }
        @Configurable(literal = true, value = "{\"foo\": 1}")
        private Obj field;
    }

    @Test
    public void testHandleStaticNullIntegerLiteral() {
        anyfig.configure(callback, failureCallback, TestHandleStaticNullIntegerLiteral.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(0, TestHandleStaticNullIntegerLiteral.field.intValue()); // FIXME: null would be better
    }
    private static class TestHandleStaticNullIntegerLiteral {
        @Configurable(literal = true, NULL = true)
        private static Integer field = 1;
    }

    @Test
    public void testHandleNullIntegerLiteral() {
        TestHandleNullIntegerLiteral test = new TestHandleNullIntegerLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(0, test.field.intValue()); // FIXME: null would be better
    }
    private static class TestHandleNullIntegerLiteral {
        @Configurable(literal = true, NULL = true)
        private Integer field = 1;
    }

    @Test
    public void testHandleStaticNullIntLiteral() {
        anyfig.configure(callback, failureCallback, TestHandleStaticNullIntLiteral.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(0, TestHandleStaticNullIntLiteral.field);
    }
    private static class TestHandleStaticNullIntLiteral {
        @Configurable(literal = true, NULL = true)
        private static int field = 1;
    }

    @Test
    public void testHandleNullIntLiteral() {
        TestHandleNullIntLiteral test = new TestHandleNullIntLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(0, test.field);
    }
    private static class TestHandleNullIntLiteral {
        @Configurable(literal = true, NULL = true)
        private int field = 1;
    }

    @Test
    public void testHandleStaticInvalidLiteral() {
        anyfig.configure(callback, failureCallback, TestHandleStaticInvalidLiteral.class);
        assertTrue(deltas.isEmpty());
        assertEquals(1, failures.size());
        assertEquals(1, TestHandleStaticInvalidLiteral.field);
    }
    private static class TestHandleStaticInvalidLiteral {
        @Configurable(literal = true, value = "oops")
        private static int field = 1;
    }

    @Test
    public void testHandleInvalidLiteral() {
        TestHandleInvalidLiteral test = new TestHandleInvalidLiteral();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertEquals(1, failures.size());
        assertEquals(1, test.field);
    }
    private static class TestHandleInvalidLiteral {
        @Configurable(literal = true, value = "oops")
        private int field = 1;
    }

    @Test
    public void testHandleStaticDefaultConst() {
        anyfig.configure(callback, failureCallback, TestHandleStaticDefaultConst.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleStaticDefaultConst.DEFAULT_FIELD, TestHandleStaticDefaultConst.field);
    }
    private static class TestHandleStaticDefaultConst {
        private static final int DEFAULT_FIELD = 2;
        private static int field = 1;
    }

    @Test
    public void testHandleDefaultConst() {
        TestHandleDefaultConst test = new TestHandleDefaultConst();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleDefaultConst.DEFAULT_FIELD, test.field);
    }
    private static class TestHandleDefaultConst {
        private static final int DEFAULT_FIELD = 2;
        private int field = 1;
    }

    @Test
    public void testHandleStaticCustomConst() {
        anyfig.configure(callback, failureCallback, TestHandleStaticCustomConst.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleStaticCustomConst.CUSTOM_CONST, TestHandleStaticCustomConst.field);
    }
    private static class TestHandleStaticCustomConst {
        private static final int CUSTOM_CONST = 2;
        @Configurable(constant = "CUSTOM_CONST")
        private static int field = 1;
    }

    @Test
    public void testHandleCustomConst() {
        TestHandleCustomConst test = new TestHandleCustomConst();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleCustomConst.CUSTOM_CONST, test.field);
    }
    private static class TestHandleCustomConst {
        private static final int CUSTOM_CONST = 2;
        @Configurable(constant = "CUSTOM_CONST")
        private int field = 1;
    }

    @Test
    public void testHandleInvalidCustomConst() {
        TestHandleInvalidCustomConst test = new TestHandleInvalidCustomConst();
        anyfig.configure(callback, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertEquals(1, failures.size());
    }
    private static class TestHandleInvalidCustomConst {
        @Configurable(constant = "NO_SUCH_CUSTOM_CONST")
        private int field = 1;
    }

    @Test
    public void testHandleStaticQualifiedConst() {
        anyfig.configure(callback, failureCallback, TestHandleStaticQualifiedConst.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleStaticQualifiedConstHolder.CUSTOM_CONST, TestHandleStaticQualifiedConst.field);
    }
    private interface TestHandleStaticQualifiedConstHolder {
        static final int CUSTOM_CONST = 2;
    }
    private static class TestHandleStaticQualifiedConst {
        @Configurable(constant = "io.osowa.anyfig.tests.AnyfigTest$TestHandleStaticQualifiedConstHolder.CUSTOM_CONST")
        private static int field = 1;
    }

    @Test
    public void testHandleQualifiedConst() {
        TestHandleQualifiedConst test = new TestHandleQualifiedConst();
        anyfig.configure(callback, failureCallback, test);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestHandleQualifiedConstHolder.CUSTOM_CONST, test.field);
    }
    private interface TestHandleQualifiedConstHolder {
        static final int CUSTOM_CONST = 2;
    }
    private static class TestHandleQualifiedConst {
        @Configurable(constant = "io.osowa.anyfig.tests.AnyfigTest$TestHandleQualifiedConstHolder.CUSTOM_CONST")
        private int field = 1;
    }

    @Test
    public void testStaticArgs() {
        String[] args = {
            "--field1=1",
            "--io.osowa.anyfig.tests.AnyfigTest$TestStaticArgs.field2=2",
            "--custom-arg=3"
        };
        anyfig.configure(callback, failureCallback, args, TestStaticArgs.class);
        assertEquals(3, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(1, TestStaticArgs.field1);
        assertEquals(2, TestStaticArgs.field2);
        assertEquals(3, TestStaticArgs.field3);
    }
    private static class TestStaticArgs {
        private static int field1;
        private static int field2;
        @Configurable(argument = "custom-arg")
        private static int field3;
    }

    @Test
    public void testArgs() {
        TestArgs test =  new TestArgs();
        String[] args = {
                "--field1=1",
                "--io.osowa.anyfig.tests.AnyfigTest$TestArgs.field2=2",
                "--custom-arg=3"
        };
        anyfig.configure(callback, failureCallback, args, test);
        assertEquals(3, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(1, test.field1);
        assertEquals(2, test.field2);
        assertEquals(3, test.field3);
    }
    private static class TestArgs {
        private int field1;
        private int field2;
        @Configurable(argument = "custom-arg")
        private int field3;
    }

    @Test
    public void testStaticProps() throws Exception {
        PropertyMechanism.withProperties(
            () -> {
                anyfig.configure(callback, failureCallback, TestStaticProps.class);
                assertEquals(3, deltas.size());
                assertTrue(failures.isEmpty());
                assertEquals(1, TestStaticProps.field1);
                assertEquals(2, TestStaticProps.field2);
                assertEquals(3, TestStaticProps.field3);
            },
            "field1", "1",
            "io.osowa.anyfig.tests.AnyfigTest$TestStaticProps.field2", "2",
            "custom-prop", "3");
    }
    private static class TestStaticProps {
        private static int field1;
        private static int field2;
        @Configurable(property = "custom-prop")
        private static int field3;
    }

    @Test
    public void testProps() throws Exception {
        PropertyMechanism.withProperties(
            () -> {
                TestProps test = new TestProps();
                anyfig.configure(callback, failureCallback, test);
                assertEquals(3, deltas.size());
                assertTrue(failures.isEmpty());
                assertEquals(1, test.field1);
                assertEquals(2, test.field2);
                assertEquals(3, test.field3);
            },
            "field1", "1",
            "io.osowa.anyfig.tests.AnyfigTest$TestProps.field2", "2",
            "custom-prop", "3");
    }
    private static class TestProps {
        private int field1;
        private int field2;
        @Configurable(property = "custom-prop")
        private int field3;
    }

    @Test
    public void testStaticEnvVar() throws Exception {
        EnvVarMechanism.withProperties(
            () -> {
                anyfig.configure(callback, failureCallback, TestStaticEnvVar.class);
                assertEquals(2, deltas.size());
                assertTrue(failures.isEmpty());
                assertEquals(1, TestStaticEnvVar.field1);
                assertEquals(2, TestStaticEnvVar.field2);
            },
            "FIELD1", "1",
            "CUSTOM_ENVVAR", "2");
    }
    private static class TestStaticEnvVar {
        private static int field1;
        @Configurable(envvar = "CUSTOM_ENVVAR")
        private static int field2;
    }

    @Test
    public void testEnvVar() throws Exception {
        EnvVarMechanism.withProperties(
            () -> {
                TestEnvVar test = new TestEnvVar();
                anyfig.configure(callback, failureCallback, test);
                assertEquals(2, deltas.size());
                assertTrue(failures.isEmpty());
                assertEquals(1, test.field1);
                assertEquals(2, test.field2);
            },
            "FIELD1", "1",
            "CUSTOM_ENV_VAR", "2");
    }
    private static class TestEnvVar {
        private int field1;
        @Configurable(envvar = "CUSTOM_ENV_VAR")
        private int field2;
    }

    @Test
    public void testBadCallback() {
        TestBadCallback test = new TestBadCallback();
        anyfig.configure(ignored -> { throw new RuntimeException(); }, failureCallback, test);
        assertTrue(deltas.isEmpty());
        assertEquals(1, failures.size());
    }
    private static class TestBadCallback {
        @Configurable(literal = true, value = "2")
        private int field = 1;
    }

    @Test(expected = ConfigurationException.class)
    public void testBadFailureCallback() {
        TestBadFailureCallback test = new TestBadFailureCallback();
        anyfig.configure(callback, ignored -> { throw new RuntimeException(); }, test);
    }
    private static class TestBadFailureCallback {
        @Configurable(literal = true, value = "Ouch")
        private int field = 1;
    }

    @Test
    public void testLogger() {
        TestLogger test = new TestLogger();
        int[] info = { 0 }, warn = { 0 };
        Logger logger = new Logger(null, null) {
            @Override public void info(String msg) {
                info[0]++;
            }
            @Override public void warning(String msg) {
                warn[0]++;
            }
        };
        anyfig.configure(logger, test);
        assertEquals(1, info[0]);
        assertEquals(1, warn[0]);
    }
    private static class TestLogger {
        @Configurable(literal = true, value = "2")
        private int field1 = 1;
        @Configurable(literal = true, value = "Ouch")
        private int field2 = 1;
    }

    @Test
    public void testMethodCallbacks() throws Exception {
        Method callback = TestMethodCallbacks.class.getMethod("callback", Delta.class);
        Method failureCallback = TestMethodCallbacks.class.getMethod("failureCallback", Failure.class);
        anyfig.configure(callback, failureCallback, TestMethodCallbacks.class);
        assertEquals(1, TestMethodCallbacks.deltas);
        assertEquals(1, TestMethodCallbacks.failures);
    }
    private static class TestMethodCallbacks {
        @Configurable(literal = true, value = "2")
        private static int field1 = 1;
        @Configurable(literal = true, value = "Ouch")
        private static int field2 = 1;
        private static int deltas = 0;
        public static void callback(Delta delta) {
            deltas++;
        }
        private static int failures = 0;
        public static void failureCallback(Failure failure) {
            failures++;
        }
    }

    @Test
    public void testBiConsumerCallbacks() throws Exception {
        anyfig.configure(
            (delta, failure) -> { if (delta != null) callback.accept(delta); else failureCallback.accept(failure); },
            TestBiConsumerCallbacks.class);
        assertEquals(1, deltas.size());
        assertEquals(1, failures.size());
        assertEquals(2, TestBiConsumerCallbacks.field1);
        assertEquals(1, TestBiConsumerCallbacks.field2);
    }
    private static class TestBiConsumerCallbacks {
        @Configurable(literal = true, value = "2")
        private static int field1 = 1;
        @Configurable(literal = true, value = "Ouch")
        private static int field2 = 1;
    }

    @Test
    public void testFieldCallbacks() throws Exception {
        Field field = TestFieldCallbacks.class.getField("field1");
        anyfig.register(callback, failureCallback, field);
        anyfig.configure(TestFieldCallbacks.class);
        assertEquals(1, deltas.size()); // we get notified only for the registered field
        assertTrue(failures.isEmpty());
        assertEquals(2, TestFieldCallbacks.field1);
        assertEquals(3, TestFieldCallbacks.field2);
    }
    private static class TestFieldCallbacks {
        @Configurable(literal = true, value = "2")
        public static int field1 = 1;
        @Configurable(literal = true, value = "3")
        private static int field2 = 1;
    }

    @Test
    public void testClassCallbacks() throws Exception {
        anyfig.register(callback, failureCallback, TestClassCallbacks.class);
        anyfig.configure(TestClassCallbacks.class);
        anyfig.configure(TestClassCallbacks2.class);
        assertEquals(1, deltas.size()); // we get notified only for the registered class
        assertTrue(failures.isEmpty());
        assertEquals(2, TestClassCallbacks.field);
        assertEquals(3, TestClassCallbacks2.field);
    }
    private static class TestClassCallbacks {
        @Configurable(literal = true, value = "2")
        public static int field = 1;
    }
    private static class TestClassCallbacks2 {
        @Configurable(literal = true, value = "3")
        public static int field = 1;
    }

    @Test
    public void testPackageCallbacks() throws Exception {
        Package pkg = TestPackageCallbacks.class.getPackage();
        anyfig.register(callback, failureCallback, pkg);
        anyfig.configure(TestPackageCallbacks.class);
        anyfig.configure(TestOtherPackageCallbacks.class);
        anyfig.configure(TestSubpackageCallbacks.class);
        // we don't get notified of deltas outside our package (though children count)
        // (and there are no failures)
        assertEquals(2, deltas.size());
        assertTrue(failures.isEmpty());
        // but _everything_ is configured
        assertEquals(2, TestPackageCallbacks.field);
        assertEquals(3, TestOtherPackageCallbacks.field);
        assertEquals(4, TestSubpackageCallbacks.field);
    }
    private static class TestPackageCallbacks {
        @Configurable(literal = true, value = "2")
        private static int field = 1;
    }

    @Test
    public void testCanConfigEnum() throws Exception {
        anyfig.configure(callback, failureCallback, TestCanConfigEnum.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestCanConfigEnum.Foo.BAR, TestCanConfigEnum.field);
    }
    private static class TestCanConfigEnum {
        private enum Foo { BAR };
        private static final Foo DEFAULT_FIELD = Foo.BAR;
        private static Foo field;
    }

    @Test
    public void testCanConfigEnumFromLiteral() throws Exception {
        anyfig.configure(callback, failureCallback, TestCanConfigEnumLiteral.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestCanConfigEnumLiteral.Foo.BAR, TestCanConfigEnumLiteral.field);
    }
    private static class TestCanConfigEnumLiteral {
        private enum Foo { BAR };
        @Configurable(literal=true, value="BAR")
        private static Foo field;
    }

    @Test
    public void testRegisterPackageCallbacks() {
        anyfig.register(callback, TestRegisterPackageCallbacks.class.getPackage());
        anyfig.configure(TestRegisterPackageCallbacks.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterPackageCallbacks.DEFAULT_FIELD, TestRegisterPackageCallbacks.field);
    }
    private static class TestRegisterPackageCallbacks {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterPackageBiConsumerCallbacks() {
        anyfig.register(
            (delta, failure) -> {
                if (delta != null) callback.accept(delta);
                if (failure != null) failureCallback.accept(failure);
            },
            TestRegisterPackageConsumerCallbacks.class.getPackage());
        anyfig.configure(TestRegisterPackageConsumerCallbacks.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterPackageConsumerCallbacks.DEFAULT_FIELD, TestRegisterPackageConsumerCallbacks.field);
    }
    private static class TestRegisterPackageConsumerCallbacks {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterPackageMethodCallback() throws Exception {
        Method callback = TestRegisterPackagesMethodCallback.class.getMethod("callback", Delta.class);
        anyfig.register(callback, TestRegisterPackagesMethodCallback.class.getPackage());
        anyfig.configure(TestRegisterPackagesMethodCallback.class);
        assertEquals(1, TestRegisterPackagesMethodCallback.deltas);
        assertEquals(2, TestRegisterPackagesMethodCallback.field);
    }
    private static class TestRegisterPackagesMethodCallback {
        @Configurable(literal = true, value = "2")
        private static int field = 1;
        private static int deltas = 0;
        public static void callback(Delta delta) {
            deltas++;
        }
    }

    @Test
    public void testRegisterPackageLoggerCalllback() {
        TestRegisterPackageLoggerCallback test = new TestRegisterPackageLoggerCallback();
        int[] info = { 0 }, warn = { 0 };
        Logger logger = new Logger(null, null) {
            @Override public void info(String msg) {
                info[0]++;
            }
            @Override public void warning(String msg) {
                warn[0]++;
            }
        };
        anyfig.register(logger, TestRegisterPackageLoggerCallback.class.getPackage());
        anyfig.configure(logger, test);
        assertEquals(1, info[0]);
        assertEquals(1, warn[0]);
        assertEquals(2, test.field1);
    }
    private static class TestRegisterPackageLoggerCallback {
        @Configurable(literal = true, value = "2")
        private int field1 = 1;
        @Configurable(literal = true, value = "Ouch")
        private int field2 = 1;
    }

    @Test
    public void testRegisterGlobalCallbacks() {
        anyfig.register(callback);
        anyfig.configure(TestRegisterGlobalCallbacks.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterGlobalCallbacks.DEFAULT_FIELD, TestRegisterGlobalCallbacks.field);
    }
    private static class TestRegisterGlobalCallbacks {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterGlobalBiConsumerCallbacks() {
        anyfig.register(
                (delta, failure) -> {
                    if (delta != null) callback.accept(delta);
                    if (failure != null) failureCallback.accept(failure);
                });
        anyfig.configure(TestRegisterGlobalConsumerCallbacks.class);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterGlobalConsumerCallbacks.DEFAULT_FIELD, TestRegisterGlobalConsumerCallbacks.field);
    }
    private static class TestRegisterGlobalConsumerCallbacks {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterGlobalMethodCallback() throws Exception {
        Method callback = TestRegisterGlobalsMethodCallback.class.getMethod("callback", Delta.class);
        anyfig.register(callback);
        anyfig.configure(TestRegisterGlobalsMethodCallback.class);
        assertEquals(1, TestRegisterGlobalsMethodCallback.deltas);
        assertEquals(2, TestRegisterGlobalsMethodCallback.field);
    }
    private static class TestRegisterGlobalsMethodCallback {
        @Configurable(literal = true, value = "2")
        private static int field = 1;
        private static int deltas = 0;
        public static void callback(Delta delta) {
            deltas++;
        }
    }

    @Test
    public void testRegisterGlobalLoggerCalllback() {
        TestRegisterGlobalLoggerCallback test = new TestRegisterGlobalLoggerCallback();
        int[] info = { 0 }, warn = { 0 };
        Logger logger = new Logger(null, null) {
            @Override public void info(String msg) {
                info[0]++;
            }
            @Override public void warning(String msg) {
                warn[0]++;
            }
        };
        anyfig.register(logger);
        anyfig.configure(logger, test);
        assertEquals(1, info[0]);
        assertEquals(1, warn[0]);
        assertEquals(2, test.field1);
    }
    private static class TestRegisterGlobalLoggerCallback {
        @Configurable(literal = true, value = "2")
        private int field1 = 1;
        @Configurable(literal = true, value = "Ouch")
        private int field2 = 1;
    }

    @Test
    public void testConfigureField() throws Exception {
        Field field = TestConfigureField.class.getDeclaredField("field");
        anyfig.configure(callback, failureCallback, field);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestConfigureField.DEFAULT_FIELD, TestConfigureField.field);
    }
    private static class TestConfigureField {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testConfigureFieldNoFailureCallback() throws Exception {
        Field field = TestConfigureFieldNoFailureCallback.class.getDeclaredField("field");
        anyfig.configure(callback, field);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestConfigureFieldNoFailureCallback.DEFAULT_FIELD, TestConfigureFieldNoFailureCallback.field);
    }
    private static class TestConfigureFieldNoFailureCallback {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testConfigureFieldBiConsumer() throws Exception {
        Field field = TestConfigureFieldBiConsumer.class.getDeclaredField("field");
        anyfig.configure(
            (delta, failure) -> {
                if (delta != null) callback.accept(delta);
                if (failure != null) failureCallback.accept(failure);
            },
            field);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestConfigureFieldBiConsumer.DEFAULT_FIELD, TestConfigureFieldBiConsumer.field);
    }
    private static class TestConfigureFieldBiConsumer {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterFieldCallbacks() throws Exception {
        Field field = TestRegisterFieldCallbacks.class.getDeclaredField("field");
        anyfig.register(callback, failureCallback, field);
        anyfig.configure(field);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterFieldCallbacks.DEFAULT_FIELD, TestRegisterFieldCallbacks.field);
    }
    private static class TestRegisterFieldCallbacks {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

    @Test
    public void testRegisterFieldBiConsumer() throws Exception {
        Field field = TestRegisterFieldBiConsumer.class.getDeclaredField("field");
        anyfig.register(
            (delta, failure) -> {
                if (delta != null) callback.accept(delta);
                if (failure != null) failureCallback.accept(failure);
            },
            field);
        anyfig.configure(field);
        assertEquals(1, deltas.size());
        assertTrue(failures.isEmpty());
        assertEquals(TestRegisterFieldBiConsumer.DEFAULT_FIELD, TestRegisterFieldBiConsumer.field);
    }
    private static class TestRegisterFieldBiConsumer {
        final static int DEFAULT_FIELD = 2;
        static int field = 1;
    }

}

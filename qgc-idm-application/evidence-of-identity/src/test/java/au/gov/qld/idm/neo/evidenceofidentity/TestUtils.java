package au.gov.qld.idm.neo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Project notificationsv2
 * Created on 03 Nov 2017.
 */
public class TestUtils {
    public static void assertUtilityClassWellDefined(final Class<?> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        assertTrue(Modifier.isFinal(clazz.getModifiers()));
        assertThat(clazz.getDeclaredConstructors().length, equalTo(1));

        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            fail("constructor is not private");
        }

        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);

        for (final Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass() == clazz) {
                fail("there exists a non-static method:" + method);
            }
        }
    }

    public static void assertFieldInstantiated(Object testObject, String key)
            throws NoSuchFieldException, IllegalAccessException {
        Object value = getObject(testObject, key);
        assertNotNull(key + " should have been instantiated", value);
    }

    public static void assertFieldInstantiatedWithObject(Object testObject, String key, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Object objectValue = getObject(testObject, key);
        assertNotNull(key + " should have been instantiated", objectValue);
        assertThat(key + " should have been instantiated", objectValue, equalTo(value));
    }

    public static String readFileToString(String fileName) throws IOException {
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        return IOUtils.toString(resource, StandardCharsets.UTF_8).trim();
    }

    private static Object getObject(Object testObject, String key) throws NoSuchFieldException, IllegalAccessException {
        Field field = testObject.getClass().getDeclaredField(key);
        field.setAccessible(true);
        return field.get(testObject);
    }
}

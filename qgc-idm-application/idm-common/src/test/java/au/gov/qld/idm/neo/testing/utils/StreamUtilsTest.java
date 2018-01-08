package au.gov.qld.idm.neo.testing.utils;

import au.gov.qld.idm.neo.TestUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Project qgc-idm-application
 * Created on 08 Jan 2018.
 */
public class StreamUtilsTest {
    @Test
    public void testUtilityClassDefined() throws Exception {
        TestUtils.assertUtilityClassWellDefined(StreamUtils.class);
    }

    @Test
    public void asStreamWithNull() throws Exception {
        Stream<Object> stream = StreamUtils.asStream(null);
        assertThat(stream.count(), equalTo(0L));
    }

    @Test
    public void asStreamWithEmpty() throws Exception {
        Stream<Object> stream = StreamUtils.asStream(Collections.emptyList());
        assertThat(stream.count(), equalTo(0L));
    }

    @Test
    public void asStreamWithNotEmpty() throws Exception {
        Stream<Object> stream = StreamUtils.asStream(Collections.singletonList(new Object()));
        assertThat(stream.count(), equalTo(1L));
    }
}
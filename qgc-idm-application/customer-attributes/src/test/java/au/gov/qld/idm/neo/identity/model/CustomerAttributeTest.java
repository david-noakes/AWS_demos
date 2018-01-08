package au.gov.qld.idm.neo.identity.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Project qgc-idm-application
 * Created on 08 Jan 2018.
 */
public class CustomerAttributeTest {

    private CustomerAttribute underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new CustomerAttribute();
    }

    @Test
    public void setKey() throws Exception {
        underTest.setKey("the.key");
        assertThat(underTest.getKey(), equalTo("the.key"));
    }

    @Test
    public void setValue() throws Exception {
        underTest.setValue("the.value");
        assertThat(underTest.getValue(), equalTo("the.value"));
    }
}
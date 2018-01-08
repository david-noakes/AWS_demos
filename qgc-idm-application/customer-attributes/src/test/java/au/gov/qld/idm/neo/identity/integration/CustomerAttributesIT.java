package au.gov.qld.idm.neo.identity.integration;

import au.gov.qld.idm.neo.testing.utils.DynamoTableDefinition;
import au.gov.qld.idm.neo.testing.utils.DynamoTestUtils;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Project qgc-idm-application
 * Created on 08 Jan 2018.
 */
public class CustomerAttributesIT extends DynamoTestUtils {

    private static final EnvironmentVariables ENVIRONMENT_VARIABLES = new EnvironmentVariables();
    private static final DynamoTableDefinition CUSTOMER_ATTRIBUTE_TABLE = new DynamoTableDefinition()
            .withTableName("CustomerAttributes");

    private static final List<DynamoTableDefinition> TABLES = Lists.newArrayList(CUSTOMER_ATTRIBUTE_TABLE);

    @BeforeClass
    public static void setupClass() {
        setupDynamoEnvironment(TABLES);

        ENVIRONMENT_VARIABLES.set("dynamo_host", dynamoEndpointUrl);
        ENVIRONMENT_VARIABLES.set("dynamo_region", dynamoRegion);
        ENVIRONMENT_VARIABLES.set("AWS_ACCESS_KEY_ID", "accesskey");
        ENVIRONMENT_VARIABLES.set("AWS_SECRET_ACCESS_KEY", "dummy");
    }

    @Before
    public void setupTest() {
        purgeTables(TABLES);
    }

    @Test
    public void testTableCreated() {
        assertThat(recordCount(CUSTOMER_ATTRIBUTE_TABLE.getTableName()), equalTo(0L));
    }
}

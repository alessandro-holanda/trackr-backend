package de.techdev.trackr.core.web;

import de.techdev.test.OAuthTestExecutionListener;
import de.techdev.test.TestRestTemplate;
import de.techdev.trackr.Trackr;
import de.techdev.trackr.core.security.AuthorityMocks;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.json.Json;
import javax.json.stream.JsonGeneratorFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Trackr.class)
@ActiveProfiles({"in-memory-database", "test-oauth", "granular-security"})
@TestExecutionListeners(value = OAuthTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class MockMvcTest2 {

    @Value("${local.server.port}")
    private Integer serverPort;

    protected JsonGeneratorFactory jsonGeneratorFactory;
    protected RestTemplate restTemplate;
    protected String host;

    @Before
    public void setUpMvcFields() throws Exception {
        jsonGeneratorFactory = Json.createGeneratorFactory(null);
        restTemplate = new TestRestTemplate(OAuthTestExecutionListener.OAUTH_TOKEN_VALUE);
        host = "http://localhost:" + serverPort;
    }

    // TODO remove below this line when AbstractDomainResourceTest3 is standard

    @Autowired
    protected TokenStore tokenStore;

    private MockHttpSession buildSession(Authentication authentication) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new MockMvcTest.MockSecurityContext(authentication));
        return session;
    }

    /**
     * An http session for an employee.
     *
     * @param username The desired id of the employee.
     * @return The mock session object.
     */
    protected MockHttpSession employeeSession(String username) {
        return buildSession(AuthorityMocks.employeeAuthentication(username));
    }

    /**
     * An http session for an employee with id 100.
     *
     * @return The mock session object.
     */
    protected MockHttpSession employeeSession() {
        return buildSession(AuthorityMocks.basicAuthentication());
    }

    /**
     * An http session for a supervisor.
     *
     * @return The mock session object.
     */
    protected MockHttpSession supervisorSession() {
        return buildSession(AuthorityMocks.supervisorAuthentication());
    }

    protected MockHttpSession supervisorSession(String username) {
        return buildSession(AuthorityMocks.supervisorAuthentication(username));
    }

    /**
     * An http session for an admin.
     *
     * @return The mock session object.
     */
    protected MockHttpSession adminSession() {
        return buildSession(AuthorityMocks.adminAuthentication());
    }
}

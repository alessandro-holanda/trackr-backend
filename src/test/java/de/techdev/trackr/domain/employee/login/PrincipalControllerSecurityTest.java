package de.techdev.trackr.domain.employee.login;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static org.junit.Assert.assertThat;

@OAuthRequest
@Sql("resourceTest.sql")
@Sql(value = "/de/techdev/trackr/domain/emptyDatabase.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PrincipalControllerSecurityTest extends MockMvcTest2 {

    @Test
    public void principal() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/principal", String.class);
        assertThat(response, isAccessible());
    }
}


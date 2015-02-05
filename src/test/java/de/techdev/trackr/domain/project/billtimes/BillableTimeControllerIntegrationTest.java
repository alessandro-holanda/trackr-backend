package de.techdev.trackr.domain.project.billtimes;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isForbidden;
import static org.junit.Assert.assertThat;

public class BillableTimeControllerIntegrationTest extends MockMvcTest2 {

    @Test
    @OAuthRequest
    public void findEmployeeMappingByProjectAndDateBetweenForbiddenForEmployee() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(host + "/billableTimes/findEmployeeMappingByProjectAndDateBetween?project=0&start=2014-01-01&end=2014-01-31", String.class);
        assertThat(response, isForbidden());
    }

    @Test
    @OAuthRequest("ROLE_SUPERVISOR")
    public void findEmployeeMappingByProjectAndDateBetweenAllowedForSupervisor() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(host + "/billableTimes/findEmployeeMappingByProjectAndDateBetween?project=0&start=2014-01-01&end=2014-01-31", String.class);
        assertThat(response, isAccessible());
    }
}

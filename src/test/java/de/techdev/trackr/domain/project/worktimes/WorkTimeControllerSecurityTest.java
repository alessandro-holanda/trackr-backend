package de.techdev.trackr.domain.project.worktimes;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isForbidden;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@OAuthRequest
public class WorkTimeControllerSecurityTest extends MockMvcTest2 {

    @Test
    public void findEmployeeMappingByProjectAndDateBetweenForbiddenForEmployee() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(host + "/workTimes/findEmployeeMappingByProjectAndDateBetween?project=0&start=2014-01-01&end=2014-01-31", String.class);
        assertThat(response, isForbidden());
    }

    @Test
    @OAuthRequest("ROLE_SUPERVISOR")
    public void findEmployeeMappingByProjectAndDateBetweenAllowedForSupervisor() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(host + "/workTimes/findEmployeeMappingByProjectAndDateBetween?project=0&start=2014-01-01&end=2014-01-31", String.class);
        assertThat(response, isAccessible());
    }
}

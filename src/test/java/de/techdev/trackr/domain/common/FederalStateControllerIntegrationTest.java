package de.techdev.trackr.domain.common;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static org.junit.Assert.assertThat;

@OAuthRequest
public class FederalStateControllerIntegrationTest extends MockMvcTest2 {

    @Test
    public void getAllFederalStates() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/federalStates", String.class);
        assertThat(response, isAccessible());
    }
}

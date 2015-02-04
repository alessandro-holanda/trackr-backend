package de.techdev.trackr.domain.translations;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static org.junit.Assert.assertThat;

@OAuthRequest
public class TranslationsResourceTest extends MockMvcTest2 {

    @Test
    public void testGetTranslationsIsAccessible() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/translations?locale=de", String.class);
        assertThat(response, isAccessible());
    }
}

package de.techdev.trackr.domain.employee.addressbook;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static org.junit.Assert.assertThat;

@OAuthRequest
public class AddressBookControllerSecurityTest extends MockMvcTest2 {

    @Test
    public void rootIsAccessible() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/address_book", String.class);
        assertThat(response, isAccessible());
    }
}
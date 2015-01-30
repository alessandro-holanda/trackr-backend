package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("resourceTest.sql")
@Sql(value = "resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class CompanyResourceSecurityTest extends AbstractDomainResourceSecurityTest {

    private CompanyJsonGenerator jsonGenerator = new CompanyJsonGenerator();

    @Override
    protected String getResourceName() {
        return "companies";
    }

    @Test
    @OAuthToken
    public void rootAccessible() throws Exception {
        assertThat(root(), isAccessible());
    }

    @Test
    @OAuthToken
    public void one() throws Exception {
        assertThat(one(0L), isAccessible());
    }

    @Test
    @OAuthToken
    public void findByNameLikeOrderByNameAsc() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/companies/search/findByNameLikeIgnoreCaseOrderByNameAsc?name=%webshop%", String.class);
        assertThat(response, isAccessible());
    }

    @Test
    @OAuthToken
    public void findByCompanyId() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(host + "/companies/search/findByCompanyId?companyId=1000", String.class);
        assertThat(response, isAccessible());
    }

    @Test
    public void postAllowedForAdmin() throws Exception {
        String json = jsonGenerator.start().withAddressId(0L).build();
        assertThat(create(json), isCreated());
    }

    @Test
    public void putAllowedForAdmin() throws Exception {
        String json = jsonGenerator.start().apply(c -> c.setId(0L)).withAddressId(0L).build();
        assertThat(update(0L, json), isUpdated());
    }

    @Test
    public void patchAllowedForAdmin() throws Exception {
        assertThat(updateViaPatch(0L, "{\"name\": \"test\"}"), isUpdated());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void postForbiddenForSupervisor() throws Exception {
        String json = jsonGenerator.start().withAddressId(0L).build();
        assertThat(create(json), isForbidden());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void putForbiddenForSupervisor() throws Exception {
        String json = jsonGenerator.start().apply(c -> c.setId(0L)).withAddressId(0L).build();
        assertThat(update(0L, json), isForbidden());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void patchForbiddenForSupervisor() throws Exception {
        assertThat(updateViaPatch(0L, "{\"name\": \"test\"}"), isForbidden());
    }

//    @Test
//    public void constraintViolation() throws Exception {
//        mockMvc.perform(
//                post("/companies")
//                        .session(adminSession())
//                        .content("{ \"companyId\": \"1234\" }")
//        )
//               .andExpect(status().isBadRequest());
//    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void addContactPersonSupervisor() throws Exception {
        assertThat(updateLink(0L, "contactPersons", "/contactPersons/0"), isNoContent());
    }

    @Test
    @OAuthToken
    public void addContactForbiddenForEmployee() throws Exception {
        assertThat(updateLink(0L, "contactPersons", "/contactPersons/0"), isForbidden());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteContactAllowedForSupervisor() throws Exception {
        assertThat(removeUrl("/companies/0/contactPersons/0"), isNoContent());
    }

    @Test
    @OAuthToken
    public void deleteContactNotAllowedForEmployee() throws Exception {
        assertThat(removeUrl("/companies/0/contactPersons/0"), isForbidden());
    }

    @Test
    public void deleteAllowedForAdmin() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteForbiddenForSupervisor() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

    @Test
    @OAuthToken
    public void getAddress() throws Exception {
        assertThat(oneUrl("/companies/0/address"), isAccessible());
    }
}

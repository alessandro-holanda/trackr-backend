package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("resourceTest.sql")
@Sql(value = "resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class CompanyResourceSecurityTest extends AbstractDomainResourceSecurityTest<Company> {

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
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//        mockMvc.perform(
//                get("/companies/search/findByNameLikeIgnoreCaseOrderByNameAsc")
//                        .session(employeeSession())
//                        .param("name", company.getName())
//        )
//               .andExpect(status().isOk())
//               .andExpect(content().contentType(STANDARD_CONTENT_TYPE))
//               .andExpect(jsonPath("_embedded.companies[0].id", isNotNull()));
    }

//    @Test
//    public void findByCompanyId() throws Exception {
//        Company company = dataOnDemand.getRandomObject();
//        mockMvc.perform(
//                get("/companies/search/findByCompanyId")
//                        .param("companyId", company.getCompanyId().toString())
//                        .session(employeeSession())
//        )
//               .andExpect(status().isOk())
//               .andExpect(content().contentType(STANDARD_CONTENT_TYPE))
//               .andExpect(jsonPath("_embedded.companies[0].companyId", is(company.getCompanyId().intValue())));
//    }

//    @Test
//    public void postAllowedForAdmin() throws Exception {
//
//        assertThat(create(), isCreated());
//    }

//    @Test
//    public void putAllowedForAdmin() throws Exception {
//        assertThat(update(adminSession()), isUpdated());
//    }

//    @Test
//    public void patchAllowedForAdmin() throws Exception {
//        assertThat(updateViaPatch(adminSession(), "{\"name\": \"test\"}"), isUpdated());
//    }

//    @Test
//    public void postForbiddenForSupervisor() throws Exception {
//        assertThat(create(supervisorSession()), isForbidden());
//    }

//    @Test
//    public void putForbiddenForSupervisor() throws Exception {
//        assertThat(update(supervisorSession()), isForbidden());
//    }

//    @Test
//    public void patchForbiddenForSupervisor() throws Exception {
//        assertThat(updateViaPatch(supervisorSession(), "{\"name\": \"test\"}"), isForbidden());
//    }

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

    @Override
    protected String getJsonRepresentation(Company company) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        jg.writeStartObject()
          .write("name", company.getName())
          .write("companyId", company.getCompanyId())
          .write("address", "/api/addresses/" + company.getAddress().getId());
        if (company.getId() != null) {
            jg.write("id", company.getId());
        }
        if (company.getTimeForPayment() != null) {
            jg.write("timeForPayment", company.getTimeForPayment());
        }
        jg.writeEnd().close();
        return writer.toString();
    }
}

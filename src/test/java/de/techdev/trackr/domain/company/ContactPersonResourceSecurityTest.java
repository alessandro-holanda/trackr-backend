package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("contactPerson/resourceTest.sql")
@Sql(value = "contactPerson/resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_SUPERVISOR")
public class ContactPersonResourceSecurityTest extends AbstractDomainResourceSecurityTest {

    private ContactPersonJsonGenerator jsonGenerator = new ContactPersonJsonGenerator();

    @Override
    protected String getResourceName() {
        return "contactPersons";
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
    public void postAllowedForSupervisor() throws Exception {
        String json = jsonGenerator.withCompanyId(0L).start().build();
        assertThat(create(json), isCreated());
    }

    @Test
    public void putAllowedForSupervisor() throws Exception {
        String json = jsonGenerator.withCompanyId(0L).start().apply(c -> c.setId(0L)).build();
        assertThat(update(0L, json), isUpdated());
    }

    @Test
    @OAuthToken
    public void putNotAllowedForEmployee() throws Exception {
        String json = jsonGenerator.withCompanyId(0L).start().apply(c -> c.setId(0L)).build();
        assertThat(update(0L, json), isForbidden());
    }

    @Test
    public void patchAllowedForSupervisor() throws Exception {
        assertThat(updateViaPatch(0L, "{\"firstName\": \"Test\"}"), isUpdated());
    }

    @Test
    @OAuthToken
    public void patchNotAllowedForEmployee() throws Exception {
        assertThat(updateViaPatch(0L, "{\"firstName\": \"Test\"}"), isForbidden());
    }

    @Test
    @OAuthToken
    public void postNotAllowedForEmployee() throws Exception {
        String json = jsonGenerator.withCompanyId(0L).start().build();
        assertThat(create(json), isForbidden());
    }

//    @Test
//    public void constraintViolation() throws Exception {
//        mockMvc.perform(
//                post("/contactPersons")
//                        .session(supervisorSession())
//                        .content("{}"))
//               .andExpect(status().isBadRequest());
//    }

    @Test
    public void deleteAllowedForSupervisor() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthToken
    public void deleteForbiddenForEmployee() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

//    @Test
//    public void updateCompanyForbiddenForEmployee() throws Exception {
//        ContactPerson contactPerson = dataOnDemand.getRandomObject();
//        mockMvc.perform(
//                put("/contactPersons/" + contactPerson.getId() + "/company")
//                        .session(employeeSession())
//                        .header("Content-Type", "text/uri-list")
//                        .content("companies/0")
//        )
//                .andExpect(status().isForbidden());
//    }

//    @Test
//    public void updateCompanyAllowedForSupervisor() throws Exception {
//        ContactPerson contactPerson = dataOnDemand.getRandomObject();
//        mockMvc.perform(
//                put("/contactPersons/" + contactPerson.getId() + "/company")
//                        .session(supervisorSession())
//                        .header("Content-Type", "text/uri-list")
//                        .content("companies/0")
//        )
//                .andExpect(status().isNoContent());
//    }

//    @Test
//    public void deleteCompanyForbiddenForSupervisor() throws Exception {
//        ContactPerson contactPerson = dataOnDemand.getRandomObject();
//        mockMvc.perform(
//                delete("/contactPersons/" + contactPerson.getId() + "/company")
//                        .session(supervisorSession())
//        )
//                .andExpect(status().isForbidden());
//    }

}

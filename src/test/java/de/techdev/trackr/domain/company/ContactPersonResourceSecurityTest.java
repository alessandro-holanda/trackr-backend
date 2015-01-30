package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@Sql("contactPerson/resourceTest.sql")
@Sql(value = "contactPerson/resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_SUPERVISOR")
public class ContactPersonResourceSecurityTest extends AbstractDomainResourceSecurityTest {


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

//    @Test
//    public void postAllowedForSupervisor() throws Exception {
//        assertThat(create(supervisorSession()), isCreated());
//    }

//    @Test
//    public void putAllowedForSupervisor() throws Exception {
//        assertThat(update(supervisorSession()), isUpdated());
//    }

//    @Test
//    public void putNotAllowedForEmployee() throws Exception {
//        assertThat(update(employeeSession()), isForbidden());
//    }

//    @Test
//    public void patchAllowedForSupervisor() throws Exception {
//        assertThat(updateViaPatch(supervisorSession(), "{\"firstName\": \"Test\"}"), isUpdated());
//    }

//    @Test
//    public void patchNotAllowedForEmployee() throws Exception {
//        assertThat(updateViaPatch(employeeSession(), "{\"firstName\": \"Test\"}"), isForbidden());
//    }

//    @Test
//    public void postNotAllowedForEmployee() throws Exception {
//        assertThat(create(employeeSession()), isForbidden());
//    }

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

    protected String getJsonRepresentation(ContactPerson contactPerson) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        jg.writeStartObject()
          .write("firstName", contactPerson.getFirstName())
          .write("lastName", contactPerson.getLastName())
          .write("salutation", contactPerson.getSalutation())
          .write("email", contactPerson.getEmail())
          .write("phone", contactPerson.getPhone())
          .write("company", "/api/companies/" + contactPerson.getCompany().getId());

        if (contactPerson.getRoles() != null) {
            jg.write("roles", contactPerson.getRoles());
        }
        if (contactPerson.getId() != null) {
            jg.write("id", contactPerson.getId());
        }
        jg.writeEnd().close();
        return writer.toString();
    }
}

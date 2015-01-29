package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isMethodNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("address/resourceTest.sql")
@Sql(value = "address/resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class AddressResourceSecurityTest extends AbstractDomainResourceSecurityTest<Address> {

    @Override
    protected String getResourceName() {
        return "addresses";
    }

    @Test
    @OAuthToken
    public void findAllNotExported() throws Exception {
        assertThat(root(), isMethodNotAllowed());
    }

    @Test
    @OAuthToken
    public void one() throws Exception {
        assertThat(one(0L), isAccessible());
    }

//    @Test
//    public void createAllowedForAdmin() throws Exception {
//        assertThat(create(adminSession()), isCreated());
//    }

//    @Test
//    public void putAllowedForAdmin() throws Exception {
//        assertThat(update(adminSession()), isUpdated());
//    }

//    @Test
//    public void patchAllowedForAdmin() throws Exception {
//        assertThat(updateViaPatch(adminSession(), "{\"street\": \"test\"}"), isUpdated());
//    }

//    @Test
//    public void createNotAllowedForSupervisor() throws Exception {
//        assertThat(create(supervisorSession()), isForbidden());
//    }

//    @Test
//    public void putForbiddenForSupervisor() throws Exception {
//        assertThat(update(supervisorSession()), isForbidden());
//    }

//    @Test
//    public void patchForbiddenForSupervisor() throws Exception {
//        assertThat(updateViaPatch(supervisorSession(), "{\"street\": \"test\"}"), isForbidden());
//    }

    @Test
    public void deleteNotExported() throws Exception {
        assertThat(remove(0L), isMethodNotAllowed());
    }

    @Override
    protected String getJsonRepresentation(Address address) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        jg.writeStartObject()
          .write("street", address.getStreet())
          .write("houseNumber", address.getHouseNumber())
          .write("city", address.getCity())
          .write("zipCode", address.getZipCode())
          .write("country", address.getCountry());
        if (address.getId() != null) {
            jg.write("id", address.getId());
        }
        jg.writeEnd().close();
        return writer.toString();
    }
}

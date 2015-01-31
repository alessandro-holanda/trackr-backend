package de.techdev.trackr.domain.company;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("address/resourceTest.sql")
@Sql(value = "address/resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class AddressResourceSecurityTest extends AbstractDomainResourceSecurityTest {

	private AddressJsonGenerator jsonGenerator = new AddressJsonGenerator();

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

    @Test
    public void createAllowedForAdmin() throws Exception {
		String json = jsonGenerator.start().build();
		assertThat(create(json), isCreated());
    }

    @Test
    public void putAllowedForAdmin() throws Exception {
		String json = jsonGenerator.start().apply(c -> c.setId(0L)).build();
		assertThat(update(0L, json), isUpdated());
    }

    @Test
    public void patchAllowedForAdmin() throws Exception {
        assertThat(updateViaPatch(0L, "{\"street\": \"test\"}"), isUpdated());
    }

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

}

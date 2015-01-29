package de.techdev.trackr.domain.project;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.math.BigDecimal;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("resourceTest.sql")
@Sql(value = "resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class ProjectResourceSecurityTest extends AbstractDomainResourceSecurityTest<Project> {

    @Override
    protected String getResourceName() {
        return "projects";
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
    public void createAllowedForAdmin() throws Exception {
        Project project = getNewTransientObject(1);
        assertThat(create(project), isCreated());
    }

//    @Test
//    public void updateAllowedForAdmin() throws Exception {
//        assertThat(update(adminSession()), isUpdated());
//    }

    @Test
    public void deleteAllowedForAdmin() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void createForbiddenForSupervisor() throws Exception {
        Project project = getNewTransientObject(1);
        assertThat(create(project), isForbidden());
    }

//    @Test
//    @OAuthToken(value = "ROLE_SUPERVISOR")
//    public void updateForbiddenForSupervisor() throws Exception {
//        assertThat(update(supervisorSession()), isForbidden());
//    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteForbiddenForSupervisor() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

    @Test
    public void setCompanyAllowedForAdmin() throws Exception {
        assertThat(updateLink(0L, "company", "/companies/0"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void setCompanyForbiddenForSupervisor() throws Exception {
        assertThat(updateLink(0L, "company", "/companies/0"), isForbidden());
    }

    @Test
    public void setDebitorAllowedForAdmin() throws Exception {
        assertThat(updateLink(0L, "debitor", "/companies/0"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void setDebitorForbiddenForSupervisor() throws Exception {
        assertThat(updateLink(0L, "debitor", "/companies/0"), isForbidden());
    }

    @Test
    public void setWorktimesAllowedForAdmin() throws Exception {
        assertThat(updateLink(0L, "workTimes", "/workTimes/0"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void setWorktimesForbiddenForSupervisor() throws Exception {
        assertThat(updateLink(0L, "workTimes", "/workTimes/0"), isForbidden());
    }

    @Test
    public void deleteCompanyAllowedForAdmin() throws Exception {
        assertThat(removeUrl("/projects/0/company"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteCompanyForbiddenForSupervisor() throws Exception {
        assertThat(removeUrl("/projects/0/company"), isForbidden());
    }

    @Test
    public void deleteDebitorAllowedForAdmin() throws Exception {
        assertThat(removeUrl("/projects/0/debitor"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteDebitorForbiddenForSupervisor() throws Exception {
        assertThat(removeUrl("/projects/0/debitor"), isForbidden());
    }

    @Test
    public void deleteWorktimesAllowedForAdmin() throws Exception {
        assertThat(removeUrl("/projects/0/workTimes/0"), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteWorktimesForbiddenForSupervisor() throws Exception {
        assertThat(removeUrl("/projects/0/workTimes/0"), isForbidden());
    }

    public Project getNewTransientObject(int i) {
        Project project = new Project();
        project.setIdentifier("identifier_" + i);
        project.setName("name_" + i);
        project.setDailyRate(BigDecimal.TEN.multiply(new BigDecimal(i)));
        project.setFixedPrice(BigDecimal.TEN.multiply(new BigDecimal(i)));
        project.setHourlyRate(BigDecimal.TEN.multiply(new BigDecimal(i)));
        project.setVolume(i);
        return project;
    }

    @Override
    protected String getJsonRepresentation(Project project) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        jg.writeStartObject()
          .write("name", project.getName())
          .write("identifier", project.getIdentifier())
          .write("volume", project.getVolume())
          .write("hourlyCostRate", project.getDailyRate())
          .write("salary", project.getHourlyRate())
          .write("title", project.getFixedPrice());

        if (project.getCompany() != null) {
            jg.write("company", "/api/companies/" + project.getCompany().getId());
        }

        if (project.getDebitor() != null) {
            jg.write("debitor", "/api/companies/" + project.getDebitor().getId());
        }
        if (project.getId() != null) {
            jg.write("id", project.getId());
        }
        jg.writeEnd().close();
        return writer.toString();
    }
}

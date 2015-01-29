package de.techdev.trackr.domain.employee.sickdays;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.echocat.jomon.testing.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Sql("resourceTest.sql")
@Sql(value = "resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken
public class SickDaysResourceSecurityTest extends AbstractDomainResourceSecurityTest<SickDays> {

    @Override
    protected String getResourceName() {
        return "sickDays";
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void rootIsNotAccessibleForAdmin() throws Exception {
        assertThat(root(), isMethodNotAllowed());
    }

    @Test
    public void oneIsAllowedForEmployee() throws Exception {
        assertThat(one(0L), isAccessible());
    }

    @Test
    @OAuthToken(username = "someone.else@techdev.de")
    public void oneIsForbiddenForOther() throws Exception {
        assertThat(one(0L), isForbidden());
    }

//    @Test
//    public void createIsAllowedForEmployee() throws Exception {
//        assertThat(create(sameEmployeeSessionProvider), isCreated());
//    }

//    @Test
//    @Ignore
//    Not testable at the moment since it returns 400 instead of 403 (it can't convert the JSON because the employee is also restricted).
//    public void createIsForbiddenForOther() throws Exception {
//        assertThat(create(otherEmployeeSessionProvider), isForbidden());
//    }

    @Test
    @OAuthToken("ROLE_ADMIN")
    public void deleteIsAllowedForAdmin() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void deleteIsForbiddenForSupervisor() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

//    @Test
//    public void updateIsAllowedForEmployee() throws Exception {
//        assertThat(update(sameEmployeeSessionProvider), isUpdated());
//    }

//    @Test
//    @Ignore
    // Not testable at the moment since it returns 400 instead of 403 (it can't convert the JSON because the employee is also restricted).
//    public void updateIsForbiddenForOther() throws Exception {
//        assertThat(update(otherEmployeeSessionProvider), isForbidden());
//    }

    @Test
    public void findByEmployeeIsAllowedForEmployee() throws Exception {
        assertThat(oneUrl("/sickDays/search/findByEmployee?employee=0"), isAccessible());
    }

    @Test
    @OAuthToken(username = "someone.else@techdev.de")
    @Ignore
    // Not testable at the moment since it returns 400 instead of 403 (it can't convert the JSON because the employee is also restricted).
    public void findByEmployeeIsForbiddenForOther() throws Exception {
        assertThat(oneUrl("/sickDays/search/findByEmployee?employee=0"), isForbidden());
    }

    @Test
    @OAuthToken("ROLE_ADMIN")
    public void findByStartDateBetweenOrEndDateBetweenIsAllowedForAdmin() throws Exception {
        assertThat(
                oneUrl("/sickDays/search/findByStartDateBetweenOrEndDateBetween?startLower=2014-07-01&startHigher=2014-07-31&endLower=2014-07-08&endHigher=2014-08-09"), isAccessible()
        );
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void findByStartDateBetweenOrEndDateBetweenIsForbiddenForSupervisor() throws Exception {
        assertThat(
                oneUrl("/sickDays/search/findByStartDateBetweenOrEndDateBetween?startLower=2014-07-01&startHigher=2014-07-31&endLower=2014-07-08&endHigher=2014-08-09"), isForbidden()
        );
    }

    @Override
    protected String getJsonRepresentation(SickDays sickDays) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        jg.writeStartObject()
                .write("startDate", sdf.format(sickDays.getStartDate()));

        if (sickDays.getEndDate() != null) {
            jg.write("endDate", sdf.format(sickDays.getEndDate()));
        }

        if (sickDays.getEmployee() != null) {
            jg.write("employee", "/api/employees/" + sickDays.getEmployee().getId());
        }

        if (sickDays.getId() != null) {
            jg.write("id", sickDays.getId());
        }
        jg.writeEnd().close();
        return writer.toString();
    }
}

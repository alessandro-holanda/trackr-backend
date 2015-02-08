package de.techdev.trackr.domain.employee.expenses.report;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("resourceTest.sql")
@Sql(value = AbstractDomainResourceSecurityTest.EMPTY_DATABASE_FILE, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthRequest
public class ReportResourceSecurityTest extends AbstractDomainResourceSecurityTest {

    private ReportJsonGenerator jsonGenerator = new ReportJsonGenerator();

    @Override
    protected String getResourceName() {
        return "travelExpenseReports";
    }

    @Test
    @OAuthRequest("ROLE_ADMIN")
    public void rootNotExported() throws Exception {
        assertThat(root(), isMethodNotAllowed());
    }

    @Test
    @OAuthRequest("ROLE_SUPERVISOR")
    public void oneAllowedForSupervisor() throws Exception {
        assertThat(one(0L), isAccessible());
    }

    @Test
    @OAuthRequest(username = "someone.else@techdev.de")
    public void oneNotAllowedForOther() throws Exception {
        assertThat(one(0L), isForbidden());
    }

    @Test
    public void oneAllowedForSelf() throws Exception {
        assertThat(one(0L), isAccessible());
    }

    @Test
    public void createAllowed() throws Exception {
        String json = jsonGenerator.start().withDebitorId(0L).withEmployeeId(0L).build();
        assertThat(create(json), isCreated());
    }

    @Test
    public void updateNotAllowedForSelf() throws Exception {
        String json = jsonGenerator.start().withDebitorId(0L).withEmployeeId(0L).apply(r -> r.setId(0L)).build();
        assertThat(update(0L, json), isForbidden());
    }

    @Test
    @Ignore("yields 400 instead of 403 ?")
    @OAuthRequest(username = "someone.else@techdev.de")
    public void updateForbiddenForOther() throws Exception {
        String json = jsonGenerator.start().withDebitorId(0L).withEmployeeId(0L).apply(r -> r.setId(0L)).build();
        assertThat(update(0L, json), isForbidden());
    }

    @Test
    public void deleteAllowedForOwnerIfPending() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    public void deleteForbiddenForOwnerIfSubmitted() throws Exception {
        assertThat(remove(1L), isForbidden());
    }

    @Test
    @OAuthRequest("ROLE_ADMIN")
    public void deleteAllowedForAdmin() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthRequest(username = "someone.else@techdev.de")
    public void deleteForbiddenForOtherEvenIfPending() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

    @Test
    @OAuthRequest("ROLE_SUPERVISOR")
    public void pdfExport() throws Exception {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(host + "/travelExpenseReports/0/pdf", byte[].class);
        assertThat(response, isAccessible());
    }

    @Test
    public void pdfExportAsEmployee() throws Exception {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(host + "/travelExpenseReports/0/pdf", byte[].class);
        assertThat(response, isAccessible());
    }

//    @Test
//    public void updateEmployeeNotAllowedForSupervisor() throws Exception {
//        assertThat(updateLink(supervisorSession(), "employee", "/employees/0"), isForbidden());
//    }

//    @Test
//    public void addTravelExpenseAllowedForSelf() throws Exception {
//        assertThat(updateLink(sameEmployeeSessionProvider, "expenses", "/travelExpenses/0"), isNoContent());
//    }

//    @Test
//    public void addTravelExpenseNotAllowedForOther() throws Exception {
//        assertThat(updateLink(otherEmployeeSessionProvider, "expenses", "/travelExpenses/0"), isForbidden());
//    }

//    @Test
//    public void submitNotAllowedForOtherSupervisor() throws Exception {
//        Report travelExpenseReport = dataOnDemand.getRandomObject();
//        travelExpenseReport.setStatus(Report.Status.PENDING);
//        repository.save(travelExpenseReport);
//        mockMvc.perform(
//                put("/travelExpenseReports/" + travelExpenseReport.getId() + "/submit")
//                        .session(supervisorSession(travelExpenseReport.getEmployee().getEmail() + 1))
//        )
//                .andExpect(status().isForbidden());
//
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        Report one = repository.findOne(travelExpenseReport.getId());
//        assertThat(one.getStatus(), is(Report.Status.PENDING));
//    }

//    @Test
//    public void approveNotAllowedForOwningSupervisor() throws Exception {
//        Report travelExpenseReport = dataOnDemand.getRandomObject();
//        travelExpenseReport.setStatus(Report.Status.SUBMITTED);
//        repository.save(travelExpenseReport);
//        mockMvc.perform(
//                put("/travelExpenseReports/" + travelExpenseReport.getId() + "/approve")
//                        .session(supervisorSession(travelExpenseReport.getEmployee().getEmail()))
//        )
//                .andExpect(status().isForbidden());
//
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        Report one = repository.findOne(travelExpenseReport.getId());
//        assertThat(one.getStatus(), is(Report.Status.SUBMITTED));
//    }

//    @Test
//    public void approveAllowedForSupervisor() throws Exception {
//        Report travelExpenseReport = dataOnDemand.getRandomObject();
//        travelExpenseReport.setStatus(Report.Status.SUBMITTED);
//        repository.save(travelExpenseReport);
//        mockMvc.perform(
//                put("/travelExpenseReports/" + travelExpenseReport.getId() + "/approve")
//                        .session(supervisorSession(travelExpenseReport.getEmployee().getEmail() + 1))
//        )
//                .andExpect(status().isNoContent());
//
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        Report one = repository.findOne(travelExpenseReport.getId());
//        assertThat(one.getStatus(), is(Report.Status.APPROVED));
//
//    }

//    @Test
//    public void rejectAllowedForSupervisor() throws Exception {
//        Report travelExpenseReport = dataOnDemand.getRandomObject();
//        travelExpenseReport.setStatus(Report.Status.SUBMITTED);
//        repository.save(travelExpenseReport);
//        mockMvc.perform(
//                put("/travelExpenseReports/" + travelExpenseReport.getId() + "/reject")
//                        .session(supervisorSession(travelExpenseReport.getEmployee().getEmail() + 1))
//        )
//                .andExpect(status().isNoContent());
//
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        Report one = repository.findOne(travelExpenseReport.getId());
//        assertThat(one.getStatus(), is(Report.Status.REJECTED));
//
//    }

}

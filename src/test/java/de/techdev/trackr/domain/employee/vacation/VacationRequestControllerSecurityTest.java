package de.techdev.trackr.domain.employee.vacation;

import de.techdev.test.OAuthRequest;
import de.techdev.trackr.core.web.MockMvcTest2;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isAccessible;
import static de.techdev.trackr.domain.DomainResourceTestMatchers2.isForbidden;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("resourceTest.sql")
@Sql("tableUuidMapping.sql")
@Sql(value = AbstractDomainResourceSecurityTest.EMPTY_DATABASE_FILE, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthRequest("ROLE_SUPERVISOR")
public class VacationRequestControllerSecurityTest extends MockMvcTest2 {

    @Test
    @OAuthRequest("ROLE_SUPERVISOR")
    public void approveNotAllowedForSupervisorOnOwnVacationRequest() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(host + "/vacationRequests/0/approve", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertThat(response, isForbidden());
        ResponseEntity<VacationRequest> vacationRequest = restTemplate.getForEntity(host + "/vacationRequests/0", VacationRequest.class);
        assertThat(vacationRequest.getBody().getStatus(), is(VacationRequest.VacationRequestStatus.PENDING));
    }

    @Test
    @OAuthRequest
    public void approveNotAllowedForEmployees() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(host + "/vacationRequests/0/approve", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertThat(response, isForbidden());
        ResponseEntity<VacationRequest> vacationRequest = restTemplate.getForEntity(host + "/vacationRequests/0", VacationRequest.class);
        assertThat(vacationRequest.getBody().getStatus(), is(VacationRequest.VacationRequestStatus.PENDING));
    }

    @Test
    @OAuthRequest(value = "ROLE_SUPERVISOR", username = "supervisor@techdev.de")
    public void approveAllowedForOtherSupervisor() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(host + "/vacationRequests/0/approve", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertThat(response, isAccessible());
        ResponseEntity<VacationRequest> vacationRequest = restTemplate.getForEntity(host + "/vacationRequests/0", VacationRequest.class);
        assertThat(vacationRequest.getBody().getStatus(), is(VacationRequest.VacationRequestStatus.APPROVED));
    }

    @Test
    @OAuthRequest(value = "ROLE_SUPERVISOR", username = "supervisor@techdev.de")
    public void rejectAllowedForSupervisor() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(host + "/vacationRequests/0/reject", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertThat(response, isAccessible());
        ResponseEntity<VacationRequest> vacationRequest = restTemplate.getForEntity(host + "/vacationRequests/0", VacationRequest.class);
        assertThat(vacationRequest.getBody().getStatus(), is(VacationRequest.VacationRequestStatus.REJECTED));
    }

    @Test
    public void selfRejectForbiddenForSupervisor() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(host + "/vacationRequests/0/reject", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertThat(response, isForbidden());
        ResponseEntity<VacationRequest> vacationRequest = restTemplate.getForEntity(host + "/vacationRequests/0", VacationRequest.class);
        assertThat(vacationRequest.getBody().getStatus(), is(VacationRequest.VacationRequestStatus.PENDING));
    }
}

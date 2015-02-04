package de.techdev.trackr.domain.employee.vacation;

import de.techdev.trackr.TransactionalIntegrationTest;
import de.techdev.trackr.domain.employee.Employee;
import de.techdev.trackr.domain.employee.EmployeeDataOnDemand;
import de.techdev.trackr.util.LocalDateUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.echocat.jomon.testing.BaseMatchers.hasSize;
import static org.echocat.jomon.testing.BaseMatchers.isNotEmpty;
import static org.hamcrest.MatcherAssert.assertThat;

public class VacationRequestRepositoryTest extends TransactionalIntegrationTest {
    
    @Autowired
    private VacationRequestDataOnDemand vacationRequestDataOnDemand;

    @Autowired
    private EmployeeDataOnDemand employeeDataOnDemand;

    @Autowired
    private VacationRequestRepository vacationRequestRepository;

    @Before
    public void setUp() throws Exception {
        vacationRequestDataOnDemand.init();
    }

    @Test
    public void findBySubmissionTimeBefore() throws Exception {
        VacationRequest vacationRequest = vacationRequestDataOnDemand.getRandomObject();
        vacationRequest.setSubmissionTime(LocalDateUtil.fromLocalDate(LocalDate.now().minusDays(8)));
        vacationRequest.setStatus(VacationRequest.VacationRequestStatus.PENDING);
        vacationRequestRepository.save(vacationRequest);
        List<VacationRequest> all = vacationRequestRepository.findBySubmissionTimeBeforeAndStatus(LocalDateUtil.fromLocalDate(LocalDate.now().minusDays(7)), VacationRequest.VacationRequestStatus.PENDING);
        assertThat(all, isNotEmpty());
    }

    @Test
    public void findByApprovedBetween() {
        Employee employee = employeeDataOnDemand.getRandomObject();

        Date start = LocalDateUtil.fromLocalDate(LocalDate.of(2014, 10, 1));
        Date end   = LocalDateUtil.fromLocalDate(LocalDate.of(2014, 12, 8));

        VacationRequest vr1 = new VacationRequest();
        vr1.setEmployee(employee);
        vr1.setStartDate(start);
        vr1.setEndDate(end);
        vr1.setStatus(VacationRequest.VacationRequestStatus.APPROVED);
        vacationRequestRepository.save(vr1);

        VacationRequest vr2 = new VacationRequest();
        vr2.setEmployee(employee);
        vr2.setStartDate(start);
        vr2.setEndDate(end);
        vr2.setStatus(VacationRequest.VacationRequestStatus.REJECTED);
        vacationRequestRepository.save(vr2);

        List<VacationRequest> all = vacationRequestRepository
                .findByStartDateBetweenOrEndDateBetweenAndStatus(start, end, start, end, VacationRequest.VacationRequestStatus.APPROVED);
        assertThat(all, hasSize(1));
    }
}

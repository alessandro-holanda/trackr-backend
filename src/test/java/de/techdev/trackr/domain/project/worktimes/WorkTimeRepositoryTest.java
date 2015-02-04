package de.techdev.trackr.domain.project.worktimes;

import de.techdev.trackr.TransactionalIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.echocat.jomon.testing.BaseMatchers.isGreaterThanOrEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class WorkTimeRepositoryTest extends TransactionalIntegrationTest {

    @Autowired
    private WorkTimeRepository workTimeRepository;

    @Autowired
    private WorkTimeDataOnDemand workTimeDataOnDemand;

    @Before
    public void setUp() throws Exception {
        workTimeDataOnDemand.init();
    }

    /**
     * This finder must only respect the date but not time part of the second parameter.
     *
     * @throws Exception
     */
    @Test
    public void findByEmployeeAndDateOnlyRespectsDatePart() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        WorkTime workTime1 = workTimeRepository.findOne(20L);
        workTime1.setDate(sdf.parse("2014-03-04 10:00"));
        workTimeRepository.save(workTime1);
        WorkTime workTime2 = workTimeRepository.findOne(21L);
        workTime2.setEmployee(workTime1.getEmployee());
        workTime2.setDate(sdf.parse("2014-03-04 11:00"));
        workTimeRepository.save(workTime2);

        List<WorkTime> workTimes = workTimeRepository.findByEmployeeAndDateOrderByStartTimeAsc(workTime1.getEmployee(), sdf.parse("2014-03-04 09:00:00"));
        assertThat(workTimes.size(), isGreaterThanOrEqualTo(2));
    }

    @Test
    public void findByEmployeeAndDateBetweenOrderByDateAscStartTimeAsc() throws Exception {
        WorkTime workTime1 = workTimeRepository.findOne(20L);
        WorkTime workTime2 = workTimeRepository.findOne(21L);
        workTime2.setEmployee(workTime1.getEmployee());
        workTimeRepository.save(workTime2);
        Date low, high;
        if(workTime1.getDate().compareTo(workTime2.getDate()) <= 0) {
            low = workTime1.getDate();
            high = workTime2.getDate();
        } else {
            low = workTime2.getDate();
            high = workTime1.getDate();
        }
        List<WorkTime> all = workTimeRepository.findByEmployeeAndDateBetweenOrderByDateAscStartTimeAsc(workTime1.getEmployee(), low, high);
        assertThat(all.size(), isGreaterThanOrEqualTo(2));
    }

    @Test
    public void findByProjectAndDateBetweenOrderByDateAscStartTimeAsc() throws Exception {
        WorkTime workTime1 = workTimeRepository.findOne(20L);
        WorkTime workTime2 = workTimeRepository.findOne(21L);
        workTime2.setProject(workTime1.getProject());
        workTimeRepository.save(workTime2);
        Date low, high;
        if(workTime1.getDate().compareTo(workTime2.getDate()) <= 0) {
            low = workTime1.getDate();
            high = workTime2.getDate();
        } else {
            low = workTime2.getDate();
            high = workTime1.getDate();
        }
        List<WorkTime> all = workTimeRepository.findByProjectAndDateBetweenOrderByDateAscStartTimeAsc(workTime1.getProject(), low, high);
        assertThat(all.size(), isGreaterThanOrEqualTo(2));
    }
}

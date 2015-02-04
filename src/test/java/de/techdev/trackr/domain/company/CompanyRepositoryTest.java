package de.techdev.trackr.domain.company;

import de.techdev.trackr.TransactionalIntegrationTest;
import de.techdev.trackr.domain.project.Project;
import de.techdev.trackr.domain.project.ProjectDataOnDemand;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CompanyRepositoryTest extends TransactionalIntegrationTest {

    @Autowired
    private CompanyDataOnDemand companyDataOnDemand;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContactPersonDataOnDemand contactPersonDataOnDemand;

    @Autowired
    private ProjectDataOnDemand projectDataOnDemand;

    @Test
    public void deleteWithContactPersons() throws Exception {
        ContactPerson contactPerson = contactPersonDataOnDemand.getRandomObject();
        companyRepository.delete(contactPerson.getCompany());
    }

    @Test
    public void deleteWithProject() throws Exception {
        Project project = projectDataOnDemand.getRandomObject();
        companyRepository.delete(project.getCompany());
    }
}

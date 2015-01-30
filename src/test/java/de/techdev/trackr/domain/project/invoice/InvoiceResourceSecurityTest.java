package de.techdev.trackr.domain.project.invoice;

import de.techdev.test.OAuthToken;
import de.techdev.trackr.domain.AbstractDomainResourceSecurityTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import static de.techdev.trackr.domain.DomainResourceTestMatchers2.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql("resourceTest.sql")
@Sql(value = "resourceTestCleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@OAuthToken("ROLE_ADMIN")
public class InvoiceResourceSecurityTest extends AbstractDomainResourceSecurityTest {

    @Override
    protected String getResourceName() {
        return "invoices";
    }

    protected String getJsonRepresentation(Invoice invoice) {
        StringWriter writer = new StringWriter();
        JsonGenerator jg = jsonGeneratorFactory.createGenerator(writer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        jg.writeStartObject()
                .write("identifier", invoice.getIdentifier())
                .write("invoiceState", invoice.getInvoiceState().toString())
                .write("invoiceTotal", invoice.getInvoiceTotal())
                .write("debitor", "/companies/" + invoice.getDebitor().getId())
                .write("creationDate", sdf.format(invoice.getCreationDate()));
        if (invoice.getDueDate() != null) {
            jg.write("dueDate", sdf.format(invoice.getDueDate()));
        }
        if (invoice.getId() != null) {
            jg.write("id", invoice.getId());
        }
        jg.writeEnd().close();
        return writer.toString();
    }

    @Test
    public void rootIsAccessibleForAdmin() throws Exception {
        assertThat(root(), isAccessible());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void rootIsForbiddenForSupervisor() throws Exception {
        assertThat(root(), isForbidden());
    }

    @Test
    public void oneIsAccessibleForAdmin() throws Exception {
        assertThat(one(0L), isAccessible());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void oneIsForbiddenForSupervisor() throws Exception {
        assertThat(one(0L), isForbidden());
    }

    @Test
    public void findByInvoiceStateIsAccessibleForAdmin() throws Exception {
        assertThat(oneUrl("/invoices/search/findByInvoiceState?state=OUTSTANDING"), isAccessible());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void findByInvoiceStateIsForbiddenForSupervisor() throws Exception {
        assertThat(oneUrl("/invoices/search/findByInvoiceState?state=OUTSTANDING"), isForbidden());
    }

    @Test
    public void findByIdentifierLikeAndInvoiceStateIsAccessibleForAdmin() throws Exception {
        assertThat(oneUrl("/invoices/search/findByIdentifierLikeIgnoreCaseAndInvoiceState?identifier=TEST&state=OUTSTANDING"), isAccessible());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void findByIdentifierLikeAndInvoiceStateIsForbiddenForSupervisor() throws Exception {
        assertThat(oneUrl("/invoices/search/findByIdentifierLikeIgnoreCaseAndInvoiceState?identifier=TEST&state=OUTSTANDING"), isForbidden());
    }

//    @Test
//    public void findByCreationDateBetweenAccessibleForAdmin() throws Exception {
//        mockMvc.perform(
//                get("/invoices/search/findByCreationDateBetween")
//                        .param("start", String.valueOf(new Date().getTime()))
//                        .param("end", String.valueOf(new Date().getTime()))
//                        .session(adminSession())
//        )
//                .andExpect(status().isOk());
//    }

//    @Test
//    public void findByCreationDateBetweenForbiddenForSupervisor() throws Exception {
//        mockMvc.perform(
//                get("/invoices/search/findByCreationDateBetween")
//                        .param("start", String.valueOf(new Date().getTime()))
//                        .param("end", String.valueOf(new Date().getTime()))
//                        .session(supervisorSession())
//        )
//                .andExpect(status().isForbidden());
//    }

//    @Test
//    public void adminCanCreate() throws Exception {
//        assertThat(create(adminSession()), isCreated());
//    }

//    @Test
//    public void supervisorCannotCreate() throws Exception {
//        assertThat(create(supervisorSession()), isForbidden());
//    }

    @Test
    public void adminCanDelete() throws Exception {
        assertThat(remove(0L), isNoContent());
    }

    @Test
    @OAuthToken("ROLE_SUPERVISOR")
    public void supervisorCannotDelete() throws Exception {
        assertThat(remove(0L), isForbidden());
    }

//    @Test
//    public void adminCanSetPaid() throws Exception {
//        Invoice invoice = dataOnDemand.getRandomObject();
//        invoice.setInvoiceState(Invoice.InvoiceState.OUTSTANDING);
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        repository.save(invoice);
//        SecurityContextHolder.getContext().setAuthentication(null);
//        mockMvc.perform(
//                post("/invoices/" + invoice.getId() + "/markPaid")
//                        .session(adminSession())
//        );
//        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
//        Invoice one = repository.findOne(invoice.getId());
//        SecurityContextHolder.getContext().setAuthentication(null);
//        assertThat(one.getInvoiceState(), is(Invoice.InvoiceState.PAID));
//    }

//    @Test
//    public void supervisorCannotSetPaid() throws Exception {
//        Invoice invoice = dataOnDemand.getRandomObject();
//        mockMvc.perform(
//                post("/invoices/" + invoice.getId() + "/markPaid")
//                        .session(supervisorSession())
//        ).andExpect(status().isForbidden());
//    }
}

package de.techdev.trackr.domain;

import de.techdev.trackr.core.web.MockMvcTest2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Arrays.asList;

public abstract class AbstractDomainResourceSecurityTest<T> extends MockMvcTest2 {

    protected abstract String getResourceName();

    protected abstract String getJsonRepresentation(T item);

    private HttpEntity<String> getJsonEntity(String content) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            put("Content-Type", asList("application/json; charset=utf-8"));
        }};
        return new HttpEntity<>(content, headers);
    }

    protected ResponseEntity root() throws Exception {
        return restTemplate.getForEntity(host + "/" + getResourceName(), String.class);
    }

    protected ResponseEntity one(Long id) throws Exception {
        return oneUrl("/" + getResourceName() + "/" + id);
    }

    protected ResponseEntity oneUrl(String url) throws Exception {
        return restTemplate.getForEntity(host + url, String.class);
    }

    protected ResponseEntity create(T newObject) throws Exception {
        return restTemplate.exchange(host + "/" + getResourceName() + "/", HttpMethod.POST, getJsonEntity(getJsonRepresentation(newObject)), String.class);
    }

//    /**
//     * Get a random object and try to PUT it to the resource path
//     *
//     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
//     * @return The result actions to perform further tests on.
//     * @throws Exception
//     */
//    protected ResponseEntity update(Long id) throws Exception {
//        T randomObject = dataOnDemand.getRandomObject();
//        HttpEntity<?> request = getJsonEntity(getJsonRepresentation(randomObject));
//        return restTemplate.exchange(host + "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject), HttpMethod.PUT, request, String.class);
//    }

    /**
     * Perform a PUT on a link of a random resource (with header Content-Type: text/uri-list)
     *
     * @param linkName    The name of the link, will be appended to the URI of the resource (e.g. /company/0/contactPersons -> linkName = contactPersons).
     * @param linkContent The content to PUT, e.g. /contactersons/0
     */
    protected ResponseEntity updateLink(Long id, String linkName, String linkContent) throws Exception {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Content-Type", asList("text/uri-list"));
        HttpEntity<?> request = new HttpEntity<>(linkContent, headers);
        return restTemplate.exchange(host + "/" + getResourceName() + "/" + id + "/" + linkName, HttpMethod.PUT, request, String.class);
    }

//    /**
//     * Get a random object and try to PATCH with the given string it to the resource path.
//     *
//     * @param session The mock session to use, e.g. admin or employee
//     * @return The result actions to perform further tests on.
//     * @throws Exception
//     */
//    protected ResponseEntity updateViaPatch(String patch) throws Exception {
//        T randomObject = dataOnDemand.getRandomObject();
//        return restTemplate.exchange(host + "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject), HttpMethod.PATCH, getJsonEntity(patch), String.class);
//    }

    protected ResponseEntity remove(Long id) throws Exception {
        return removeUrl("/" + getResourceName() + "/" + id);
    }

    protected ResponseEntity removeUrl(String url) throws Exception {
        return restTemplate.exchange(host + url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }
}
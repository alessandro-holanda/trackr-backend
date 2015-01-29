package de.techdev.trackr.domain;

import de.techdev.trackr.core.web.MockMvcTest;
import de.techdev.trackr.core.web.MockMvcTest2;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.echocat.jomon.runtime.CollectionUtils.asSet;

public abstract class AbstractDomainResourceTest2<T> extends MockMvcTest2 {

    @Autowired
    protected AbstractDataOnDemand<T> dataOnDemand;

    @Autowired
    protected CrudRepository<T, Long> repository;

    private OAuth2AccessToken token;

    protected abstract String getResourceName();

    protected abstract String getJsonRepresentation(T item);

    @Before
    public void setUpAbstractDomainResourceTest() throws Exception {
        dataOnDemand.init();
        token = new DefaultOAuth2AccessToken("token");
    }

    @After
    public void tearDown() throws Exception {
        tokenStore.removeAccessToken(token);
    }

    protected void createToken(MockHttpSession session) {
        OAuth2Request clientAuthentication = new OAuth2Request(new HashMap<>(), "trackr-page", null, true, asSet("read", "write"), asSet("techdev-services"), null, null, null);
        tokenStore.storeAccessToken(token, new OAuth2Authentication(clientAuthentication, getAuthenticationFromSession(session)));
    }

    //TODO replace this, don't base it on sessions anymore anyway!
    private Authentication getAuthenticationFromSession(MockHttpSession session) {
        MockMvcTest.MockSecurityContext securityContext = (MockMvcTest.MockSecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        return securityContext.getAuthentication();
    }

    private HttpEntity<String> getJsonEntity(String content) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            put("Content-Type", asList("application/json; charset=utf-8"));
        }};
        return new HttpEntity<>(content, headers);
    }

    /**
     * Access the root of a resource via GET.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity root(MockHttpSession session) throws Exception {
        createToken(session);
        return restTemplate.getForEntity(host + "/" + getResourceName(), String.class);
    }

    /**
     * Access a single random object via GET.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity one(MockHttpSession session) throws Exception {
        return one((object) -> session);
    }

    /**
     * Access a single random object via GET.
     *
     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity one(Function<T, MockHttpSession> sessionProvider) throws Exception {
        T randomObject = dataOnDemand.getRandomObject();
        return oneUrl(sessionProvider.apply(randomObject), "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject));
    }

    /**
     * Access a URL via GET.
     *
     * @param session The The mock session to use, e.g. admin or employee
     * @param url     The URL to access.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity oneUrl(MockHttpSession session, String url) throws Exception {
        createToken(session);
        return restTemplate.getForEntity(host + url, String.class);
    }

    /**
     * Get a new transient object and try to POST it to the resource path.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity create(MockHttpSession session) throws Exception {
        return create((object) -> session);
    }

    /**
     * Get a new transient object and try to POST it to the resource path.
     *
     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity create(Function<T, MockHttpSession> sessionProvider) throws Exception {
        T newObject = dataOnDemand.getNewTransientObject(500);
        createToken(sessionProvider.apply(newObject));
        return restTemplate.exchange(host + "/" + getResourceName() + "/", HttpMethod.POST, getJsonEntity(getJsonRepresentation(newObject)), String.class);
    }

    /**
     * Get a random object and try to PUT it to the resource path.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity update(MockHttpSession session) throws Exception {
        return update((object) -> session);
    }

    /**
     * Get a random object and try to PUT it to the resource path
     *
     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity update(Function<T, MockHttpSession> sessionProvider) throws Exception {
        T randomObject = dataOnDemand.getRandomObject();
        createToken(sessionProvider.apply(randomObject));
        HttpEntity<?> request = getJsonEntity(getJsonRepresentation(randomObject));
        return restTemplate.exchange(host + "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject), HttpMethod.PUT, request, String.class);
    }

    /**
     * Perform a PUT on a link of a random resource (with header Content-Type: text/uri-list)
     *
     * @param session     The mock session to use, e.g. admin or employee
     * @param linkName    The name of the link, will be appended to the URI of the resource (e.g. /company/0/contactPersons -> linkName = contactPersons).
     * @param linkContent The content to PUT, e.g. /contactersons/0
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity updateLink(MockHttpSession session, String linkName, String linkContent) throws Exception {
        return updateLink((object) -> session, linkName, linkContent);
    }

    /**
     * Perform a PUT on a link of a random resource (with header Content-Type: text/uri-list)
     *
     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
     * @param linkName    The name of the link, will be appended to the URI of the resource (e.g. /company/0/contactPersons -> linkName = contactPersons).
     * @param linkContent The content to PUT, e.g. /contactersons/0
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity updateLink(Function<T, MockHttpSession> sessionProvider, String linkName, String linkContent) throws Exception {
        T randomObject = dataOnDemand.getRandomObject();
        createToken(sessionProvider.apply(randomObject));

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Content-Type", asList("text/uri-list"));
        HttpEntity<?> request = new HttpEntity<>(linkContent, headers);

        return restTemplate.exchange(host + "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject) + "/" + linkName, HttpMethod.PUT, request, String.class);
    }

    /**
     * Get a random object and try to PATCH with the given string it to the resource path.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity updateViaPatch(MockHttpSession session, String patch) throws Exception {
        createToken(session);
        T randomObject = dataOnDemand.getRandomObject();
        return restTemplate.exchange(host + "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject), HttpMethod.PATCH, getJsonEntity(patch), String.class);
    }

    /**
     * Get a random object and try to DELETE it.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity remove(MockHttpSession session) throws Exception {
        return remove((object) -> session);
    }

    /**
     * Get a random object and try to DELETE it.
     *
     * @param sessionProvider Converts the random object to a {@link org.springframework.mock.web.MockHttpSession}. This can be used to set the session to a specific employee.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity remove(Function<T, MockHttpSession> sessionProvider) throws Exception {
        T randomObject = dataOnDemand.getRandomObject();
        createToken(sessionProvider.apply(randomObject));
        return removeUrl(sessionProvider.apply(randomObject), "/" + getResourceName() + "/" + dataOnDemand.getId(randomObject));
    }

    /**
     * Perform a DELETE on a URL.
     *
     * @param session The mock session to use, e.g. admin or employee
     * @param url     The URL to access.
     * @return The result actions to perform further tests on.
     * @throws Exception
     */
    protected ResponseEntity removeUrl(MockHttpSession session, String url) throws Exception {
        createToken(session);
        return restTemplate.exchange(host + url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }
}
package com.andromeda.galaxy.web.rest;

import com.andromeda.galaxy.AndromedaApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LogoutResource REST controller.
 *
 * @see LogoutResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AndromedaApp.class)
public class LogoutResourceIntTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private final static String ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
        ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsIm" +
        "p0aSI6ImQzNWRmMTRkLTA5ZjYtNDhmZi04YTkzLTdjNmYwMzM5MzE1OSIsImlhdCI6MTU0M" +
        "Tk3MTU4MywiZXhwIjoxNTQxOTc1MTgzfQ.QaQOarmV8xEUYV7yvWzX3cUE_4W1luMcWCwpr" +
        "oqqUrg";

    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;

    private MockMvc restLogoutMockMvc;

    @Before
    public void before() {
        LogoutResource logoutResource = new LogoutResource(restTemplateFactory(), accessTokenUri);
        this.restLogoutMockMvc = MockMvcBuilders.standaloneSetup(logoutResource)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    public void getLogoutInformation() throws Exception {
        String logoutUrl = accessTokenUri.replace("token", "logout");
        restLogoutMockMvc.perform(post("/api/logout"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.logoutUrl").value(logoutUrl))
            .andExpect(jsonPath("$.idToken").value(ID_TOKEN));
    }

    private UserInfoRestTemplateFactory restTemplateFactory() {
        UserInfoRestTemplateFactory factory = mock(UserInfoRestTemplateFactory.class);
        Map<String, Object> idToken = new HashMap<>();
        idToken.put("id_token", ID_TOKEN);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("my-fun-token");
        token.setAdditionalInformation(idToken);
        when(factory.getUserInfoRestTemplate()).thenReturn(mock(OAuth2RestTemplate.class));
        when(factory.getUserInfoRestTemplate().getAccessToken()).thenReturn(token);
        return factory;
    }
}

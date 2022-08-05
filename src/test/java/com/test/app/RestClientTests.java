package com.test.app;

import com.test.app.dao.Assignee;
import com.test.app.dao.Issues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RestClientTests {

    @MockBean
    DatabaseClient mockDB;

    @MockBean
    WebClient mockClient;

    @Autowired
    RestClient restClient;

    private WebClient.RequestHeadersUriSpec mockHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
    private WebClient.RequestHeadersSpec mockHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
    private Flux<Issues> issuesFlux;
    private static final Long ID = (long) 1;
    private static final String TITLE = "title";
    public static final String STATE = "state";
    public static final String BODY = "body";
    private Assignee assignee;
    private Issues issueWithAssignee;
    private Issues issueWithoutAssignee;

    @BeforeEach
    public void before(){
        reset(mockDB,mockClient);
        assignee = Assignee.builder().id(ID).build();
        issueWithAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).assignee(assignee).assigneeId(ID).build();
        issueWithoutAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).build();
        issuesFlux = Flux.just(issueWithAssignee,issueWithoutAssignee);
    }

    @Test
    public void callAPITest(){
        when(mockClient.get()).thenReturn(mockHeadersUriSpec);
        when(mockHeadersUriSpec.uri(String.format(RestClient.URI_STR, LocalDateTime.now().plusWeeks(-2).format(ISO_LOCAL_DATE_TIME)))).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.header(String.format(RestClient.HEADER_STR,"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"))).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.exchangeToFlux(RestClient::getClientResponse)).thenReturn(issuesFlux);
        restClient.callAPI();
        verify(mockDB,times(2)).insertIssue(any());
    }

    @Test
    public void getClientResponseOkTest(){

    }

    @Test
    public void getClientResponseBadTest(){

    }

}

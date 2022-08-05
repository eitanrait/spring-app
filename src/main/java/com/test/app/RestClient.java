package com.test.app;

import com.test.app.dao.Issues;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Log4j2
@Configuration
public class RestClient {

    public static final String URI_STR = "/repos/facebook/react/issues?since=%s&per_page=100";
    public static final String HEADER_STR = "Authorization: token %s";


    /** *** IMPORTANT *********
     ******* READ ME **********
     *
     * Replace the value in the String named TOKEN with your own OAuth token from GitHub
     * Reference for creating your own authentication token:
     * https://docs.github.com/en/enterprise-server@3.4/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
     *
     * THANKS
     * ***************** **/
     
    public static final String TOKEN = "REPLACE WITH YOUR OAUTH TOKEN";
    @Autowired
    private WebClient client;
    @Autowired
    DatabaseClient db;

    /*
     *    Since the number of issues was low, setting the API call to 100 issues was sufficient
     */
    public void callAPI() {
        client.get()
                .uri(String.format(URI_STR, LocalDateTime.now().plusWeeks(-2).format(ISO_LOCAL_DATE_TIME)))
                .header(String.format(HEADER_STR, TOKEN))
                .exchangeToFlux(RestClient::getClientResponse)
                .subscribe(db::insertIssue);
    }

    public static Flux<Issues> getClientResponse(Object response) {
        ClientResponse clientResponse = (ClientResponse) response ;
        if (clientResponse.statusCode().equals(HttpStatus.OK))
            return clientResponse.bodyToFlux(Issues.class);
         else
            return clientResponse.createException().flatMapMany(Mono::error);
    }
}
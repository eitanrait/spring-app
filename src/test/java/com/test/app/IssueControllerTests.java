package com.test.app;

import com.test.app.dao.Assignee;
import com.test.app.dao.Comment;
import com.test.app.dao.Issues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class IssueControllerTests {

    @MockBean
    DatabaseClient mockDB;
    @Autowired
    IssuesController issueController;
    private static final Long ID = (long) 1;
    private static final String TITLE = "title";
    public static final String STATE = "state";
    public static final String BODY = "body";
    public static final String COMMENT_STR = "comment";
    private static final Integer ISSUE_COUNT = 1;
    private Assignee assignee;
    private Comment comment;
    private List<Comment> comments;
    private Issues issueWithAssignee;
    private Issues issueWithoutAssignee;
    private List<Issues> issues;
    private List<String> issuesById;
    private ResponseEntity<Integer> okResponse;
    private ResponseEntity<Integer> badResponse;

    @BeforeEach
    public void before(){
        reset(mockDB);
        assignee = Assignee.builder().id(ID).build();
        comment = Comment.builder().comment(COMMENT_STR).build();
        comments = List.of(comment);
        issueWithAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).assignee(assignee).assigneeId(ID).build();
        issueWithoutAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).build();
        issues = Arrays.asList(issueWithAssignee,issueWithoutAssignee);
        issuesById = issues.stream().map(Issues::toString).collect(Collectors.toList());
        okResponse = ResponseEntity.status(HttpStatus.OK).build();
        badResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Test
    public void getIssuesTest() {
        when(mockDB.sortByState()).thenReturn(issues);
        List<String> sortedIssues = issueController.getIssues();
        assert(sortedIssues.equals(issuesById));
    }

    @Test
    public void getIssuesByIdTest(){
        when(mockDB.getIssuesById(ID)).thenReturn(issues);
        List<String> issueControllerIssuesById = issueController.getIssuesById(ID);
        assert(issueControllerIssuesById.equals(issuesById));
    }

    @Test
    public void getIssueCountByAssigneeTest(){
        when(mockDB.getIssueCount(ID)).thenReturn(ISSUE_COUNT);
        Integer issueCount = issueController.getIssuesCountByAssignee(ID);
        assert(issueCount.equals(ISSUE_COUNT));
    }

    @Test
    public void addCommentToIssueOkTest(){
        ResponseEntity<Integer> addOkResponse = issueController.addCommentToIssue(ID,comment);
        verify(mockDB,times(1)).insertComment(ID,comment);
        assert(addOkResponse.equals(okResponse));
    }

    @Test
    public void addCommentToIssueBadTest(){
        doThrow(new RuntimeException()).when(mockDB).insertComment(ID,comment);
        ResponseEntity<Integer> badTestResponse = issueController.addCommentToIssue(ID,comment);
        assert(badTestResponse.equals(badResponse));
    }
}

package com.test.app;

import com.test.app.dao.Assignee;
import com.test.app.dao.Comment;
import com.test.app.dao.Issues;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class DatabaseClientTests {

    @MockBean
    private JdbcTemplate mockJdbcTemplate;
    @Mock
    private Logger log;
    @Autowired
    private DatabaseClient db;
    private static final Long ID = (long) 1;
    private static final String TITLE = "title";
    public static final String STATE = "state";
    public static final String BODY = "body";
    public static final String COMMENT_STR = "comment";
    private static final Integer ISSUE_COUNT = 1;
    private List<Issues> issues;
    private List<Comment> comments;
    private Issues issueWithAssignee;
    private Issues issueWithoutAssignee;
    private Assignee assignee;
    private Comment comment;

    @BeforeEach
    public void before() {
        Mockito.reset(mockJdbcTemplate);
        assignee = Assignee.builder().id(ID).build();
        issueWithAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).assignee(assignee).assigneeId(ID).build();
        issueWithoutAssignee = Issues.builder().id(ID).title(TITLE).state(STATE).body(BODY).build();
        comment = Comment.builder().comment(COMMENT_STR).build();
        issues = Arrays.asList(issueWithAssignee,issueWithoutAssignee);
        comments = List.of(comment);
    }

    @Test
    public void makeTableTest() {
        db.makeTable();
        verify(mockJdbcTemplate,times(1)).execute(DatabaseClient.DROP_TABLE_SQL);
        verify(mockJdbcTemplate,times(1)).execute(DatabaseClient.CREATE_TABLE_ISSUES_SQL);
        verify(mockJdbcTemplate,times(1)).execute(DatabaseClient.CREATE_TABLE_COMMENTS_SQL);
    }

    @Test
    public void insertIssueWithAssigneeTest() {
        //when(log.info(anyString(),any(Long.class)));
        db.insertIssue(issueWithAssignee);
        verify(mockJdbcTemplate,times(1)).update(DatabaseClient.INSERT_ISSUE_WITH_ASSIGNEE_SQL,ID,TITLE,STATE,BODY,ID);
    }


    @Test
    public void insertIssueWithoutAssigneeTest() {
        db.insertIssue(issueWithoutAssignee);
        verify(mockJdbcTemplate,times(1)).update(DatabaseClient.INSERT_ISSUE_WITHOUT_ASSIGNEE_SQL,ID,TITLE,STATE,BODY);
    }

    @Test
    public void insertCommentTest() {
        db.insertComment(ID,comment);
        verify(mockJdbcTemplate,times(1)).update(DatabaseClient.INSERT_COMMENT_SQL,ID,COMMENT_STR);
    }

    @Test
    public void sortByStateTest() {
        when(mockJdbcTemplate.query(eq(DatabaseClient.SORT_BY_STATE_SQL), ArgumentMatchers.<RowMapper<Issues>>any())).thenReturn(issues);
        when(mockJdbcTemplate.query(eq(String.format(DatabaseClient.GET_COMMENTS_SQL,ID)), ArgumentMatchers.<RowMapper<Comment>>any())).thenReturn(comments);
        List<Issues> sortedIssues = db.sortByState();
        assert(sortedIssues.equals(issues));
        sortedIssues.forEach(issue -> {
            assert(issue.getAddedComments().equals(comments));
        });
    }

    @Test
    public void getCommentsTest() {
        when(mockJdbcTemplate.query(eq(String.format(DatabaseClient.GET_COMMENTS_SQL,ID)),ArgumentMatchers.<RowMapper<Comment>>any())).thenReturn(comments);
        List<Comment> returnedComments = db.getComments(ID);
        assert(returnedComments.equals(comments));
    }

    @Test
    public void issuesByIdTest(){
        when(mockJdbcTemplate.query(eq(String.format(DatabaseClient.ISSUE_BY_ID_SQL,ID)),ArgumentMatchers.<RowMapper<Issues>>any())).thenReturn(issues);
        when(mockJdbcTemplate.query(eq(String.format(DatabaseClient.GET_COMMENTS_SQL,ID)),ArgumentMatchers.<RowMapper<Comment>>any())).thenReturn(comments);
        List<Issues> issuesById = db.getIssuesById(ID);
        assert(issuesById.equals(issues));
        issuesById.forEach(issue -> {
            assert(issue.getAddedComments().equals(comments));
        });
    }

    @Test
    public void issueCountTest() {
        when(mockJdbcTemplate.query(eq(String.format(DatabaseClient.ISSUE_COUNT_SQL,ID)),ArgumentMatchers.<ResultSetExtractor<Integer>>any())).thenReturn(ISSUE_COUNT);
        Integer issueCount = db.getIssueCount(ID);
        assert(issueCount.equals(ISSUE_COUNT));
    }
}

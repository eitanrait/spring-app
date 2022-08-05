package com.test.app;

import com.test.app.dao.Comment;
import com.test.app.dao.Issues;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Configuration
public class DatabaseClient {

    @Autowired
    JdbcTemplate jdbcTemplate;
    public static final String SORT_BY_STATE_SQL = "SELECT issueId AS id, title, state, body, assigneeId FROM issues ORDER BY state";
    public static final String ISSUE_COUNT_SQL = "SELECT COUNT(issueId) AS total FROM issues WHERE assigneeId=%d";
    public static final String ISSUE_BY_ID_SQL = "SELECT issueId as id, title, state, body, assigneeId FROM issues WHERE issueId=%d";
    public static final String GET_COMMENTS_SQL = "SELECT comment FROM comments WHERE issueId=%d";
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS issues,comments CASCADE;";
    public static final String CREATE_TABLE_ISSUES_SQL = "CREATE TABLE issues(issueId BIGINT NOT NULL, title TEXT, state TEXT, body TEXT, assigneeId BIGINT, PRIMARY KEY(issueId))";
    public static final String CREATE_TABLE_COMMENTS_SQL = "CREATE TABLE comments(issueId BIGINT NOT NULL, commentId SERIAL, comment TEXT, PRIMARY KEY(commentId), FOREIGN KEY (issueId) REFERENCES issues(issueId))";
    public static final String INSERT_ISSUE_WITH_ASSIGNEE_SQL = "INSERT INTO issues(issueId, title, state, body, assigneeId) VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_ISSUE_WITHOUT_ASSIGNEE_SQL = "INSERT INTO issues(issueId, title, state, body) VALUES (?, ?, ?, ?)";
    public static final String INSERT_COMMENT_SQL = "INSERT INTO comments(issueId, comment) VALUES (?, ?)";


    public void makeTable(){
        log.info("Creating tables");
        jdbcTemplate.execute(DROP_TABLE_SQL);
        jdbcTemplate.execute(CREATE_TABLE_ISSUES_SQL);
        jdbcTemplate.execute(CREATE_TABLE_COMMENTS_SQL);
    }

    public void insertIssue(Issues i) {
        log.info("Inserting entry into issues with issueId: {}",i.getId());
        if(i.getAssignee() != null)
            jdbcTemplate.update(INSERT_ISSUE_WITH_ASSIGNEE_SQL,i.getId(),i.getTitle(), i.getState(), i.getBody(),i.getAssignee().getId());
        else
            jdbcTemplate.update(INSERT_ISSUE_WITHOUT_ASSIGNEE_SQL, i.getId(), i.getTitle(), i.getState(), i.getBody());
    }

   public void insertComment(Long id, Comment comment) {
          jdbcTemplate.update(INSERT_COMMENT_SQL,id, comment.getComment());
    }

    public List<Issues> sortByState() {
        log.info("Querying for all issues sorted by state:");
        return jdbcTemplate.query(SORT_BY_STATE_SQL, new BeanPropertyRowMapper<>(Issues.class))
                .stream()
                .peek(issues -> issues.setAddedComments(getComments(issues.getId())))
                .collect(Collectors.toList());
    }

    public List<Comment> getComments(Long id) {
        return jdbcTemplate.query(String.format(GET_COMMENTS_SQL,id), new BeanPropertyRowMapper<>(Comment.class));
    }

    public List<Issues> getIssuesById(Long id) {
        log.info("Querying for issue details by id:");
        return jdbcTemplate.query(String.format(ISSUE_BY_ID_SQL,id), new BeanPropertyRowMapper<>(Issues.class))
                .stream()
                .peek(issue -> issue.setAddedComments(getComments(issue.getId())))
                .collect(Collectors.toList());
    }

    public Integer getIssueCount(Long id) {
        return jdbcTemplate.query(String.format(ISSUE_COUNT_SQL,id),rs ->{
            rs.next();
            return rs.getInt("total");
        });
    }
}

package com.test.app;

import com.test.app.dao.Comment;
import com.test.app.dao.Issues;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class IssuesController {

    @Autowired
    DatabaseClient db;

    @GetMapping("/issues")
    List<String> getIssues() {
        return db.sortByState().stream().map(Issues::toString).collect(Collectors.toList());
    }

    @GetMapping("issues/{id}")
    List<String> getIssuesById(@PathVariable Long id) {
        return db.getIssuesById(id).stream().map(Issues::toString).collect(Collectors.toList());
    }

    @GetMapping("/assignees/{id}")
    Integer getIssuesCountByAssignee(@PathVariable Long id) {
        return db.getIssueCount(id);
    }

    @PostMapping("/issues/{id}/addComment")
    public ResponseEntity<Integer> addCommentToIssue(@PathVariable Long id, @RequestBody Comment comment) {
        try {
            db.insertComment(id,comment);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (RuntimeException e){
            log.error(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

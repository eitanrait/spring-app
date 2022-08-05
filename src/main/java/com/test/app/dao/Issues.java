package com.test.app.dao;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Issues implements Serializable {

    private Long id;
    private String title;
    private String state;
    private String body;
    @ToString.Exclude
    private Assignee assignee;
    private Long assigneeId;
    private List<Comment> addedComments;
}
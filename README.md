Author: Eitan Raitses
Date: May 2, 2022

****IMPORTANT****

BEFORE RUNNING THE APPLICATION, INSTALL Docker, and RUN 'docker compose up -d' IN THE TERMINAL INSIDE ../docker/ DIRECTORY

In RestClient.java, replace the value in TOKEN with your own OAuth token from GitHub

THANK YOU
*****************

Description:

This document serves as a breakdown of the design and functionality of the SpringBoot application.

Overview : Each of the following project elements and their relationships will be examined in detail

	pom.xml
	compose.yml
	
	Issues.java
	Assignee.java
	Comment.java
	
	TechApplication.java

	application.properties
	DatabaseClient.java

	ApplicationConfig.java
	RestClient.java
	
	IssuesController.java

pom.xml :
	
	- This dependency manager lists all the dependencies used in this project

compose.yml :
	
	- This docker compose file runs a dockerized PostgreSQL database

Issues.java :
	
	- This class stores the required fields and objects for each Issues object requested from the GitHub API

Assignee.java :
	
	- This class stores the id for each Assignee object requested from the GitHub API

Comment.java :
	
	- This class stores the comment string for each Comment object to be inserted into the comments table and to be added to the list of comments in each Issues object

TechApplication.java :
	
	- This class is the entry point for the SpringBoot application

	- The two objects, db and client, are injected with the DatabaseClient and the RestClient beans, respectively
	
	- A call is made to the DatabaseClient to create the necessary relations in the PostgreSQL DB and another is made to the RestClient to request issues from the GitHub API and persist them in the DB

application.properties :
	
	- This resource file defines the Spring datasource properties for the JdbcTemplate

DatabaseClient.java :
	
	- This class contains all the needed methods to create tables, insert entries, and query the DB
	
	- The autowired JdbcTemplate connects the Spring application to the PostgreSQL DB and executes SQL operations

	- Two tables are created:
		
	issues(issueId, title, state, body, assigneeId) 

	comments(issueId,commentId,comment) 	
	
	- The two tables are used so multiple comments can be associated with a single issueId without the need for nested comments in the issues table (flattened relations)

ApplicationConfig.java :
	
	- This class is the source for the WebClient bean to be injected into the RestClient	
	
RestClient.java :

	- This class contains the methods that make a request to the GitHub API and persist the data
	
	- As WebClient responses come in, they are converted to Issues objects and inserted into the DB


IssuesController.java :
	
	- This is the RestController which maps the required endpoints for the GET and POST requests to the local REST client

	- mapped to localhost:8080/issues
	- getIssues returns a list of sorted issues collected from the call to db.sortByState()

	- mapped to localhost:8080/issues/{id}
	- getIssuesById takes in a path variable argument corresponding to the issueId from the GET request and returns the details of that issue

	- mapped to localhost:8080/assignees/{id}
	- getIssuesCountByAssignee takes in a path variable argument corresponding to the assigneeId from the GET request and returns the count of issues associated with that assigneeId

	- mapped to localhost:8080/issues/{id}/addComment
	- addCommentToIssue() trys to insert the comment into the comments table and throws an exception if that fails

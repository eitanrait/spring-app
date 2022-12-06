Author: Eitan Raitses
Date: May 2, 2022

****IMPORTANT****

Install Docker, and run docker compose --file docker/compose.yml up -d

In RestClient.java, replace the value in TOKEN with your own OAuth token from GitHub
*****************

Description:

This document is a breakdown of the classes and their functions

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
	
	- This dependency manager lists all the required dependencies

compose.yml :
	
	- This docker compose file defines a dockerized PostgreSQL database

Issues.java :
	
	- This is the Issues object that's requested from the GitHub API

Assignee.java :
	
	- This contains the id for an Assignee object requested from the GitHub API

Comment.java :
	
	- A Comment object is inserted into the comments table and appended to a list of comments in each Issues object

TechApplication.java :
	
	- This is the entry point for the SpringBoot application

	- The two objects (db and client) are injected with the DatabaseClient and the RestClient beans
	
	- Two calls are made:
		- 1) is made to the DatabaseClient to create the necessary tables in the PostgreSQL DB, and 
		- 2) is made to the RestClient to request issues from the GitHub API and persist them in the DB

application.properties :
	
	- This resource file defines the Spring datasource properties for the JdbcTemplate

DatabaseClient.java :
	
	- Contains all the required methods to create tables, insert entries, and query the DB
	
	- The autowired JdbcTemplate connects the Spring application to the data source (PostgreSQL DB) and executes SQL operations

	- Two tables are created:
		
	issues(issueId, title, state, body, assigneeId) 

	comments(issueId,commentId,comment) 	
	
	- Two tables enables multiple comments for one issueId without nesting comments in the issues table (it is a flattened table)

ApplicationConfig.java :
	
	- This is the source for the WebClient bean to be injected into the RestClient
	
RestClient.java :

	- Contains the methods that request Issues from the GitHub API and persists them
	
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

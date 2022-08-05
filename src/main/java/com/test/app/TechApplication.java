package com.test.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechApplication implements CommandLineRunner {

	@Autowired
	DatabaseClient db;

	@Autowired
	RestClient client;

	public static void main(String[] args) {
		SpringApplication.run(TechApplication.class, args);
	}

	@Override
	public void run (String... strings) {
		db.makeTable();
		client.callAPI();
	}

}

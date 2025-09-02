package com.example.integrationpoc.domain.model;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:33:27
 * File: PlanetResponse.java
 */

public record PlanetResponse(
	    String message,
	    Result result,
	    String apiVersion,
	    String timestamp,
	    Support support,
	    Social social
	) {}
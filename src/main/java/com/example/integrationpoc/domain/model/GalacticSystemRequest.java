package com.example.integrationpoc.domain.model;

import java.util.List;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:32:30
 * File: GalacticSystemRequest.java
 */

public record GalacticSystemRequest(
	    String nombre,
	    String habitantes,
	    String capital,
	    String gobierno,
	    String sector,
	    List<String> codigosPlanetarios
	) {}

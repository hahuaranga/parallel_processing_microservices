package com.example.integrationpoc.domain.model;

import java.util.List;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 01-09-2025 at 18:42:37
 * File: ConsolidatedResponse.java
 */

public record ConsolidatedResponse(
	    String sistema,
	    List<String> nombresPlanetas,
	    int totalPlanetas,
	    String timestamp
	) {}

package com.cyberone.report;

import org.springframework.data.mongodb.core.MongoTemplate;

public class Common {
	
    private MongoTemplate baseProm;

	public MongoTemplate getBaseProm() {
		return baseProm;
	}

	public void setBaseProm(MongoTemplate baseProm) {
		baseProm = baseProm;
	}
	
}

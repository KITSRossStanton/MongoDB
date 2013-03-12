package com.stanton.mongo;

import java.util.Vector;

import com.mongodb.BasicDBObject;

public class ResultQueue{ 
	private static Vector<BasicDBObject> results = new Vector<BasicDBObject>();
	
	public static void addResult(BasicDBObject result){
		results.add(result);
	}

	public static synchronized BasicDBObject getResult(){
		if(results.size()>0){
			BasicDBObject result = results.get(0);
			results.remove(0);
			//System.out.println("Result Queue Depth after get: "+results.size());
			return result;
		}
		else
			return null;
	}
}

package com.stanton.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class ResultThread implements Runnable {

	@Override
	public void run() {
		try{
			long timeout = 60000; // 60 Second time out if queue is empty
			long waittime = 0;
			long lastResult = System.currentTimeMillis();
			
			BasicDBObject dbo= new BasicDBObject();
			while(waittime<timeout){
				dbo = ResultQueue.getResult();
				
				if(dbo==null){
					System.out.println("No Result Found. Sleeping. "+waittime);
					waittime = System.currentTimeMillis() - lastResult;
					Thread.sleep(1000);
				}
				else{
					MongoClient mongoClient = new MongoClient("localhost", 27017);
					DB db = mongoClient.getDB("POS");
					DBCollection coll = db.getCollection("RESULT");
					
					String id = dbo.getString("_id");
					BasicDBObject find = new BasicDBObject("_id", id);
					
					DBCursor findResult = coll.find(find);
					if(findResult.size()>0){
						find = (BasicDBObject)findResult.next();
						//System.out.println(find.toString());
						int qty = find.getInt("QTY");
						qty = qty + dbo.getInt("QTY");
						dbo.append("QTY", qty);
						coll.update(new BasicDBObject("_id", id), dbo);
					}
					else{
						coll.insert(dbo);
					}
					
					lastResult = System.currentTimeMillis();
				}
			}
			
			System.out.println("Result Queue Empty. Result Thread Ending");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}

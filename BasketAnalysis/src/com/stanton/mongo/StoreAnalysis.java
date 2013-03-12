package com.stanton.mongo;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class StoreAnalysis {
	private static int min_support = 5;
	private static Vector<String> storeQueue;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			System.out.println("Start time ms:" +System.currentTimeMillis());
			if(args.length>=1){
				min_support = Integer.parseInt(args[0]);
				System.out.println("Using Min Support of "+min_support);
			}
			Vector<String> candidates = new Vector<String>();
			
			MongoClient mongoClient = new MongoClient("localhost", 27017);
	
			DB db = mongoClient.getDB("POS");
			DBCollection coll = db.getCollection("POSTX");

			//BasicDBList eans = (BasicDBList) coll.distinct("LINEITEMS.EAN");
			//System.out.println("Unique EANS: "+eans.size());
			BasicDBList stores = (BasicDBList) coll.distinct("STORE");
			System.out.println("Unique Stores: "+stores.size());
			
			storeQueue = new Vector<String>();
			
			for(int n=0; n<stores.size(); n++){
				String store = (String)stores.get(n);
				System.out.println(store);
				DBCollection strColl = null;
				
				if(db.collectionExists("STORE"+store)){
					//AnalysisQueue.addStore(store);
					//continue;
					strColl = db.getCollection("STORE"+store);
					strColl.drop();
				}
				
				List<DBObject> txs = coll.find(new BasicDBObject("STORE", store)).toArray();
				strColl = db.createCollection("STORE"+store, null);
				strColl.ensureIndex(new BasicDBObject("LINEITEMS.EAN",1));
				strColl.insert(txs);
				
//				storeQueue.add(store);
				AnalysisQueue.addStore(store);
			}
			db = null;
			mongoClient.close();

			int threadCount = 20;
			for(int c=0;c<threadCount; c++){
				Thread th = new Thread(new AnalysisThread());
				th.start();
			}
			
			Thread th = new Thread(new ResultThread());
			th.start();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}

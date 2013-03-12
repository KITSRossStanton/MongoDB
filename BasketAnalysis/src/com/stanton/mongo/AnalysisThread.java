package com.stanton.mongo;

import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class AnalysisThread implements Runnable {
	private int min_support=15;
	
	@Override
	public void run() {
		try{
			String store = "";
			while(store!=null){
				store = AnalysisQueue.getNextStore();
				if(store!=null){
					processQueue(store);
				}
			}
			System.out.println("Store Queue Is Null. Finishing");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void processQueue(String store) throws Exception{
			Vector<String> candidates = new Vector<String>();
			
			String collName = "STORE"+store;
			
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("POS");
			DBCollection coll = db.getCollection(collName);
			
			BasicDBList list = (BasicDBList)coll.distinct("LINEITEMS.EAN");
			//System.out.println(store+": "+list.size()+" unique EANs, determining how many occur >"+min_support+" times");
			

			for(int x=0; x<list.size(); x++){
				//System.out.println(coll.find(new BasicDBObject("LINEITEMS.EAN", list.get(x))));
				if(coll.find(new BasicDBObject("LINEITEMS.EAN", list.get(x))).count()>min_support)
					candidates.add(""+list.get(x));
			}
			
			//System.out.println(store+": Found "+candidates.size()+" candidates (EANs appear more that "+min_support+" times)");

			//this needs to be multi-threaded!
			combination(store, coll,candidates);
			
			coll.drop();
			mongoClient.close();
	}
	private void combination(String store, DBCollection coll,Vector<String> candidates) throws Exception{

		for(int n=0; n<candidates.size(); n++){
			String ean = candidates.get(n);
			
			for(int x=n+1; x<candidates.size(); x++){
				String ean2 = candidates.get(x);

				Vector<String> vt = new Vector<String>();
				vt.add(ean);
				vt.add(ean2);
				
				BasicDBObject dbo = new BasicDBObject("LINEITEMS.EAN", new BasicDBObject("$all", vt));
				//System.out.println(dbo.toString());
				int count = coll.find(dbo).count();
				if(count>=min_support){
					System.out.println(store+": Found "+count+" combinations of "+ean+" and "+ean2);
					BasicDBObject r = new BasicDBObject("_id", ean+ean2);
					r.append("QTY", count);
					r.append("EAN", new String[]{ean,ean2});
					//r.append("STORE_RESULT", new BasicDBObject("STORE", store).append("QTY", count));
					ResultQueue.addResult(r);
				}
			}
		}
	}
}

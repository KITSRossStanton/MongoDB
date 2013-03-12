package com.stanton.mongo;

import java.io.File;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Analysis {

	private static int min_support = 3;
	
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
			DBCollection itemColl = db.getCollection("SMALLPOSTX");

			BasicDBList list = (BasicDBList)itemColl.distinct("LINEITEMS.EAN");
			System.out.println(list.size()+" unique EANs, determining how many occur >"+min_support+" times");
			
			for(int n=0; n<list.size(); n++){
				if(itemColl.find(new BasicDBObject("LINEITEMS.EAN", list.get(n))).count()>min_support)
					candidates.add(""+list.get(n));
			}
			
			System.out.println("Found "+candidates.size()+" candidates (EANs appear more that "+min_support+" times)");

			//this needs to be multi-threaded!
			combination(itemColl,candidates);

			System.out.println("End time ms:" +System.currentTimeMillis());
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void combination(DBCollection coll, Vector<String> eans) throws Exception{
		for(int n=0; n<eans.size(); n++){
			String ean = eans.get(n);
			
			for(int x=n+1; x<eans.size(); x++){
				String ean2 = eans.get(x);

				Vector<String> vt = new Vector<String>();
				vt.add(ean);
				vt.add(ean2);
				
				BasicDBObject dbo = new BasicDBObject("LINEITEMS.EAN", new BasicDBObject("$all", vt));
				//System.out.println(dbo.toString());
				int count = coll.find(dbo).count();
				if(count>=min_support)
					System.out.println("Found "+count+" combinations of "+ean+" and "+ean2);
			}
		}
	}
	
	private static void list(Vector<String> x){
		for(int n=0; n<x.size(); n++)
			System.out.println(x.get(n));
	}
	
	private static void writeOutput(String ean, Hashtable<String, Vector<String>> map) throws Exception{
		FileWriter fw = new FileWriter(new File("c:/temp/ba.csv"),true);
		
		java.util.Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			fw.write(ean+","+key+","+map.get(key).size()+"\n");
		}
		fw.close();
	}

}

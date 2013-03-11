package com.stanton.mongo.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class BasicLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			//	MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017), new ServerAddress("localhost", 27018)));
		
			DB db = mongoClient.getDB("POS");
			DBCollection itemColl = db.getCollection("ITEMS");
		
			BufferedReader br = new BufferedReader(new FileReader(new File("C:/java/items.csv")));
	
			boolean endLoop = false;
			Hashtable<String, Hashtable<String, Hashtable>> txs = new Hashtable<String, Hashtable<String, Hashtable>>();
			System.out.println("Loading");
			int count=0;
			
			while(!endLoop){
				System.out.println(count++);
				String line = br.readLine();
				String[] vals = line.split(",");
				String ean = vals[0];
				String store = vals[1];
				String tx = vals[2];
				String vf = vals[3];
				String qty = vals[4];
			
				BasicDBObject dbo = new BasicDBObject();
				dbo.append("TXID", tx);
				dbo.append("EAN",  ean);
				dbo.append("STORE", store);
				dbo.append("VOID", vf);
				dbo.append("QTY", Float.parseFloat(qty));
			
				itemColl.insert(dbo);
		
			}
		}
		catch(Exception e){
			System.exit(-1);
		}
		
	}

}

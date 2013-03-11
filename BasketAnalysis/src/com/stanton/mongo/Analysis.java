package com.stanton.mongo;

import java.io.File;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Analysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Hashtable<String, Integer> map = new Hashtable<String, Integer>();
			
			MongoClient mongoClient = new MongoClient("localhost", 27017);
	
			DB db = mongoClient.getDB("POS");
			DBCollection itemColl = db.getCollection("ITEMS");
			
			DBCursor txs = itemColl.find(new BasicDBObject("EAN", "0000003380741"), new BasicDBObject("TXID",1));
			System.out.println("0000003380741 found in "+txs.count()+" Transactions");
			
			while(txs.hasNext()){
				BasicDBObject tx = (BasicDBObject) txs.next();
				//System.out.println(tx.get("TXID"));
				DBCursor eans = itemColl.find(new BasicDBObject("TXID", tx.get("TXID")));
				
				while(eans.hasNext()){
					String re = (String)eans.next().get("EAN");
					if(!re.equals("0000003380741")){
						if(map.containsKey(re)){
							int i = map.get(re).intValue();
							i++;
							map.put(re, new Integer(i));
							
						}
						else
							map.put(re,new Integer(1));
					}
				}
			}

			
			
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				
				System.out.println(key+","+map.get(key).intValue());
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
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

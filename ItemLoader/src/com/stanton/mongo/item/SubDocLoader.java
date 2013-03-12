package com.stanton.mongo.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class SubDocLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			//	MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017), new ServerAddress("localhost", 27018)));
		
			DB db = mongoClient.getDB("POS");
			DBCollection pos = db.getCollection("SMALLPOSTX");
		
			BufferedReader br = new BufferedReader(new FileReader(new File("C:/java/0723-20130308-items.csv")));
			
			boolean endLoop = false;
			String prevID = "";
			int counter = 0;
			Hashtable<String, Object> tx = new Hashtable<String, Object>();
			
			while(!endLoop){
				String line = br.readLine();
				if(line==null){//Finished file
					endLoop = true;
					commitTransaction(pos, tx);
				}
				else{
					String[] vals = line.split(",");
					String id = vals[2];
					String ean = vals[0];
					float qty = Float.parseFloat(vals[4]);
					
					//System.out.println("Processing EAN "+ean);
					//System.out.println(prevID+" = "+id);
					if(id.equals(prevID)){
						Vector<Hashtable<String, Object>> lineItems = (Vector<Hashtable<String, Object>>)tx.get("LineItems");
						
						//check for existence of EAN in hashtable already, increment QTY if exists.
						boolean foundEAN = false;
						for(int c=0; c<lineItems.size(); c++){
							Hashtable<String, Object> lineHash = lineItems.elementAt(c);
							if(lineHash.contains(ean)){
								//System.out.println("Found EAN "+ean);
								float f = Float.parseFloat(""+lineHash.get("QTY"));
								f = f+qty;
								lineHash.put("QTY", f);
								lineItems.remove(c);
								lineItems.add(lineHash);
								tx.put("LineItems", lineItems);
								foundEAN = true;
								break;
							}
						}
						
						//If we haven't updated an existing line item then add a new line item.
						if(!foundEAN){
							lineItems.add(getItem(vals));
							tx.put("LineItems", lineItems);
						}
					}
					else{
						System.out.println("New Transaction. Commit all previous data ("+counter+")");
						if(counter!=0)
							commitTransaction(pos, tx);
						
						tx = new Hashtable<String, Object>();
						tx.put("ID", vals[2]);
						tx.put("STORE", vals[1]);
						tx.put("VOID", vals[3]);
						
						Vector<Hashtable<String, Object>> lineItems = new Vector<Hashtable<String, Object>>();
						lineItems.add(getItem(vals));
						tx.put("LineItems",lineItems);
						
						prevID = id;
						
						counter++;
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static  Hashtable<String, Object> getItem(String[] vals) throws Exception{
		Hashtable<String, Object> item = new Hashtable<String, Object>();
		item.put("EAN", vals[0]);
		item.put("QTY", Float.parseFloat(vals[4]));	
		return item;
	}
	
	private static void commitTransaction(DBCollection pos, Hashtable<String, Object>tx) throws Exception{
		BasicDBObject dbo = new BasicDBObject("_id", tx.get("ID"));
		dbo.append("STORE", tx.get("STORE"));
		dbo.append("LINEITEMS",  tx.get("LineItems"));
		dbo.append("VOID",  tx.get("VOID"));
		
		DBCursor cursor = pos.find(new BasicDBObject("_id", tx.get("ID")));
		if(cursor.count()>0){
			System.out.println("Duplicate. Skipping");
		}
		else{
			pos.insert(dbo);
		}
	}

}

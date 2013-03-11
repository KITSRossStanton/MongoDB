package com.stanton.mongo.item;
/**
 * Currently 2 problems - null pointers at the end of the file and doesn't commit the last record.
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ItemLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Hashtable<String, String> txMap = new Hashtable<String, String>();
			
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
				String line = br.readLine();
				System.out.println(count++);
				if(line==null){
					endLoop = true;
				}
				else{
					String[] vals = line.split(",");

					String ean = vals[0];
					String store = vals[1];
					String txid = vals[2];
					String vFlag = vals[3];
					String qty = vals[4];

					if(!txs.containsKey(txid)){
						//System.out.println("New Transaction");
						//add new tx to hashtable
						Hashtable tx = new Hashtable();
						tx.put("StoreCode",  store);
						
						Hashtable<String, String> item = new Hashtable<String, String>();
						item.put("EAN", ean);
						item.put("QTY", ""+Float.parseFloat(qty));
						
						Vector<Hashtable> lineItems = new Vector<Hashtable>();
						lineItems.add(item);
						
						tx.put("LineItems", lineItems);
						
						txs.put(txid, tx);
					}
					else{
						//System.out.println("Old Transaction");
						Hashtable tx = (Hashtable)txs.get(txid);
						
						//Get Lineitems for current transaction
						Vector<Hashtable> lineItems = (Vector<Hashtable>)tx.get("LineItems");

						boolean foundEan = false;
						for(int n=0; n<lineItems.size(); n++){
							Hashtable<String, String> lineItem = lineItems.get(n);
							//Does it contain the EAN we're processing?
							if(lineItem.contains(ean)){
								//System.out.println("Old EAN");
								float f = Float.parseFloat(lineItem.get("QTY"));
								f++;
								lineItem.put("QTY", ""+f);
								lineItems.remove(n);
								lineItems.add(lineItem);
								
								foundEan = true;
							}
						}
						
						if(!foundEan){
							//System.out.println("New EAN");
							Hashtable<String, String> lineItem = new Hashtable<String, String>();
							lineItem.put("EAN",ean);
							lineItem.put("QTY", ""+Float.parseFloat(qty));
							
							lineItems.add(lineItem);
						}
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}

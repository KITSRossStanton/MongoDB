package com.stanton.step;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class ClassificationParser extends ParseUtils implements STEPParser {

	@Override
	public void parse(Node node) throws Exception {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		//MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017), new ServerAddress("localhost", 27018)));

		DB db = mongoClient.getDB("ATG");
		DBCollection classColl = db.getCollection("Classifications");
		if(node.getNodeType()==Node.ELEMENT_NODE){
			Element clsElem = (Element)node;

			NodeList clss = clsElem.getElementsByTagName("Classification");
			System.out.println("Found "+clss.getLength()+" Classification Nodes");
			
			for(int n=0; n<clss.getLength(); n++){
				Node cls = clss.item(n);
				
				//Create Mongo DB Object
				BasicDBObject dbo = new BasicDBObject("_id", getAttributeValue("ID", cls));
				dbo.put("Name", getValue("Name", cls));
				dbo.put("Type",  getAttributeValue("UserTypeID", cls));
				//Get Parent Node in order to determine parent ID
				Node parent = cls.getParentNode();
				dbo.put("ParentID", getAttributeValue("ID",parent));
				System.out.println(dbo.toString());
				DBCursor results = classColl.find(new BasicDBObject("_id", getAttributeValue("ID", cls)));
				System.out.println("Found "+results.count()+" objects in DB");
				if(results.count()>=1){
					WriteResult result = classColl.update(results.next(), dbo);
				}
				else{
					WriteResult result = classColl.insert(dbo);
				}
			}
			
			Vector<Hashtable> vt = new Vector<Hashtable>();
			Hashtable<String, String> item = new Hashtable<String, String>();
			item.put("EAN", "501151651651651");
			item.put("QTY", "5");
			vt.add(item);
			item.put("EAN", "5015105156516514");
			item.put("QTY", "2");
			vt.add(item);
			
			BasicDBObject test = new BasicDBObject("_id", "tx_id");
			test.append("LineItems", vt);
			System.out.println(test.toString());
						
		}
	}
}

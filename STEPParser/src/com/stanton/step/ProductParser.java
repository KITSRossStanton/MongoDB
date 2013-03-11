package com.stanton.step;

import java.util.Arrays;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

public class ProductParser extends ParseUtils implements STEPParser {

	@Override
	public void parse(Node node) throws Exception{
		System.out.println("Parsing Node '"+node.getNodeName()+"'");
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		//MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017), new ServerAddress("localhost", 27018)));

		DB db = mongoClient.getDB("ATG");
		DBCollection prodColl = db.getCollection("products");
		
		if(node.getNodeType()==Node.ELEMENT_NODE){
			Element prds = (Element)node;
			
			NodeList productList = prds.getElementsByTagName("Product");
		
			for(int n=0; n<productList.getLength(); n++){
				Node product = productList.item(n);
				HashMap<String,Object> dbProd = parseProduct(product);
				
				BasicDBObject dbo = new BasicDBObject("_id", getAttributeValue("ID",product));
				DBCursor cursor = prodColl.find(new BasicDBObject("_id", getAttributeValue("ID",product)));
			
				dbo.append("name", getValue("Name",product));
				dbo.append("attributes", dbProd);
				System.out.println(dbo.toString());
				
				if(cursor.count()>0){
					prodColl.update(cursor.next(), dbo);
				}
				else{
					WriteResult result = prodColl.insert(dbo);
				}
			}
		}
	}
	
	public HashMap parseProduct(Node node) throws Exception{
		HashMap<String, String> prodAttrs = new HashMap();
		if(node.getNodeType()==Node.ELEMENT_NODE){
			Element prd = (Element)node;
			
			Element valuesElem = (Element)prd.getElementsByTagName("Values").item(0);
			NodeList values = valuesElem.getChildNodes();

			
			for(int n=0; n<values.getLength(); n++){
				Node value = values.item(n);
				if(value.getNodeType()==Node.ELEMENT_NODE){
					prodAttrs.put(validateKey(getAttributeValue("AttributeID", value)), value.getTextContent());
				}
			}
		}
		return prodAttrs;
	}
	

}

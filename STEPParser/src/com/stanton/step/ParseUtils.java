package com.stanton.step;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParseUtils {
	protected String validateKey(String key){
		while(key.indexOf(".")!=-1){
			key = key.replace('.','_');
		}
		return key;
	}
	
	protected String getValue(String name, Node node) throws Exception{
		if(node.getNodeType()==Node.ELEMENT_NODE){
			Element elem = (Element)node;
			
			//None of these two lines are entirely safe
			Element child = (Element)elem.getElementsByTagName(name).item(0);
			if(child!=null)
				return (String)child.getChildNodes().item(0).getNodeValue();
			else
				return null;			
		}
		
		return null;
	}
	
	protected String getAttributeValue(String attrName, Node node) throws Exception{
		if(node.getAttributes().getLength()>0){
			if((node.getAttributes().getNamedItem(attrName)!=null) && node.getAttributes().getNamedItem(attrName).getNodeType()==Node.ATTRIBUTE_NODE){
				return node.getAttributes().getNamedItem(attrName).getNodeValue();
			}
		}
		
		return null;
	}
}

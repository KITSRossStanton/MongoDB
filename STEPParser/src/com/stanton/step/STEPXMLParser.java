package com.stanton.step;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class STEPXMLParser {
	private Document dom;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			STEPXMLParser parser = new STEPXMLParser();
			//parser.loadDocument("c:/java/exported_short.xml");
			parser.loadDocument("c:/java/exported_class.xml");
			parser.parseDocument();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void parseDocument() throws Exception{
		//Get root element
		Element root = dom.getDocumentElement();
		System.out.println(root.getNodeName());
		
		if(root.hasChildNodes()){
			loopChildren(root.getChildNodes());
		}
		
		if(root.hasAttributes()){
			parseAttributes(root.getAttributes());
		}
	}
	
	private void loopChildren(NodeList nodes) throws Exception{
		for(int x=0; x<nodes.getLength(); x++){
			Node node = nodes.item(x);

			if(node.getNodeName().equals("AttributeGroupList")){
				
			}
			else if(node.getNodeName().equals("AttributeList")){
				
			}
			else if(node.getNodeName().equals("Classifications")){
				STEPParser parser = new ClassificationParser();
				
				NodeList classifications = node.getChildNodes();
				for(int n=0;n<classifications.getLength(); n++){
					Node cls = classifications.item(n);
					if(cls.getNodeName().equals("Classification")){
						parser.parse(cls);
					}
				}
			}
			else if(node.getNodeName().equals("Products")){
				//STEPParser parser = new ProductParser();
				//parser.parse(node);
			}
		}
	}
	

	
	private String getValueForElement(String elementName,Node node) throws Exception{
		if(node.getNodeType()==Node.ELEMENT_NODE){
			Element elem = (Element)node;
			
			if(elem.getElementsByTagName(elementName).getLength()>=1){
				Element x = (Element) elem.getElementsByTagName(elementName).item(0);
				
				if(x.hasChildNodes()&&x.getChildNodes().getLength()==1){
					return x.getChildNodes().item(0).getNodeValue();
				}
			}
		}
		
		return null;
	}
	

	
	private void parseAttributes(NamedNodeMap attributes){
		for(int n=0; n<attributes.getLength(); n++){
			Node attribute = attributes.item(n);
		}
	}
	
	public void loadDocument(String stepFile) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
			
		dom = db.parse(new File(stepFile));
	}
}

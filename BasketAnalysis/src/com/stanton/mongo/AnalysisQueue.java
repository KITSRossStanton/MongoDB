package com.stanton.mongo;

import java.util.Vector;

public class AnalysisQueue {
	private static Vector<String> storeQueue = new Vector<String>();
	
	public static void addStore(String store){
		storeQueue.add(store);
	}
	
	public static synchronized String getNextStore(){
		
		if(storeQueue.size()>0){
			String store = storeQueue.get(0);
			storeQueue.remove(0);
			return store;
		}
		else
			return null;
	}
}

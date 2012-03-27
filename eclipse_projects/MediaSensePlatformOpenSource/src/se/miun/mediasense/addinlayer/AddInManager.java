package se.miun.mediasense.addinlayer;

import java.util.ArrayList;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;

public class AddInManager {

	MediaSensePlatform platform = null;
	
	ArrayList<AddIn> addInList = new ArrayList<AddIn>();
	
	public AddInManager(MediaSensePlatform platform) {
		this.platform = platform;
	}
	
	//TODO stuff
	
	
	
	//LOAD

	public void loadAddIn(AddIn addIn){


		addIn.loadAddIn(platform);
		addIn.startAddIn();
		
		addInList.add(addIn);
		
	}
	
	
	public void unloadAddIn(AddIn addIn){


		addIn.stopAddIn();
		addIn.unloadAddIn();
		
		addInList.remove(addIn);
		
	}
	
	public void unloadAllAddIns(){
		
		for(int i = 0; i < addInList.size(); i++){
		
			AddIn addIn = addInList.get(i);
			unloadAddIn(addIn);			
		}
		
	}
	
	public void forwardMessageToAddIns(Message message){
		
		for(int i = 0; i != addInList.size(); i++){
			
			AddIn addIn = addInList.get(i);
			addIn.handleMessage(message);
		}
		
		


		
		
		
		
	}
	
}

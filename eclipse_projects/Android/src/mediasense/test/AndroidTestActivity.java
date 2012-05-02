package mediasense.test;

import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.disseminationcore.ResolveResponseListener;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidTestActivity extends Activity implements ResolveResponseListener {
    private MediaSensePlatform platform;
	private DisseminationCore core;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Create the platform itself
        platform = new MediaSensePlatform();
        	       
        //Initialize the platform with chosen LookupService type and chosen Communication type. 
        platform.initalize(LookupServiceInterface.DISTRIBUTED, CommunicationInterface.TCP); //For DHT Lookup and TCP P2P communication
	
      //Extract the core for accessing the primitive functions
        core = platform.getDisseminationCore();
        
        //Set the response listeners
        core.setResolveResponseListener(this);
        
        
        setContentView(R.layout.main);
    }
    
    public void register(View view) {
    	Editable e = ((EditText)findViewById(R.id.uci)).getText();
    	String uci = e.toString(); 
    	if(uci != null) {
    		TextView t = (TextView)findViewById(R.id.textView1);
    		t.append("Registered: " + uci + "\n");
    		core.register(uci);
    	}
    }
    
    public void resolve(View view) {
    	Editable e = ((EditText)findViewById(R.id.uci)).getText();
    	String uci = e.toString(); 
    	if(uci != null) {
    		TextView t = (TextView)findViewById(R.id.textView1);
    		t.append("Started to resolve: " + uci + "\n");
    		core.resolve(uci);
    	}
    }

	@Override
	public void resolveResponse(String uci, String ip) {
		TextView t = (TextView)findViewById(R.id.textView1);
		if (ip == null) {
			t.append("Resolve failed for " + uci + "\n");
		}
		else {
			t.append("Resolved " + uci + " @ " + ip + "\n");
		}
	}
}
package manager;

public interface LookupServiceInterface {

	
	public final static int SERVER = 1;
	public final static int DHT_CHORD = 2;
	public final static int DHT_PGRID = 3;
	public final static int DISTRIBUTED = 4;
	public final static int DISTRIBUTED_BOOTSTRAP = 5;
	
	
	public abstract void resolve(String uci);
	public abstract void register(String uci);
	
	public abstract void shutdown();
	
	public abstract void handleMessage(Message message);
    
}

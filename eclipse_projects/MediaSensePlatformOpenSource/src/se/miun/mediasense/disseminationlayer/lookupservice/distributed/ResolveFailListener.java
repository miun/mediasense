package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

public interface ResolveFailListener {
	public void OnResolveFail(Sensor sensor,String uci);
}

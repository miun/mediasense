package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

public class Sensor implements Comparable<Sensor> {
	private NodeID sensorHash;
	private FingerEntry owner;
	
	public Sensor(NodeID sensor,FingerEntry owner) {
		this.sensorHash = sensor;
		this.owner = owner;
	}

	public NodeID getSensorHash() {
		return sensorHash;
	}

	public FingerEntry getOwner() {
		return owner;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sensorHash == null) ? 0 : sensorHash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Sensor other = (Sensor) obj;
		if (sensorHash == null) {
			if (other.sensorHash != null)
				return false;
		} else if (!sensorHash.equals(other.sensorHash))
			return false;
		return true;
	}

	@Override
	public int compareTo(Sensor o) {
		return sensorHash.compareTo(o.sensorHash);
	}
}

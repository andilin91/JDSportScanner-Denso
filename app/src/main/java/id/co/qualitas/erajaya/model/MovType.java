package id.co.qualitas.erajaya.model;

import java.io.Serializable;

public class MovType implements Serializable {

	private String id;
	private String name;

	public MovType(String id) {
		this.id = id;
	}

	public MovType() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
	
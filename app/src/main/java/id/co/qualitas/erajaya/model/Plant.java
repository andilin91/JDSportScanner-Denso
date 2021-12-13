package id.co.qualitas.erajaya.model;

import java.io.Serializable;

public class Plant implements Serializable {

	private String id;
	private String name;

	public Plant(String id, String name) {
		this.id = id;
		name = name;
	}

	public Plant() {
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
	
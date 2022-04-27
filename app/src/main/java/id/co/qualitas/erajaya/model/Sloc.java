package id.co.qualitas.erajaya.model;

import java.io.Serializable;

public class Sloc implements Serializable {

	private String id;
	private String name;

	public Sloc(String id) {
		this.id = id;
	}

	public Sloc() {
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
	
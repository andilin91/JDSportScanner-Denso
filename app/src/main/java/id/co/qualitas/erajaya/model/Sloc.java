package id.co.qualitas.erajaya.model;

import java.io.Serializable;

public class Sloc implements Serializable {

	private String id;

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
}
	
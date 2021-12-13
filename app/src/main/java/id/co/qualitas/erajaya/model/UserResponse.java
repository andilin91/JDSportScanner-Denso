package id.co.qualitas.erajaya.model;

import java.io.Serializable;
import java.util.List;

public class UserResponse implements Serializable{
	private User user;
	private List<Plant> plant;
    private List<Sloc> sloc;
	private List<MovType> movType;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Plant> getPlant() {
		return plant;
	}

	public void setPlant(List<Plant> plant) {
		this.plant = plant;
	}

	public List<Sloc> getSloc() {
		return sloc;
	}

	public void setSloc(List<Sloc> sloc) {
		this.sloc = sloc;
	}

	public List<MovType> getMovType() {
		return movType;
	}

	public void setMovType(List<MovType> movType) {
		this.movType = movType;
	}

}

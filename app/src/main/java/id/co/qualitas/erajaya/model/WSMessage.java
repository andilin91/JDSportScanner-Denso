package id.co.qualitas.erajaya.model;

/**
 * Created by caroline on 3/29/2016.
 */
public class WSMessage {
	private int idMessage;
	private String message;
	private Object result;

	public int getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(int idMessage) {
		this.idMessage = idMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		result = result;
	}
}

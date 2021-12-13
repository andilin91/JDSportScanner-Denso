package id.co.qualitas.erajaya.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Material implements Serializable {
	@SerializedName("instore_barcode")
	private String ean;
	@SerializedName("full_desc")
	private String materialDesc;
	@SerializedName("id")
	private String materialNumber;

	private int qty;

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getMaterialDesc() {
		return materialDesc;
	}

	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}
	
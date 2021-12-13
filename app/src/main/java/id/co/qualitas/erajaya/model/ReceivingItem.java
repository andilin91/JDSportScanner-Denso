package id.co.qualitas.erajaya.model;

public class ReceivingItem {
	private String materialNo;
	private String materialDesc;
	private String ean;
	private int qty;

	public ReceivingItem() {
	}

	public ReceivingItem(String materialNo, String materialDesc, String ean, int qty) {
		this.materialNo = materialNo;
		this.materialDesc = materialDesc;
		this.ean = ean;
		this.qty = qty;
	}

	public String getMaterialNo() {
		return materialNo;
	}

	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}

	public String getMaterialDesc() {
		return materialDesc;
	}

	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}

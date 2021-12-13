package id.co.qualitas.erajaya.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Receiving {
	@SerializedName("po_no")
	private String docNo;
	@SerializedName("id_material")
	private String materialNumber;
	@SerializedName("posting_date")
	private String postingDate;
	@SerializedName("ref_doc_no")
	private String refDocNo;
	@SerializedName("header_text")
	private String headerText;
	@SerializedName("id_plant")
	private String idPlant;
	@SerializedName("id_sloc")
	private String idSloc;
	@SerializedName("move_type")
	private String movementType;
	@SerializedName("full_desc")
	private String materialDesc;
	@SerializedName("created_date")
	private long createdDate;
	@SerializedName("created_by")
	private String createdBy;
	@SerializedName("instore_barcode")
	private String ean;
	private int qty;
	private String scanner;

	public Receiving() {
	}

	public Receiving(String docNo, String materialNumber, String postingDate, String refDocNo, String headerText, String idPlant, String idSloc, String movementType, int qty, String scanner) {
		this.docNo = docNo;
		this.materialNumber = materialNumber;
		this.postingDate = postingDate;
		this.refDocNo = refDocNo;
		this.headerText = headerText;
		this.idPlant = idPlant;
		this.idSloc = idSloc;
		this.movementType = movementType;
		this.qty = qty;
		this.scanner = scanner;
	}

	public Receiving(String docNo, String materialNumber, String postingDate, String refDocNo, String headerText, String idPlant, String idSloc, String movementType, String materialDesc, String ean, int qty, String scanner) {
		this.docNo = docNo;
		this.materialNumber = materialNumber;
		this.postingDate = postingDate;
		this.refDocNo = refDocNo;
		this.headerText = headerText;
		this.idPlant = idPlant;
		this.idSloc = idSloc;
		this.movementType = movementType;
		this.materialDesc = materialDesc;
		this.ean = ean;
		this.qty = qty;
		this.scanner = scanner;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

	public String getRefDocNo() {
		return refDocNo;
	}

	public void setRefDocNo(String refDocNo) {
		this.refDocNo = refDocNo;
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public String getIdPlant() {
		return idPlant;
	}

	public void setIdPlant(String idPlant) {
		this.idPlant = idPlant;
	}

	public String getIdSloc() {
		return idSloc;
	}

	public void setIdSloc(String idSloc) {
		this.idSloc = idSloc;
	}

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
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

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getScanner() {
		return scanner;
	}

	public void setScanner(String scanner) {
		this.scanner = scanner;
	}
}

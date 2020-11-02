package phis.application.ivc.source;

import java.math.BigDecimal;

public class ClinicPayBO {
	private BigDecimal amount;
	private int scale; 
	private int payType;
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}
}

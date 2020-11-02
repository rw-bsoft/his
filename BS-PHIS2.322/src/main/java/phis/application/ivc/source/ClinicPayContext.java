package phis.application.ivc.source;

import java.math.BigDecimal;

import org.apache.commons.lang.NullArgumentException;

public class ClinicPayContext extends AbstractPayContext implements PayContext{
	
	private PayStrategy strategy;
	private ClinicPayBO bo;
	public ClinicPayContext(ClinicPayBO bo){
		this.bo=bo;
	}
	@Override
	public PayContext setPayStrategy(PayStrategy strategy) {
		this.strategy=strategy;
		return this;
	}

	@Override
	public BigDecimal convertAmount() {
		if(this.bo.getAmount()==null){
			throw new NullArgumentException("amount不能为空");
		}
		this.setPayStrategy(searchStrategy(this.bo));
		return strategy.calculate(this.bo.getAmount(), this.bo.getScale());
	}
	

}

/**
 * @(#)IDGEN.java 创建于 2010-11-19 下午11:57:22
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.exp;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.Expression;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class IDGen extends Expression {
	
	private static Logger logger = LoggerFactory
			.getLogger(IDGen.class);

	private String endPointReference = "";
	private String targetNamespace = "";
	
	/**
	 * @return the endPointReference
	 */
	public String getEndPointReference() {
		return endPointReference;
	}

	/**
	 * @param endPointReference the endPointReference to set
	 */
	public void setEndPointReference(String endPointReference) {
		this.endPointReference = endPointReference;
	}

	/**
	 * @return the targetNamespace
	 */
	public String getTargetNamespace() {
		return targetNamespace;
	}

	/**
	 * @param targetNamespace the targetNamespace to set
	 */
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}


	/* (non-Javadoc)
	 * @see ctd.util.exp.Expression#run(java.util.List, ctd.util.exp.ExpressionProcessor)
	 */
	@Override
	public Object run(List<?> ls, ExpressionProcessor processor)
			throws ExpException {
		KeyManagerWS.setEndPointReference(endPointReference);
		KeyManagerWS.setTargetNamespace(targetNamespace);
		try {
			String entryName =(String) ls.get(1);
			return KeyManagerWS.getKeyByName(entryName, ContextUtils.getContext());
		} catch (Exception e) {
			logger.info("run JSONException:", e);
		}
		return "";
	}


}

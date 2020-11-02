/**
 * @(#)TumourHighRiskIdLoader.java Created on 2014-4-1 下午1:39:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskIdLoader extends AbstractIdLoader {

	/* (non-Javadoc)
	 * @see chis.source.ehrview.AbstractIdLoader#getLoadType()
	 */
	@Override
	public String getLoadType() {
		return "highRiskType";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#hasStatus()
	 */
	@Override
	public boolean hasStatus() {
		return true;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return "MDC_TumourHighRisk";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return MDC_TumourHighRisk;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "THRID";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "MDC_TumourHighRisk.THRID";
	}
}

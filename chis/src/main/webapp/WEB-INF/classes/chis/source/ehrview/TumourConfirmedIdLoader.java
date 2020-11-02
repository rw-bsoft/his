/**
 * @(#)TumourConfirmedIdLoader.java Created on 2015-3-9 下午1:58:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourConfirmedIdLoader extends AbstractIdLoader{
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
		return "MDC_TumourConfirmed";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return MDC_TumourConfirmed;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "TCID";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "MDC_TumourConfirmed.TCID";
	}
}

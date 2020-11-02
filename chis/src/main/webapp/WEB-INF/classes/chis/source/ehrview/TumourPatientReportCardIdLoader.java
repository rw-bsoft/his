/**
 * @(#)TumourPatientReportCardIdLoader.java Created on 2014-4-25 上午9:11:36
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPatientReportCardIdLoader extends AbstractIdLoader {

	/**
	 * @see chis.source.biz.ehrview.IdLoader#getLoadBy()
	 */
	public String getLoadPkey() {
		return "TPRCID";
	}
	
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
		return "MDC_TumourPatientReportCard";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return MDC_TumourPatientReportCard;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "TPRCID";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "MDC_TumourPatientReportCard.TPRCID";
	}

}

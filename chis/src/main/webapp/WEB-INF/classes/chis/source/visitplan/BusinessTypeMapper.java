/*
 * @(#)BusinessTypeMapper.java Created on 2012-3-9 下午4:18:08
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan;

import static chis.source.BSCHISEntryNames.CDH_DebilityChildren;
import static chis.source.BSCHISEntryNames.CDH_HealthCard;
import static chis.source.BSCHISEntryNames.MDC_DiabetesRecord;
import static chis.source.BSCHISEntryNames.MDC_HypertensionRecord;
import static chis.source.BSCHISEntryNames.MDC_OldPeopleRecord;
import static chis.source.BSCHISEntryNames.MDC_TumourRecord;
import static chis.source.BSCHISEntryNames.MHC_PregnantRecord;
import static chis.source.BSCHISEntryNames.PSY_PsychosisRecord;
import static chis.source.BSCHISEntryNames.RVC_RetiredVeteranCadresRecord;
import static chis.source.dic.BusinessType.CD_CU;
import static chis.source.dic.BusinessType.CD_DC;
import static chis.source.dic.BusinessType.CD_IQ;
import static chis.source.dic.BusinessType.GXY;
import static chis.source.dic.BusinessType.HYPERINQUIRE;
import static chis.source.dic.BusinessType.LNR;
import static chis.source.dic.BusinessType.MATERNAL;
import static chis.source.dic.BusinessType.PREGNANT_HIGH_RISK;
import static chis.source.dic.BusinessType.PSYCHOSIS;
import static chis.source.dic.BusinessType.TNB;
import static chis.source.dic.BusinessType.ZL;
import static chis.source.dic.BusinessType.RVC;

import java.util.HashMap;
import java.util.Map;

/**
 * @description BusinessType和档案的互相映射。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class BusinessTypeMapper {

	private static final Map<String, String> mapper = new HashMap<String, String>();

	static {
		mapper.put(GXY, MDC_HypertensionRecord);
		mapper.put(TNB, MDC_DiabetesRecord);
		mapper.put(ZL, MDC_TumourRecord);
		mapper.put(LNR, MDC_OldPeopleRecord);
		mapper.put(CD_CU, CDH_HealthCard);
		mapper.put(CD_IQ, CDH_HealthCard);
		mapper.put(CD_DC, CDH_DebilityChildren);
		mapper.put(MATERNAL, MHC_PregnantRecord);
		mapper.put(PREGNANT_HIGH_RISK, MHC_PregnantRecord);
		mapper.put(PSYCHOSIS, PSY_PsychosisRecord);
		mapper.put(HYPERINQUIRE, MDC_HypertensionRecord);
		mapper.put(RVC, RVC_RetiredVeteranCadresRecord);
	}

	/**
	 * 根据档案实体名获取businessType。
	 * 
	 * @param entryName
	 * @return
	 */
	public static String getBusinessType(String entryName) {
		for (String key : mapper.keySet()) {
			if (entryName.equals(mapper.get(key))) {
				return key;
			}
		}
		return null;
	}

	/**
	 * 根据businessType获取档案实体名。
	 * 
	 * @param businessType
	 * @return
	 */
	public static String getEntryName(String businessType) {
		return mapper.get(businessType);
	}
}

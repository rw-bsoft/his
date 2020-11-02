/**
 * @(#)PatientHealthQuestionnaireModel.java Created on 2014-3-17 下午4:13:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phq;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;


/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PatientHealthQuestionnaireModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public PatientHealthQuestionnaireModel(BaseDAO dao) {
		this.dao = dao;
	}
	
}

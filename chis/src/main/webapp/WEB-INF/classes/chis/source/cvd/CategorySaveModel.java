/**
 * @(#)CategorySaveModel.java Created on 2013-3-15 上午09:19:09
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.cvd;

import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;

import com.bsoft.mpi.model.ModelDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class CategorySaveModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public CategorySaveModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> saveCategory(String op, Map<String, Object> body)
			throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, CVD_Category, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存生活方式干预建议失败");
		}
	}

}

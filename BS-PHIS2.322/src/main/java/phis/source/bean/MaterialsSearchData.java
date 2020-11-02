package phis.source.bean;

import java.util.List;
/**
 * 
 * @description 物资查询
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class MaterialsSearchData {
	private Long count;
	private List<Materials> mats;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Materials> getMats() {
		return mats;
	}

	public void setMats(List<Materials> mats) {
		this.mats = mats;
	}

}

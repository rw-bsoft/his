/*
 * @(#)CreateParams.java Created on 2011-12-28 下午6:01:43
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import chis.source.dic.YesNo;
import chis.source.util.BSCHISUtil;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class CreatationParams implements Serializable {

	private static final long serialVersionUID = 5815853275173685451L;

	private Date onsetDate;
	private Date cutoutDate;
	private String empiId;
	private String recordId;
	private String businessType;
	private String taskDoctorId;


	private Map<String, Object> values = new HashMap<String, Object>();

	/**
	 * 启动日期。
	 * 
	 * @return
	 */
	public Date getOnsetDate() {
		return onsetDate;
	}

	/**
	 * 启动日期。
	 * 
	 * @param onsetDate
	 */
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
		values.put("onsetDate", onsetDate);
	}

	/**
	 * 截止日期。
	 * 
	 * @return
	 */
	public Date getCutoutDate() {
		return cutoutDate;
	}

	/**
	 * 截止日期。
	 * 
	 * @param cutoutDate
	 */
	public void setCutoutDate(Date cutoutDate) {
		this.cutoutDate = cutoutDate;
		values.put("cutoutDate", cutoutDate);
	}

	public String getEmpiId() {
		return empiId;
	}

	public void setEmpiId(String empiId) {
		this.empiId = empiId;
		values.put("empiId", empiId);
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
		values.put("recordId", recordId);
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
		values.put("businessType", businessType);
	}

	public String getTaskDoctorId() {
		return taskDoctorId;
	}

	public void setTaskDoctorId(String taskDoctorId) {
		this.taskDoctorId = taskDoctorId;
		values.put("taskDoctorId", taskDoctorId);
	}

	public void setStringValue(String name, String value) {
		values.put(name, value);
	}

	public String getStringValue(String name) {
		Object s = values.get(name);
		if (s != null) {
			return s.toString();
		}
		return null;
	}

	public void setIntValue(String name, Integer value) {
		values.put(name, value);
	}

	public Integer getIntValue(String name) {
		Object o = values.get(name);
		if(o == null){
			return null;
		}
		return (Integer) o;
	}

	public void setDoubleValue(String name, Double value) {
		values.put(name, value);
	}

	public double getDoubleValue(String name) {
		return (Double) values.get(name);
	}

	public void setDateValue(String name, Date value) {
		values.put(name, value);
	}

	public Date getDateValue(String name) {
		if (values.get(name) instanceof String) {
			return BSCHISUtil.toDate((String) values.get(name));
		} else {
			return (Date) values.get(name);
		}
	}

	public void setBooleanValue(String name, boolean value) {
		values.put(name, value);
	}

	/**
	 * 这里取字典值-传过来的都是字符型-强转报错 精神病 referral 现[n 否 y 是] 原[1 否 2 是]
	 * 
	 * @param name
	 * @return
	 */
	public boolean getBooleanValue(String name) {
		if (values.get(name) instanceof String) {
			if (YesNo.YES.equals(values.get(name))) {
				return true;
			} else {
				return false;
			}
		}
		Boolean b = (Boolean) values.get(name);
		return b == null ? false : b;
	}

	public void setObjectValue(String name, Object value) {
		values.put(name, value);
	}

	public Object getObjectValue(String name) {
		return values.get(name);
	}

	/**
	 * @param datas
	 */
	public void setValues(Map<String, Object> datas) {
		for (String key : datas.keySet()) {
			Object value = datas.get(key);
			values.put(key, value);
			try {
				Field f = this.getClass().getDeclaredField(key);
				f.setAccessible(true);
				f.set(this, value);

			} catch (NoSuchFieldException e) {
				// @@ just forget it.
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Cannot set value of [" + key + "].", e);
			}
		}
	}
}

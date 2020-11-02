/**
 * @(#)CDHService.java Created on Nov 25, 2009 3:17:42 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.cvd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.empi.EmpiUtil;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class CVDService extends AbstractActionService implements DAOSupportable {
	private static final Log logger = LogFactory.getLog(CVDService.class);

	private static Map<String, String> m = new HashMap<String, String>();

	static {
		m.put("1", "您的体重属于肥胖，建议您通过控制饮食和增加体育锻炼来减重。");
		m.put("2", "您的体重属于超重，建议您通过控制饮食和增加体育锻炼来减重。");
		m.put("3", "您的体重标准，请继续保持。");
		m.put("4", "您的体重偏瘦，建议您通过控制饮食和增加体育锻炼来增重。");
	}

	@SuppressWarnings({ "unchecked" })
	protected void doUploadCvdFileTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		ArrayList<Object> files = (ArrayList<Object>) req.get("files");
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		int count = 0;
		for (int i = 0; i < files.size(); i++) {
			try {
				FileItem file = (FileItem) files.get(i);
				LineNumberReader line = new LineNumberReader(
						new InputStreamReader(file.getInputStream()));

				String temp;
				Map<String, Object> map = null;
				while ((temp = line.readLine()) != null) {
					map = new HashMap<String, Object>();
					try {
						if (line.getLineNumber() == 1) {
							continue;
						}
						String s[] = null;
						s = temp.split(",");
						if (s[0] != null && !s[0].equals("")) {
							map.put("createDate",
									BSCHISUtil.toDate(s[0].substring(0, 10)));
						}
						if (s.length > 1 && s[1] != null && !s[1].equals("")) {
							if (s[1].length() > 8) {
								continue;
							}
							map.put("personName", s[1]);
						}
						if (s.length > 2 && s[2] != null && !s[2].equals("")) {
							if (s[2].equals("男")) {
								map.put("sexCode", "1");
							} else if (s[2].equals("女")) {
								map.put("sexCode", "2");
							} else {
								map.put("sexCode", "0");
							}
						}
						if (s.length > 3 && s[3] != null && !s[3].equals("")) {
							if (!s[3].contains("X")) {
								continue;
							}
							if (!EmpiUtil.IDCardValidate(s[3].toUpperCase())
									.equals("")) {
								continue;
							}
							map.put("idCard", EmpiUtil.idCard15To18(s[3])
									.replaceAll("x", "X"));
						}
						if (s.length > 4 && s[4] != null && !s[4].equals("")) {
							map.put("cardNo", s[4]);
						}
						if (s.length > 5 && s[5] != null && !s[5].equals("")) {
							map.put("contact", s[5]);
						}
						if (s.length > 9 && s[9] != null && !s[9].equals("")) {
							map.put("homeAddress", s[9]);
						}
						if (s.length > 10 && s[10] != null && !s[10].equals("")) {
							String birthday = s[10].substring(0, 4) + "-"
									+ s[10].substring(4, 6) + "-"
									+ s[10].substring(6, 8);
							map.put("birthday", BSCHISUtil.toDate(birthday));
						}

						if (s.length > 12 && s[12] != null && !s[12].equals("")) {
							map.put("inputDate",
									BSCHISUtil.toDate(s[12].substring(0, 10)));
						}

						if (s.length > 13 && s[13] != null && !s[13].equals("")) {
							if (s[13].length() > 6) {
								continue;
							}
							map.put("height", Double.valueOf(s[13]));
						}
						if (s.length > 14 && s[14] != null && !s[14].equals("")) {
							if (s[14].length() > 6) {
								continue;
							}
							map.put("weight", Double.valueOf(s[14]));
						}
						if (s.length > 15 && s[15] != null && !s[15].equals("")) {
							map.put("waistLine", Double.valueOf(s[15]));
						}
						if (s.length > 16 && s[16] != null && !s[16].equals("")) {
							map.put("hipLine", Double.valueOf(s[16]));
						}
						if (s.length > 17 && s[17] != null && !s[17].equals("")) {
							map.put("tc", Double.valueOf(s[17]));
						}
						if (s.length > 18 && s[18] != null && !s[18].equals("")) {
							map.put("ldl", Double.valueOf(s[18]));
						}
						if (s.length > 19 && s[19] != null && !s[19].equals("")) {
							map.put("hdl", Double.valueOf(s[19]));
						}
						if (s.length > 20 && s[20] != null && !s[20].equals("")) {
							map.put("tg", Double.valueOf(s[20]));
						}
						if (s.length > 21 && s[21] != null && !s[21].equals("")) {
							if (s[21].equals("是")) {
								map.put("diabetes", "1");
							} else if (s[21].equals("否")) {
								map.put("diabetes", "2");
							}
						}
						if (s.length > 22 && s[22] != null && !s[22].equals("")) {
							map.put("fbs", Double.valueOf(s[22]));
						}
						if (s.length > 23 && s[23] != null && !s[23].equals("")) {
							map.put("pbs", Double.valueOf(s[23]));
						}
						if (s.length > 24 && s[24] != null && !s[24].equals("")) {
							if (s[24].equals("是")) {
								map.put("smoke", "1");
							} else if (s[24].equals("否")) {
								map.put("smoke", "2");
							}
						}
						if (s.length > 25 && s[25] != null && !s[25].equals("")) {
							map.put("constriction", Integer.valueOf(s[25]));
						}
						if (s.length > 26 && s[26] != null && !s[26].equals("")) {
							map.put("diastolic", Integer.valueOf(s[26]));
						}
						if (s.length > 27 && s[27] != null && !s[27].equals("")) {
							if (s[27].equals("是")) {
								map.put("bpIncreased", "1");
							} else if (s[24].equals("否")) {
								map.put("bpIncreased", "2");
							}
						}
						String cardiovascular = "";
						if (s.length > 28 && s[28] != null && !s[28].equals("")) {
							if (s[28].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "02";
								} else {
									cardiovascular += "," + "02";
								}
							}
						}
						if (s.length > 29 && s[29] != null && !s[29].equals("")) {
							if (s[29].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "02";
								} else {
									cardiovascular += "," + "02";
								}
							}
						}
						if (s.length > 30 && s[30] != null && !s[30].equals("")) {
							if (s[30].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "03";
								} else {
									cardiovascular += "," + "03";
								}
							}
						}

						if (s.length > 31 && s[31] != null && !s[31].equals("")) {
							if (s[31].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "04";
								} else {
									cardiovascular += "," + "04";
								}
							}
						}

						if (s.length > 32 && s[32] != null && !s[32].equals("")) {
							if (s[32].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "05";
								} else {
									cardiovascular += "," + "05";
								}
							}
						}
						if (s.length > 33 && s[33] != null && !s[33].equals("")) {
							if (s[33].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "06";
								} else {
									cardiovascular += "," + "06";
								}
							}
						}
						if (s.length > 34 && s[34] != null && !s[34].equals("")) {
							if (s[34].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "07";
								} else {
									cardiovascular += "," + "07";
								}
							}
						}
						if (s.length > 35 && s[35] != null && !s[35].equals("")) {
							if (s[35].equals("是")) {
								if (cardiovascular.equals("")) {
									cardiovascular = "08";
								} else {
									cardiovascular += "," + "08";
								}
							}
						}

						map.put("cardiovascular", cardiovascular);
						map.put("lastModifyUser",
								UserUtil.get(UserUtil.USER_ID));
						map.put("lastModifyDate", new Date());
						map.put("isImport", "1");
						map.put("isImported", "2");
						try {
							cvdsModel.saveAssessRegisterRecord("create", map);
							count = count + 1;
						} catch (Exception e) {

						}
					} catch (Exception e) {
						continue;
					}

				}
				cvdsModel.updateAssessRegisterData();
				cvdsModel.deleteAssessregisterData();
				cvdsModel.updateAssessRegisterIsImported();
				req.put("count", count);
			} catch (Exception e) {

			}
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	protected void doSaveAssessRegister(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String inquireId = "";
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");
		EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> person;
		try {
			person = empiModel.getEmpiInfoByEmpiid(empiId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		String sexCode = (String) person.get("sexCode");
		double tc = StringUtils.isEmpty(String.valueOf(body.get("tc"))) ? 0
				: Double.parseDouble(String.valueOf(body.get("tc")));
		tc = tc == 0.0 ? 0 : tc;
		String diabetes = (String) body.get("diabetes");
		diabetes = this.changYesNoStr(diabetes);
		String smoke = (String) body.get("smoke");
		smoke = this.changYesNoStr(smoke);
		String ageGroup = this.getAge(person);
		String bp = this.getBp(body);
		String riskRatio = "";
		String riskRatioDic = "";
		String riskAssessment = "";
		String lifeStyle = "";
		String drugs = "";
		String cardiovascular = (String) body.get("cardiovascular");
		String[] cardiovasculars = cardiovascular.split(",");
		int constriction = (Integer) body.get("constriction");
		int diastolic = (Integer) body.get("diastolic");
		String bpIncreased = body.get("bpIncreased") == null ? ""
				: (String) body.get("bpIncreased");
		String kidney = body.get("kidney") == null ? "" : (String) body
				.get("kidney");
		String riskPrediction = "";
		String riskRatio_text = "不在预测范围内";

		double wh = (body.get("wh") == null ? -1 : Double.valueOf((String) body
				.get("wh")));
		if (wh == -1) {
			wh = (Double) body.get("waistLine")
					/ Double.valueOf((String) body.get("hipLine"));
		}
		double bmi = (body.get("bmi") == null ? -1 : Double
				.valueOf((String) body.get("bmi")));
		if (bmi == -1) {
			bmi = (Double.valueOf((String) body.get("weight"))
					/ Double.valueOf((String) body.get("height"))
					* Double.valueOf((String) body.get("height")) / 10000);
		}
		String weightControl = getWeightControl(bmi, sexCode, wh);

		body.put("personName", person.get("personName"));
		body.put("sexCode", person.get("sexCode"));
		body.put("birthday", person.get("birthday"));
		body.put("cardNo", person.get("cardNo"));
		body.put("idCard", person.get("idCard"));

		String op = (String) req.get("op");
		Map<String, Object> jsonRes;
		try {
			jsonRes = cvdsModel.saveAssessRegisterRecord(op, body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			inquireId = (String) ((Map<String, Object>) jsonRes)
					.get("inquireId");
		} else {
			inquireId = (String) ((Map<String, Object>) req.get("body"))
					.get("inquireId");
		}
		vLogService.saveVindicateLog(CVD_AssessRegister, op, inquireId, dao, empiId);
		
		String hypertensionTreat = (String) body.get("hypertensionTreat");
		String riskiness = (String) body.get("riskiness");
		String[] riskiness_array = null;
		int cvdCount = hypertensionTreat.equals("1") ? 1 : 0;
		if (!riskiness.equals("")) {
			riskiness_array = riskiness.split(",");
		}
		if (null != riskiness_array) {
			cvdCount += riskiness_array.length;
		}
		// 患病人群：
		if (!cardiovasculars[0].equals("")) {
			String lifeStyleExp = "['and'," + "['eq',['$','type'],['s','3']],"
					+ "['eq',['$','riskFactor'],['s','0']]]";
			lifeStyle = this.getLifeStyle(lifeStyleExp, weightControl, dao)
					.trim();

			String drugsExp = "['and'," + "['eq',['$','type'],['s','4']],"
					+ "['eq',['$','riskFactor'],['s','0']]]";
			drugs = this.getDrugs(drugsExp, riskRatioDic, dao, ctx).trim();

			Dictionary category = null;
			try {
				category = DictionaryController.instance().get(
						"chis.dictionary.cardiovascular");
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			riskPrediction = "您已患";
			for (int i = 0; i < cardiovasculars.length; i++) {
				String v = cardiovasculars[i];
				DictionaryItem it = category.getItem(v);
				if (i == cardiovasculars.length - 1) {
					riskPrediction += it.getText();
				} else {
					riskPrediction += it.getText() + ",";
				}
			}
		} else {
			try {
				if (tc > 8) {
					tc = 8;
				} else if (tc < 4 && tc != 0) {
					tc = 4;
				}
				String tcstr = "0";
				if (tc == 0.0) {
					tcstr = "0";
				}else if (tc == 8.0){
					tcstr = "8";
				}else if (tc == 7.0){
					tcstr = "7";
				}else if (tc == 6.0){
					tcstr = "6";
				}else if (tc == 5.0){
					tcstr = "5";
				}else if (tc == 4.0){
					tcstr = "4";
				}else{
					tcstr = String.valueOf(tc);
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("tc", String.valueOf(tcstr));
				parameters.put("diabetes", diabetes);
				parameters.put("smoke", smoke);
				parameters.put("ageGroup", ageGroup);
				parameters.put("bp", bp);
				parameters.put("sexCode", sexCode);
				List<Map<String, Object>> l = cvdsModel
						.getRecordByInfo(parameters);
				int count = l.size();
				if (count > 0) {
					riskRatio = (String) (l.get(0)).get("riskRatio");
				}
			} catch (Exception e) {
				logger.error("Failed to select riskRatio.", e);
				throw new ServiceException(e);
			}

			if (cvdCount >= 4) {
				// 2.有4种情况直接判定为危险度≥30%
				if (riskRatio.equals("") || Integer.valueOf(riskRatio) <= 4) {
					riskRatio = "4";
					riskRatioDic = riskRatio;
				}
				// 1.未患心脑血管疾病，但总胆固醇≥8mmol/L；
				// 2.无心脑血管疾病，但血压持续升高，>160070/100005mmHg；
				// 3.患1/2型糖尿病，伴显性肾病；
				// 4.肾衰或肾损害；
			} else if (tc >= 8
					|| (bpIncreased.equals("1") && constriction >= 160 && diastolic > 100)
					|| (diabetes.equals("1") && kidney.equals("2"))
					|| (kidney.equals("3") || kidney.equals("4"))) {
				if (riskRatio.equals("") || Integer.valueOf(riskRatio) <= 4) {
					riskRatio = "4";
					riskRatioDic = riskRatio;
				}
			}

			if (riskRatio.equals("5")) {
				riskRatioDic = "4";
			}
			if (riskRatio.equals("1")) {
				riskAssessment = "此类个体发生心血管事件的危险度较低。低危险度并不表示没有危险度。建议采取保守的管理方式，重点放在生活方式的干预。";
				riskRatio_text = "＜10%";
			} else if (riskRatio.equals("2")) {
				riskAssessment = "此类个体发生致死性或非致死性心血管事件的危险度为中度。建议每隔6～12个月重复检查一次全套的危险因素水平。";
				riskRatio_text = "10%-20%";
			} else if (riskRatio.equals("3")) {
				riskAssessment = "此类个体发生致死性或非致死性心血管事件的危险度为高度。建议每隔3～6个月重复检查一次全套的危险因素水平。";
				riskRatio_text = "20%-30%";
			} else if (riskRatio.equals("4")) {
				riskAssessment = "此类个体发生致死性或非致死性心血管事件的危险度为极高度。建议每隔3～6个月重复检查一次全套的危险因素水平。";
				riskRatio_text = "30%-40%";
			} else if(riskRatio.equals("5")){
				riskAssessment = "此类个体发生致死性或非致死性心血管事件的危险度为极高度。建议每隔3～6个月重复检查一次全套的危险因素水平。";
				riskRatio_text = ">=40%";
			}
			riskRatioDic = riskRatio;
			String lifeStyleExp = "['and'," + "['eq',['$','type'],['s','1']],"
					+ "['or'," + "['eq',['$','riskFactor'],['s','"
					+ riskRatioDic + "']],"
					+ "['eq',['$','riskFactor'],['s','0']]]]";
			lifeStyle = this.getLifeStyle(lifeStyleExp, weightControl, dao)
					.trim();

			String drugsExp = "['and'," + "['eq',['$','type'],['s','2']],"
					+ "['or'," + "['eq',['$','riskFactor'],['s','"
					+ riskRatioDic + "']],"
					+ "['eq',['$','riskFactor'],['s','0']]]]";
			drugs = this.getDrugs(drugsExp, riskRatioDic, dao, ctx).trim();
			riskPrediction = "未来10年内，您可能患有心血管疾病的危险度为  " + riskRatio_text
					+ "  。(仅供参考)\n";
			if (hypertensionTreat.equals("1") || null != riskiness_array) {
				riskPrediction += "同时您具有以下几条增加危险度的因素，您患心血管疾病的危险度可能高于预测值：\n";
			}
			if (hypertensionTreat.equals("1")) {
				riskPrediction += "已经在接受高血压治疗";
			}
			if (null != riskiness_array) {
				Dictionary riskinessDic = null;
				try {
					riskinessDic = DictionaryController.instance().get(
							"chis.dictionary.riskiness");
				} catch (ControllerException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < riskiness_array.length; i++) {
					String v = riskiness_array[i];
					DictionaryItem it = riskinessDic.getItem(v);
					if (!hypertensionTreat.equals("1") && i == 0) {
						riskPrediction += it.getText();
					} else {
						riskPrediction += "，" + it.getText();
					}
				}
			}
		}
		String myOp = "create";
		String recordId = "";
		try {
			List<Map<String, Object>> l = cvdsModel
					.getAppraisalByInquireId(inquireId);
			if (l.size() > 0) {
				// 如果评价已经被更新过的话 修改cvd信息 将不再更新评价内容
				Map<String, Object> m = l.get(0);
				String modified = (String) m.get("modified");
				recordId = (String) m.get("recordId");
				if (body.get("updateAppraisal") != null
						&& body.get("updateAppraisal").equals("no")) {
					if (modified != null && modified.equals("1")) {
						return;
					}
				}
				myOp = "update";
			}
		} catch (Exception e) {
			logger.error("Failed to get CVD_Appraisal.", e);
			throw new ServiceException(e);
		}

		Map<String, Object> bodyRes = new HashMap<String, Object>();
		Map<String, Object> appraisalBody = new HashMap<String, Object>();
		if (myOp.equals("update")) {
			appraisalBody.put("recordId", recordId);
		}
		appraisalBody.put("inquireId", inquireId);
		appraisalBody.put("phrId", phrId);
		appraisalBody.put("empiId", empiId);
		appraisalBody.put("otherRisk", riskiness);
		appraisalBody.put("riskAssessment", riskAssessment);
		if (!cardiovasculars[0].equals("")) {
			appraisalBody.put("riskPredictionResult", "");
		} else {
			appraisalBody.put("riskPredictionResult", riskRatio);
		}
		appraisalBody.put("riskPrediction", riskPrediction);
		StringBuffer sb_lifeStyle = new StringBuffer();
		sb_lifeStyle.append(lifeStyle);
		if (lifeStyle.length() >= 1000 && lifeStyle.length() <= 2000) {
			for (int i = lifeStyle.length(); i <= 2000; i++) {
				sb_lifeStyle.append(" ");
			}
		}
		appraisalBody.put("lifeStyle", sb_lifeStyle.toString());
		StringBuffer sb_drugs = new StringBuffer();
		sb_drugs.append(drugs);
		if (drugs.length() >= 1000 && drugs.length() <= 2000) {
			for (int i = drugs.length(); i <= 2000; i++) {
				sb_drugs.append(" ");
			}
		}
		appraisalBody.put("drugs", sb_drugs.toString());
		try {
			bodyRes = cvdsModel.saveAppraisalData(myOp, appraisalBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if (body.get("updateAppraisal") != null
				&& body.get("updateAppraisal").equals("yes")) {
			try {
				cvdsModel.deleteCVDTest(inquireId);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}

		res.put("body", jsonRes);
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	protected void doSaveDiseaseRegister(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");


		String op = (String) req.get("op");
		Map<String, Object> jsonRes;
		try {
			jsonRes = cvdsModel.saveDeseaseRegisterRecord(op, body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CVD_DiseaseManagement, op,(String)jsonRes.get("recordId"), dao, empiId);
		res.put("body", jsonRes);
	}
	
	protected void doSaveDiseaseVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");


		String op = (String) req.get("op");
		Map<String, Object> jsonRes;
		try {
			jsonRes = cvdsModel.saveDeseaseVerificationRecord(op, body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CVD_DiseaseVerification, op,(String)jsonRes.get("recordId"), dao, empiId);
		res.put("body", jsonRes);
	}
	
	protected void doRemoveDiseaseRegister(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String pkey = (String) reqBody.get("pkey");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		CVDServiceModel csm = new CVDServiceModel(dao);
		try {
			csm.deleteDiseaseRegister(pkey);
		} catch (Exception e) {
			throw new ServiceException("删除心脑血管数据失败!");
		}
		vLogService.saveVindicateLog(CVD_DiseaseManagement,op, pkey, dao, empiId);
	}
	
	
	private String changYesNoStr(String yesNo){
		if(yesNo == null || yesNo.equals("")){
			return "";
		}else if (yesNo.equals(YesNo.YES)) {
			return "1";
		}else if(yesNo.equals(YesNo.NO)){
			return "2";
		}else{
			return yesNo;
		}
		
	}

	private String getAge(Map<String, Object> body) {
		int month = BSCHISUtil.getMonths((Date) body.get("birthday"),
				new Date());
		int p = month % 12 > 6 ? 1 : 0;
		int age = month / 12 + p;
		String result = "";
		if (age < 50) {
			result = "1";
		} else if (age >= 50 && age < 60) {
			result = "2";
		} else if (age >= 60 && age < 70) {
			result = "3";
		} else if (age >= 70) {
			result = "4";
		}
		return result;
	}

	private String getBp(Map<String, Object> body) {
		String result = "";
		int bp = (Integer) body.get("constriction");
		if (bp < 140) {
			result = "1";
		} else if (bp >= 140 && bp < 160) {
			result = "2";
		} else if (bp >= 160 && bp < 180) {
			result = "3";
		} else if (bp >= 180) {
			result = "4";
		}
		return result;

	}

	private String getLifeStyle(String lifeStyleExp, String weightControl,
			BaseDAO dao) throws ServiceException {
		String result = "";
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		List<Map<String, Object>> list;
		try {
			list = cvdsModel.getSuggestionRecordByExp(lifeStyleExp);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		Dictionary category = null;
		try {
			category = DictionaryController.instance().get(
					"chis.dictionary.category");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> obj = list.get(i);
			String v = (String) obj.get("category");
			DictionaryItem it = category.getItem(v);
			if (null != it) {
				result += (i + 1) + "、" + it.getText() + ":"
						+ ((String) obj.get("content")).trim() + "<br>\n\n";
			}
			if (i == list.size() - 1) {
				result += (i + 2) + "、控制体重：" + m.get(weightControl)
						+ "<br>\n\n";
			}
		}
		return result;

	}

	private String getDrugs(String drugsExp, String riskRatioDic, BaseDAO dao,
			Context ctx) throws ServiceException {
		String result = "";
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		List<Map<String, Object>> list = null;
		Dictionary category = null;
		try {
			list = cvdsModel.getSuggestionRecordByExp(drugsExp);
			category = DictionaryController.instance().get(
					"chis.dictionary.category");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> obj = list.get(i);
			String v = (String) obj.get("category");
			DictionaryItem it = category.getItem(v);
			if (null != it) {
				result += (i + 1) + "、" + it.getText() + ":"
						+ ((String) obj.get("content")).trim() + "<br>\n\n";
			}
		}
		return result;
	}

	protected void doInitCvdAssessRegister(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		CVDServiceModel sm = new CVDServiceModel(dao);
		try {
			Map<String, Object> resBody = sm.initCvdAssessRegister(empiId);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CVD_AssessRegister));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("initCvdAssessRegister失败.");
		}
	}
	protected void doInitDiseaseRegistration(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CVDServiceModel sm = new CVDServiceModel(dao);
		try {
			Map<String, Object> resBody = sm.initDiseaseRegistration(req);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CVD_DiseaseManagement));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("initCvdAssessRegister失败.");
		}
	}
	protected void doInitDiseaseVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CVDServiceModel sm = new CVDServiceModel(dao);
		try {
			Map<String, Object> resBody = sm.initDiseaseVerification(req);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CVD_DiseaseVerification));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("initDiseaseVerification失败.");
		}
	}
	
	
	private String getWeightControl(double bmi, String sexCode, double wh) {
		String weightControl = "";
		if (bmi >= 28) {
			weightControl = "1";
		} else if (bmi >= 24
				|| ((sexCode.equals("1") && wh > 0.9) || (sexCode.equals("2") && wh > 0.85))) {
			weightControl = "2";
		} else if (bmi < 18.5) {
			weightControl = "4";
		} else {
			weightControl = "3";
		}
		return weightControl;
	}

	@SuppressWarnings("unchecked")
	protected void doExportCVDFile(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws SQLException, IOException, PersistentDataOperationException {
		int count = 0;
		try {
			String hql = "select * from cvd_testdict";
			List<?> l = dao.doQuery(hql, null);
			count = l.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> map = (Map<String, Object>) req.get("body");
		List<?> dataList = this.getDataList(dao, map);
		HSSFWorkbook wb = new HSSFWorkbook();
		this.createWorkbook(wb, count, dataList);

		URL url = this.getClass().getClassLoader().getResource("");
		String path = URLDecoder.decode(url.getPath(), "UTF-8")
				+ "../../tmp/cvd/export/";
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String absPath = f.getAbsolutePath() + File.separator
				+ BSCHISUtil.toString(new Date(), "yyyyMMddHHmmss") + ".xls";
		File file = new File(absPath);
		FileOutputStream fos = new FileOutputStream(file);
		wb.write(fos);
		fos.close();

		res.put("path", "tmp/cvd/export/" + file.getName());
	}

	private List<?> getDataList(BaseDAO dao, Map<String, Object> map)
			throws PersistentDataOperationException {
		List<?> l = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		StringBuffer hql1 = new StringBuffer();
		if (map.get("isLastExport").equals("1")) {
			hql.append("select a.empiid, max(inputDate) as inputDate from CVD_AssessRegister a, CVD_Appraisal b where a.inquireid = b.inquireid");
		} else {
			hql.append("select * from CVD_AssessRegister a,CVD_Appraisal b where a.inquireId = b.inquireId");
		}

		StringBuffer where = new StringBuffer();
		if (!map.get("personName").equals("")) {
			where.append(" and a.personname =:personName");
		}
		if (!map.get("sexCode").equals("")) {
			where.append(" and a.sexCode =:sexCode");
		}
		if (!map.get("birthday").equals("")) {
			where.append(" and to_char(a.birthday,'yyyy-MM-dd') =:birthday");
		}
		if (!map.get("idCard").equals("")) {
			where.append(" and a.idCard =:idCard");
		}
		if (!map.get("riskPredictionResult").equals("")) {
			where.append(" and b.riskPredictionResult =:riskPredictionResult");
		}
		if (!map.get("beginDate").equals("")) {
			where.append(" and to_char(a.inputDate,'yyyy-MM-dd') >=:beginDate");
		}
		if (!map.get("endDate").equals("")) {
			where.append(" and to_char(a.inputDate,'yyyy-MM-dd') <=:endDate");
		}
		if (!map.get("inputUnit").equals("")) {
			where.append(" and a.inputUnit like :inputUnit||'%'");
		}
		if (!map.get("residenceAddress").equals("")) {
			where.append(" and a.residenceAddress like :residenceAddress||'%'");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (map.get("isLastExport").equals("1")) {
			hql1.insert(
					0,
					"select * "
							+ "from CVD_AssessRegister a,CVD_Appraisal b "
							+ "where a.inquireId = b.inquireId "
							+ "and exists (select '1' from  (select a.inquireid "
							+ "from CVD_AssessRegister a,(")
					.append(hql)
					.append(where)
					.append(" group by a.empiId) t "
							+ "where a.empiid = t.empiId and a.inputdate = t.inputDate ) p where a.inquireid = p.inquireid ) ");
		} else {
			hql.append(where);
		}

		if (!map.get("personName").equals("")) {
			parameters.put("personName", (String) map.get("personName"));
		}
		if (!map.get("sexCode").equals("")) {
			parameters.put("sexCode", (String) map.get("sexCode"));
		}
		if (!map.get("birthday").equals("")) {
			parameters.put("birthday", (String) map.get("birthday"));
		}
		if (!map.get("idCard").equals("")) {
			parameters.put("idCard", (String) map.get("idCard"));
		}
		if (!map.get("riskPredictionResult").equals("")) {
			parameters.put("riskPredictionResult",
					(String) map.get("riskPredictionResult"));
		}
		if (!map.get("beginDate").equals("")) {
			parameters.put("beginDate", (String) map.get("beginDate"));
		}
		if (!map.get("endDate").equals("")) {
			parameters.put("endDate", (String) map.get("endDate"));
		}
		if (!map.get("inputUnit").equals("")) {
			parameters.put("inputUnit", (String) map.get("inputUnit"));
		}
		if (!map.get("residenceAddress").equals("")) {
			parameters.put("residenceAddress",
					(String) map.get("residenceAddress"));
		}
		l = dao.doQuery(hql.toString(), parameters);
		return l;
	}

	private void createWorkbook(HSSFWorkbook wb, int count, List<?> l)
			throws SQLException, IOException {
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(
					"chis.application.cvd.schemas.CVD_AssessRegisterExport");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = wb.createSheet("心血管预测");
		HSSFRow row = sheet.createRow(0);
		row.createCell(1).setCellValue(new HSSFRichTextString("姓名"));
		row.createCell(2).setCellValue(new HSSFRichTextString("性别"));
		row.createCell(3).setCellValue(new HSSFRichTextString("出生日期"));
		row.createCell(4).setCellValue(new HSSFRichTextString("市民卡号"));
		row.createCell(5).setCellValue(new HSSFRichTextString("身份证号"));
		row.createCell(6).setCellValue(new HSSFRichTextString("联系方式"));
		row.createCell(7).setCellValue(new HSSFRichTextString("户籍地址"));
		row.createCell(8).setCellValue(new HSSFRichTextString("家庭地址"));
		row.createCell(9).setCellValue(new HSSFRichTextString("是否吸烟"));
		row.createCell(10).setCellValue(new HSSFRichTextString("身高(cm)"));
		row.createCell(11).setCellValue(new HSSFRichTextString("体重(kg)"));
		row.createCell(12).setCellValue(new HSSFRichTextString("腰围(cm)"));
		row.createCell(13).setCellValue(new HSSFRichTextString("臀围(cm)"));
		row.createCell(14).setCellValue(new HSSFRichTextString("总胆固醇"));
		row.createCell(15).setCellValue(new HSSFRichTextString("低密度脂蛋白"));
		row.createCell(16).setCellValue(new HSSFRichTextString("高密度脂蛋白"));
		row.createCell(17).setCellValue(new HSSFRichTextString("甘油三酯"));
		row.createCell(18).setCellValue(new HSSFRichTextString("是否糖尿病"));
		row.createCell(19).setCellValue(new HSSFRichTextString("空腹血糖(mmol/L)"));
		row.createCell(20).setCellValue(new HSSFRichTextString("餐后血糖(mmol/L)"));
		row.createCell(21).setCellValue(new HSSFRichTextString("随机血糖(mmol/L)"));
		row.createCell(22).setCellValue(new HSSFRichTextString("是否有高血压"));
		row.createCell(23).setCellValue(new HSSFRichTextString("收缩压(mmHg)"));
		row.createCell(24).setCellValue(new HSSFRichTextString("舒张压(mmHg)"));
		row.createCell(25).setCellValue(new HSSFRichTextString("是否高血压治疗"));
		row.createCell(26).setCellValue(new HSSFRichTextString("心血管疾病"));
		row.createCell(27).setCellValue(new HSSFRichTextString("危险因素"));
		row.createCell(28).setCellValue(new HSSFRichTextString("血压持续升高"));
		row.createCell(29).setCellValue(new HSSFRichTextString("肾部疾病"));
		row.createCell(30).setCellValue(new HSSFRichTextString("建档日期"));
		row.createCell(31).setCellValue(new HSSFRichTextString("建档机构"));
		row.createCell(32).setCellValue(new HSSFRichTextString("建档人员"));
		row.createCell(33).setCellValue(new HSSFRichTextString("危险度"));
		row.createCell(34).setCellValue(new HSSFRichTextString("危险度预测"));
		row.createCell(35).setCellValue(new HSSFRichTextString("健康知识评分"));
		row.createCell(36).setCellValue(new HSSFRichTextString("健康行为评分"));

		if (l.size() > 0) {
			for (int i = 0; i < l.size(); i++) {
				Map<?, ?> r = (Map<?, ?>) l.get(i);
				Map<String, Object> jsonRec = new HashMap<String, Object>();
				body.add(jsonRec);
				List<SchemaItem> li = sc.getItems();
				for (int j = 0; j < li.size(); j++) {
					String name = li.get(j).getId();
					SchemaItem si = sc.getItem(name);
					if (si.getType().equals("text")) {
						Clob clob = (Clob) r.get(name.toUpperCase());
						Reader reader = clob.getCharacterStream();
						BufferedReader br = new BufferedReader(reader);
						StringBuffer sb = new StringBuffer();
						String temp;
						while ((temp = br.readLine()) != null) {
							sb.append(temp);
						}
						jsonRec.put(name, sb.toString());
						br.close();
						continue;
					}
					Object v = r.get(name.toUpperCase());
					jsonRec.put(name, v);
					if (si != null && si.isCodedValue()) {
						Object dv = null;
						boolean isCacheDic = false;
						for (int k = 0; k < li.size(); k++) {
							if (li.get(k).getId().equals(name + "_text")) {
								dv = r.get((name + "_text").toUpperCase());
								isCacheDic = true;
								break;
							}
						}
						if (dv != null) {
							jsonRec.put(name + "_text", dv);
						} else {
							if (!isCacheDic) {
								jsonRec.put(name + "_text",
										si.toDisplayValue(v));
							}
						}
					}
					if (name.equals("answer") && null != v) {
						String answer = v.toString();
						if (!answer.equals("")) {
							for (int k = 0; k < v.toString().length(); k++) {
								String temp = v.toString().substring(k, k + 1);
								if (temp.equals("1")) {
									temp = "对";
								} else {
									temp = "否";
								}
								jsonRec.put(name + (k + 1), temp);
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < l.size(); i++) {
			Map<String, Object> json = (Map<String, Object>) body.get(i);
			HSSFRow r = sheet.createRow(i + 1);
			r.createCell(1).setCellValue(
					new HSSFRichTextString((String) json.get("personName")));
			r.createCell(2).setCellValue(
					new HSSFRichTextString((String) json.get("sexCode_text")));
			r.createCell(3).setCellValue(
					new HSSFRichTextString((String) json.get("birthday")));
			r.createCell(4).setCellValue(
					new HSSFRichTextString((String) json.get("cardNo")));
			r.createCell(5).setCellValue(
					new HSSFRichTextString((String) json.get("idCard")));
			r.createCell(6).setCellValue(
					new HSSFRichTextString((String) json.get("contact")));
			r.createCell(7).setCellValue(
					new HSSFRichTextString((String) json
							.get("residenceAddress_text")));
			r.createCell(8).setCellValue(
					new HSSFRichTextString((String) json.get("homeAddress")));
			r.createCell(9).setCellValue(
					new HSSFRichTextString((String) json.get("smoke_text")));
			r.createCell(10).setCellValue(
					json.get("height") == null ? 0 : (Double) json
							.get("height"));
			r.createCell(11).setCellValue(
					json.get("weight") == null ? 0 : (Double) json
							.get("weight"));
			r.createCell(12).setCellValue(
					json.get("waistLine") == null ? 0 : (Double) json
							.get("waistLine"));
			r.createCell(13).setCellValue(
					json.get("hipLine") == null ? 0 : (Double) json
							.get("hipLine"));
			r.createCell(14).setCellValue(
					json.get("tc") == null ? 0 : (Double) json.get("tc"));
			r.createCell(15).setCellValue(
					json.get("ldl") == null ? 0 : (Double) json.get("ldl"));
			r.createCell(16).setCellValue(
					json.get("hdl") == null ? 0 : (Double) json.get("hdl"));
			r.createCell(17).setCellValue(
					json.get("tg") == null ? 0 : (Double) json.get("tg"));
			r.createCell(18).setCellValue(
					new HSSFRichTextString((String) json.get("diabetes_text")));
			r.createCell(19).setCellValue(
					json.get("fbs") == null ? 0 : (Double) json.get("fbs"));
			r.createCell(20).setCellValue(
					json.get("pbs") == null ? 0 : (Double) json.get("pbs"));
			r.createCell(21).setCellValue(
					json.get("rbs") == null ? 0 : (Double) json.get("rbs"));
			r.createCell(22).setCellValue(
					new HSSFRichTextString((String) json
							.get("hypertension_text")));
			r.createCell(23).setCellValue(
					json.get("constriction") == null ? 0 : (Integer) json
							.get("constriction"));
			r.createCell(24).setCellValue(
					json.get("diastolic") == null ? 0 : (Integer) json
							.get("diastolic"));
			r.createCell(25).setCellValue(
					new HSSFRichTextString((String) json
							.get("hypertensionTreat_text")));
			r.createCell(26).setCellValue(
					new HSSFRichTextString((String) json
							.get("cardiovascular_text")));
			r.createCell(27)
					.setCellValue(
							new HSSFRichTextString((String) json
									.get("riskiness_text")));
			r.createCell(28).setCellValue(
					new HSSFRichTextString((String) json
							.get("bpIncreased_text")));
			r.createCell(29).setCellValue(
					new HSSFRichTextString((String) json.get("kidney_text")));
			r.createCell(30).setCellValue(
					new HSSFRichTextString((String) json.get("inputDate")));
			r.createCell(31)
					.setCellValue(
							new HSSFRichTextString((String) json
									.get("inputUnit_text")));
			r.createCell(32)
					.setCellValue(
							new HSSFRichTextString((String) json
									.get("inputUser_text")));
			r.createCell(33).setCellValue(
					new HSSFRichTextString((String) json
							.get("riskPredictionResult_text")));
			r.createCell(34)
					.setCellValue(
							new HSSFRichTextString((String) json
									.get("riskPrediction")));
		}
	}

	@SuppressWarnings("unused")
	protected void doModifyData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		CVDServiceModel cvdsModel = new CVDServiceModel(dao);
		List<Map<String, Object>> list = cvdsModel.getModifyRecord();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> row = list.get(i);
			String inquireId = (String) row.get("inquireId");
			List<Map<String, Object>> recoreds = cvdsModel
					.getAssessRegisterByInquireId(inquireId);
			Map<String, Object> modifyReq = new HashMap<String, Object>();
			Map<String, Object> modifyBody = list.get(0);
			modifyReq.put("body", modifyBody);
			modifyReq.put("op", "update");
			this.doSaveAssessRegister(modifyReq, res, dao, ctx);
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetCVDControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(CVD_AssessRegister, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check CVD_AssessRegister control error.", e);
			throw e;
		}
		res.put("body", data);
	}

	@SuppressWarnings("unchecked")
	protected void doRemoveAssessRegister(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String inquireId = (String) reqBody.get("pkey");
		String empiId = (String) reqBody.get("empiId");
		CVDServiceModel csm = new CVDServiceModel(dao);
		try {
			csm.deleteAssessRegisterRecord(inquireId);
		} catch (Exception e) {
			throw new ServiceException("删除心血管数据失败!");
		}
		vLogService.saveVindicateLog(CVD_AssessRegister, "4", inquireId, dao, empiId);
	}
}

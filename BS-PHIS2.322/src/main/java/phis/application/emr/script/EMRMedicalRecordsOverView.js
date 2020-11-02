$package("phis.application.emr.script");

$import("phis.script.SimpleModule");

phis.application.emr.script.EMRMedicalRecordsOverView = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsOverView.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordsOverView,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var html = '<HTML>';
				html += '<BODY>';
				html += '<table style="border:1px solid #000000;border-collapse:collapse;width:800px;left:20px;position:relative;line-height:25px;">';
				html += '<tr><td align="center" colspan = "3">医疗机构<input id="YLJGMC" style="width:200px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />(组织机构代码：<input id="YLJGDM" style="width:80px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;"  type="text" />)</td></tr>';
				html += '<tr><td style="width:266px">&nbsp;医疗付费方式：<input id="YLFYDM_text" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: 0px solid;background-color: #FFFFFF;" /><input id="YLFYDM" style="display:none"/></td><td style="width:266px;" align="center"><span style="font-weight:bold;font-size:18pt">住院病案首页</span></td><td></td></tr>';
				html += '<tr><td>&nbsp;健康卡号：<input id="JMJKKH" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: 0px solid;background-color: #FFFFFF;" type="text" /></td><td align="center">第<input id="ZYCS" style="width:30px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: 0px solid;background-color: #FFFFFF;text-align:center;" type="text" />次住院</td><td align="right">病案号：<input id="BAHM" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: 0px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '</table><table style="width:800px;left:20px;position:relative;border:1px solid #000000;line-height:25px;">';
				html += '<tr><td>&nbsp;<span style="font-weight:bold;">姓名</span><input id="ZL_BRXM" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td><td><span style="font-weight:bold;">性别</span><input id="ZL_BRXB" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />1.男 2.女</td><td><span style="font-weight:bold;">出生日期</span><input id="CSNY" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td><td><span style="font-weight:bold;">年龄</span><input id="BRNL" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">国籍</span><input id="GJDM_text" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="GJDM" style="display:none"/></td></tr>';
				html += '<tr><td>&nbsp;(年龄不足一周岁的)</td><td><span style="font-weight:bold;">年龄</span><input id="YL" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">月</span></td><td><span style="font-weight:bold;">新生儿出生体重</span><input id="XSECSTZ" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">克</span></td><td><span style="font-weight:bold;">新生儿入院体重</span><input id="XSERYTZ" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">克</span></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">出生地</span><input id="CSD_SQS_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="CSD_SQS" style="display:none;"/>&nbsp;<input id="CSD_S_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="CSD_S" style="display:none"/>&nbsp;<input id="CSD_X_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="CSD_X" style="display:none"/>&nbsp;<span style="font-weight:bold;">籍贯</span><input id="JGDM_SQS_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="JGDM_SQS" style="display:none"/>&nbsp;<input id="JGDM_S_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="JGDM_S" style="display:none"/>&nbsp; <span style="font-weight:bold;">民族</span><input id="MZDM_text" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" ><input id="MZDM" style="display:none"/></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">身份证号</span><input id="SFZJHM" style="width:170px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">职业</span><input id="ZYDM_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" ><input id="ZYDM" style="display:none"/><span style="font-weight:bold;"> 婚姻</span><input id="HYDM" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" >1.未婚 2.已婚 3.丧偶 4.离婚 9.其他</td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">现住址</span><input id="XZZ_SQS_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="XZZ_SQS" style="display:none"/>&nbsp;<input id="XZZ_S_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="XZZ_S" style="display:none"/>&nbsp;<input id="XZZ_X_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="XZZ_X" style="display:none"/>&nbsp;<input id="XZZ_DZ" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">电话</span><input id="XZZ_DH" style="width:90px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">邮编</span><input id="XZZ_YB" style="width:80px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">户口地址</span><input id="HKDZ_SQS_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="HKDZ_SQS" style="display:none"/>&nbsp;<input id="HKDZ_S_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="HKDZ_S" style="display:none"/>&nbsp;<input id="HKDZ_X_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="HKDZ_X" style="display:none"/>&nbsp;<input id="HKDZ_DZ" style="width:252px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">邮编</span><input id="HKDZ_YB" style="width:80px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">工作单位及地址</span><input id="DWDZ" style="width:410px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">单位电话</span><input id="DWDH" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">邮编</span><input id="DWYB" style="width:80px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">联系人姓名</span><input id="LXRXM" style="width:105px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">关系</span><input id="LXRGX_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text"/><input id="LXRGX" style="display:none"/><span style="font-weight:bold;">地址</span><input id="LXRDZ" style="width:300px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">电话</span><input id="LXRDH" style="width:90px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">入院途径</span><input id="RYTJ" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" >1.急诊&nbsp;&nbsp;2.门诊&nbsp;&nbsp;3.其他医疗机构转入 &nbsp;&nbsp;9.其他</td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">入院时间</span><input id="ZL_RYRQ" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">入院科别</span><input id="RYKS_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="RYKS" style="display:none"/><span style="font-weight:bold;">病房</span><input id="RYBF_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="RYBF" style="display:none"/><span style="font-weight:bold;">转科科别</span><input id="ZKKSMC" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">出院时间</span><input id="CYRQ" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">出院科别</span><input id="CYKS_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="CYKS" style="display:none"/><span style="font-weight:bold;">病房</span><input id="CYBQ_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><input id="CYBQ" style="display:none"/><span style="font-weight:bold;">实际住院</span><input id="SJZYYS" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">天</span></td></tr>';
				html += '<tr><td colspan = "4">&nbsp;<span style="font-weight:bold;">门(急)诊诊断</span><input id="MZZD" style="width:350px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /><span style="font-weight:bold;">疾病编码</span><input id="MZ_JBBM" style="width:200px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '</table>';
				html += '<table style="border:1px solid #000000;border-collapse:collapse;width:800px;left:20px;position:relative;line-height:25px;">';
				html += '<tr><td align="center" style="width:200px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">出院诊断</td><td align="center" style="width:100px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">疾病编码</td><td align="center" style="width:100px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">入院病情</td><td align="center" style="width:200px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">出院诊断</td><td align="center" style="width:100px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">疾病编码</td><td align="center" style="width:100px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">入院病情</td></tr>';
				html += '<tr><td id="MSZD_0" style="border:1px solid #000000;border-collapse:collapse;"><span style="font-weight:bold;">&nbsp;主要诊断：</span></td><td id="JBBM_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_11" style="border:1px solid #000000;border-collapse:collapse;"><span style="font-weight:bold;">&nbsp;其他诊断：</span></td><td id="JBBM_11" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_11" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_1" style="border:1px solid #000000;border-collapse:collapse;"><span style="font-weight:bold;">&nbsp;其他诊断：</span></td><td id="JBBM_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_12" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_12" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_12" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_2" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_13" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_13" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_13" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_3" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_14" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_14" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_14" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_4" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_15" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_15" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_15" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_5" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_16" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_16" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_16" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_17" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_17" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_17" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_7" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_18" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_18" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_18" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_8" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_8" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_8" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_19" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_19" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_19" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_9" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_9" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_9" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_20" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_20" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_20" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="MSZD_10" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;</td><td id="JBBM_10" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_10" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MSZD_21" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_21" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_21" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td colspan = "3"  style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">入院病情：</span>1.有，2.临床未确定，3.情况不明，4.无 </td><td id="MSZD_22" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="JBBM_22" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="RYBQDM_text_22" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">损伤、中毒的外部原因</span><input id="SSZD" style="width:360px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><span style="font-weight:bold;">疾病编码</span><input id="SS_JBBM" style="width:250px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /></td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">病理诊断：</span><input id="BLZD" style="width:425px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><span style="font-weight:bold;">疾病编码</span><input id="BL_JBBM" style="width:250px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><input id="BLZD01" style="width:490px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><span style="font-weight:bold;">病理号</span><input id="BLH" style="width:260px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /></td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">药物过敏</span>&nbsp;&nbsp;<input id="GMYWBZ" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;&nbsp;1.无&nbsp;&nbsp; 2.有，&nbsp;&nbsp;过敏药物：<input id="GMYWMC" style="width:250px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><span style="font-weight:bold;">&nbsp;&nbsp;&nbsp;&nbsp;死亡患者尸检</span>&nbsp;&nbsp;<input id="SJBZ" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;&nbsp;1.是  &nbsp;&nbsp;2.否</td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">血型 </span>&nbsp;&nbsp;<input id="ABOXXDM" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> &nbsp;&nbsp;1.A  &nbsp;&nbsp;2.B  &nbsp;&nbsp;3.O  &nbsp;&nbsp;4.AB  &nbsp;&nbsp;5.不详  6.未查  &nbsp;&nbsp;&nbsp;&nbsp;<span style="font-weight:bold;">Rh</span> &nbsp;&nbsp;<input id="RHXXDM" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />   &nbsp;&nbsp;1.阴 &nbsp;&nbsp;2.阳 &nbsp;&nbsp;3.不详  4.未查</td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">科主任</span>&nbsp;<input id="KZRQM_text" style="width:130px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">主任（副主任）医师</span>&nbsp;<input id="ZRYSQM_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">主治医师</span>&nbsp;<input id="ZZYSQM_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">住院医师</span>&nbsp;<input id="ZYYSQM_text" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />';
				html += '<br>&nbsp;<span style="font-weight:bold;">责任护士</span>&nbsp;<input id="ZRHSQM_text" style="width:115px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">进修医师</span>&nbsp;<input id="JXYSQM_text" style="width:188px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">实习医师</span>&nbsp;<input id="SXYSQM_text" style="width:110px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">编码员</span>&nbsp;<input id="BABMYQM_text" style="width:132px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /></td></tr>';
				html += '<tr><td colspan = "6" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">病案质量</span> &nbsp;&nbsp;<input id="BAZL" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> &nbsp;&nbsp;1.甲&nbsp;&nbsp;&nbsp;&nbsp; 2.乙 &nbsp;&nbsp;&nbsp;&nbsp;3.丙 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-weight:bold;">质控医师</span><input id="ZKYSQM_text" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />&nbsp;<span style="font-weight:bold;">质控护士</span><input id="ZKHSQM_text" style="width:150px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /><br>&nbsp;<span style="font-weight:bold;">质控日期</span><input id="ZKRQ" style="width:180px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /></td></tr>';
				html += '</table>';
				html += '<table style="width:800px;left:20px;position:relative;line-height:25px;border:1px solid #000000;border-collapse:collapse;">';
				html += '<tr><td rowspan="2" align="center" style="width:80px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">&nbsp;手术及操作编码&nbsp;</td><td rowspan="2" align="center" style="width:120px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">手术及操作日期</td><td rowspan="2" align="center" style="width:45px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">手术级别</td><td align="center" rowspan="2" style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">手术及操作名称</td><td align="center" colspan = "3" style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">手术及操作医师</td><td align="center" rowspan="2" style="width:60px;border:1px solid #000000;border-collapse:collapse;font-weight:bold;">切口愈合等级</td><td align="center" rowspan="2"  style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">麻醉方式</td><td align="center" rowspan="2"  style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">麻醉医师</td></tr>';
				html += '<tr><td style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">术者</td><td style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">Ⅰ助</td><td style="border:1px solid #000000;border-collapse:collapse;font-weight:bold;">Ⅱ助</td></tr>';
				html += '<tr><td id="SSDM_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_0" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_0" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_1" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_1" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_2" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_2" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_3" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_3" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_4" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_4" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_5" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_5" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_6" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_6" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td id="SSDM_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSRQ_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSJB_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSMC_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="SSYS_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="YZYS_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="EZYS_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td align="center" id="YHDJ_text_7" style="border:1px solid #000000;border-collapse:collapse;">/</td><td id="MZFS_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td><td id="MZYS_text_7" style="border:1px solid #000000;border-collapse:collapse;"></td></tr>';
				html += '<tr><td colspan = "10" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;<span style="font-weight:bold;">离院方式</span> &nbsp;<input id="LYFS" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> &nbsp;&nbsp;1.医嘱离院 &nbsp;&nbsp; 2.医嘱转院，&nbsp;&nbsp;拟接收医疗机构名称：<input id="NJSYLJLMC_2" style="width:300px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> <br>&nbsp;3.医嘱转社区卫生服务机构/乡镇卫生院，&nbsp;&nbsp;拟接收医疗机构名称：<input id="NJSYLJLMC_3" style="width:200px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> 4.非医嘱离院  &nbsp;&nbsp;5.死亡 &nbsp;&nbsp; 9.其他  </td></tr>';
				html += '<tr><td colspan = "10" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;是否有出院31天内再住院计划 <input id="CY31ZYBZ" style="width:18px;border-right: 1px solid;border-top: 1px solid;border-left: 1px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /> 1.无  2.有，目的:<input id="CY31ZYMD" style="width:200px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" /></td></tr>';
				html += '<tr><td colspan = "10" style="border:1px solid #000000;border-collapse:collapse;">&nbsp;颅脑损伤患者昏迷时间： &nbsp;&nbsp;入院前<input id="RYQHMSJ_T" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />天<input id="RYQHMSJ_S" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />小时<input id="RYQHMSJ_F" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />分钟    &nbsp;&nbsp;&nbsp;&nbsp;入院后<input id="RYHHMSJ_T" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />天<input id="RYHHMSJ_S" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />小时<input id="RYHHMSJ_F" style="width:50px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" />分钟</td></tr>';
				html += '</table>';
				html += '<table style="width:800px;left:20px;position:relative;border:1px solid #000000;line-height:25px;">';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;住院费用(元)：总费用</span><input id="ZYZFY" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（自付金额：<input id="ZL_ZFJE" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />）</td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;1.综合医疗服务类：</span>（1）一般医疗服务费：<input id="YBYLFWF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（2）一般治疗操作费：<input id="YBZLCZF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（3）护理费：<input id="HLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></br>（4）其他费用：<input id="QTFY" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;2.诊断类：</span>（5）病理诊断费：<input id="BLZDF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（6）实验室诊断费：<input id="SYSZDF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（7）影像学诊断费：<input id="YXXZDF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></br>（8）临床诊断项目费：<input id="LCZDXMF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;3.治疗类：</span>（9）非手术治疗项目费：<input id="FSSZLXMF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（临床物理治疗费：<input id="LCWLZLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />）</br>（10）手术治疗费：<input id="SSZLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（麻醉费：<input id="MZF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />手术费：<input id="SSF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />）</td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;4.康复类：</span>（11）康复费：<input id="KFF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;5.中医类：</span>（12）中医治疗费：<input id="ZYZLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;6.西药类：</span>（13）西药费：<input id="XYF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（抗菌药物费用：<input id="KJYWFY" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />）</td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;7.中药类：</span>（14）中成药费：<input id="ZCYF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（15）中草药费：<input id="ZCY" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;8.血液和血液制品类：</span>（16）血费：<input id="XF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（17）白蛋白类制品费：<input id="BDBLZPF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（18）球蛋白类制品费：<input id="QDBLZPF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></br>（19）凝血因子类制品费：<input id="NXYZLZPF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（20）细胞因子类制品费：<input id="XBYZLZPF" style="width:120px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;9.耗材类：</span>（21）检查用一次性医用材料费：<input id="JCYCLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" />（22）治疗用一次性医用材料费：<input id="ZLYCLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></br>（23）手术用一次性医用材料费：<input id="SSYCLF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr>';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;10.其他类：</span>（24）其他费：<input id="QTF" style="width:100px;border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;background-color: #FFFFFF;" type="text" /></td></tr></table>';
				html += '<table style="width:800px;left:20px;position:relative;border:1px solid #000000;line-height:25px;">';
				html += '<tr><td><span style="font-weight:bold;">&nbsp;说明：</span>（一）医疗付费方式  1.城镇职工基本医疗保险  2.城镇居民基本医疗保险 3.新型农村合作医疗 4.贫困救助  5.商业医疗保险  6.全公费  7.全自费  </br>&nbsp;8.其他社会保险  9.其他     ';
				html += '（二）凡可由医院信息系统提供住院费用清单的，住院病案首页中可不填写“住院费用”。</td></tr>';
				html += '</table>';
				html += '</BODY></HTML>';
				var panel = new Ext.Panel({
							frame : false,
							autoScroll : true,
							html : html
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			getFYTJForm : function() {
				this.FYTJModule = this.createModule("CIC00105", this.CIC00105);
				this.FYTJForm = this.FYTJModule.initPanel();
				return this.FYTJForm;
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : 870,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel(),
								autoScroll : true,
								height : 600
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			onWinShow : function() {
				this.loadData();
			},
			loadData : function() {
				if (this.exContext.BRXX.RYQHMSJ_T) {
					document.getElementById("RYQHMSJ_T").value = this.exContext.BRXX.RYQHMSJ_T;
					document.getElementById("RYQHMSJ_S").value = this.exContext.BRXX.RYQHMSJ_S;
					document.getElementById("RYQHMSJ_F").value = this.exContext.BRXX.RYQHMSJ_F;
					document.getElementById("RYHHMSJ_T").value = this.exContext.BRXX.RYHHMSJ_T;
					document.getElementById("RYHHMSJ_S").value = this.exContext.BRXX.RYHHMSJ_S;
					document.getElementById("RYHHMSJ_F").value = this.exContext.BRXX.RYHHMSJ_F;
				}
				document.getElementById("RYQHMSJ_T").readOnly=true;
				document.getElementById("RYQHMSJ_S").readOnly=true;
				document.getElementById("RYQHMSJ_F").readOnly=true;
				document.getElementById("RYHHMSJ_T").readOnly=true;
				document.getElementById("RYHHMSJ_S").readOnly=true;
				document.getElementById("RYHHMSJ_F").readOnly=true;
				document.getElementById("NJSYLJLMC_2").readOnly=true;
				document.getElementById("NJSYLJLMC_3").readOnly=true;
				this.initData(this.properties.EMR_BASY, 1);
				this.initData(this.properties.EMR_BASY_FY, 1);
				this.initSsData(this.exContext.ZYSSJL);
				this.initZdData(this.exContext.ZYZDJL);
			},
			onReady : function() {

			},
			initSsData : function(ZYSSJLS) {
				var schema = this.getMySchema(this.properties.EMR_ZYSSJL);
				var items = schema.items
				var size = items.length;
				for (var i = 0; i < 8; i++) {
					if (i < ZYSSJLS.length) {
						var zyssjl = ZYSSJLS[i];
						for (var j = 0; j < size; j++) {
							var it = items[j]
							if (document.getElementById(it.id + "_" + i)) {
								document.getElementById(it.id + "_" + i).innerHTML = '&nbsp;'
										+ zyssjl[it.id]
							} else if (document.getElementById(it.id + "_text_"
									+ i)) {
								document.getElementById(it.id + "_text_" + i).innerHTML = '&nbsp;'
										+ zyssjl[it.id + "_text"]
							}
						}
					} else {
						for (var j = 0; j < size; j++) {
							var it = items[j]
							if (document.getElementById(it.id + "_" + i)) {
								document.getElementById(it.id + "_" + i).innerHTML = "&nbsp;"
							} else if (document.getElementById(it.id + "_text_"
									+ i)) {
								document.getElementById(it.id + "_text_" + i).innerHTML = "&nbsp;"
							}
						}
					}
				}
			},
			initZdData : function(ZYZDJLS) {
				for (var i = 0; i < 23; i++) {
					if (i < ZYZDJLS.length) {
						var zyzdjl = ZYZDJLS[i];
						if (i == 0) {
							document.getElementById("MSZD_" + i).innerHTML = '<span style="font-weight:bold;">&nbsp;主要诊断:</span>'
									+ zyzdjl["MSZD"]

						} else if (i == 1 || i == 11) {
							document.getElementById("MSZD_" + i).innerHTML = '<span style="font-weight:bold;">&nbsp;其他诊断:</span>'
									+ zyzdjl["MSZD"]
						} else {
							document.getElementById("MSZD_" + i).innerHTML = '&nbsp;'
									+ zyzdjl["MSZD"]
						}
						document.getElementById("JBBM_" + i).innerHTML = '&nbsp;'
								+ zyzdjl["JBBM"]
						document.getElementById("RYBQDM_text_" + i).innerHTML = '&nbsp;'
								+ zyzdjl["RYBQDM_text"]
					} else {
						if (i == 0) {
							document.getElementById("MSZD_" + i).innerHTML = '<span style="font-weight:bold;">&nbsp;主要诊断:</span>'
						} else if (i == 1 || i == 11) {
							document.getElementById("MSZD_" + i).innerHTML = '<span style="font-weight:bold;">&nbsp;其他诊断:</span>'
						} else {
							document.getElementById("MSZD_" + i).innerHTML = '&nbsp;'
						}
						document.getElementById("JBBM_" + i).innerHTML = '&nbsp;'
						document.getElementById("RYBQDM_text_" + i).innerHTML = '&nbsp;'
					}
				}
			},
			initData : function(entryName, dic) {
				var schema = this.getMySchema(entryName);
				var items = schema.items
				var size = items.length;
				for (var i = 0; i < size; i++) {
					var it = items[i]
					// if(it.dic&&dic){
					// this.getStore(it.dic, it.id)// 关系
					// }
					var obj = document.getElementById(it.id);
					var zl_obj = document.getElementById("ZL_" + it.id);
					var obj_text = document.getElementById(it.id + "_text");
					if (obj) {
						if (this.exContext.BRXX[it.id]
								|| this.exContext.BRXX[it.id] == 0) {
							obj.value = this.exContext.BRXX[it.id];
						}
						if (dic) {
							obj.readOnly=true;
						}
					}
					if (zl_obj) {
						if (this.exContext.BRXX[it.id]
								|| this.exContext.BRXX[it.id] == 0) {
							zl_obj.value = this.exContext.BRXX[it.id];
						}
						if (dic) {
							zl_obj.readOnly=true;
						}
					}
					if (obj_text) {
						if (this.exContext.BRXX[it.id + "_text"]) {
							obj_text.value = this.exContext.BRXX[it.id
									+ "_text"];
						}
						if (dic) {
							obj_text.readOnly=true;
						}
					}
				}
				document.getElementById("MZZD").readOnly=true;
				document.getElementById("MZ_JBBM").readOnly=true;
				document.getElementById("SSZD").readOnly=true;
				document.getElementById("SS_JBBM").readOnly=true;
				document.getElementById("BLZD").readOnly=true;
				document.getElementById("BL_JBBM").readOnly=true;
				document.getElementById("BLZD01").readOnly=true;
				if (this.exContext.BRXX.MZZD) {
					document.getElementById("MZZD").value = this.exContext.BRXX.MZZD;
				}else{
					document.getElementById("MZZD").value = "";
				}
				if (this.exContext.BRXX.MZ_JBBM) {
					document.getElementById("MZ_JBBM").value = this.exContext.BRXX.MZ_JBBM;
				}else{
					document.getElementById("MZ_JBBM").value = "";
				}
				if (this.exContext.BRXX.SSZD) {
					document.getElementById("SSZD").value = this.exContext.BRXX.SSZD;
				}else{
					document.getElementById("SSZD").value = "";
				}
				if (this.exContext.BRXX.SS_JBBM) {
					document.getElementById("SS_JBBM").value = this.exContext.BRXX.SS_JBBM;
				}else{
					document.getElementById("SS_JBBM").value = "";
				}
				if (this.exContext.BRXX.BLZD) {
					document.getElementById("BLZD").value = this.exContext.BRXX.BLZD;
				}else{
					document.getElementById("BLZD").value = "";
				}
				if (this.exContext.BRXX.BL_JBBM) {
					document.getElementById("BL_JBBM").value = this.exContext.BRXX.BL_JBBM;
				}else{
					document.getElementById("BL_JBBM").value = "";
				}
			},
			getMySchema : function(entryName) {
				var schema = "";
				if (this.opener.schema) {
					schema = this.opener.schema[entryName]
				}
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if (this.opener.schema) {
							this.opener.schema[entryName] = schema;
						} else {
							this.opener.schema = {}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			getStore : function(dic, fieldId, value) {
				var this_ = this;
				var url = this.getUrl(dic)
				// add by yangl 请求统一加前缀
				if (this.requestData) {
					this.requestData.serviceId = 'phis.'
							+ this.requestData.serviceId
				}
				var fields = null;
				if (dic.fields) {
					dic.fields = dic.fields + "";
					fields = dic.fields.split(",")
				}
				var store = new Ext.data.JsonStore({
							totalProperty : 'result',
							url : url,
							root : 'items',
							fields : fields
									|| ['key', 'text',
											dic.searchField || "mCode"]
						})
				store.on('load', function() {
					if (this.getCount() == 0) {
						return;
					}
					document.getElementById(fieldId).options.add(new Option("",
							""));
					for (var i = 0; i < this.reader.jsonData.items.length; i++) {

						var key = this.reader.jsonData.items[i].key;
						var text = this.reader.jsonData.items[i].text;
						document.getElementById(fieldId).options
								.add(new Option(text, key));
					}
					if (value) {
						document.getElementById(fieldId).value = value;
					}
					if (this_.exContext.BRXX[fieldId]) {
						document.getElementById(fieldId).value = this_.exContext.BRXX[fieldId];
					}
				})
				store.load();
			},
			getUrl : function(dic) {
				var url = ClassLoader.serverAppUrl || ""
				url += dic.id + ".dic"
				if (dic.parentKey) {
					url += "?parentKey=" + dic.parentKey;
				}
				if (dic.slice) {
					if (url.indexOf("?") > -1) {
						url += "&sliceType=" + dic.slice
					} else {
						url += "?sliceType=" + dic.slice
					}
				}
				if (dic.src) {
					if (url.indexOf("?") > -1) {
						url += "&src=" + dic.src
					} else {
						url += "?src=" + dic.src
					}
				}
				if (dic.filter) {
					if (url.indexOf("?") > -1) {
						url += "&filter=" + dic.filter;
					} else {
						url += "?filter=" + dic.filter;
					}
				}
				url = encodeURI(url);
				return url
			}
		})
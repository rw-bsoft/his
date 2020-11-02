$package("phis.application.ccl.script");
$import("phis.script.TableForm")

phis.application.ccl.script.CheckApplyForm_WAR = function(cfg) {
	this.colCount = 3;
	this.autoFieldWidth = true
	this.showButtonOnTop = 0;
	this.labelWidth = 110;
	phis.application.ccl.script.CheckApplyForm_WAR.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.ccl.script.CheckApplyForm_WAR,
		phis.script.TableForm, {
			init : function() {
				if (this.form != undefined) {
					var form = this.form.getForm();
					var BRXM = form.findField("BRXM");
					var RYZD = form.findField("RYZD");
					//var ZRYS = form.findField("ZRYS");//主任医生
					//var BZXX = form.findField("BZXX");//备注信息
					var ZSXX = form.findField("ZSXX");
					//var CTXX = form.findField("CTXX");
					var XBS = form.findField("XBS");
					var JWS = form.findField("JWS");//既往史
				      var GMS = form.findField("GMS");//过敏史
				      var FZJC = form.findField("FZJC");//辅助检查
				      var TGJC = form.findField("TGJC");//体格检查
					//var SYXX = form.findField("SYXX");//实验室和器材检查
					//var XJ = form.findField("XJ");
					var XL = form.findField("XL");
					var XLV = form.findField("XLV");
					//var XY = form.findField("XY");
					//var XLSJ = form.findField("XLSJ");
					//var XGJC = form.findField("XGJC");

					BRXM.setValue(this.brxm);
					RYZD.setValue(this.jbmc);
					//ZRYS.setValue(this.zrys);
					// form.findField("BZXX").setValue("近两周内服毛地黄量 天内共
					// 克（正在服用/已停用); 天内其他对心肌或心律（率）有影响的药品及剂量有:");
					//BZXX.setValue("");
					ZSXX.setValue("");
					//CTXX.setValue("T:℃    P:次/分   R:次/分    BP: / mmHg   ");
					//SYXX.setValue("");
					XBS.setValue("");
					JWS.setValue("");
	                GMS.setValue("");
	                FZJC.setValue("");
	                TGJC.setValue("");
					//XJ.setValue("");
					XL.setValue("");
					XLV.setValue("");
					//XY.setValue("");
					//XLSJ.setValue("");
					//XGJC.setValue("");
				}
			}
		});
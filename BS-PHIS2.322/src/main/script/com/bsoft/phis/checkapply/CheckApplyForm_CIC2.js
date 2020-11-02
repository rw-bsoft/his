$package("com.bsoft.phis.checkapply")
$import("com.bsoft.phis.TableForm")

com.bsoft.phis.checkapply.CheckApplyForm_CIC2 = function(cfg) {
	this.colCount = 3;
	this.autoFieldWidth = true
	this.showButtonOnTop = 0;
	com.bsoft.phis.checkapply.CheckApplyForm_CIC2.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(com.bsoft.phis.checkapply.CheckApplyForm_CIC2, com.bsoft.phis.TableForm,
		{
			init : function(){
				if(this.form!=undefined){
					var form = this.form.getForm();
					var BRXM = form.findField("BRXM");//病人姓名
					var LCZD = form.findField("LCZD");//临床诊断
					var ZDYS = form.findField("ZDYS");//诊断医生
					var ZSXX = form.findField("ZSXX");//主诉信息
					var CTXX = form.findField("CTXX");//查体信息
					var SYXX = form.findField("SYXX");//实验室和器材检查
					var BZXX = form.findField("BZXX");//备注信息
					/*****************门诊首页获取的病历信息*********************/
					var zsxx = Ext.getCmp("ZSXX").getValue();//获取的主诉信息
					var t = document.getElementById("T").value;//体温
					var r = document.getElementById("R").value;//呼吸频率
					var p = document.getElementById("P").value;//脉搏
					var ssy = document.getElementById("SSY").value;//收缩压
					var szy = document.getElementById("SZY").value;//舒张压
					var height = document.getElementById("HEIGHT").value;//身高
					var weight = document.getElementById("WEIGHT").value;//体重
					var tgjc = Ext.getCmp("TGJC").getValue();//体格检查
					var ct = "T:"+t+"℃    P:"+p+"次/分   R:"+r+"次/分    BP:"+ssy+" / "+szy+"mmHg  身高:"+height+"cm   体重:"+weight+"kg  "+tgjc;
					var syxx = Ext.getCmp("FZJC").getValue();//实验室和器材检查
					/***************************************************/
					BRXM.setValue(this.brxm);
					LCZD.setValue(this.zdmc);
					ZDYS.setValue(this.zdys);
					ZSXX.setValue(zsxx);
					CTXX.setValue(ct);
					SYXX.setValue(syxx);
					BZXX.setValue("近两周内服毛地黄量     天内共    克（正在服用/已停用);   天内其他对心肌或心律（率）有影响的药品及剂量有:");
				}
				
			}

		});
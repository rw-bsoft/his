$package("chis.application.conf.script.mdc")
$import("chis.script.BizHtmlFormView",
		"chis.application.conf.script.mdc.DiabetesAssessmentTemplate",
		"util.Accredit")
$styleSheet("chis.css.diabetesAssess")
chis.application.conf.script.mdc.DiabetesAssessmentManage = function(cfg) {
	cfg.idPostfix = cfg.idPostfix || "_assess";
	chis.application.conf.script.mdc.DiabetesAssessmentManage.superclass.constructor
			.apply(this, [cfg]);
	Ext
			.apply(this,
					chis.application.conf.script.mdc.DiabetesAssessmentTemplate);
	this.autoLoadData = false;
	this.on("loadData", this.onLoadData, this);
	this.on("save", this.onSave, this)
	this.otherDisable = [{
		fld : "assessType",
		type : "radio",
		control : [{
			key : "2",
			exp : 'eq',
			field : ["assessYearCon_1", "assessYearCon_2", "assessYearCon_3",
					"normManage_1", "normManage_2", "normManage_3",
					"normScale1", "normScale2", "normScale3", "assessDays",
					"assessHour1", "assessHour2"],
			defaultValue : {
				"assessYearCon_1" : true,
				"assessYearCon_2" : true,
				"assessYearCon_3" : true,
				"normManage_1" : true,
				"normManage_2" : true,
				"normManage_3" : true,
				"normScale1" : 75,
				"normScale2" : 75,
				"normScale3" : 75,
				"assessDays" : 5,
				"assessHour1" : 16,
				"assessHour2" : 6
			}

		}]
	}]
}

Ext.extend(chis.application.conf.script.mdc.DiabetesAssessmentManage,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getDiabetesAssessmenHTML();
			},
			getLoadRequest : function() {
				return {
					"1" : "1"
				};
			},
			onLoadData : function(entryName, body) {
				if(body.planMode=="2"){
					MyMessageTip.msg("提示","随访方式为按下次预约时间，部分数据不可维护！", true);
					this.setHtmlFldValue("assessType","1");
					document.getElementById("assessType_1"+this.idPostfix).disabled=true;
					document.getElementById("assessType_2"+this.idPostfix).disabled=true;
				}else if(body.assessType=="2"){
					this.setHtmlFldValue("assessType","2");
					document.getElementById("assessType_1"+this.idPostfix).disabled=false;
					document.getElementById("assessType_2"+this.idPostfix).disabled=false;
				}
				this.needSetDefaultValue=true;
				this.setFieldEnable();
				this.addFieldDataValidateFun(this.schema);
			},
			onSave : function(entryName, op, json, data) {
				Ext.Msg.show({
							title : '提示信息',
							msg : '配置完成,请重新登录以激活配置！',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									location.reload();
								}
							},
							scope : this
						})
			},
			setFieldEnable : function() {
				var assessType = this.getHtmlFldValue("assessType");
				var assessYearCon_1 = document.getElementById("assessYearCon_1"
						+ this.idPostfix);
				var assessYearCon_2 = document.getElementById("assessYearCon_2"
						+ this.idPostfix);
				var assessYearCon_3 = document.getElementById("assessYearCon_3"
						+ this.idPostfix);
				var normManage_1 = document.getElementById("normManage_1"
						+ this.idPostfix);
				var normManage_2 = document.getElementById("normManage_2"
						+ this.idPostfix);
				var normManage_3 = document.getElementById("normManage_3"
						+ this.idPostfix);
				var normScale1 = document.getElementById("normScale1"
						+ this.idPostfix);
				var normScale2 = document.getElementById("normScale2"
						+ this.idPostfix);
				var normScale3 = document.getElementById("normScale3"
						+ this.idPostfix);
				var assessDays = document.getElementById("assessDays"
						+ this.idPostfix);
				var assessHour1 = document.getElementById("assessHour1"
						+ this.idPostfix);
				var assessHour2 = document.getElementById("assessHour2"
						+ this.idPostfix);
				if (assessType == "1") {
					assessYearCon_1.checked = false;
					assessYearCon_2.checked = false;
					assessYearCon_3.checked = false;
					normManage_1.checked = false;
					normManage_2.checked = false;
					normManage_3.checked = false;
					normScale1.value = "";
					normScale2.value = "";
					normScale3.value = "";
					assessDays.value = "";
					assessHour1.value = "";
					assessHour2.value = "";
					assessYearCon_1.disabled = true;
					assessYearCon_2.disabled = true;
					assessYearCon_3.disabled = true;
					normManage_1.disabled = true;
					normManage_2.disabled = true;
					normManage_3.disabled = true;
					normScale1.disabled = true;
					normScale2.disabled = true;
					normScale3.disabled = true;
					assessDays.disabled = true;
					assessHour1.disabled = true;
					assessHour2.disabled = true;
				} else {
					assessYearCon_1.disabled = false;
					assessYearCon_2.disabled = false;
					assessYearCon_3.disabled = false;
					normManage_1.disabled = false;
					normManage_2.disabled = false;
					normManage_3.disabled = false;
					normScale1.disabled = false;
					normScale2.disabled = false;
					normScale3.disabled = false;
					assessDays.disabled = false;
					assessHour1.disabled = false;
					assessHour2.disabled = false;
				}
			}
		});

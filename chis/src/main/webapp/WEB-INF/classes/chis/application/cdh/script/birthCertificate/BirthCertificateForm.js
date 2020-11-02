/**
 * 儿童出生医学证明表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.birthCertificate")
$import("chis.script.BizTableFormView", "chis.script.util.Vtype",
		"util.rmi.miniJsonRequestAsync")
chis.application.cdh.script.birthCertificate.BirthCertificateForm = function(
		cfg) {
	cfg.width = 750;
	cfg.height = 260;
	cfg.fldDefaultWidth = 150
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	this.title = cfg.name
	if (!cfg.isCombined) {
		cfg.actions.push({
					id : "cancel",
					name : "取消",
					iconCls : "common_cancel"
				})
	}
	cfg.saveServiceId = "chis.birthCertificateService";
	cfg.saveAction = "saveBirthCertificate"
	cfg.loadServiceId = "chis.birthCertificateService";
	cfg.loadAction = "loadBirthCertificate";
	cfg.initAction = "initBirthData";
	chis.application.cdh.script.birthCertificate.BirthCertificateForm.superclass.constructor
			.apply(this, [cfg])
	this.exist = false;
	this.on("loadNoData", this.onLoadNoData, this) // ** 默认基本信息，暂时注释
	this.on("beforeSave", this.onBeforeSave, this)
	this.on("save", this.onSave, this);
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.cdh.script.birthCertificate.BirthCertificateForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.cdh.script.birthCertificate.BirthCertificateForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var certificateNo = form.findField("certificateNo")
				if (certificateNo) {
					certificateNo.on("change", this.onCertificateNoBlur, this);
				}
				var motherCardNo = form.findField("motherCardNo");
				if (motherCardNo) {
					motherCardNo.on("change", this.onMotherCardNo, this)
					motherCardNo.on("blur", this.onMotherCardNo, this)
					motherCardNo.on("keyup", this.onMotherCardNo, this)
				}
				var fatherCardNo = form.findField("fatherCardNo");
				if (fatherCardNo) {
					fatherCardNo.on("change", this.onFatherCardNo, this)
					fatherCardNo.on("blur", this.onFatherCardNo, this)
					fatherCardNo.on("keyup", this.onFatherCardNo, this)
				}
			},

			onMotherCardNo : function(f) {
				var id = f.getValue();
				if (id == "") {
					return
				}
				var sex = id.slice(14, 17) % 2 ? "1" : "2"
				if (sex == "1") {
					Ext.MessageBox.alert("提示", "性别不符！");
					f.reset();
					f.validate();
				}
			},

			onFatherCardNo : function(f) {
				var id = f.getValue();
				if (!id) {
					return
				}
				var sex = id.slice(14, 17) % 2 ? "1" : "2"
				if (sex == "2") {
					Ext.MessageBox.alert("提示", "性别不符！");
					f.reset();
					f.validate();
				}
			},

			onCertificateNoBlur : function(f) {
				var value = f.getValue();
				if (!value || value == "") {
					return;
				}
				var empiId = this.empiId || this.exContext.ids.empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "checkCNo",
							method : "execute",
							body : {
								certificateNo : value,
								empiId : empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (code == 200) {
								var resBody = json.body;
								if (json.body) {
									this.exist = resBody.isRepeat;
									if (this.exist) {
										Ext.MessageBox.alert("提示信息",
												"出生证明编号重复!", function() {
													f.focus(true, true);
												}, this);
									}
								}
							}
						}, this)
			},

			// ** 默认基本信息，暂时注释
			onLoadNoData : function() {
				var empiId = this.empiId || this.exContext.ids.empiId;
				this.data["empiId"] = empiId;
				if (!this.empiId) {
					this.data["phrId"] = this.exContext.ids["CDH_HealthCard.phrId"]
				}
				if (!empiId) {
					return;
				}
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initAction,
							method : "execute",
							body : {
								"empiId" : empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {
								this.initFormData(body);
							}
						}, this)
			},
			getLoadRequest : function() {
				this.initDataId = null;
				var empiId = this.empiId || this.exContext.ids.empiId;
				return {
					"fieldName" : "empiId",
					"fieldValue" : empiId
				};
			},

			onBeforeSave : function(entryName, op, saveData) {
				if (this.exist) {
					Ext.MessageBox.alert("提示", "出生证号重复，不允许保存！")
					return false;
				}
				
			},

			onSave : function() {
				this.doCancel();
			},

			onWinShow : function() {
				this.win.doLayout();
				this.changeButtonState(false, 0);
				this.exist = false;
				this.loadData();
			}

		});
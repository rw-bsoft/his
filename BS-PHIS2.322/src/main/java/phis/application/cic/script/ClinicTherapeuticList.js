$package("phis.application.cic.script")

$import("phis.script.SimpleList")

$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicTherapeuticList = function(cfg) {
	cfg.modal = true;
	phis.application.cic.script.ClinicTherapeuticList.superclass.constructor.apply(this,
			[cfg])
	this.initCnd = ['eq', ['$', 'QYBZ'], ['d', 1]];
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.ClinicTherapeuticList, phis.script.SimpleList,
		{

			onWinShow : function() {
				this.requestData.cnd = ['eq', ['$', 'QYBZ'], ['d', 1]];
				this.loadData();
			},
			onRenderer : function(value, metaData, r) {
				var QYBZ = r.get("QYBZ");
				var src = (QYBZ == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
				+ "resources/phis/resources/images/" + src + ".png'/>";
			},
			doImport : function() {
				this.onDblClick();
			},
			onDblClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				Ext.Msg.show({
							title : '提示',
							msg : "载入模版将覆盖当前病历内容，确认要载入当前模版吗？",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									this.loadClinicTherapeutic(r);
								}
							},
							scope : this
						});
			},
			loadClinicTherapeutic : function(r) {
				// 载入诊疗方案信息
				var ZLXH = r.get("ZLXH");
				this.mask("诊疗方案载入中...")
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveClinicTherapeutic",
							schema : this.entryName,
							body : {
								ZLXH : ZLXH,
								JZXH : this.exContext.ids.clinicId,
								BRID : this.exContext.ids.brid,
								BRXZ : this.exContext.empiData.BRXZ,
								BRXM : this.exContext.empiData.personName,
								GHKS : this.mainApp.reg_departmentId
							}
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								var isModifyMI = false;
								if(json.body.blxx){
									isModifyMI = true;
								}
								this.opener.setMedicalInfo(json.body.blxx);
								this.opener.initClinicRecord("2" , isModifyMI);
								this.opener.initClinicRecord("3" , isModifyMI);
								this.win.hide();
								if(json.body.errorMsg){
									MyMessageTip.msg("提示", json.body.errorMsg, true);
								}
							} else {
								this.processReturnMsg(code, msg, this.doImport)
							}
						}, this)
			},
			doClose : function() {
				this.win.hide();
			}
		});
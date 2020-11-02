$package("phis.application.cic.script")

$import("phis.script.SimpleList")

$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.MedicalHistoryList = function(cfg) {
	phis.application.cic.script.MedicalHistoryList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.MedicalHistoryList,
		phis.script.SimpleList, {
			doImport : function() {
				this.onDblClick();
			},
			onDblClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				// 载入病历模板信息
				var JLXH = r.get("JLXH");
				this.mask("病历载入中...")
				phis.script.rmi.jsonRequest({
							serviceId : "simpleLoad",
							schema : "phis.application.cic.schemas.GY_BLMB_Y",
							pkey : JLXH
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								var JKCFRecordMap=this.openermodule.medicalDetailList.JKCFRecords;
								var JKCFRecords=[];
								for(var o in JKCFRecordMap){
									var r=JKCFRecordMap[o];
									JKCFRecords.push(r)
								}
								json.body.JKCFRecords = JKCFRecords;
								this.opener.setMedicalInfo(json.body);
								this.openermodule.win.hide();
							} else {
								this.processReturnMsg(code, msg, this.doImport)
							}
						}, this)
			},
			doClose : function() {
				this.openermodule.win.hide();
			}
		});
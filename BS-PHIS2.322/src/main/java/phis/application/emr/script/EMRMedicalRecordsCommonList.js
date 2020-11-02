$package("phis.application.emr.script");

$import("phis.script.SimpleList");

phis.application.emr.script.EMRMedicalRecordsCommonList = function(cfg) {
//	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.emr.script.EMRMedicalRecordsCommonList.superclass.constructor
			.apply(this, [cfg]);
}
var recordIds = new Array();
Ext.extend(phis.application.emr.script.EMRMedicalRecordsCommonList,
		phis.script.SimpleList, {
			onDblClick : function(){
				var record = this.getSelectedRecord();
				this.fireEvent("commit", record);
				this.doCancel();
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}
		})
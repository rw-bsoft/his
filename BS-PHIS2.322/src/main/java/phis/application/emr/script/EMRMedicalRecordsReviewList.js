$package("phis.application.emr.script");

$import("phis.script.SimpleList");

phis.application.emr.script.EMRMedicalRecordsReviewList = function(cfg) {
	// this.showemrRootPage = true
	cfg.disablePagingTbr = true;// 不分页
	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.emr.script.EMRMedicalRecordsReviewList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordsReviewList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				var record = this.getSelectedRecord();
				this.fireEvent("commit", record.data);
				this.doCancel();
			},
			doCommit : function(){
				var record = this.getSelectedRecord();
				this.fireEvent("commit", record.data);
				this.doCancel();
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}
		})
$package("chis.application.inc.script")

$import("chis.script.BizSimpleListView")

chis.application.inc.script.IncompleteRecordList = function(cfg) {
	chis.application.inc.script.IncompleteRecordList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.inc.script.IncompleteRecordList,
		chis.script.BizSimpleListView, {
			doRemove : function() {
				var record = this.getSelectedRecord();
				var manaDoctorId = record.get("manaDoctorId");
				if (manaDoctorId != this.mainApp.uid
						&& this.mainApp.jobId != "system") {
					Ext.Msg.alert("提示","只有对应责任医生本人才能删除记录。");
					return;
				}
				chis.application.inc.script.IncompleteRecordList.superclass.doRemove
						.call(this);
			}

		})
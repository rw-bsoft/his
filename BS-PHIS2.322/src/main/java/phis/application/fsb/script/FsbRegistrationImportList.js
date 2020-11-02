$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbRegistrationImportList = function(cfg) {
	phis.application.fsb.script.FsbRegistrationImportList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.fsb.script.FsbRegistrationImportList, phis.script.SimpleList, {
			doImport : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var re = phis.script.rmi.miniJsonRequestSync({
							serviceId : "jczxManageService",
							serviceAction : "getExistJCBH",
							BRID : r.data.BRID
						});
				if (re.code > 300) {
					Ext.Msg.alert("提示", re.msg);
					return
				}
				if (re.json && re.json.JCBH) {
					r.data.JCBH = re.json.JCBH
				}
				this.opener.doCommitImport(r.data);
				this.doCancel();
			},
			doCancel : function() {
				this.getWin().hide();
			},
			onDblClick : function() {
				this.doImport();
			}
		});
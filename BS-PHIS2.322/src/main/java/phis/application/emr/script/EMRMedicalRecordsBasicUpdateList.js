$package("phis.application.emr.script");

$import("phis.script.SelectList");

phis.application.emr.script.EMRMedicalRecordsBasicUpdateList = function(cfg) {
	// this.showemrRootPage = true
	cfg.disablePagingTbr = true;// 不分页
	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.emr.script.EMRMedicalRecordsBasicUpdateList.superclass.constructor
			.apply(this, [cfg]);
}
var recordIds = new Array();
Ext.extend(phis.application.emr.script.EMRMedicalRecordsBasicUpdateList,
		phis.script.SelectList, {
			expansion : function(cfg) {
				cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
				cfg.sm.on("rowselect", this.rowSelect, this);
				cfg.sm.on("rowdeselect", this.rowdeSelect, this);
				cfg.listeners = {
					"beforeclose" : this.beforeclose,
					scope : this
				}
			},
			rowSelect : function(e, rowIndex, record) {
				if (!this.containsArray(recordIds, record.get("XMID")))
					recordIds.push({id:record.get("XMID"),value:record.get("GXSJ")});
			},
			rowdeSelect : function(e, rowIndex, record) {
				if (this.containsArray(recordIds, record.get("XMID"))) {
					this.RemoveArray(recordIds, record.get("XMID"));
				}
			},
			containsArray : function(array, attachId) {
				for (var i = 0; i < array.length; i++) {
					if (array[i].id == attachId) {
						return true;
						break;
					}
				}
				return false;
			},
			RemoveArray : function(array, attachId) {
				for (var i = 0, n = 0; i < array.length; i++) {
					if (array[i].id != attachId) {
						array[n++] = array[i]
					}
				}
				array.length -= 1;
			},
			beforeclose : function() {
				if (recordIds.length > 0) {
					Ext.Msg.confirm("请确认", "当前存在已勾选未确定的更新数据，是否更新？", function(btn) {
						if (btn == 'yes') {
							this.doCommit();
						}else{
							recordIds = [];// 清空recordIds
						}
					}, this);
				}
			},
			doCommit : function(){
				this.fireEvent("commit", recordIds);
				this.doCancel();
			},
			doCancel : function() {
				var win = this.getWin();
				recordIds = new Array();
				if (win)
					win.hide();
			}
		})
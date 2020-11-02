/**
 * 孕妇多胞胎信息列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizEditorListView")
chis.application.mhc.script.visit.FetalRecordList = function(cfg) {
	chis.application.mhc.script.visit.FetalRecordList.superclass.constructor.apply(this, [cfg]);
	this.disablePagingTbr = true;
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.mhc.script.visit.FetalRecordList, chis.script.BizEditorListView, {

	beforeCellEdit : function(e) {
		var f = e.field
		var record = e.record
		var op = record.get("_opStatus")
		var cm = this.grid.getColumnModel()
		var c = cm.config[e.column]
		var enditor = cm.getCellEditor(e.column)
		var it = c.schemaItem
		var ac = util.Accredit;
		if (op == "create") {
			if (!ac.canCreate(it.acValue)) {
				return false
			}
		} else {
			if (!ac.canUpdate(it.acValue)) {
				return false
			}
		}

		if (it.id == "fetalPosition" || it.id == "fetalPositionFlag") {
			if (this.exContext.args.fetalDisable) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}

		if (it.dic) {
			e.value = {
				key : e.value,
				text : record.get(f + "_text")
			}
		} else {
			e.value = e.value || ""
		}

		if (this
				.fireEvent("beforeCellEdit", it, record, enditor.field, e.value)) {
			return true
		}
	},

	loadData : function() {
		if (this.__actived) {
			return;
		}
		if (!this.exContext.args.visitId) {
			if (this.store) {
				this.store.removeAll();
			}
			this.resetButtons();
		} else {
			this.initCnd = [
					'and',
					["eq", ["$", "visitId"], ["s", this.exContext.args.visitId]],
					["eq", ["$", "saveFlag"], ["s", 'n']]];
			this.requestData.cnd = this.initCnd;
			chis.application.mhc.script.visit.FetalRecordList.superclass.loadData.call(this);
		}
	},

	doSave : function() {
		this.fireEvent("recordSave", this.store, this.getListData());
	},

	doRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		Ext.Msg.show({
					title : '确认删除记录',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processRemove();
						}
					},
					scope : this
				})
	},

	processRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		this.store.remove(r);
	},

	onWinShow : function() {
		this.loadData();
	}

});
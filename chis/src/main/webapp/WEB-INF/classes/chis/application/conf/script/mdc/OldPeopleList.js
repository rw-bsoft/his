$package("chis.application.conf.script.mdc")

$import("chis.script.BizEditorListView", "chis.application.conf.script.SystemConfigListCommon")

chis.application.conf.script.mdc.OldPeopleList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	Ext.apply(cfg, chis.application.conf.script.SystemConfigListCommon);
	chis.application.conf.script.mdc.OldPeopleList.superclass.constructor.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this)
	this.on("beforeCellEdit", this.onBeforeCellEdit, this)
}

Ext.extend(chis.application.conf.script.mdc.OldPeopleList, chis.script.BizEditorListView, {

	onBeforeCellEdit : function(it, record, field, value) {
		if (it.id == "oldPeopleStartAge" || it.id == "oldPeopleEndAge") {
			this.fireEvent("beforeCellEditToModule", it, record, field, value)
		}

	},

	afterCellEdit : function(e) {
		var f = e.field
		var v = e.value
		var record = e.record
		var cm = this.grid.getColumnModel()
		var enditor = cm.getCellEditor(e.column, e.row)
		var c = cm.config[e.column]
		var it = c.schemaItem
		var field = enditor.field
		var value = field.getValue();
		if (it.dic) {
			record.set(f + "_text", field.getRawValue())
		}
		if (it.type == "date") {
			var dt = new Date(v)
			v = dt.format('Y-m-d')
			record.set(f, v)
		}

		if (f == "oldPeopleStartAge" || f == "oldPeopleEndAge") {
			if (v == "") {
				Ext.Msg.alert("提示", "不能允许为空")
				return
			}
			if (isNaN(v)) {
				Ext.Msg.alert("提示", "年龄必须为数字")
				record.set(f, "")
				record.set(f + "_text", "")
				return
			}
			if (!this.oldPeopleAge || this.oldPeopleAge == "") {
				Ext.Msg.alert("提示", "请先输入老年人起始年龄")
				record.set(f, "")
				record.set(f + "_text", "")
				return
			}

			if (v < this.oldPeopleAge) {
				Ext.Msg.alert("提示", "不能小于老年人起始年龄")
				record.set(f, "")
				record.set(f + "_text", "")
				return
			}

			if (this.checkExists(v, record.id)) {
				record.set(f, "")
				record.set(f + "_text", "")
				Ext.Msg.alert("提示", "已经在年龄范围内")
			}
			if (this.checkInterval()) {
				record.set(f, "")
				record.set(f + "_text", "")
				Ext.Msg.alert("提示", "已经在年龄范围内")
			}

			if (record.get("oldPeopleStartAge")
					&& record.get("oldPeopleEndAge")
					&& record.get("oldPeopleStartAge") != ""
					&& record.get("oldPeopleEndAge") != "") {

				if (parseInt(record.get("oldPeopleStartAge")) > parseInt(record
						.get("oldPeopleEndAge"))) {
					Ext.Msg.alert("提示", "结束年龄不能小于起始年龄");
					record.set(f, "")
					record.set(f + "_text", "")
				}
			}
		}

		record.commit();
	},

	checkExists : function(age, rowid) {
		var listCount = this.store.getCount();
		for (var i = 0; i < listCount; i++) {
			var storeItem = this.store.getAt(i);
			if (storeItem.id == rowid) {
				continue;
			}
			if (parseInt(storeItem.data.oldPeopleStartAge) <= parseInt(age)
					&& parseInt(age) <= parseInt(storeItem.data.oldPeopleEndAge)) {
				return true;
			}
		}
		return false;
	},

	checkInterval : function() {
		var listCount = this.store.getCount();
		for (var i = 0; i < listCount; i++) {
			var outStartAge = parseInt(this.store.getAt(i).data.oldPeopleStartAge);
			var outEndAge = parseInt(this.store.getAt(i).data.oldPeopleEndAge);
			for (var j = i + 1; j < listCount; j++) {
				var inStartAge = parseInt(this.store.getAt(j).data.oldPeopleStartAge);
				var inEndAge = parseInt(this.store.getAt(j).data.oldPeopleEndAge);
				if ((inStartAge <= outStartAge && outStartAge <= inEndAge)
						|| (inStartAge <= outEndAge && outEndAge <= inEndAge))
					return true;
			}
		}

		return false;
	},

	getSaveData : function() {
		var data = [];
		for (var i = 0; i < this.store.getCount(); i++) {
			var storeItem = this.store.getAt(i);
			var oldPeopleStartAge = storeItem.data.oldPeopleStartAge
			var oldPeopleEndAge = storeItem.data.oldPeopleEndAge
			var planTypeCode = storeItem.data.planTypeCode

			if (!oldPeopleStartAge || !oldPeopleEndAge
					|| oldPeopleStartAge == "" || oldPeopleEndAge == "") {
				var num = i + 1;
				Ext.Msg.alert("提示", "第" + num + "行起始年龄或结束年龄为空!");
				return false;
			}
			if (!planTypeCode || storeItem.data.planTypeCode == "") {
				var num = i + 1;
				Ext.Msg.alert("提示", "第" + num + "行【计划类型】不能为空!");
				return false;
			}
			data.push(storeItem.data);
		}
		return data;
	},

	doRemove : function() {

		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		Ext.Msg.show({
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

	loadData : function() {
		chis.application.conf.script.mdc.OldPeopleList.superclass.loadData.call(this);
		this.resetButtonsReadOnly();
		if (this.readOnly) {
			this.grid.disable();
		}
	}
})
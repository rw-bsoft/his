$package("chis.application.conf.script.cdh")

$import("chis.script.BizEditorListView", "chis.application.conf.script.SystemConfigListCommon")

chis.application.conf.script.cdh.ChildrenConfigList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	Ext.apply(cfg, chis.application.conf.script.SystemConfigListCommon);
	chis.application.conf.script.cdh.ChildrenConfigList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.conf.script.cdh.ChildrenConfigList, chis.script.BizEditorListView, {

			afterCellEdit : function(e) {
				var f = e.field
				var v = e.value
				var record = e.record
				var cm = this.grid.getColumnModel()
				var enditor = cm.getCellEditor(e.column, e.row)
				var c = cm.config[e.column]
				var it = c.schemaItem
				var field = enditor.field
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					var dt = new Date(v)
					v = dt.format('Y-m-d')
					record.set(f, v)
				}
				if (v != "" && (f == "startMonth" || f == "endMonth")) {
					var isOld = this.checkMonthExists(v, record.id);
					if (isOld) {
						Ext.Msg.alert("提示", "月龄与已有月龄范围重叠");
						record.set(f, "")
						record.set(f + "_text", "")
					}
				}
				if (f == "startMonth" && record.get("startMonth") != ""
						&& record.get("endMonth") != "") {
					if (parseInt(v) > parseInt(record.get("endMonth"))) {
						Ext.Msg.alert("提示", "起始月龄不能大于终止月龄");
						record.set(f, "")
						record.set(f + "_text", "")
					}
				}
				if (f == "endMonth" && record.get("startMonth") != ""
						&& record.get("endMonth") != "") {
					if (parseInt(v) < parseInt(record.get("startMonth"))) {
						Ext.Msg.alert("提示", "终止月龄不能小于起始月龄");
						record.set(f, "")
						record.set(f + "_text", "")
					}
				}
				record.commit();
			},

			checkMonthExists : function(month, rowid) {
				var listCount = this.store.getCount();
				for (var i = 0; i < listCount; i++) {
					var storeItem = this.store.getAt(i);
					if (storeItem.id == rowid) {
						continue;
					}
					if (parseInt(storeItem.data.startMonth) <= parseInt(month)
							&& parseInt(month) <= parseInt(storeItem.data.endMonth)) {
						return true;
					}
				}
				return false;
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除选中记录',
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

			getSaveData : function() {
				var data = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					var storeItem = this.store.getAt(i);
					if (storeItem.data.startMonth == ""
							|| storeItem.data.startMonth == null
							|| storeItem.data.endMonth == ""
							|| storeItem.data.endMonth == null) {
						var num = i + 1;
						Ext.Msg.alert("提示", "第" + num + "行起始月龄或终止月龄为空!");
						return false;
					}
					if (storeItem.data.planTypeCode == ""
							|| storeItem.data.planTypeCode == null) {
						var num = i + 1;
						Ext.Msg.alert("提示", "第" + num + "行【计划类型】不能为空!");
						return false;
					}
					data.push(storeItem.data);
				}
				if (data.length < 1) {
					Ext.Msg.alert("提示", "列表不能为空!");
					return false;
				}
				return data;
			},

			loadData : function() {
				chis.application.conf.script.cdh.ChildrenConfigList.superclass.loadData
						.call(this);
				this.resetButtonsReadOnly();
			}

		
		})
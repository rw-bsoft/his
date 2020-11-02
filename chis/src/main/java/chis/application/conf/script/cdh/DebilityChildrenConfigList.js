$package("chis.application.conf.script.cdh")

$import("chis.script.BizEditorListView", "chis.application.conf.script.SystemConfigListCommon")

chis.application.conf.script.cdh.DebilityChildrenConfigList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	Ext.apply(cfg, chis.application.conf.script.SystemConfigListCommon);
	chis.application.conf.script.cdh.DebilityChildrenConfigList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(chis.application.conf.script.cdh.DebilityChildrenConfigList,
		chis.script.BizEditorListView, {

			loadData : function() {
				chis.application.conf.script.cdh.DebilityChildrenConfigList.superclass.loadData
						.call(this);
				this.resetButtonsReadOnly();
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

			getSaveData : function(flag) {
				var data = [];
				if (flag) {
					if (this.store.data.length == 0) {
						Ext.Msg.alert("提示", "列表不能为空!");
						return false;
					}
					for (var i = 0; i < this.store.data.length; i++) {
						var storeItem = this.store.getAt(i);
						var dr = storeItem.data.debilityReason;
						var pc = storeItem.data.planTypeCode;

						// 体弱儿分类为空判定
						if (!dr) {
							Ext.Msg
									.alert("提示", "第" + (i + 1)
													+ "行[体弱儿分类]不能为空!");
							return false;
						}
						// 计划类型为空判定
						if (!pc) {
							Ext.Msg.alert("提示", "第" + (i + 1) + "行[计划类型]不能为空!");
							return false;
						}

						// 体弱儿分类和计划类型相同记录只能录入一次判断
						for (var m = 0; m < data.length; m++) {
							var odr = data[m].debilityReason;
							if (dr == odr) {
								Ext.Msg.alert("提示", "第" + (m + 1) + "行与第"
												+ (i + 1) + "行[体弱儿分类]重复!");
								return false;
							}
						}

						data.push(storeItem.data);
					}
				}
				return data;
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
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					var dt = new Date(v)
					v = dt.format('Y-m-d')
					record.set(f, v)
				}
				if (f == "debilityReason") {
					this.checkDebilityReasonExists(f, v);
				}
				this.fireEvent("afterCellEdit", it, record, field, v)
			},

			checkDebilityReasonExists : function(f, v) {
				var store = this.store;
				for (var i = 0; i < store.getCount(); i++) {
					var storeItem = store.getAt(i);
					var ov = storeItem.data.debilityReason;
					for (var j = i + 1; j < store.getCount(); j++) {
						var nv = store.getAt(j).data.debilityReason;
						if (ov == nv) {
							Ext.Msg.alert("提示", "第" + (i + 1) + "行与第" + (j + 1)
											+ "行[体弱儿分类]重复!");
							return;
						}
					}
				}
			}
		})
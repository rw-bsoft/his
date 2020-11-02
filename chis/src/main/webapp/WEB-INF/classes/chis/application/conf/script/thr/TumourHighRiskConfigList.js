$package("chis.application.conf.script.thr");

$import("chis.script.BizEditorListView",
		"chis.application.conf.script.SystemConfigListCommon");

chis.application.conf.script.thr.TumourHighRiskConfigList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	Ext.apply(cfg, chis.application.conf.script.SystemConfigListCommon);
	chis.application.conf.script.thr.TumourHighRiskConfigList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.conf.script.thr.TumourHighRiskConfigList,
		chis.script.BizEditorListView, {
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							scope : this,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							}
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
				if (this.store.data.length == 0) {
					Ext.Msg.alert("提示", "列表不能为空!");
					return false;
				}
				for (var i = 0; i < this.store.data.length; i++) {
					var storeItem = this.store.getAt(i);
					var group = storeItem.data.group;
					var tumourType = storeItem.data.tumourType;
					var planTypeCode = storeItem.data.planTypeCode;

					// 随访计划为空判定
					if (!group) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行[随访分组]不能为空!");
						return false;
					}
					if(!tumourType){
						Ext.Msg.alert("提示","第"+(i+1)+"行[高危类别]不能为空!");
						return false;
					}
					// 计划类型为空判定
					if (!planTypeCode) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行[计划类型]不能为空!");
						return false;
					}

					// 随访计划和计划类型相同记录只能录入一次判断
					for (var m = 0; m < data.length; m++) {
						var oGroup = data[m].group;
						var oTumourType = data[m].tumourType;
						if (group == oGroup && tumourType == oTumourType) {
							Ext.Msg.alert("提示", "第" + (m + 1) + "行与第" + (i + 1)
											+ "行随访分类[随访分组与高危类别]数据重复!");
							return false;
						}
					}
					data.push(storeItem.data);
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
				if (f == "group" || f == "tumourType") {
					this.checkInstanceTypeExists(f, v);
				}
				this.fireEvent("afterCellEdit", it, record, field, v)
			},

			checkInstanceTypeExists : function(f, v) {
				var store = this.store;
				for (var i = 0; i < store.getCount(); i++) {
					var storeItem = store.getAt(i);
					var og = storeItem.data.group;
					var ot = storeItem.data.tumourType;
					for (var j = i + 1; j < store.getCount(); j++) {
						var ng = store.getAt(j).data.group;
						var nt = store.getAt(j).data.tumourType;
						if (og == ng && ot == nt) {
							Ext.Msg.alert("提示", "第" + (i + 1) + "行与第" + (j + 1)
											+ "行随访分类[随访分组与高危类别]重复!");
							return;
						}
					}
				}
			},

			loadData : function() {
				chis.application.conf.script.thr.TumourHighRiskConfigList.superclass.loadData.call(this);
				this.resetButtonsReadOnly();
			}
		});
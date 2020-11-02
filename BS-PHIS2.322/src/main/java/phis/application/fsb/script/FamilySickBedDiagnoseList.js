$package("phis.application.fsb.script")

$import("phis.script.EditorList")

phis.application.fsb.script.FamilySickBedDiagnoseList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	cfg.remoteUrl = "Disease";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	this.serviceId = "familySickBedManageService";
	phis.application.fsb.script.FamilySickBedDiagnoseList.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	//this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}

Ext.extend(phis.application.fsb.script.FamilySickBedDiagnoseList,
		phis.script.EditorList, {
			onWinShow : function() {
				this.loadData()
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'JBMC'

								}, {
									name : 'ICD10'

								}, {
									name : 'JBPB'

								}, {
									name : 'JBPB_text'

								}]);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (i != row) {
						if (r.get("ZDXH") == record.get("JBXH")
								&& r.get("ZDLB") == rowItem.get("ZDLB")) {
							MyMessageTip.msg("提示", "\"" + record.get("JBMC")
											+ "\"已存在，请勿重复录入！", true);
							return;
						}
					}
				}
				rowItem.set('ZDMC', record.get("JBMC"));
				rowItem.set('ZDXH', record.get("JBXH"));
				rowItem.set('ICD10', record.get("ICD10"));
				rowItem.set('ZDSJ', Date.getServerDateTime());
				obj.setValue(record.get("JBMC"));
				obj.collapse();
				obj.triggerBlur()
				this.grid.startEditing(row, 4);
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("ZDXH") == null || r.get("ZDXH") == ""
							|| r.get("ZDXH") == 0) {
						store.remove(r);
					}
				}
			},
			onBeforeCellEdit : function(it, record, field) {
				if (record.get("CCXH") && this.CCXH != record.get("CCXH"))
					return false;
			},
			doSave : function() {
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
				this.removeEmptyRecord();
				this.mask("数据保存中...");
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = [];
				if (n == 0) {
					MyMessageTip.msg('提示', '当前没有需要保存的数据!', true);
					this.unmask();
					return false;
				}
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("CCXH") && this.CCXH != r.get("CCXH")) {
						continue;
					}
					var items = this.schema.items
					for (var j = 0; j < items.length; j++) {
						var it = items[j]
						if (it['not-null'] && r.get(it.id) == "") {
							return
						}
					}
					r.set("ZYH", this.ZYH);
					r.set("CCXH", this.CCXH);
					r.set("BRID", this.exContext.brxx.get("BRID"));
					data.push(r.data)
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveDiagnossis",
							body : data
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return false;
							}
							this.win.hide();
							this.fireEvent("doRefresh");
						}, this);
			},
			/**
			 * 重写doRemove，当grid中的数据未保存在数据库时，直接从grid中删除，若删除的数据 已保存，则发起请求删除数据库中数据
			 */
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("JLBH") && this.CCXH != r.get("CCXH")) {
					MyMessageTip.msg("提示", "不能操作非本次查床记录的诊断信息!", true);
					return;
				}
				if (r.get("JLBH") == null || !r.get("ZDXH")) {
					this.store.remove(r);
				} else {
					Ext.Msg.show({
								title : '确认删除记录[' + r.data.ZDMC + ']',
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										if (r.data.JLBH) {
											this.processRemove();
										}
									}
								},
								scope : this
							})

				}
			},
			doClose : function() {
				this.win.hide();
			}
		});
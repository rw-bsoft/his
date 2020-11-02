$package("phis.application.war.script");
$import("phis.script.EditorList");

phis.application.war.script.WardOutPharmacySet = function(cfg) {
	phis.application.war.script.WardOutPharmacySet.superclass.constructor.apply(this,
			[cfg]);

	this.on("afterCellEdit", this.afterMyCellEdit, this);
}
Ext.extend(phis.application.war.script.WardOutPharmacySet, phis.script.EditorList, {
			initPanel:function(sc){
				// 判断是否有病区
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				var grid = phis.script.EditorList.superclass.initPanel.call(this,sc)
				grid.on("afteredit",this.afterCellEdit,this)
				grid.on("beforeedit",this.beforeCellEdit,this)
				grid.on("doNewColumn",this.doInsertAfter,this)
				return grid
			},
			// 判断医嘱类型和药品类型不能重复 begin
			onReady : function() {
				phis.script.EditorList.superclass.onReady.call(this);
				var type = this.grid.colModel.getColumnById("TYPE").editor;
				type.on("beforeselect", this.beforeTypeSelect, this);
				var dmsb = this.grid.colModel.getColumnById("DMSB").editor;
				dmsb.on("beforeselect", this.beforeDmsbSelect, this);
			},
			beforeTypeSelect : function(f, record, index) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRecord = this.getSelectedRecord();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var r = this.store.getAt(i);
						if (r.get("TYPE") == record.get("key")
								&& curRecord.get("DMSB") == r.get("DMSB")) {
							var row = i+1;
							MyMessageTip.msg("提示", "本条医嘱类型下的药品类型和第"+row+"重复！", true);
							return false;
						}
					}
				}
				return true;
			},
			beforeDmsbSelect : function(f, record, index) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRecord = this.getSelectedRecord();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var r = this.store.getAt(i);
						if (r.get("DMSB") == record.get("key")
								&& curRecord.get("TYPE") == r.get("TYPE")) {
							var row = i+1;
							MyMessageTip.msg("提示", "本条医嘱类型下的药品类型和第"+row+"重复！", true);
							return false;
						}
					}
				}
				return true;
			}, // end
			// 自动新增行
			doCreate : function(item, e) {
				phis.application.war.script.WardOutPharmacySet.superclass.doCreate.call(
						this, item, e)
				var rowItem = this.store.getAt(this.store.getCount() - 1);
				if (rowItem) {
					// 设置默认病区为当前病区
					rowItem.set("BQDM", this.mainApp['phis'].wardId);
					rowItem.set("BQDM_text", this.mainApp['phis'].wardName);
				}
			},
			/*编辑后失去焦点后判断
			 * afterMyCellEdit : function(it, record, field, v) {
				if (it.id == "TYPE" || it.id == "DMSB") {
					var store = this.grid.getStore();
					var n = store.getCount();

				}
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
				}
			},*/
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("BQDM") == null || r.get("BQDM") == ""
							|| r.get("BQDM") == 0) {
						store.remove(r);
					}
				}
			},
			doCommit : function() {
				this.mask();
				var body = [];
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					body.push(r.data);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardOutPharmacySetService",
							serviceAction : "saveCommit",
							body : body
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				this.unmask();
				if (code < 300) {
					MyMessageTip.msg("提示", "保存成功!", true);
					this.refresh();
					return true;
				} else {
					this.processReturnMsg(code, msg);
					return false;
				}

			},
			doUpdateStage : function() {
				var r = this.getSelectedRecord();
				if(!r){
					alert("请选中行！");
					return;
				}
				if (!r.get("JLXH") && r.get("JLXH") != 0) {
					alert("请先保存数据");
					return;
				}
				var body = {};
				body["JLXH"] = r.get("JLXH");
				if (r.get("ZXPB") == "1") {
					body["ZXPB"] = "0";
					this.grid.el.mask("正在取消启用...", "x-mask-loading")
				} else {
					body["ZXPB"] = "1";
					this.grid.el.mask("正在启用...", "x-mask-loading")
				}
				phis.script.rmi.jsonRequest({
							serviceId : "wardOutPharmacySetService",
							serviceAction : "updateStage",
							body : body
						}, function(code, msg, json) {
							this.grid.el.unmask();
							if (code >= 200) {
								MyMessageTip.msg("提示", "操作成功", true);
								var btns = this.grid.getTopToolbar();
								var btn = btns.find("cmd", "updateStage");
								btn = btn[0];
								if (r.data.ZXPB == 0) {
									if (btn.getText().indexOf("启用") > -1) {
										return;
									}
									btn.setText(btn.getText().replace("注销",
											"启用"));
								} else {
									btn.setText(btn.getText().replace("启用",
											"注销"));
								}
							}
						}, this);
				this.refresh();
				/*
				 * Ext.Msg.show({ title : '消息', msg : '注销操作操作将无法使用，是否继续?', modal :
				 * true, width : 300, buttons : Ext.MessageBox.OKCANCEL,
				 * multiline : false, fn : function(btn, text) { if (btn ==
				 * "ok") { var body = {}; body["JLXH"]= r.get("JLXH"); if
				 * (r.get("ZXPB") == "1") { body["ZXPB"] = "0"; } else {
				 * body["ZXPB"] = "1"; } phis.script.rmi.jsonRequest({ serviceId :
				 * "wardOutPharmacySetService", serviceAction : "updateStage",
				 * body : body },function(code, msg, json){ if (code >= 200) {
				 * MyMessageTip.msg("提示", msg, true); var btns =
				 * this.grid.getTopToolbar(); var btn = btns.find("cmd",
				 * "updateStage"); btn = btn[0]; if (r.data.ZXPB == 0) { if
				 * (btn.getText().indexOf("启用") > -1) { return; }
				 * btn.setText(btn.getText().replace("注销","启用")); } else {
				 * btn.setText(btn.getText().replace("启用","注销")); } } },this);
				 * this.refresh(); } }, scope : this })
				 */

			}, // 单击时改变注销按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "updateStage");
				btn = btn[0];
				if (r.data.ZXPB == 0) {
					if (btn.getText().indexOf("注销") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("启用", "注销"));
				} else {
					btn.setText(btn.getText().replace("注销", "启用"));
				}

			},
			doRemoveCell : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				if (r.get("JLXH") || r.get("JLXH") == 0) {
					return;
				}
				this.store.remove(r);
				// 移除之后焦点定位
				var count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
			}
		});
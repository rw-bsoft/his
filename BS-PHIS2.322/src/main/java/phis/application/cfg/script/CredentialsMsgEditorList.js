$package("phis.application.cfg.script");
$import("phis.script.EditorList");

phis.application.cfg.script.CredentialsMsgEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.entryName = "phis.application.cfg.schemas.WL_ZJXX";
	phis.application.cfg.script.CredentialsMsgEditorList.superclass.constructor
			.apply(this, [ cfg ])
	// this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext
		.extend(
				phis.application.cfg.script.CredentialsMsgEditorList,
				phis.script.EditorList,
				{
					/*
					 * afterCellEdit : function(e) { var f = e.field var v =
					 * e.value var old = e.originalValue;// 编辑前的值 var record =
					 * e.record var reg = /^[0-9]{1,3}\.{0,1}[0-9]{0,1}$/; if
					 * (!reg.test(v) || !(0 <= parseFloat(v) <= 100)) {
					 * record.set("ZFBL", old); } }
					 */
					// 页面显示图片状态的图标
					onRenderer : function(value, metaData, r) {
						var TPXX = r.get("TPXX");
						var src = (TPXX != null && TPXX != "") ? "yes" : "no";
						return "<img src='" + ClassLoader.appRootOffsetPath
								+ "resources/phis/resources/images/" + src
								+ ".png'/>";
					},
					doUploadImage : function() {
						if (this.getSelectedRecord() == null) {
							Ext.Msg.alert("提示", "请选择证件");
						}
						var ZJXH = this.getSelectedRecord().id;
						var TPXX = this.getSelectedRecord().get("TPXX");
						this.editPhotoModule = this.createModule(
								"editPhotoModule",
								"phis.application.cfg.CFG/CFG/CFG5301040101");

						this.editPhotoModule.ZJXH = ZJXH;
						this.editPhotoModule.TPXX = TPXX;
						this.editPhotoModule.on("doSave", this.onSavePhoto,
								this);
						this.editPhotoModule.initPanel();
						// 重新加载form表单中的数据
						// this.editPhotoModule.doNew();
						var win = this.editPhotoModule.getWin();
						this.editPhotoModule.win = win;
						win.add(this.editPhotoModule.initPanel());
						win.show();
						win.center();
					},
					onSavePhoto : function(value) {
						var r = this.getSelectedRecord();
						r.set("TPXX", value);
						this.editPhotoModule.doCancel();
					},
					doRemoveImage : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							Ext.Msg.alert("提示", "请选择证件");
							return;
						}
						if (isNaN(r.id)) {
							Ext.Msg.alert("提示", "该证件没有保存");
							return;
						}
						if (r.get("TPXX") == null || r.get("TPXX") == "") {
							Ext.Msg.alert("提示", "没有要删除的图片");
							return;
						}
						var data = {};
						data["ZJXH"] = r.id;
						data["TPXX"] = r.get("TPXX");
						this.grid.el.mask("正在删除图片...", "x-mask-loading")
						phis.script.rmi.jsonRequest({
							serviceId : "configSupplyUnitService",
							serviceAction : "removePhoto",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								MyMessageTip.msg("提示", "操作成功", true);
								this.refresh();
							}
						}, this)
					},
					initPanel : function(sc) {
						var grid = phis.application.cfg.script.CredentialsMsgEditorList.superclass.initPanel
								.call(this,

								sc)
						grid.onEditorKey = function(field, e) {
							if (field.needFocus) {
								field.needFocus = false;
								ed = this.activeEditor;
								if (!ed) {
									ed = this.lastActiveEditor;
								}
								this.startEditing(ed.row, ed.col);
								return;
							}
							if (e.getKey() == e.ENTER && !e.shiftKey) {
								var sm = this.getSelectionModel();
								var cell = sm.getSelectedCell();
								var count = this.colModel.getColumnCount()
								if (cell[1] + 1 >= (count - 1) && !this.editing) {
									return;
								}
							}
							this.selModel.onEditorKey(field, e);
						}
						var sm = grid.getSelectionModel();
						sm.onEditorKey = function(field, e) {
							var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
							if (k == e.ENTER) {
								e.stopEvent();
								if (!ed) {
									ed = g.lastActiveEditor;
								}
								ed.completeEdit();
								if (e.shiftKey) {
									newCell = g.walkCells(ed.row, ed.col - 1,
											-1, sm.acceptsNav, sm);
								} else {
									newCell = g.walkCells(ed.row, ed.col + 1,
											1, sm.acceptsNav, sm);
								}

							} else if (k == e.TAB) {
								e.stopEvent();
								ed.completeEdit();
								if (e.shiftKey) {
									newCell = g.walkCells(ed.row, ed.col - 1,
											-1, sm.acceptsNav, sm);
								} else {
									newCell = g.walkCells(ed.row, ed.col + 1,
											1, sm.acceptsNav, sm);
								}
							} else if (k == e.ESC) {
								ed.cancelEdit();
							}
							if (newCell) {
								r = newCell[0];
								c = newCell[1];
								this.select(r, c);
								if (g.isEditor && !g.editing) {
									ae = g.activeEditor;
									if (ae && ae.field.triggerBlur) {
										ae.field.triggerBlur();
									}
									g.startEditing(r, c);
								}
							}

						};
						return grid
					}
				})

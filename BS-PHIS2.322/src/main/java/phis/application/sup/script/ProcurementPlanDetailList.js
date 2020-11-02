$package("phis.application.sup.script")

$import("phis.script.EditorList")
phis.application.sup.script.ProcurementPlanDetailList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.minListWidth = 360;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = "ProcurementPlan";
	cfg.remoteTpl = cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td>'
			+ '<td width="140px">{WZMC}</td><td width="200px">{CJMC}</td>';
	phis.application.sup.script.ProcurementPlanDetailList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.ProcurementPlanDetailList,
				phis.script.EditorList,
				{
					initPanel : function(sc) {
						var grid = phis.application.sup.script.ProcurementPlanDetailList.superclass.initPanel
								.call(this, sc)
						var sm = grid.getSelectionModel();
						var _ctr = this;
						grid.onEditorKey = function(field, e) {
							var sm = this.getSelectionModel();
							var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
							if (e.getKey() == e.ENTER && !e.shiftKey) {
								var cell = sm.getSelectedCell();
								var count = this.colModel.getColumnCount()
								if (cell[1] + 8 > count) {
									_ctr.doCreate();
									return;
								}
							}

							this.selModel.onEditorKey(field, e);
						}
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
					},
					loadDatas : function(records) {
						this.clear();
						this.requestData.serviceId = "phis.procurementPlanService";
						this.requestData.serviceAction = "planImportQuery";
						this.requestData.cndw = records;

						if (this.store) {
							if (this.disablePagingTbr) {
								this.store.load()
							} else {
								var pt = this.grid.getBottomToolbar()
								if (this.requestData.pageNo == 1) {
									pt.cursor = 0;
								}
								pt.doLoad(pt.cursor)
							}
						}
						this.resetButtons();

					},
					doCreate : function(item, e) {
						phis.application.sup.script.ProcurementPlanDetailList.superclass.doCreate
								.call(this);
						var store = this.grid.getStore();
						var n = store.getCount() - 1
						this.grid.startEditing(n, 1);
					},
					getRemoteDicReader : function() {
						return new Ext.data.JsonReader({
							root : 'mats',
							totalProperty : 'count',
							id : 'mtssearch'
						}, [ {
							name : 'numKey'
						}, {
							name : 'WZXH'
						}, {
							name : 'WZMC'
						}, {
							name : 'WZGG'
						}, {
							name : 'WZDW'
						}, {
							name : 'GLFS'
						}, {
							name : 'CJXH'
						}, {
							name : 'CJMC'
						}, {
							name : 'WZJG'
						}, {
							name : 'KCXH'
						}, {
							name : 'KSDM'
						}, {
							name : 'KSMC'
						} ]);
					},
					setBackInfo : function(obj, record) {
						obj.collapse();
						var cell = this.grid.getSelectionModel()
								.getSelectedCell();
						var row = cell[0];
						var col = cell[1];
						var griddata = this.grid.store.data;
						var wzxh = record.get("WZXH");
						for ( var i = 0; i < griddata.length; i++) {
							if (griddata.itemAt(i).get("WZXH") == wzxh
									&& i != row) {
								MyMessageTip.msg("提示", "该物资已存在,请修改此物资", true);
								return;
							}
						}
						_ctx = this;
						var rowItem = griddata.itemAt(row);
						rowItem.set('WZXH', record.get("WZXH"));
						rowItem.set('WZMC', record.get("WZMC"));
						if (record.get("WZGG") && record.get("WZGG") != "null") {
							rowItem.set('WZGG', record.get("WZGG"));
						}
						if (record.get("WZDW") && record.get("WZDW") != "null") {
							rowItem.set('WZDW', record.get("WZDW"));
						}
						rowItem.set('CJXH', record.get("CJXH"));

						rowItem.set('KSDM', record.get("KSDM"));
						rowItem.set('KSDM_text', record.get("KSMC"));
						rowItem.set('SLSJ', new Date().format("Y-m-d"));
						obj.setValue(record.get("WZMC"));
						obj.triggerBlur();
						this.grid.startEditing(row, 4);
						this.remoteDic.lastQuery = "";
					}

				})
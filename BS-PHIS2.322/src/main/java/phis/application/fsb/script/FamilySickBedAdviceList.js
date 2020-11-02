$package("phis.application.fsb.script")

$import("phis.script.EditorList", "phis.script.widgets.DatetimeField",
		"org.ext.ux.CheckColumn", "phis.script.util.asynLoop")
phis.application.fsb.script.FamilySickBedAdviceList = function(cfg) {
	cfg.remoteUrl = 'MedicineClinicForFSB';
	cfg.minListWidth = 510;
	cfg.remoteTpl = this.getRemoteTpl();
	cfg.autoLoadData = false;
	cfg.listServiceId = "familySickBedAdviceQuery";
	cfg.selectOnFocus = true;
	cfg.disablePagingTbr = true;
	cfg.sortable = false;
	cfg.enableHdMenu = false;
	cfg.showButtonOnTop = false;
	cfg.showRowNumber = false;
	this.removeRecords = [];
	this.serviceId = "familySickBedManageService";
	// Ext.apply(this,phis.script.yb.YbUtil);
	phis.application.fsb.script.FamilySickBedAdviceList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	this.on("afterCellEdit", this.afterGridEdit, this);
	this.on("loadData", this.myLoadData, this);
	// this.on("beforeSearchQuery",this.beforeSearchQuery,this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedAdviceList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.fsb.script.FamilySickBedAdviceList.superclass.initPanel
						.call(this, sc)
				var _ctx = this;
				grid.store.ctx = this
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
					this.selModel.onEditorKey(field, e);
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						// 判断是否项目
						var r = this.store.getAt(cell[0]);
						if (_ctx.openBy == 'nurse') {
							count = 14;
						}
						if (cell[1] + 1 >= count && !this.editing) {
							this.fireEvent("doNewColumn");
							return;
						}
					}
				}
				var sm = grid.getSelectionModel();
				// 重写onEditorKey方法，实现Enter键导航功能
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						e.stopEvent();
						if (!ed) {
							ed = g.lastActiveEditor;
						}
						if (field.notNull && !field.value) {
							g.startEditing(ed.row, ed.col);
							return;
						}
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							var yplx = g.getStore().getAt(ed.row).get("YPLX");
							if (ed.fieldName == "SYPC") {
								if (yplx == 0) {
									if (_ctx.adviceType == 'longtime') {
										ed.col = g.getColumnModel()
												.getIndexById("YYTS")
												- 1;
									} else {
										ed.col = g.getColumnModel()
												.getIndexById("YCSL")
												- 1;
									}
								} else {

								}
							}
							if (ed.fieldName == "YCSL") {
								if (yplx == 0) {
									if (!g.getStore().getAt(ed.row).get("YPDJ")) {
										ed.col = g.getColumnModel()
												.getIndexById("YPDJ")
												- 1;
									} else {
										ed.col = 16;// 新增
									}
								}

							}
							if (ed.fieldName == "YPYF") {
								ed.col = 16;
							}

							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}

					} else if (k == e.TAB) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}
					} else if (k == e.ESC) {
						ed.cancelEdit();
					}
					if (newCell) {
						r = newCell[0];
						c = newCell[1];
						// 特殊处理
						if (c == 5 && g.getStore().getAt(r).get("YPLX") == 0) {
							c = g.getStore().getAt(r).get("JFBZ") == 3 ? 14 : 8
						}
						this.select(r, c);
						if (ed.col == 15 || ed.col == 16) {
							g.fireEvent("doNewColumn");
							return;
						}
						if (g.isEditor && !g.editing) {
							ae = g.activeEditor;
							if (ae && ae.field.triggerBlur) {
								ae.field.triggerBlur();
							}
							g.startEditing(r, c);
						}
					}
				};
				var sypc = grid.getColumnModel().getColumnById("SYPC").editor;
				sypc.on("select", this.sypcSelect, this);
				var ypyf = grid.getColumnModel().getColumnById("YPYF").editor;
				ypyf.on("select", this.ypyfSelect, this);
				return grid
			},
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				if (!r.get("JLXH")) {
					MyMessageTip.msg("提示", "新医嘱没有计费信息!", true);
					return;
				}
				this.opener.showYzjf(r.get("JLXH"));
			},
			showTooltip : function(msg) {
				var t = this.tooltip;
				if (!t) {
					t = this.tooltip = new Ext.ToolTip({
								maxWidth : 600,
								cls : 'errorTip',
								width : 300,
								title : "帖数",
								autoHide : false,
								anchor : 'left',
								anchorToTarget : true,
								mouseOffset : [40, 0]
							});
				}
				t.initTarget(this.grid.lastVisibleColumn().getEl());
				t.body.update(msg);
				t.doAutoWidth(20);
				t.show();
			},
			onRowClick : function(grid, index, e) {

			},
			getRowClass : function(record, rowIndex, rowParams, store) {
				if (record.get("TJZX") == 1
						&& (record.get("QRSJ") && record.get("QRSJ").length > 0)) {
					return "x-grid-record-smoke";
				}
				if (record.get("TJZX") == 1) {
					return "x-grid-record-gray";
				}
				if ((record.get("QRSJ") && record.get("QRSJ").length > 0)) {
					return "x-grid-record-smoke";
				}
				// if (store.ctx.openBy != 'nurse' && record.get("CCJL")) { //
				// 查床记录对应显示颜色
				// if (store.ctx.opener.ccxh == record.get("CCJL")) {
				// return "x-grid-record-pink";
				// }
				// }
				return ""
			},
			sypcSelect : function(f, record, index) {
				// 根据频次设置MRCS和YZZXSJ
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("SYPC") == record.get("key"))
					return;
				r.set("MRCS", record.get("MRCS"));
				if (r.get("YPLX") > 0) {
					r.set("SRCS", record.get("MRCS"));
				} else {
					r.set("SRCS", "0");
				}
				r.set("YZZXSJ", record.get("ZXSJ"));
			},
			sypcChange : function(f, record, oldVal) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				r.set("MRCS", record.get("MRCS"));
				if (r.get("YPLX") > 0) {
					r.set("SRCS", record.get("MRCS"));
				} else {
					r.set("SRCS", "0");
				}
				r.set("YZZXSJ", record.get("ZXSJ"));
			},
			ypyfSelect : function(f, record, index, r) {
				if (!r) {
					r = this.getSelectedRecord()
					if (r == null) {
						return
					}
				}
				if (index != -1
						&& (record && r.get("YPYF") == record.get("key")))
					return;
			},
			beforeGridEdit : function(it, record, field, value) {
				if (this.exContext.readOnly) {
					return false;
				}
				// 护士站不能操作医生的医嘱
				if (this.openBy == "nurse" && record.get("YSBZ") == 1) {
					return false;
				}
				// 医生站非本医生医嘱不能编辑
				if (this.openBy != "nurse"
						&& record.get("YSGH") != this.mainApp.uid) {
					return false;
				}
				if (record.get("YWID")) {
					return false;
				}
				if (record.get("YSTJ") == 1 || record.get("FHBZ") == 1) {// 已经提交或者复核的数据不能编辑
					if (it.id != "TZYS" && it.id != "TZSJ")
						return false;
				}
				if (record.get("TJZX") == 1
						|| (record.get("QRSJ") && record.get("QRSJ").length > 0)) {
					if ((it.id == "TZSJ" || it.id == "TZYS")
							&& record.get("TZSJ")) {
						return true;
					}
					return false;// 非新医嘱
				}
				if (it.id == "YCJL" || it.id == "YPYF" || it.id == "YPCD"
						|| it.id == "YFSB" || it.id == "FYFS"
						|| it.id == "SRCS") {
					if (record.get("YPLX") < 1) {
						return false;
					}
				}
				// 行为医嘱
				if (it.id == "YCSL" || it.id == "YPDJ" || it.id == "SYPC"
						|| it.id == "SRCS") {
					if (record.get("JFBZ") == 3) {
						return false;
					}
				}
				if (it.id == "YPDJ" && value > 0) {
					return false;
				}

				if ((it.id == "TZYS" || it.id == "TZSJ")) {
					if (!record.get("TZSJ")) {
						return false;
					} else {
						if (it.id == "TZSJ") {
							// if (record.get("QRSJ")) {
							// field.setMinValue(Date.parseDate(record.get("QRSJ"),
							// "Y-m-d H:i:s"))
							// } else
							if (record.get("KSSJ")) {
								field.setMinValue(Date.parseDate(record
												.get("KSSJ"), "Y-m-d H:i:s"))
							}
						}
						return true;
					}
				}

				if (!record.get("YPXH") && it.id != "KSSJ" && it.id != "YZMC"
						&& it.id != "YYTS") {
					return false;
				}
				if (this.openBy != "nurse"
						&& (it.id == 'YSGH' || it.id == 'TZYS')) {
					return false;
				}
				// 已经确认或者提交的医嘱，插入的同组记录不能修改同组信息
				if (it.id == "SYPC" || it.id == "YPYF" || it.id == "SRCS") {
					for (var i = 0; i < this.store.getCount(); i++) {
						var r = this.store.getAt(i);
						if (r.get("YZZH_SHOW") == record.get("YZZH_SHOW")
								&& (r.get("TJZX") == 1 || (r.get("QRSJ") && r
										.get("QRSJ").length > 0))) {
							var cell = this.grid.getSelectionModel()
									.getSelectedCell();
							this.grid.startEditing(cell[0], 13);
							return false;
						}
					}
				}
				if (record.get("YPLX") == 3) {
					if (it.id == "KSSJ" || it.id == "YSGH" || it.id == "FYFS"
							|| it.id == "BZXX" || it.id == "YFSB") {
						return true;
					}
					return false;
				}
				return true;
			},
			afterGridEdit : function(it, record, field, v) {
				// 一组医嘱的频次，执行时间，途径，首日次数，开嘱医生必需一致
				if (it.id == 'SYPC' || it.id == 'YZZXSJ' || it.id == 'YPYF'
						|| it.id == 'SRCS' || it.id == 'YSGH'
						|| it.id == "YYTS" || it.id == 'KSSJ') {
					if (it.id == 'SRCS' && v > record.get("MRCS")) {
						MyMessageTip.msg("提示", "首日次数不能超过频次的每日次数"
										+ record.get("MRCS") + "!", true)
						var cell = this.grid.getSelectionModel()
								.getSelectedCell();
						this.grid.startEditing(cell[0], 10);
						return;
					}
					var store = this.grid.store;
					store.each(function(r) {
								if (r.get('YZZH_SHOW') == record
										.get('YZZH_SHOW')) {// 同组信息
									r.set(it.id, v);
									if (it.id == "YSGH" || it.id == 'YPYF'
											|| it.id == "SYPC") {
										r.set(it.id + '_text', record.get(it.id
														+ '_text'));
									}
									if (it.id == 'SYPC') {
										var sypc = this.grid.getColumnModel()
												.getColumnById("SYPC").editor;
										var sypc_rec = sypc
												.findRecord("key", v);
										record
												.set("MRCS", sypc_rec
																.get("MRCS"));
										record.set("YZZXSJ", sypc_rec
														.get("ZXSJ"));
										if (r.get("YPLX") > 0) {
											r.set("SRCS", record.get("MRCS"));
										} else {
											r.set("SRCS", "0");
										}
										r.set("MRCS", record.get("MRCS"));
										r.set("SRCS", record.get("SRCS"));
										r.set("YZZXSJ", record.get("YZZXSJ"));
										this.setMedQuantity(r);
									}
									if (it.id == "YYTS" || it.id == 'KSSJ') {
										this.setSyncGroup(r);
									}
								}
							}, this);
				}
				if (it.id == "YZMC") {
					if (v.substr(0, 1) == '*' || record.get("JFBZ") == 3) {
						var yzmc = v.substr(0, 1) == '*' ? v.substr(1) : v;
						record.set("YZMC", yzmc)
						record.set("YPLX", 0);
						record.set("JFBZ", 3);
						record.set("XMLX", 1);
						record.set("BZXX", "(不计费医嘱)");
						record.modified[it.id] = yzmc;
					}
				}
				if (it.id == "YCJL" && record.get("YPJL")) {
					record
							.set(
									"YCSL",
									parseFloat(v
											/ (parseFloat(record.get("YPJL")) * parseFloat(record
													.get("YFBZ")))).toFixed(2));
					this.setMedQuantity(record);
				}
				if (it.id == "YCSL" || it.id == "KSSJ" || it.id == "YYTS") {// KSSJ变化对抗菌药物的判断有影响
					// YYTS(出院带药)
					if (it.id == "YYTS" || it.id == "KSSJ") {
						if (record.get("YYTS") > 0 && record.get("KSSJ")) {// 计算提交至日期
							if (record.get("YTJZRQ")) {// 已经提交过
								record.set("TJZRQ", Date.parseDate(
												record.get("KSSJ"), 'Y-m-d H:i:s')
												.add(Date.DAY,
														record.get("YYTS"))
												.format('Y-m-d'));
							} else {
								record.set("TJZRQ", Date.parseDate(
												record.get("KSSJ"),
												'Y-m-d H:i:s').add(Date.DAY,
												record.get("YYTS"))
												.format('Y-m-d'));
							}
						}
					}
					this.setMedQuantity(record, it.id);
					// this.checkInventory(record);
				}
				if (it.id == "TZSJ") {
					if (!v) {
						record.set("TZYS", "");
						record.set("TZYS_text", "");
					}
				}
			},
			setSyncGroup : function(record) {
				if (record.get("YYTS") > 0 && record.get("KSSJ")) {// 计算提交至日期
					if (record.get("YTJZRQ")) {// 已经提交过
						record.set("TJZRQ", Date.parseDate(
										record.get("KSSJ"), 'Y-m-d H:i:s').add(
										Date.DAY, record.get("YYTS"))
										.format('Y-m-d'));
					} else {
						record.set("TJZRQ", Date.parseDate(record.get("KSSJ"),
										'Y-m-d H:i:s').add(Date.DAY,
										record.get("YYTS")).format('Y-m-d'));
					}
					this.setMedQuantity(record);
				}
			},
			dateFormat : function(value, params, r, row, col, store) {
				if (row > 0
						&& col < 5
						&& store.getAt(row - 1).get("YZZH_SHOW") == r
								.get("YZZH_SHOW")) {
					return "";
				}
				// if (store.ctx.openBy != 'nurse' && r.get("CCJL")) { //
				// 查床记录对应显示颜色
				// if (store.ctx.opener.ccxh != r.get("CCJL")) {
				// var tips = "开具：" + r.get("KSSJ") + " "
				// + r.get("YSGH_text")
				// if (r.get("TZSJ")) {
				// tips += "<br>停止：" + r.get("TZSJ") + " "
				// + r.get("TZYS_text");
				// }
				// tips = '<div class=\'ext-cell-tip\'>' + tips + '</div>';
				// params.attr = 'ext:qtip="' + tips + '"';
				// //params.css = "x-grid-record-pink";
				// }
				// }
				// store.ctx.opener.ccxh
				// 'ext:qtitle="提示标题"' +background-color : #78FFFF;

				if (!value)
					return;
				return Ext.util.Format.date(Date
								.parseDate(value, "Y-m-d H:i:s"), 'Y.m.d H:i')
			},
			getRemoteTpl : function() {
				return '<tpl if="YPLX &gt; 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="160px" title="{YPMC}">({YBFL_text}){YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td><td width="50px">{KCSL}</td></tpl>'
						+ '<tpl if="YPLX == 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="250px" title="{FYMC}">{FYMC}</td><td width="80px">{FYDW}</td></tpl>';
			},
			YstjRender : function(v, params, r) {
				// params.style = "text-align:left;font-weight:bold;";
				if (r.get("YSBZ") == 1) {
					if (v == 0) {
						return "<font color='#1E90FF'>新开</font>"
					} else if (v === 1 && r.get("FHBZ") != 1) {
						return "<font color='#32CD32'>提交</font>"
					} else if (v === 1 && r.get("FHBZ") == 1) {
						return "<font color='#FA8072'>复核</font>"
					}
				} else {
					if (r.get("FHBZ") != 1) {
						return "<font color='#1E90FF'>新开</font>"
					} else if (r.get("FHBZ") == 1) {
						return "<font color='#FA8072'>复核</font>"
					}
				}
			},
			ysbzRender : function(v, params, r) {
				params.style = "text-align:left;font-weight:bold;";
				if (v == 1) {
					return "<font color='red'>医</font>"
				}
				return "";
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH_SHOW") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return " ";
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'
								}, {
									name : 'YZMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YPDW'
								}, {
									name : 'YPSL'
								}, {
									name : 'JLDW'
								}, {
									name : 'YPJL'
								}, {
									name : 'YCJL'
								}, {
									name : 'YFBZ'
								}, {
									name : 'GYFF'
								},// 药品用法
								{
									name : 'LSJG'
								}, {
									name : 'YPCD'
								}, {
									name : 'CDMC'
								}, {
									name : 'PSPB'
								}, {
									name : 'TSYP'
								}, {
									name : 'TYPE'
								}, {
									name : 'TSYP'
								}, {
									name : 'YFDW'
								}, {
									name : 'YBFL'
								}, {
									name : 'YBFL_text'
								}, {
									name : 'GYFF_text'
								}, {
									name : 'JBYWBZ'
								}, {
									name : 'YPGG'
								}, {
									name : 'KCSL'
								}, {
									name : 'YFSB'
								}, {
									name : 'YFSB_text'
								}, {// 以下为项目部分
									name : 'FYXH'
								}, {
									name : 'FYMC'
								}, {
									name : 'FYDW'
								}, {
									name : 'XMLX'
								}, {
									name : 'FYDJ'
								}, {
									name : 'FYGB'
								}, {
									name : 'FYKS'
								}, {
									name : 'FYKS_text'
								}, {
									name : 'YPLX'
								}, {
									name : 'FYFS'
								}, {
									name : 'FYFS_text'
								}, {
									name : 'YJSY'
								},// 抗菌药物
								{
									name : 'KSBZ'
								}, {
									name : 'YQSYFS'
								}, {
									name : 'YCYL'
								}, {
									name : 'KSSDJ'
								}, {
									name : 'SFSP'
								}, {
									name : 'ZFYP'
								}]);
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);

					if ((r.get("YPXH") == null || r.get("YPXH") == "" || r
							.get("YPXH") == 0)) {
						if (r.get("JFBZ") != 3 || !r.get("YZMC")) {
							store.remove(r);
						}
					}
				}
			},
			myLoadData : function(store) {
				// this.showPanelTips(this.grid);
				var count = this.store.getCount();
				var yzzh = 0;
				var lastYZZH = -1;
				for (var i = 0; i < count; i++) {
					var now_yzzh = this.store.getAt(i).get("YZZH");
					if (now_yzzh != lastYZZH) {
						yzzh++;
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
						lastYZZH = now_yzzh;
					} else {
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
					}
				}
				this.store.each(function(r) {
							r.set("ZFYP", r.data.ZFYP == 1 ? true : false);
						}, this);
				this.store.commitChanges();
				if (!this.exContext.readOnly) {
					this.doNewGroup();
				}
			},
			doNewGroup : function(item, e, stopEditor) {
				this.doInsertAfter(item, e, true, stopEditor);
			},
			doInsert : function(item, e) {// 当前记录前插入一条记录
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					if (this.openBy == "nurse"
							&& selectdRecord.get("YSBZ") == 1) {
						MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
						return;
					}
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0)
							&& selectdRecord.get("JFBZ") != 3) {
						selectRow = selectRow;
					} else {
						selectRow = this.store.indexOf(selectdRecord) + 1;
					}
				} else {
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue != undefined) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							data[it.id] = v.key;
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v]
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
				}
				var r = new Record(data)
				try {
					store.insert(row, [r])
				} catch (e) {
					store.removeAll();
					return;
				}
				var storeData = store.data;
				var rowItem = null;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 第一条记录或者自动新组
					rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YZZH_SHOW", 1);
					if (this.openBy == "nurse"
							&& this.exContext.brxx.get("ZRYS")) {
						rowItem.set("YSGH", this.exContext.brxx.get("ZRYS"));
						rowItem.set("YSGH_text", this.exContext.brxx
										.get("ZRYS_text"));
						// if (this.exContext.docPermissions.KCFQ == 1) {
						rowItem.set("HSGH", this.mainApp.uid);
						rowItem.set("HSGH_text", this.mainApp.uname);
						// }
					} else {
						rowItem.set("HSGH", this.exContext.brxx.get("ZRHS"));
						rowItem.set("HSGH_text", this.exContext.brxx
										.get("ZRHS_text"));
						rowItem.set("YSGH", this.mainApp.uid);
						rowItem.set("YSGH_text", this.mainApp.uname);
					}
					rowItem.set("CZGH", this.mainApp.uid);
					rowItem.set("CZGH_text", this.mainApp.uname);
					rowItem.set("KSSJ", Date.getServerDateTime());
					rowItem.set("LSYZ", this.adviceType == 'longtime' ? 0 : 1);
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					rowItem = storeData.itemAt(row);
					rowItem.set("KSSJ", upRowItem.get("KSSJ"));
					rowItem.set("JFBZ", upRowItem.get("JFBZ"));
					rowItem.set("YZZH_SHOW", upRowItem.get("YZZH_SHOW"));
					rowItem.set("YZZH", upRowItem.get("YZZH"));
					rowItem.set("LSYZ", this.adviceType == 'longtime' ? 0 : 1);
					rowItem.set("YPYF", upRowItem.get("YPYF"));
					rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
					rowItem.set("SYPC", upRowItem.get("SYPC"));
					rowItem.set("SYPC_text", upRowItem.get("SYPC_text"));
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("SRCS", upRowItem.get("SRCS"));
					rowItem.set("YZZXSJ", upRowItem.get("YZZXSJ"));
					rowItem.set("YPLX", upRowItem.get("YPLX"));// 判断药品项目依据
					rowItem.set("CZGH", this.mainApp.uid);
					rowItem.set("CZGH_text", this.mainApp.uname);
					rowItem.set("YSGH", upRowItem.get("YSGH"));
					rowItem.set("YSGH_text", upRowItem.get("YSGH_text"));
					rowItem.set("HSGH", upRowItem.get("HSGH"));
					rowItem.set("HSGH_text", upRowItem.get("HSGH_text"));
					rowItem.set("uniqueId", upRowItem.get("uniqueId"));
					rowItem.set("TJZRQ", upRowItem.get("TJZRQ"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
				}
				// 设置公共属性
				rowItem.set("YSTJ", 0);
				rowItem.set("JGID", this.mainApp.deptId);
				rowItem.set("ZYH", this.initDataId);
				rowItem.set("YSBZ", (this.openBy == "nurse" ? 0 : 1));
				this.grid.getView().refresh()// 刷新行号
				this.grid.startEditing(row, 4);
				this.onRowClick();

			},
			doInsertAfter : function(item, e, newGroup, stopEditor) {// 当前记录后插入一条记录
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();
				this.doCreate();
				var rowItem = null;
				if (maxIndex == 0 || newGroup) {// 处方的第一条记录或者自动新组
					var yzzh = 1;
					var upRowItem = storeData.itemAt(maxIndex - 1);
					if (maxIndex > 0) {
						yzzh = upRowItem.get("YZZH_SHOW") + 1;
					}
					rowItem = storeData.itemAt(maxIndex);
					rowItem.set("YZZH_SHOW", yzzh);
					if (this.openBy == "nurse"
							&& this.exContext.brxx.get("ZRYS")) {
						rowItem.set("YSGH", this.exContext.brxx.get("ZRYS"));
						rowItem.set("YSGH_text", this.exContext.brxx
										.get("ZRYS_text"));
						// if (this.exContext.docPermissions.KCFQ == 1) {
						rowItem.set("HSGH", this.mainApp.uid);
						rowItem.set("HSGH_text", this.mainApp.uname);
						// }
					} else {
						rowItem.set("HSGH", this.exContext.brxx.get("ZRHS"));
						rowItem.set("HSGH_text", this.exContext.brxx
										.get("ZRHS_text"));
						rowItem.set("YSGH", this.mainApp.uid);
						rowItem.set("YSGH_text", this.mainApp.uname);
					}
					rowItem.set("CZGH", this.mainApp.uid);
					rowItem.set("CZGH_text", this.mainApp.uname);
					rowItem.set("KSSJ", Date.getServerDateTime());
					// rowItem.set("LSYZ", this.adviceType == 'longtime' ? 0 :
					// 1);
				} else {
					var upRowItem = storeData.itemAt(maxIndex - 1);
					rowItem = storeData.itemAt(maxIndex);
					rowItem.set("YZZH_SHOW", upRowItem.get("YZZH_SHOW"));
					rowItem.set("YZZH", upRowItem.get("YZZH"));
					// rowItem.set("LSYZ", this.adviceType == 'longtime' ? 0 :
					// 1);
					rowItem.set("JFBZ", upRowItem.get("JFBZ"));
					rowItem.set("KSSJ", upRowItem.get("KSSJ"));
					rowItem.set("YPYF", upRowItem.get("YPYF"));
					rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
					rowItem.set("SYPC", upRowItem.get("SYPC"));
					rowItem.set("SYPC_text", upRowItem.get("SYPC_text"));
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("SRCS", upRowItem.get("SRCS"));
					rowItem.set("YZZXSJ", upRowItem.get("YZZXSJ"));
					rowItem.set("YPLX", upRowItem.get("YPLX"));// 判断药品项目依据
					rowItem.set("YSGH", upRowItem.get("YSGH"));
					rowItem.set("YSGH_text", upRowItem.get("YSGH_text"));
					rowItem.set("CZGH", this.mainApp.uid);
					rowItem.set("CZGH_text", this.mainApp.uname);
					rowItem.set("uniqueId", upRowItem.get("uniqueId"));
					rowItem.set("TJZRQ", upRowItem.get("TJZRQ"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
				}
				// 设置公共属性
				rowItem.set("JGID", this.mainApp.deptId);
				rowItem.set("ZYH", this.initDataId);
				rowItem.set("YSBZ", (this.openBy == "nurse" ? 0 : 1));
				if (!stopEditor) {
					this.grid.startEditing(maxIndex, 4);
				} else {
					this.grid.getSelectionModel().select(maxIndex, 0)
				}
				this.onRowClick();
			},
			setBackInfo : function(obj, record) {
				Ext.EventObject.stopEvent();// 停止事件
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				if (!this.setRecordIntoList(record.data, rowItem, row)) {
					return;
				}
				var YZMC;
				if (record.get("YPLX") == 0) {
					YZMC = record.get("FYMC")
							+ (record.get("FYDW")
									? "/" + record.get("FYDW")
									: "")
				} else {
					YZMC = record.get("YPMC")
							+ (record.get("YFGG")
									? "/" + record.get("YFGG")
									: "")
							+ (record.get("YFDW")
									? "/" + record.get("YFDW")
									: "")
				}
				rowItem.modified["YZMC"] = YZMC;
				obj.setValue(YZMC);
				obj.collapse();
				obj.triggerBlur();
				rowItem.set("YZMC", YZMC);
				// 调用需要异步的校验
				this.loop = new asynLoop([this.checkAntibacterials], [
								record.data, rowItem], this);
				this.loop.over = function() {
					if (this.grid.activeEditor != null) {
						this.grid.activeEditor.completeEdit();
					}
					this.grid.startEditing(row, record.get("YPLX") === 0
									? 7
									: 5);
				}
				this.loop.start();
			},
			setRecordIntoList : function(data, rowItem, curRow) {
				var ypyf = this.grid.getColumnModel().getColumnById("YPYF").editor;
				var store = this.grid.getStore();
				var n = store.getCount()
				if (data.YPLX == 0) {// 项目
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (i != curRow
								&& r.get("YZZH_SHOW") == rowItem
										.get("YZZH_SHOW")) {
							if (r.get("YPXH") == data.FYXH) {
								MyMessageTip.msg("提示", "\"" + data.FYMC
												+ "\"在这组中已存在，请进行修改!", true);
								return false;
							}
						}
					}
					this.getDetailsInfo(data, rowItem, curRow);
					rowItem.set('YPJL', 0);
					rowItem.set('YCJL', 0);
					rowItem.set('JLDW', null);
					rowItem.set('YFDW', null);
					rowItem.set('YFBZ', "");
					rowItem.set("YFSB", null);
					rowItem.set("YFSB_text", null);
					rowItem.set('YPCD', null);
					rowItem.set('YPCD_text', null);
					rowItem.set("FYFS", null);
					rowItem.set("FYFS_text", null)
					rowItem.set("BZXX", null)
					rowItem.set("ZFYP", false);
					rowItem.set("ZBY", 0);
					rowItem.set("XMLX", data.XMLX || 4);
					rowItem.set("YPXH", data.FYXH);
					rowItem.set("YJXH", data.FYXH);
					rowItem.set("YZMC", data.FYMC);
					rowItem.set("YPDJ", data.FYDJ);
					// rowItem.set("YFDW", data.FYDW);
					rowItem.set("YJZX", data.YJZX);
					rowItem.set("YPLX", 0);
					rowItem.set("ZXKS", data.FYKS
									|| this.exContext.brxx.get("BRKS"))
				} else if (data.YPLX > 0) {//
					// XXX（病人的姓名）对XXX（药品名称）过敏，如果有不良反应，则还需显示不良反应为XXX
					// 获取发药药房：YPXH（药品序号），BQDM（病区代码）,YZLX(医嘱类型，这里为1)
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (i != curRow
								&& r.get("YZZH_SHOW") == rowItem
										.get("YZZH_SHOW")) {
							if (r.get("YPXH") == data.YPXH) {
								MyMessageTip.msg("提示", "\"" + data.YPMC
												+ "\"在这组中已存在，请进行修改!", true);
								return false;
							}
						}
					}
					if (!this.checkDoctorPermission(data, rowItem))// 判断药品权限
						return false;
					this.getDetailsInfo(data, rowItem, curRow);
					// 抗生标志 add by yangl
					rowItem.set('KSBZ', data.KSBZ);
					rowItem.set('YCYL', data.YCYL);
					rowItem.set('SFSP', data.SFSP);
					rowItem.set("YJXH", 0);
					rowItem.set("XMLX", 1);
					rowItem.set('YPMC', data.YPMC);
					rowItem.set('YFGG', data.YFGG);
					rowItem.set('JLDW', data.JLDW);
					rowItem.set('YFDW', data.YFDW);
					rowItem.set('YPXH', data.YPXH);
					rowItem.set('YPJL', data.YPJL);
					rowItem.set('YCJL', data.YCJL);
					rowItem.set('YFBZ', data.YFBZ);
					rowItem.set('YPDJ', data.LSJG);
					rowItem.set('YPLX', data.TYPE);
					rowItem.set('YPCD', data.YPCD);
					rowItem.set('YPCD_text', data.CDMC);
					rowItem
							.set(
									"YCSL",
									parseFloat(rowItem.get("YCJL")
											/ (parseFloat(rowItem.get("YPJL")) * parseFloat(data.YFBZ)))
											.toFixed(2));
					// this.checkInventory(record);
					// 判断是否已经存在GYTJ GYFF
					if (rowItem.get("YPYF") == null
							|| rowItem.get("YPYF") == ""
							|| rowItem.get("YPYF") == 0) {
						if (data.GYFF) {
							rowItem.set("YPYF", data.GYFF);
							rowItem.set("YPYF_text", data.GYFF_text)
							this.ypyfSelect(ypyf, ypyf.findRecord("key",
											rowItem.get("YPYF")));// 附加项目
						}
					}
					rowItem.set("FYFS", data.FYFS);
					rowItem.set("FYFS_text", data.FYFS_text)
					// var yfxx = this.getYfsb(data.TYPE)
					// alert(data.YFSB + "+"+data.YFSB_text)
					rowItem.set("YFSB", data.YFSB);
					rowItem.set("YFSB_text", data.YFSB_text);
					rowItem.set("ZBY", data.ZFYP);
					rowItem.set("ZFYP", data.ZFYP == 1 ? true : false);
				}
				return true;
			},
			checkDoctorPermission : function(data, rowItem) {
				if (data.TSYP > 0) {
					if (data.TSYP == this.exContext.systemParams.MZYP) {
						if (this.exContext.docPermissions.MZYQ == ""
								|| this.exContext.docPermissions.MZYQ == "0") {
							MyMessageTip.msg("提示", '药品【' + data.YPMC
											+ '】是麻醉类药品，你暂不能开麻醉类处方!', true);
							return false;
						}
					}
					if (data.TSYP == this.exContext.systemParams.JSYP) {
						if (this.exContext.docPermissions.JSYQ == ""
								|| this.exContext.docPermissions.JSYQ == "0") {
							MyMessageTip.msg("提示", '药品【' + data.YPMC
											+ '】是精神类药品，你暂不能开精神类处方!', true);
							return false;
						}
					}
				}
				// 增加抗生素管理功能 add by yangl
				// 1、判断是否抗生素（具体规则待需求完善）
				if (this.exContext.systemParams.QYKJYWGL == 1 && data.KSBZ > 0) {
					if (this.adviceType == 'longtime') {// 长期医嘱不能使用
						MyMessageTip.msg("提示", '药品【' + data.YPMC
										+ '】是抗菌药物，只能在临时医嘱中使用!', true);
						return false;
					}
					if (!data.KSSDJ
							|| !this.exContext.docPermissions.KSSQX
							|| this.exContext.docPermissions.KSSQX
									.indexOf(data.KSSDJ) < 0) {// 医生没有权限
						// 判断药物提醒方式
						if (!data.YQSYFS || data.YQSYFS == '2') {
							MyMessageTip.msg("提示", "您没有抗菌药物【" + data.YPMC
											+ "】的使用权限！", true);
							return false;
						} else {
							// rowItem.set("YQSY", 1);
							// this.doRemainMedic(data, rowItem);
						}
					} else {
						// 2、如果是，则弹出提示消息：当前药物为抗生素，是否需要申请？如果是则弹出抗生素申请界面
						// this.doRemainMedic(data, rowItem);
					}
				}
				return true;
			},
			checkAntibacterials : function(data, rowItem) {
				if (this.exContext.systemParams.QYKJYWGL == 1 && data.KSBZ > 0) {
					if (this.adviceType == 'longtime') {// 长期医嘱不能使用
						MyMessageTip.msg("提示", '药品【' + data.YPMC
										+ '】是抗菌药物，只能在临时医嘱中使用!', true);
						return false;
					}
					if (!data.KSSDJ
							|| !this.exContext.docPermissions.KSSQX
							|| this.exContext.docPermissions.KSSQX
									.indexOf(data.KSSDJ) < 0) {// 医生没有权限
						// 判断药物提醒方式
						if (!data.YQSYFS || data.YQSYFS == '2') {
							MyMessageTip.msg("提示", "您没有抗菌药物【" + data.YPMC
											+ "】的使用权限！", true);
							return false;
						} else {
							rowItem.set("YQSY", 1);
							this.doRemainMedic(data, rowItem);
						}
					} else {
						// 2、如果是，则弹出提示消息：当前药物为抗生素，是否需要申请？如果是则弹出抗生素申请界面
						this.doRemainMedic(data, rowItem);
					}
				} else {
					this.loop.next();
				}
			},
			doRemainMedic : function(data, rowItem) {
				// 住院审批是否开启
				var _ctx = this;
				if (data.SFSP != 1
						|| this.exContext.systemParams.QYZYKJYWSP != '1') {
					if (rowItem.get("YQSY") == 1) {
						setTimeout(toShowYqMsg, 500);// 延迟调用，解决焦点问题
					}
					return;
				}
				// 显示提示信息
				setTimeout(toShowMsg, 500);// 延迟调用，解决焦点问题
				function toShowMsg() {
					// 判断是否已经审批，如果是，则跳过申请（返回剩余可用数量），否则弹出申请单
					var SPSL = _ctx.checkApplyInfo(data, rowItem);
					data.YCYL = SPSL;
					rowItem.set("YCYL", SPSL);
					if (SPSL > 0) {
						this.loop.next();
						return;
					}
					_ctx.grid.stopEditing();
					Ext.Msg.show({
						title : '提示',
						msg : '【'
								+ data.YPMC
								+ '】属需审批抗菌药物，需提交抗菌药物申请表经相关人员同意方可使用！<br />是否现在填写申请？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								// 弹出审批界面
								this.doShowAduitWin(data, rowItem);
							} else {
								var r = this.getSelectedRecord();
								this.store.remove(r)
								this.loop.next();
							}
						},
						scope : _ctx
					})
				}
				function toShowYqMsg() {
					_ctx.grid.stopEditing();
					Ext.Msg.show({
								title : '提示',
								msg : '您没有抗菌药物（' + data.YPMC
										+ '）的使用权限！若越权使用，限一日剂量，是否确认要越权使用？',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.loop.next();
										// var r = this.getSelectedRecord();
										// var cell = this.grid
										// .getSelectionModel()
										// .getSelectedCell();
										// this.grid.startEditing(cell[0], 5);
									} else {
										var r = this.getSelectedRecord();
										this.store.remove(r)
										this.loop.next();
									}
								},
								scope : _ctx
							})
				}
			},
			checkApplyInfo : function(data, rowItem) {
				// 根据药品序号缓存能够使用的数量，便于前台处理
				if (!this.cacheSpslReocrd) {
					this.cacheSpslReocrd = {};
				}
				if (this.cacheSpslReocrd[data.YPXH]) {
					return this.cacheSpslReocrd[data.YPXH];
				}
				// this.patinetData
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "checkApplyInfo",
							body : {
								JZXH : this.initDataId,
								YPXH : data.YPXH,
								JGID : this.mainApp.deptId
							}
						});

				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				return this.cacheSpslReocrd[data.YPXH] = res.json.body.SPSL;
			},
			// 申请审批
			doShowAduitWin : function(data, rowItem) {
				var form = this.createModule("antibacterialApplyForm",
						this.refAntibacterialApplyForm);
				form.initDataId = null;
				form.brxx = this.exContext.brxx.data;
				form.brxx.YPXH = data.YPXH;
				form.brxx.YPMC = data.YPMC;
				form.brxx.YFGG = data.YFGG;
				form.brxx.YPJL = data.YPJL;
				form.brxx.YFBZ = data.YFBZ;
				form.openBy = "fsb";
				form.fixLoadCfg = function(cfg) {
					cfg.openBy = "fsb"
				}
				form.on("hide", function(f) {

							if (f.initDataId) {
								// var cell = this.grid.getSelectionModel()
								// .getSelectedCell();
								// this.grid.startEditing(cell[0], 5);
							} else {
								var r = this.getSelectedRecord();
								this.store.remove(r)

							}
							this.loop.next();
						}, this);
				if (!this.applyFormWin) {
					this.applyFormWin = form.getWin();
					this.applyFormWin.add(form.initPanel());
				}
				this.applyFormWin.show();
			},
			// 获取选择医嘱的详细信息，如自负判别，过敏判别
			getDetailsInfo : function(data, rowItem, curRow) {
				// 获取自负比例
				// 判断是否有过敏 loadAllergyInfo
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "loadDetailsInfo",
							body : {
								ZYH : this.initDataId,
								YPXH : data.YPLX > 0 ? data.YPXH : data.FYXH,
								YPLX : data.YPLX,
								BRXZ : this.exContext.brxx.get("BRXZ"),
								BRID : this.exContext.brxx.get("BRID")
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var body = json.body;
								var zfbl = body.payProportion.ZFBL;
								/**
								 * 2013-09-10
								 * 直接使用医保返回的自负判别(-1的情况下调用前会直接返回，所以不会进入该方法) *
								 */
								rowItem.set("ZFPB", data.ZFPB);
								if (zfbl > 0) {
									// MyMessageTip.msg("提示",
									// (data.YPLX>0?data.YPMC :
									// data.FYMC)+"的自负比例是"+zfbl+"!", true);
									rowItem.set("ZFPB", 1);
								}
								/** modify end* */
								if (body.isAllergy) {
									var errorMsg = "病人【"
											+ this.exContext.brxx.get("BRXM")
											+ "】对药物【" + data.YPMC + "】过敏";
									if (body.BLFY && body.BLFY.length > 0) {
										errorMsg += ",\n不良反应为:" + body.BLFY
									}
									errorMsg += ",\n是否进行录入？";
									if (confirm(errorMsg)) {
										this.grid.startEditing(curRow, 5);
									} else {
										var Yzzh = rowItem.get("YZZH");
										var upYzzh = -1;
										this.store.remove(rowItem);
										if (curRow > 0) {
											this.grid.getSelectionModel()
													.select(curRow - 1, 0)
											var r = this.store
													.getAt(curRow - 1);
											upYzzh = r.get("YZZH");
										}
										Yzzh == upYzzh ? this.doInsert() : this
												.doNewGroup();
									}
								} else {
									if (data.PSPB > 0) {
										MyMessageTip.msg("注意", '药品【'
														+ data.YPMC
														+ '】是皮试药品，需要做皮试处理!',
												true);
									}
								}
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
			},
			// 单停
			doSingleStop : function() {
				var r = this.getSelectedRecord();
				if (r) {
					if (this.openBy == "nurse" && r.get("YSBZ") == 1) {
						MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
						return;
					}
					if (this.openBy != "nurse"
							&& r.get("YSGH") != this.mainApp.uid) {
						MyMessageTip.msg("提示", "不能操作其他医生开出的医嘱信息!", true);
						return;
					}
					if (r.get("TZSJ") || (!r.get("YPXH") && r.get("JFBZ") != 3))// 无效医嘱或者
						// 已经存在单停时间
						return;
					var isGroupFirst = false;
					var yzzh = r.get("YZZH");
					var count = 0;
					this.store.each(function(record) {
								if (record.get("YZZH") == yzzh) {
									count++;
									if (r == record && count == 1) {
										isGroupFirst = true
									}
								}
							})
					if (count > 1 && isGroupFirst) {
						Ext.Msg.confirm("提示", "是否停同组的医嘱？", function(btn) {
									if (btn == 'yes') {
										this.execGroupStop(yzzh);
									} else {
										this.execSingleStop(r);
									}
								}, this);
					} else {
						this.execSingleStop(r);
					}
				}
			},
			doAllStop : function() {
				this.doTimeTested();
			},
			execSingleStop : function(r) {
				// new Date().format('Y-m-d H:i:s')
				r.set("TZSJ", Date.getServerDateTime());
				r.set("TZYS", this.exContext.brxx.get("ZRYS"));
				r.set("TZYS_text", this.exContext.brxx.get("ZRYS_text"));
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				this.grid.startEditing(cell[0], 14);
				var records = [];
				this.copyReocrd(records, r.data);
				Ext.Msg.confirm("提示", "是否复制医嘱？", function(btn) {
							if (btn == 'yes') {
								this.copyStopedRecords(records);
							}
						}, this);
			},
			execGroupStop : function(yzzh) {
				var isfirst = true;
				var tzsj = Date.getServerDateTime();
				var records = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get("YZZH") == yzzh) {
						r.set("TZSJ", tzsj);
						r.set("TZYS", this.exContext.brxx.get("ZRYS"));
						r
								.set("TZYS_text", this.exContext.brxx
												.get("ZRYS_text"));
						// r.set("TZYS", this.mainApp.uid);
						// r.set("TZYS_text", this.mainApp.uname);
						if (isfirst) {
							isfirst = false;
							this.grid.startEditing(i, 14);
						}
						this.copyReocrd(records, r.data);
					}
				}
				Ext.Msg.confirm("提示", "是否复制医嘱？", function(btn) {
							if (btn == 'yes') {
								this.copyStopedRecords(records);
							}
						}, this);

			},
			executeAllStop : function() {
				var tzsj = this.dateField.getValue();
				var count = 0;
				var records = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (!r.get("TZSJ") && (r.get("YPXH") || r.get("JFBZ") == 3)) {
						if (this.openBy == "nurse" && r.get("YSBZ") == 1) {
							// MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
							continue;
						}
						if (this.openBy != "nurse"
								&& r.get("YSGH") != this.mainApp.uid) {
							// MyMessageTip.msg("提示", "不能操作其他医生开出的医嘱信息!", true);
							continue;
						}
						// if (r.get("QRSJ")) {
						// if (Date.parseDate(tzsj, "Y-m-d H:i:s").getTime() <=
						// Date
						// .parseDate(r.get("QRSJ"), "Y-m-d H:i:s")
						// .getTime()) {
						// MyMessageTip.msg("提示", "停嘱时间必须大于确认时间!错误行 "
						// + (i + 1) + " 。", true);
						// continue;
						// }
						// } else
						if (r.get("KSSJ")) {
							if (Date.parseDate(tzsj, "Y-m-d H:i:s").getTime() <= Date
									.parseDate(r.get("KSSJ"), "Y-m-d H:i:s")
									.getTime()) {
								MyMessageTip.msg("提示", "停嘱时间必须大于开嘱时间!错误行 "
												+ (i + 1) + " 。", true);
								continue;
							}
						}
						r.set("TZSJ", tzsj);
						r.set("TZYS", this.exContext.brxx.get("ZRYS"));
						r
								.set("TZYS_text", this.exContext.brxx
												.get("ZRYS_text"));
						this.copyReocrd(records, r.data);
						count++;
					}
				}
				if (count == 0) {
					MyMessageTip.msg("提示", "没有可停止的医嘱信息!", true);
				} else {
					Ext.Msg.confirm("提示", "是否复制医嘱？", function(btn) {
								if (btn == 'yes') {
									this.copyStopedRecords(records);
								}
							}, this);
				}
				this.wind.hide();
			},
			copyStopedRecords : function(records) {
				if (!records || records.length == 0)
					return;
				this.removeEmptyRecord();
				var kssj = Date.getServerDateTime();
				var yzzh = this.store.getAt(this.store.getCount() - 1)
						.get("YZZH_SHOW")
				var lastYzzh = -1;
				var items = this.schema.items
				var o = this.getStoreFields(items)
				var store = this.grid.getStore();
				var Record = Ext.data.Record.create(o.fields)
				for (var i = 0; i < records.length; i++) {
					var data = records[i];
					if (data.YZZH_SHOW != lastYzzh) {
						lastYzzh = data.YZZH_SHOW;
						data.YZZH_SHOW = ++yzzh;
					} else {
						data.YZZH_SHOW = yzzh;
					}
					data.KSSJ = kssj;
					if (this.openBy == "nurse"
							&& this.exContext.brxx.get("ZRYS")) {
						data.YSGH = this.exContext.brxx.get("ZRYS");
						data.YSGH_text = this.exContext.brxx.get("ZRYS_text");
						// if (this.exContext.docPermissions.KCFQ == 1) {
						data.HSGH = this.mainApp.uid;
						data.HSGH_text = this.mainApp.uname;
						// }
					} else {
						data.HSGH = this.exContext.brxx.get("ZRHS");
						data.HSGH_text = this.exContext.brxx.get("ZRHS_text");
						data.YSGH = this.mainApp.uid;
						data.YSGH_text = this.mainApp.uname;
					}
					var r = new Record(data);
					this.store.add(r);
				}
			},
			copyReocrd : function(records, data) {
				var dataCopy = {};
				Ext.apply(dataCopy, data);
				delete dataCopy.JLXH;
				delete dataCopy.YZZH;
				delete dataCopy.TZSJ;
				delete dataCopy.TZYS;
				delete dataCopy.TZYS_text;
				delete dataCopy.TJZRQ;
				delete dataCopy.TJZX;
				delete dataCopy.QRSJ;
				delete dataCopy.YYTS;
				dataCopy.FHBZ = 0;
				dataCopy.TJZX = 0;
				dataCopy.YSTJ = 0;
				records.push(dataCopy);
			},
			doTimeTested : function() {
				if (!this.wind) {
					this.dateField = new phis.script.widgets.DateTimeField({
								id : 'stopDate',
								value : Date.getServerDateTime(),
								width : 200,
								fieldLabel : '停嘱时间确认',
								allowBlank : false,
								altFormats : 'Y-m-d H:i:s',
								format : 'Y-m-d H:i:s'
							});
					this.dateForm = new Ext.FormPanel({
								frame : true,
								labelAlign : 'right',
								shadow : true,
								items : [this.dateField]
							})
					this.wind = new Ext.Window({
								layout : "form",
								title : '停嘱时间确认',
								width : 350,
								resizable : false,
								modal : true,
								iconCls : 'x-logon-win',
								shim : true,
								buttonAlign : 'center',
								closeAction : 'hide',
								buttons : [{
											text : '确定',
											handler : this.executeAllStop,
											scope : this
										}]
							})
					this.midiWins["stopDateWin"] = this.wind;
					this.wind.add(this.dateForm);
					this.dateField.on("specialkey", this.dateFieldSpeKey, this)
				}
				this.wind.show();
				this.dateField.setMinValue(Date.getServerDateTime())
				this.dateField.setValue(Date.getServerDateTime());
				this.dateField.focus(false, 100)

				// this.wind.on("hide", this.windhide, this);
			},
			dateFieldSpeKey : function(f, e) {
				if (e.getKey() == e.ENTER) {
					this.executeAllStop();
				}
			},
			// 赋空
			doAssignedEmpty : function() {
				var r = this.getSelectedRecord();
				if (r) {
					if (this.openBy == "nurse" && r.get("YSBZ") == 1) {
						MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
						return;
					}
					if (this.openBy != "nurse"
							&& r.get("YSGH") != this.mainApp.uid) {
						MyMessageTip.msg("提示", "不能操作其他医生开出的医嘱信息!", true);
						return;
					}
					if (!r.get("TZSJ")
							|| (!r.get("YPXH") && (r.get("JFBZ") != 3 && !r
									.get("YZMC"))))// 不存在单停时间
						return;
					var isGroupFirst = false;
					var yzzh = r.get("YZZH");
					var count = 0;
					this.store.each(function(record) {
								if (record.get("YZZH") == yzzh) {
									count++;
									if (r == record && count == 1) {
										isGroupFirst = true
									}
								}
							})
					if (count > 1 && isGroupFirst) {
						Ext.Msg.confirm("提示", "是否赋空同组的医嘱？", function(btn) {
									if (btn == 'yes') {
										this.execGroupEmpty(yzzh);
									} else {
										this.execSingleEmpty(r);
									}
								}, this);
					} else {
						this.execSingleEmpty(r);
					}
				}
			},
			execSingleEmpty : function(r) {
				r.set("TZSJ", "");
				r.set("TZYS", "");
				r.set("TZYS_text", "");
			},
			execGroupEmpty : function(yzzh) {
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get("YZZH") == yzzh) {
						r.set("TZSJ", "");
						r.set("TZYS", "");
						r.set("TZYS_text", "");
					}
				}
			},
			// 复核
			doReview : function(panel) {
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveReview",
							body : {
								ZYH : this.initDataId,
								LSYZ : this.adviceType == "longtime" ? 0 : 1
							}
						}, function(code, msg, json) {
							panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.count <= 0) {
								if (json.RMESSAGE == 1) {
									MyMessageTip
											.msg(
													"提示",
													"没有需要复核的医嘱!但有医嘱,因录入与复核人不能为同人限制没有复核!",
													true);
								} else {
									MyMessageTip.msg("提示", "没有需要复核的医嘱!", true);
								}
							} else {
								if (json.RMESSAGE == 1) {
									MyMessageTip.msg("提示",
											"复核成功!但有医嘱,因录入与复核人不能为同人限制没有复核!",
											true);
								} else {
									MyMessageTip.msg("提示", "复核成功!", true);
								}
							}
							this.refresh();
						}, this);
			},
			// 取消复核
			doUnReview : function(panel) {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要取消复核的医嘱组!", true);
					return false;
				}
				var totalCount = 0;
				var _this = this;
				var errMsg = null;
				for (var i = 0; i < this.store.getCount(); i++) {
					var record = this.store.getAt(i);
					if (record.get("YZZH") == r.get("YZZH")
							&& record.get("FHBZ") == 1) {
						if (record.get("TJZX") == 1) {
							errMsg = "医嘱已提交到药房,不能取消复核!";
							// MyMessageTip.msg("提示", "医嘱已提交到药房,不能取消复核!", true);
							continue;
						}
						if (record.get("QRSJ")) {
							errMsg = "医嘱已执行,不能取消复核!";
							// MyMessageTip.msg("提示", "医嘱已执行,不能取消复核!", true);
							continue;
						}
						if (record.get("FHGH") != _this.mainApp.uid) {
							errMsg = "取消复核人和复核人不同,不能取消复核!";
							continue;
						}
						totalCount++;
					}
				}
				if (totalCount <= 0) {
					if (errMsg) {
						MyMessageTip.msg("提示", errMsg, true);
					} else {
						MyMessageTip.msg("提示", "当前选中的医嘱组没有复核,无需取消!", true);
					}
					return false;
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveUnReview",
							body : {
								ZYH : this.initDataId,
								YZZH : r.get("YZZH")
							}
						}, function(code, msg, json) {
							panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.count <= 0) {
								MyMessageTip.msg("提示", "没有需要取消复核的医嘱!", true);
							} else {
								MyMessageTip.msg("提示", "取消复核成功!", true);
							}
							this.refresh();
						}, this);
				return true;
			},
			// 医生站 提交到病区
			docSubmit : function(panel) {
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveDocSubmit",
							body : {
								ZYH : this.initDataId,
								LSYZ : this.adviceType == "longtime" ? 0 : 1
							}
						}, function(code, msg, json) {
							panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.count <= 0) {
								MyMessageTip.msg("提示", "没有需要提交的医嘱!", true);
							} else {
								MyMessageTip.msg("提示", "提交成功!", true);
							}
							this.refresh();
						}, this);
			},
			// 病区 退回
			goback : function(panel) {
				var r = this.getSelectedRecord();
				if (!r || !r.get("JLXH")) {
					MyMessageTip.msg("提示", "请先选中需要退回的医嘱信息!", true);
					return false;
				}
				if (r.get("YSBZ") == 0) {
					MyMessageTip.msg("提示", "非医生录入的医嘱，无需退回!", true);
					return false;
				}
				if (r.get("FHBZ") == 1) {
					MyMessageTip.msg("提示", "当前选中的医嘱已复核,请先取消复核后再退回!", true);
					return false;
				}
				if (r.get("YSTJ") === 0) {
					MyMessageTip.msg("提示", "当前选中的医嘱医生未提交,无需退回!", true);
					return false;
				}
				if (r.get("QRSJ")) {
					MyMessageTip.msg("提示", "当前选中的医嘱医生已执行,不能退回", true);
					return false;
				}
				if (r.get("TJZX") == 1) {
					MyMessageTip.msg("提示", "当前选中的医嘱已提交到药房,不能退回!", true);
					return false;
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveGoback",
							body : {
								JLXH : r.get("JLXH")
							}
						}, function(code, msg, json) {
							panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.count <= 0) {
								MyMessageTip.msg("提示", "退回医嘱失败：可能该医嘱已被其它护士退回!",
										true);
							} else {
								MyMessageTip.msg("提示", "退回医嘱成功!", true);
							}
							this.refresh();
						}, this);
				return true;
			},
			getYfsb : function(type) {
				var yfxx = {};
				if (type == 1) {
					yfxx.YFSB = this.exContext.yfxx.bqxyf;
					yfxx.YFMC = this.exContext.yfxx.bqxyf_text;
				} else if (type == 2) {
					yfxx.YFSB = this.exContext.yfxx.bqzyf;
					yfxx.YFMC = this.exContext.yfxx.bqzyf_text;
				} else if (type == 3) {
					yfxx.YFSB = this.exContext.yfxx.bqcyf;
					yfxx.YFMC = this.exContext.yfxx.bqcyf_text;
				}
				return yfxx;
			},
			// 判断是否有同组有医嘱
			hasEffectGroupRecord : function(record) {
				var uniqueId = (record.get("YZZH") > 0) ? "YZZH" : "uniqueId";
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get(uniqueId) && r.get("YPXH")
							&& r.get(uniqueId) == record.get(uniqueId)) {
						return true;
					}
				}
				this.doNewGroup();
				return false;
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
				if (this.openBy == "nurse" && r.get("YSBZ") == 1) {
					MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
					return;
				}
				if (r.get("YSTJ") == 1 || r.get("FHBZ") == 1) {
					MyMessageTip.msg("提示", "不能删除已提交或者复核的医嘱信息!", true);
					return;
				}
				// 是否删除最后一条记录
				if (r.get("TJZX") == 1
						|| (r.get("QRSJ") && r.get("QRSJ").length > 0)) {
					MyMessageTip.msg("提示", "非新医嘱，不能执行删除操作!", true);
					return;
				}
				if (this.openBy != "nurse" && r.get("YSGH") != this.mainApp.uid) {
					MyMessageTip.msg("提示", "不能删除其他医生开出的医嘱信息!", true);
					return;
				}
				if (r.get("YWID")) {
					MyMessageTip.msg("提示", "该医嘱不能删除!", true);
					return;
				}
				if (r.get("JLXH") == null
						&& !r.get("YPXH")
						&& !r.get("YJXH")
						&& (r.get("JFBZ") != 3 || (r.get("JFBZ") == 3 && !r
								.get("YZMC")))) {
					this.store.remove(r);
					this.reloadYZZH();
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
						this.onRowClick();
					}
				} else {
					Ext.Msg.show({
						title : '确认删除记录[' + r.data.YZMC + ']',
						msg : '删除操作将无法恢复，是否继续?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								this.store.remove(r);
								// 移除之后焦点定位
								var count = this.store.getCount();
								if (count > 0) {
									cm.select(cell[0] < count
													? cell[0]
													: (count - 1), cell[1]);
								}
								if (r.data.JLXH) {
									// 记录后台需要删除的处方识别
									this.removeRecords.push({
												_opStatus : "remove",
												JLXH : r.data.JLXH
											});
								}
								this.store.remove(r);
								this.reloadYZZH();
								// 移除之后焦点定位
								if (cell[0] == this.store.getCount()) {
									this.doInsertAfter();
								} else {
									var count = this.store.getCount();
									if (count > 0) {
										cm.select(cell[0] < count
														? cell[0]
														: (count - 1), cell[1]);
									}
									this.onRowClick();
								}
							}
						},
						scope : this
					})

				}
				if (cell[0] == this.store.getCount()) {
					this.doInsertAfter();
				}
			},
			reloadYZZH : function() {
				var count = this.store.getCount();
				var yzzh = 0;
				var lastYZZH = -1;
				for (var i = 0; i < count; i++) {
					var now_yzzh = this.store.getAt(i).get("YZZH_SHOW");
					if (now_yzzh != lastYZZH) {
						yzzh++;
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
						lastYZZH = now_yzzh;
					} else {
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
					}
				}
				this.grid.getView().refresh()// 刷新行号
			},
			beforeSearchQuery : function() {
				// 判断当前行是否满足同组输入
				var r = this.getSelectedRecord();
				if (!r)
					return false;
				this.remoteDic.lastQuery = "";
				if (!this.remoteDicStore.baseParams) {
					this.remoteDicStore.baseParams = {};
				}
				if (r.get("ZFYP")) {
					this.remoteDicStore.baseParams.ZFYP = 1;
				} else {
					this.remoteDicStore.baseParams.ZFYP = 0;
				}
				var s = this.remoteDic.getValue();
				if (s == null || s == "" || s.length == 0)
					return true;
				this.remoteDicStore.baseParams.TYPE = 1;// 药品
				if (s.substr(0, 1) == '*') {
					if (r.get("YPLX") != -1 && r.get("JFBZ") != 3) {
						MyMessageTip.msg("提示", "当前组不能录入行为医嘱!", true);
						this.remoteDic.setValue("");
					}
					return false;
				} else if (s.substr(0, 1) == '.') {
					if (r.get("YPLX") > 0 || r.get("JFBZ") == 3) {
						// 判断是否有同组信息
						var count = 0;
						this.store.each(function(rd) {
									if (rd.get("YZZH_SHOW")
											&& rd.get("YZZH_SHOW") == r
													.get("YZZH_SHOW")) {
										count++;
									}
								});
						if (count > 1) {
							MyMessageTip.msg("提示", "当前组不能录入费用医嘱!", true);
							this.remoteDic.setValue("");
							return false;
						}
					}
					return s.length >= 2;
				} else {
					if (r.get("JFBZ") == 3) {
						return false;
					}
					if (r.get("YPLX") == 0) {
						// 自动转为费用医嘱查询
						this.remoteDicStore.baseParams.TYPE = 0;// 费用
						// MyMessageTip.msg("提示", "当前组不能录入药品医嘱!", true);
						// this.remoteDic.setValue("");
						// return false;
					}
					return true;
				}
			},
			checkInventory : function(record) {
				// update by caijy at 2014.10.9 for自备药不需要检索库存
				if (record.get("ZFYP") == 1) {
					return;
				}
				var YYZL = record.get("YCSL");
				if (YYZL == null || YYZL == 0)
					return;
				var MRCS = record.get("MRCS");
				var pharmId = record.get("YFSB");// 需要修改
				var data = {};
				var medId = record.get("YPXH");
				if (!record.get("KSSJ") || !record.get("SYPC") || !medId
						|| !pharmId || !MRCS) {
					return;
				}
				data.medId = medId;
				data.medsource = record.get("YPCD");
				// if (this.adviceType == 'DischargeMedication') {
				data.quantity = parseFloat(YYZL);
				// } else {
				// data.quantity = parseFloat(YYZL) * parseFloat(MRCS);
				// }
				data.pharmId = pharmId;
				data.ypmc = record.get("YZMC");
				data.lsjg = record.get("YPDJ");
				if (!data.quantity)
					return;
				// add by yangl 增加抗生素限量判断
				if (this.exContext.systemParams.QYKJYWGL == 1
						&& record.get("KSBZ") > 0) {
					if ((record.get("SFSP") != 1 || this.exContext.systemParams.QYZYKJYWSP != '1')
							&& record.get("YQSY") == 1) {
						// 获取当天该病人已经保存的用药总量（后台）
						var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "loadAmqcCount",
									body : {
										YPXH : medId,
										ZYH : this.initDataId,
										KSSJ : record.get("KSSJ").split(" ")[0]
									}
								});
						var json = res.json;
						if (res.code > 200) {
							this.processReturnMsg(res.code, res.msg);
							return;
						}
						var sysl_ht = res.json.SYSL;// 后台已使用数量
						var sysl_qt = this.getSysl(record);
						if (data.quantity > (record.get("YCYL") - sysl_ht - sysl_qt)) {
							MyMessageTip.msg("错误", "越权使用的抗菌药物【"
											+ record.get("YPMC")
											+ "】数量不能大于维护的一日限量!最大限量为："
											+ record.get("YCYL")
											+ (record.get("YFDW") || '')
											+ ",当天累计已使用数量为"
											+ parseFloat((sysl_ht + sysl_qt))
													.toFixed(2)
											// + record.get("YCYL")
											+ record.get("YFDW"), true);
							record.set("SIGN", -1);
							return;
						} else {
							record.set("SIGN", 1);
						}
					} else {
						// 判断是否需要审批
						if (record.get("SFSP") == 1
								&& this.exContext.systemParams.QYZYKJYWSP == '1') {
							if (record.get("YCYL") > 0
									&& data.quantity > record.get("YCYL")) {
								var task = new Ext.util.DelayedTask(function() {
									this.grid.stopEditing();
									var msg = '抗菌药物【'
											+ record.get("YPMC")
											+ '】当前可用数量为：'
											+ record.get("YCYL")
											+ record.get("YFDW")
											+ ',如要超量使用，需提交抗菌药物申请!<br />是否现在填写申请？';
									Ext.Msg.show({
										title : '提示',
										msg : msg,
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.YESNO,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "yes") {
												// 弹出审批界面
												this
														.doShowAduitWin(record.data);
											} else {
												record.set("SIGN", -1);
												return;
											}
										},
										scope : this
									})
								}, this)
								task.delay(200);
								return;
							} else {
								record.set("SIGN", 1);
							}
						}
					}
				}
				if (this.exContext.systemParams.JCKCGL != '3') {
					record.set("SIGN", 1);
					return;
				}
				// 校验是否有足够药品库存
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "checkInventory",
							body : data
						}, function(code, mmsg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, mmsg);
								return;
							}
							if (json.sign <= 0) {
								MyMessageTip.msg("警告", "药品【" + data.ypmc
												+ "】库存不足!库存数量："
												+ (json.KCZL || 0) + ",实际数量："
												+ data.quantity, true);
								record.set("SIGN", -1);
							} else {
								record.set("SIGN", 1);
							}

						}, this)
			},
			getSysl : function(record) {
				var sysl = 0.0;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (!r.get("JLXH") && r != record
							&& r.get("YPXH") == record.get("YPXH")) {
						// 是否为同一天
						if (r.get("KSSJ").split(" ")[0] == record.get("KSSJ")
								.split(" ")[0]) {
							sysl += r.get("YCSL") || 0.0;
						}
					}
				}
				return sysl;
			},
			// 附加项目停嘱与付空
			stopOrEmptyExlist : function() {
				var lastTzys = null, lastTzsj = null, lastYzzh = -1, allStop = true;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get("YZZH_SHOW") != lastYzzh) {
						if (lastYzzh != -1) {
							// 更新附加项目停嘱状态
							var lastR = this.store.getAt(i - 1);
							var uniqueId = (lastR.get("YZZH") > 0)
									? "YZZH"
									: "uniqueId";
							allStop = true;
						}
						if (!r.get("TZSJ")) {
							allStop = false;
						}
						lastTzys = r.get("TZYS");
						lastTzsj = r.get("TZSJ");
						lastYzzh = r.get("YZZH_SHOW");
					} else {
						if (!r.get("TZSJ")) {
							allStop = false;
						}
						if (!lastTzsj
								|| (r.get("TZSJ") && Date.parseDate(
										r.get("TZSJ"), "Y-m-d H:i:s").getTime() > Date
										.parseDate(lastTzsj, "Y-m-d H:i:s")
										.getTime())) {
							lastTzsj = r.get("TZSJ");
							lastTzys = r.get("TZYS");
						}
					}

					if (i == (this.store.getCount() - 1)) {// 最后一条数据
						var lastR = this.store.getAt(i);
						var uniqueId = (lastR.get("YZZH") > 0)
								? "YZZH"
								: "uniqueId";
					}
				}
			},
			doSave : function(unrefresh) {
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
				// 判断数据有效性
				this.removeEmptyRecord();
				// return true;
				var store = this.grid.getStore();
				var n = store.getCount()
				if (n == 0 && this.removeRecords.length == 0) {
					MyMessageTip.msg("提示", "请先录入医嘱数据!", true);
					this.doInsert();
					return false;
				}
				var listData = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					var items = this.schema.items;
					var type = r.get("YPLX");// 医嘱类型:1,2,3药品
					// 非新医嘱且没有设置过单停操作，不用提交后台
					if (r.get("TJZX") == 1
							|| (r.get("QRSJ") && r.get("QRSJ").length > 0)) {
						if (!r.dirty) {
							continue;
						} else {
							if (type == 0) {
								r.set("midifyYjzx", false);
							}
						}
					}
					if(r.get("ZFYP") == 1 &&r.get("YPLX") == 0&&r.get("JFBZ") != 3){
						MyMessageTip.msg("提示", "项目医嘱不能转成自备药!错误行 " + (i + 1)
								 + " 。", true);
						return false;
					}
					
//					 if(r.get("ZFYP") == 0 &&r.get("QRSJ")){
//						 MyMessageTip.msg("提示", "医嘱已执行不能转成自备药!错误行 " + (i + 1)
//						 + " 。", true);
//						 return false;
//					 }
					
					if (r.get("SIGN") == -1) {
						MyMessageTip.msg("提示", "药品数量超过允许范围!错误行 " + (i + 1)
										+ " 。", true);
						return false;
					}
					if (!r.get("YSGH")) {
						MyMessageTip.msg("提示",
								"开嘱医生不能为空!错误行 " + (i + 1) + " 。", true);
						return false;
					}
					// 有停嘱时间
					if (r.get("TZSJ") && !r.get("TZYS")) {
						MyMessageTip.msg("提示",
								"停嘱医生不能为空!错误行 " + (i + 1) + " 。", true);
						return false;
					}
					if ((!r.get("YYTS") && this.adviceType == 'longtime')
							|| r.get("YYTS") <= 0) {
						MyMessageTip.msg("提示", "请录入用药天数!错误行 " + (i + 1) + " 。",
								true);
						return false;
					}
					// 0:项目（其中记账标志为3的是行为医嘱）
					if (type > 0) {

						if (!r.get("YPXH")) {
							continue;
						}
						if (r.get("YCJL") <= 0) {
							MyMessageTip.msg("提示", "药品剂量不能为空且必须大于0!错误行 "
											+ (i + 1) + " 。", true);
							return false;
						}
						if (r.get("YCSL") <= 0) {
							MyMessageTip.msg("提示", "药品数量不能为空且必须大于0!错误行 "
											+ (i + 1) + " 。", true);
							return false;
						}
						if (!r.get("SYPC")) {
							MyMessageTip.msg("提示", "使用频次不能为空!错误行 " + (i + 1)
											+ " 。", true);
							return false;
						}
						if (r.get("LSYZ") != 1 && !r.get("SRCS")) {
							MyMessageTip.msg("提示", "首日次数不能为空!错误行 " + (i + 1)
											+ " 。", true);
							return false;
						}
						if (r.get("LSYZ") != 1 && r.get("SRCS") > r.get("MRCS")) {
							MyMessageTip.msg("提示", "首日次数不能大于频次的每日次数!错误行 "
											+ (i + 1) + " 。", true);
							return false;
						}
						if (!r.get("YPYF")) {
							MyMessageTip.msg("提示", "给药途径不能为空!错误行 " + (i + 1)
											+ " 。", true);
							return false;
						}
						// if (!r.get("FYFS")) {
						// MyMessageTip.msg("提示", "发药方式不能为空!错误行 " + (i + 1)
						// + " 。", true);
						// return false;
						// }
						// if (!r.get("YPDJ") || r.get("YPDJ") <= 0) {
						// MyMessageTip.msg("提示", "药品单价必须大于0!错误行 " + (i + 1)
						// + " 。", true);
						// return false;
						// }
						if (!r.get("YFSB") && r.get("ZFYP") != 1) {
							MyMessageTip.msg("提示", "发药药房不能为空!错误行 " + (i + 1)
											+ " 。", true);
							return false;
						}
						if (r.get("JLXH") > 0) {
							r.data['_opStatus'] = 'update';
						} else {
							r.data['_opStatus'] = 'create';
						}
						listData.push(r.data)
					} else if (type == 0) {
						if (r.get("JFBZ") != 3) {
							if (!r.get("YPXH")) {
								continue;
							}
							if (!r.get("SYPC")) {
								MyMessageTip.msg("提示", "使用频次不能为空!错误行 "
												+ (i + 1) + " 。", true);
								return false;
							}
							// if (r.get("LSYZ") != 1 && !r.get("SRCS")) {
							// MyMessageTip.msg("提示", "首日次数不能为空!错误行 "
							// + (i + 1) + " 。", true);
							// return false;
							// }
							if (!r.get("YPDJ") || r.get("YPDJ") <= 0) {
								MyMessageTip.msg("提示", "费用项目单价必须大于0!错误行 "
												+ (i + 1) + " 。", true);
								return false;
							}
							if (r.get("YCSL") <= 0) {
								MyMessageTip.msg("提示", "项目数量不能小于等于0!错误行 "
												+ (i + 1) + " 。", true);
								return false;
							}
						}
						if (r.get("JLXH") > 0) {
							r.data['_opStatus'] = 'update';
						} else {
							r.data['_opStatus'] = 'create';
						}
						listData.push(r.data)
					}
				}
				for (var i = 0; i < this.removeRecords.length; i++) {
					listData.push(this.removeRecords[i]);
				}
				// 判断是否
				if (listData[0]) {
					if (listData[0].LSYZ == 1) {
						var datas = [];
						for (var i = 0; i < listData.length; i++) {
							var r = listData[i];
							var data = {};
							if (r.YZMC) {
								data["FYMC"] = r.YZMC; // 费用名称
								// data["YLSL"] = r.MRCS * r.YCSL; // 数量
								// update by caijy for 家床里面总量就是总量 不需要乘以次数
								data["YLSL"]=r.YCSL;
								data["YLXH"] = r.YPXH; // 费用序号
								data["ZYH"] = r.ZYH; // 住院号
								data["QRSJ"] = r.QRSJ; // 确认时间
								if (r.JLXH) {
									data["JLXH"] = r.JLXH; // 住院号
								}
								datas[i] = data;
							}
						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicDisposalEntryService",
							serviceAction : "checkProjectMaterials",
							bodys : datas,
							mzzy : 1
								// 0是门诊 ，1 是住院
							});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg);
							return false;
						}
					}
				}
				// 特殊处理自备药字段
				for (var i = 0; i < listData.length; i++) {
					listData[i].ZFYP = listData[i].ZFYP == true ? 1 : 0
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveWardPatientInfo",
							body : {
								brxx : this.patinetData,
								yzxx : listData,
								ccxx : this.checkInfo
							}
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					// this.processReturnMsg(code, msg);
					MyMessageTip.msg("提示", msg, true);
					return false;
				}
				if(json.ccjl_sbxh && this.checkInfo) {
					this.opener.jcform.initDataId = json.ccjl_sbxh;
					this.opener.jcform.doLoadCcjl();
				}
				if (unrefresh) {
					this.removeRecords = [];
					this.store.rejectChanges();
					return true;
				}
				this.removeRecords = [];
				this.store.rejectChanges();
				this.refresh();
				return true;
			},
			srcsRender : function(value, metaData, r, row, col, store) {
				if (value == 0 || value == "") {
					return "";
				}
				return value;
			},
			numRenderer : function(value, metaData, r, row, col, store) {
				metaData.css = "x-grid3-col x-grid3-cell x-grid3-td-numberer x-grid3-cell-first";
				if (store.ctx.openBy != 'nurse' && r.get("CCJL")) { // 查床记录对应显示颜色
					if (store.ctx.opener.ccxh != r.get("CCJL")) {
						var tips = "开具：" + r.get("KSSJ") + " "
								+ r.get("YSGH_text")
						if (r.get("TZSJ")) {
							tips += "<br>停止：" + r.get("TZSJ") + " "
									+ r.get("TZYS_text");
						}
						tips = '<div class=\'ext-cell-tip\'>' + tips + '</div>';
						metaData.attr = 'ext:qtip="' + tips + '"';
						metaData.css = "x-grid3-col x-grid3-cell x-grid-record-pink x-grid3-cell-first";
					}

				}
				var num = row + 1// 序号
				var records = store.getRange(0, row);
				return num;
			},
			getCM : function(items) {
				var cm = []
				var fm = Ext.form
				var ac = util.Accredit;
				var _this = this;
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2) || it.noList
							|| it.hidden || !ac.canRead(it.acValue)) {
						continue
					}
					var width = parseInt(it.width
							|| (it.length < 80 ? 80 : it.length) || 80)
					var c = {
						id : it.id,
						header : it.alias,
						width : width,
						sortable : false,
						dataIndex : it.id,
						schemaItem : it
					}
					/** ******************** */
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					// add by yangl,modify simple code Generation methods
					if (it.codeType) {
						if (!this.CodeFieldSet)
							this.CodeFieldSet = [];
						this.CodeFieldSet.push([it.target, it.codeType, it.id]);
					}
					var editable = true;

					if ((it.pkey && it.generator == 'auto') || it.fixed) {
						editable = false
					}
					if (it.evalOnServer && ac.canRead(it.acValue)) {
						editable = false
					}
					var notNull = !(it['not-null'] == 'true')

					var editor = null;
					var dic = it.dic
					if (it.properties && it.properties.mode == "remote") {
						// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
						editor = this.createRemoteDicField(dic, it);
					} else if (dic) {
						dic.defaultValue = it.defaultValue
						dic.width = width
						dic.listWidth = it.listWidth;
						if (dic.render == "Radio" || dic.render == "Checkbox") {
							dic.render = ""
						}
						if (dic.fields) {
							if (typeof(dic.fields) == 'string') {
								var fieldsArray = dic.fields.split(",")
								dic.fields = fieldsArray;
							}
						}
						if (editable) {
							editor = this.createDicField(dic)
							if (it['not-null']) {
								editor.notNull = true;
							}
							editor.isDic = true
							if (Ext.isIE) {
								editor.on("select", function(f) {
											var sm = this.grid
													.getSelectionModel();
											var cell = sm.getSelectedCell()
											this.grid.walkCells(cell[0],
													cell[1] + 1, 1,
													sm.acceptsNav, sm);
										}, this);
							}
						}
						var _ctx = this
						c.isDic = true
						c.renderer = function(v, params, record, r, c, store) {
							var cm = _ctx.grid.getColumnModel()
							var f = cm.getDataIndex(c)
							return record.get(f + "_text")
						}
					} else {
						if (!editable) {
							if (it.type != "string" && it.type != "text"
									&& it.type != "date") {
								c.align = "right";
								c.css = "color:#00AA00;font-weight:bold;"
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								if (!c.renderer) {
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							cm.push(c);
							continue;
						}
						editor = new fm.TextField({
									allowBlank : notNull
								});
						var fm = Ext.form;
						switch (it.type) {
							case 'string' :
							case 'text' :
								var cfg = {
									allowBlank : notNull,
									maxLength : it.length
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								if (it.inputType) {
									cfg.inputType = it.inputType
								}
								editor = new fm.TextField(cfg)
								break;
							case 'date' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期",
									format : 'Y-m-d'
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.DateField(cfg)
								break;
							case 'datetime' :
							case 'datetimefield' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期"
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new phis.script.widgets.DateTimeField(cfg)
								break;
							case 'double' :
							case 'bigDecimal' :
							case 'int' :
								if (!it.dic) {
									c.css = "color:#00AA00;font-weight:bold;"
									c.align = "right"
									if (it.type == 'double'
											|| it.type == 'bigDecimal') {
										c.precision = it.precision;
										c.nullToValue = it.nullToValue;
										c.itemId = it.id;
										c.renderer = function(value, metaData,
												r, row, col, store) {
											if (r.get("JFBZ") == 3) {
												return "";
											}
											if (this.itemId == "YCJL") {
												if (value == 0 || value == "") {
													return "";
												}
											}
											if (value == null
													&& this.nullToValue) {
												value = parseFloat(this.nullToValue)
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												try {
													r
															.set(this.itemId,
																	retValue);
												} catch (e) {
													// 防止新增行报错
												}
												return retValue;
											}
											if (value != null) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												if (this.itemId == "YCSL") {
													if (r.get("SIGN") == -1) {
														return "<font color='red'>"
																+ retValue
																+ "</font>";
													}
												}
												return retValue;
											}
										}
									}
								}
								var cfg = {}
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.min) {
									cfg.minValue = it.min;
								} else {
									cfg.minValue = 0;
								}
								if (it.max) {
									cfg.maxValue = it.max;
								}
								cfg.allowBlank = notNull
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.NumberField(cfg)
								if (it.properties.xtype == "checkBox") {
									c.xtype = 'checkcolumn';
									editor = new Ext.ux.grid.CheckColumn();
									// editor.on("beforeedit",this.beforeCheckEdit,this)
								}
								break;
						}
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			},
			createRemoteDicField : function(dic) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrl
					// ,pageSize : this.pageSize || 25,
					// pageNo : 1
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonData
						});
				var mdsstore = new Ext.data.Store({
							proxy : proxy,
							reader : mds_reader
						});
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)

				this.remoteDicStore = mdsstore;
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>');
				var _ctx = this;
				var remoteField = new Ext.form.ComboBox({
							// id : "YPMC",
							width : 280,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : this.minListWidth || 280,
							tpl : resultTpl,
							minChars : 2,
							enableKeyEvents : true,
							lazyInit : false,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								// onSelect to do
								this.bySelect = true;
								_ctx.setBackInfo(this, record);
								// this.hasFocus = false;
							}
						});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keyup", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								var searchTypeValue = _ctx.cookie
										.getCookie(_ctx.mainApp.uid
												+ "_searchType");
								if (searchTypeValue != 'BHDM') {
									if (obj.isExpanded()) {
										if (key == 48 || key == 96)
											key = key + 10;
										key = key < 59 ? key - 49 : key - 97;
										var record = this.getStore().getAt(key);
										obj.bySelect = true;
										_ctx.setBackInfo(obj, record);
									}
								}
							}
							// 支持翻页
							if (key == 37) {
								obj.pageTb.movePrevious();
							} else if (key == 39) {
								obj.pageTb.moveNext();
							}
							// 删除事件 8
							if (key == 8) {
								if (obj.getValue().trim().length == 0) {
									if (obj.isExpanded()) {
										obj.collapse();
									}
								}
							}
						})
				if (remoteField.store) {
					remoteField.store.load = function(options) {
						Ext.apply(_ctx.comboJsonData, options.params);
						Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
						options = Ext.apply({}, options);
						this.storeOptions(options);
						if (this.sortInfo && this.remoteSort) {
							var pn = this.paramNames;
							options.params = Ext.apply({}, options.params);
							options.params[pn.sort] = this.sortInfo.field;
							options.params[pn.dir] = this.sortInfo.direction;
						}
						try {
							return this.execute('read', null, options); // <--
							// null
							// represents
							// rs. No rs for
							// load actions.
						} catch (e) {
							this.handleException(e);
							return false;
						}
					}
				}
				remoteField.isSearchField = true;
				remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							// 设置下拉框的分页信息
							// remoteField.pageTb.changePage(0);
							return this.beforeSearchQuery();
						}, this);
				// remoteField.store.on("load",function(store){
				// if(store.getCount() == 1) {
				// this.setBackInfo(remoteField,store.getAt(0));
				// }
				// },this);
				this.remoteDic = remoteField;
				return remoteField
			},
			// 根据天数,剂量和频次 计算总量
			setMedQuantity : function(record, fieldId) {
				var YYTS = record.get("YYTS");
				var YPJL = record.get("YPJL");
				var YCJL = record.get("YCJL");
				var SYPC = record.get("SYPC");
				var MRCS = record.get("MRCS");
				var YFBZ = record.get("YFBZ");
				var YYZL;
				if (fieldId == 'YCSL') {
					this.checkInventory(record);
					return;
				}
				// 项目 计算总量
				if (YYTS && MRCS && record.get("YPLX") == 0) {
					record.set("YCSL", MRCS * YYTS);
					return;
				}
				if (YYTS && YCJL && MRCS) {
					if (!YCJL || !YPJL) {
						YYZL = 0;
					} else {
						if (!YFBZ) {
							YFBZ = 1;
						}
						if (record.get("ZSSF") == 1
								&& (YCJL * 1000 % (YPJL * 1000 * YFBZ) != 0)) {// 乘数和被乘数都乘以1000，解决浮点数运算产生误差问题
							YCJL = this.accMul(Math.floor(this.accDiv(YCJL,
											this.accMul(YPJL, YFBZ)))
											+ 1, this.accMul(YPJL, YFBZ));
						}
						YYZL = Math.ceil(YCJL / (YPJL * YFBZ) * MRCS * YYTS);
						// YYZL = Math.ceil(this.accDiv(YCJL, this.accMul(YPJL,
						// YFBZ))
						// * MRCS * YYTS);
					}
					record.set("YCSL", YYZL);
					this.checkInventory(record)
					// 提示库存信息不足，光标定位
				}
			},
			accDiv : function(arg1, arg2) {
				var t1 = 0, t2 = 0, r1, r2;
				try {
					t1 = arg1.toString().split(".")[1].length
				} catch (e) {
				}
				try {
					t2 = arg2.toString().split(".")[1].length
				} catch (e) {
				}
				with (Math) {
					r1 = Number(arg1.toString().replace(".", ""))
					r2 = Number(arg2.toString().replace(".", ""))
					return (r1 / r2) * pow(10, t2 - t1);
				}
			},
			accMul : function(arg1, arg2) {
				var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
				try {
					m += s1.split(".")[1].length
				} catch (e) {
				}
				try {
					m += s2.split(".")[1].length
				} catch (e) {
				}
				return Number(s1.replace(".", ""))
						* Number(s2.replace(".", "")) / Math.pow(10, m)
			},
			beforeCheckEdit : function(record) {
				if (this.openBy != "nurse") {
					if (record.get("CCJL") != this.opener.ccxh) {
						// MyMessageTip.msg("提示", "自备药药品无法转成非自备药", true)
						return false;
					}
				}
				if(record.get("TJZX") > 0) {
					return false;
				}
				if (record.get("YSTJ") == 1) {
					// MyMessageTip.msg("提示", "项目无法转成自备药", true)
					return false;
				}
				return true;
			},
			onReady : function() {
				phis.application.fsb.script.FamilySickBedAdviceList.superclass.onReady
						.call(this);
				this.grid.on("beforeCheckedit", this.beforeCheckEdit, this)
				this.grid.on("afterCheckedit", this.afterCheckedit, this)
			},
			afterCheckedit : function(id, column, value) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				this.grid.startEditing(row, 4);
			}
		});

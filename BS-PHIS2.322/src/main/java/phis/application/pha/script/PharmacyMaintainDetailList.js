/**
 */
$package("phis.application.pha.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailList");

phis.application.pha.script.PharmacyMaintainDetailList = function(cfg) {
	// cfg.columnNum=3;//换行
	cfg.count = 9;
	cfg.toColumnNum = 9;// 换行到下一行的哪列
	cfg.labelText = " ";// 底部合计
	cfg.disablePagingTbr = false;
	this.editorData = [];
	this.mutiSelect = true;
	phis.application.pha.script.PharmacyMaintainDetailList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyMaintainDetailList,
		phis.application.sto.script.StorehouseMySimpleDetailList, {
			doPrint : function() {
				var records = this.getSelectedRecords();
				if (records.length == 0) {
					return;
				}

				// console.debug(records)
				var printRecords = [];
				var recordStr = '';
				for (var i = 0; i < records.length; i++) {
					// var record = {};
					// record["CGH"] = records[i].data.CGH;
					// record["YPMC"] = records[i].data.YPMC;
					// record["YPGG"] = records[i].data.YPGG;
					// record["YPDW"] = records[i].data.YPDW;
					// record["CDMC"] = records[i].data.CDMC;
					// record["PFJG"] = records[i].data.PFJG;
					// record["KCSL"] = records[i].data.KCSL;
					// record["YPPH"] = records[i].data.YPPH;
					// record["YPXQ"] = records[i].data.YPXQ;
					// record["FXWTYPJCL"] = records[i].data.FXWTYPJCL;
					// printRecords.push(record);
					if (i == 0) {
						recordStr = records[i].data.SBXH;
					} else {
						recordStr = recordStr + ',' + records[i].data.SBXH;
					}
				}

				var pages = "phis.prints.jrxml.PharmacyMaintain";
				var url = "resources/" + pages + ".print?silentPrint=1";
				url += "&records="
						+ encodeURI(encodeURI(Ext.encode(recordStr)));
				// + Ext.encode(printRecords);
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			},
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if (it.id == "SHSL") {
					if (parseFloat(v) > parseFloat(record.get("YKKC"))) {
						MyMessageTip.msg("提示", "破损数量不能大于库存总量", true);
						record.set("SHSL", parseFloat(record.get("YKKC")));
						record.set("KCSL", 0);
					} else {
						record.set("KCSL", parseFloat(record.get("YKKC"))
										- parseFloat(v));
					}
					var length = this.editorData.length;
					var b = false;
					for (i = 0; i < length; i++) {
						var yh02 = this.editorData[i];
						if (yh02.KCSB == record.get("KCSB")) {
							yh02.SHSL = record.get(SHSL);
							yh02.KCSL = record.get(KCSL);
							b = true;
							break;
						}
					}
					if (!b) {
						this.editorData.push(record.data);
					}
				}

			},
			loadData : function() {
				this.requestData.serviceId = this.fullServiceId;
				this.requestData.serviceAction = this.queryActionId;
				this.requestData.op = this.op;
				phis.application.pha.script.PharmacyMaintainDetailList.superclass.loadData
						.call(this);
			},
			// 新增
			doNew : function() {
				this.clear();
				this.editorData = [];
				this.requestData.body = {
					"YHDH" : 0
				};
				this.loadData();
			},
			doJshj : function() {
			},
			expansion : function(cfg) {
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				var count = store.getCount();
				for (var i = 0; i < count; i++) {
					var length = this.editorData.length;
					for (var j = 0; j < length; j++) {
						if (store.getAt(i).get("KCSB") == this.editorData[j].KCSB) {
							store.getAt(i).set("SHSL", this.editorData[j].SHSL);
							store.getAt(i).set("KCSL", this.editorData[j].KCSL);
							break;
						}
					}
				}
			},
			init : function() {
				this.addEvents({
							"select" : true
						})
				if (this.mutiSelect) {
					this.selectFirst = false
				}
				this.selects = {}
				this.singleSelect = {}
				phis.application.pha.script.PharmacyMaintainDetailList.superclass.init
						.call(this)
			},
			initPanel : function(schema) {
				return phis.application.pha.script.PharmacyMaintainDetailList.superclass.initPanel
						.call(this, schema)
			},
			onStoreLoadData : function(store, records, ops) {
				phis.application.pha.script.PharmacyMaintainDetailList.superclass.onStoreLoadData
						.call(this, store, records, ops)
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				var selRecords = []
				for (var id in this.selects) {
					var r = store.getById(id)
					if (r) {
						selRecords.push(r)
					}
				}
				this.grid.getSelectionModel().selectRecords(selRecords)

			},
			getCM : function(items) {
				var cm = phis.application.pha.script.PharmacyMaintainDetailList.superclass.getCM
						.call(this, items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : !this.mutiSelect
						})
				this.sm = sm
				var _ctx = this;
				this.sm.handleMouseDown = function(g, rowIndex, e) {
					if (e.button !== 0 || this.isLocked()) {
						return;
					}
					var view = this.grid.getView();
					if (e.shiftKey && !this.singleSelect && this.last !== false) {
						var last = this.last;
						this.selectRange(last, rowIndex, e.ctrlKey);
						this.last = last; // reset the last
						view.focusRow(rowIndex);
					} else {
						// add by yangl 选中行默认和checkbox选中的效果一致
						e.ctrlKey = _ctx.autoCtrl;
						var isSelected = this.isSelected(rowIndex);
						if (e.ctrlKey && isSelected) {
							this.deselectRow(rowIndex);
						} else if (!isSelected || this.getCount() > 1) {
							this.selectRow(rowIndex, e.ctrlKey || e.shiftKey);
							view.focusRow(rowIndex);
						}
					}
				}
				sm.on("rowselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
							} else {
								this.singleSelect = record
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
							} else {
								this.singleSelect = {}
							}
						}, this)
				return [sm].concat(cm)
			},
			onDblClick : function(grid, index, e) {
				this.doConfirmSelect()
			},
			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelections();
				var checker = Ext.fly(this.grid.getView().innerHd)
						.child('.x-grid3-hd-checker')
				if (checker) {
					checker.removeClass('x-grid3-hd-checker-on');
				}
				// Ext.fly(this.grid.getView().innerHd).child('.x-grid3-hd-checker').removeClass('x-grid3-hd-checker-on');
			},
			doConfirmSelect : function() {
				this.fireEvent("select", this.getSelectedRecords(), this)
				this.clearSelect();
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},
			doShowOnlySelected : function() {
				this.store.removeAll()
				var records = this.getSelectedRecords()
				this.store.add(records)
				this.grid.getSelectionModel().selectRecords(records)
			},
			getSelectedRecords : function() {
				var records = []
				if (this.mutiSelect) {
					for (var id in this.selects) {
						records.push(this.selects[id])
					}
				} else {
					records[0] = this.singleSelect
				}
				return records
			}
		});
$package("phis.application.mds.script")

/*
 * 药品私用信息导入module caijy 2012.4.13
 */
$import("phis.script.SimpleModule", "util.dictionary.TreeCheckDicFactory",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy",
		"util.dictionary.TreeDicFactory")
phis.application.mds.script.MedicinesImportModule = function(cfg) {
	cfg.mutiSelect = true
	cfg.westWidth = cfg.westWidth || 250
	cfg.gridDDGroup = 'gridDDGroup'
	cfg.showNav = true
	cfg.width = 1024;
	cfg.height = 500;
	cfg.mutiSelect = this.mutiSelect = true;
	cfg.autoLoadData = false;
	cfg.modal = true;
	cfg.ypdm = null;
	// cfg.rootVisible = true;// 设置根节点
	// cfg.parentText = "全部药品";// 根节点名称
	phis.application.mds.script.MedicinesImportModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)

}
Ext.extend(phis.application.mds.script.MedicinesImportModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (!this.showNav) {
					return grid
				}
				var tree = util.dictionary.TreeCheckDicFactory.createTree({
							id : this.navDic,
							checkModel : "cascade",
							rootVisible : false
						})
				this.panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : this.getTbar(),
							items : [{
										title : '机构列表',
										layout : "fit",
										split : true,
										collapsible : true,
										region : 'west',
										width : this.westWidth,
										width : 180,
										items : tree
									}, {
										title : '药品信息',
										layout : "fit",
										split : true,
										region : 'east',
										width : 450,
										items : this.getList()
									}, {
										title : '待调入药库',
										layout : "fit",
										split : true,
										region : 'center',
										items : this.getStoreHouseList()
									}]
						});
				this.tree = tree
				var actions = this.actions
				if (actions.length > 0) {
					this.tree.on("contextmenu", this.onContextMenu, this)
					this.tree.on('checkchange', this.getHouseStore, this);
				}
				this.tree.expandAll();
				return this.panel
			},
			// 罗列选中机构下的药库信息
			getHouseStore : function(node, checked) {
				var jgids = [];
				var checkedNodes = this.tree.getChecked();
				for (var i = 0; i < checkedNodes.length; i++) {
					var node = checkedNodes[i];
					if (node.attributes.type == "a"
							|| node.attributes.type == "b") {
						continue;
					}
					jgids.push(node.attributes.key);
				}
				if (jgids.length == 0) {
					jgids.push('-1');
				}
				this.hList.requestData.cnd = ['in', ['$', 'JGID'], jgids];
				this.hList.loadData.call(this.hList);
			},
			getList : function() {
				this.list = this.createModule("privateImplist", this.refList);
				// list.mutiSelect = true;
				this.list.on("medicinesPrivateImp", this.onMedicinesPrivateImp,
						this)
				return this.list.initPanel();
			},
			getStoreHouseList : function() {
				this.hList = this.createModule("houseStorelist",
						this.houseStoreList);
				this.hList.mutiSelect = true;
				this.hList.selectFirst = false;
				this.hList.requestData.cnd = ['in', ['$', 'JGID'], ['-1']];
				this.houseStore = this.hList.initPanel();
				this.houseStore.getSelectionModel().on("beforerowselect",
						this.singleUnitHouseSelect, this);
				return this.houseStore;
			},
			singleUnitHouseSelect : function(sm, rIndex, keepExisting, r) {
				var rs = sm.getSelections();
				var len = rs.length;
				var jgid = r.get('JGID');
				if (len > 0) {
					for (var i = 0; i < len; i++) {
						if (rs[i].get('JGID') == jgid) {
							var index = 0;
							var rows = [];
							var j = this.houseStore.getStore().each(
									function(rec) {
										if (rec.get('JGID') == jgid
												&& sm.isSelected(index)) {
											sm.deselectRow(index);
										}
										index++;
									});
						}
					}
				}
			},
			doNew : function() {

			},
			// 导入后调用
			onMedicinesPrivateImp : function() {
				this.fireEvent("save");
				this.doTimeQuery();
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var pym = new Ext.form.TextField({
							name : "pym",
							listeners : {
								specialkey : this.onFieldSpecialkey,
								scope : this
							}
						});
				var ypdmCombox = this.createDicField({
							id : "phis.dictionary.prescriptionType",
							width : 140,
							height : 25
						})
				ypdmCombox.on("click", this.onLeafClick, this);
				var simple = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							layout : "table",
							bodyStyle : 'padding:5px 5px 5px 5px',
							defaults : {},
							defaultType : 'textfield',
							items : [{
										xtype : "label",
										forId : "window",
										text : "新增时间从 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '新增日期',
												name : 'dateFrom',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												}
											}), {
										xtype : "label",
										forId : "window",
										text : "至"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '至',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												}
											}), {
										xtype : "label",
										forId : "window",
										text : "拼音码"
									}, pym, {
										xtype : "label",
										forId : "window",
										text : "药品类型 "
									}, ypdmCombox, {
										xtype : 'hidden',
										name : 'type'
									}]
						});
				this.simple = simple;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			// 药品类型树点击事件
			onLeafClick : function(node, e) {
				this.simple.items.get(7).setValue(node.attributes.key);
			},
			// 按时间和按药品编码和拼音码查询.不传参
			doTimeQuery : function() {
				var datefrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var pym = this.simple.items.get(5).getValue();
				var type = this.simple.items.get(7).getValue();
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge', ['$', "str(XZSJ,'yyyy-mm-dd')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le', ['$', "str(XZSJ,'yyyy-mm-dd')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(XZSJ,'yyyy-mm-dd')"],
									['s', datefrom]],
							['le', ['$', "str(XZSJ,'yyyy-mm-dd')"],
									['s', dateTo]]];
				}
				if (pym != null && pym != "") {
					pym = pym.toUpperCase();
				}
				if (this.list) {
					var cnd = this.list.initCnd;
					if (timeCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, timeCnd]
						} else {
							cnd = timeCnd;
						}
					}
					if (pym != null && pym != '') {
						if (cnd != null) {
							cnd = [
									'and',
									cnd,
									['like', ['$', 'a.PYDM'],
											['s', '%' + pym + '%']]]
						} else {
							cnd = ['like', ['$', 'a.PYDM'],
									['s', '%' + pym + '%']];
						}
					}
					if (type != null && type != '') {
						if (cnd != null) {
							cnd = ['and', cnd,
									['eq', ['$', 'a.TYPE'], ['s', type]]]
						} else {
							cnd = ['eq', ['$', 'a.TYPE'], ['s', type]];
						}
					}
					this.list.requestData.cnd = cnd;
					this.list.requestData.pageNo = 1;
					this.list.loading = true;
					this.list.loadData();
				}

			},
			// 调入
			doSave : function() {
				/*
				 * var jgids = []; var checkedNodes = this.tree.getChecked();
				 * for (var i = 0; i < checkedNodes.length; i++) { var node =
				 * checkedNodes[i]; if (node.attributes.type == "a" ||
				 * node.attributes.type == "b") { continue; }
				 * jgids.push(node.attributes.id); } // var jgids =
				 * this.tree.getChecked("id");//选中的机构 if (jgids.length < 1) {
				 * Ext.Msg.alert("提示", "未选中有效机构"); return; } var bodys = {};
				 * bodys["jgids"] = jgids;
				 */
				var store_records = this.houseStore.getSelectionModel()
						.getSelections();
				if (store_records.length == 0) {
					Ext.Msg.alert("提示", "未选择调入药库");
					return;
				}
				var yk_infos = [];
				for (var i = 0; i < store_records.length; i++) {
					var yk_info = {};
					yk_info['YKSB'] = store_records[i].get('YKSB');
					yk_info['JGID'] = store_records[i].get('JGID');
					yk_infos.push(yk_info);
				}
				var bodys = {};
				bodys["yk_infos"] = yk_infos;
				var records = this.list.getSelectedRecords();
				if (records.length == 0) {
					Ext.Msg.alert("提示", "未选中任何药品");
					return;
				}
				var yps = [];
				for (var i = 0; i < records.length; i++) {
					var yp = {};
					yp["YPXH"] = records[i].json.YPXH;
					yp["CFLX"] = records[i].json.TYPE;
					yps[i] = yp;
				}
				bodys["yps"] = yps;
				this.panel.el.mask("正在调入数据,时间可能很长,请耐心等待...", "x-mask-loading");
				var _ctr = this;
				var whatsthetime = function() {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveActionId,
								body : bodys
							});
					if (ret.code > 300) {
						_ctr.processReturnMsg(ret.code, ret.msg,
								_ctr.onBeforeSave);
						_ctr.panel.el.unmask();
						return;
					}
					_ctr.panel.el.unmask();
					Ext.Msg.alert("提示", "调入成功");
					_ctr.doTimeQuery();
				}
				whatsthetime.defer(1000);
			},
			doPrint : function() {
				if (this.list) {
					this.list.doPrint();
				}
			},
			onWinShow : function() {
				this.doTimeQuery();
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			onFieldSpecialkey : function(f, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					e.stopEvent()
					this.doTimeQuery();
				}
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"

				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			}
		});

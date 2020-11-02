$package("phis.application.sto.script")

/*
 * 药品私用信息导入module caijy 2012.4.13
 */
$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory")
phis.application.sto.script.StorehouseMedicinesPirvateImportModule = function(cfg) {
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
	cfg.rootVisible = true;// 设置根节点
	cfg.parentText = "全部药品";// 根节点名称
	phis.application.sto.script.StorehouseMedicinesPirvateImportModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)

}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateImportModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (!this.showNav) {
					return grid
				}
				var navDic = this.navDic
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							parentText : this.parentText,
							parentKey : "",
							rootVisible : this.rootVisible || false
						})

				this.panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : this.getTbar(),
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : this.westWidth,
										width : 180,
										items : tf.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										// width : 280,
										items : this.getList()
									}]
						});
				this.tree = tf.tree
				tf.tree.on("click", this.onCatalogChanage, this)
				return this.panel
			},
			onCatalogChanage : function(node, e) {
				if (this.parentText == node.text) {
					this.ypdm = "";
				} else {
					this.ypdm = node.id;
				}
				this.doTimeQuery();

			},
			getList : function() {
				this.list = this.createModule("privateImplist", this.refList);
				this.list.mutiSelect = true;
				return this.list.initPanel();
			},
			doNew : function() {

			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var pym=new Ext.form.TextField({
					name : "pym",
					listeners :{
					specialkey:this.onFieldSpecialkey,
					scope : this
					}
				});
				var ypdmCombox = util.dictionary.TreeDicFactory.createDic({
					id : "phis.dictionary.prescriptionType",
					width : 100
				});
				ypdmCombox.tree.on("click", this.onLeafClick, this);
				
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
				/*
				 * var queryButton = new Ext.Button({ text : '查询', iconCls :
				 * "query" }) queryButton.on("click", this.doTimeQuery, this);
				 * var confirmButton = new Ext.Button({ text : '确认', // handler :
				 * function(){this.doTimeQuery();}, iconCls : "common_select" })
				 * confirmButton.on("click", this.doSave, this); var printButton =
				 * new Ext.Button({ text : '打印', // handler :
				 * function(){this.doTimeQuery();}, iconCls : "common_select" })
				 * printButton.on("click", this.doPrint, this); var closeButton =
				 * new Ext.Button({ text : '关闭', id : 'close', iconCls :
				 * 'common_cancel' }) closeButton.on("click", this.doClose,
				 * this);
				 */
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			//药品类型树点击事件
			onLeafClick : function(node, e) {
				this.simple.items.get(8).setValue(node.attributes.key);
			},
			// 按时间和按药品编码和拼音码查询.不传参
			doTimeQuery : function() {
				var datefrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var pym = this.simple.items.get(5).getValue();
				var type = this.simple.items.get(8).getValue();
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
					var ypdm = this.ypdm;
					if (ypdm != null && ypdm != '') {
						if (cnd != null) {
							cnd = ['and', cnd,
									['like', ['$', 'YPDM'], ['s', ypdm + '%']]]
						} else {
							cnd = ['like', ['$', 'YPDM'], ['s', ypdm + '%']];
						}
					}
					if (pym != null && pym != '') {
						if (cnd != null) {
							cnd = [
									'and',
									cnd,
									['like', ['$', 'yp.PYDM'],
											['s', '%' + pym + '%']]]
						} else {
							cnd = ['like', ['$', 'yp.PYDM'],
									['s', '%' + pym + '%']];
						}
					}
					
					if(type != null && type != '') {
						if (cnd != null) {
							cnd = [
									'and',
									cnd,
									['eq', ['$', 'yp.TYPE'],
											['s', type]]]
						} else {
							cnd = ['eq', ['$', 'yp.TYPE'],
									['s', type]];
						}
					}
					this.list.requestData.cnd = cnd;
					this.list.requestData.pageNo = 1;
					this.list.requestData.serviceId = this.fullserviceId;
					this.list.requestData.serviceAction = this.listActionid;
					this.list.loading = true;
					this.list.loadData();
				}

			},
			// 多条记录导入
			doSave : function() {
				var records = this.list.getSelectedRecords();
				if (records.length == 0) {
					Ext.Msg.alert("提示", "未选中任何记录");
					return;
				}
				var bodys = [];
				for (var i = 0; i < records.length; i++) {
					var body = {};
					body["YPXH"] = records[i].json.YPXH;
					body["CFLX"] = records[i].json.CFLX;
					bodys[i] = body;
				}
				this.panel.el.mask("正在调入数据,时间可能很长,请耐心等待...", "x-mask-loading");
				var _ctr=this;
				var whatsthetime = function(){
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : _ctr.serviceId,
							serviceAction : _ctr.saveActionId,
							schema : _ctr.saveSchema,
							body : bodys
						});
				_ctr.panel.el.unmask();
				if (ret.code > 300) {
					_ctr.processReturnMsg(ret.code, ret.msg, _ctr.doSave);
					return;
				}
				_ctr.list.clearSelect();
				// Ext.Msg.alert("提示", "调入成功");
				MyMessageTip.msg("提示", "调入成功", true);
				_ctr.fireEvent("save");
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
			// 全部调入
			doAllImport : function() {
				Ext.Msg.show({
							title : "提示",
							msg : "确定全部调入?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.confirmToImport();
									// this.panel.el.mask(
									// "正在调入数据,时间可能很长,请耐心等待...",
									// "x-mask-loading");
									// var ret = phis.script.rmi.miniJsonRequestSync({
									// serviceId : this.serviceId,
									// serviceAction : this.saveAllActionId
									// });
									// this.panel.el.unmask();
									// if (ret.code > 300) {
									// this.processReturnMsg(ret.code,
									// ret.msg, this.doAllImport);
									// return;
									// }

								}
							},
							scope : this
						});

			},
			confirmToImport : function() {
				var _ctx = this;
				var body = {};
				Ext.MessageBox.show({
							title : '请等待',
							msg : '正在调入数据……',
							width : 240,
							progress : true,
							closable : false
						});
				var progressValue = 0;
				updateProgress();
				var itv = setInterval(updateProgress, 5000);
				function updateProgress() {
					phis.script.rmi.jsonRequest({
						serviceId : "medicinesPrivateManageService",
						serviceAction : "saveAllMedicinesPrivateImportInformation",
						body : body
					}, function(code, msg, json) {
						if (code > 300) {
							MyMessageTip.msg("提示", msg, true, 5);
							return
						} else {
							if (json.body) {
								progressValue = json.body;
								Ext.MessageBox.updateProgress(progressValue,
										"已完成" + parseInt(progressValue * 100)
												+ "%");
								if (json.finish == 1) {
									body.finish = 1;
									Ext.MessageBox.updateProgress(
											progressValue, "数据提交中...请稍后");
								}
								if (json.removeSuccess == 1) {
									clearInterval(itv);
									Ext.MessageBox.hide();
									_ctx.list.clearSelect();
									Ext.Msg.alert("提示", "调入成功");
									// MyMessageTip.msg("提示", "调入成功", true);
									_ctx.fireEvent("save");
									_ctx.doTimeQuery();
								}
							}
						}
					}, this)// jsonRequest
				}
			}
,
		onFieldSpecialkey : function(f, e) {
		var key = e.getKey()
		if (key == e.ENTER) {
			e.stopEvent()
			this.doTimeQuery();
		}
	}
		});

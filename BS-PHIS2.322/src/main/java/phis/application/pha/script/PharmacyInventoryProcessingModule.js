$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyInventoryProcessingModule = function(cfg) {
	cfg.exContext = {};
	phis.application.pha.script.PharmacyInventoryProcessingModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingModule,
		phis.script.SimpleModule, {
			// 页面初始化
			initPanel : function(sc) {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return null;
				}

				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'west',
										width : 120,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										items : this.getMod()

									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("rowClick", this.onClick, this);
				return this.list.initPanel();
			},
			// 获取右边的module
			getMod : function() {
				this.moudle = this.createModule("moudle", this.refMoudle);
				this.moudle.on("click", this.doXgsl, this);
				return this.moudle.initPanel();
			},
			// 日期选中后刷新右边记录
			onClick : function(r) {
				if (r == null) {
					this.moudle.doNew();
					return;
				}
				this.checkData = r;// 选中的记录
				var body = {};
				body["YFSB"] = r.YFSB;
				body["CKBH"] = r.CKBH;
				body["PDDH"] = r.PDDH;
				var pdwc = r.PDWC;
				var hzwc = r.HZWC;
				var btns = this.panel.getTopToolbar();
				var btn = btns.find("cmd", "hz");
				btn = btn[0];
				if (pdwc == 1) {
					this.setButtonsState(['remove', 'hz', 'wc', 'xgsl'], false);
					if (btn.getText().indexOf("取消") < 0) {
						btn.setText(btn.getText().replace("汇总", "取消汇总"));
					}
				} else if (hzwc == 1) {
					this.setButtonsState(['remove', 'hz', 'wc', 'xgsl'], true);
					if (btn.getText().indexOf("取消") < 0) {
						btn.setText(btn.getText().replace("汇总", "取消汇总"));
					}
				} else {
					this.setButtonsState(['wc', 'xgsl'], false);
					this.setButtonsState(['remove', 'hz'], true);
					if (btn.getText().indexOf("取消") > -1) {
						btn.setText(btn.getText().replace("取消汇总", "汇总"));
					}
				}
				this.panel.el.mask("正在查询...", "x-mask-loading");
				if (hzwc == 1 && pdwc == 0) {
					this.moudle.hzwc = 1;// 用于标识 能不能修改数量
				} else {
					this.moudle.hzwc = 0;
				}
				this.moudle.loadData(r);
				this.panel.el.unmask();
			},
			// 开始
			doKs : function() {
				this.panel.el.mask("正在正生成数据...");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId
						});
				// 如果有未确认的单子,打开提示界面
				if (ret.code > 300) {
					if (ret.code == 9000) {
						this.tab = this.createModule("tab", this.refTab);
						var win = this.tab.getWin();
						win.add(this.tab.initPanel());
						win.setHeight(500);
						win.setWidth(800);
						win.show();
						win.center();
						this.panel.el.unmask();
						return;
					}
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.list.refresh()
			},
			doRemove : function() {
				var r = this.list.getSelectedRecord()
				if (r == null) {
					return
				}
				var body = {};
				body["YFSB"] = r.data.YFSB;
				body["PDDH"] = r.data.PDDH;
				body["CKBH"] = r.data.CKBH;
				Ext.Msg.show({
							title : '确认删除记录[' + r.data.PDDH + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.panel.el.mask("正在删除...");
									var ret = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.removeActionId,
												body : body
											});
									this.panel.el.unmask();
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doSave);
										return;
									}
									this.list.refresh()
								}
							},
							scope : this
						})
			},
			doHz : function() {
				if (this.moudle.underModule.leftList.store.getCount() < 3) {
					MyMessageTip.msg("提示", "没人录入,不能汇总", true);
					return;
				}
				var tag = 1;// 用于标识是取消汇总 还是汇总
				var btns = this.panel.getTopToolbar();
				var btn = btns.find("cmd", "hz");
				btn = btn[0];
				if (btn.getText().indexOf("取消") > -1) {
					tag = 0;
				}
				var body = {};
				body["TAG"] = tag;
				body["YFSB"] = this.checkData.YFSB;
				body["CKBH"] = this.checkData.CKBH;
				body["PDDH"] = this.checkData.PDDH;
				this.panel.el.mask("正在保存...");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.hzActionId,
							body : body
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doHz);
					this.list.refresh();
					return;
				}
				this.list.refresh();
			},
			doWc : function() {
				if (!this.checkData) {
					Ext.Msg.alert("提示", "没有选中记录");
					return false;
				}
				//完成前增加数据校验
				this.panel.el.mask("正在校验数据...");
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.wcqActionId,
							body : this.checkData
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doWc);
					this.list.refresh();
					return;
				}
				this.wcForm = this.createModule("wcForm", this.refWcForm);
				this.wcForm.on("save", this.onWc, this);
				var win = this.wcForm.getWin();
				win.add(this.wcForm.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.wcForm.loadData(this.checkData);
				}
			},
			onWc : function() {
				this.panel.el.mask("正在保存...");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.wcActionId,
							body : this.checkData
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doWc);
					this.list.refresh();
					return;
				}
				this.list.refresh();
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			doXgsl : function() {
				if (this.checkData.PDWC == 1
						|| (this.checkData.HZWC == 0 && this.checkData.PDWC == 0)) {
					return;
				}
				var initData = this.moudle.getXGSLdata();
				if (initData == null) {
					return
				}
				this.xgModule = this.createModule("Xgslmodule",
						this.refXGMoudle);
				this.xgModule.on("save", this.onSave, this);
				// this.xgModule.initPanel();
				var win = this.xgModule.getWin();
				win.add(this.xgModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.xgModule.loadData(initData);
				}
			},
			onSave : function() {
				this.list.refresh();
			},
			doPrint : function() {
				var yfsb = this.mainApp['phis'].pharmacyId;
				var pdrq = this.list.getSelectedRecord().get("PDRQ");
				var pddh = this.list.getSelectedRecord().get("PDDH");
				var pdr=this.moudle.form.data["CZGH"].key;
				this.printurl = util.helper.Helper.getUrl();
				//var url = this.printurl+ "*.print?pages=phis.prints.jrxml.MedicinalCheck&silentPrint=1&yfsb=" + yfsb + "&pdrq=" + pdrq + "&pddh=" + pddh+"&pdr="+pdr;
                var pages="phis.prints.jrxml.MedicinalCheck";
				var url="resources/"+pages+".print?silentPrint="+1+"&yfsb=" + yfsb + "&pdrq=" + pdrq.replace(' ','_') + "&pddh=" + pddh+"&pdr="+pdr;
				if(this.flag)
					{
					url+="&flag=1";
					}
					//window.open(url)
					var rehtm=util.rmi.loadXML({
													url : url,
													httpMethod : "get"
												});
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();
			},
			//导出 zhaojian 2017-10-10 
			doExcel : function() {
				var yfsb = this.mainApp['phis'].pharmacyId;
				var pdrq = this.list.getSelectedRecord().get("PDRQ");
				var pddh = this.list.getSelectedRecord().get("PDDH");
				var pdr=this.moudle.form.data["CZGH"].key;
				this.printurl = util.helper.Helper.getUrl();
				var pages="phis.prints.jrxml.MedicinalCheck";
				var url="resources/"+pages+".print?type=3&silentPrint="+1+"&yfsb=" + yfsb + "&pdrq=" + pdrq.replace(' ','_') + "&pddh=" + pddh+"&pdr="+pdr;
				if(this.flag)
				{
					url+="&flag=1";
				}
				var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			//自动校准
			doZdjz:function(){
				if (!this.checkData) {
					Ext.Msg.alert("提示", "没有选中记录");
					return false;
				}
				Ext.Msg.show({
							title : "提示",
							msg : "自动校准将改变实盘数量,是否继续?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
							this.panel.el.mask("正在自动校准...");
							var ret=phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.jzActionId,
								body : this.checkData
							});
							this.panel.el.unmask();
							this.list.refresh();
							if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg, this.doWc);
							return;
							}
								}
							},
							scope : this
						});
			}
		});
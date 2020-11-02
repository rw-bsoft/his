$package("phis.application.spt.script");

$import("phis.script.SimpleModule");

phis.application.spt.script.SptMedicinesCompareModule = function(cfg) {
	this.exContext = {};
	phis.application.spt.script.SptMedicinesCompareModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.spt.script.SptMedicinesCompareModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										title : "医院药品目录",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 680,
										items : [this.getMainList()]
									}, {
										title : "省平台药品目录",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getAppendList()
									}]
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
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
			getMainList : function() {
				this.mainList = this.createModule("mainList", this.refyyypmlList);
				var _ctx = this;
				this.mainList.onRowClick = function() {
					_ctx.onListRowClick();
				};
				this.mainList.on("loadData", this.onListLoadData, this);
				return this.mainList.initPanel();
			},
			getAppendList : function() {
				this.appendList = this.createModule("appendList",
						this.refnhypmlList);
				var _ctx=this;
				this.appendList.onDblClick = function() {
					_ctx.onnhypmlListRowDblClick();
				};
				return this.appendList.initPanel();
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认选中第一行
				this.mainList.selectRow(0);
				this.onListRowClick();
			},
			onListRowClick : function() {
				this.beforeclose();
				var r = this.mainList.getSelectedRecord();
				if (!r) {
					return;
				}
				this.appendList.requestData.cnd =  
						['like', ['$', 'a.NAME'], ['s', r.get("YPMC")]]
				this.appendList.loadData();
			},
			onnhypmlListRowDblClick : function(){
				var yyml_r=this.mainList.getSelectedRecord();
				if((yyml_r.get("ZBBM") !=null)&&(yyml_r.get("ZBBM") !="") ){
					alert("药品"+yyml_r.get("YPMC")+"已对照！不能再次对照！");
					return;
				}
				var sptyyml_r=this.appendList.getSelectedRecord();
				
				var body={};
				body.YY_YPXH=yyml_r.get("YPXH");
				body.SPT_YPXH=sptyyml_r.get("UNICODE");
				body.YY_YPCD=yyml_r.get("YPCD");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.SptService",
							serviceAction : "updatezbbm",
							body:body
						});
				if(ret.json.errormsg !=""){
					alert(ret.json.errormsg);
                    return;
				}
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.mainList.refresh());
				} else {
					this.mainList.refresh();
				}
				
			},
			beforeclose : function() {
				
			},
			// 关闭
			doClose : function() {

			}
		});

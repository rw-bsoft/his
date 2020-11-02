$package("phis.application.xnh.script");

$import("phis.script.SimpleModule");

phis.application.xnh.script.NhClinicItemsCompareModule = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhClinicItemsCompareModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhClinicItemsCompareModule,
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
										title : "医院诊疗目录",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 680,
										items : [this.getMainList()]
									}, {
										title : "农合诊疗目录",
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
						['like', ['$', 'a.FYMC'], ['s', r.get("FYMC")]]
				this.appendList.loadData();
			},
			onnhypmlListRowDblClick : function(){
				var yyml_r=this.mainList.getSelectedRecord();
				if(yyml_r.get("NHBM_SH")=="1"){
					alert("费用"+yyml_r.get("FYMC")+"农合已审核！不能再次匹配！");
					return;
				}
				var nhml_r=this.appendList.getSelectedRecord();
				
				var body={};
				body.YY_FYXH=yyml_r.get("FYXH");
				body.NH_FYXH=nhml_r.get("FYXH");
				body.type="2";//费用对照
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "updatenhbm",
							body:body
						});
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

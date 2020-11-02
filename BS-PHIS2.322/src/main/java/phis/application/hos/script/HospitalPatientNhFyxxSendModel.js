$package("phis.application.hos.script");

$import("phis.script.SimpleModule");

phis.application.hos.script.HospitalPatientNhFyxxSendModel = function(cfg) {
	phis.application.hos.script.HospitalPatientNhFyxxSendModel.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.hos.script.HospitalPatientNhFyxxSendModel,
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
										title : "病人列表",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 480,
										items : [this.getMainList()]
									}, {
										title : "费用列表",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getAppendList()
									}],
							tbar : (this.tbar || []).concat(this.createButton())
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var me=this;
				var resend = new Ext.form.Checkbox({   
							id : "resendbox",   
							name : "resendbox",   
							autoScroll : false,   
							width : 60,
							boxLabel : "费用重传",   
							anchor : "90%",
							style : "padding-left:10px;font-size:15px;",
							hideLabel : false,   
							listeners : { "check" : function(obj,ischecked){
									me.resendcheck=ischecked;
									me.onListRowClick()
							}}  
							});
				me.resend=resend;	
				buttons.push(resend);
				
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			
//			createButton : function() {
//				var me=this;
//				var startDate=new Ext.form.DateField({
//					margins:{left:20},
//					value:new Date(),
//					name : 'startDate',
//					width : 100,
//					//allowBlank : false,
//					altFormats : 'Y-m-d',
//					format : 'Y-m-d',
//					emptyText : '开始时间'
//				});
//				me.startDate=startDate;
//				// 定义结束时间
//				var endDate=new Ext.form.DateField({
//					value:new Date(),
//					name : 'endDate',
//					width : 100,
//					hight:20,
//					//allowBlank : false,
//					altFormats : 'Y-m-d',
//					format : 'Y-m-d',
//					emptyText : '结束时间'
//					});
//				me.endDate=endDate;
//				var queryBtn = new Ext.Toolbar.Button({
//					
//					iconCls : "query",
//					text : "查询",
//					handler : me.doQuery,
//					scope:me
//				});
//				var resend = new Ext.form.Checkbox({   
//							id : "resendbox",   
//							name : "resendbox",   
//							autoScroll : false,   
//							width : 50,
//							boxLabel : "重传",   
//							anchor : "90%",
//							style : "padding-left:10px;font-size:15px;",
//							hideLabel : false,   
//							listeners : { "check" : function(obj,ischecked){
//							alert(ischecked)
//								
//							}}  
//							});
//				me.resend=resend;
//				return ['开始日期：', startDate,'结束时间:',endDate,'',resend,queryBtn]
//			},
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
				this.mainList = this.createModule("mainList", this.zybrTab);
				var _ctx = this;
				this.mainList.onRowClick = function() {
					_ctx.onListRowClick();
				};
				this.mainList.on("loadData", this.onListLoadData, this);
				return this.mainList.initPanel();
			},
			getAppendList : function() {
				this.appendList = this.createModule("appendList",
						this.dscxxTab);
				var _ctx=this;
				this.appendList.onDblClick = function() {
					_ctx.onnhypmlListRowDblClick();
				};
				return this.appendList.initPanel();
			},
			onListRowClick : function() {
				this.beforeclose();
				var rs = this.mainList.getSelectedRecords();
				if (!rs && rs.length <1) {
					return;
				}
				var xzzhy="";
				for(var i=0;i<rs.length;i++){
					if(i==0){
						xzzhy=rs[i].get("ZYH");
					}else {
						xzzhy=xzzhy+"','"+rs[i].get("ZYH")
					}
				}
				if(this.resendcheck){
					this.appendList.requestData.cnd = ['in', ['$', 'a.ZYH'], [xzzhy]]
				}else{
					this.appendList.requestData.cnd = ['and',['in', ['$', 'a.ZYH'], [xzzhy]],
												      	['or',['$', 'a.NHSCBZ is null'],
												      	['eq',['$','a.NHSCBZ'],['s','0']]]
												  	] 
				}									
				this.appendList.loadData();
			},
			//上传费用
			doSend: function() {
				var store = this.appendList.grid.getStore();
				if (store.getCount() == 0) {
					Ext.Msg.alert("提示", "当前没有可上传费用!");
					return;
				}
				var body={};
				var n = store.getCount();
				var fyxxdata = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					fyxxdata.push(r.data);
				}
				body.fyxxdata=fyxxdata;
				body.resendcheck=this.resendcheck;
				var maincheck=this.mainList.getSelectedRecords();
				if (!maincheck || maincheck.length == 0) {
					Ext.Msg.alert("提示", "当前没有可上传费用!");
					return;
				}
				var zyxxdata = [];
				for (var j = 0; j < maincheck.length; j++) {
					zyxxdata.push(maincheck[j].data);
				}
				body.zyxxdata=zyxxdata;	
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.xnhService",
						serviceAction : "sendzyfymx",
						body:body
					});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.appendList.refresh());
				} else {
					this.resendcheck= false;
					document.getElementById("resendbox").checked = false;
					this.onListRowClick();
				}
			},
			beforeclose : function() {
				
			},
			// 关闭
			doClose : function() {

			}
			
		});

$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HosCancelCommitModule = function(cfg) {
	phis.application.hos.script.HosCancelCommitModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HosCancelCommitModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				this.width = 300;
				this.height = 350;
				var panel = new Ext.Panel({
//							border : false,
							width : this.width,
							height : this.height,
							buttonAlign : 'center',
//							frame : true,
							layout : 'border',
//							defaults : {
//								border : false
//							},
							items : [{
//										layout : "fit",
										border : false,
//										split : true,
										region : 'center',
										title : '取消的结账日报',
										items : this.getList()
									}, {
//										layout : "fit",
										border : false,
//										split : true,
										region : 'north',
										height : 110,
										items : this.getForm()
									}],
							buttons : [{
								cmd : 'commit',
								text : '确定',
								handler : this.doCommit,
								iconCls : "commit",
								scope : this
							}, {
								text : '取消',
								handler : this.doConcel,
								iconCls : "common_cancel",
								scope : this
							}]
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
			},
			doCommit : function(){
				var rs = this.list.grid.getSelectionModel().getSelected();
				if(!rs)return;
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "cancelCommit",
					JZRQ : rs.data.JZRQ
				});
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
					return
				}
				Ext.MessageBox.alert("提示", "取消日报成功!");
				this.list.loadData();
				return;
			},
			loadDate : function(){
//				this.list.requestData.method = "queryCancelCommit";
				this.list.requestData.serviceAction = "queryCancelCommit";
				this.list.loadData();
			},
			doConcel : function() {
				win = this.getWin();
				if (win)
					win.hide();
			},
			getList : function() {
				var module = this.createModule("refPaymentProcessingList",
						this.refList);

				this.list = module;
				module.listMethod = "queryCancelCommit";
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this.createModule("refPaymentProcessingForm",
						this.refForm);
				module.opener = this;
				var formModule = module.initPanel();
				this.form = module;
				return formModule;
			}
		});
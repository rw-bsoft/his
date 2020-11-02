$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleModule")

com.bsoft.phis.checkapply.CheckApplyModule_CIC2 = function(cfg) {
	com.bsoft.phis.checkapply.CheckApplyModule_CIC2.superclass.constructor.apply(this,
			[cfg])
	this.zdxh = [];
	this.zdmc = [];
	this.jcbw = [];
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyModule_CIC2,
		com.bsoft.phis.SimpleModule, {
			initPanel : function() {
				var r = util.rmi.miniJsonRequestSync({
							serviceId : "hisGetPatientDiagnoseRecordService",
							serviceAction : "getPatientDiagnose",
							body : {
								jgid : this.mainApp.deptId,
								brid : this.exContext.ids.brid,
								jzxh : this.exContext.ids.clinicId
							}
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					this.zdxh = eval(r.json).ZDXH;
					this.zdmc = eval(r.json).ZDMC;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										id:"checkApplyForm2",
										layout : "fit",
										border : true,
										split : true,
										region : 'north',
										height : 170,
										items : this.getCheckApplyForm()
									}, {
										id:"checkApplyList2",
										layout : "fit",
										border : true,
										split : true,
										region : 'center',
										items : this.getCheckApplyList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getCheckApplyForm : function() {
				this.checkApplyForm = this.createModule(
						"checkApplyForm", this.refCheckApplyForm);
				this.checkApplyForm.opener = this;
				this.checkApplyFormGrid = this.checkApplyForm.initPanel();
				//传递值
				this.checkApplyForm.brxm = this.exContext.empiData.personName
				this.checkApplyForm.zdmc = this.zdmc;
				this.checkApplyForm.zdys = this.mainApp.uname;
				this.checkApplyForm.init();//初始化赋值
				return this.checkApplyFormGrid;
			},
			getCheckApplyList : function() {
				this.checkApplyList = this.createModule(
						"checkApplyList", this.refCheckApplyList);
				this.checkApplyList.opener = this;
				this.checkApplyGrid = this.checkApplyList.initPanel();
				return this.checkApplyGrid;
			},
			getFormTbar : function(){
				var tbar = [];
				tbar.push({
							xtype : "button",
							text : "病情描述模板",
							iconCls : "new",
							scope : this,
							handler : this.doBQMSModel
				});
				return tbar;
			},
			doBQMSModel : function(){
				var module = this.createModule("checkApplyBQMSMBModule",this.refCheckApplyBQMSMBModule);
				if (module) {
					var win = module.getWin();
					win.setWidth(800);
					win.setHeight(400);
					win.show();
					module.opener = this;
				}
			}
		})

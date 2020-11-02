$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyModule_WAR = function(cfg) {
	phis.application.ccl.script.CheckApplyModule_WAR.superclass.constructor.apply(this,
			[cfg])
	this.zdxh = [];
	this.zdmc = [];
	this.jcbw = [];
}
Ext.extend(phis.application.ccl.script.CheckApplyModule_WAR,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : true,
										split : true,
										region : 'north',
										//height : "30%",
										height : 180,
										items : this.getCheckApplyForm()
//										tbar : this.getFormTbar()
									}, {
										layout : "fit",
										border : false,
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
				this.checkApplyForm.zrys = this.exContext.empiData.ZSYS_text;//this.kdInfo.zyh;
				this.checkApplyForm.zyh = this.exContext.empiData.ZYH;//this.kdInfo.zyh;
				this.checkApplyForm.brxm = this.exContext.empiData.BRXM;//this.kdInfo.brxm;
				this.checkApplyForm.jbmc = this.exContext.empiData.JBMC;//this.kdInfo.jbmc;
				//this.checkApplyForm.zrys = this.exContext.empiData.ZZYS;//this.kdInfo.zrys;
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

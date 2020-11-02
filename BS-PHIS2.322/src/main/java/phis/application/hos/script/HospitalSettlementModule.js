$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalSettlementModule = function(cfg) {
	phis.application.hos.script.HospitalSettlementModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalSettlementModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
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
										region : 'west',
										width : 300,
										collapsible : true,
										items : this.getSettlementList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getSettlementModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getSettlementList : function() {
				var module = this.createModule("refSettlementList",
						this.refList);
				module.on("dblClick", this.doDblClick, this);
				this.list = module;
				module.opener = this;
				var list = module.initPanel();
				module.schema = 'ZY_BRRY_CYLB_ZC';
				module.requestData.cnd = ['and',
						['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'a.CYPB'], ['i', 1]]];
				module.refresh();
				return list
			},
			getSettlementModule : function() {
				var module = this.createModule("refSettlementModule",
						this.refModule);
				module.on("settlement", this.settlement, this);
				this.module = module;
				module.opener = this;
				return module.initPanel();
			},
			doDblClick : function(data) {
				//yx-新农合不允许中途结算-开始
				if(data.BRXZ==6000 && this.JSLX==1){
					MyMessageTip.msg("提示", "新农合不允许中途结算！", true);
					return;
				}
				//yx-新农合不允许中途结算-结束
				if(data.BRXZ==6000){
					//农合改变自付比例
					var changedata={}
					changedata.ZYH=data.ZYH;
					changedata.BRXZ=data.BRXZ;
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "hospitalPatientSelectionService",
						serviceAction : "changezfbl",
						data : changedata
					});
				}
				var form = this.module.form.form.getForm();
				if(!data.JSBS){
					if (this.JSLX) {
						data.JSBS = data.JSLX;//将该病人结算类型传值到后台
						data.JSLX = this.JSLX
					} else {
						data.JSBS = data.JSLX;
						data.JSLX = 5;
					}
				}
				this.doCommit(data);
			},
			doCommit : function(data) {
				if (!data) {
					return;
				}
				this.module.doFillIn(data);
			},
			doQuery : function(data) {
				this.list.doQuery(data)
			},
			settlement : function(){
				this.list.doRefresh()
			},
			doNew : function(){
				this.module.doNew(this.JSLX);
			}
		});
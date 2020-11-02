$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyInventoryProcessingXGSLModule = function(cfg) {
	cfg.width = 700;
	cfg.height = 550;
	this.modal = this.modal = true;
	this.exContext={}
	phis.application.pha.script.PharmacyInventoryProcessingXGSLModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingXGSLModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
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
										region : 'north',
										width : 960,
										height : 129,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			// 保存方法
			doSave : function() {
				var count =this.list.store.getCount();
				var pd02=new Array();
				for(var i=0;i<count;i++){
				pd02.push(this.list.store.getAt(i).data);
				}
				var body={};
				body["PD02"]=pd02;
				body["sbxh"]=this.sbxh
				this.panel.el.mask("正在保存...");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				this.fireEvent("save",this);
				this.doCancel();
			},
			doCancel : function() {
				this.getWin().hide();
			},
			loadData:function(initData){
			this.sbxh=initData.SBXH;
			this.form.doNew();
			this.form.initFormData(initData);
			//这里暂时先按批次盘点,以后改
			if((initData.KCSB==0||initData.KCSB==null||initData.KCSB==undefined)&&initData.PQSL!=0){
			this.list.requestData.cnd=["and",['eq',['$','YFSB'],['l',initData.YFSB]],['eq',['$','PDDH'],['d',initData.PDDH]],['eq',['$','YPXH'],['d',initData.YPXH]],['eq',['$','YPCD'],['d',initData.YPCD]]];
			}else{
				this.list.requestData.cnd=["and",['eq',['$','YFSB'],['l',initData.YFSB]],['eq',['$','PDDH'],['d',initData.PDDH]],['eq',['$','KCSB'],['d',initData.KCSB]]];
				if(initData.KCSB==0){//新增数据
				if(initData.YPPH!=null&&initData.YPPH!=""&&initData.YPPH!=undefined){
				this.list.requestData.cnd.push(['eq',['$','YPPH'],['s',initData.YPPH]])
				}
				if(initData.YPXQ!=null&&initData.YPXQ!=""&&initData.YPXQ!=undefined){
				this.list.requestData.cnd.push(['eq',['$',"str(YPXQ,'yyyy-mm-dd')"],['s',initData.YPXQ]])
				}
				}
			}
			this.list.loadData();
			}
		});
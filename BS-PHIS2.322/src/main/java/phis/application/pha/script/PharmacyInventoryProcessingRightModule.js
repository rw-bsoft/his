$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyInventoryProcessingRightModule = function(cfg) {
	cfg.width=this.width =window.screen.width/2;
	phis.application.pha.script.PharmacyInventoryProcessingRightModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingRightModule,phis.script.SimpleModule,
		{
			//页面初始化
			initPanel : function(sc) {
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
										region : 'north',
										width : 800,
										height : 48,
										items : this.getTopForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										width : 800,
										items : this.getUnderModule()
										
									}]
						});
				this.panel = panel;
				return panel;
			},
			//获取左边的list
			getTopForm : function() {
				this.form = this.createModule(
						"form", this.refForm);
				return this.form.initPanel();
			},
			//获取右边的module
			getUnderModule : function() {
				this.underModule = this.createModule("underModule",
						this.refModule);
				this.underModule.on("click",this.onClick,this);		
				return this.underModule.initPanel();
			},
			loadData:function(r){
			this.form.initDataBody=r;
			this.form.loadData();
			this.underModule.loadData(r);
			},
			doNew:function(){
			this.form.doNew();
			this.underModule.doNew();
			},
			getXGSLdata:function(){
			return this.underModule.getXGSLdata();
			},
			onClick:function(){
			this.fireEvent("click",this);
			}
		});
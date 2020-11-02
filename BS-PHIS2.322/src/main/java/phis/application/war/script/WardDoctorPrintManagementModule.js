$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.WardDoctorPrintManagementModule = function(cfg) {
	this.noDefaultBtnKey=true;
	phis.application.war.script.WardDoctorPrintManagementModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.war.script.WardDoctorPrintManagementModule,
		phis.script.SimpleModule, {
		initPanel : function(sc) {
				if (this.panel) {
					return this.panel;
				}	
				var schema = sc
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
										title :"",
										region : 'west',
										width:280,
										items : this.getLList()
									}, {
										layout : "fit",
										ddGroup : "secondGrid",
										border : false,
										split : true,
										region : 'center',
										items : this.getRModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getLList:function(){
			this.leftList = this.createModule("leftList", this.refLeftList);
			this.leftList.on("rowClick",this.onLeftClick,this) 
			this.leftList.on("noRecord",this.onNoRecord,this) 
				return this.leftList.initPanel();
			},
			getRModule:function(){
			this.rightModule = this.createModule("rightModule",this.rightModule);
			return this.rightModule.initPanel();
			},
			//左边List选中刷新右边
			onLeftClick:function(zyh){
			this.rightModule.zyh=zyh;
			if(this.rightModule.yzzt==2){
				this.rightModule.radiogroup.setValue(1);
			}
			else{
			this.rightModule.loadData();}
			},
			onNoRecord:function(){
			this.rightModule.doNew();
			}
		})
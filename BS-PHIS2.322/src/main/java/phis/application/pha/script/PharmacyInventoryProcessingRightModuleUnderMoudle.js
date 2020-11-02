$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudle = function(cfg) {
	phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudle.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudle,phis.script.SimpleModule,
		{
			// 页面初始化
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
										region : 'west',
										width:200,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										items : this.getRightList()
										
									}]
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getLeftList : function() {
				this.leftList = this.createModule(
						"leftList", this.refLeftList);
				return this.leftList.initPanel();
			},
			// 获取右边的list
			getRightList : function() {
				var ref=this.refRightList;
				var kcpd_pc=this.getKCPD_PC();
				if(kcpd_pc==0){
				ref=this.refRightList_pc;
				}
				this.rightList = this.createModule("rightList",
						ref);
				this.rightList.kcpd_pc=kcpd_pc;
				this.rightList.on("click",this.onClick,this);		
				return this.rightList.initPanel();
			},
			getKCPD_PC:function(){
				if(this.KCPD_PC!=undefined){
				return this.KCPD_PC;
				}
					var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryKCPD_PCAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "查询系统参数失败", this.getKCPD_PC);
					return null;
				}
				this.KCPD_PC=ret.json.body;
				return ret.json.body;
			},
			loadData:function(r){
			this.leftList.requestData.serviceId=this.fullserviceId;
			this.leftList.requestData.serviceAction=this.queryStateActionId;
			this.leftList.requestData.body=r;
			this.leftList.loadData();
			this.rightList.requestData.cnd=['and',['eq',['$','a.YFSB'],['l',r.YFSB]],['eq',['$','a.CKBH'],['i',r.CKBH]],['eq',['$','a.PDDH'],['i',r.PDDH]]];
			this.rightList.initCnd=['and',['eq',['$','a.YFSB'],['l',r.YFSB]],['eq',['$','a.CKBH'],['i',r.CKBH]],['eq',['$','a.PDDH'],['i',r.PDDH]]];
			this.rightList.loadData();
			},
			doNew:function(){
			this.leftList.clear();
			//this.rightList.clearSelect();
			this.rightList.clear();
			},
			//获取list选中记录,修改数量用
			getXGSLdata:function(){
			var r=this.rightList.getSelectedRecord();
			if(r==null){
			return null;
			}
			this.checkData=r;
			return r.data;
			},
			onClick:function(){
			this.fireEvent("click",this);
			}
		});
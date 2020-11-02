/**
 * 医技项目退回
 * 
 * @author caijy
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.MedicalTechnologyProjectReturnRightModule = function(cfg) {
	phis.application.war.script.MedicalTechnologyProjectReturnRightModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.MedicalTechnologyProjectReturnRightModule,
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
										title : '',
										region : 'north',
										height : 260,
										items : this.getTopList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getUnderList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getTopList : function() {
				this.topList = this.createModule("topList", this.refTopList);
				this.topList.on("noRecord",this.onNoRecord,this)
				this.topList.on("topSelect",this.onTopSelect,this)
				return this.topList.initPanel();
			},
			getUnderList : function() {
				this.underList = this.createModule("underList", this.refUnderList);
				this.underList.on("jzwc",this.onJzwc,this)
				return this.underList.initPanel();
			},
			//加载完成,取消页面锁定
			onJzwc:function(){
			this.fireEvent("jzwc",this);
			},
			onNoRecord:function(){
			this.underList.clear();
			this.fireEvent("jzwc",this);
			},
			//上面选定刷新下面数据
			onTopSelect:function(r){
			var cnd=['eq',['$','a.YJXH'],['l',r.get("YJXH")]];
			this.underList.requestData.cnd=cnd;
			this.underList.loadData();
			},
			doNew:function(){
			this.topList.clear();
			this.underList.clear();
			}
		});
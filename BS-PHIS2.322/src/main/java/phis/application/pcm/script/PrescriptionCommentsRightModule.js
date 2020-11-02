/**
 * 处方点评-最外面的module
 * 
 * @author caijy
 */
$package("phis.application.pcm.script");

$import("phis.script.SimpleModule");

phis.application.pcm.script.PrescriptionCommentsRightModule = function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsRightModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModule,phis.script.SimpleModule,
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
										region : 'center',
										items : this.getLModule()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'east',
										width:370,
										items : this.getRModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			//获取左边的module
			getLModule : function() {
				this.leftMoudle = this.createModule(
						"leftMoudle", this.refLMoudle);
				this.leftMoudle.on("cymxClick",this.onCymxClick,this)
				this.leftMoudle.on("noRecord",this.onNoRecord,this)
				this.leftMoudle.on("afterLoadData",this.onAfterLoadData,this)
				this.leftMoudle.on("changeButtonState",this.onChangeButtonState,this)
				return this.leftMoudle.initPanel();
			},
			//获取右边的module
			getRModule : function() {
				this.rightModule = this.createModule("rightModule",
						this.refModule);
				this.rightModule.on("hl",this.onHl,this);
				this.rightModule.on("bhl",this.onBhl,this);
				return this.rightModule.initPanel();
			},
			//list记录加载后调用
			onAfterLoadData:function(){
			this.fireEvent("afterLoadData",this);
			},
			//刷新tab数据
			loadData:function(cyxh,cywc){
			this.cywc=cywc;
			this.leftMoudle.cyxh=cyxh;
			this.leftMoudle.loadData();
			},
			//刷新打印界面
			onCymxClick:function(cfsb,dpbz,cflx){
			this.panel.el.mask("正在加载中.....", "x-mask-loading")
			this.rightModule.cfsb=cfsb;
			//this.rightModule.dpbz=dpbz;
			this.rightModule.cywc=this.cywc;
			if(this.leftMoudle.tab.activeTab.id == "dpjg"){
			this.rightModule.cywc=1;
			}
			this.rightModule.cflx=cflx;
			this.rightModule.doPrint();
			var _ctr=this;
			var whatsthetime = function() {
								_ctr.panel.el.unmask();
							}
			whatsthetime.defer(500);
//			this.panel.el.unmask();
			},
			//清空数据
			doNew:function(){
			this.rightModule.doNew();
			this.leftMoudle.doNew();
			},
			//明细无记录 刷新打印
			onNoRecord:function(){
			this.rightModule.doNew();
			},
			//合理
			onHl:function(){
			this.leftMoudle.doHloBhl(1);
			},
			//不合理
			onBhl:function(wtdms){
			this.leftMoudle.doHloBhl(2,wtdms);
			},
			onChangeButtonState:function(tabid){
			this.fireEvent("changeButtonState",tabid);
			}
		});
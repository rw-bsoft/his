/**
 * 处方点评-右边tab第一个module
 * 
 * @author caijy
 */
$package("phis.application.pcm.script");

$import("phis.script.SimpleModule");

phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModule = function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModule,phis.script.SimpleModule,
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
										height : 85,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			//获取上面的form
			getForm : function() {
				this.form = this.createModule(
						"form", this.refForm);
				return this.form.initPanel();
			},
			//获取下面的List
			getList : function() {
				this.list = this.createModule("list",
						this.refList);
				this.list.on("cymxClick",this.onCymxClick,this);
				this.list.on("noRecord",this.onNoRecord,this);
				this.list.on("loadData",this.onLoadData,this);
				return this.list.initPanel();
			},
			loadData:function(){
			this.form.initDataId=this.cyxh;
			this.form.loadData();
			this.list.requestData.cnd=['eq',['$','a.CYXH'],['l',this.cyxh]];
			this.list.loadData();
			},
			//list数据刷完后抛出
			onLoadData:function(){
			this.fireEvent("save",this);
			},
			onCymxClick:function(cfsb,dpbz,cflx){
			this.opener.loadDY(cfsb,dpbz,cflx);
			//this.fireEvent("cymxClick",cfsb,dpbz);
			},
			doNew:function(){
			this.cyxh=0;
			this.form.doNew();
			this.list.clear();
			},
			onNoRecord:function(){
			this.opener.clearDY();
			},
			getDpxh:function(){
			var r=this.list.getSelectedRecord();
			if(r==null){
			MyMessageTip.msg("提示","未选择记录",true);
			return null;
			}
			return r.get("DPXH");
			},
			//是否有点评记录
			getSfdp:function(){
			var store=this.list.store;
			var count=store.getCount() ;
			for(var i=0;i<count;i++){
			if(store.getAt(i).get("DPBZ")==1){
			return true;
			}
			}
			return false;
			},
			//是否全部点评
			getSfqbdp:function(){
			var store=this.list.store;
			var count=store.getCount() ;
			for(var i=0;i<count;i++){
			if(store.getAt(i).get("DPBZ")==0){
			return false;
			}
			}
			return true;
			}
		});
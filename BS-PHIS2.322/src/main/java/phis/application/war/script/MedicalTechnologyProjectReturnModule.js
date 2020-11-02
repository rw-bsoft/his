/**
 * 医技项目退回
 * 
 * @author caijy
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.MedicalTechnologyProjectReturnModule = function(cfg) {
	phis.application.war.script.MedicalTechnologyProjectReturnModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.MedicalTechnologyProjectReturnModule,
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
										//title : this.title,
										region : 'west',
										width : 230,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getMo()
									}],
							tbar : this.getTbar()
						});
				this.panel = panel;
				return panel;
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("noRecord",this.onNoRecord,this);
				this.list.on("selectRecord",this.onLeftSelect,this);
				return this.list.initPanel();
			},
			getMo : function() {
				this.module = this.createModule("module", this.refModule);
				this.module.on("jzwc",this.onJzwc,this)
				return this.module.initPanel();
			},
			getTbar:function(){
			var tbar=new Ext.Toolbar();
			var label=new Ext.form.Label({"text":"住院号码:"})
			this.ZYHMText=new Ext.form.TextField();
			this.ZYHMText.on("specialkey",this.doRefresh,this);
			tbar.add(label);
			tbar.add(this.ZYHMText)
			tbar.add(this.createButtons());
			return tbar;
			},
			//打开界面后刷新
			afterOpen : function() {
				if(!this.list||!this.module){return;}
				this.doRefresh();
			},
			//刷新
			doRefresh:function(){
			this.panel.el.mask("正在加载...", "x-mask-loading");
			this.list.loadData();
			},
			//加载完成取消页面锁定
			onJzwc:function(){
			this.panel.el.unmask();
			},
			//没有科室记录
			onNoRecord:function(){
			this.module.doNew();
			this.panel.el.unmask();
			},
			//左边选中记录刷新右边记录
			onLeftSelect:function(r){
			var body={};
			var zyhm=this.ZYHMText.getValue();
			if(zyhm&&zyhm!=null&&zyhm!=""){
			body["ZYHM"]=zyhm
			}
			body["KSDM"]=r.get("KSDM");
			this.module.topList.requestData.body=body;
			this.module.topList.loadData();
			},
			doTh:function(){
				var r=this.module.topList.getSelectedRecord();
				if(!r){
				return;}
			this.panel.el.mask("正在退回...", "x-mask-loading");
			var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : {"YJXH":r.get("YJXH")}
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					this.doRefresh();
					return;
				}
				this.doRefresh();
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "退回成功!", true);
			}
		});
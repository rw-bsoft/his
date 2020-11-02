$package("phis.application.war.script");
$import("app.biz.BizCombinedModule2");

phis.application.war.script.ConsultationApplyModule = function(cfg) {
	phis.application.war.script.ConsultationApplyModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemCollapsible = false;
	this.width=500;
	this.height=230;
	this.on("loadData",this.onLoadData,this);
};

Ext.extend(phis.application.war.script.ConsultationApplyModule,app.biz.BizCombinedModule2,{
	initPanel : function(sc) {
		if (this.panel) {
					return this.panel;
		}
		var items = this.getPanelItems();
		var panel = new Ext.Panel({
					border : false,
					split : true,
					hideBorders : true,
					frame : this.frame || true,
					layout : 'border',
					width : this.width || 600,
					height : this.height || 300,
					buttonAlign : "left",
					tbar : [{
								text : "确定",
								iconCls : "save",
								handler : this.doSave,
								scope : this
							}, {
								text : "取消",
								iconCls : "common_cancel",
								handler : function() {
									this.win.hide();
								},
								scope : this
							}],
					items : items
			});
		panel.on("afterrender", this.onReady, this);
		this.panel = panel;

		this.form = this.midiModules[this.actions[0].id];
		this.form.on("saveHyperRecord",this.onHyperRecordSave,this);
		this.list = this.midiModules[this.actions[1].id];
		return panel;
	},
	
	getLoadRequest : function() {
		var body = {
			ZYH:this.ZYH
		};
		return body;
	},
	
	onLoadData : function(entryName,body){
		
	},
	
	doSave : function(){
		var r = this.list.getSelectedRecord();
		if(!r){
			Ext.Msg.alert("提示","请在右边列表中选择一个病区！");
			return;
		}
		var ksdm = this.form.getKSDM();
		var targetBQ = r.get("KSDM");
		if(ksdm == targetBQ){
			Ext.Msg.alert("提示","同一病区不用会诊");
			return;
		}
		var zyh = this.form.getZYH();
		var saveData = {};
		saveData.ZYH = zyh;
		saveData.HZKS = targetBQ;
		this.op = "update";
		this.saveToServer(saveData);
	}
	
	
});
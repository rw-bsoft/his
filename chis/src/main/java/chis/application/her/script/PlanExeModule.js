$package("chis.application.her.script")

$import("chis.script.BizCombinedModule2")

chis.application.her.script.PlanExeModule = function(cfg) {
	cfg.layOutRegion = "north";
	cfg.moduleWidth =Ext.getBody().getWidth()-308;
	cfg.itemWidth = 500; // ** 第一个Item的宽度
	cfg.itemHeight = 72;
	cfg.itemCollapsible = false;
	cfg.autoLoadData = false;
	cfg.modal = true;
    cfg.frame = true;
	chis.application.her.script.PlanExeModule.superclass.constructor.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.her.script.PlanExeModule, chis.script.BizCombinedModule2, {
	initPanel : function() {
		var panel = chis.application.her.script.PlanExeModule.superclass.initPanel.call(this);
			this.panel = panel;
		this.form = this.midiModules[this.actions[0].id];
		this.list = this.midiModules[this.actions[1].id];
		return panel;
	},

	getLoadRequest : function() {
		return {
			exeId : this.initDataId
		};
	},
	
	onLoadData : function(entryName,body){
		if(body){
			this.exContext.args = {};
			this.exContext.args.exeId = body["chis.application.her.schemas.HER_EducationPlanExe_data"].exeId;
			this.exContext.args.setId = body["chis.application.her.schemas.HER_EducationPlanExe_data"].setId;
			this.list.requestData.exeId = this.exContext.args.exeId;
			if(this.list){
				Ext.apply(this.list.exContext,this.exContext);
				if (body["chis.application.her.schemas.HER_EducationRecord_actions"]) {
					this.list.exContext.control = body["chis.application.her.schemas.HER_EducationRecord_actions"];
					this.list.resetButtons();
				}
			}
		}
	},

	onWinShow : function() {
		
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						title : this.title,
						width : this.moduleWidth,
						height : this.height,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						layout : "form",
						plain : true,
						autoScroll : false,
						minimizable : false,
						maximizable : false,
						shadow : false,
						modal : this.modal,
						constrain : true,
						buttonAlign : 'center',
						items : this.initPanel()
					})
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win
	}
})
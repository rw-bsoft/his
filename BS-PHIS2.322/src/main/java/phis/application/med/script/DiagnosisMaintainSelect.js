$package("phis.application.med.script");
$import("phis.script.SimpleList","phis.script.widgets.MyMessageTip");
phis.application.med.script.DiagnosisMaintainSelect = function(cfg) {
	cfg.autoWidth =true;
	cfg.autoLoadData = true;
	cfg.disablePagingTbr = true;
	phis.application.med.script.DiagnosisMaintainSelect.superclass.constructor.apply(this, [cfg]);
},
Ext.extend(phis.application.med.script.DiagnosisMaintainSelect,phis.script.SimpleList, { 
	onDblClick : function(){
		var r = this.getSelectedRecord();
		zdid = r.get("ZDID"); 
		this.fireEvent("zddmReturn",zdid);
    },
	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.name,
						width : 315,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrain : true,
						resizable : false,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : false,
						maximizable : false,
						shadow : false,
						modal : this.modal || true,
						items : this.initPanel()
					});
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this);
			win.on("beforeshow", function() {
						this.fireEvent("beforeWinShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		return win;
	},
	// 加载数据时增加机构和科室过滤
	loadData : function() {
		/** 2013-06-20 gejj 修改bug1784 加载数据时增加机构和科室过滤
		 *  修改描述:增加了整个loadData方法
		 * */
		this.requestData.cnd = ['and',
				['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
				['eq', ['$', 'KSDM'], ['d', this.mainApp['phis'].MedicalId]]]
		phis.application.med.script.DiagnosisMaintainSelect.superclass.loadData
				.call(this);
	}
});
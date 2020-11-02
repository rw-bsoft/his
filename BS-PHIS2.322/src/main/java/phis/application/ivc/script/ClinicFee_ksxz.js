$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.ClinicFee_ksxz = function(cfg) {
	cfg.width=150;
	cfg.modal=true;
	cfg.title="科室选择"
	cfg.height=250
	phis.application.ivc.script.ClinicFee_ksxz.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ivc.script.ClinicFee_ksxz, phis.script.SimpleList, {
		doCommit:function(){
		var record=this.getSelectedRecord();\
		debugger;
		if(record==null){
		MyMessageTip.msg("提示","请先选择科室",true);
		return;
		}
//		var ksdms=new Array();
//		var length=records.length;
//		for(var i=0;i<length;i++){
//		ksdms.push(records[i].data.KSDM);
//		}
		this.fireEvent("commit",record.get("JZXH"));
		this.getWin().hide();
		},
		doCancel:function(){
		this.getWin().hide();
		this.fireEvent("cancel",this)
		},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false,
						closable :false,
						minimizable :false,
						maximizable :false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			},
			onDblClick:function(grid,index,e){
			this.doCommit();
			}

})
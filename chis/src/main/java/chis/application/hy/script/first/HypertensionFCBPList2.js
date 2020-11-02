// @@ 就诊记录列表。
$package("chis.application.hy.script.first");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.first.HypertensionFCBPList2 = function(cfg) {
	this.initCnd = ["eq", ["$", "a.empiId"], ["s", cfg.exContext.ids.empiId]];
	chis.application.hy.script.first.HypertensionFCBPList2.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.first.HypertensionFCBPList2,
		chis.script.BizSimpleListView, {
getWin: function(){
		var win = this.win
		var closeAction = "close"
		if(!this.mainApp || this.closeAction){
			closeAction = "hide"
		}
		if(!win){
			win = new Ext.Window({
		        title: this.title || this.name||"首诊测压查询",
//		        width: this.width,
		        width: document.body.scrollWidth ,
		        height:document.body.scrollHeight ,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        closeAction:closeAction,
		        constrainHeader:true,
			    constrain : true,
		        minimizable: true,
		        maximizable: true,
		        shadow:false,
		        modal :this.modal || false // add by huangpf.
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			win.on("show", function() {
				this.fireEvent("winShow")
			}, this)
			win.on("add",function(){
				this.win.doLayout()
			},this)
			win.on("close",function(){
				this.fireEvent("close",this)
			}, this)
			win.on("hide", function() {
				this.fireEvent("hide", this)
			},this)
			this.win = win
		}
		return win;
	}
		});
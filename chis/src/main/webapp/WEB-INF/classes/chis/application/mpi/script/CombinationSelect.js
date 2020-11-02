/**
 * 个人基本信息列表
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizSimpleListView");

chis.application.mpi.script.CombinationSelect = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.actions = [{
				id : "select",
				name : "选择",
				iconCls : "common_select"
			}, {
				id : "close",
				name : "取消",
				iconCls : "common_cancel"
			}];
	cfg.showButtonOnTop = true;
	chis.application.mpi.script.CombinationSelect.superclass.constructor.apply(this, [cfg]);
	this.on("hide", this.closeWin, this);
}

Ext.extend(chis.application.mpi.script.CombinationSelect, chis.script.BizSimpleListView,{
	setRecords : function(records) {	
		this.store.removeAll();
		this.store.add(records);
	},
	
	closeWin : function() {
		this.getWin().hide()
	},
	
	doClose : function() {
		this.fireEvent("hide", this);
	},
	
	doSelect : function() {
		var record = this.getSelectedRecord();
		if (!record){
			return;
		}
		this.fireEvent("onSelect", record);
		this.getWin().hide();
	},
	
	onDblClick : function(grid, index, e) {
		this.doSelect();
	},
	
	getWin: function(){
		var win = this.win
		var closeAction = "close"
		if(!this.mainApp || this.closeAction){
			closeAction = "hide"
		}
		if(!win){
			win = new Ext.Window({
		        title: this.title || this.name,
		        width: this.width,
		        height:this.height,
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
		win.instance = this;
		return win;
	}
});

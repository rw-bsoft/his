$package("chis.application.tr.script.inspectionItem");

$import("chis.script.BizTableFormView");


chis.application.tr.script.inspectionItem.TumourInspectionItemInputForm = function(cfg){
	cfg.colCount=2;
	chis.application.tr.script.inspectionItem.TumourInspectionItemInputForm.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.inspectionItem.TumourInspectionItemInputForm,chis.script.BizTableFormView,{
	getWin: function(){
		var win = this.win
		if(!win){
			win = new Ext.Window({
		        title: this.title || this.name,
		        width: this.width,
		        autoHeight:this.autoHeight || true,
		        iconCls: 'icon-form',
		        bodyBorder:false,
		        closeAction:'hide',
		        shim:true,
		        layout:"fit",
		        plain:true,
		        autoScroll:false,
		        //minimizable: true,
		        maximizable: true,
		        constrain : true,
		        shadow:false,
		        buttonAlign:'center',
		        modal:true
            })
//            win.on({
//            	beforehide:this.confirmSave,
//            	beforeclose:this.confirmSave,
//            	scope:this
//            })
		    win.on("show",function(){
		    	this.fireEvent("winShow")
		    },this)
		    win.on("close",function(){
				this.fireEvent("close",this)
			},this)
		    win.on("hide",function(){
				this.fireEvent("close",this)
			},this)
			win.on("restore",function(w){
				this.form.onBodyResize()
				this.form.doLayout()				
			    this.win.doLayout()
			},this)
			
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			this.win = win
		}
        this.afterCreateWin(win);
		return win;
	}
});

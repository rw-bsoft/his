$package("chis.application.tr.script.screening")

$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.query.QueryModule")

chis.application.tr.script.screening.TumourScreeningCheckResultSee = function(cfg){
	cfg.actions = [{id:"cancel",name:"取消",iconCls:"common_cancel"}]
	chis.application.tr.script.screening.TumourScreeningCheckResultSee.superclass.constructor.apply(this,[cfg]);
	this.on("beforeCreate",this.onBeforeCreate,this);
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningCheckResultSee,chis.script.BizTableFormView,{
	onBeforeCreate : function(){
		this.data.screeningId = this.exContext.args.screeningId;
		this.data.empiId = this.exContext.args.empiId;
	},
	onReady : function(){
		var frm = this.form.getForm();
		var checkItemFld = frm.findField("checkItem");
		if(checkItemFld){
			//checkItemFld.on("lookup", this.doInspectionItemNameQuery, this);
			//checkItemFld.on("clear", this.doNew, this);
			checkItemFld.validate();
		}
		chis.application.tr.script.screening.TumourScreeningCheckResultSee.superclass.onReady.call(this);
	},
	doInspectionItemNameQuery : function(field){
		if (!field.disabled) {
			var checkItemQuery = this.midiModules["checkItemQuery"];
			if (!checkItemQuery) {
				checkItemQuery = new chis.script.util.query.QueryModule(
						{
							title : "筛查项目查询",
							autoLoadSchema : true,
							isCombined : true,
							autoLoadData : false,
							mutiSelect : false,
							queryCndsType : "1",
							entryName : "chis.application.tr.schemas.MDC_TumourInspectionItemCommonQuery",
							buttonIndex : 3,
							itemHeight : 125
						});
				this.midiModules["checkItemQuery"] = checkItemQuery;
			}
			checkItemQuery.on("recordSelected", function(r) {
						if (!r) {
							return;
						}
						var frmData = r[0].data;
						this.setInspectionItem(frmData);
					}, this);
			var win = checkItemQuery.getWin();
			win.setPosition(250, 100);
			win.show();
			var highRiskType = this.exContext.args.highRiskType;
			if(highRiskType){
				var itemTypeFld = checkItemQuery.form.form.getForm().findField("itemType");
				if(itemTypeFld){
					itemTypeFld.setValue({key:highRiskType,text:this.exContext.args.highRiskType_text});
					itemTypeFld.disable();
				}
			}
		}
	},
	setInspectionItem : function(frmData){
		var inspectionItem = frmData.itemId;
		var inspectionItemName = frmData.definiteItemName;
		this.data.itemId = inspectionItem;
		var frm = this.form.getForm();
		var checkItemFld = frm.findField("checkItem");
		if(checkItemFld){
			checkItemFld.setValue(inspectionItemName);
		}
	},
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
$package("phis.application.gp.script")

$import("phis.script.SelectList")

phis.application.gp.script.GpSelectList = function(cfg) {
    cfg.autoLoadData = true;
    // cfg.autoLoadSchema = false;
    // cfg.disablePagingTbr = false;
    cfg.entryName = this.entryName || "phis.application.gp.schemas.SCM_INCREASEITEMSDETIL";
    phis.application.gp.script.GpSelectList.superclass.constructor.apply(this,
        [cfg]);
    this.on("winShow",this.loadData,this)
    this.on("winShow",this.clearSelect,this)
}

Ext.extend(phis.application.gp.script.GpSelectList, phis.script.SelectList, {

    /**
     * 确定时传入选中的减免项目
     */
    doCommit:function(){
        var records = this.getSelectedRecords()
        this.opener.GPRecords = records;
        this.opener.gpselected = true;
        this.getWin().hide();
        this.fireEvent("gpDone",this.opener.data_bak);
    },
    /**
     * 取消时不传入任何的减免信息
     */
    doCancel:function(){
        this.opener.GPRecords = null;
        for(var i=0 ; i < this.opener.data_bak.length ; i++){
            this.opener.data_bak[i].ZFBL = 1;
        }
        this.opener.gpselected = true;
        this.getWin().hide();
        this.fireEvent("gpDone",this.opener.data_bak);
    } ,
	showColor : function(value, metaData, r, row, col) {
		if(value==undefined){
			return "";
		}
		return "<font style='color:red;font-weight:bold'>"+value+"</font>";
	},
    loadData : function() {
        this.requestData.serviceId = this.serviceId;
        this.requestData.serviceAction = this.serviceAction;
        phis.application.gp.script.GpSelectList.superclass.loadData.call(this);
    },
	onStoreLoadData : function(store, records, ops) {
		if (!this.store) {
			return;
		}
		var griddata = this.grid.store.data;
		this.grid.selModel.selectAll();
		for (var i = 0; i < records.length; i++) {
			if (records[i].data.SERVICETIMES >=0 &&  records[i].data.TOTSERVICETIMES >0) {
				records[i].set("SERVICETIMES",records[i].data.TOTSERVICETIMES-records[i].data.SERVICETIMES);
			}
		}
	}
});
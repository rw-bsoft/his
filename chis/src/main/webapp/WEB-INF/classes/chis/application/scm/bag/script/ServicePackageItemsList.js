$package("chis.application.scm.bag.script")
$import("chis.script.BizEditorListView")

chis.application.scm.bag.script.ServicePackageItemsList=function(cfg){
	chis.application.scm.bag.script.ServicePackageItemsList.superclass.constructor.apply(this,[cfg]);
    this.on("loadData",this.onLoadData,this);
    this.on("beforeCellEdit",this.onBeforeCellEdit,this);
    this.on("afterCellEdit",this.onAfterCellEdit,this);
}

Ext.extend(chis.application.scm.bag.script.ServicePackageItemsList,chis.script.BizEditorListView,{
    loadData : function () {
        this.requestData.cnd=['eq',['$','a.SPID'],['s',this.SPID]];
        chis.application.scm.bag.script.ServicePackageItemsList.superclass.loadData.call(this);
    },
    onStoreLoadData:function(store,records,ops){
        this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
        if(records.length == 0){
            return
        }
        this.totalCount = store.getTotalCount()
        if (!this.selectedIndex || this.selectedIndex >= records.length) {
            this.selectRow(0)
            this.selectedIndex = 0;
        }
        else{
            this.selectRow(this.selectedIndex);
        }
        var girdcount = 0;
        store.each(function(r) {
            this.grid.getView().getCell(girdcount,4).style.backgroundColor = '#CCCCCC';
            girdcount += 1;
        }, this);
    },
    onLoadData : function (store) {
        //var rn = store.getCount();
        var toolBar = this.grid.getTopToolbar();
        if(toolBar){
            var smBtn = toolBar.find("cmd", "saveModifyRecords");
            if(smBtn && smBtn.length > 0){
                smBtn[0].disable();
            }
        }
    },
    onBeforeCellEdit : function(it,record,field,value){
        if(it.readOnly=="true"){
            return true;
        }else{
            return false;
        }
    },
    onAfterCellEdit : function (it,record,field,v) {
        if(field && field.isValid()){
            var toolBar = this.grid.getTopToolbar();
            if(toolBar){
                var smBtn = toolBar.find("cmd", "saveModifyRecords");
                if(smBtn && smBtn.length > 0){
                    smBtn[0].enable();
                }
            }
        }
    },
	doCreateItem : function(){
		var SPID = this.SPID;
		var STQuery = this.midiModules["ST_Query"];
		if (!STQuery) {
			$import("chis.script.util.query.QueryRecordsModule2");
			STQuery = new chis.script.util.query.QueryRecordsModule2(
					{
						title : "新增服务项目",
						autoLoadSchema : true,
						isCombined : true,
						autoLoadData : false,
						mutiSelect : true,
						queryCndsType : "1",
						entryName : "chis.application.scm.schemas.SCM_ServiceItemsQuery",
						detailEntryName : "chis.application.scm.schemas.SCM_ServiceItems",
						queryField :"SPID",
						SPID : this.SPID,
						buttonIndex : 3,
						width : 900,
						itemHeight : 150
					});
			this.midiModules["ST_Query"] = STQuery;
		}
		STQuery.on("reset",function(form){
		},this);
		STQuery.opener = this;
		var win = STQuery.getWin();
		win.setPosition(250, 100);
		win.show();
		STQuery.loadData();
	},
    doSaveModifyRecords : function () {
        var rds = this.store.getModifiedRecords();
        var stList = [];
        for (var i = 0, len = rds.length; i < len; i++) {
            var rData = rds[i].data;
            var st = {};
            st.SPIID = rData.SPIID;
            st.serviceTimes = rData.serviceTimes;
            stList.push(st);
        }
        util.rmi.jsonRequest({
                serviceId: "chis.signContractRecordService",
                method: "execute",
                serviceAction: "updateServiceTimes",
                body: stList
            }, function (code, msg, json) {
                if (code > 300) {
                    return
                }
                MyMessageTip.msg("提示", "数据保存成功！", true);
            },
            this);
    },
	onDblClick : function(grid, index, e) {
		this.doModify();
	}
});
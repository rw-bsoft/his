$package("chis.application.scm.item.script")
// $import("chis.script.BizEditorListView")
$import("chis.script.BizSimpleListView")

chis.application.scm.item.script.HisServicePackageItemsListLeft = function(cfg) {
    chis.application.scm.item.script.HisServicePackageItemsListLeft.superclass.constructor
        .apply(this, [cfg]);
    this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.scm.item.script.HisServicePackageItemsListLeft,
    chis.script.BizSimpleListView, {
        loadData : function() {
            // this.requestData.serviceId=this.serviceId;
            // this.requestData.serviceAction = "queryDataFormHis";
            chis.application.scm.item.script.HisServicePackageItemsListLeft.superclass.loadData
                .call(this);
        },
        onLoadData : function(store) {
            // var rn = store.getCount();
            var toolBar = this.grid.getTopToolbar();
            if (toolBar) {
                var smBtn = toolBar.find("cmd", "saveModifyRecords");
                if (smBtn && smBtn.length > 0) {
                    smBtn[0].disable();
                }
            }
        },
        doSaveModifyRecords : function() {
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
                serviceId : "chis.signContractRecordService",
                method : "execute",
                serviceAction : "updateServiceTimes",
                body : stList
            }, function(code, msg, json) {
                if (code > 300) {
                    return
                }
                MyMessageTip.msg("提示", "数据保存成功！", true);
            }, this);
        },
        onDblClick : function(grid, index, e) {
            var r = this.grid.store.getAt(index);
            if (!r) {
                return
            }
            this.opener.rightList.addCol(r)
            this.store.remove(r)
        },
        addCol : function(r){

            this.store.add(r)
        }
    });
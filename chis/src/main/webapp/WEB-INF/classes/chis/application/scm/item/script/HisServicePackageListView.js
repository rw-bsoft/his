$package("chis.application.scm.item.script")
$import("chis.script.BizSimpleListView")
chis.application.scm.item.script.HisServicePackageListView = function (cfg) {
    chis.application.scm.item.script.HisServicePackageListView.superclass.constructor.apply(this, [cfg]);
    this.on("loadData", this.afterLoad, this);
}

Ext.extend(chis.application.scm.item.script.HisServicePackageListView, chis.script.BizSimpleListView, {
    afterLoad: function () {

        var res = util.rmi.miniJsonRequestSync({
            serviceId: "chis.signContractRecordService",
            serviceAction: "queryTask",
            method: "execute"
        });
        this.usedPackge = res.json.body || "";
    },
    doCreateSP: function () {
        this.showSPModule("create", '2');
    },
    onDblClick: function (grid, index, e) {
        this.doModify();
    },
    doModify: function () {
        debugger
        var r = this.getSelectedRecord();
        if (!r) {
            return;
        }
        this.showSPModule("update", r.id);
    },
    showSPModule: function (op, id) {
        var module = this.createCombinedModule("SPModule", this.refModule);
        module.on("SPFormSave", this.refresh, this);
        module.activateId = 0;
        debugger
        this.showWin(module);
        if (op == "update") {
            module.initDataId = id;
            module.loadData();
        } else {
            module.initDataId = id;
            module.parentcode = '2'
            module.doNew();
        }
    },
    doLogOff: function () {
        var r = this.getSelectedRecord();
        this.selectedRecord = r;
        if (!r) {
            MyMessageTip.msg("提示", "请选择需要注销/启用的管理服务项目！", true);
            return;
        }

        var flag = this.checkUsed(r.data.SPID);
        if (flag != undefined && !flag) {
            MyMessageTip.msg("提示", "已使用的服务包无法注销！", true);
            return;
        }

        Ext.Msg.show({
            title: "注销/启用 确认",
            msg: '是否确定要修改该管理服务项目状态?',
            modal: true,
            width: 300,
            buttons: Ext.MessageBox.OKCANCEL,
            multiline: false,
            fn: function (btn, text) {
                if (btn == "ok") {
                    this.logData();
                }
            }, scope: this
        });

    },
    checkUsed: function (SPID) {
        for (var i in this.usedPackge) {
            if (SPID && SPID == this.usedPackge[i]) {
                return false;
            }
        }
    },
    logData : function(){
        util.rmi.jsonRequest({
            serviceId: "chis.signContractRecordService",
            method: "execute",
            serviceAction: "logOffPackage",
            body: this.selectedRecord.data
        }, function (code, msg, json) {
            if (json.body > 300) {
                return this.processReturnMsg(code, msg);
            }
            this.refresh();
        }, this);
    }
})
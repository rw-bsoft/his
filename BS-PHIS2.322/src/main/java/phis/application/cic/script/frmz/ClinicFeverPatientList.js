$package("phis.application.cic.script.frmz")

$import("phis.script.SimpleList")

phis.application.cic.script.frmz.ClinicFeverPatientList = function(cfg) {
    cfg.modal = true;
    cfg.autoLoadData = true;
    cfg.disablePagingTbr = false;
    phis.application.cic.script.frmz.ClinicFeverPatientList.superclass.constructor
        .apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.frmz.ClinicFeverPatientList,
    phis.script.SimpleList, {
        loadData : function() {
            // 得到检索的类别和值
            this.requestData.serviceId = this.listServiceId;
            this.requestData.serviceAction = this.loadInFeverlData;
            phis.application.cic.script.frmz.ClinicFeverPatientList.superclass.loadData
                .call(this)
        },
        //新增
        doAdd : function(){
            if(!this.module){
                this.module = this.createModule("refModule", this.refModule);
                this.module.opener = this;
                this.module.on("saveSucess", function(){
                    this.doRefresh();
                }, this);
            }
            var win = this.module.getWin();
            win.add(this.module.initPanel());
            this.module.doModuleNew();
            win.show();
            win.center();
        },
        //双击修改
        onDblClick : function(grid, index, e) {
            this.doUpdateInfo();
        },
        //修改
        doUpdateInfo : function() {
            var r = this.getSelectedRecord();
            if (r == null) {
                return;
            }
            var sbxh = r.data.SBXH;
            this.showUpdateWin(sbxh);

        },
        showUpdateWin : function(sbxh){
            // 获取数据
            var result = util.rmi.miniJsonRequestSync({
                serviceId : "phis.clinicFeverPatientService",
                serviceAction : "getBySBXH",
                method : "execute",
                SBXH : sbxh
            });
            if (result.code != 200) {
                this.processReturnMsg(result.code, result.msg);
                return null;
            }
            var data =result.json.body;
            if(!this.module){
                this.module = this.createModule("refModule", this.refModule);
            }
            this.module.op = "update";
            var win = this.module.getWin();
            this.module.initModuleData(data)
            win.show();
            win.center();
        },
        //刷新
        doRefresh : function() {
            this.refresh();
        },
        //删除
        doRemove : function(){
            var r = this.getSelectedRecord();
            if (r==undefined) {
                Ext.Msg.alert("提示", "请选择一条数据");
                return;
            }
            Ext.Msg.show({
                title : '提示',
                msg : '您确定要删除该条数据吗？',
                modal : true,
                width : 300,
                buttons : Ext.MessageBox.YESNO,
                multiline : false,
                fn : function(btn, text) {
                    if (btn == "yes") {
                        var res = phis.script.rmi.jsonRequest({
                            serviceId : "phis.clinicFeverPatientService",
                            serviceAction : "deleteFeverPatient",
                            body : r.data
                        }, function(code, msg, json) {
                            if (code >= 300) {
                                this.processReturnMsg(code, msg);
                                return;
                            }else{
                                Ext.Msg.alert("提示", "删除成功");
                            }
                            this.doRefresh();
                        }, this);
                    }
                },
                scope : this
            });
        },
        showColor : function(v, params, data) {
            var zdxh = data.get("ZDXH")
            if(zdxh.indexOf("J12.800+B97.200+Z11.500") != -1){
                params.css = "x-grid-cellbg-1";
            }else if(zdxh.indexOf("J12.800+B97.200") != -1){
                params.css = "x-grid-cellbg-5";
            }else if(zdxh.indexOf("B34.2") != -1){
                params.css = "x-grid-cellbg-1";
            }
            return "";
        },
        getPagingToolbar : function(store) {
            var cfg = {
                pageSize : 100,
                store : store,
                requestData : this.requestData,
                displayInfo : true,
                emptyMsg : "无相关记录"
            };
            var prompt = new Ext.form.Label({
                text : "【绿色标记：核酸检测阳性，而无肺炎表现，橙色标记：疑似新型冠状病毒肺炎，紫色标记：确诊新型冠状病毒肺炎】",
                style:"color:red"
            });
            cfg.items = [prompt];
            var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
            this.pagingToolbar = pagingToolbar;
            return pagingToolbar;
        },
    });

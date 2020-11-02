/**
 * 签约服务Form
 *
 * @author : guol
 */
$package("chis.application.scm.item.script")
$import("chis.script.BizTableFormView", "util.widgets.LovCombo")

chis.application.scm.item.script.UpdateContractServiceForm = function (cfg) {
    cfg.actions = [{
        id: "save",
        name: "确定"
    }, {
        id: "cancel",
        name: "取消",
        iconCls: "common_cancel"
    }]
    cfg.colCount = 2;
    cfg.width = 740;
    cfg.loadServiceId = "chis.contractService";
    cfg.loadAction = "getItemsFormData";
    cfg.labelWidth = 100;
    this.fieldProxys = [];
    chis.application.scm.item.script.UpdateContractServiceForm.superclass.constructor.apply(
        this, [cfg]);
}
Ext.extend(chis.application.scm.item.script.UpdateContractServiceForm, chis.script.BizTableFormView,
    {
        onTableCodeChange: function (f, newVal, oldVal) {
            this.serviceFields.enable();
            this.entryNamesrc = f.selectedNode.attributes["entryName"];
            if (newVal != oldVal) {
                if (newVal) {
                    var length = this.serviceFields.store.data.items.length;
                    if (!length || length == 1) {
                        this.serviceFields.store = new Ext.data.ArrayStore({
                            fields: ['key', 'text'],
                            proxy: new Ext.data.MemoryProxy([['', '']])
                        })
                    }
                    var store = this.serviceFields.store;
                    if (this.fieldProxys[newVal]) {
                        store.proxy.data = this.fieldProxys[newVal]
                        store.load()
                        return
                    }
                    util.rmi.jsonRequest({
                            serviceId: 'chis.healthGeneralInformationService',
                            serviceAction: 'loadFields',
                            method: 'execute',
                            body: {
                                tableCode: newVal
                            }
                        },
                        function (code, msg, json) {
                            if (code < 300) {
                                store.proxy.data = json.body
                                store.load()
                                this.fieldProxys[newVal] = json.body
                            } else {
                                Ext.MessageBox.alert('错误', msg)
                            }
                        }, this)
                }
            }
        },
        changFieldStatusToBlack: function (array) {
//            for (var i = 0; i < array.length; i++) {
//                Ext.getCmp(array[i].id).getEl().up('.x-form-item')
//                    .child('.x-form-item-label').update(array[i].fieldLabel.replace("red", "black") + ":");
//                array[i].reset();
//                array[i].disable();
//                array[i].allowBlank = true;
//                array[i].validate();
//            }
        },
        changFieldStatusToRed: function (array) {
            for (var i = 0; i < array.length; i++) {
                Ext.getCmp(array[i].id).getEl().up('.x-form-item')
                    .child('.x-form-item-label').update("<span style='color:red'>" + array[i].fieldLabel
                    + ":</span>");
                array[i].reset();
                array[i].enable();
                array[i].allowBlank = false;
                array[i].validate();
            }
        },
        saveToServer: function (saveData) {
            var mdId = this.form.getForm().findField("moduleAppId").getValue();
            var itType = this.form.getForm().findField("itemType").getValue();
            // if (itType == "4" && (mdId == "" || mdId == null || mdId == "undefiend"))
            //     return MyMessageTip.msg("提示", "服务项目请维护任务模块路径！", true);
            if (!this.fireEvent("beforeSave", this.entryName, this.op,
                saveData)) {
                return;
            }
            request = {
                serviceId: "chis.contractService",
                method: "execute",
                op: this.op,
                schema: this.entryName,
                body: saveData
            }
            if (this.parentCode) {
                saveData["parentCode"] = this.parentCode;
            }
            saveData["oldItemCode"] = this.oldItemCode;
            request.serviceAction = "saveServiceItem";
            this.saveData(request);
        },
        confirmSave: function () {

        },
        setNodeInfo: function (node, op) {
            debugger
            if (!node) {
                return;
            }
            this.node = node;
            this.parentCode = node.attributes["key"];
            this.parentBottom = node.attributes["isBottom"];
            this.form.getForm().findField("parentCode")
                .setValue(this.parentCode);
            this.itemCodeFld = this.form.getForm().findField("itemCode");
            this.itemCodeFld.disable();
            if ("create" == op) {
                //生成项目编码
                if (this.parentCode.length < 9) {
                    var result = util.rmi.miniJsonRequestSync({
                        serviceId: "chis.contractService",
                        serviceAction: "genItemCode",
                        method: "execute",
                        body: {
                            "parentCode": this.parentCode
                        }
                    });
                    if (result.code > 300) {
                        this.processReturnMsg(result.code, result.msg);
                        return
                    }
                    var nic = result.json.newItemCode || '';
                    this.itemCodeFld.setValue(nic);
                }
            }
            var array = [];
            var form = this.form.getForm();
            this.priceField = form.findField('price');
            this.realPriceField = form.findField('realPrice');
            this.startUsingDateField = form.findField('startUsingDate');
            this.serviceTableField = form.findField('serviceTable');
            this.serviceFieldsField = form.findField('serviceFields');
            this.moduleAppIdField = form.findField('moduleAppId');
            this.isOneWeekWorkField = form.findField('isOneWeekWork');
            array.push(this.priceField);
            array.push(this.realPriceField);
            array.push(this.startUsingDateField);
            array.push(this.serviceTableField);
            array.push(this.serviceFieldsField);
            array.push(this.moduleAppIdField);
            array.push(this.isOneWeekWorkField);
            this.array = array;
            debugger
            if (this.parentCode.length >2 || this.itemCode>3) {
                //为了解决node节点更改的问题添加或3判断
                this.form.getForm().findField("isBottom")
                    .setValue({"key": "y", "text": "是"});
                this.form.getForm().findField("itemType")
                    .setValue({"key": "4", "text": "服务项目"});
                this.form.getForm().findField("isBottom").disable();
                this.form.getForm().findField("itemType").disable();


                this.changFieldStatusToRed(this.array);
            } else {
                // if (this.parentCode.length == 1) {
                    this.form.getForm().findField("itemType")
                        .setValue({"key": "1", "text": "业务分类"});
                // }
//                if (this.parentCode.length == 2) {
//                    this.form.getForm().findField("itemType")
//                        .setValue({"key": "2", "text": "项目分类"});
//                }
//                if (this.parentCode.length == 4) {
//                    this.form.getForm().findField("itemType")
//                        .setValue({"key": "3", "text": "服务分类"});
//                }
                this.form.getForm().findField("isBottom")
                    .setValue({"key": "n", "text": "否"});
                this.form.getForm().findField("isBottom").disable();
                this.form.getForm().findField("itemType").disable();
                this.changFieldStatusToBlack(this.array);
            }
//            this.serviceFields.disable();
        },
        saveData: function (request) {
            this.form.el.mask("正在保存数据...", "x-mask-loading")
            util.rmi.jsonRequest(request, function (code, msg, json) {
                this.form.el.unmask()
                if (code > 300) {
                    this.processReturnMsg(code, msg,
                        this.saveToServer, [request.body]);
                    return
                }
                Ext.apply(request.body, json.body);
                this.fireEvent("save", request.body, this.op)
//                this.getWin().hide();
            }, this);
        },
        initFormData: function (data) {
            chis.application.scm.item.script.UpdateContractServiceForm.superclass.initFormData
                .call(this, data);
        }
    })
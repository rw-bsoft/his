$package("chis.application.scm.item.script")
$import("chis.script.BizTableFormView","util.widgets.LookUpField")
chis.application.scm.item.script.HisServicePackageForm=function(cfg){
    cfg.colCount=2;
    chis.application.scm.item.script.HisServicePackageForm.superclass.constructor.apply(this,[cfg]);
    this.on("beforeCreate",this.beforeCreate,this);
}

Ext.extend(chis.application.scm.item.script.HisServicePackageForm,chis.script.BizTableFormView,{
    beforeCreate : function () {
        var parentCode = this.form.getForm().findField("parentCode");
        parentCode.setValue('2');
    },
    doNew : function(){
        chis.script.BizTableFormView.superclass.doNew.call(this);
        var parentCode = this.form.getForm().findField("parentCode");
        var maxItemCode = this.getNextItemCode('2');
        var itemCode = this.form.getForm().findField("itemCode");
        var isBottom = this.form.getForm().findField("isBottom");
        if (this.initDataId) {
            this.fireEvent("beforeUpdate", this); // **

            // 在数据加载之前做一些初始化操作
        } else {
            this.fireEvent("beforeCreate", this);
            // ** 在页面新建时做一些初始化操作
            parentCode.setValue('2');
            itemCode.setValue(maxItemCode);
            isBottom.setValue(maxItemCode.length < 5 ? 'n' : 'y');
            if(this.opener)this.opener.SPID = maxItemCode;
            // this.opener.spiModule.loadData();
        }


    },
    getNextItemCode :function(parentCode){
        var result = util.rmi.miniJsonRequestSync({
                serviceId: "chis.contractService",
                serviceAction: "genItemCode",
                method: "execute",
                body: {
                    "parentCode": parentCode
                }
            }
        )
        if (result.code > 300) {
            this.processReturnMsg(result.code, result.msg);
            return null;
        }
        return result.json["newItemCode"];
    },
    setNodeInfo: function(node, op){
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
    }

});
/**
 * 个人主要问题表单
 *
 * @author : renwei 2020-10-21
 */
$package("phis.application.dpc.script")

$import("util.Accredit", "chis.script.BizTableFormView")

phis.application.dpc.script.PeriodontalAgreementForm = function(cfg) {
    cfg.labelAlign = "left";
    cfg.showButtonOnTop = true;
    cfg.autoFieldWidth = false;
    cfg.fldDefaultWidth = 249;
    cfg.colCount = 2;
    cfg.width = 710;
    phis.application.dpc.script.PeriodontalAgreementForm.superclass.constructor.apply(this, [cfg]);
    this.on("save", this.onSave, this);
    this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.dpc.script.PeriodontalAgreementForm, chis.script.BizTableFormView, {
    doNew : function() {
        debugger
        phis.application.dpc.script.PeriodontalAgreementForm.superclass.doNew.call(this);
        var form = this.form.getForm();
        if (this.exContext.ids.empiId) {
            this.data["empiId"] = this.exContext.ids.empiId;
            var empiData = this.exContext.empiData;
            var empiId = this.exContext.ids.empiId;
            var personName = empiData.personName;
            var age = empiData.age;
            var sexCode = empiData.sexCode;
            var form = this.form.getForm();
            if(this.initDataId){
                var record = null;
                var records = this.opener.store.data.items;
                for(var i=0;i<records.length;i++){
                    if(this.initDataId == records[i].id){
                        record = records[i].data;
                        form.findField("personName").setValue(record.personName);
                        form.findField("age").setValue(record.age);
                        form.findField("sex").selectedIndex = record.sex;
                        form.findField("diagnosis").setValue(record.diagnosis);
                        form.findField("operationDate").setValue(record.operationDate);
                        form.findField("proposed").setValue(record.proposed);
                    }
                }
            }else{
                form.findField("personName").setValue(personName);
                form.findField("age").setValue(age);
                form.findField("sex").selectedIndex = sexCode;
                form.findField("operationDate").setValue(record.operationDate);
                form.findField("proposed").setValue(record.proposed);
            }
        }
        if (this.op == "create" && !this.initDataId) {
            var form = this.form.getForm();
        }
    },

    onReady : function() {
        phis.application.dpc.script.PeriodontalAgreementForm.superclass.onReady.call(this);
        var form = this.form.getForm();
    },

    onLoadData : function(entry, body) {
        debugger;
        var a = body;

    },

    doCancer : function() {
        this.getWin().hide();
    },

    onSave : function() {
        this.getWin().hide();
    }
});
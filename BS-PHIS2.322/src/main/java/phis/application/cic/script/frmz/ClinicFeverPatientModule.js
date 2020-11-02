$package("phis.application.cic.script.frmz")

$import("phis.script.SimpleModule")

phis.application.cic.script.frmz.ClinicFeverPatientModule = function (cfg) {
    cfg.width = 1200;
    cfg.height = 390;
    this.openerType = this.openerType || "1";
    this.op = this.op || "create";
    phis.application.cic.script.frmz.ClinicFeverPatientModule.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.frmz.ClinicFeverPatientModule, phis.script.SimpleModule, {
    initPanel : function () {
        if (this.panel) {
            if (this.openerType == 2){
                this.onReady();
            }
            return this.panel;
        }
        this.panel = new Ext.Panel({
            border : false,
            frame : false,
            layout : 'border',
            width : this.width,
            height : this.height,
            tbar : (this.tbar || []).concat(this.createButtons()),
            items : [{
                title : '',
                layout : "fit",
                region : 'north',
                height : 260,
                items : this.getNorthForm()
            },{
                title : '', // 医技检查
                layout : "fit",
                region : 'center',
                items : this.getSouthForm()
            }
            ]
        });
        this.panel.on("afterrender", this.onReady, this);
        this.initSystemParamsAndData();
        return this.panel;
    },
    initSystemParamsAndData : function(){
        //1.不存在系统参数进行，系统参数的加载
        if(!this.systemParams){
            this.systemParams = {};
            Ext.apply(this.systemParams, this.loadSystemParams({
                    commons: ['SFQYGWXT']
                })
            );
        }
    },
    onReady: function(){
        if (this.openerType == 2){
            var btns = this.panel.getTopToolbar();
            var dr = btns.find("cmd", "import");
            dr[0].hide();
            var r = util.rmi.miniJsonRequestSync({
                serviceId : "phis.clinicManageService",
                serviceAction : "queryBRXX",
                empiId : this.empiId,
                brid : this.brid,
                jzxh : this.jzxh
            });
            if(r.code==200){
                if(r.json.body){
                    this.northForm.formData = r.json.body;
                }
            }
        }
    },
    getNorthForm : function(){
        this.northForm = this.createModule("refNorthForm_frmzAdd", this.refNorthForm);
        var  grid = this.northForm.initPanel();
        return grid;
    },
    getSouthForm : function(){
        this.southForm = this.createModule("refSouthForm_frmzAdd", this.refSouthForm);
        var  grid = this.southForm.initPanel();
        return grid;
    },
    doSave : function(){
        var northData = this.northForm.getFormData();
        var southData = this.southForm.getFormData();
        if (northData==null || southData==null){
            return
        }
        if(!this.brid || !this.empiId){
            MyMessageTip.msg("提示", "该病人在系统中未有就诊记录，请先挂号就诊！", true);//请调入病人的相关信息
            return;
        }

        var body = {
            northData: northData,
            southData: southData,
            jzxh: this.jzxh || 0,
            brid: this.brid,
            openerType: this.openerType,
            empiId: this.empiId
        }
        this.panel.el.mask("正在保存数据...", "x-mask-loading")
        var ret = util.rmi.jsonRequest({
            serviceId : "phis.clinicFeverPatientService",
            serviceAction : "saveClinicFeverInfo",
            method : "execute",
            body : body,
            op : this.op
        }, function(code, msg, json) {
            this.panel.el.unmask();
            if (code > 300) {
                this.processReturnMsg(code, msg);
                return
            }
            if(code == 200){
                MyMessageTip.msg("提示", '保存成功!', true);
                this.fireEvent("saveSucess", body);
                this.getWin().hide();
            }
        }, this)
    },
    initModuleData : function(data) {
        this.setProperties(data);
        this.northForm.initFormData(data);
        this.southForm.initFormData(data);
    },
    doCancel : function() {
        this.getWin().hide();
    },
    doImport : function() {
        var m = this.midiModules["healthRecordModule"];
        if (!m) {
            $import("phis.application.pix.script.EMPIInfoModule");
            m = new phis.application.pix.script.EMPIInfoModule({
                entryName : "phis.application.pix.schemas.MPI_DemographicInfo_SMQ",
                title : "个人基本信息查询",
                height : 450,
                modal : true,
                mainApp : this.mainApp
            });
            m.on("onEmpiReturn", function (data) {
                this.setPatientInfo(data);
            }, this);
            this.midiModules["healthRecordModule"] = m;
        }
        var win = m.getWin();
        win.show();
        m.clear();
    },
    setPatientInfo:function (data) {
        this.empiId = data.empiId;
        this.brid = data.BRID;

        var form = this.northForm.form.getForm();
        form.findField("BRXM").setValue(data.personName);
        if(data.sexCode == 1){
            form.findField("BRXB").setValue({key: 1, text: "男"});
        }else{
            form.findField("BRXB").setValue({key: 2, text: "女"});
        }
        form.findField("CSNY").setValue(data.birthday);
        form.findField("SFZH").setValue(data.idCard);
        form.findField("BRDZ").setValue(data.address);
        form.findField("BRDH").setValue(data.mobileNumber);

        this.getChisDataInfo();
    },
    openerTypeSetProperties : function(openerType){
        var northForm = this.northForm.form.getForm();
        if(openerType == "1"){
            northForm.findField("SJLY").setValue({key:openerType, text:"直接新增"});
        }else if(openerType == "2"){
            northForm.findField("SJLY").setValue({key:openerType, text:"门诊引入"});
        }else if(openerType == "3"){
            northForm.findField("SJLY").setValue({key:openerType, text:"住院引入"});
        }
    },
    doModuleNew : function(){
        this.resetProperties();
        this.northForm.doNew();
        this.southForm.doNew();
        this.openerTypeSetProperties(this.openerType);
    },
    resetProperties : function () {
        this.brid = null;
        this.empiId = null
    },
    setProperties : function(data){
        this.brid = data.BRID;
        this.empiId = data.EMPIID;
    },
    getChisDataInfo : function(){
        if(!this.empiId){
            return
        }
        if(this.mainApp.chisActive && this.systemParams.SFQYGWXT) {
            util.rmi.jsonRequest({
                serviceId: "chis.healthRecordService",
                serviceAction: "getPersonDiease",
                method: "execute",
                body: {
                    empiId: this.empiId
                }
            }, function (code, msg, json) {
                if (code < 300) {
                    this.setChisDataInfo(json.body);
                }
            }, this);
        }
    },
    setChisDataInfo : function(body){
        var northForm = this.northForm.form.getForm();
        var old_jcjb = northForm.findField("JCJB").getValue();
        var jcjb = old_jcjb;
        if(body.GXY && old_jcjb.indexOf("1") == -1){
            if(jcjb != ""){
                jcjb += ",";
            }
            jcjb += "1";  //1 心脑血管疾病
        }
        if(body.TNB && old_jcjb.indexOf("2") == -1){
            if(jcjb != ""){
                jcjb += ",";
            }
            jcjb += "2"  //2 内分泌系统疾病
        }
        northForm.findField("JCJB").setValue(jcjb);
    }
});

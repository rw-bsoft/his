$package("chis.application.scm.sign.script")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField", "chis.script.util.Vtype", "chis.script.util.query.QueryModule")

chis.application.scm.sign.script.PersonalContractInfoForm = function (cfg) {
    chis.application.scm.sign.script.PersonalContractInfoForm.superclass.constructor.apply(this, [cfg]);
    this.labelWidth = 55;
    this.colCount = 5;
}

Ext.extend(chis.application.scm.sign.script.PersonalContractInfoForm, chis.script.BizTableFormView, {
    initPanel: function (sc) {
        if (this.form) {
            if (!this.isCombined) {
                this.addPanelToWin();
            }
            return this.form;
        }
        var schema = sc
        if (!schema) {
            var re = util.schema.loadSync(this.entryName)
            if (re.code == 200) {
                schema = re.schema;
            }
            else {
                this.processReturnMsg(re.code, re.msg, this.initPanel)
                return;
            }
        }
        this.schema = schema;
        var ac = util.Accredit;
        var defaultWidth = this.fldDefaultWidth || 200
        var items = schema.items
        if (!this.fireEvent("changeDic", items)) {
            return
        }
        var colCount = this.colCount;

        var table = {
            layout: 'tableform',
            layoutConfig: {
                columns: colCount,
                tableAttrs: {
                    border: 0,
                    cellpadding: '2',
                    cellspacing: "2"
                }
            },
            items: []
        }
        if (!this.autoFieldWidth) {
            var forceViewWidth = (defaultWidth + (this.labelWidth || 80)) * colCount
            table.layoutConfig.forceWidth = forceViewWidth
        }
        var size = items.length
        for (var i = 0; i < size; i++) {
            var it = items[i]
            if ((it.display == 0 || it.display == 1 || it.hidden == true) || !ac.canRead(it.acValue)) {
                continue;
            }
            var f = this.createField(it)
            f.index = i;
            f.anchor = it.anchor || "100%"
            delete f.width

            f.colspan = parseInt(it.colspan)
            f.rowspan = parseInt(it.rowspan)

            if (!this.fireEvent("addfield", f, it)) {
                continue;
            }
            table.items.push(f)
        }

        var cfg = {
            buttonAlign: 'center',
            labelAlign: this.labelAlign || "left",
            labelWidth: this.labelWidth || 80,
            frame: true,
            shadow: false,
            border: false,
            collapsible: false,
            autoWidth: true,
            autoHeight: true,
            floating: false
        }
        if (this.isCombined) {
            cfg.frame = true
            cfg.shadow = false
            cfg.width = this.width
            cfg.height = this.height
        }
        else {
            cfg.autoWidth = true
            cfg.autoHeight = true
        }
        this.changeCfg(cfg);
        this.initBars(cfg);
        Ext.apply(table, cfg)
        this.form = new Ext.FormPanel(table)
        this.form.on("afterrender", this.onReady, this)

        this.schema = schema;
        this.setKeyReadOnly(true)
        if (!this.isCombined) {
            this.addPanelToWin();
        }
        return this.form
    },
    onReady: function () {
        chis.application.scm.sign.script.PersonalContractInfoForm.superclass.onReady.call(this);
        this.form = this.form.getForm();
		var personName = this.form.findField("favoreeName");
		if (personName) {
			personName.on("lookup", this.doQuery, this)
		}
    },
    doQuery : function(field) {
		if (!field.disabled) {
				$import("chis.application.scm.sign.script.FamilyMemberList")
				m = new chis.application.scm.sign.script.FamilyMemberList(
						{
							entryName : "chis.application.scm.schemas.MPI_DemographicInfoFamily",
							title : "家庭成员信息查询",
							height : 450,
							width : 868,
							modal : true,
							wi : 151,
							mainApp : this.mainApp,
							isDeadRegist : false,
							familyId:this.familyId
						})
            m.opened = true;
			m.on("select",this.onChangePersonnal,this);
			m.operen = this;
			var win = m.getWin();
			win.setPosition(250, 100);
			win.show();
		}
	},
   
    onChangePersonnal : function(body) {
		var selectData = body.data;
		var pData = {};
		pData["favoreeEmpiId"] = selectData.empiId;
		pData["favoreeName"] = selectData.personName;
		var favoreeName = this.form.findField("favoreeName");
		favoreeName.setValue(selectData.personName);
		pData["idCard"] = selectData.idCard;
		var idCard = this.form.findField("idCard");
		idCard.setValue(selectData.idCard);
		pData["birthday"] = selectData.birthday;
		var birthday = this.form.findField("birthday");
		birthday.setValue(selectData.birthday);
		pData["mobileNumber"] = selectData.mobileNumber;
		var mobileNumber = this.form.findField("mobileNumber");
		mobileNumber.setValue(selectData.mobileNumber);
		pData["sexCode"] = selectData.sexCode;
		var sexCode = this.form.findField("sexCode");
		var unknow = "0";// 未知性别
		var man = "1";
		var women = "2";
		var unspecified = "9";// 未说明的性别
		if (selectData.sexCode == unknow) {
			sexCode.setValue("未知性别");
		}
		if (selectData.sexCode == man) {
			sexCode.setValue("男");
		}
		if (selectData.sexCode == women) {
			sexCode.setValue("女");
		}
		if (selectData.sexCode == unspecified) {
			sexCode.setValue("未说明的性别");
		}
		var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		var birth = Date.parseDate(pData.birthday, "Y-m-d");
		const monthAge = chis.script.util.helper.Helper.getAgeMonths(
				birth, currDate);
		pData.age = ~~(monthAge / 12);
		this.scModule.pData = pData;
		this.scModule.exContext.args.pData = pData;
		this.scModuleSubList.requestData.cnd = ['eq',
				['$', 'a.favoreeEmpiId'], ['s', pData["favoreeEmpiId"]]];
		this.scModuleSubList.exContext.ids.empiId = pData["favoreeEmpiId"];
		this.scModuleSubList.loadData();
	},
    loadData: function () {
        this.favoreeName = this.form.findField("favoreeName");
        this.idCard = this.form.findField("idCard");
        this.sexCode = this.form.findField("sexCode");
        this.birthday = this.form.findField("birthday");
        this.mobileNumber = this.form.findField("mobileNumber");
        var data = this.exContext.args.pData;
        util.rmi.jsonRequest({
                serviceId: 'chis.signContractRecordService',
                serviceAction: 'queryfamilyPeopleList',
                method: 'execute',
                empiId: this.exContext.ids.empiId
            },
            function (code, msg, json) {
                if (code < 300) {
                    if (json.body && json.body.length > 1) {
                        this.favoreeName.enable()
                        this.familyId=json.familyId;
                        this.initPersonnalInfo(data);
                    } else {
                        this.favoreeName.disable();
                        this.initPersonnalInfo(data);
                    }
                }
            }, this);
    },
    initPersonnalInfo: function (data) {
        this.favoreeName.setValue(data.favoreeName);
        this.idCard.setValue(data.idCard);
        this.sexCode.setValue(data.sexCode.text);
        this.birthday.setValue(data.birthday)
        this.mobileNumber.setValue(data.mobileNumber)

    }
});
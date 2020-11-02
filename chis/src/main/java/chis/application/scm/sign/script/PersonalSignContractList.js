$package("chis.application.scm.sign.script")
$import("chis.script.BizSimpleListView")

chis.application.scm.sign.script.PersonalSignContractList = function (cfg) {
    cfg.autoLoadSchema = false;
    cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
    cfg.selectFirst = true;
    cfg.isCombined = true;
    chis.application.scm.sign.script.PersonalSignContractList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.scm.sign.script.PersonalSignContractList, chis.script.BizSimpleListView, {
    doNew: function () {
       var empiId = this.exContext.ids.empiId;
       var result = util.rmi.miniJsonRequestSync({
           serviceId: "chis.signContractRecordService",
           serviceAction: "checkPersonnalContractRecord",
           method: "execute",
           empiId: empiId

       });
//       if (result.json.body && result.json.body > 0) {
//           MyMessageTip.msg("提示", "存在有效签约记录无法再次签约！", true);
//           return;
//       }
        this.opener.haveRecord = false;
       this.opener.isSupplementary = false;
      this.fireEvent("newSC");
   },
    doSupplementary: function () {
       var empiId = this.exContext.ids.empiId;
       var result = util.rmi.miniJsonRequestSync({
           serviceId: "chis.signContractRecordService",
           serviceAction: "checkPersonnalContractRecord",
           method: "execute",
           empiId: empiId

       });
       this.opener.haveRecord = false;
       this.opener.isSupplementary = true;
      this.fireEvent("newSC");
   },
    doRenewal: function(){
        var r = this.getSelectedRecord();
        if(!r){
            return MyMessageTip.msg("提示","请先选中签约记录！",true);
        }
        this.opener.haveRecord = false;
        this.fireEvent("renewSC");
    },
    doRemove: function () {//当天签约，当天又解约的可以删除
        var today = new Date();
        var r = this.getSelectedRecord()
        if (r == null) {
            return
        }
        var beginDate = Date.parseDate(r.get("scDate"), 'Y-m-d');
//		if (beginDate.getTime() + 86400000 < today.getTime()) {
//			alert("签约生效超过一天，不能删除该记录")
//			return;
//		}
        Ext.Msg.show({
            title: '确认删除此记录',
            msg: '删除操作将无法恢复，是否继续?',
            modal: true,
            width: 300,
            buttons: Ext.MessageBox.OKCANCEL,
            multiline: false,
            fn: function (btn, text) {
                if (btn == "ok") {
                    this.processRemove();
                }
            },
            scope: this
        })
    },
    processRemove: function () {
        var r = this.getSelectedRecord()
        if (r == null) {
            return
        }
        if (!this.fireEvent("beforeRemove", this.entryName, r)) {
            return;
        }
        this.mask("正在删除数据...")
        util.rmi.jsonRequest({
            serviceId: "chis.signContractRecordService",
            pkey: r.id,
            familyId: this.exContext.args.initDataId,
            serviceAction: "removeSCR",
            method: "execute",
            body: {
                "empiId": r.get("favoreeEmpiId") || '',
                "SCID": r.get("SCID") || '',
                "signFlag": r.get("signFlag")
            }
        }, function (code, msg, json) {
            this.unmask()
            if (code < 300) {
                this.store.remove(r)
                this.fireEvent("remove", this.entryName,
                    'remove', json, r.data)
            } else {
                this.processReturnMsg(code, msg, this.doRemove)
            }
            this.leftList.loadData();
            this.father.loadData();
//					this.setBtnStatus(true, "new");
        }, this)
    },
    setBtnStatus: function (s, iselse) {
        var toolBar = this.grid.getTopToolbar();
        this.toolBar = toolBar;
        if (toolBar) {
            var btn = toolBar.find("cmd", iselse);
            if (!btn || "" == btn)
                return
            if (!btn) {
                s = true;
            }
            if (s) {
                btn[0].enable()
            } else {
                btn[0].disable()
            }
        }
    },
	showColor : function(value, metaData, r, row, col) {
		debugger;
		if(value==undefined){
			return "";
		}		
		var nowdate = new Date();
        nowdate.setMonth(nowdate.getMonth()+1);
        var y = nowdate.getFullYear();
        var m = nowdate.getMonth()+1;
        var d = nowdate.getDate();
        var nextMonthDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
        nowdate.setMonth(nowdate.getMonth()-1);
        y = nowdate.getFullYear();
        m = nowdate.getMonth()+1;
        d = nowdate.getDate();
        var currentDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
		//客户端电脑时间与签约结束时间对比判定
		if(value=="签约" && r.data.endDate.length>0 && nextMonthDate>=r.data.endDate && currentDate<=r.data.endDate){
			return "<font style='color:red'>即将到期</font>";
		}else if(value=="签约" && r.data.endDate.length>0 && currentDate>r.data.endDate){
			return "<font style='color:red'>已到期</font>";
		}
		else if(value=="解约"){
			return "<font style='color:red'>"+value+"</font>";
		}
		return "<font style='color:#2AAA00'>"+value+"</font>";
		
	},
	checkPaymentExist : function(value, metaData, r, row, col) {
		debugger;
		if(value == null || value == "" || value.indexOf("ZF") != -1){
			r.data.fphm = "否";
			value = "否"
		}else{
			r.data.fphm = "是";
			value = "是"
		}
		return value
	}
});
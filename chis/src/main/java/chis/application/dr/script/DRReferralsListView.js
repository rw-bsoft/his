$package("chis.application.dr.script")
$import("chis.script.BizTableFormView", "util.dictionary.DictionaryLoader")

chis.application.dr.script.DRReferralsListView = function(cfg) {
	cfg.actions = [ {
				id : "printList",
				name : "打印转诊单",
				iconCls : "print"
			}];
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.dr.script.DRReferralsListView.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.dr.script.DRReferralsListView, chis.script.BizTableFormView, {
			
	setField : function() {
//				var form = this.form.getForm();
//				form.findField("status").disable();
//				form.findField("treatTime").disable();
//				form.findField("treatDesc").disable();
//				form.findField("description").enable();
//				form.findField("contactPhone").enable();
//				if (this.isTreat) {
//					form.findField("treatTime").enable();
//					form.findField("treatTime").setValue(this.mainApp.serverDate);
//					form.findField("treatDesc").enable();
//					form.findField("description").disable();
//					form.findField("contactPhone").disable();
//					form.findField("status").enable();
//				} else {
//					var treatTime = form.findField("treatTime");
//					if (treatTime) {
//						treatTime.setValue("");
//					}
//				}
	},
	onWinShow : function() {
//		this.setButton();
		if (this.op == "create") {
			this.doCreate();
		}
		this.win.doLayout();
		this.setField();
	},
	setButton : function() {
		if (!this.form.getTopToolbar()) {
			return;
		}
		var bts = this.form.getTopToolbar().items;
		if (this.op == "read") {
			bts.items[0].disable();
		} else {
			bts.items[0].enable();
		}
	},
	doPrintList:function(){
		var Form = this.form.getForm();
		var reserveNo = Form.findField("reserveNo");
		if(typeof(reserveNo.getValue())=="undefined" || reserveNo.getValue()==null || reserveNo.getValue()==""){
			MyMessageTip.msg("提示", "转诊单号查询失败，请先提交!", true);
			return;
		}
		var url = "resources/chis.prints.template.ReferralInfoFile.print?type=" + 1
						+ "&reserveNo=" + reserveNo.getValue();
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
		
//		var LODOP=getLodop();
//		LODOP.PRINT_INIT("打印控件");
//		LODOP.SET_PRINT_PAGESIZE("0","","","");
//		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
//		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
//		//预览
//		LODOP.PREVIEW();
//		//直接打印
//		//LODOP.PRINT();
	}
})
$package("chis.application.ohr.script");


$import("chis.script.BizTableFormView");

chis.application.ohr.script.OldPeopleSelfCareFrom=function(cfg){
	cfg.colCount=3;
	chis.application.ohr.script.OldPeopleSelfCareFrom.superclass.constructor.apply(this,[cfg]);
	this.labelWidth=60;
	this.fldDefaultWidth=100;
	this.saveServiceId = "chis.oldPeopleSelfCareService";
	this.saveAction = "saveOldPeopleSelfCare";
	this.on("doNew", this.onDoNew, this);
};

Ext.extend(chis.application.ohr.script.OldPeopleSelfCareFrom,chis.script.BizTableFormView,{
	doAdd : function() {
		this.fireEvent("add");
	},
	
	onDoNew : function() {
		var empiData=this.exContext.empiData;
				var birthDay = empiData.birthday;
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear()
						- this.mainApp.exContext.oldPeopleAge);
				if (birth.getFullYear() > currDate.getFullYear()) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '年龄小于'
										+ this.mainApp.exContext.oldPeopleAge
										+ '岁不允许建立老年人自理评估',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;	
				}else{
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
		this.op="create";
		this.initDataId=null;
		}
	},
	
	getSaveRequest : function(saveData){
		saveData.empiId = this.exContext.ids.empiId;
		saveData.phrId = this.exContext.ids.phrId;
		return saveData;
	},
	
	onReady : function() {
		chis.application.ohr.script.OldPeopleSelfCareFrom.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("JC").on("select", this.onJCSelect, this);
		form.findField("JC").on("change", this.onJCSelect, this);
		
		form.findField("SX").on("select", this.onSXSelect, this);
		form.findField("SX").on("change", this.onSXSelect, this);
		
		form.findField("CY").on("select", this.onCYSelect, this);
		form.findField("CY").on("change", this.onCYSelect,	this);
		
		form.findField("RC").on("select", this.onRCSelect, this);
		form.findField("RC").on("change", this.onRCSelect,	this);
		
		form.findField("HD").on("select", this.onHDSelect, this);
		form.findField("HD").on("change", this.onHDSelect,	this);
	},
	
	totalPF : function(){
		var form = this.form.getForm();
		var jcpf = form.findField("JCPF").getValue();
		var sxpf = form.findField("SXPF").getValue();
		var cypf = form.findField("CYPF").getValue();
		var rcpf = form.findField("RCPF").getValue();
		var hdpf = form.findField("HDPF").getValue();
		var zpfs = jcpf + sxpf + cypf + rcpf+ hdpf;
		form.findField("ZPFS").setValue(zpfs);
		
		var zp = {};
		var zpdj = {};
		if(zpfs >=0 && zpfs <=3){
			zp = {key:"1",text:"可自理"};
			zpdj = {key:"1",text:"可自理"};
		}else if(zpfs >= 4 && zpfs <= 8){
			zp = {key:"2",text:"轻度依赖"};
			zpdj = {key:"2",text:"轻度依赖"};
		}else if(zpfs >= 9 && zpfs <= 18){
			zp = {key:"3",text:"中度依赖"};
			zpdj = {key:"3",text:"中度依赖"};
		}else if(zpfs >= 19){
			zp = {key:"4",text:"不能自理"};
			zpdj = {key:"4",text:"不能自理"};
		}
		form.findField("ZP").setValue(zp);
		form.findField("ZPDJ").setValue(zpdj);
	},
	
	onJCSelect : function(){
		var form = this.form.getForm();
		var jcVal = form.findField("JC").getValue();
		var jcpf = 0;
		var jcdj = {};
		if(jcVal=="1"){
			jcpf=0;
			jcdj={key:"1",text:"可自理"};
		}
		if(jcVal=="2"){
			jcpf=0;
			jcdj={key:"2",text:"轻度依赖"};
		}
		if(jcVal=="3"){
			jcpf=3;
			jcdj={key:"3",text:"中度依赖"};
		}
		if(jcVal=="4"){
			jcpf=5;
			jcdj={key:"4",text:"不能自理"};
		}
		form.findField("JCPF").setValue(jcpf);
		form.findField("JCDJ").setValue(jcdj);
		this.totalPF();
	},
	onSXSelect : function(){
		var form = this.form.getForm();
		var sxVal = form.findField("SX").getValue();
		var sxpf = 0;
		var sxdj={};
		if(sxVal=="1"){
			sxpf=0;
			sxdj={key:"1",text:"可自理"};
		}
		if(sxVal=="2"){
			sxpf=1;
			sxdj={key:"2",text:"轻度依赖"};
		}
		if(sxVal=="3"){
			sxpf=3;
			sxdj={key:"3",text:"中度依赖"};
		}
		if(sxVal=="4"){
			sxpf=7;
			sxdj={key:"4",text:"不能自理"};
		}
		form.findField("SXPF").setValue(sxpf);
		form.findField("SXDJ").setValue(sxdj);
		this.totalPF();
	},
	onCYSelect : function(){
		var form = this.form.getForm();
		var cyVal = form.findField("CY").getValue();
		var cypf = 0;
		var cydj = {};
		if(cyVal=="1"){
			cypf=0;
			cydj={key:"1",text:"可自理"};
		}
		if(cyVal=="2"){
			cypf=0;
			cydj={key:"2",text:"轻度依赖"};
		}
		if(cyVal=="3"){
			cypf=3;
			cydj={key:"3",text:"中度依赖"};
		}
		if(cyVal=="4"){
			cypf=5;
			cydj={key:"4",text:"不能自理"};
		}
		form.findField("CYPF").setValue(cypf);
		form.findField("CYDJ").setValue(cydj);
		this.totalPF();
	},
	onRCSelect : function(){
		var form = this.form.getForm();
		var rcVal = form.findField("RC").getValue();
		var rcpf = 0;
		var rcdj = {};
		if(rcVal=="1"){
			rcpf=0;
			rcdj={key:"1",text:"可自理"};
		}
		if(rcVal=="2"){
			rcpf=1;
			rcdj={key:"2",text:"轻度依赖"};
		}
		if(rcVal=="3"){
			rcpf=5;
			rcdj={key:"3",text:"中度依赖"};
		}
		if(rcVal=="4"){
			rcpf=10;
			rcdj={key:"4",text:"不能自理"};
		}
		form.findField("RCPF").setValue(rcpf);
		form.findField("RCDJ").setValue(rcdj);
		this.totalPF();
	},
	onHDSelect : function(){
		var form = this.form.getForm();
		var hdVal = form.findField("HD").getValue();
		var hdpf = 0;
		var hddj = {};
		if(hdVal=="1"){
			hdpf=0;
			hddj={key:"1",text:"可自理"};
		}
		if(hdVal=="2"){
			hdpf=1;
			hddj={key:"2",text:"轻度依赖"};
		}
		if(hdVal=="3"){
			hdpf=5;
			hddj={key:"3",text:"中度依赖"};
		}
		if(hdVal=="4"){
			hdpf=10;
			hddj={key:"4",text:"不能自理"};
		}
		form.findField("HDPF").setValue(hdpf);
		form.findField("HDDJ").setValue(hddj);
		this.totalPF();
	}
});
$package("chis.application.dbs.script.risk")
$import("util.Accredit", "chis.script.BizModule")

chis.application.dbs.script.risk.DiabetesRiskConfirmForm = function(cfg) {
	cfg.fldDefaultWidth = 170;
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	chis.application.dbs.script.risk.DiabetesRiskConfirmForm.superclass.constructor.apply(
			this, [cfg])
	this.colCount = 2;
	this.height = 110;
	this.width = 300
	this.title = "核实结果";
	
}
Ext.extend(chis.application.dbs.script.risk.DiabetesRiskConfirmForm,	
		chis.script.BizModule, {
			 initPanel : function(){  	
				var combox = util.dictionary.SimpleDicFactory.createDic({id:'chis.dictionary.diabetesRiskConfirmResult',width:150,editable:false})
		        combox.name = 'result'
		        combox.fieldLabel = '核实结果'
		        combox.allowBlank = false
//		        combox.on("select",this.initPropGrid,this)
				this.combox = combox
				var form = new Ext.FormPanel({
					labelWidth: 75, 
					width:280,
					height:100,
			        frame:true,
			        labelAlign:'left',
			        tbar:[{text:'确定',iconCls:'save',handler:this.doSaveItem,scope:this},{text:'取消',iconCls:'common_cancel',handler:this.doCancel,scope:this}],
			        defaultType: 'textfield',
			        items: [combox]
			    });
			    this.form = form
			    return form;
			 }
			 ,
			 doCancel:function(){
			 		this.win.hide()
			 }
			 ,
			 doSaveItem:function(){
			 	this.exContext.args.result = this.combox.getValue()
			 	var msg = ""
			 	if(this.exContext.args.result == '1'){
			 		msg = "确认高危人群?"
			 	}else{
			 		msg = "排除高危人群?"
			 	}
			 	Ext.Msg.confirm("高危审核",msg,function(btn){
					if(btn == "yes"){
						util.rmi.jsonRequest({
							serviceId:"chis.diabetesRiskService",
							method:"execute",
							serviceAction:"saveConfirmDiabetesRisk",
							body:this.exContext.args
						},function(code,msg,json){
							if(code > 300){
								Ext.Msg.alert("错误",msg);
							}
							this.win.hide()
							this.fireEvent("save",this.entryName,this.exContext.args)
						},this)
					}
				},this);
			 }
		});
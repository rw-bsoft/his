$package("phis.application.cic.script")

$import("phis.script.SimpleForm", "util.Accredit")

phis.application.cic.script.SkinTestRecordForm = function(cfg) {
	cfg.width = 250
	cfg.height = 300
	phis.application.cic.script.SkinTestRecordForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.SkinTestRecordForm,
		phis.script.SimpleForm, {
			initPanel : function() {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var radiogroup = new Ext.form.RadioGroup({
							hideLabel : true,
							items : [{
										boxLabel : '阴性',
										inputValue : -1,
										name : "PSJG"
									}, {
										boxLabel : '阳性',
										name : "PSJG",
										inputValue : 1
									}, {
										boxLabel : '未知',
										name : "PSJG",
										inputValue : 0
									}]
						});
				this.radiogroup = radiogroup;
				this.form = new Ext.FormPanel({
							// unless overridden
							frame : true,
							height : 80,
							items : [radiogroup],
							buttonAlign : "center",
							buttons : [{
										text : '确定',
										handler : this.doSave,
										scope : this
									}, {
										text : '取消',
										handler : this.doCancel,
										scope : this
									}]
						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			doSave : function() {
				if (!this.radiogroup.getValue()) {
					MyMessageTip.msg("提示", "请先选择皮试结果信息!", true);
					return;
				}
				var psjg = this.radiogroup.getValue().inputValue;
				this.form.el.mask("保存中...");
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveSkinTestResult",
							body : {
								SBXH : this.sbxh,
								YPXH : this.ypxh,
								CFSB : this.cfsb,
								PSJG : psjg,
								BRID : this.exContext.ids.brid
							}
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code < 300) {
								var info = "皮试结果保存成功!";
								MyMessageTip.msg("提示", info, true);
								this.win.hide();
								this.fireEvent("doSave", "3");
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
				
				//与公卫业务联动开始，过敏药品添加到个人既往史中
				//如果皮试结果不是阳性或者过敏类别为空则不写到既往史
			    if(psjg!=1||!this.gmywlb)
			    	return;	

			    //如果存在公卫系统则保存到公卫个人既往史中
			    if(!this.SFQYGWXT){
				var publicParam = {
						"commons" : ['SFQYGWXT']
					}
				this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
			    }	
			//如果存在公卫系统，并且皮试结果是阳性则保存到公卫个人既往史中
			if(this.SFQYGWXT=='1'&&this.mainApp.chisActive){
				
			var brempiid=this.exContext.empiData.empiId;
			var nowDate=(new Date()).format('Y-m-d');
			var gmywlb='0109';
			var gmywlbtext='其他';
			var gmywlbs={'1':'0102',
					     '2':'0103',
					     '3':'0104'
						}
			if(gmywlbs.hasOwnProperty(this.gmywlb))
				gmywlb=gmywlbs[this.gmywlb];
			if(this.gmywlbtext)
			{
				gmywlbtext=this.gmywlbtext;
			}
			//保存既往史
			util.rmi.miniJsonRequestAsync({
				serviceId : "chis.healthRecordService",
				serviceAction : "savePastHistoryHis",
				schema:'chis.application.hr.schemas.EHR_PastHistory',
				op:'create',
				body : {
					empiId:brempiid, 
					record:[
					{
						empiId:brempiid,
						pastHisTypeCode_text:'药物过敏史', 
						pastHisTypeCode:'01',
						methodsCode:'', 
						protect:'', 
						diseaseCode:gmywlb,
						diseaseText:gmywlbtext, 
						vestingCode:'', 
						startDate:'', 
						endDate:'', 
						confirmDate:nowDate, 
						recordUnit:this.mainApp.deptId, 
						recordUser:this.mainApp.uid, 
						recordDate:nowDate, 
						lastModifyUser:this.mainApp.uid, 
						lastModifyUnit:this.mainApp.deptId, 
						lastModifyDate:nowDate
					   }
					   ], 
					   delPastId:[]	
				}
			}, function(code, msg, json) {
				if (code < 300) {	
				} else {
					this.processReturnMsg(code, msg);
				}
			}, this);
			
			}	
			//与公卫业务联动结束
			},
			doCancel : function() {
				this.win.hide();
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : false,
								minimizable : false,
								maximizable : false,
								resizable : false,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				return win;
			}
		});
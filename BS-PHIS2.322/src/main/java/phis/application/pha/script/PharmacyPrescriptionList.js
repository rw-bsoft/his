$package("phis.application.pha.script")
$import("phis.script.SimpleList")
//$styleSheet("phis.resources.css.app.biz.style")
phis.application.pha.script.PharmacyPrescriptionList = function(cfg) {
	var defaultCfg = {
		searchFormWidth : 360,
		searchFormHeight : 20,
		searchFormId :'phis_mds_pharmacy_prescription_audit_list',
		bodyStyle : 'border:0px',
		enableRowBody : true,
		headerGroup : true,
		modal : true,
		bindGrid : null
	};
	Ext.apply(cfg, defaultCfg);
	cfg.entryName="phis.application.pha.schemas.YF_CF01_SH";
	cfg.serviceId="phis.pharmacyManageService";
	cfg.serviceAction="queryPrescription";
	phis.application.pha.script.PharmacyPrescriptionList.superclass.constructor.apply( this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyPrescriptionList, phis.script.SimpleList, {
			initPanel:function(sc){
				var grid = phis.application.pha.script.PharmacyPrescriptionList.superclass.initPanel.call(this,sc);
				this.grid = grid;
				return grid;
			},
			getCndBar : function(items) {
				var cfhmText=new Ext.form.TextField({
					fieldLabel : '处方号', 
					name : 'cfhm',
				    labelWidth : 45,
					columnWidth : .37,
					regex : /^[0-9]{0,10}$/
				})
				this.cfhmText=cfhmText;
				var titi =  {
						xtype : "label",
						text : "处方号∶ "
					};
				var barLeft = [];
				var radio1=new Ext.form.Radio({
						xtype : "radio",
						checked : true,
						boxLabel : '未审核',
						inputValue : 0,
						name : "sfjg",
						id : "write" ,
						clearCls : true
					}); 
				 radio1.on('check', this.radioChange, this); 
				this.radio1=radio1; 
				var radio2=new Ext.form.Radio({
						xtype : "radio",
						checked : false,
						boxLabel : '未通过',
						inputValue : 2,
						name : "sfjg",
						id : "nopass",  
						listeners : {
							check : function(group,checked) {
							}
			            },
			            clearCls : true
					});  
				radio2.on('check', this.radioChange, this); 
				this.radio2=radio2;
				var radio3=new Ext.form.Radio({
					 	xtype : "radio",
						checked : false,
						boxLabel : '已通过',
						inputValue : 1,
						name : "sfjg",
						id : "pass" ,
						clearCls : true
					}
				);
				radio3.on('check', this.radioChange, this);
				this.radio3=radio3;
				barLeft.push(radio1);
				barLeft.push(radio2);
				barLeft.push(radio3);
				this.barLeft=barLeft; 
				var simple = new Ext.form.FormPanel({
					id : this.searchFormId,
					frame : false,
					layout : 'column',
					bodyStyle : this.bodyStyle,
					width : this.searchFormWidth,
					height : this.searchFormHeight,
					items : [titi,cfhmText,
					new Ext.Button({
						iconCls : 'query',
						handler : function() {
							this.loadData();
						},
						columnWidth : .08,
						scope : this
					}),
					barLeft
					]
				});
				this.simple = simple; 
				return [simple];
			},
			//生成查询框
			initConditionFields : function() {
				var items = [];
				var conditionField = [];
				var cfhm = new Ext.form.TextField({ 
					fieldLabel : '处方号', 
					name : 'cfhm',
					labelWidth : 45,
					columnWidth : .37,
					regex : /^[0-9]{0,10}$/
				});
				var searchBtn = new Ext.Button({
					iconCls : 'query',
					handler : function() {
						this.loadData();
					},
					columnWidth : .08,
					scope : this
				});
				conditionField.push(cfhm);
				conditionField.push(searchBtn);
				//审核状态单选选择框
				var barLeft = [];
				var radio1=new Ext.form.Radio({
						xtype : "radio",
						checked : true,
						boxLabel : '未审核',
						inputValue : 0,
						name : "sfjg",
						id : "write" ,
						scope : this
					});
				this.radio1=radio1;
				var radio2=new Ext.form.Radio({
						xtype : "radio",
						checked : false,
						boxLabel : '未通过',
						inputValue : 2,
						name : "sfjg",
						id : "nopass" ,
						scope : this
					});
				
				this.radio2=radio2;
				var radio3=new Ext.form.Radio({
						xtype : "radio",
						checked : false,
						boxLabel : '已通过',
						inputValue : 1,
						name : "sfjg",
						id : "pass" ,
						scope : this
					}
				);
				this.radio3=radio3;
				barLeft.push(radio1);
				barLeft.push(radio2);
				barLeft.push(radio3);
				this.barLeft=barLeft;
				conditionField.push(barLeft);   
				for (var i = 0; i < conditionField.length; i++) {
					var config = {};
					Ext.apply(config, conditionField[i]);
					config.xtype = config.xtype || 'textfield',
					config.anchor = config.anchor || '99%';
					items[i] = {
						layout : 'form',
						labelWidth : conditionField[i].labelWidth || 60,
						bodyStyle : this.bodyStyle,
						columnWidth : conditionField[i].columnWidth || 0.25,
						items : [config]
					};
				} 
				return items;
			},
			radioChange : function(group, checked) {
				var status = checked.inputValue;
				this.fireEvent('radioChange', status); 
				this.loadData();
			},
			//处方列表信息
			loadData : function(){
				this.requestData.body = this.getQueryParams();
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.serviceAction;
				phis.application.pha.script.PharmacyPrescriptionList.superclass.loadData.call(this);
			},
			getQueryParams : function() {
				var params = {}; 
				if(this.cfhmText.getValue()&&this.cfhmText.getValue()!=null&&this.cfhmText.getValue()!=""){
					params["cfhm"]=this.cfhmText.getValue();
				}
				if(this.getRadioValue() !=null){  
					params["sfjg"]=this.getRadioValue();
					}
				//alert(Ext.encode(params));
				return params;
			},getRadioValue : function() {
				var radiovalue1= this.radio1.getValue();
				var radiovalue2= this.radio2.getValue();
				var radiovalue3= this.radio3.getValue();
				var backValue=0;
				if(radiovalue1==true){
					backValue=0;
				}else if(radiovalue2==true){
					backValue=2;
				}else if(radiovalue3==true){
					backValue=1;
				}
				return backValue;
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if(r == null) {
					this.fireEvent("prescriptionClick", null);
					return;
				}
				var data = r.data;
				Ext.apply(data, this.requestData.body);
				this.fireEvent("prescriptionClick", data);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					this.onRowClick();
					return;
				}
				if (!this.selectedIndex || this.selectedIndex > records.length - 1) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex =0;
					this.onRowClick();
				}
			}
		});
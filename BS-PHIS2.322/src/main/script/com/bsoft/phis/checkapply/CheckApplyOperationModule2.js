$package("com.bsoft.phis.checkapply")
$import("com.bsoft.phis.SimpleModule")

com.bsoft.phis.checkapply.CheckApplyOperationModule2 = function(cfg) {
	com.bsoft.phis.checkapply.CheckApplyOperationModule2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyOperationModule2,
		com.bsoft.phis.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							// layout : 'border',
							layout : {
								type : 'hbox',
								align : 'stretch'
							},
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "�������",
										flex : 1,
										height : 300,
										width : 200,
										items : this.getCheckTypeList(),
										tbar : this.getCheckTypeTbar()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "��鲿λ",
										flex : 1,
										height : 300,
										width : 200,
										items : this.getCheckPointList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "�����Ŀ",
										flex : 1,
										height : 300,
										width : 320,
										items : this.getCheckProjectList(),
										tbar: this.getCheckProjectTbar()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "�շ���ϸ",
										flex : 1,
										height : 300,
										items : this.getFeeDetailsList()
									}]
						});
				this.panel = panel;
				this.changeRadio(3);//Ĭ���ȼ��ط���
				return panel;
			},
			getCheckTypeList : function() {
				this.checkTypeList = this.createModule("checkTypeList",
						this.refCheckTypeList);
				this.checkTypeList.opener = this;
				this.checkTypeGrid = this.checkTypeList.initPanel();
				return this.checkTypeGrid;

			},
			getCheckPointList : function() {
				this.checkPointList = this.createModule("checkPointList",
						this.refCheckPointList);
				this.checkPointList.opener = this;
				this.checkPointGrid = this.checkPointList.initPanel();
				return this.checkPointGrid;

			},
			getCheckProjectList : function() {
				this.checkProjectList = this.createModule("checkProjectList",
						this.refCheckProjectList);
				this.checkProjectList.opener = this;
				this.checkProjectGrid = this.checkProjectList.initPanel();
				return this.checkProjectGrid;
			},
			getFeeDetailsList : function() {
				this.feeDetailsList = this.createModule("feeDetailsList",
						this.refFeeDetailsList);
				this.feeDetailsList.opener = this;
				this.feeDetailsGrid = this.feeDetailsList.initPanel();
				return this.feeDetailsGrid;
			},
			getCheckTypeTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.RadioGroup({
							id : "sslx2",
							disabled : false,
							height : 20,
							width : 200,
							value : 3,
							items : [{
										boxLabel : 'B��',
										name : 'sslx',
										inputValue : 3
								}
//										, {
//										boxLabel : '����',
//										name : 'sslx',
//										inputValue : 2
//									}, {
//										boxLabel : 'B��',
//										name : 'sslx',
//										inputValue : 3
//									}
											],
							listeners : {
								change : function(group, newValue, oldValue) {
									this.changeRadio(newValue.inputValue);
								},
								scope : this
							}
						}));
				return tbar;
			},
			getCheckProjectTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "ƴ������"
						}));
				tbar.push(new Ext.form.TextField({
							id : "xmpydm",
							width : 100
						}));
				tbar.push({
							xtype : "button",
							text : "��ѯ",
							iconCls : "query",
							scope : this.checkProjectList,
							handler : this.checkProjectList.refresh
						});
				return tbar;
			},
			onWinShow : function(){
				alert(1)
			},
			changeRadio : function(inputValue){
				this.checkTypeList.sslx = inputValue;//������ҳ�����
				this.checkTypeList.refresh();
				this.checkPointList.store.removeAll();
				this.checkProjectList.store.removeAll();
				var form = this.opener.opener.checkApplyForm.form.getForm();
				if(inputValue!=1){
					form.findField("XJ").hide();
					form.findField("XJ").setValue("");
					form.findField("XL").hide();
					form.findField("XL").setValue("");
					form.findField("XY").hide();
					form.findField("XY").setValue("");
					form.findField("XLV").hide();
					form.findField("XLV").setValue("");
					form.findField("XLSJ").hide();
					form.findField("XLSJ").setValue("");
					form.findField("XGJC").hide();
					form.findField("XGJC").setValue("");
					form.findField("BZXX").setValue("");										
				}else{
					form.findField("XJ").show();
					form.findField("XL").show();
					form.findField("XY").show();
					form.findField("XLV").show();
					form.findField("XLSJ").show();
					form.findField("XGJC").show();
					form.findField("BZXX").setValue("�������ڷ�ë�ػ���     ���ڹ�    �ˣ����ڷ���/��ͣ��);   �����������ļ������ɣ��ʣ���Ӱ���ҩƷ��������:");
				}
			}
		})

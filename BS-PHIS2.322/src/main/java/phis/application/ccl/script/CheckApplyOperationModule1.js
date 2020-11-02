$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyOperationModule1 = function(cfg) {
	phis.application.ccl.script.CheckApplyOperationModule1.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyOperationModule1,
		phis.script.SimpleModule, {
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
						title : "检查类型",
						flex : 1,
						height : 300,
						width : 220,
						items : this.getCheckTypeList(),
						tbar : this.getCheckTypeTbar()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : "检查部位",
						flex : 1,
						height : 300,
						width : 200,
						items : this.getCheckPointList()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : "检查项目",
						flex : 1,
						height : 300,
						width : 320,
						items : this.getCheckProjectList(),
						tbar : this.getCheckProjectTbar()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : "收费明细",
						flex : 1,
						height : 300,
						items : this.getFeeDetailsList()
					}]
				});
				this.panel = panel;
				this.changeRadio(3);// 默认先加载B超
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
				var data=[['B超','3'],['放射','2'],['心电','1'],['胃肠镜','4']];
				var store = new Ext.data.SimpleStore({
						fields: ['text', 'value'],
						data : data
					});						
				tbar.push(
					new Ext.form.ComboBox({		
						id : "sslx1",
						store : store,
						valueField : "value",
						displayField : "text",
						editable : false,
						selectOnFocus : true,
						triggerAction : 'all',
						mode : 'local',
						value : 3,
						listeners : {
							select : function(group, newValue, oldValue) {
								debugger
								this.changeRadio(newValue.data.value);
							},
							scope : this
						}
					})
				);
				return tbar;
			},
			getCheckProjectTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
					text : "拼音代码"
				}));
				tbar.push(new Ext.form.TextField({
					id : "xmpydm",
					width : 100
				}));
				tbar.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this.checkProjectList,
					handler : this.checkProjectList.refresh
				});
				return tbar;
			},
			onWinShow : function() {
				alert(1)
			},
			changeRadio : function(inputValue) {
				this.checkTypeList.sslx = inputValue;// 供其他页面调用
				this.checkTypeList.refresh();
				this.checkPointList.store.removeAll();
				this.checkProjectList.store.removeAll();
				var form = this.opener.opener.checkApplyForm.form.getForm();
				if (inputValue == 3) {
					/** ***************门诊首页获取的病历信息******************** */
					var zsxx = Ext.getCmp("ZSXX").getValue();// 获取的主诉信息
					var xbs = Ext.getCmp("XBS").getValue();// 获取的现病史
					var jws = Ext.getCmp("JWS").getValue();// 获取的既往史
					var gms = Ext.getCmp("GMS").getValue();// 获取的过敏史
					var fzjc = Ext.getCmp("FZJC").getValue();// 获取的辅助检查
					var tgjc = Ext.getCmp("TGJC").getValue();// 获取的体格检查
					//var t = document.getElementById("T").value;// 体温
				//	var r = document.getElementById("R").value;// 呼吸频率
					//var p = document.getElementById("P").value;// 脉搏
					//var ssy = document.getElementById("SSY").value;// 收缩压
					//var szy = document.getElementById("SZY").value;// 舒张压
					//var height = document.getElementById("H").value;// 身高
					//var weight = document.getElementById("W").value;// 体重
					//var ct = "T:" + t + "℃    P:" + p + "次/分   R:" + r
						//	+ "次/分    BP:" + ssy + " / " + szy + "mmHg ";
					//var syxx = Ext.getCmp("FZJC").getValue();// 实验室和器材检查
					/** ************************************************ */
					form.findField("BRXM")
							.setValue(this.opener.opener.checkApplyForm.brxm);
					form.findField("LCZD")
							.setValue(this.opener.opener.checkApplyForm.zdmc);
					form.findField("ZDYS")
							.setValue(this.opener.opener.checkApplyForm.zdys);
					form.findField("ZSXX").setValue(zsxx);
					form.findField("XBS").setValue(xbs);
					form.findField("JWS").setValue(jws);
					form.findField("GMS").setValue(gms);
					form.findField("FZJC").setValue(fzjc);
					form.findField("TGJC").setValue(tgjc);
					// form.findField("XJ").hide();
					// form.findField("XJ").setValue("");
					form.findField("XL").hide();
					form.findField("XL").setValue("");
					// form.findField("XY").hide();
					// form.findField("XY").setValue("");
					form.findField("XLV").hide();
					form.findField("XLV").setValue("");
					// form.findField("XLSJ").hide();
					// form.findField("XLSJ").setValue("");
					// form.findField("XGJC").hide();
					// form.findField("XGJC").setValue("");
					form.findField("BZXX").setValue("");
				} else if (inputValue == 2) {
					// form.findField("CTXX").setValue(ct);
					//form.findField("SYXX").setValue(syxx);
					// form.findField("XJ").show();
					form.findField("XL").show();
					// form.findField("XY").show();
					form.findField("XLV").show();
					// form.findField("XLSJ").show();
					// form.findField("XGJC").show();
					// form.findField("BZXX").setValue("备孕及已孕请勿X线检查");
					form.findField("BZXX").setValue("");
					//form.findField("TW").setValue(t);
				//	form.findField("HXPL").setValue(r);
					//form.findField("MB").setValue(p);
					//form.findField("SSY").setValue(ssy);
					//form.findField("SZY").setValue(szy);
				} else {
					// form.findField("XJ").show();
					form.findField("XL").show();
					// form.findField("XY").show();
					form.findField("XLV").show();
					// form.findField("XLSJ").show();
					// form.findField("XGJC").show();
					// form.findField("BZXX").setValue("近两周内服毛地黄量 天内共
					// 克（正在服用/已停用); 天内其他对心肌或心律（率）有影响的药品及剂量有:");
					form.findField("BZXX").setValue("");
					//form.findField("TW").setValue(t);
					//form.findField("HXPL").setValue(r);
					//form.findField("MB").setValue(p);
					//form.findField("SSY").setValue(ssy);
					//form.findField("SZY").setValue(szy);
				}
			}
		})

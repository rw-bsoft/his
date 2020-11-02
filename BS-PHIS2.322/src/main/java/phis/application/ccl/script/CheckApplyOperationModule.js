$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyOperationModule = function(cfg) {
	phis.application.ccl.script.CheckApplyOperationModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyOperationModule,
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
										width : 230,
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
										tbar: this.getCheckProjectTbar()
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
				this.changeRadio(3);//默认先加载B超
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
				//alert(this.openby);
				if(this.openby=="CIC"){
					tbar.push(new Ext.form.RadioGroup({
						id : "sslx",
						disabled : false,
						height : 20,
						width : 200,
						value : 2,
						items : [{
									boxLabel : '放射',
									name : 'sslx',
									inputValue : 2
							}
//									, {
//									boxLabel : '放射',
//									name : 'sslx',
//									inputValue : 2
//								}, 
							/*{
									boxLabel : 'B超',
									name : 'sslx',
									inputValue : 3
								}*/
										],
						listeners : {
							change : function(group, newValue, oldValue) {
								this.changeRadio(newValue.inputValue);
							},
							scope : this
						}
					}));
				}else if(this.openby=="WAR"){
					tbar.push(new Ext.form.RadioGroup({
						id : "sslx",
						disabled : false,
						height : 20,
						width : 230,
						value : 3,
						items : [
							{
									boxLabel : 'B超',
									name : 'sslx',
									inputValue : 3
							}
									, 
										{
									boxLabel : '放射',
									name : 'sslx',
									inputValue : 2
								}
								, 
							{
									boxLabel : '心电',
									name : 'sslx',
									inputValue : 1
								}
								, 
							{
									boxLabel : '胃肠镜',
									name : 'sslx',
									inputValue : 4
								}
										],
						listeners : {
							change : function(group, newValue, oldValue) {
								this.changeRadio(newValue.inputValue);
							},
							scope : this
						}
					}));
				}
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
			onWinShow : function(){
				alert(1)
			},
			changeRadio : function(inputValue){
				this.checkTypeList.sslx = inputValue;//供其他页面调用
				this.checkTypeList.refresh();
				this.checkPointList.store.removeAll();
				this.checkProjectList.store.removeAll();
				var form = this.opener.opener.checkApplyForm.form.getForm();
				var zyh =this.opener.opener.checkApplyForm.zyh;//住院号
				var res = util.rmi.miniJsonRequestSync({
										serviceId : "phis.checkApplyService",
										serviceAction : "getZsxx_WAR",
										body : {
											zyh : zyh	
										}
									});
			 if (res.json.code > 200) {		
			 	Ext.MessageBox.alert("提示","病人检查信息查询失败");
			  }
				if(inputValue!=1){
					debugger;
					form.findField("BRXM").setValue(this.opener.opener.checkApplyForm.brxm);
					form.findField("RYZD").setValue(this.opener.opener.checkApplyForm.jbmc);
					form.findField("ZRYS").setValue(this.opener.opener.checkApplyForm.zrys);
					if(res.json.body != null){
					form.findField("ZSXX").setValue(res.json.body["ZSXX"]);
					form.findField("XBS").setValue(res.json.body["XBS"]);
					form.findField("JWS").setValue(res.json.body["JWS"]);
					form.findField("GMS").setValue(res.json.body["GMS"]);
					form.findField("FZJC").setValue(res.json.body["FZJC"]);
					form.findField("TGJC").setValue(res.json.body["TGJC"]);
					form.findField("BZXX").setValue(res.json.body["BZXX"]);
				   }
					form.findField("XL").hide();
					form.findField("XL").setValue("");
					form.findField("XLV").hide();
					form.findField("XLV").setValue("");										
				}else{
					form.findField("BRXM").setValue(this.opener.opener.checkApplyForm.brxm);
					form.findField("RYZD").setValue(this.opener.opener.checkApplyForm.jbmc);
					form.findField("ZRYS").setValue(this.opener.opener.checkApplyForm.zrys);
				if(res.json.body != null){
					form.findField("ZSXX").setValue(res.json.body["ZSXX"]);
					form.findField("XBS").setValue(res.json.body["XBS"]);
					form.findField("JWS").setValue(res.json.body["JWS"]);
					form.findField("GMS").setValue(res.json.body["GMS"]);
					form.findField("FZJC").setValue(res.json.body["FZJC"]);
					form.findField("TGJC").setValue(res.json.body["TGJC"]);
					form.findField("BZXX").setValue(res.json.body["BZXX"]);
				   }
					form.findField("XL").show();
					form.findField("XLV").show();
					form.findField("XL").setValue(res.json.body["XL"]);
					form.findField("XLV").setValue(res.json.body["XLV"]);	
				}
			}
		})

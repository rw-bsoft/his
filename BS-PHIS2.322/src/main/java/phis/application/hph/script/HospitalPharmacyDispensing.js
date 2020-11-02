/**
 * 医嘱发药
 * 
 * @author caijy
 */
$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyDispensing = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.hph.script.HospitalPharmacyDispensing.superclass.constructor.apply(
			this, [cfg]);
	this.yzlx=0;
	this.yzlxls=1;
	this.yzlxcq=1;
}
Ext.extend(phis.application.hph.script.HospitalPharmacyDispensing,
		phis.script.SimpleModule, {
			F2 : function()  {
				this.doFy();
			},
			initPanel : function() {
				
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.initializationServiceID,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				// 是否维护领药科室验证
				var ret_lyks = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryMedicineDepartmentActionID
						});
				if (ret_lyks.code > 300) {
					this.processReturnMsg(ret_lyks.code, ret_lyks.msg, this.initPanel);
					return null;
				}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										// split : true,
										title : '医嘱发药',
										region : 'west',
										width : 400,
										items : this.getLists()
									}, {
										layout : "fit",
										border : false,
										// split : true,
										title : '',
										region : 'center',
										items : this.getRModule()
									}],
							tbar : (this.getTbar() || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			getLists : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("recordClick", this.onRecordClick, this);
				this.list.on("recordClick_br", this.onRecordClick_br, this);
				this.list.on("clear", this.onClear, this);
				this.list.on("BeforeLoadDataLeft", this.onBeforeLoadDataLeft, this);
				this.list.on("LoadDataLeft", this.onLoadDataLeft, this);
				this.list.opener=this;
				return this.list.initPanel();
			},
			getRModule : function() {
				this.module = this.createModule("module", this.refModule);
				this.module.on("checkTab", this.onTabCheck, this);
				this.module.on("loading", this.onLoading, this);
				this.module.on("BeforeLoadDataRight", this.onBeforeLoadDataRight, this);
				this.module.on("loadDataRight", this.onLoadDataRight, this);
				return this.module.initPanel();
			},
			// 左下单击 右边数据查询(提交单)
			onRecordClick : function(r) {
				// this.module.yzlx=this.getRadioValue();
				this.module.yzlx=this.yzlx;
				this.module.showRecord(r,1);
			},
			// 左下单击 右边数据查询(病人)
			onRecordClick_br : function(r) {
				// this.module.yzlx=this.getRadioValue();
				this.module.yzlx=this.yzlx;
				this.module.showRecord(r,2);
			},
			// 发药
			doFy : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要进行发药吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var fymx = this.module.getData("fy");// 病区提交02记录
							if (fymx.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							body["fymx"] = fymx;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveActionID,
										body : body
									});
									this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							if (ret.msg != null && ret.msg.length > 0&&ret.msg!='Success') {
								MyMessageTip.msg("提示", ret.msg + " 库存不够", true);
							}
							// var FYSJ = ret.json.otherRet.FYSJ;
							// var FYBQ = ret.json.otherRet.FYBQ;
							
							var JLID = ret.json.otherRet.JLID;
							this.doIsPrint(JLID);
							this.doSx();
						}
					},
					scope : this
				});
				
			},
			doIsPrint : function(JLID){ // 打印费用明细
				Ext.Msg.show({
					title : "提示",
					msg : "是否打印发药明细信息?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
// var module =
// this.createModule("dispensingDetails","phis.application.hph.HPH/HPH/HPH0103");
// module.JLID=JLID;
// module.BS=1;
// module.FYSJ=FYSJ;
// module.FYBQ=FYBQ;
// module.initPanel();
// module.doPrint();
				var url="resources/phis.prints.jrxml.DispensingDetails.print?type=1&JLID="+JLID;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
						}else {
							return true;
						}
					},
					scope : this
				});
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.panel.getTopToolbar();
				} else {
					btns = this.panel.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 汇总发药
			doYzhzfy : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要进行汇总发药吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var fymx = this.module.getData("fy");// 病区提交02记录
							if (fymx.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							body["fymx"] = fymx;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveActionID,
										body : body
									});
									this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							if (ret.msg != null && ret.msg.length > 0&&ret.msg!='Success') {
								MyMessageTip.msg("提示", ret.msg + " 库存不够", true);
							}
							// var FYSJ = ret.json.otherRet.FYSJ;
							// var FYBQ = ret.json.otherRet.FYBQ;
							
							var JLID = ret.json.otherRet.JLID;
							this.doIsHzPrint(JLID);
							this.doSx();
						}
					},
					scope : this
				});
				
			},
			//汇总发药打印
			doIsHzPrint : function(JLID){ // 打印费用明细
				Ext.Msg.show({
					title : "提示",
					msg : "是否打印发药汇总信息?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
// var module =
// this.createModule("dispensingDetails","phis.application.hph.HPH/HPH/HPH0103");
// module.JLID=JLID;
// module.BS=1;
// module.FYSJ=FYSJ;
// module.FYBQ=FYBQ;
// module.initPanel();
// module.doPrint();
				var url="resources/phis.prints.jrxml.DispensingDetailsHZ.print?type=1&JLID="+JLID;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
						}else {
							return true;
						}
					},
					scope : this
				});
			},
			// 选择汇总发药, 发药按钮变灰
			onTabCheck : function(tag) {
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thbq"], false);
				if(tag==1){
					if(this.module.getData("fy").length>0){
					this.setButtonsState(["fy"], true);
				}
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thbq"], false);
				this.setButtonsState(["yzhzfy"], false);
				}else if(tag==3){
					if(this.module.getData("fy").length>0){
						this.setButtonsState(["yzhzfy"], true);
					}
				}else{
				this.setButtonsState(["yzhzfy"], false);
				this.setButtonsState(["fy"], false);
				if(this.module.getData("th").length>0){
				this.setButtonsState(["qt"], true);
				this.setButtonsState(["thbq"], true);}
				}
				
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			// 刷新
			doSx : function() {
				// this.yzlx = this.getRadioValue();
				this.list.yzlx=this.yzlx;
				this.list.doNew();
				this.module.doNew();
				this.module.tab.activate(0);
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thbq"], false);
				this.setButtonsState(["yzhzfy"], false);
			},
			// 全退
			doQt : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要将所选医嘱全都退回病区吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的记录", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveFullRefundActionID,
										body : body
									});
									this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							this.doSx();
						}
					},
					scope : this
				});
			},
			// 退回病区
			doThbq : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要将所选医嘱退回病区吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的药", true);
								return;
							}
							var fymx = this.module.getData("th");// 病区提交02记录
							if (fymx.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的药", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							body["fymx"] = fymx;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveRefundActionID,
										body : body
									});
									this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							this.doSx();
						}
					},
					scope : this
				});
			},
			onReady:function(){
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thbq"], false);
				this.setButtonsState(["yzhzfy"], false);
			},
			onLoading:function(tag){
			if(tag==1){
			this.panel.el.mask("正在加载数据...", "x-mask-loading");
			}else{
			this.panel.el.unmask();
			}
			},
			onClear:function(){
			this.module.doNew();
			this.module.tab.activate(0);
			},
			getTbar : function() {
						var radio1 = new Ext.form.Radio({
							xtype : "radio",
							checked : true,
							boxLabel : '全部',
							inputValue : 0,
							name : "yzlxfy",
							id : "qb",
							clearCls : true
						});
						this.radio1 = radio1;
						radio1.on('check', this.onChange1, this);
						var checkbox1 = new Ext.form.Checkbox({
							id : "ls",
							checked : true,
							boxLabel : '临时',
							inputValue : 1,
							clearCls : true
						});
						this.checkbox1 = checkbox1;
						checkbox1.on('check', this.onChange2, this);
						var checkbox2 = new Ext.form.Checkbox({
							id : "cq",
							checked : true,
							boxLabel : '长期',
							inputValue : 2,
							clearCls : true
						});
						this.checkbox2 = checkbox2;
						checkbox2.on('check', this.onChange3, this);
						var radio2 = new Ext.form.Radio({
							xtype : "radio",
							checked : false,
							boxLabel : '急诊',
							inputValue : 3,
							name : "yzlxfy",
							id : "jz",
							clearCls : true
						});
						this.radio2 = radio2;
						radio2.on('check', this.onChange4, this);
						var radio3 = new Ext.form.Radio({
							xtype : "radio",
							checked : false,
							boxLabel : '带药',
							inputValue : 4,
							name : "yzlxfy",
							id : "dy",
							clearCls : true
						});
						this.radio3 = radio3;
						radio3.on('check', this.onChange5, this);
						// this.xradio.on("change", this.onChange, this);
						// return [this.xradio];
						return [ radio1, checkbox1, checkbox2, radio2,radio3 ]
					},
					onChange1 : function(radiofield, oldvalue) {
						var radiovalue1 = this.radio1.getValue();
						if(radiovalue1==true){
							this.yzlxls=1;
							this.yzlxcq=1;
							this.checkbox1.setValue(true);
							this.checkbox2.setValue(true);
						}
					},
					onChange2 : function(radiofield, oldvalue) {
						var checkboxvalue1 = this.checkbox1.getValue();
						if(checkboxvalue1==true){
							this.yzlxls=1;
							if(this.yzlxcq==1){
								this.radio1.setValue(true);
								this.yzlx=0;
							}else{
								this.radio2.setValue(false);
								this.radio3.setValue(false);
								this.yzlx=1;
							}
						}else{
							this.yzlxls=0;
							this.radio1.setValue(false);
							if(this.checkbox2.getValue()==true){
								if( this.radio2.getValue()==false&&this.radio3.getValue()==false){
									this.yzlx=2;
									this.yzlxcq=1;
								}else{
									this.yzlx=-1;
									this.yzlxcq=0;
									if(this.radio2.getValue()==true){
										this.yzlx=3;
									}
									if(this.radio3.getValue()==true){
										this.yzlx=4;
									}
								}
							}else{
								this.yzlx=-1;
								this.yzlxcq=0;
								if(this.radio2.getValue()==true){
									this.yzlx=3;
								}
								if(this.radio3.getValue()==true){
									this.yzlx=4;
								}
							}
						}
						this.doSx();
					},
					onChange3 : function(radiofield, oldvalue) {
						var checkboxvalue2 = this.checkbox2.getValue();
						if(checkboxvalue2==true){
							this.yzlxcq=1;
							if(this.yzlxls==1){
								this.radio1.setValue(true);
								this.yzlx =0
							}else{
								this.radio2.setValue(false);
								this.radio3.setValue(false);
								this.yzlx = 2;
							}
						
						}else{
							this.yzlxcq=0;
							this.radio1.setValue(false);
							if(this.checkbox1.getValue()==true){
								if( this.radio2.getValue()==false&&this.radio3.getValue()==false){
									this.yzlx=1;
									this.yzlxls=1;
								}else{
									this.yzlx=-1;
									this.yzlxls=0;
									if(this.radio2.getValue()==true){
										this.yzlx=3;
									}
									if(this.radio3.getValue()==true){
										this.yzlx=4;
									}
								}
							}else{
								this.yzlx=-1;
								this.yzlxls=0;
								if(this.radio2.getValue()==true){
									this.yzlx=3;
								}
								if(this.radio3.getValue()==true){
									this.yzlx=4;
								}
							}
						}
						this.doSx();
					},
					onChange4 : function(radiofield, oldvalue) {
						var radiovalue2 = this.radio2.getValue();
						if(radiovalue2==true){
							this.checkbox1.setValue(false);
							this.checkbox2.setValue(false);
							if(this.yzlxls==0&&this.yzlxcq==0&&(this.yzlx==-1||this.yzlx==4)){
								this.yzlx=3;
								this.doSx();
							}
						}
					},
					onChange5 : function(radiofield, oldvalue) {
						var radiovalue3 = this.radio3.getValue();
						if(radiovalue3==true){
							this.checkbox1.setValue(false);
							this.checkbox2.setValue(false);
							if(this.yzlxls==0&&this.yzlxcq==0&&(this.yzlx==-1||this.yzlx==3)){
								this.yzlx=4;
								this.doSx();
							}
						}
					},
					onBeforeLoadDataRight:function(){
						this.panel.el.mask("正在加载数据...", "x-mask-loading");
					},
					onLoadDataRight:function(){
						this.panel.el.unmask();
					},
					onBeforeLoadDataLeft:function(){
						this.panel.el.mask("正在加载数据...", "x-mask-loading");
					},
					onLoadDataLeft:function(){
						this.panel.el.unmask();
					}
		});
$package("phis.application.hos.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout","phis.application.yb.script.MedicareCommonMethod","phis.script.Phisinterface")

phis.application.hos.script.HospitalPatientManagementForm = function(cfg) {
	cfg.width = 900;
	cfg.modal = true;
	cfg.remoteUrl = 'YBDisease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.minListWidth = 250;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.hos.script.HospitalPatientManagementForm.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.hos.script.HospitalPatientManagementForm,
		phis.script.SimpleForm, {
			getSQStoS : function(id) {
				var form = this.form.getForm();
				var sqs = form.findField(id+"_SQS");
				var s = form.findField(id+"_S");
				s.setValue("");
				this.setSOnFocus(sqs,s);
				if(id!="JGDM"){
					var x = form.findField(id+"_X");
					x.setValue("");
				}
			},
			getStoX : function(id) {
				var form = this.form.getForm();
				var s = form.findField(id+"_S");
				var x = form.findField(id+"_X");
				x.setValue("");
				this.setXOnFocus(s,x);
			},
			setSOnFocus : function(superObj,obj){
				obj.onFocus = function() {
					util.widgets.MyCombox.superclass.onFocus.call(this);
//					if (this.store.getCount() == 0 || this.mode == 'local') {
					if(superObj.getValue()){
						var id = "phis.dictionary.City";
						var store = util.dictionary.SimpleDicFactory.getStore({
							id : id,
							filter : "['eq',['$','item.properties.superkey'],['s','"
									+ superObj.getValue() + "']]"
						});
						store.load();
						obj.bindStore(store);
						this.focusLoad = true;
					}
				};
			},
			setXOnFocus : function(superObj,obj){
				obj.onFocus = function() {
					util.widgets.MyCombox.superclass.onFocus.call(this);
//					if (this.store.getCount() == 0 || this.mode == 'local') {
					if(superObj.getValue()){
						var id = "phis.dictionary.County";
						var store = util.dictionary.SimpleDicFactory.getStore({
							id : id,
							filter : "['eq',['$','item.properties.superkey'],['s','"
									+ superObj.getValue() + "']]"
						});
						store.load();
						obj.bindStore(store);
						this.focusLoad = true;
					}
				};
			},
			clearSQStoS : function(id){
				var form = this.form.getForm();
				var sqs = form.findField(id+"_SQS");
				var s = form.findField(id+"_S");
				if(!sqs.getValue()){
					s.setValue("");
					if(id!="JGDM"){
						var x = form.findField(id+"_X");
						x.setValue("");
					}
				}
			},
			clearStoX : function(id){
				var form = this.form.getForm();
				var s = form.findField(id+"_S");
				var x = form.findField(id+"_X");
				if(!s.getValue()){
					x.setValue("");
				}
			},
			initPanel : function(sc) {
				sc=null
				this.entryName = "phis.application.hos.schemas.ZY_BRRY_BRGL";
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
					labelWidth : 66, // label settings here cascade
					frame : true,
					width : 1024,
					height : 600,
					autoScroll : true,
					items : [{
						xtype : "label",
						html : "<div style='font-size:20px;font-weight:bold;text-align:center;letter-spacing:20px' >住院病人信息</div>"
					}, {
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part1')
					}, {
						xtype : 'fieldset',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part2')
					}, {
						xtype : 'fieldset',
						title : '基本信息',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part3')
					}, {
						xtype : 'fieldset',
						title : '入院信息',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part7')
					}
					],
					tbar : (this.tbar || []).concat(this.createButton())
				});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				return this.form
			},
			csnyChange : function() {
				var form = this.form.getForm();
				var CSNY = form.findField("CSNY");
				var RYNL = form.findField("RYNL");
				var birthday = CSNY.getValue();
				if (!birthday || birthday == "") {
					RYNL.setValue(0);
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "personAge",
							body : {
								birthday : birthday
							}
						});
				var body = null;
				if (result.json.body) {
					body = result.json.body
					RYNL.setValue(body.ages);
				}
			},
			onReady : function() {
				phis.application.hos.script.HospitalPatientManagementForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var CSNY = form.findField("CSNY");
				CSNY.on("change", this.csnyChange, this)
				var brxz = form.findField("BRXZ");
				brxz.on("select",function(){
					if(brxz.getValue()==6089){
						//
					}
				})
				var csd_sqs = form.findField("CSD_SQS");
				var jgdm_sqs = form.findField("JGDM_SQS");
				var xzz_sqs = form.findField("XZZ_SQS");
				var hkdz_sqs = form.findField("HKDZ_SQS");
				csd_sqs.on("select", function(){this.getSQStoS("CSD")}, this)
				csd_sqs.on("blur", function(){this.clearSQStoS("CSD");}, this)
				jgdm_sqs.on("select", function(){this.getSQStoS("JGDM")}, this)
				jgdm_sqs.on("blur", function(){this.clearSQStoS("JGDM");}, this)
				xzz_sqs.on("select", function(){this.getSQStoS("XZZ")}, this)
				xzz_sqs.on("blur", function(){this.clearSQStoS("XZZ");}, this)
				hkdz_sqs.on("select", function(){this.getSQStoS("HKDZ")}, this)
				hkdz_sqs.on("blur", function(){this.clearSQStoS("HKDZ");}, this)
				var csd_s = form.findField("CSD_S");
				var xzz_s = form.findField("XZZ_S");
				var hkdz_s = form.findField("HKDZ_S");
				var jgdm_s = form.findField("JGDM_S");
				jgdm_s.on("focus",function(){
					if(!jgdm_sqs.getValue()){
						MyMessageTip.msg("提示", "请先选择籍贯地址_省", true);
						return false;
					}
				})
				csd_s.on("select", function(){this.getStoX("CSD")}, this)
				csd_s.on("blur", function(){this.clearStoX("CSD");}, this)
				csd_s.on("focus",function(){
					if(!csd_sqs.getValue()){
						MyMessageTip.msg("提示", "请先选择出生地_省", true);
						return false;
					}
				})
				xzz_s.on("select", function(){this.getStoX("XZZ")}, this)
				xzz_s.on("blur", function(){this.clearStoX("XZZ");}, this)
				xzz_s.on("focus",function(){
					if(!xzz_sqs.getValue()){
						MyMessageTip.msg("提示", "请先选择现住址_省", true);
						return false;
					}
				})
				hkdz_s.on("select", function(){this.getStoX("HKDZ")}, this)
				hkdz_s.on("blur", function(){this.clearStoX("HKDZ");}, this)
				hkdz_s.on("focus",function(){
					if(!hkdz_sqs.getValue()){
						MyMessageTip.msg("提示", "请先选择户口地址_省", true);
						return false;
					}
				})
				var csd_x = form.findField("CSD_X");
				var xzz_x = form.findField("XZZ_X");
				var hkdz_x = form.findField("HKDZ_X");
				csd_x.on("focus",function(){
					if(!csd_s.getValue()){
						MyMessageTip.msg("提示", "请先选择出生地_市", true);
						return false;
					}
				})
				xzz_x.on("focus",function(){
					if(!xzz_s.getValue()){
						MyMessageTip.msg("提示", "请先选择现住址_市", true);
						return false;
					}
				})
				hkdz_x.on("focus",function(){
					if(!hkdz_s.getValue()){
						MyMessageTip.msg("提示", "请先选择户口地址_市", true);
						return false;
					}
				})
				var ybmcs = form.findField("YBMCS");
				if(ybmcs){
					this.ybmcs=ybmcs
					this.ybmcs.on("select",this.ybmcchange,this)
				}
				var ybmc = form.findField("YBMC");
				if(ybmc){
					this.ybmc=ybmc;
				}
			},
			ybmcchange: function() {
				var ybmctemp=this.ybmcs.getValue();
				var ybmcbm=this.ybmc.getValue();
				if(ybmcbm.indexOf(ybmctemp)>=0){
					if(ybmcbm.indexOf(","+ybmctemp+",")>=0 ){
						ybmcbm=ybmcbm.replace(","+ybmctemp+",",",")
					}else if(ybmcbm.indexOf(","+ybmctemp)>0 &&
					(ybmcbm.indexOf(","+ybmctemp)+ybmctemp.length+1)==ybmcbm.length){
						ybmcbm=ybmcbm.replace(","+ybmctemp,"")
					}else if(ybmcbm.indexOf(ybmctemp)==0 && ybmctemp.length==ybmcbm.length){
						ybmcbm="";
					} else{
						ybmcbm=ybmcbm+","+ybmctemp;
					}
				}else{
					ybmcbm=ybmcbm+","+ybmctemp;
				}
				this.ybmc.setValue(ybmcbm);
			},
			initButton : function() {
				this.sbxx=false;
				var btns = this.form.getTopToolbar();
				var transform = btns.find("cmd", "transform");// 转换
				var canceled = btns.find("cmd", "canceled");// 注销
				var save = btns.find("cmd", "save");// 保存
				if (this.cmd == "transform") {
					transform[0].show();
					canceled[0].hide();
					save[0].hide();
				} else if (this.cmd == "update") {
					transform[0].hide();
					canceled[0].hide();
					save[0].show();
				} else if (this.cmd == "canceled") {
					transform[0].hide();
					canceled[0].show();
					save[0].hide();
				} else if (this.cmd == "home") {
					transform[0].hide();
					canceled[0].hide();
					save[0].hide();
				}
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = [{
							"id" : "transform",
							"name" : "转换",
							"iconCls" : "transfer"
						}, {
							"id" : "canceled",
							"name" : "注销",
							"iconCls" : "writeoff"
						}, {
							"id" : "save",
							"name" : "保存"
						},{
							"id" : "print",
							"name" : "打印"
						}, {
							"id" : "cancel",
							"name" : "关闭",
							"iconCls" : "common_cancel",
							"notReadOnly" : true
						}];
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
						continue;
					}
					if (it.mode == "remote") {
						var f = this.createRemoteDicField(it);
						MyItems.push(f);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width
					if (it.id == 'ZYHM') {
						f.labelWidth = 80;
						f.style = 'color:red;background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;'
					} else {
						f.labelWidth = 60;
					}
					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.clearYbxx();//清除医保缓存信息
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPatientManagementService",
							serviceAction : "loadBrxx",
							pkey : this.initDataId,
							body : this.initDataBody
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew()
								this.YBRXZ = json.body.BRXZ;
								this.oldbrxz=json.body.BRXZ;
								this.initFormData(json.body)
								var form = this.form.getForm();
								if (this.cmd == "transform") {
									var BRXZ = form.findField("BRXZ");
									BRXZ.setDisabled(false);
									BRXZ.on("select",this.onbrxzselect,this);
									if (BRXZ.el.parent().parent().parent().first().dom.innerHTML.indexOf("style") >= 0) {
										BRXZ.el.parent().parent().parent().first().dom.innerHTML = BRXZ.el
												.parent().parent().parent()
												.first().dom.innerHTML.toLowerCase().replace(/style=""/,"style=\"color:red\"");
									} else {
										BRXZ.el.parent().parent().parent().first().dom.innerHTML = BRXZ.el
											.parent().parent().parent().first().dom.innerHTML
												.toLowerCase().replace(/span/,"span style=\"color:red\"");
									}
									var BRXM = form.findField("BRXM");
									BRXM.el.parent().parent().first().dom.innerHTML = BRXM.el
											.parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
									var BRKS = form.findField("BRKS");
									BRKS.el.parent().parent().parent().first().dom.innerHTML = BRKS.el
											.parent().parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
									var RYRQ = form.findField("RYRQ")
									RYRQ.el.parent().parent().parent().first().dom.innerHTML = RYRQ.el
											.parent().parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
								} else if (this.cmd == "update") {
									var BRXM = form.findField("BRXM");
									if (BRXM.el.parent().parent()
											.first().dom.innerHTML
											.indexOf("style") >= 0) {
										BRXM.el.parent().parent().first().dom.innerHTML = BRXM.el
												.parent().parent().first().dom.innerHTML
												.toLowerCase().replace(
														/style=""/,
														"style=\"color:red\"");
									} else {
										BRXM.el.parent().parent()
												.first().dom.innerHTML = BRXM.el
												.parent().parent()
												.first().dom.innerHTML
												.toLowerCase()
												.replace(/span/,"span style=\"color:red\"");
									}
									if (json.body.BRCH) {
										var BRKS = form.findField("BRKS");
										BRKS.setDisabled(true);
										BRKS.el.parent().parent().parent()
												.first().dom.innerHTML = BRKS.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase().replace(
														/color:/, "").replace(
														/red/, "");
										var RYRQ = form.findField("RYRQ")
										RYRQ.setDisabled(true);
										RYRQ.el.parent().parent().parent()
												.first().dom.innerHTML = RYRQ.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase().replace(
														/color:/, "").replace(
														/red/, "");
										var BRXZ = form.findField("BRXZ");
										BRXZ.el.parent().parent().parent()
												.first().dom.innerHTML = BRXZ.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase().replace(
														/color:/, "").replace(
														/red/, "");
									} else {
										var BRKS = form.findField("BRKS");
										if (BRKS.el.parent().parent().parent()
												.first().dom.innerHTML
												.indexOf("style") >= 0) {
											BRKS.el.parent().parent().parent()
													.first().dom.innerHTML = BRKS.el
													.parent().parent().parent()
													.first().dom.innerHTML
													.toLowerCase()
													.replace(/style=""/,
															"style=\"color:red\"");
										} else {
											BRKS.el.parent().parent().parent()
													.first().dom.innerHTML = BRKS.el
													.parent().parent().parent()
													.first().dom.innerHTML
													.toLowerCase()
													.replace(/span/,
															"span style=\"color:red\"");
										}
										var RYRQ = form.findField("RYRQ")
										if (RYRQ.el.parent().parent().parent()
												.first().dom.innerHTML
												.indexOf("style") >= 0) {
											RYRQ.el.parent().parent().parent()
													.first().dom.innerHTML = RYRQ.el
													.parent().parent().parent()
													.first().dom.innerHTML
													.toLowerCase()
													.replace(/style=""/,
															"style=\"color:red\"");
										} else {
											RYRQ.el.parent().parent().parent()
													.first().dom.innerHTML = RYRQ.el
													.parent().parent().parent()
													.first().dom.innerHTML
													.toLowerCase()
													.replace(/span/,
															"span style=\"color:red\"");
										}
									}
								} else {
									var BRKS = form.findField("BRKS");
									BRKS.el.parent().parent().parent().first().dom.innerHTML = BRKS.el
											.parent().parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
									var RYRQ = form.findField("RYRQ")
									RYRQ.el.parent().parent().parent().first().dom.innerHTML = RYRQ.el
											.parent().parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
									var BRXZ = form.findField("BRXZ");
									BRXZ.el.parent().parent().parent().first().dom.innerHTML = BRXZ.el
											.parent().parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
									var BRXM = form.findField("BRXM");
									BRXM.el.parent().parent().first().dom.innerHTML = BRXM.el
											.parent().parent().first().dom.innerHTML
											.toLowerCase()
											.replace(/color:/, "").replace(
													/red/, "");
								}
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}
						}, this)
			},
			onbrxzselect:function(){
				var form=this.form.getForm();
				var brxz=form.findField("BRXZ").getValue();
				if(brxz=="2000"){
					Ext.Msg.alert("提示", "在转换病人性质为南京金保之前请先确定医保病种是否已选择，如没有请点修改按钮修改保存后再转换...");
				}
			},
			initFormData : function(data) {
				this.clearYbxx();//清除医保缓存信息
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (this.cmd == "update") {
							if (it.updates == 'false') {
								f.disable();
							}else{
								f.enable();
							}
						} else {
							if (it.readOnly != 'true') {
								f.disable();
							}
						}
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v);
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
								this.setValueAgain(f, v, it);
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			},
			doTransform : function() {
				var form = this.form.getForm();
				this.brxz = form.findField("BRXZ").getValue();
				if(this.oldbrxz==this.brxz){
					MyMessageTip.msg("提示", "原性质和转换的性质一样，不需要转换！", true);
					return;
				}
				var updateCs={
							serviceId : "hospitalPatientManagementService",
							serviceAction : "updateTransform",
							pkey : this.initDataId,
							brxz : this.brxz
						}
				if(this.oldbrxz==2000){
					var ywzqh=this.getywzqh();
					if(!ywzqh){
						return;
					}
					var qxdjbody={};
					qxdjbody.NJJBLSH=this.data.NJJBLSH;
					qxdjbody.JBR=this.mainApp.uid;
					var str=this.buildstr("2240",ywzqh,qxdjbody);
					this.addPKPHISOBJHtmlElement();
					this.drinterfaceinit();
					var drre=this.drinterfacebusinesshandle(str);
					var obj = drre.split('^');
					if(obj[0] != 0 ){
						MyMessageTip.msg("提示", obj[3], true);
						return false;
					}
					var njjbdjxx={};
					njjbdjxx.NJJBLSH="";
					njjbdjxx.NJJBYLLb="";
					updateCs.njjbdjxx=njjbdjxx;
				}
						
				if(this.brxz==2000){
					if(this.njjbdk && this.njjbdk=="1"){
						this.njjbdk="0";
						var zqhbody = {};
						zqhbody.USERID=this.mainApp.uid;
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "getywzqh",
							body:zqhbody
							});
					if (ret.code <= 300) {
						
					}else {
						MyMessageTip.msg("提示", "获取业务周期号失败", true);
						return;
					}
					var lsh = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "getnjjblsh"
							});
					if (lsh.code <= 300) {
						
					}else {
						MyMessageTip.msg("提示", "获取流水号失败", true);
						return;
					}
					var njjbdjbody={};
					njjbdjbody.type=2;//住院
					njjbdjbody.NJJBLSH=lsh.json.lsh.LSH;
					njjbdjbody.YLLB=this.ybxx.YLLB;
					var RYRQ=form.findField("RYRQ").getValue();
					RYRQ=RYRQ.replace(/-/g,"");
					RYRQ=RYRQ.replace(/ /g,"");
					RYRQ=RYRQ.replace(/:/g,"");
					njjbdjbody.RYSJ=RYRQ;
					njjbdjbody.JBBM=form.findField("YBMC").getValue();
					njjbdjbody.BQMC="";
					njjbdjbody.KSDM=form.findField("BRKS").getValue();
					njjbdjbody.KSDM=form.findField("BRKS").getValue();
					njjbdjbody.CWH=form.findField("BRCH").getValue();
					njjbdjbody.YSBM=form.findField("SZYS").getValue();
					njjbdjbody.JBR=this.mainApp.uid;
					njjbdjbody.LXDH=this.ybxx.LXDH;
					njjbdjbody.SHBZKH=this.ybxx.SHBZKH;
					njjbdjbody.ZYH="undefined";
					this.addPKPHISOBJHtmlElement();
					this.drinterfaceinit();
					var ywzqh=ret.json.YWZQH;
					var str=this.buildstr("2210",ywzqh,njjbdjbody);
					var drre=this.drinterfacebusinesshandle(str);
					var obj = drre.split('^');
					if(obj[0] != 0 ){
						MyMessageTip.msg("提示", obj[3], true);
						return false;
					}
					var njjbdjxx={};
					njjbdjxx.NJJBLSH=njjbdjbody.NJJBLSH;
					njjbdjxx.NJJBYLLB=njjbdjbody.YLLB;
					updateCs.njjbdjxx=njjbdjxx;
				}else{
					this.doNjjb();
					return;
				}
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在转换...", "x-mask-loading")
				}
				
				//医保部分
				var xzbody={};
				xzbody["ZYH"]=this.initDataId;
				if (this.getYbbrxz(this.brxz)!=0&&this.getYbbrxz(this.YBRXZ)==0){//自费转医保
				if (!this.ybkxx) {
					MyMessageTip.msg("提示", "自费转医保,未读卡,请先读卡", true);
					this.form.el.unmask();
					return;
					}
				var	body={};
				if(!this.doZfzyb()){return;}//自费转医保失败则返回
				}else if(this.getYbbrxz(this.brxz)==0&&this.getYbbrxz(this.YBRXZ)!=0){//医保转自费
				if (!this.ybkxx) {
						MyMessageTip.msg("提示", "医保病人,未读卡,请先读卡", true);
						this.form.el.unmask();
						return;
						}
				var body={};
				body["ZH"]=1;//用于标识 是医保转自费
				if(!this.doYbzzf()){//医保转自费失败则返回
				return;}
				}
				this.loading = true
				phis.script.rmi.jsonRequest(updateCs, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								MyMessageTip.msg("提示", json.body, true);
								return;
							}
							this.fireEvent("update", this.entryName);
							MyMessageTip.msg("提示", "病人性质转换成功!", true);
							this.win.hide();
						}, this)
			},
			doCanceled : function() {
				if (this.form && this.form.el) {
					this.form.el.mask("正在注销...", "x-mask-loading")
				}
				if(this.YBRXZ == "2000"){
					//初始化
					this.addPKPHISOBJHtmlElement();
					this.drinterfaceinit();
					var body={};
					body.USERID=this.mainApp.uid;
					//获取业务周期号
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getywzqh",
						body:body
						});
					if (ret.code <= 300) {
						
					}else {
						MyMessageTip.msg("提示", "获取业务周期号失败", true);
						return;
					}
					var ywzqh=ret.json.YWZQH;
					//金保查询该病人是否有结算记录
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "queryNJJBjsjl",
								zyh : this.data.ZYH
						});
					if (r.code > 300) {
						MyMessageTip.msg("提示", "查询病人是否有结算记录失败，住院号："+this.data.ZYH, true);
						return ;
					}
					var result = eval(r.json);
					if(result.jsjl > 0){
						MyMessageTip.msg("提示", "该病人已有医保计算记录,不能进行取消入院登记操作!", false);
						this.form.el.unmask();
						return ;
					}
					this.ybxx={};
					this.ybxx.NJJBLSH = this.data.NJJBLSH;
					this.ybxx.JBR=this.mainApp.uid;
					if(!this.doNjjbqxrydj(ywzqh,this.ybxx)){
						return;
					}//金保撤销
				}
				var re = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalPatientManagementService",
							serviceAction : "updateCanceled",
							pkey : this.initDataId,
							body : this.initDataBody
						});
				this.loading = false
				if(re.code >300){
				    this.processReturnMsg(code, msg, this.loadData);
					return;
				}
				if (re.json.body) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					MyMessageTip.msg("提示", re.json.body, true);
					return;
				}
				this.fireEvent("update", this.entryName);
				MyMessageTip.msg("提示", "注销成功!", true);
				this.win.hide();
				this.loading = true
			},
			doNjjbqxrydj : function(ywzqh,data){
				this.addPKPHISOBJHtmlElement();
				var str=this.buildstr("2240",ywzqh,data);//撤销住院登记
				var drre=this.drinterfacebusinesshandle(str);
				var obj = drre.split('^');
				if(obj[0] != 0){
					MyMessageTip.msg("南京金保提示",drre, true);
					return false;
				}else {
					MyMessageTip.msg("提示", "南京金保注销成功!", true);
					return true;
				}
			},
			saveToServer : function(saveData) {
				var YBMC=saveData.YBMC;
				if(YBMC && YBMC.length >0){
					var arr=saveData.YBMC.split(",")
					if(arr.length >3){
						MyMessageTip.msg("提示","医保病种最多只能选择三个！", true);
						return;
					}
				}
				var str = saveData.RYRQ.replace(/-/g, "/");
				if ((new Date(str) - new Date()) > 0) {
					var this_ = this;
					Ext.Msg.alert("提示", "入院日期不能大于当前日期!", function() {
								var form = this_.form.getForm();
								RYRQ = form.findField("RYRQ")
								RYRQ.focus(true, 100);
							});
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPatientManagementService",
							serviceAction : "updateBRRY",
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							if (json.body) {
								MyMessageTip.msg("提示", json.body, true);
								return;
							}
							this.fireEvent("update", this.entryName);
							MyMessageTip.msg("提示", "病人基本信息修改成功!", true);
						}, this)// jsonRequest
			},
			
			doPrint : function() {
				var module = this.createModule("hospMediRecordPrint",
						'phis.application.hos.HOS/HOS/HOS040101')
				var form = this.form.getForm();
				var ZYHM = form.findField("ZYHM").getValue();
				var BAHM = form.findField("BAHM").getValue();
				var GJ = form.findField("GJDM").getRawValue();
				var MZ = form.findField("MZDM").getRawValue();
				var ZYH = this.data.ZYH;
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
					return;
				}
				module.ZYH = ZYH;
				module.ZYHM = ZYHM;
				module.BAHM = BAHM;
				module.GJ = encodeURIComponent(GJ);
				module.MZ = encodeURIComponent(MZ);
				module.initPanel();
				module.doPrint();
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'JBMC'

								}, {
									name : 'ICD10'

								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse(record.get("JBXH"));
				this.data.zddm=record.get("JBXH");
				obj.setValue(record.get("JBMC"));
			},
			doNew:function(){
			this.clearYbxx();
			phis.application.hos.script.HospitalPatientManagementForm.superclass.doNew.call(this);
			},
			// 下面医保相关代码
			doYbdk : function() {
				alert("医保功能完善中....");
				return;
				var ybModule = this.createModule("ybxzzhModule","phis.application.yb.YB/YB/YB01");
				ybModule.initPanel();
				var win = ybModule.getWin();
				win.show();
				ybModule.on("qr", this.onQr_qxrydj, this);
				ybModule.doNew();
				this.ybModule = ybModule;
			},
			doNjjb : function() {
				var module = this.createModule("njjbForm", "phis.application.hos.HOS/HOS/HOS0109");
				module.on("qr", this.onNjjbQr, this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.doNew();
				module.form.getForm().findField('YLLB').setValue({
					key : '21',
					text : '普通住院'
				});
				win.show();
				module.initFormData(module.doDk());
			},
			onNjjbQr : function(data) {
				var sfzh =this.form.getForm().findField("SFZH").getValue();
				if(sfzh!=data.SFZH){
					MyMessageTip.msg("提示", "读卡信息与入院登记病人不是同一个人,请确认", true);
					return;
				}
				this.ybxx = data;
				this.onSaveNJJBCBXX(this.data.BRID);
			},
			onSaveNJJBCBXX : function(BRID) {
				if (this.ybxx && this.ybxx != null) {
					this.ybxx.BRID = BRID;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.medicareService",
								serviceAction : "saveNJJBBRXX",
								body : this.ybxx
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
					} else {
						this.njjbdk="1";
						this.doTransform();
					}
				}
			}
		});
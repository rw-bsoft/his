$package("phis.application.fsb.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedPatientManagementForm = function(cfg) {
	cfg.width = 900;
//	cfg.height = 900;
	cfg.modal = true;
	cfg.remoteUrl = 'MedicalDiagnosisZdlr';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
	cfg.queryParams = {
			"ZXLB" : 1
		};
	cfg.minListWidth = 250;
//	Ext.apply(this,phis.application.cfg.script.YbUtil);
	phis.application.fsb.script.FamilySickBedPatientManagementForm.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientManagementForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {
				this.entryName = "phis.application.fsb.schemas.JC_BRRY_BRGL";
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
					labelWidth : 66,
					frame : true,
					width : 1024,
					height : 600,
					autoScroll : true,
					items : [{
						xtype : "label",
						html : "<br/><div style='font-size:25px;font-weight:bold;text-align:center;letter-spacing:20px' >家庭病床登记</div><br/>"
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
						title : '病人信息',
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
						title : '家床信息',
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
						items : this.getItems('part4')
					}],
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
			onReady : function() {
				phis.application.fsb.script.FamilySickBedPatientManagementForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var brxz = form.findField("BRXZ");
				brxz.on("select",function(){
					if(brxz.getValue()==6089){
						//????????
					}
				})
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
						}, {
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
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
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
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.clearYbkxx();//清除医保缓存信息
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedPatientManagementService",
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
								this.initFormData(json.body)
								var form = this.form.getForm();
								//form.findField("JBMC").setDisabled(true);
								if (this.cmd == "transform") {
									var BRXZ = form.findField("BRXZ");
									BRXZ.setDisabled(false);
									//form.findField("JBMC").setDisabled(false);
									if (BRXZ.el.parent().parent().parent()
											.first().dom.innerHTML
											.indexOf("style") >= 0) {
										BRXZ.el.parent().parent().parent()
												.first().dom.innerHTML = BRXZ.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase().replace(
														/style=""/,
														"style=\"color:red\"");
									} else {
										BRXZ.el.parent().parent().parent()
												.first().dom.innerHTML = BRXZ.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase()
												.replace(/span/,
														"span style=\"color:red\"");
									}
									var BRXM = form.findField("BRXM");
									BRXM.el.parent().parent().first().dom.innerHTML = BRXM.el
											.parent().parent().first().dom.innerHTML
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
												.replace(/span/,
														"span style=\"color:red\"");
									}
									if (json.body.BRCH) {
										var BRXZ = form.findField("BRXZ");
										BRXZ.el.parent().parent().parent()
												.first().dom.innerHTML = BRXZ.el
												.parent().parent().parent()
												.first().dom.innerHTML
												.toLowerCase().replace(
														/color:/, "").replace(
														/red/, "");
									}
								} else {
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
						}, this)// jsonRequest
			},
			initFormData : function(data) {
				this.clearYbkxx();//清除医保缓存信息
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
							if (it.updates == 'true') {
								f.enable();
							}else if(this.exContext["phis.application.fsb.schemas.JC_BRRY_BRGL_LIST"].data.SQFS==3){
								if(it.updates == 'auto'){
									f.enable();
								}else{
									f.disable();
								}
							}else{
								f.disable();
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
				if(this.brxz==this.YBRXZ){
					this.win.hide();
					return;
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在转换...", "x-mask-loading")
				}
				var updateCs={
							serviceId : "familySickBedPatientManagementService",
							serviceAction : "updateTransform",
							pkey : this.initDataId,
							brxz : this.brxz
						}
				//医保部分
				var xzbody={};
				xzbody["ZYH"]=this.initDataId;
				var ybbrxz=this.getYbbrxz();
				if(ybbrxz==null){
				return ;
				}
				if (this.brxz ==ybbrxz.YBBRXZ&& this.brxz != this.YBRXZ){//自费转医保
				if (!this.ybkxx) {
					MyMessageTip.msg("提示", "自费转医保,未读卡,请先读卡", true);
					this.form.el.unmask();
					return;
					}
					var	body={};
					if(!this.doZfzyb()){return;}//自费转医保
				}else if(this.YBRXZ==ybbrxz.YBBRXZ&&this.brxz!=ybbrxz.YBBRXZ){//医保转自费
				if (!this.ybkxx) {
						MyMessageTip.msg("提示", "医保病人,未读卡,请先读卡", true);
						this.form.el.unmask();
						return;
						}
				var body={};
				if(!this.doYbzzf()){
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
				var req={
							serviceId : "familySickBedPatientManagementService",
							serviceAction : "updateCanceled",
							pkey : this.initDataId,
							body : this.initDataBody
						};
				this.loading = true
				phis.script.rmi.jsonRequest(req, function(code, msg, json) {
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
							MyMessageTip.msg("提示", "注销成功!", true);
							this.win.hide();
						}, this)
			},
			saveToServer : function(saveData) {
				if (saveData.KSRQ > saveData.JSRQ) {
					MyMessageTip.msg("提示", "开始日期不能大于结束日期！", true);
					return;
				}	
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedPatientManagementService",
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
//							this.doPrintXY();测试说修改后不用自动打印,张伟同意了.
						}, this)// jsonRequest
			},
			
			doPrint : function() {
				var module = this.createModule("hospMediRecordPrint",
						'phis.application.fsb.FSB/FSB/FSB0402');
				var ZYH = this.data.ZYH;
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
					return;
				}
				module.ZYH = ZYH;
				module.initPanel();
				module.doPrintSY();
			},
			doPrintXY : function() {
				var module = this.createModule("hospMediRecordPrint",
						'phis.application.fsb.FSB/FSB/FSB0402');
				var ZYH = this.data.ZYH;
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
					return;
				}
				module.ZYH = ZYH;
				module.initPanel();
				module.doPrintXY();
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
			//读卡返回 自动调用病人结算信息
			onQr : function(data) {
				//以下代码用于判断读出来的卡与当前病人是否是同一个(仅供参考),可以根据需要判断
//			var sfzh =this.form.getForm().findField("SFZH").getValue();
//			if(sfzh!=data.GRSFH){
//				MyMessageTip.msg("提示", "读卡信息与入院登记病人不是同一个人,请确认", true);
//				return;
//			}
			this.ybkxx = data;
			},
			doNew:function(){
			this.clearYbkxx();
			phis.application.fsb.script.FamilySickBedPatientManagementForm.superclass.doNew.call(this);
			},
			//清除医保缓存记录
			clearYbkxx:function(){
			this.ybkxx=null;
			},
			// 获取医保病人性质代码
			getYbbrxz : function() {
				if (this.ybbrxz && this.ybbrxz != null) {
					return this.ybbrxz;
				} else {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "queryYbbrxz"
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return null;
					}
					this.ybbrxz = ret.json.body;
					return this.ybbrxz;
				}
			},
			//自费转医保,成功返回true,失败返回false
			doZfzyb:function(body){
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbRydjcs",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return false;
				}
				var rydjcs=ret.json.body;
				//医保调用入院登记代码
				//....
				//如果失败 返回false
				//return false;
				//如果成功,更新zy_brry表
				//this.doUpdateRydj(body);
				return true;
			},
			//医保转自费,成功返回true,失败返回false
			doYbzzf:function(body){
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbZyxzzhcs",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return false;
				}
				var xzzhcs=ret.json.body;
				//医保转换代码
				//....
				return true;
			},
			//如果医保登记成功,更新入院登记表
			doUpdateRydj:function(body){
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "updateRydj",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "医保未入院登记成功,本地更新失败,请联系管理员手动删除记录,ZYH=:"+body.zyh+"PAR=:"+Ext.encode(body.par));
					return;
				}
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
									name : 'MSZD'

								}, {
									name : 'JBBM'

								}, {
									name : 'JBPB'

								}, {
									name : 'JBPB_text'

								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.form.getForm().findField("JCZD").setValue(record.get("MSZD"));
				this.form.getForm().findField("ICD10").setValue(record.get("JBBM"));
			}
		});
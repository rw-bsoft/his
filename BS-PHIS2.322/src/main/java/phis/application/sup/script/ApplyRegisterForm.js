$package("phis.application.sup.script")

$import("util.Accredit", "phis.script.SimpleForm",
"org.ext.ux.layout.TableFormLayout")

phis.application.sup.script.ApplyRegisterForm = function(cfg) {
	cfg.width = 600;
	cfg.colCount = 2;
	cfg.remoteUrl = "ApplyRegister";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="120px">{WZMC}</td><td width="60px">{WZGG}</td>';
	phis.application.sup.script.ApplyRegisterForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.ApplyRegisterForm, phis.script.SimpleForm, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName);
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		var ac = util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		var items = schema.items
		if (!this.fireEvent("changeDic", items)) {
			return
		}
		var colCount = this.colCount;

		var table = {
			layout : 'tableform',
			layoutConfig : {
				columns : colCount,
				tableAttrs : {
					border : 0,
					cellpadding : '2',
					cellspacing : "2"
				}
			},
			items : []
		}
		var size = items.length
		for (var i = 0; i < size; i++) {
			var it = items[i]
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)

			if (!this.fireEvent("addfield", f, it)) {
				continue;
			}
			table.items.push(f)
		}

		var cfg = {
			buttonAlign : 'center',
			labelAlign : this.labelAlign || "left",
			labelWidth : this.labelWidth || 80,
			frame : true,
			shadow : false,
			border : false,
			collapsible : false,
			autoHeight : true,
			autoScroll : true,
			floating : false
		}

		if (this.isCombined) {
			cfg.frame = true
			cfg.shadow = false
			cfg.height = this.height
		} else {
			cfg.autoHeight = true
		}
		this.initBars(cfg);
		Ext.apply(table, cfg)
		this.form = new Ext.FormPanel(table)
		this.form.on("afterrender", this.onReady, this)

		this.schema = schema;
		this.setKeyReadOnly(true)
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		return this.form
	},
	doAdd:function(){
		this.doNew();
	},
	    doNew : function() {
				phis.application.sup.script.ApplyRegisterForm.superclass.doNew.call(this);
				if(this.num == 2){
						var btns =  this.form.getTopToolbar().items;
						if (!btns) {
							return;
						}
						var n = btns.getCount();
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i);
							if(i == 2){
							  btn.enable();
							}else{
							  btn.disable();
							}
						}
					}else{
						var btns =  this.form.getTopToolbar().items;
						if (!btns) {
							return;
						}
						var n = btns.getCount();
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i);
							  btn.enable();
						}
					}
				this.num = 0;
				this.fillRkfs();
			},
			fillRkfs : function() {
				if(this.oper){
					this.op = this.oper;
				}
				if (this.oper == "create") {
					var form = this.form.getForm();
					
					form.findField("WZMC").setValue("");
					form.findField("WZGG").setValue("");
					form.findField("WZDW").setValue("");
					form.findField("WZSL").setValue("");
					form.findField("KFXH").setValue("");
					form.findField("KFXH").setRawValue("");
					form.findField("SLSJ").setValue("");
					form.findField("BZXX").setValue("");
					form.findField("TJSL").setValue("");
// if(this.KSDM){
// form.findField("SLKS").setValue(this.KSDM);
// form.findField("SLKS").setRawValue(this.KSMC);
// form.findField("SLKS").focus(false,300);
// form.findField("WZMC").focus();
// }else{
						var KS = this.doQueryKS();
						if(KS){
							form.findField("SLKS").setValue(KS.KSDM);
							form.findField("SLKS").setRawValue(KS.KSMC);
							form.findField("SLKS").focus(false,300);
						}
					 // form.findField("WZMC").focus();
					// }
				}
				if(this.oper == "update"){
					var form = this.form.getForm();
					if(form){
						form.findField("WZMC").setValue(this.WZMC);
						form.findField("WZGG").setValue(this.WZGG);
						form.findField("WZDW").setValue(this.WZDW);
						form.findField("WZSL").setValue(this.WZSL);
						form.findField("KFXH").setValue(this.KFXH1);
						form.findField("KFXH").setRawValue(this.KFXH);
						form.findField("SLSJ").setValue(this.SLSJ);
						form.findField("BZXX").setValue(this.BZXX);
						form.findField("SLKS").setValue(this.SLKS);
						form.findField("SLKS").setRawValue(this.SLKS_text);
						this.data["WZXH"] = {
							"text" : this.WZMC,
							"key" : this.WZXH
						};
						var TJSL = this.searchYtsl(this.WZXH,this.KFXH1);
						if(TJSL == null){
						   form.findField("TJSL").setValue(0);
						}else{
						   form.findField("TJSL").setValue(TJSL);
						}
						form.findField("SLKS").focus(false,200);
						form.findField("WZMC").focus(); 	
					}
				}
			},
			saveToServer : function(saveData) {
				if(this.oper){
	        	   	  this.op = this.oper;
	        	    }
	        	    var SystemParams = this.getParams();
	        	    var saveorno=0;
	        	    if(SystemParams == 1){
	        	    	if((Number(saveData.WZSL)) > (Number(saveData.TJSL))){
	        	    		MyMessageTip.msg("提示", "申领数量不能大于推荐数量！", true);
						    return;
	        	    	}else{
	        	    		saveorno=1;	
	        	    	}
	        	    }else{
	        	    	if((Number(saveData.WZSL)) > (Number(saveData.TJSL))){
	        	    		Ext.Msg.confirm("请确认", "申领数量不能大于推荐数量,确定保存吗！", function(btn) {// 先提示是否删除
								if (btn == 'yes') {
									saveorno=1;
								}else{
									saveorno=0;
								}
							}, this);
	        	    				return;
	        	    	}
	        	    }
	        if(saveorno==1){
        	    if(saveData.WZSL <= 0 ){
        	    	MyMessageTip.msg("提示", "申领数量不能小于等于0！", true);
				    return;
        	    }
				if (!this.fireEvent("beforeSave", "WL_SLXX", this.op, saveData)) {
					return;
				}
				if(saveData.SLKS == ""){
					MyMessageTip.msg("提示", "申领科室不能为空！", true);
				    return;
				}
				var data = {"WZMC":saveData.WZMC,"WZGG":saveData.WZGG,"WZDW":saveData.WZDW,"WZSL":saveData.WZSL,"WZXH":saveData.WZXH,
				            "JGID":saveData.JGID,"ZBLB":saveData.ZBLB,"GLFS":saveData.GLFS,"BZXX":saveData.BZXX,
				            "SLKS":saveData.SLKS,"KFXH":saveData.KFXH};
					if(this.JLXH){
				      data = {"JLXH": this.JLXH,"WZMC":saveData.WZMC,"WZGG":saveData.WZGG,"WZDW":saveData.WZDW,"WZSL":saveData.WZSL,
				              "WZXH":saveData.WZXH,"ZBLB":saveData.ZBLB,"BZXX":saveData.BZXX,
				              "SLKS":saveData.SLKS,"KFXH":saveData.KFXH};
					}
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "applyRegisterService",
							serviceAction :"saveform",
							op : this.op,
							body : data
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg, this.saveToServer,[saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op, json,
										this.data)
							}
							this.op = "update"
							MyMessageTip.msg("提示", "保存成功！", true);
							this.fireEvent("refresh", this);
							this.doCancel();
						}, this)
			  }
			},
		  getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mats',
							totalProperty : 'count',
							id : 'mtssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'WZXH'
								}, {
									name : 'WZMC'
								}, {
									name : 'WZGG'
								}, {
									name : 'WZDW'
								}, {
									name : 'GLFS'
								}, {
									name : 'ZBLB'
								}, {
									name : 'KFXH'
								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.form.getForm().findField("WZMC").setValue(record.get("WZMC"));
				if(record.get("WZGG") == "null" || record.get("WZGG") == ""){
					this.form.getForm().findField("WZGG").setValue("");
				}else{
					this.form.getForm().findField("WZGG").setValue(record.get("WZGG"));
				}
				this.form.getForm().findField("WZDW").setValue(record.get("WZDW"));
				this.form.getForm().findField("KFXH").setValue(record.get("KFXH"));
				this.data["WZXH"] = {
					"text" : record.get("WZMC"),
					"key" : record.get("WZXH")
				};
				this.data["ZBLB"] = {
					"key" : record.get("ZBLB")
				};
				var YKSL = this.searchYtsl(record.get("WZXH"),record.get("KFXH"));
				
                if(YKSL == null){
                	this.form.getForm().findField("TJSL").setValue("0");
                }else{
                	this.form.getForm().findField("TJSL").setValue(YKSL);
                }
			},
			searchYtsl : function(WZXH,KFXH) {
				if(!WZXH || !KFXH){
					MyMessageTip.msg("提示", "物资序号或者库房序号不能为空！", true);
					return;
				}
				var body = {};
				body["WZXH"] = WZXH;
				body["KFXH"] = KFXH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "queryYksl",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLoadReport);
				}
				return ret.json.ret;
			},
			resetButtons:function(){
			},
			getParams : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "loadSystemParams"
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLoadReport);
				}
				return ret.json.ret;
			},
			doQueryKS : function(){
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "queryKs"
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLoadReport);
				}
				return ret.json.ret;
			}
		})
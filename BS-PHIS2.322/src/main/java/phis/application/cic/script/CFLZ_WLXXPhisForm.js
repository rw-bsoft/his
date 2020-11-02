$package("phis.application.cic.script")

$import("phis.script.TableForm","phis.script.util.helper.Helper","util.Vtype");

phis.application.cic.script.CFLZ_WLXXPhisForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 170;
	cfg.labelWidth = 90;
	cfg.width = 900;
	phis.application.cic.script.CFLZ_WLXXPhisForm.superclass.constructor.apply(
			this, [cfg]);
//	this.on("beforePrint", this.onBeforePrint, this);
//	this.on("beforeCreate", this.onBeforeCreate, this);
//	this.on("loadData", this.onLoadData, this);
	this.on("beforeclose", this.beforeclose, this);
	this.on("beforeSave", this.onBeforeSave, this);
};

Ext.extend(phis.application.cic.script.CFLZ_WLXXPhisForm,
		phis.script.TableForm, {
			doNew : function() {
				phis.application.cic.script.CFLZ_WLXXPhisForm.superclass.doNew
						.call(this);
			},
			createField : function(it) {
				debugger;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				// alert(defaultWidth)
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					validator : function(str) {
						return true;
					},
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
//				cfg.listeners = {
//					specialkey : this.onFieldSpecialkey,
//					scope : this
//				}
//				if (it.inputType) {
//					cfg.inputType = it.inputType
//				}
				if (it['not-null']) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
//				if (it['showRed']) {
//					cfg.fieldLabel = "<span style='color:red'>"
//							+ cfg.fieldLabel + "</span>"
//				}
				if (it.fixed || it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					debugger;
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					debugger;
					cfg.disabled = true
				}

				if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					var combox = this.createDicField(it.dic)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
//
//				if (it.length) {
//					cfg.maxLength = it.length;
//				}
//
//				if (it.xtype) {
//					return cfg;
//				}
//				switch (it.type) {
//					case 'int' :
//					case 'double' :
//					case 'bigDecimal' :
//						cfg.xtype = "numberfield"
//						if (it.type == 'int') {
//							cfg.decimalPrecision = 0;
//							cfg.allowDecimals = false
//						} else {
//							cfg.decimalPrecision = it.precision || 2;
//						}
//						if (it.minValue) {
//							cfg.minValue = it.minValue;
//						} else {
//							cfg.minValue = 0;
//						}
//						if (it.maxValue) {
//							cfg.maxValue = it.maxValue;
//						}
//						break;
//					case 'datetime' :
//						cfg.xtype = 'datetimefield'
//						cfg.emptyText = "请选择日期"
//						cfg.format = 'Y-m-d H:i:s'
//						break;
//						case 'date' :
//						cfg.xtype = 'datefield'
//						cfg.emptyText = "请选择日期"
//						cfg.format = 'Y-m-d H:i:s'
//						break;
//					case 'text' :
//						cfg.xtype = "htmleditor"
//						cfg.enableSourceEdit = false
//						cfg.enableLinks = false
//						cfg.width = 700
//						cfg.height = 450
//						break;
//				}
				return cfg;
			},
			doCreate : function(){
				debugger;
				this.initDataId = null;
				this.doNew();
				if(!this.form.data){
					this.form.data={};
				}
				this.op = "create";
			},
			onReady : function() {
				debugger;
				phis.application.cic.script.CFLZ_WLXXPhisForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				var serverDateTime = Date.getServerDateTime();

				
				// 加上延迟,防止太快执行,参数没有完全加载
				var d = new Ext.util.DelayedTask(function(){
					debugger;
					var MS_BRZD_JLBH = form.findField("MS_BRZD_JLBH");
					if (MS_BRZD_JLBH) {
						MS_BRZD_JLBH.setValue(this.exContext.args.MS_BRZD_JLBH || '');
					}
					debugger;
					//var finishStatus="0";
					var BRNAME = form.findField("BRNAME");
					if (BRNAME) {
						BRNAME.setValue(this.exContext.empiData.personName || '');
					}
					debugger;
					//var finishStatus="0";
					var SEX = form.findField("SEX");
					if (SEX) {
						SEX.setValue(this.exContext.empiData.sexCode_text || '');
					}
					debugger;
					var fullAge = form.findField("AGE");
					if (fullAge) {
						fullAge.setValue(this.exContext.empiData.age || '');
					}
					debugger;
					var SFZH = form.findField("SFZH");
					if (SFZH) {
						SFZH.setValue(this.exContext.empiData.idCard || '');
					}
					debugger;
//					var KDYS = form.findField("KDYS");
//					if (KDYS) {
//						KDYS.setValue(this.exContext.docPermissions.YGXM || '');
//					}
					debugger;
//					var LZORGID = form.findField("LZORGID");
					var LZORGNAME = form.findField("LZORGNAME");
					debugger;
					if (LZORGNAME) {
						debugger;
						var name = this.exContext.JGNAME || '';
						LZORGNAME.setValue(name);
					}
					
				},this);
				d.delay(1000);
			},
			doSave : function() {
				debugger;
				this.data.empiId = this.exContext.ids.empiId;
				this.data.phrId = this.exContext.empiData.phrId;
				this.data.MS_BRZD_JLBH = this.exContext.args.MS_BRZD_JLBH || '';
				debugger;
				var form = this.form.getForm();
				debugger;
				var PHONE = form.findField("PHONE").getValue();
				debugger;
				var ADDRESS = form.findField("ADDRESS").getValue();
				var msg = "";
				if(PHONE==""||PHONE==undefined){
					msg += "电话不能为空！</br>";
				}
				debugger;
				if(ADDRESS==""||ADDRESS==undefined){
					msg += "地址不能为空！</br>";
				}
				debugger;
				if(msg != ""){
					MyMessageTip.msg("提示",msg, true);
					return false;
				}else{
				
				var body = {};
				body["EMPIID"] = this.data.empiId;
				body["WLZT"] = form.findField("WLZT").getValue();
				body["BRNAME"] = form.findField("BRNAME").getValue();
				body["CREATEORGID"] = form.findField("CREATEORGID").getValue();
				body["LZORGID"] = this.exContext.SJJG;
				body["LZORGNAME"] = form.findField("LZORGNAME").getValue();
				body["KDYS"] = this.exContext.docPermissions.YGXM;
				body["KDYSGH"] = form.findField("KDYSGH").getValue();
				body["SFZH"] = form.findField("SFZH").getValue();
				body["SEX"] = form.findField("SEX").getValue();
				body["AGE"] = form.findField("AGE").getValue();
				body["KDYSGH"] = form.findField("KDYSGH").getValue();
				body["PHONE"] = PHONE;
				body["ADDRESS"] = ADDRESS;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicManageService",
					serviceAction : "saveCFLZWLXX",
					body : body
				});
				debugger;
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				}else{
					MyMessageTip.msg("提示","保存成功", true);
					var win = this.opener.getWin();
					if (win) {
						win.hide();
					}
				} 
				
				}
			},
			doPrint : function(){
				var pages="phis.prints.jrxml.infectiousDisease";
				var url="resources/"+pages+".print?type=1";
				url += "&empiId="+this.exContext.ids.empiId;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();
			},
			beforeclose : function() {
				if(!this.opener.doSave()){
					this.opener.doSaveDiagnosis();
				}
			},
			onBeforeSave:function(entryName, op, saveData){
				debugger;
				if(this.opener.opener.opener!=null){
					this.opener.opener.opener.needSaveCrb = false;
				}else{
					this.opener.opener.needSaveCrb = false;
				}
			},
			doClose : function() {
				var win = this.opener.getWin();
				if (win) {
					win.hide();
				}
			}
		});
/** 
 * @author : renwei 2020-09-11
 */
$package("chis.application.fdsr.script")

$import("chis.script.BizHtmlFormView", "app.modules.list.SimpleListView",
		"chis.application.mpi.script.CombinationSelect",
		"chis.script.BizSimpleFormView", "chis.script.BizTableFormView",
		"chis.application.fdsr.script.FdsrTemplate",
		"chis.application.mpi.script.ParentsQueryList",
		"chis.application.mpi.script.SubTableForm","util.Accredit","chis.script.util.helper.Helper")
		
$styleSheet("chis.css.JYFWJL");

debugger;
chis.application.fdsr.script.FdsrForm = function(cfg) {
	debugger;
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	//cfg.width = 710;
	chis.application.fdsr.script.FdsrForm.superclass.constructor.apply(this, [cfg]);
	Ext.apply(this,chis.application.fdsr.script.FdsrTemplate);
	this.on("save", this.onSave, this);
	this.on("loadData", this.onLoadData, this);
	debugger;

}

Ext.extend(chis.application.fdsr.script.FdsrForm, chis.script.BizHtmlFormView, {
		
	getHTMLTemplate : function() {
		var jobTitle = this.mainApp.jobtitleId;
		return this.getBasicInformationHTML(jobTitle);
	},
		
	getHTMLFormData : function(schema) {
		debugger;
		if (!schema) {
			schema = this.schema;
		}
		// 取表单数据
		if (!schema) {
			return
		}		
		
		var ac = util.Accredit;
		var values = {};
		var items = schema.items;
		var n = items.length
		var frmEl = this.form.getEl();
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (this.op == "create" && !ac.canCreate(it.acValue)) {
				continue;
			}
			// 从内存中取
			var v = this.data[it.id];
			if (v == undefined) {
				v = it.defaultValue;
			}
			if (v != null && typeof v == "object") {
				v = v.key;
			}
			// 从页面上取
			if (this.isCreateField(it.id)) {
				v = eval("this." + it.id + ".getValue()");
				values[it.id] = v;
			} else {
				if (it.dic) {
					var fs = document.getElementsByName(it.id);
					if (!fs) {
						continue;
					}
					var vs = [];
					if (fs && fs.length > 0) {
						for (var j = 0, len = fs.length; j < len; j++) {
							var f = fs[j];
							if (frmEl.contains(f)) {
								if (f.type == "checkbox"
										|| f.type == "radio") {
									if (f.checked) {
										vs.push(f.value);
									}
								} else if (f.type == "hidden") {
									vs.push(f.value || '');
								}
							}
						}
					}
					if (vs.length > 1) {
						v = vs.join(',') || ''
					} else {
						v = vs[0] || ''
					}
					values[it.id] = v;
				} else {
					var f = document.getElementById(it.id
							+ this.idPostfix)
					if (f) {
						v = f.value || f.defaultValue || '';
						if (v == f.defaultValue && f.type != "hidden") {
							v = '';
						}
						values[it.id] = v;
					}
				}
			}
			if (v == null || v === "") {
				if (!(it.pkey)
						&& (it["not-null"] == "1" || it['not-null'] == "true")
						&& !it.ref) {
					if (eval("this." + it.id)) {
						eval("this." + it.id + ".focus(true,200)");
					} else if (it.dic) {
						var divId = "div_" + it.id + this.idPostfix;
						var div = document.getElementById(divId);
						if (document.getElementsByName(it.id)[0]) {
							document.getElementsByName(it.id)[0]
									.focus();
						}
					} else {
						if (document.getElementById(it.id
								+ this.idPostfix)) {
							document.getElementById(it.id
									+ this.idPostfix).focus();
							document.getElementById(it.id
									+ this.idPostfix).select();
						}
					}
					MyMessageTip.msg("提示", it.alias + "为必填项", true);
					return;
				}
			}
		}
		return values;
	},
	afterSaveData : function(a,b,c) {
		this.refreshEhrTopIcon();
	},
	doNew : function() {
		debugger;
		if(this.exContext.ids.phrId == null){
			Ext.Msg.alert("提示","请创建个人档案");
			return;
		}	
		var record = this.exContext.record.data;
		if(record != null && typeof this.newFlag == "undefined"){
			this.op = "update";
			this.id = record.recodeId;
		}else{
			debugger;
			this.id = null;
			this.op = "create";
			for(var i=0;i<document.forms.length;i++){
				document.forms[i].reset();
			}
			
		}
		chis.application.fdsr.script.FdsrForm.superclass.fieldValidate.call(this);
	},
	
	doAdd : function() {
		this.fireEvent("add");
		//this.doCreate();
	},
	
	setFieldEnable : 
		function() {
		var len = this.otherDisable.length;
		var me = this;
		for (var i = 0; i < len; i++) {
			var od = this.otherDisable[i];
			var cArr = od.control;
			var type = od.type;
			if (type == "text") {
				var fObj = document.getElementById(od.fld
						+ this.idPostfix);
				if (fObj) {
					me.textOnChange(fObj, cArr, me);
				}
			}
			if (type == "checkbox") {
				for (var j = 0, cLen = cArr.length; j < cLen; j++) {
					var co = cArr[j];
					var key = co.key;
					var fId = od.fld + "_" + key + this.idPostfix;
					var fObj = document.getElementById(fId);
					if (!fObj) {
						continue;
					}
					me.checkOnClick(fObj, co, me);
				}
			}
			if (type == "radio") {
				var fldName = od.fld;
				var fldes = document.getElementsByName(fldName);
				me.radioOnClick(fldName, cArr, me);
			}
		}
	},
	
	onReady : function() {
		chis.application.fdsr.script.FdsrForm.superclass.onReady.call(this);
		var form = this.form.getForm();
	},

	initFormData:function(data){
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		Ext.apply(this.data, data);
		this.initDataId = this.data[this.schema.pkey]
		this.initHTMLFormData(data, this.schema);
		this.fieldValidate(this.schema);
		// this.setKeyReadOnly(true)
		// this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		this.focusFieldAfter(-1, 800);

		
	},
	
	doCancer : function() {
		this.getWin().hide();
	},
	
	doSave : function(){
		debugger;
		if(this.exContext.ids.phrId == null){
			Ext.Msg.alert("提示","请创建个人档案");
			return;
		}
		
		if(this.initDataId){
			debugger;
			this.op = "update";
			var recedeId = this.initDataId;
			var r = this;
		}else{
			this.op = "create";
			var recodeId = "0";
		}
		debugger;	
		var ids = this.exContext.ids;		
		var nameValue=null; //姓名
		var sexValue=null;  //性别
		var ageValue=null;  //年龄
		var telephoneValue=null; //电话
		var isjkglValue=0; 
		var jkglValue=null; //健康管理服务
		var isjksjValue=0;
		var jksjValue=null; //健康数据监测
		var ishlyyValue=0;
		var hlyyValue=null;  //合理用药指导
		var ispsyzValue=0;
		var psyzValue=null;  //配送医嘱内药品
		var islxdcValue=0;
		var lxdcValue=null;  //联系代采购药品
		var isfyjkValue=0;
		var fyjkValue=null;  //妇幼健康项目咨询服务
		var iskzmxValue=0;
		var kzmxValue=null;  //开展慢性病等重点人群自我管理小组活动
		var iszyyjValue=0;
		var zyyjValue=null;  //中医药健康管理
		var isbyzdValue=0;
		var byzdValue=null;  //避孕指导、药具发放
		var iszzyyValue=0;
		var zzyyValue=null;  //转诊、预约就诊等联络
		var isyytjValue=0;
		var yytjValue=null;  //预约体检时间
		var isjdtjValue=0;
		var jdtjValue=null;  //解读体检报告
		var isxlghValue=0;
		var xlghValue=null;  //心理关怀
		var iskzjyValue=0;
		var kzjyValue=null;  //开展家医签约
		var isqtValue=0;
		var qtValue=null;  //其他

		var name = document.getElementById("name"+this.idPostfix);
		nameValue = name.value;
		var sex = document.getElementById("sex"+this.idPostfix);
		sexValue = sex.value;
		var age = document.getElementById("age"+this.idPostfix);
		ageValue = age.value;
		var telephone = document.getElementById("telephone"+this.idPostfix);
		telephoneValue = telephone.value;
		debugger;
		
				if(this.op == "create" || this.op == "update"){
					
					//健康管理服务
					var isjkgl = document.getElementById("isjkgl_1"+this.idPostfix);
					if(isjkgl.checked){
						isjkglValue = "1";
					}
					var jkglValue = document.getElementById("jkgl"+this.idPostfix).value;
					//健康数据监测
					var isjksj = document.getElementById("isjksj_1"+this.idPostfix);
					if(isjksj.checked){
						isjksjValue = "1";
					}
					var jksjValue = document.getElementById("jksj"+this.idPostfix).value;
					//合理用药指导
					var ishlyy = document.getElementById("ishlyy_1"+this.idPostfix);
					if(ishlyy.checked){
						ishlyyValue = "1";
					}
					var hlyyValue = document.getElementById("hlyy"+this.idPostfix).value;
					//配送医嘱内药品
					var ispsyz = document.getElementById("ispsyz_1"+this.idPostfix);
					if(ispsyz.checked){
						ispsyzValue = "1";
					}
					var psyzValue = document.getElementById("psyz"+this.idPostfix).value;
					//联系代采购药品
					var islxdc = document.getElementById("islxdc_1"+this.idPostfix);
					if(islxdc.checked){
						islxdcValue = "1";
					}
					var lxdcValue = document.getElementById("lxdc"+this.idPostfix).value;
					//妇幼健康项目咨询服务
					var isfyjk = document.getElementById("isfyjk_1"+this.idPostfix);
					if(isfyjk.checked){
						isfyjkValue = "1";
					}
					var fyjkValue = document.getElementById("fyjk"+this.idPostfix).value;
					//开展慢性病等重点人群自我管理小组活动
					var iskzmx = document.getElementById("iskzmx_1"+this.idPostfix);
					if(iskzmx.checked){
						iskzmxValue = "1";
					}
					var kzmxValue = document.getElementById("kzmx"+this.idPostfix).value;
					//中医药健康管理
					var iszyyj = document.getElementById("iszyyj_1"+this.idPostfix);
					if(iszyyj.checked){
						iszyyjValue = "1";
					}
					var zyyjValue = document.getElementById("zyyj"+this.idPostfix).value;
					//避孕指导、药具发放
					var isbyzd = document.getElementById("isbyzd_1"+this.idPostfix);
					if(isbyzd.checked){
						isbyzdValue = "1";
					}
					var byzdValue = document.getElementById("byzd"+this.idPostfix).value;
					//转诊、预约就诊等联络
					var iszzyy = document.getElementById("iszzyy_1"+this.idPostfix);
					if(iszzyy.checked){
						iszzyyValue = "1";
					}
					var zzyyValue = document.getElementById("zzyy"+this.idPostfix).value;
					//预约体检时间
					var isyytj = document.getElementById("isyytj_1"+this.idPostfix);
					if(isyytj.checked){
						isyytjValue = "1";
					}
					var yytjValue = document.getElementById("yytj"+this.idPostfix).value;
					//解读体检报告
					var isjdtj = document.getElementById("isjdtj_1"+this.idPostfix);
					if(isjdtj.checked){
						isjdtjValue = "1";
					}
					var jdtjValue = document.getElementById("jdtj"+this.idPostfix).value;
					//心理关怀
					var isxlgh = document.getElementById("isxlgh_1"+this.idPostfix);
					if(isxlgh.checked){
						isxlghValue = "1";
					}
					var xlghValue = document.getElementById("xlgh"+this.idPostfix).value;
					//开展家医签约
					var iskzjy = document.getElementById("iskzjy_1"+this.idPostfix);
					if(iskzjy.checked){
						iskzjyValue = "1";
					}
					var kzjyValue = document.getElementById("kzjy"+this.idPostfix).value;
					//其他
					var isqt = document.getElementById("isqt_1"+this.idPostfix);
					if(isqt.checked){
						isqtValue = "1";
					}
					var qtValue = document.getElementById("qt"+this.idPostfix).value;

					if (!this.htmlFormSaveValidate()) {
						return;
					}

					var res = util.rmi.jsonRequest({
						serviceId : "chis.FdsrService",
						serviceAction : "saveJYFW",
						method:"execute",
						op:this.op,
						body:{
							"id":this.initDataId,
							"empiId":ids.empiId,
							"phrId":ids.phrId,
							"name":nameValue, //姓名
							"sex":sexValue, //性别
							"age":ageValue, //年龄
							"telephone":telephoneValue, //电话
							"jkgl":jkglValue, //健康管理服务
							"jksj":jksjValue, //健康数据监测
							"hlyy":hlyyValue, //合理用药指导
							"psyz":psyzValue, //配送医嘱内药品
							"lxdc":lxdcValue, //联系代采购药品
							"fyjk":fyjkValue, //妇幼健康项目咨询服务
							"kzmx":kzmxValue, //开展慢性病等重点人群自我管理小组活动
							"zyyj":zyyjValue, //中医药健康管理
							"byzd":byzdValue, //避孕指导、药具发放
							"zzyy":zzyyValue, //转诊、预约就诊等联络
							"yytj":yytjValue, //预约体检时间
							"jdtj":jdtjValue, //解读体检报告
							"xlgh":xlghValue, //心理关怀
							"kzjy":kzjyValue, //开展家医签约
							"qt":qtValue, //其他
							
							"isjkgl":isjkglValue, //是否健康管理服务
							"isjksj":isjksjValue, //是否健康数据监测
							"ishlyy":ishlyyValue, //是否合理用药指导
							"ispsyz":ispsyzValue, //是否配送医嘱内药品
							"islxdc":islxdcValue, //是否联系代采购药品
							"isfyjk":isfyjkValue, //是否妇幼健康项目咨询服务
							"iskzmx":iskzmxValue, //是否开展慢性病等重点人群自我管理小组活动
							"iszyyj":iszyyjValue, //是否中医药健康管理
							"isbyzd":isbyzdValue, //是否避孕指导、药具发放
							"iszzyy":iszzyyValue, //是否转诊、预约就诊等联络
							"isyytj":isyytjValue, //是否预约体检时间
							"isjdtj":isjdtjValue, //是否解读体检报告
							"isxlgh":isxlghValue, //是否心理关怀
							"iskzjy":iskzjyValue, //是否开展家医签约
							"isqt":isqtValue //是否其他
						}
					},function(code,msg,json){				
							Ext.MessageBox.hide()
							if(code < 300){
								this.parent.loadData();
								MyMessageTip.msg("提示", "保存成功!", true);
								this.fireEvent("save",this);
								
							}else{
								alert(msg)
							}
					},this);	
				}
				debugger;
	},
	
	onSave : function() {
		this.onLoadData();
		this.getWin().hide();
	}
});
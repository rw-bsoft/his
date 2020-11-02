/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.hos.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.hos.script.HospitalDayQueryModule = function(cfg) {
	this.exContext = {};
	phis.application.hos.script.HospitalDayQueryModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalDayQueryModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var form = this.createForm();
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 78,
										items : form
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});
				this.panel = panel;
				//发票
				this.frameId = "SimplePrint_frame_invoice";
				this.conditionFormId = "SimplePrint_form_invoice";
				this.mainFormId = "SimplePrint_mainform_invoice";
				var fppanel = new Ext.Panel({
					id : this.mainFormId,
					width : 800,
					height : 500,
					title : '发票打印',
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"invoice\")'></iframe>"
				})
				this.fppanel = fppanel
				return panel;
			},
			createForm : function() {
				var BRXZcombox = util.dictionary.SimpleDicFactory.createDic({
							id : 'patientProperties',
							width : 130
						})
				BRXZcombox.name = 'BRXZ';
				BRXZcombox.fieldLabel = '病人性质';
				var dic = {"id":"user_bill","filter":"['eq',['$map',['s','b.manaUnitId']],['$','%user.manageUnit.id']]","src":"MS_YGPJ_FP.YGDM","width":130};
				var CZGHcombox = util.dictionary.SimpleDicFactory.createDic(dic)
				CZGHcombox.name = 'CZGH';
				CZGHcombox.fieldLabel = '收款员';
				var form = new Ext.FormPanel({
					labelWidth : 60,
					frame : true,
					defaultType : 'textfield',
					layout : 'tableform',
					layoutConfig : {
						columns : 6,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : [{
								fieldLabel : '发票号码:',
								name : 'FPHMFrom'
							}, {
								fieldLabel : '至',
								name : 'FPHMTo'
							}, new Ext.ux.form.Spinner({
										fieldLabel : '收费日期:',
										name : 'SFRQFrom',
										value : new Date().format('Y-m-d'),
										strategy : {
											xtype : "date"
										}
									}), new Ext.ux.form.Spinner({
										fieldLabel : '至',
										name : 'SFRQTo',
										value : new Date().format('Y-m-d'),
										strategy : {
											xtype : "date"
										}
									}), {
								xtype : 'panel',
								
								rowspan : "2",
								layout : "table",
								layoutConfig : {
									rowspan : 2
								},
								items : [new Ext.Button({
											iconCls : 'query',
											height : 40,
											width : 80,
											text : '查询',
											handler : this.doQuery,
											scope : this
										})]
							}, {
								xtype : "panel"
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 65,
											html : "病人性质:"
										}, BRXZcombox]
							}, {
								fieldLabel : '病人姓名:',
								name : 'BRXM'
							}, {
								fieldLabel : '门诊号码:',
								name : 'MZHM'
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
									xtype : "panel",
									width : 65,
									html : "收款员:"
								}, CZGHcombox]
							}]
				});
				this.form = form
				return form
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				return listModule;
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
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
					buttons.push(btn, '');
				}
				this.buttons = buttons;
				return buttons;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
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
			doFp : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;
				this.conditionFormId = "SimplePrint_form_invoice";
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var items = form.items
				this.printurl = util.helper.Helper.getUrl();
				//var url = this.printurl + ".print?pages=invoice";
				var pages="invoice";
				 var url="resources/"+pages+".print?type=1";
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					url += "&" + f.getName() + "=" + f.getValue()
				}
				url += "&temp=" + new Date().getTime()+"&fphm="+fphm;
				window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			},
			doDj : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryFphm",
							body : fphm
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该发票号码没有数据!");
						return;
					}
					// var cfsbs = r.json.body;
					var module = this.midiModules["mzModule"];
					if (!module) {
						module = this.createModule("mzModule", "IVC0203");
						module.opener = this;
					}
					module.initDataId = fphm;
					module.person = r.json.body;
					module.djs = r.json.djs;
					var win = module.getWin();
					module.loadData();
					win.show();
				}
			},
			doNewQuery : function() {
				var year = new Date().getFullYear();
				var month = new Date().getMonth() >= 9
						? (new Date().getMonth() + 1)
						: "0" + (new Date().getMonth() + 1)
				var date = new Date().getDate() >= 10
						? (new Date().getDate())
						: "0" + (new Date().getDate())
				var now = year + "-" + month + "-" + date;
				var from = this.form.getForm();
				from.findField("FPHMFrom").setValue("");
				from.findField("FPHMTo").setValue("");
				from.findField("SFRQFrom").setValue(now);
				from.findField("SFRQTo").setValue(now);
				from.findField("BRXZ").setValue("");
				from.findField("BRXM").setValue("");
				from.findField("MZHM").setValue("");
				from.findField("CZGH").setValue("");
			},
			doQuery : function() {
				var from = this.form.getForm();
				FPHMFrom = from.findField("FPHMFrom").getValue();
				FPHMTo = from.findField("FPHMTo").getValue();
				SFRQFrom = from.findField("SFRQFrom").getValue();
				SFRQTo = from.findField("SFRQTo").getValue()+' 23:59:59';
				BRXZ = from.findField("BRXZ").getValue();
				BRXM = from.findField("BRXM").getValue();
				MZHM = from.findField("MZHM").getValue();
				CZGH = from.findField("CZGH").getValue();
				if (FPHMFrom != null && FPHMTo != null && FPHMFrom != ""
						&& FPHMTo != "" && FPHMFrom > FPHMTo) {
					Ext.MessageBox.alert("提示", "开始发票号不能大于结束发票号");
					return;
				}
				if (SFRQFrom != null && SFRQTo != null && SFRQFrom != ""
						&& SFRQTo != "" && SFRQFrom > SFRQTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				FPHMFromCnd = ['ge', ['$', "a.FPHM"], ['s', FPHMFrom]];
				FPHMToCnd = ['le', ['$', "a.FPHM"], ['s', FPHMTo]];
				SFRQFromCnd = ['ge', ['$', "a.SFRQ"],
						['todate', SFRQFrom,'yyyy-mm-dd']];
				SFRQToCnd = ['le', ['$', "a.SFRQ"],
						['todate', SFRQTo,'yyyy-mm-dd hh:mm:ss']];
				BRXZCnd = ['eq', ['$', "a.BRXZ"], ['d', BRXZ]];
				BRXMCnd = ['like', ['$', "a.BRXM"], ['s', "%"+BRXM+"%"]];
				MZHMCnd = ['like', ['$', "d.MZHM"], ['s', MZHM+"%"]];
				CZGHCnd = ['eq', ['$', "a.CZGH"], ['s', CZGH]];
				var cnd = [];
				if (FPHMFrom != null && FPHMFrom != "") {
					cnd = FPHMFromCnd;
				}
				if (FPHMTo != null && FPHMTo != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, FPHMToCnd];
					} else {
						cnd = FPHMToCnd;
					}
				}
				if (SFRQFrom != null && SFRQFrom != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, SFRQFromCnd];
					} else {
						cnd = SFRQFromCnd;
					}
				}
				if (SFRQTo != null && SFRQTo != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, SFRQToCnd];
					} else {
						cnd = SFRQToCnd;
					}
				}
				if (BRXZ != null && BRXZ != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, BRXZCnd];
					} else {
						cnd = BRXZCnd;
					}
				}
				if (BRXM != null && BRXM != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, BRXMCnd];
					} else {
						cnd = BRXMCnd;
					}
				}
				if (MZHM != null && MZHM != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, MZHMCnd];
					} else {
						cnd = MZHMCnd;
					}
				}
				if (CZGH != null && CZGH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, CZGHCnd];
					} else {
						cnd = CZGHCnd;
					}
				}
				this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			}
			,
			/**
			 * 以下方法是医保接口获取数字签名方法
			 * 
			 * @return {}
			 */
			sign : function(ss) {
				var CAPICOM_STORE_OPEN_READ_ONLY = 0;
				var CAPICOM_CURRENT_USER_STORE = 2;
				var CAPICOM_CERTIFICATE_FIND_EXTENDED_PROPERTY = 6;
				var CAPICOM_CERTIFICATE_FIND_TIME_VALID = 9;
				var CAPICOM_CERTIFICATE_FIND_KEY_USAGE = 12;
				var CAPICOM_DIGITAL_SIGNATURE_KEY_USAGE = 0x00000080;
				var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
				var CAPICOM_E_CANCELLED = -2138568446;
				var CERT_KEY_SPEC_PROP_ID = 6;
				var CAPICOM_VERIFY_SIGNATURE_AND_CERTIFICATE = 1;
				var CAPICOM_ENCODE_BASE64 = 0;
				var CAPICOM_HASH_ALGORITHM_SHA1 = 0;
				var ret = "";
				var SignedData = new ActiveXObject("CAPICOM.SignedData"); // 数字签名对象
				var Signer = new ActiveXObject("CAPICOM.Signer"); // 签名人对象
				Signer.Options = 2;
				try {
					// 以下从证书列表中获取签名证书
					var Store = new ActiveXObject("CAPICOM.Store"); // 证书存贮对象
					Store.Open(CAPICOM_CURRENT_USER_STORE, "My",
							CAPICOM_STORE_OPEN_READ_ONLY);

					var FilteredSignCertificates = Store.Certificates.Find(
							CAPICOM_CERTIFICATE_FIND_KEY_USAGE,
							CAPICOM_DIGITAL_SIGNATURE_KEY_USAGE)
							.Find(CAPICOM_CERTIFICATE_FIND_TIME_VALID).Find(
									CAPICOM_CERTIFICATE_FIND_EXTENDED_PROPERTY,
									CERT_KEY_SPEC_PROP_ID); // 证书列表对象

					if (FilteredSignCertificates.Count == 1)
						Signer.Certificate = FilteredSignCertificates.Item(1);
					else if (FilteredSignCertificates.Count > 1)
						Signer.Certificate = FilteredSignCertificates.Select(
								"选择签名证书", "请选中你的签名证书，按确定").Item(1);
					else
						alert("无法读取可供选择的证书！");
					SignedData.Content = this.hex_sha1(ss); // 对原文摘要签名
					ret = SignedData.Sign(Signer, false, CAPICOM_ENCODE_BASE64);

				} catch (e) {
					alert("签名过程中出现错误: " + e.description);
				}
				return ret;
			},
			hex_sha1 : function(s) {
				var chrsz = 8;
				return this.binb2hex(this.core_sha1(this.str2binb(s), s.length
								* chrsz));
			},
			core_sha1 : function(x, len) {
				x[len >> 5] |= 0x80 << (24 - len % 32);
				x[((len + 64 >> 9) << 4) + 15] = len;
				var w = Array(80);
				var a = 1732584193;
				var b = -271733879;
				var c = -1732584194;
				var d = 271733878;
				var e = -1009589776;
				for (var i = 0; i < x.length; i += 16) {
					var olda = a;
					var oldb = b;
					var oldc = c;
					var oldd = d;
					var olde = e;
					for (var j = 0; j < 80; j++) {
						if (j < 16)
							w[j] = x[i + j];
						else
							w[j] = this.rol(w[j - 3] ^ w[j - 8] ^ w[j - 14]
											^ w[j - 16], 1);
						var t = this.safe_add(this.safe_add(this.rol(a, 5),
										this.sha1_ft(j, b, c, d)), this
										.safe_add(this.safe_add(e, w[j]), this
														.sha1_kt(j)));
						e = d;
						d = c;
						c = this.rol(b, 30);
						b = a;
						a = t;
					}
					a = this.safe_add(a, olda);
					b = this.safe_add(b, oldb);
					c = this.safe_add(c, oldc);
					d = this.safe_add(d, oldd);
					e = this.safe_add(e, olde);
				}

				return Array(a, b, c, d, e);
			},
			sha1_ft : function(t, b, c, d) {
				if (t < 20)
					return (b & c) | ((~b) & d);
				if (t < 40)
					return b ^ c ^ d;
				if (t < 60)
					return (b & c) | (b & d) | (c & d);
				return b ^ c ^ d;
			},
			sha1_kt : function(t) {
				return (t < 20) ? 1518500249 : (t < 40) ? 1859775393 : (t < 60)
						? -1894007588
						: -899497514;
			},
			safe_add : function(x, y) {
				var lsw = (x & 0xFFFF) + (y & 0xFFFF);
				var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
				return (msw << 16) | (lsw & 0xFFFF);
			},
			rol : function(num, cnt) {
				return (num << cnt) | (num >>> (32 - cnt));
			},

			binb2hex : function(binarray) {
				var hexcase = 0;
				var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
				var str = "";
				for (var i = 0; i < binarray.length * 4; i++)
					str += hex_tab
							.charAt((binarray[i >> 2] >> ((3 - i % 4) * 8 + 4))
									& 0xF)
							+ hex_tab
									.charAt((binarray[i >> 2] >> ((3 - i % 4) * 8))
											& 0xF);
				return str;
			},
			str2binb : function(str) {
				var chrsz = 8;
				var bin = Array();
				var mask = (1 << chrsz) - 1;
				for (var i = 0; i < str.length * chrsz; i += chrsz)
					bin[i >> 5] |= (str.charCodeAt(i / chrsz) & mask) << (32
							- chrsz - i % 32);
				return bin;
			},
			// ---------获取数字签名方法结束
			//发票补传
			doBc:function(){
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var jslsh = record.data.JSLSH;
				if(jslsh==null||jslsh==""||jslsh==undefined||jslsh.length<4){
				Ext.MessageBox.alert("提示", "不是医保发票");
					return;
				}
				var fpsc=record.data.FPSC
				if(fpsc=="1"){
				Ext.MessageBox.alert("提示", "已经上传过,不需要重复上传!");
					return;
				}
				var brxz= record.data.BRXZ;
				var ss="0";
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryNatureActionId
						});
					if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg,
							this.doBc);
					return;
					}
					this.natureList=ret.json.natureList;
					//查询参数里面的性质
					for(var i=0;i<this.natureList.length;i++){
					if(brxz==this.natureList[i]){
						if(brxz=="6104"){
							ss="1";
						}
					}
					}
			if (ss!="1") {
				var cas = "门诊上传发票号";
				var parXml='<?xml version=\"1.0\" encoding=\"GB18030\"?><XML><结算流水号>'+jslsh+'</结算流水号><姓名>'+record.data.BRXM+'</姓名><参保险种>'+record.data.CBXZ+'</参保险种><发票号>'+record.data.FPHM+'</发票号><发票流水号>'+record.data.FPHM+'</发票流水号></XML>';
				var ret=this.dyjk(cas,parXml);
				if(ret.状态=="2"){
				MyMessageTip.msg("提示", ret.备注, true);
				}else if(ret.状态=="3"){
				Ext.Msg.alert("提示",ret.备注);
				return;
				}
				}else{
				var cas = "放化疗碎石它院检查报销上传发票号";
				var parXml='<?xml version=\"1.0\" encoding=\"GB18030\"?><XML><结算流水号>'+jslsh+'</结算流水号><结算日期>'+record.data.SFRQ+'</结算日期><姓名>'+record.data.BRXM+'</姓名><参保险种>'+record.data.CBXZ+'</参保险种><发票号>'+record.data.FPHM+'</发票号><发票流水号>'+record.data.FPHM+'</发票流水号></XML>';
				var ret=this.dyjk(cas,parXml);
				if(ret.状态=="2"){
				MyMessageTip.msg("提示", ret.备注, true);
				}else if(ret.状态=="3"){
				Ext.Msg.alert("提示",ret.备注);
				return;
				}
				}
					var body_fp={}
				body_fp["jslsh"]=jslsh
				var ret_c=phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.uploadInvoiceActionId,
							body:body_fp
						});
					if (ret_c.code > 300) {
					this.processReturnMsg(ret_c.code, "发票上传失败!请重新传或者联系管理员!",
							this.doBc);
					return;
					}
					MyMessageTip.msg("提示", "发票上传成功", true);
					this.listModule.refresh();
					
			},
			//接口调用方法
			dyjk:function(cas,parXml){
				if(!this.dep||!this.cer){
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryDeptActionId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg,
							this.dyjk);
					return;
				}
				this.dep=ret.json.dep;
				this.cer=ret.json.cer;
				}
				
				var dep = this.dep;
				var cas = cas;
				var ser = "医院联网结算";
				var cer = this.cer;
				var parXml=parXml;
				var sig = this.sign(ser + dep + cas + parXml);
				var body = {};
				body["dep"] = dep;
				body["cas"] = cas;
				body["ser"] = ser;
				body["cer"] = cer;
				body["sig"] = sig;
				body["parXML"] = parXml;
				var ret_c = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.qualificationActionId,
							body : body
						});
				if (ret_c.code > 300) {
					this.processReturnMsg(ret_c.code, ret_c.msg,
							this.dyjk);
					return;
				}
				return ret_c.json.body;
			}
		});
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
$package("phis.application.reg.script");

$import("phis.script.TableForm",
	"phis.script.util.DateUtil",
	"phis.application.yb.script.MedicareCommonMethod",
	"phis.script.Phisinterface");

phis.application.reg.script.RegistrationManageForm = function(cfg) {
	this.colCount = 3;
	cfg.labelWidth = 55;
	this.labelWidth = 55;
	this.autoFieldWidth = true;
	Ext.apply(this, phis.script.Phisinterface);
	Ext.apply(this, phis.application.yb.script.MedicareCommonMethod);
	phis.application.reg.script.RegistrationManageForm.superclass.constructor
		.apply(this, [cfg]);
	this.yytag = 1;
	this.zjyyData = null;//add by lizhi 2017-12-25 紫金数云预约挂号信息
};
Ext.extend(phis.application.reg.script.RegistrationManageForm,
	phis.script.TableForm, {
		doCallIn : function() {
			var callInModel = this.createModule("callInModel",
				this.reCallIn);
			callInModel.initPanel();
			var form = this.form.getForm();
			var GHSJ = form.findField("GHSJ");
			var GHLB = form.findField("GHLB");
			callInModel.GHSJ = GHSJ.getValue();
			if (this.yytag == 2) {
				callInModel.ZBLB = GHLB.getValue();
			} else {
				callInModel.ZBLB = this.opener.ZBLB;
			}
			var win = callInModel.getWin();
			win.show();
			callInModel.on("reservationChoose", this.doReservationChoose,
				this);
		},
		onReady : function() {
			phis.application.reg.script.RegistrationManageForm.superclass.onReady
				.call(this);
			this.ghsjselect = this.form.getForm().findField("GHSJ");
			this.ghsjselect.maxValue = new Date(Date.getDateAfter(new Date,
				6));
			this.ghlbselect = this.form.getForm().findField("GHLB");
			this.ghsjselect.on("select", this.doGhsjselect, this);
			this.ghlbselect.on("select", this.doghlbselect, this);
			this.ghlbselect.store.on("load", this.defaultloadghlb, this);
			var NJJBYLLB=this.form.getForm().findField("NJJBYLLB");
			if(NJJBYLLB){
				this.NJJBYLLB=NJJBYLLB;
				this.NJJBYLLB.on("select",this.changeyllb,this);
				NJJBYLLB.setDisabled(false);
			}
			var YBMC=this.form.getForm().findField("YBMC");
			if(YBMC){
				this.ybmc=YBMC;
				YBMC.setDisabled(false);
			}
		},
		changeyllb:function(){
			this.ybmc.setValue("");
			var v=this.NJJBYLLB.getValue();
			var dic={};
			dic.id="phis.dictionary.ybJbbm";
			var tsbz="";//特殊病种
			if(v==16){
				var arr=this.opener.drnjjbkxx.YBMMBZ.split(",");
				var bz=""
				for(var i=0;i<arr.length;i++){
					bz+="'"+arr[i]+"',"
				}
				dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
				if(arr.length>=1){
					tsbz=arr[0];
				}
			}else if(v==171){
				var arr=this.opener.drnjjbkxx.YBMTBZ.split(",");
				var bz=""
				for(var i=0;i<arr.length;i++){
					bz+="'"+arr[i]+"',"
				}
				dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
				if(arr.length==1){
					tsbz=arr[0];
				}
			}
			this.ybmc.store.proxy = new util.dictionary.HttpProxy({
				method : "GET",
				url : util.dictionary.SimpleDicFactory.getUrl(dic)
			})
			this.ybmc.store.load();
			if(tsbz.length >2){
				this.ybmc.setValue(tsbz);
				tsbz="";
			}else{
				this.ybmc.setValue("20");
			}
		},
		defaultloadghlb : function() {
			if (Date.parseDate(Date.getServerDateTime(), 'Y-m-d h:i:s')
					.getHours() < 12) {
				this.ghlbselect.setValue(1);
			} else {
				this.ghlbselect.setValue(2);
			}
		},
		focusFieldAfter : function(index, delay) {
			var items = this.schema.items
			var form = this.form.getForm()
			for (var i = index + 1; i < items.length; i++) {
				var next = items[i]
				var field = form.findField(next.id)
				if (items[index] && items[index].id == 'GHLB') {
					var f = form.findField('GHLB');
					f.triggerBlur();
					this.opener.KSList.cndField.focus();
					return;
					// f.beforeBlur();
					// var age =
					// this.getAgeFromServer(f.getValue());
					// this.onBirthdayChange(age);
				}
				if (field && !field.disabled && !field.readOnly) {
					field.focus(false, delay || 200)
					return;
				}
			}
			var btns;
			if (this.showButtonOnTop) {
				if (this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar().items
					if (btns) {
						var n = btns.getCount()
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i)
							if (btn.cmd == "save") {
								if (btn.rendered) {
									btn.focus()
								}
								return;
							}
						}
					}
				}
			} else {
				btns = this.form.buttons;
				if (btns) {
					var n = btns.length
					for (var i = 0; i < n; i++) {
						var btn = btns[i]
						if (btn.cmd == "save") {
							if (btn.rendered) {
								btn.focus()
							}
							return;
						}
					}
				}
			}
		},
		doGhsjselect : function() {
			var ghlb = 1;
			if (this.ghlbselect.getValue()) {
				ghlb = this.ghlbselect.getValue();
			}
			this.fireEvent("refreshghks", this.ghsjselect.getValue(), ghlb);
			this.fireEvent("getJTYGH", this);
			this.opener.YSList.clear();
		},
		doghlbselect : function() {
			this.fireEvent("refreshghks", this.ghsjselect.getValue(),
				this.ghlbselect.getValue());
			this.opener.YSList.clear();
		},
		doNew : function() {
			if (this.ybkxx) {
				this.clearYbxx();// 清除医保缓存
			}
			if(this.zjyyData){//add by lizhi 2017-12-26清除紫金数云预约挂号信息缓存
				this.zjyyData=null;
			}
			var form = this.form.getForm();
			var JZKH = form.findField("JZKH");
			var BRXZ = form.findField("BRXZ");
			var BRXM = form.findField("BRXM");
			var BRXB = form.findField("BRXB");
			var KSMC = form.findField("KSMC");
			var JZYS = form.findField("JZYS");
			var GHJE = form.findField("GHJE");
			var ZLJE = form.findField("ZLJE");
			var ZJFY = form.findField("ZJFY");
			JZKH.setValue();
			JZKH.setDisabled(false);
			var btns = this.form.getTopToolbar().items;
			btns.item(1).setDisabled(false);
			btns.item(3).setDisabled(false);
			BRXZ.setValue();
			BRXM.setValue();
			BRXB.setValue();
			KSMC.setValue();
			JZYS.setValue();
			GHJE.setValue("0.00");
			ZLJE.setValue("0.00");
			ZJFY.setValue("0.00");
			this.opener.KSList.BRXX = "";
			this.opener.KSList.doCndQuery();
			this.opener.YSList.BRXX = "";
			this.opener.YSList.YSDM = "";
			this.opener.YSList.clear();
			this.opener.getJZHM();
			this.GHXX = "";
			this.opener.nhdk="";
			//zhaojian 2019-03-23 增加医保读卡判定变量
			this.opener.ybdk="";
		},
		doCreate : function() {
			// 新建 判断 1 卡号,门诊号码
			var r = phis.script.rmi.miniJsonRequestSync({
				serviceId : "clinicChargesProcessingService",
				serviceAction : "checkCardOrMZHM"
			});
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
				return;
			} else {
				if (!r.json.cardOrMZHM) {
					Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
					f.focus(false, 100);
					return;
				}
				this.opener.showModule(r.json.cardOrMZHM);
			}
		},
		doAppointment : function() {
			var ghsjobj = this.form.getForm().findField("GHSJ");
			var ghlbobj = this.form.getForm().findField("GHLB");
			var btns = this.form.getTopToolbar();
			var btn = btns.find("cmd", "appointment");
			btn = btn[0];
			if (ghsjobj) {
				ghsjobj.setDisabled(false);
			}
			if (ghlbobj) {
				ghlbobj.setDisabled(false);
			}
			if (btn.getText().indexOf("取消") == 0) {
				btn.setText(btn.getText().replace("取消预约", "预约"));
				if (ghsjobj) {
					ghsjobj.setDisabled(true);
				}
				if (ghlbobj) {
					ghlbobj.setDisabled(true);
				}
				var GHSJ = this.form.getForm().findField("GHSJ");
				var GHLB = this.form.getForm().findField("GHLB");
				GHSJ.setValue(this.opener.GHSJ);
				if (Date.parseDate(Date.getServerDateTime(), 'Y-m-d h:i:s')
						.getHours() < 12) {
					GHLB.setValue(1);
				} else {
					GHLB.setValue(2);
				}
				this.opener.yysign = 1;
				this.yytag = 1;
				this.doNew();
			} else {
				var ghrq = ghsjobj.getValue();
				var zblb = ghlbobj.getValue();
				this.fireEvent("refreshghks", ghrq, zblb);
				btn.setText(btn.getText().replace("预约", "取消预约"));
				this.opener.yysign = 2;
				this.yytag = 2;
				this.opener.YSList.clear();
				this.doNew();
				// 判断当前时间 当前班别
				if (new Date(ghrq).format('Y-m-d') == new Date()
						.format('Y-m-d')) {
					// 如果是当天上午 选预约时默认下午 并不可以编辑
					if (zblb == 1) {
						ghlbobj.setValue(2);
						ghlbobj.setDisabled(true);
					} else if (zblb == 2) {// 如果是当天下午 选预约时 默认第二天的上午
						var afterdate = Date.getDateAfter(new Date(ghrq)
							.format('Y-m-d'), 1);
						ghsjobj.setValue(afterdate);
						ghlbobj.setValue(1);
						ghsjobj.setMinValue(new Date(afterdate)
							.format('Y-m-d'));
					}
				}
			}
		},
		loadModuleCfg : function(id) {
			var result = phis.script.rmi.miniJsonRequestSync({
				serviceId : "moduleConfigLocator",
				id : id
			});
			if (result.code != 200) {
				if (result.msg = "NotLogon") {
					this.mainApp.logon(this.loadModuleCfg, this, [id]);
				}
				return null;
			}
			return result.json.body;
		},
		saveToServer : function(saveData) {
			var saveRequest = this.getSaveRequest(saveData); // **
			// 获取保存条件数据
			if (!saveRequest) {
				return;
			}
			if (!this.fireEvent("beforeSave", this.entryName, this.op,saveRequest)) {
				return;
			}

			this.saving = true;

			this.form.el.mask("正在保存数据...", "x-mask-loading");
			phis.script.rmi.jsonRequest({
				serviceId : "registeredManagementService",
				serviceAction : this.saveAction || "",
				body : saveRequest
			}, function(code, msg, json) {
				this.form.el.unmask();
				this.saving = false;
				if (code > 300) {
					this.processReturnMsg(code,msg,this.saveToServer,[saveData]);
					return;
				}
				Ext.apply(this.data,saveData);
				this.fireEvent("save",this.entryName,this.op,json, this.data);
				this.op = "update";
			}, this);
		},
		doSave : function() {
			//增加家医减免 
			this.GHXX.JYJMBZ = "";
			debugger;
			if (!this.GHXX) {
				MyMessageTip.msg("提示", "请先调入病人信息", true);
				return;
			}
			if (!this.GHXX.KSDM) {
				MyMessageTip.msg("提示", "请先选择挂号科室", true);
				return;
			}
			var ghsjvalue = this.form.getForm().findField("GHSJ")
				.getValue();
			var ghlbvalue = this.form.getForm().findField("GHLB")
				.getValue();
			var curdate = Date.parseDate(Date.getServerDateTime(),
				'Y-m-d h:i:s');
			var curghsj = curdate.format('Y-m-d H-i-s').replace("-","").replace(" ","");
			if (this.yytag == 2) {
				var nowdate = Date.parseDate(Date.getServerDateTime(),
					'Y-m-d h:i:s');
				var nowghsj = nowdate.format('Y-m-d');
				var nowghsjvalue = ghsjvalue.format('Y-m-d');
				var daylb = 1;
				if (nowdate.getHours() < 12) {
					daylb = 1;
				} else {
					daylb = 2;
				}
				if (nowghsj == nowghsjvalue) {
					if (daylb == ghlbvalue) {
						MyMessageTip.msg("提示", "当前班别不能预约", true);
						return;
					}
					if (daylb == 2) {
						if (ghlbvalue == 1) {
							MyMessageTip.msg("提示", "当天下午不能预约上午的号", true);
							return;
						}
					}
				}
			}
			debugger;
			if (this.ybkxx && this.ybkxx != null) {
				this.ybbhxx = this.GHXX;
				this.doYbghyjs(); // 预结算
			} else {
				var brxz = this.form.getForm().findField("BRXZ").getValue();
				// 打开结算找零前如果没有读卡判断病人是否是医保病人,如果是则不让结算
				// 必须读完卡才能结算(根据实际需求确定是否需要该判断)
				//增加了农合读卡判断
				if(this.opener.nhdk && this.opener.nhdk=="1"){

				}else if (this.getSfYb(brxz) == null) {//不明白这段代码意思，该方法不可能返回null zhaojian 2019-03-23
					return;
				}
				/**************zhaojian 2019-04-02 挂号收费增加一般诊疗费收取功能 begin**************/
				debugger;
				if(this.GHXX.njjbyjsxx){delete this.GHXX.njjbyjsxx;}
				// yx-南京金保业务流程
				if (brxz == "2000") {
					var njjbbody = {};
					njjbbody.USERID = this.mainApp.uid;
					njjbbody.empiId = this.GHXX.EMPIID;
					// 获取业务周期号
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getywzqh",
						body : njjbbody
					});
					if (ret.code <= 300) {
						this.ywzqh = ret.json.YWZQH;
					} else {
						Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
						return;
					}
					var djbody = {};
					var ybmc = this.form.getForm().findField("YBMC");
					var yllb = this.form.getForm().findField("NJJBYLLB");
					if (!yllb.getValue()) {
						MyMessageTip.msg("提示", "请录入医疗类别！", true);
						return;
					}
					if (!ybmc.getValue()) {
						MyMessageTip.msg("提示", "请录入医保病种！", true);
						return;
					}
					var ybmctemp = ybmc.getValue();
					if ((ybmctemp + "").indexOf(",") > 0) {
						ybmctemp = (ybmctemp + "").substring(0,(ybmctemp + "").indexOf(","));
					}
					djbody.YBJBBM = ybmctemp;//医保病种，根据表单中所选值
					djbody.YLLB = yllb.getValue();//医疗类别，根据表单中所选值
					djbody.YSDM = this.GHXX.YSDM;
					/*							djbody.YSMC = ysdm.getRawValue();
					 djbody.ICD10 = this.MZXX.ICD10;*/
					djbody.KSDM = this.GHXX.KSDM;
					djbody.GHSJ = curghsj;//获取当前时间
					djbody.YBZY =  "";
//							return;
					// 获取卡信息
					var getkxx = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getnjjbkxx",
						body : njjbbody
					});
					debugger;
					var ylrylb;
					var jmsfzh;
					var DQZHYE;
					if (getkxx.code <= 300) {
						if (getkxx.json && getkxx.json.jbkxx) {
							djbody.SHBZKH = getkxx.json.jbkxx.SHBZKH;
							djbody.JBR = this.mainApp.uid;
							djbody.LXDH = getkxx.json.jbkxx.LXDH;
							djbody.SFZH = getkxx.json.jbkxx.SFZH;
							ylrylb = getkxx.json.jbkxx.YLRYLB;
							jmsfzh = getkxx.json.jbkxx.SFZH;
							DQZHYE = getkxx.json.jbkxx.DQZHYE;
						} else {
							MyMessageTip.msg("提示", "获取医保信息失败！",
								true);
							this.running = false;
							return;
						}
					} else {
						MyMessageTip.msg("提示", "获取医保信息失败！", true);
						this.running = false;
						return;
					}
					this.opener.ybdk = "0";
					// 获取流水号
					var getlsh = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getnjjblsh"
					});
					if (getlsh.code >= 300) {
						MyMessageTip.msg("提示",  "获取流水号失败！", true);
						return;
					} else {
						djbody.NJJBLSH = getlsh.json.lsh.LSH;
						//djbody.NJJBLSH="CS"+djbody.NJJBLSH
					}
					/***********医保，家医签约享受挂号费减免一元钱 hujian 2020-05-08*************/
					if(DQZHYE>=1){
						var data = {};
						data.SFZH = jmsfzh;
						debugger;
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "getjypackage",
							body:data
							});
						debugger;
						if(ret.json.FLAGE&&ret.code==200&&this.GHXX.ZLJE>0){
							this.GHXX.ZLJE = parseInt(this.GHXX.ZLJE)-1+"";
							if(this.GHXX.ZLJE=="8"){//解决缓存8元的问题
								this.GHXX.ZLJE = parseInt(this.GHXX.ZLJE)+1+"";
							};
							this.GHXX.JYJMBZ = "家医减免一元";
							alert("该患者已签约家庭医生：享受挂号费减免一元钱，"+"减免后挂号金额:¥"+this.GHXX.ZLJE);
						}
					};
					this.addPKPHISOBJHtmlElement();
					this.GHXX.JBBM = djbody.YBJBBM;
					this.GHXX.YBMC=djbody.YBJBBM;
					this.GHXX.YLLB = djbody.YLLB;
					this.GHXX.NJJBYLLB=djbody.YLLB;
					this.GHXX.JBR = djbody.JBR;
					this.GHXX.SHBZKH = djbody.SHBZKH;
					this.GHXX.IDCARD = djbody.SFZH;
					/*					if(this.GHXX.YBMC){
					 this.GHXX.JBBM=this.GHXX.YBMC;
					 }*/
					this.GHXX.YSBM="";
					this.GHXX.LXDH="";
					this.GHXX.NJJBLSH=djbody.NJJBLSH;
					debugger;
					var str=this.buildstr("2210",this.ywzqh,this.GHXX);
					var drre=this.drinterfacebusinesshandle(str);
					var arr=drre.split("^");
					if(arr[0]=="0"){
						//登记成功
					}else{
						MyMessageTip.msg("金保返回提示",arr[3], true);
						this.running = false;
						return;
					}
					if(this.GHXX.ZLXM && this.GHXX.ZLXM!=""){
						// 先撤销处方明细
						var njjbcxmx = {};
						njjbcxmx.LSH = djbody.NJJBLSH;
						njjbcxmx.CFH = djbody.NJJBLSH + "-99";
						njjbcxmx.CFLSH = "";
						njjbcxmx.JBR = this.mainApp.uid;
						var str = this.buildstr("2320", this.ywzqh,
							njjbcxmx);
						var drre = this.drinterfacebusinesshandle(str);
						var arr = drre.split("^")
						if (arr[0] == 0
							|| (arr[0] == "-2" && arr[3]
								.indexOf("没有需要删除") > -1)) {
							// 清除费用明细成功
						} else {
							MyMessageTip.msg("撤销处方明细提示", drre, true);
							return;
						}
						// yx-上传费用明细-b
						var data=[];
						var fymx = {};
						fymx.CFLX = "0";
						fymx.YPXH = this.GHXX.ZLXM;//"2005" 一般诊疗费对应费用序号
						fymx.FYGB = "13";//费用归并：诊查费
						fymx.YPDJ = this.GHXX.ZLJE;//"10.00";//单价
						//this.GHXX.GHJE=10;
						fymx.YPSL = "1";//数量
						debugger;
						fymx.HJJE = this.GHXX.ZLJE;// "10.00";//合计
						fymx.KFRQ = djbody.GHSJ;//此处取挂号时间
						fymx.CFTS = "1";
						fymx.KSDM = this.GHXX.KSDM;
						fymx.YSDM = "";
						data.push(fymx);
						var getsfmx = phis.script.rmi
							.miniJsonRequestSync({
								serviceId : "phis.NjjbService",
								serviceAction : "getnjjbsfmx",
								body : data
							});
						var sfmxdata = {};
						sfmxdata.YSDM = djbody.YSDM;
						sfmxdata.KSDM = djbody.KSDM;
						sfmxdata.YBJBBM = djbody.YBJBBM;
						sfmxdata.JBR = njjbcxmx.JBR;
						sfmxdata.NJJBLSH = djbody.NJJBLSH;
						sfmxdata.CFH = djbody.NJJBLSH + "-99";
						if (getsfmx.code <= 300) {
							if (getsfmx.json.fyxxlist) {
								sfmxdata.sfmx = getsfmx.json.fyxxlist;
							} else {
								MyMessageTip.msg("提示", "无可收费信息！", true);
								return;
							}
						} else {
							MyMessageTip.msg("提示", getsfmx.msg, true);
							return;
						}
						var cfxxstr = this.buildstr("2310", this.ywzqh,
							sfmxdata);
						// yx-上传费用明细-e
						var cfre = this
							.drinterfacebusinesshandle(cfxxstr);
						var cfmxarr = cfre.split("^");
						if (cfmxarr[0] == "0") {
							// 上传成功
						} else {
							MyMessageTip.msg("上传费用明细提示", cfre, true);
							return;
						}
						// yx-预结算-b
						var yjsxx = {};
						yjsxx.NJJBLSH = djbody.NJJBLSH;
						yjsxx.DJH = "";
						yjsxx.YLLB = djbody.YLLB;
						yjsxx.JSRQ = new Date();
						yjsxx.CYRQ = new Date();
						yjsxx.CYYY = "";// 出院原因
						if (djbody.YBZY && djbody.YBZY.length > 0) {
							yjsxx.CYYY = "03";
						}
						yjsxx.CYZDJBBM = djbody.YBJBBM;// 出院诊断疾病编码
						yjsxx.YJSLB = "";// 月结算类别
						yjsxx.ZTJSBZ = "0";// 中途结算标志
						yjsxx.JBR = djbody.JBR;// 经办人
						yjsxx.FMRQ = ""// 分娩日期
						yjsxx.CC = "";// 产次
						yjsxx.TRS = "";// 胎儿数
						yjsxx.SHBZKH = djbody.SHBZKH;// 社会保障卡号
						yjsxx.ZYYYBH = djbody.YBZY// 转院医院编号
						yjsxx.KSDM = djbody.KSDM;// 科室编码
						yjsxx.YSDM = djbody.YSDM;// 医生编码
						yjsxx.SFWGHFJS = "1";// 是否为挂号费结算
						yjsxx.ZSESHBZKH = "";// 准生儿社会保障卡号
						yjsxx.SSSFCHBZ = "1"// 手术是否成功标志
						debugger;
						var yjsxxstr = this.buildstr("2420",
							this.ywzqh, yjsxx);
						// alert(yjsxxstr)
						debugger;
						var reyjsxx = this
							.drinterfacebusinesshandle(yjsxxstr);
//							alert(reyjsxx);
						debugger;
						var yjsarr = reyjsxx.split("^");
						var njjbyjsxx = {};
						if (yjsarr[0] == 0) {
							var reyjs = yjsarr[2].split("|");
							njjbyjsxx.BCYLFZE = reyjs[0];// 本次医疗费总额
							njjbyjsxx.BCTCZFJE = reyjs[1];// 本次统筹支付金额
							njjbyjsxx.BCDBJZZF = reyjs[2];// 本次大病救助支付
							njjbyjsxx.BCDBBXZF = reyjs[3];// 本次大病保险支付
							njjbyjsxx.BCMZBZZF = reyjs[4];// 本次民政补助支付
							njjbyjsxx.BCZHZFZE = reyjs[5];// 本次帐户支付总额
							njjbyjsxx.BCXZZFZE = reyjs[6];// 本次现金支付总额
							njjbyjsxx.BCZHZFZF = reyjs[7];// 本次帐户支付自付
							njjbyjsxx.BCZHZFZL = reyjs[8];// 本次帐户支付自理
							njjbyjsxx.BCXJZFZF = reyjs[9];// 本次现金支付自付
							njjbyjsxx.BCXJZFZL = reyjs[10];// 本次现金支付自理
							njjbyjsxx.YBFWNFY = reyjs[11];// 医保范围内费用
							njjbyjsxx.ZHXFHYE = reyjs[12];// 帐户消费后余额
							njjbyjsxx.DBZBZBM = reyjs[13];// 单病种病种编码
							njjbyjsxx.SMXX = reyjs[14];// 说明信息
							njjbyjsxx.YFHJ = reyjs[15];// 药费合计
							njjbyjsxx.ZLXMFHJ = reyjs[16];// 诊疗项目费合计
							njjbyjsxx.BBZF = reyjs[17];// 补保支付
							njjbyjsxx.YLLB = reyjs[18];// 医疗类别
							njjbyjsxx.BY6 = reyjs[19];// 备用6

						} else {
							debugger;
							MyMessageTip.msg("南京金保预结算提示", yjsarr, true);
							return;
						}
						debugger;
						njjbyjsxx.NJJBYJSXX = yjsxx;
						this.GHXX.njjbyjsxx = njjbyjsxx;
					}
					// yx-预结算-e
				}
				/**************zhaojian 2019-04-02 挂号收费增加一般诊疗费收取功能 end**************/
				this.GHXX.GHSJ = ghsjvalue;
				this.GHXX.GHLB = ghlbvalue;
				var module = this.midiModules["rjsModule"];
				if (!module) {
					module = this.createModule("rjsModule", this.refJsForm);
					this.midiModules["rjsModule"] = module;
					module.opener = this;
					module.on("settlement", this.doNew, this);
				}
				this.GHXX.BLJE = this.form.getForm().findField('BLJE').getValue();
				var win = module.getWin();
				module.setData(this.GHXX);
				module.yytag = this.yytag;
				module.zjyyData = this.zjyyData;//add by lizhi 2017-12-25 紫金数云预约挂号信息
				debugger;
				win.show();
			}

		},
		doRetire : function() {
			var module = this.midiModules["rhjsModule"];
			if (!module) {
				module = this.createModule("rhjsModule", this.retireForm);
				if (!module)
					return;
				this.midiModules["rhjsModule"] = module;
				module.opener = this;
				module.on("retire", this.doNew, this);
			}
			var win = module.getWin();
			win.show();
		},
		doReservationChoose : function(grid, record) {
			if (!this.GHXX) {
				this.GHXX = {};
			}
			this.GHXX.YYBZ = 1;
			this.GHXX.KSDM = record.get("KSDM");
			this.GHXX.YSDM = record.get("YSDM");
			this.GHXX.YYXH = record.get("YYXH");
			this.GHXX.JZXH = record.get("JZXH");
			this.clearYbxx();
			this.opener.getBRXXByMZHM(record.get("MZHM"), this.GHXX.KSDM,
				this.GHXX.YSDM, 1);
		},
		/*
		 * 读卡
		 */
		doYbdk : function() {
			this.opener.showYbModule();
		},
		//农合读卡
		doNhdk : function() {
			this.opener.doNhghdk();
		},
		//健康卡
		doJkkdk : function() {
			this.opener.doJkkghdk();
		},
		//南京金保
		doNjjb:function(){
			//先执行读卡程序
			var body={};
			body.USERID=this.mainApp.uid;
			//获取业务周期号
			var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:body
			});
			if (ret.code <= 300) {
				var ywzqh=ret.json.YWZQH;
				this.addPKPHISOBJHtmlElement();
				this.drinterfaceinit();
				var str=this.buildstr("2100",ywzqh,"");
				var drre=this.drinterfacebusinesshandle(str);
				var arr=drre.split("^");
				this.SHBZKH="";
				if(arr[0]=="0"){
					var canshu=arr[2].split("|")
					var data={};
					data.SHBZKH=canshu[0];//社会保障卡号
					this.SHBZKH=canshu[0];
					data.DWBH=canshu[1];//单位编号
					data.DWMC=canshu[2];//单位名称
					data.SFZH=canshu[3];//身份证号
					data.XM=canshu[4];//姓名
					data.XB=canshu[5];//性别
					data.YLRYLB=canshu[6];//医疗人员类别
					data.YDRYBZ=canshu[7];//异地人员标志
					data.TCQH=canshu[8];//统筹区号
					data.DQZHYE=canshu[9];//当前帐户余额
					data.ZYZT=canshu[10];//在院状态
					data.BNZYCS=canshu[11];//本年住院次数
					data.DYXSBZ=canshu[12];//待遇享受标志
					data.DYBXSYY=canshu[13];//待遇不享受原因
					data.BZDJQK=canshu[14];//病种登记情况
					data.YBMMZG=canshu[15];//医保门慢资格
					data.YBMMBZ=canshu[16];//医保门慢病种
					data.YBMJZG=canshu[17];//医保门精资格
					data.YBMJBZ=canshu[18];//医保门精病种
					data.YBMAZG=canshu[19];//医保门艾资格
					data.YBMABZ=canshu[20];//医保门艾病种
					data.YBBGGRSZG=canshu[21];//医保丙肝干扰素资格
					data.YBBGGRSBZ=canshu[22];//医保丙肝干扰素病种
					data.YBMZXYBZG=canshu[23];//医保门诊血友病资格
					data.YBMZXYBBZ=canshu[24];//医保门诊血友病病种
					data.YBMTZG=canshu[25];//医保门特资格
					data.YBMTBZ=canshu[26];//医保门特病种
					data.YBTYZG=canshu[27];//医保特药资格
					data.YBTYBZ=canshu[28];//医保特药病种
					data.YBTYMCBM=canshu[29];//医保特药名称编码
					data.JMMDZG=canshu[30];//居民门大资格
					data.JMMDBZ=canshu[31];//居民门大病种
					data.JMMZXYBZG=canshu[32];//居民门诊血友病资格
					data.JMMZXYBBZ=canshu[33];//居民门诊血友病病种
					data.JMTYZG=canshu[34];//居民特药资格
					data.JMTYBZ=canshu[35];//居民特药病种
					data.JMTYMCBM=canshu[36];//居民特药名称编码
					data.NMGMDZG=canshu[37];//农民工门大资格
					data.NMGMDBZ=canshu[38];//农民工门大病种
					data.NMGTYZG=canshu[39];//农民工特药资格
					data.NMGTYBZ=canshu[40];//农民工特药病种
					data.NMGTYMCBM=canshu[41];//农民工特药名称编码
					data.NFXSZGMZTC=canshu[42];//能否享受职工门诊统筹
					data.SYSPLX=canshu[43];//生育审批类型
					data.FSYY=canshu[44];//封锁原因
					data.MMSYKBJE=canshu[45];//门慢剩余可报金额
					data.MTFZZLSYKBJE=canshu[46];//门特辅助治疗剩余可报金额
					data.GSDYZG=canshu[47];//工伤待遇资格
					data.GSDYBZ=canshu[48];//工伤待遇病种
					data.GSZDJL=canshu[49];//工伤诊断结论
					data.DKSYKBJE=canshu[50];//大卡剩余可报金额
					data.MTSYKBJE=canshu[51];//门统剩余可报金额
					data.YBJCZG = canshu[52];//医保家床资格
					/****增加建档立卡低收入人群参数  hj****/
					data.YBMZZXZG = canshu[53];//医保门诊专项资格
					data.YBMZZXYPTYMBM = canshu[54];//医保门诊专项药品通用名编码
					data.SFKNJDLK = canshu[55];//是否困难建档立卡人员
					//跳转到RegistrationManageModule.js
					this.opener.donjjbform(data);
				}else{
					MyMessageTip.msg("提示：","金保返回错误:"+drre+",HIS系统提示读卡器可能没连接或没插卡！", true);
					return;
				}

			} else {
				MyMessageTip.msg("提示：",ret.msg, true);
				return;
			}
		},
		/**
		 * 转科处理
		 */
		doTurnDept : function() {
			if (this.tdWin) {
				var module = this.midiModules["refTurnDept"];
				module.on("settlement", this.doNew, this);
				module.clearData();
				module.getParameter();
			} else {
				module = this.createModule("refTurnDept",
					this.refTurnDept);
				if (!module)
					return;
				module.on("settlement", this.doNew, this);
				module.opener = this;
				this.tdWin = module.getWin();
				this.tdWin.setTitle(module.name);
				this.tdWin.add(module.initPanel());

			}
			this.tdWin.show();
		},
		//紫金数云预约取号
		doGetNo : function() {
			if(this.zjyyData){//add by lizhi 2017-12-26清除紫金数云预约挂号信息缓存
				this.zjyyData = null;
			}
			this.opener.doGetNo();
		},
		//分时预约处理
		doYycl:function(){
			this.opener.openFsyyList();
		},
		doPrintSet : function() {
			var LODOP = getLodop();
			LODOP.PRINT_INITA(10, 10, 501, 499, "挂号收费发票");
			LODOP.SET_PRINT_STYLE("FontColor", "#0000FF");
			LODOP.ADD_PRINT_TEXT(23, 34, 130, 25, "就诊号码");
			LODOP.ADD_PRINT_TEXT(47, 34, 130, 25, "姓名");
			LODOP.ADD_PRINT_TEXT(72, 34, 130, 25, "日期");
			LODOP.ADD_PRINT_TEXT(95, 34, 130, 25, "挂号费");
			LODOP.ADD_PRINT_TEXT(119, 34, 130, 25, "诊疗费");
			LODOP.ADD_PRINT_TEXT(142, 34, 130, 25, "专家费");
			LODOP.ADD_PRINT_TEXT(165, 34, 130, 25, "病历费");
			LODOP.ADD_PRINT_TEXT(216, 33, 130, 25, "本年账户支付");
			LODOP.ADD_PRINT_TEXT(239, 33, 130, 25, "历年账户支付");
			LODOP.ADD_PRINT_TEXT(263, 33, 130, 25, "统筹基金");
			LODOP.ADD_PRINT_TEXT(287, 33, 130, 25, "医疗救助基金支付");
			LODOP.ADD_PRINT_TEXT(310, 33, 130, 25, "现金支付");
			LODOP.ADD_PRINT_TEXT(347, 32, 45, 25, "门诊号码");
			LODOP.ADD_PRINT_TEXT(347, 77, 107, 25, "门诊号码");
			LODOP.ADD_PRINT_TEXT(398, 31, 45, 25, "挂号科室");
			LODOP.ADD_PRINT_TEXT(398, 75, 41, 25, "上午或者下午");
			LODOP.ADD_PRINT_TEXT(398, 115, 35, 25, "门诊序号");
			LODOP.ADD_PRINT_TEXT(398, 148, 30, 25, "号");
			LODOP.ADD_PRINT_TEXT(427, 30, 148, 25, "机构名称");
			LODOP.ADD_PRINT_TEXT(23, 281, 130, 25, "就诊号码");
			LODOP.ADD_PRINT_TEXT(47, 281, 130, 25, "姓名");
			LODOP.ADD_PRINT_TEXT(68, 281, 130, 25, "日期");
			LODOP.ADD_PRINT_TEXT(92, 281, 130, 25, "挂号费");
			LODOP.ADD_PRINT_TEXT(117, 281, 130, 25, "诊疗费");
			LODOP.ADD_PRINT_TEXT(141, 281, 130, 25, "专家费");
			LODOP.ADD_PRINT_TEXT(165, 281, 130, 25, "病历费");
			LODOP.ADD_PRINT_TEXT(211, 282, 130, 25, "本年账户支付");
			LODOP.ADD_PRINT_TEXT(234, 282, 130, 25, "历年账户支付");
			LODOP.ADD_PRINT_TEXT(258, 282, 130, 25, "统筹基金");
			LODOP.ADD_PRINT_TEXT(282, 282, 130, 25, "医疗救助基金支付");
			LODOP.ADD_PRINT_TEXT(306, 282, 130, 25, "现金支付");
			LODOP.ADD_PRINT_TEXT(344, 283, 45, 25, "门诊号码");
			LODOP.ADD_PRINT_TEXT(344, 325, 107, 25, "门诊号码");
			LODOP.ADD_PRINT_TEXT(394, 283, 46, 25, "挂号科室");
			LODOP.ADD_PRINT_TEXT(394, 328, 52, 25, "上午或者下午");
			LODOP.ADD_PRINT_TEXT(394, 378, 32, 25, "门诊序号");
			LODOP.ADD_PRINT_TEXT(394, 408, 32, 25, "号");
			LODOP.ADD_PRINT_TEXT(424, 283, 158, 25, "机构名称");
			LODOP.PRINT_SETUP();
		}
	});

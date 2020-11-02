/**
 * 处方点评-最外面的module
 * 
 * @author caijy
 */
$package("phis.application.pcm.script");

$import("phis.script.SimpleModule");

phis.application.pcm.script.PrescriptionCommentsModule = function(cfg) {
	cfg.isNewOpen=1;
	phis.application.pcm.script.PrescriptionCommentsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsModule,
		phis.script.SimpleModule, {
			// 页面初始化
			initPanel : function(sc) {
				if (this.panel) {
					return this.panel;
				}
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
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.getTbar(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'west',
										width : 110,
										collapsible : true,
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										items : this.getRModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refList);
				this.leftList.on("cyrqClick",this.onCyrqClick,this);
				this.leftList.on("noRecord",this.onNoRecord,this);
				return this.leftList.initPanel();
			},
			// 获取右边的module
			getRModule : function() {
				this.rightModule = this.createModule("rightModule",
						this.refModule);
				this.rightModule.on("afterLoadData",this.onRightLoadData,this);
				this.rightModule.on("changeButtonState",this.onChangeButtonState,this);
				return this.rightModule.initPanel();
			},
			getTbar : function() {
				this.year = new Ext.ux.form.Spinner({
							fieldLabel : '财务月份',
							name : 'storeDate',
							width:80,
							value : new Date().format("Y"),
							strategy : {
								xtype : "year"
							}
						})
				this.dplx = this.getSelectBox();
				var tbar = [this.year, '-', this.dplx]
				return tbar.concat(this.createButtons())
			},
			// 生成条件下拉框
			getSelectBox : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "DPLX") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 100;
				f = this.createDicField(it.dic);
				f.on("select", this.onSelect, this);
				return f;
			},
			onSelect:function(){
			this.doRefresh();
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"

				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			},
			// 抽取
			doCq : function() {
				if(this.dplx.getValue()==null||this.dplx.getValue()==""){
				MyMessageTip.msg("提示", "未选择点评类型,请先选择类型", true);
				return;
				}
				this.cqForm = this.createModule("cqForm", this.refCqform);
				this.cqForm.on("cqcg",this.doRefresh,this)
				var win = this.cqForm.getWin();
				win.add(this.cqForm.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.cqForm.dplx=this.dplx.getValue();
					this.cqForm.doNew();
				}
			},
			//刷新
			doRefresh:function(){
			this.leftList.dplx=this.dplx.getValue();
			this.leftList.year=this.year.getValue();
			this.leftList.loadData();
			},
			//打开界面刷新
			afterOpen:function(){
			this.doRefresh();
			},
			//左边点击,刷新右边记录
			onCyrqClick:function(cyxh,cywc){
				var _ctr=this;
				this.panel.el.mask("正在加载中.....", "x-mask-loading") 
//				if(this.isNewOpen==1){
//					var whatsthetime = function() {
//								_ctr.rightModule.list.selectedIndex=0
//								_ctr.rightModule.loadData(cyxh,cywc);
//							}
//					whatsthetime.defer(500);
//				}else{
//				_ctr.rightModule.list.selectedIndex=0
//				this.rightModule.loadData(cyxh,cywc);
//				}
				if(this.rightModule.leftMoudle.midiModules["cyjg"]){
				this.rightModule.leftMoudle.midiModules["cyjg"].list.selectedIndex=0;
				}
				this.rightModule.loadData(cyxh,cywc);	
//			var whatsthetime = function() {
//								_ctr.panel.el.unmask();
//							}
//			whatsthetime.defer(1000);
			},
			onRightLoadData:function(){
			this.panel.el.unmask();
			},
			//如果左边List没有记录 清空右边
			onNoRecord:function(){
			this.rightModule.doNew();
			},
			//删除按钮
			doRemove:function(){
			var r=this.leftList.getSelectedRecord();
			if(r==null){
			return ;
			}
			if(r.get("WCZT")==1){
			MyMessageTip.msg("提示", "已经完成数据不能删除", true);
			return;
			}
			if(this.rightModule.leftMoudle.getSfdp()){
			Ext.Msg.show({
								title : "提示",
								msg : "已经有记录点评,是否继续删除?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.doUpdate(r.get("CYXH"),"sc");
									}
								},
								scope : this
							});
			}else{
			this.doUpdate(r.get("CYXH"),"sc");
			}
			},
			//更新记录
			doUpdate:function(cyxh,tag){
			this.panel.el.mask("正在更新数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : {"CYXH":cyxh,"TAG":tag}
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return;
				}
				MyMessageTip.msg("提示", "更新成功", true);
				this.doRefresh();
			},
			//完成按钮
			doWc:function(){
			var r=this.leftList.getSelectedRecord();
			if(r==null){
			return ;
			}
			if(r.get("WCZT")==1){
			MyMessageTip.msg("提示", "记录已经完成,不需要重复完成", true);
			return;	
			}
				if(!this.rightModule.leftMoudle.getSfqbdp()){
				Ext.Msg.show({
								title : "提示",
								msg : "还有未点评的记录,是否继续完成?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.doUpdate(r.get("CYXH"),"wc");
									}
								},
								scope : this
							});
			}else{
			this.doUpdate(r.get("CYXH"),"wc");
			}
			},
			//取消完成
			doQxwc:function(){
			var r=this.leftList.getSelectedRecord();
			if(r==null){
			return ;
			}
			if(r.get("WCZT")==0){
			MyMessageTip.msg("提示", "记录未完成,不需要取消完成", true);
			return;	
			}
			this.doUpdate(r.get("CYXH"),"qxwc");
			},
			//抽样结果打印
			doPrint:function(){
			var mo=this.rightModule.leftMoudle.midiModules["dpjg"];
			if(mo){
			mo.doPrint();
			}
			},
			doDc:function(){
			var mo=this.rightModule.leftMoudle.midiModules["dpjg"];
			if(mo){
			mo.doDc();
			}
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getTopToolbar();
				if (!btns) {
					return;
				}
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
			},
			//钟伟伟要求的.见2.4.1BUG1111
			onChangeButtonState:function(tabid){
			if(tabid=="cyjg"){
			this.setButtonsState(['print','dc'],false)
			}else{
			this.setButtonsState(['print','dc'],true)
			}
				
			},
			//自动点评
			doZddp:function(){
			var r=this.leftList.getSelectedRecord();
			if(r==null){
			return ;
			}
			if(r.get("WCZT")==1){
			MyMessageTip.msg("提示", "记录已经完成,不需要自动点评", true);
			return;	
			}
			//this.panel.el.mask("正在自动点评...", "x-mask-loading")
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.zddpServiceAction,
							CYXH :r.get("CYXH")
						});
			if (ret.code > 300) {
				this.panel.el.unmask();
				this.processReturnMsg(ret.code, ret.msg);
				return;
			}
			var records=ret.json.body;
			var l=records.length;
			for(var x=0;x<l;x++){
			var res=this.hlyyFun(records[x])
			if(!res){
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "自动点评出错", true);
				return;
			}
			}
			this.panel.el.unmask();
			MyMessageTip.msg("提示", "自动点评完成", true);
			},
			hlyyFun : function(record) {
					var gmywr = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.simpleQuery",
								schema : "phis.application.cic.schemas.GY_PSJL_MZ",
								cnd : ["eq", ["$", "a.BRID"],
										["l", record.BRID]]
							});
					this.gmyw = "";
					if (gmywr.code > 300) {
						this.processReturnMsg(gmywr.code, gmywr.msg);
						return false;
					} else {
						if (gmywr.json.body.length > 0) {
							for (var i = 0; i < gmywr.json.body.length; i++) {
								if (i == 0) {
									this.gmyw = "1||" + gmywr.json.body[i].YPXH
								} else {
									this.gmyw += ",1||"
											+ gmywr.json.body[i].YPXH
								}
							}
						}
					}
					var nowtime = new Date().format("Y-m-d h:m:s");
					// 诊断
					var brzdStr = "";
					var brzdsres = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "loadClinicInfo",
								body : {
									"brid" : record.BRID,
									"clinicId" : record.JZXH,
									"type" : "2"
								}
							});
					if (brzdsres.code <= 300) {
						var brzds = brzdsres.json.ms_brzd;
						for (var i = 0; i < brzds.length; i++) {
							if (i == 0) {
								brzdStr = brzds[i].ZDXH + "||" + brzds[i].ICD9
										+ "||" + brzds[i].ZDMC
							} else {
								brzdStr += "," + brzds[i].ZDXH + "||"
										+ brzds[i].ICD9 + "||" + brzds[i].ZDMC
							}
						}
					}
					var ZSXX = record.ZSXX;
//					if (document.getElementById("ZSXX")) {
//						ZSXX = document.getElementById("ZSXX").value
//					}
					var TZXX = record.W;
//					if (document.getElementById("W")) {
//						TZXX = document.getElementById("W").value
//					}
					var rzbz = record.RSBZ;
//					var radioObj = document.getElementsByName("RSBZ");
//					for (var i = 0; i < radioObj.length; i++) {
//						if (radioObj[i].type == "radio") {
//							if (radioObj[i].checked == true) {
//								rzbz = radioObj[i].value;
//							}
//						}
//					}
					var rzbz_r = 0;
					var plbz_r = 0;
					if ((rzbz + '') == '2') {
						plbz_r = 1;
					} else if ((rzbz + '') == '1') {
						rzbz_r = 1;
					}
					var userUrl = record.HLYYIP
							+ "/remindAutoController.do?action=setPatient&jzlx=2&patientid="
							+ record.BRID + "&jzhm="
							+ record.MZHM + "&jzxh="
							+ record.JZXH + "&brxm="
							+ record.BRXM + "&brnl="
							+ record.NL + "&brxb="
							+ record.BRXB
							+ "&brtz=" + TZXX
							+ "&brsg=&jzks=" + record.KSDM
							+ "||" + record.KSMC + "&jzys="
							+ this.mainApp.uid + "||" + this.mainApp.uname
							+ "&rzbz=" + rzbz_r + "&plbz=" + plbz_r + "&gmyw="
							+ this.gmyw + "&brzs=" + ZSXX + "&brzd=" + brzdStr
							+ "&jzrq=" + nowtime + "&ysjb=1&uid="
							+ this.mainApp.uid + "&pwd=" + record.password;
					var userRes = phis.script.rmi.miniJsonRequestSync({
								serviceId : "publicService",
								serviceAction : "getRefForUrl",
								body : encodeURI(userUrl)
							});
					if (userRes.code > 300) {
						this.processReturnMsg(userRes.code, userRes.msg);
						return false;
					}
					userRes = userRes.json.body
					if (userRes.code == "success") {
//						var store = this.grid.getStore();
//						var n = store.getCount()
						var drugUrl = record.HLYYIP
								+ "/remindAutoController.do?action=checkRule&jzlx=2&jzxh="
								+ record.JZXH + "&czgh="
								+ this.mainApp.uid + "&czmc="
								+ this.mainApp.uname
								+ "&czjb=1&ruleType=1&recipe=";
						var cfmxs=record.cfmx;
						n=cfmxs.length;
						for (var i = 0; i < n; i++) {
							var r = cfmxs[i];
							if (i != 0) {
								drugUrl += ","
							}
							drugUrl += "<recipe><zymz>2</zymz><xmlx>1</xmlx><yzxh>"
									+ (i + 1)
									+ "</yzxh><xmdm>"
									+ r.YPXH
									+ "</xmdm><xmmc>"
									+ r.YPMC
									+ "</xmmc><xmsx>"
									+ r.YPCD
									+ "</xmsx><xmjl>"
									+ r.YCJL
									+ "</xmjl><xmdw>"
									+ r.JLDW
									+ "</xmdw><sypc>"
									+ r.MRCS
									+ "/1|"
									+ r.YPYF
									+ "</sypc><yytj>"
									+ r.GYTJ
									+ "||"
									+ r.XMMC
									+ "</yytj><kzsj>"
									+ nowtime
									+ "</kzsj><tzsj></tzsj><yzzh>";
							if ((r.YPZH + "") == 'undefined') {
								drugUrl += i;
							} else {
								drugUrl += r.YPZH;
							}
							drugUrl += "</yzzh><yzqx>1</yzqx><kzys>"
									+ this.mainApp.uid + "||"
									+ this.mainApp.uname + "</kzys></recipe>"
						}
						drugUrl += "&uid=" + this.mainApp.uid + "&pwd="
								+ record.password;
						var drugRes = phis.script.rmi.miniJsonRequestSync({
									serviceId : "publicService",
									serviceAction : "getRefForUrl",
									body : encodeURI(drugUrl)
								});
						if (drugRes.code > 300) {
							this.processReturnMsg(drugRes.code, drugRes.msg);
							return false;
						}
						drugRes = drugRes.json.body
						if (!drugRes) {
							return false;
						}
						if(drugRes.code =="noRule"){
						var ret1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "prescriptionCommentsService",
							serviceAction : "saveCfdpWt",
							body : {
								"DPXH" : record.DPXH,
								"TAG" : 1
							}
						});
						if (ret1.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return false;
							}
						}//如果有问题
						else if (drugRes.code == "rule" || drugRes.code == "warning") {
							//MyMessageTip.msg('1',drugRes.result,true)
							var openUrl = record.HLYYIP
									+ "/remindAutoController.do?action=getRuleData&key="
									+ drugRes.result + "&fields=gzlb,gzmc";
							var drugRes12 = phis.script.rmi.miniJsonRequestSync({
									serviceId : "publicService",
									serviceAction : "getRefForUrl",
									body : encodeURI(openUrl)
								});
							if (drugRes12.code > 300) {
								this.processReturnMsg(drugRes12.code, drugRes12.msg);
								return false;
							}
							var bhllb=drugRes12.json.body.result;
							//MyMessageTip.msg("提示", Ext.encode(drugRes12.json.body), true);
							var wtdm="";
							var bhll=bhllb.split("|")[0]
							//MyMessageTip.msg("提示", bhll, true);
							if(bhll==12){
								wtdm="2-3";
							}else if(bhll==8||bhll==17){
								wtdm="2-5";
							}else if(bhll==6){
								wtdm="2-8";
							}else if(bhll==5){
								wtdm="2-1";
							}
							var ret1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "prescriptionCommentsService",
							serviceAction : "saveCfdpWt",
							body : {
								"DPXH" : record.DPXH,
								"WTDMS" : wtdm,
								"TAG" : 2
							}
							});
						if (ret1.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return false;
							}
						}
					}else{
					MyMessageTip.msg("提示", Ext.encode(userRes), true);
					}
				this.doRefresh();
				return true;
			}
		});
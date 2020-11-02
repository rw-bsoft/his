$package("phis.application.hos.script")

$import("phis.script.SimpleList","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")

phis.application.hos.script.HospitalPaymentProcessingList = function(cfg) {
	cfg.autoLoadData = false;
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.hos.script.HospitalPaymentProcessingList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalPaymentProcessingList,
		phis.script.SimpleList, {
			doCanceled : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("ZFPB")) {
					MyMessageTip.msg("提示", "缴款记录已经作废!", true);
					return
				}
				var this_ = this;
				var msg = '<br/><br/>病人姓名：' + r.get("BRXM") + '<br/><br/>缴款日期：'
						+ r.get("JKRQ");
				var navigatorName = "Microsoft Internet Explorer";
				if (navigator.appName == navigatorName) {
					msg += ' <br/>缴款金额：'
				} else {
					msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/><br/>缴款金额：'
				}
				msg += r.get("JKJE") + '<br/><br/>缴款方式：' + r.get("JKFS_text")
						+ '<br/><br/>收据号码：' + r.get("SJHM") + '<br/><br/>票卡号码：'
						+ r.get("ZPHM") + '<br/><br/>'
				Ext.MessageBox.confirm('确认注销下列缴款记录吗?', msg,
						function(btn, text) {
							if (btn == "yes") {
								this_.processRemove(r);
							}
						});
			},
			processRemove : function(r) {
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.grid.el.mask("正在注销...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPaymentProcessingService",
							serviceAction : "updatePayment",
							pkey : r.id
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code < 300) {
								/*****begin 住院预交金退费（扫码支付） zhaojian 2019-11-13 *****/
								//如果本地失败，调用支付平台取消微信支付宝付款
								if(json.zy_jkxx.JKFS==52 || json.zy_jkxx.JKFS==53){
									var querydata ={};
								 	querydata.APIURL = getPayApiUrl("orderQuery");
									this.initHtmlElement();//获取客户端IP前先初始化插件
								 	querydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
								 	querydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
									//querydata.IP = "192.168.9.71";//测试时使用
					 				//querydata.COMPUTERNAME = "DPSF01";//测试时使用
								 	querydata.PAYSERVICE = "3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费 -3住院预交金退费'
								 	querydata.PAYSERVICE_REFUND = "-3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费 -3住院预交金退费'
									querydata.PATIENTID = json.zy_jkxx.BRID+"";
									querydata.ORGANIZATIONCODE = json.zy_jkxx.JGID;
									//querydata.ORGANIZATIONCODE = "320124003";//测试时使用
									querydata.VOUCHERNO = json.zy_jkxx.SJHM;
									//获取需退款订单信息
									var getorders = phis.script.rmi.miniJsonRequestSync({
										serviceId : "phis.mobilePaymentService",
										serviceAction : "queryNeedRefundOrder",
										body:querydata
										});
									if(getorders.code <=300){
										if(getorders.json && getorders.json.body && getorders.json.body.orders){
											if (getorders.json.body.orders.length == 0) {
												MyMessageTip.msg("提示", "未找到可退款的扫码支付订单信息!", true);
												return;
											}
											for (var i = 0; i < getorders.json.body.orders.length; i++) {
												//循环执行退款操作
												var refunddata ={};
												refunddata.ID_REFUND = getorders.json.body.orders[i].ID_REFUND;//退号单id
												refunddata.PAYSERVICE = "-3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费 -3住院预交金退费'
												refunddata.IP = querydata.IP;//支付终端IP
								 				refunddata.COMPUTERNAME = querydata.COMPUTERNAME;//支付终端电脑名称
												//refunddata.IP = "192.168.9.71";//测试时使用
								 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
												if(getorders.json.body.orders[i].HOSPNO_REFUND && getorders.json.body.orders[i].HOSPNO_REFUND!=""){
													refunddata.HOSPNO = getorders.json.body.orders[i].HOSPNO_REFUND;//医院流水号
												}
												else{
													refunddata.HOSPNO = generateHospno("ZYYJKTF",getorders.json.body.orders[i].VOUCHERNO);//医院流水号
												}
												refunddata.PAYMONEY = getorders.json.body.orders[i].PAYMONEY+"";//支付金额
												refunddata.VOUCHERNO = getorders.json.body.orders[i].VOUCHERNO;//就诊号码或发票号码
												refunddata.ORGANIZATIONCODE = getorders.json.body.orders[i].ORGANIZATIONCODE;//机构编码
												//refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
												refunddata.PATIENTYPE = getorders.json.body.orders[i].PATIENTYPE;//病人性质
												refunddata.PATIENTID = getorders.json.body.orders[i].PATIENTID;//病人id
												refunddata.NAME = getorders.json.body.orders[i].NAME;//病人姓名
												refunddata.SEX = getorders.json.body.orders[i].SEX;//性别
												refunddata.IDCARD = getorders.json.body.orders[i].IDCARD;//身份证号
												refunddata.BIRTHDAY = getorders.json.body.orders[i].BIRTHDAY;//出生年月
												refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
												//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
												refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
												refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名
												//refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
												//refunddata.COLLECTFEESNAME = "管理员";//测试时使用	
												refunddata.HOSPNO_ORG = getorders.json.body.orders[i].HOSPNO;//退款交易时指向原HOSPNO
												refund(refunddata,this);
												return;
											}
										}else{
											MyMessageTip.msg("提示", "获取可退款的扫码支付订单信息失败！", true);
											this.running = false;
											return;	
										}
									}else{
										MyMessageTip.msg("提示", "获取可退款的扫码支付订单信息失败！", true);
										this.running = false;
										return;
									}
								}
								/*****end 住院预交金退费（扫码支付） zhaojian 2019-11-13 *****/							
								this.doCommit3();
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
			doCommit3 : function(){
				this.store.remove(this.getSelectedRecord())
				this.fireEvent("remove", this.entryName,
						'remove', json, this.getSelectedRecord().data)
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				var queryBtn = new Ext.Toolbar.Button({
							text : '',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				if (!this.cnd) {
					return;
				} // yzh ,
				// 2010-06-09 **
				
				//去掉第二个过滤条件zyh=0
				if(this.cnd[1][1][1]=='a.ZYH'){
					this.cnd.splice(1,1); 
				}
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							var rcnd = ['and', initCnd, this.navCnd];
							if (rcnd) {
								this.requestData.cnd = ['and', rcnd, this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						} else {
							if (this.navCnd) {
								this.requestData.cnd = ['and', this.navCnd,
										this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							var rcnd = ['and', initCnd, this.navCnd];
							if (rcnd) {
								this.requestData.cnd = ['and', rcnd, this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						} else {
							if (this.navCnd) {
								this.requestData.cnd = ['and', this.navCnd,
										this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					var rcnd = cnd.length == 0 ? null : cnd;
					if (rcnd) {
						this.requestData.cnd = ['and', rcnd, this.cnd];
					} else {
						this.requestData.cnd = this.cnd;
					}
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					var rcnd = ['and', cnd, this.navCnd];
					if (rcnd) {
						this.requestData.cnd = ['and', rcnd, this.cnd];
					} else {
						this.requestData.cnd = this.cnd;
					}
					this.refresh()
					return
				}
				if (cnd) {
					this.requestData.cnd = ['and', cnd, this.cnd];
				} else {
					this.requestData.cnd = this.cnd;
				}
				this.refresh()
			},
			onStoreLoadData:function(store,records,ops){
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				}
				else{
					this.selectRow(this.selectedIndex);
				}
				if(this.opener.JKXH){
					var _this = this;
					var deferFunction = function(){
						_this.opener.form.doPrint(_this.opener.JKXH);
					}
					deferFunction.defer(1000);
				}
			},
			doPrint : function() {
//				var module = this.createModule("paymentprint",
//						this.refPaymentList)
				var r = this.getSelectedRecord()
				if (r) {
					var msg = '<br/><br/>病人姓名：' + r.get("BRXM")
							+ '<br/><br/>缴款日期：' + r.get("JKRQ");
					var navigatorName = "Microsoft Internet Explorer";
					if (navigator.appName == navigatorName) {
						msg += ' <br/>缴款金额：'
					} else {
						msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/><br/>缴款金额：'
					}
					msg += +r.get("JKJE") + '<br/><br/>缴款方式：'
							+ r.get("JKFS_text") + '<br/><br/>收据号码：'
							+ r.get("SJHM") + '<br/><br/>票卡号码：' + r.get("ZPHM")
							+ '<br/><br/>';
					Ext.MessageBox
							.confirm(
									'确认重打下列缴款收据吗?',
									msg,
									function(btn, text) {
										if (btn == "yes") {
											this.dorPrint(r.get("JKXH"));
//											module.jkxh = r.get("JKXH");
//											module.initPanel();
//											module.doPrint();
										}
									},this)

				} else {
					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
				}
			},
			dorPrint : function(jkxh) {
				var LODOP=getLodop();  
				LODOP.PRINT_INITA(10,12,390,300,"住院缴款打印");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalAdmissionService",
					serviceAction : "printMoth",
					jkxh : jkxh
					});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(17,114,160,25,ret.json.title);
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(47,182,30,25,"NO.");
				LODOP.ADD_PRINT_TEXT(47,212,140,25,ret.json.PJHM);
				LODOP.ADD_PRINT_TEXT(72,34,80,25,"住院号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,114,80,25,ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT(72,194,80,25,"床号");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,274,80,25,ret.json.BRCH);
				LODOP.ADD_PRINT_TEXT(97,34,80,25,"姓名");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,114,80,25,ret.json.BRXM);
				LODOP.ADD_PRINT_TEXT(97,194,80,25,"缴款方式");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,274,80,25,ret.json.JKFS);
				LODOP.ADD_PRINT_TEXT(122,34,80,25,"科室");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,114,80,25,ret.json.KSMC);
				LODOP.ADD_PRINT_TEXT(122,194,80,25,"病区");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,274,80,25,ret.json.BQMC);
				LODOP.ADD_PRINT_TEXT(147,34,80,25,"预缴款金额");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(147,114,240,25,ret.json.JKJE);
				LODOP.ADD_PRINT_TEXT(172,34,80,25,"人民币大写");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(172,114,240,25,ret.json.DXJE);
				LODOP.ADD_PRINT_TEXT(197,34,80,25,"缴款合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,114,80,25,ret.json.JKHJ);
				LODOP.ADD_PRINT_TEXT(197,194,80,25,"票(卡)号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,274,80,25,ret.json.PKHM);
				LODOP.ADD_PRINT_TEXT(222,34,80,25,"自负合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,114,80,25,ret.json.ZFHJ);
				LODOP.ADD_PRINT_TEXT(222,194,80,25,"剩余合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,274,80,25,ret.json.SYHJ);
				LODOP.ADD_PRINT_TEXT(250,47,60,25,"收款员");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,107,80,25,ret.json.JSR);
				LODOP.ADD_PRINT_TEXT(250,191,60,25,"缴款日期");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,251,86,25,ret.json.JKRQ);
				if((ret.json.FPYL+"")=='1'){
					LODOP.PREVIEW();
				}else{
					LODOP.PRINT();
				}
			}
		});
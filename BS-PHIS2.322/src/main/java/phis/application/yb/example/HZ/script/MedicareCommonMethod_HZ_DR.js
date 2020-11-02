$package("phis.application.yb.example.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync", "phis.script.widgets.ymPrompt",
		"phis.script.widgets.PrintWin");
phis.application.yb.example.script.MedicareCommonMethod = {
	// 医保接口方法,挂号退号部分开始
	/**
	 * 获取医保病人性质,医保性质在系统参数里面
	 * 
	 */
	getYbbrxz : function() {
		var ret = phis.script.rmi.miniJsonRequestSync({ // modify by yangl 调用phis的ajax请求,为当前文件整体替换
					serviceId : "medicareService",
					serviceAction : "queryYbbrxz"
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
		}
		return ret.json.body;
					},
	/**
	 * 医保挂号预结算
	 */
	doYbghyjs : function() {
		if (this.ybkxx == null) {
			return;
		}
		var body = {};
		body["ghxx"] = this.GHXX;// 当前挂号信息
		body["ybkxx"] = this.ybkxx;// 医保读卡信息.
		// 其他需要查询医保挂号登记,上传,预结算,结算需要的参数另行添加
		var yllb = 11;// 医疗类别
		var jslb = 1;// 结算类别,
		if (this.form.getForm().findField("GDBZ").getValue()) {
			yllb = 31;
			jslb = 3;
		}
		body["YLLB"] = yllb;
		body["JSLB"] = jslb;
		body["TYPE"] = 1;
		body["spData"] = this.spData;
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "queryYbGhjscs",
					body : body
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return;
		}
		var ybghcs = ret.json.body;
		// 以下代码 进行医保的挂号接口调用
		data = ybghcs.YJS;
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		res = o.invokeMCIF(data.TransType, data.ProcessingCode, data.HosCode,
				data.OPTRCode, data.BusiCycle, data.CenterCode,
				data.SenderSerialNo, data.InputStr, inInputStrAcc,
				data.ItemNumberIndicator);
		res = res.replace(/\"/g, "");
		json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
		if (json.transType != parseInt(data.TransType) + 10
				|| json.resultCode != 0 || json.responseCode != "00") {
			MyMessageTip.msg("提示", "医保预结算出错,错误信息:" + json.outputStr, true);
			return;
		}
		var reg = "YLFZE|GRZFJE|YLYPZL|TJZL|TZZL|QFZFJE|QFZHZF|QFXJZF|FDZL|YSZHZF|YSXJZF|FDZF1|FDZF2|FDZF3|FDZF4|FDZF5|CDGRZF|BNZHZF|LNZHZF|TCZF|GRXJZF|JZJZF|GWJJZF|ZLCWF|BCQF|QFLNZF|FDMZXJ|DWLX|RYLB|RYXZ|KLZJE|LXJJ|TXTCJJ|ZNTCJJ|LFJJ|LXJSJJ|QZHYE|QBNZHYE|QLNZHYE|HZHYE|HBNZHYE|HLNZHYE|KNJZJJ|ZZTCJJ|LNJMJJ|SNETJJ|NMGJJ|NMGLJJF|JTBC|GFKZJJ|GFJFJJ|LFKZJJ|LFJFJJ|FSZJJ|FSJJJ|FTJJJ|FJJJJ|FCJJJ|CQZNJJ|CQGFJJ|CQLFJJ|CQH|XNHJJ|TJJJ|DXSJJ|EJBJZF|LMJJ|LNZHZL|SCZF|SCZL|YYCD|DBBZ|CJDD|";
		var gh_yjxss = this.outputStrToMap(json.outputStr, reg);
		// gh_jxss["ZXLSH"]=json.ReceiverSerialNo;//中心流水号
		// gh_jxss["YYLSH"]=ret.json.body.JYLSH;//医院流水号
		// gh_jxss["JYXH"]=ret.json.body.MZH;//门诊号
		// gh_jxss["YLLB"]=yllb;//医疗类别
		var body = {};
		body["YJSXX"] = gh_yjxss;
		body["CSXX"] = ret.json.body;
		body["YLLB"] = yllb;
		body["SRKH"] = this.ybxx.SRKH;
		body["SMKLSH"] = this.ybxx.SMKLSH;
		body["SMKRZM"] = this.ybxx.SMKRZM;
		if (this.spData && this.spData != null) {
			body["SPDATA"] = this.spData;
		}
		var ybData = body;// 将需要传到挂号界面的数据放到这个集合,包括医保卡信息,预结算返回的金额,医保结算的参数
		var module = this.midiModules["rjsModule"];
		if (!module) {
			module = this.createModule("rjsModule", this.refJsForm);
			this.midiModules["rjsModule"] = module;
			module.opener = this;
			module.on("settlement", this.doNew, this);

		}
		this.GHXX.BLJE = this.form.getForm().findField('BLJE').getValue();
		var win = module.getWin();
		module.setData(this.GHXX, ybData);
		win.show();
	},
	// 医保接口方法 挂号部分结束
	// *******************************************
	// 医保接口方法,挂号退号部分开始
	doybghth : function() {
	var module = this.createModule("ybxx", "YB0106");
				module.on("qr", this.onQr_Ghth, this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.doNew();
				win.show();
	},
	onQr_Ghth : function(data) {
		if(this.data.BRXM!=data.XM){
					Ext.Msg.alert("提示","医保卡信息和挂号病人信息不符!");
					return;
					}
					this.saving = true
					this.form.el.mask("正在退号...", "x-mask-loading")
					var body = {};
					body.SBXH = this.SBXH;
					body.YBXX = data;
					var inInputStrAcc = data.SRKH + "|000000|"
							+ data.SMKLSH + "|" + data.SMKRZM + "|";
					var ret = this.simpleDyyb("ghth", body, "", 1,
							inInputStrAcc);
					if (ret.code != 1) {
						MyMessageTip.msg("提示", ret.outputStr, true);
						this.form.el.unmask();
						return;
					}
					body.YWZQH = ret.BusiCycle;
					body.ZFLSH=ret.ReceiverSerialNo;
					util.rmi.jsonRequest({
								serviceId : "registeredManagementService",
								serviceAction : "saveRetireRegistered",
								body : body
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false
								if (code > 300) {
									this.processReturnMsg(code, msg);
									return
								}
								this.fireEvent("retire", this);
								this.doConcel();
							}, this)
				//this.ybxx = data;
				//this.doQuery(null, null, data);
		},
		// 医保接口方法,挂号退号部分结束
		//*******************************************************
		//医保接口方法,门诊收费部分开始
		onQr_sf : function(data) {
				this.ybxx = data;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryOutpatientAssociation",
							GRBH : this.ybxx.GRBH
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				if (ret.json.MZGL == 0) {
					this.doQx();
					return;
				}
				var f = this.formModule.form.getForm().findField("MZGL");
				f.setValue(ret.json.MZGL);
				this.formModule.doENTER(f)
		},
		doYbmzyjs : function() {
		if (!this.querySFQD(1)) {// 判断是否已经签到
					return;
				}
				var store = this.listModule.grid.getStore();
				var body = {};
				var n = store.getCount();
				var data = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
				}
				// body["CF01"]=this.formModule.getFormData();
				body["CFMX"] = data;
				// body["MZXX"] = this.MZXX;
				body["GHGL"] = this.MZXX.GHGL;
				body["TYPE"] = 1;
				body["YBXX"] = this.ybxx;
				body["DJH"] = this.formModule.form.getForm().findField("FPHM")
						.getValue();
				body["ZD1"] = this.formModule.ZD1;
				body["ZD2"] = this.formModule.ZD2;
				body["ZD3"] = this.formModule.ZD3;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "saveOutpatientPreSettlement",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				$PhisActiveXObjectUtils.initHtmlElement();
				var o = $PhisActiveXObjectUtils.getObject();
				var inInputStrAcc = this.ybxx.SRKH + "|000000|"
						+ this.ybxx.SMKLSH + "|" + this.ybxx.SMKRZM + "|";
				var data = ret.json.body.DJ;
				var res = o.invokeMCIF(data.TransType, data.ProcessingCode,
						data.HosCode, data.OPTRCode, data.BusiCycle,
						data.CenterCode, data.SenderSerialNo, data.InputStr,
						inInputStrAcc, data.ItemNumberIndicator);
				res = res.replace(/\"/g, "'");
				var json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
				if (json.transType == parseInt(data.TransType) + 10
						&& json.resultCode == 0 && json.responseCode == "00") {// 成功
					data = ret.json.body.SC;
					var count = data.length;
					for (var i = 0; i < count; i++) {
						var d = data[i];
						res = o.invokeMCIF(d.TransType, d.ProcessingCode,
								d.HosCode, d.OPTRCode, d.BusiCycle,
								d.CenterCode, d.SenderSerialNo, d.InputStr,
								inInputStrAcc, d.ItemNumberIndicator);
						res = res.replace(/\"/g, "'");
						json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
						if (json.resultCode != 0) {
							MyMessageTip.msg("提示", "医保上传出错" + json.outputStr,
									true);
							return null;
						}
					}
					data = ret.json.body.YJS;
					res = o.invokeMCIF(data.TransType, data.ProcessingCode,
							data.HosCode, data.OPTRCode, data.BusiCycle,
							data.CenterCode, data.SenderSerialNo,
							data.InputStr, inInputStrAcc,
							data.ItemNumberIndicator);
					res = res.replace(/\"/g, "'");
					json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
					if (json.transType != parseInt(data.TransType) + 10
							|| json.resultCode != 0
							|| json.responseCode != "00") {
						MyMessageTip.msg("提示", "医保结算出错" + json.outputStr, true);
						return null;
					}
					var reg = "医疗费总额|个人自费金额|乙类药品自理|特检自理|特治自理|起付自负金额|起付标准帐户支付金额|起付标准现金支付金额|分段自理|起付以上帐户支付金额|起付以上现金支付金额|分段1自付|分段2自付|分段3自付|分段4自付|分段5自付|超过封顶线个人自付金额|本年帐户支出金额|历年帐户支出金额|统筹支付金额|个人现金支付金额|救助金支付金额|公务员补助支付金额|自理床位费|公务员本次进入门诊起伏线|公务员门诊起伏线历年账户支付|公务员门诊分段自负金额|单位性质|人员类别|人员性质|符合基本医疗保险费用|离休人员基金|退休门诊统筹基金|子女统筹基金|两费基金|离休家属基金|结算前帐户余额|结算前本年帐户余额|结算前历年帐户余额|结算后帐户余额|结算后本年帐户余额|结算后历年帐户余额|困难救助基金|企业在职门诊统筹基金|老居民基金|少年儿童基金|农民工基金支付|农民工累计缴费月数|家庭病床建床天数(符合医保范围内)|公费抗战配偶基金|公费解放配偶基金|两费抗战配偶基金|两费解放配偶基金|副省级基金|副市级基金|副厅级基金|副局级基金|副处以下基金|城区子女基金|城区公费离休基金|城区两费离休基金|城区号(六城区离休和子女)|新农合基金|体检基金|大学生基金|职工医保二级保健干部自负金额|劳模基金|历年账户支付自理|伤残基金支付自负现金|伤残基金支付自理现金|单病种医院承担费用|单病种标志|城乡居民定点标志|";
					var gh_yjxss = this.outputStrToMap(json.outputStr, reg);
					var jsret = {};
					jsret["JSCS"] = ret.json.body.JS;
					jsret["YJSXX"] = gh_yjxss;
					jsret["YBKXX"] = this.ybxx;
					jsret["YLLB"] = ret.json.body.YLLB;
					jsret["JYXH"] = ret.json.body.MZH;
					jsret["YYLSH"] = ret.json.body.YYLSH;
					jsret["YBMZMX"] = ret.json.body.YBMZMX;
					var jsData = {};
					var YJSXX = jsret.YJSXX;
					jsData["ZJJE"] = YJSXX.医疗费总额;
					jsData["ZFJE"] = YJSXX.个人现金支付金额;// YJSXX.本年帐户支出金额+YJSXX.历年帐户支出金额+YJSXX.个人现金支付金额;
					jsData["JJZF"] = (parseFloat(YJSXX.医疗费总额)
							- parseFloat(YJSXX.个人现金支付金额)
							- parseFloat(YJSXX.本年帐户支出金额) - parseFloat(YJSXX.历年帐户支出金额))
							.toFixed(2);
					jsData["ZHZF"] = (parseFloat(YJSXX.本年帐户支出金额) + parseFloat(YJSXX.历年帐户支出金额))
							.toFixed(2);
					jsData["JSCS"] = jsret.JSCS;
					jsData["YBKXX"] = jsret.YBKXX;
					jsData["YLLB"] = jsret.YLLB;
					jsData["JYXH"] = jsret.JYXH;
					jsData["YYLSH"] = jsret.YYLSH;
					jsData["YBMZMX"] = jsret.YBMZMX;
					var module = this.midiModules["jsModule"];
					if (!module) {
						module = this.createModule("jsModule", "IVC010103");
						this.midiModules["jsModule"] = module;
						var sm = module.initPanel();
						module.opener = this
						module.on("settlement", this.doQx, this);
					}
					var win = module.getWin();
					module.setData(data, this.MZXX, jsData);
					win.show();
				} else {
					MyMessageTip.msg("提示", "医保登记出错" + json.outputStr, true);
				}
		},
		doYbMzjs:function(){
		var reg = "YLJGBH|GRBH|JZLSH|DJH|JYLX|YYJYLSH|ZXJYLSH|YLFZE|BNZHZF|LNZHZF|LNZHZFZFBF|YBJJZF|BCXJZF|BCXJZFZFBF|ZLJE|ZFJE|CXJZF|BNZHYE|" +
									  "LNZHYE|ZYCS|BNGRXJZFLJ|BNDZHZFLJ|BNJRTCLJ|BNMZQFBZZFLJ|JSRQ|YWZQH|JSLSH|ZBJJZC|KNJZJJZC|SCJJZCZL|SCJJZCZF|LMJJZC|BJJJZC|TCJJZC|" +
									  "QFBZZF|TSXX|XJZFZL|XJZFZF|LNZHZFZL|LNZHZFZF|ZFZFJE|BJJJZFZF|BFZZFY|BFZZLZF|BNDZFLJS|JGQLGRJJZC|YYCDFY|QTJJZC";
				var ret = this.simpleHZYB("queryMzjs",this.jsData,reg,1);//门诊结算
				if(ret.code == 0){//门诊结算成功
					var mz_jsxx = ret.outputStr;
					var jsData = {};
					jsData["JSXX"] = mz_jsxx;
					jsData["YBXX"] = this.jsData.YBXX;
					jsData["CKLX"] = this.jsData.YBXX.CKLX;//标志为新市医保
					//alert("结算返回："+Ext.encode(jsData));
					body.jsData = jsData;
					util.rmi.jsonRequest({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "saveOutpatientSettlement",
								body : body
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false;
								this.runing = false;
								if (code > 300) {
									this.processReturnMsg(code, msg,this.saveToServer, [body]);
									// 医保结算成功,本地失败后 取消医保结算
									var cz = {};
								 	cz["CZYWBH"] = "2410";//冲正业务交易编码
								 	cz["CZYYJYLSH"] = ret.YYJYLSH;//被冲正交易医院交易流水号
								 	cz["CZYY"] = "1";//冲正原因
									ret = this.simpleHZYB("queryMzqxjscs",cz,"",1);
									if (ret.code == 0) {
										MyMessageTip.msg("提示", "医保门诊结算成功,本地保存失败,冲正成功!", true);
									}else{
										MyMessageTip.msg("提示", "医保门诊结算成功,本地保存失败,冲正失败:"+ ret.outputStr+", 请联系管理员!", false);
										return;
									}
									this.runing = false;
									return
								}
								this.opener.opener.JSXX = this.MZXX;
								this.fireEvent("settlement", this);
								this.doConcel();
								if(this.jsData.YBXX.BXLR){//如果是报销录入，改变打印纸张
									var balb = this.jsData.YBXX.BXLR.BALB
									this.opener.doPrintFp(this.MZXX.FPHM,balb);
								} else {
									this.opener.doPrintFp(this.MZXX.FPHM);
								}
						}, this)
				}
		},
	// 医保接口方法,门诊结算部分结束
	//*******************************************************
	//医保接口方法,门诊发票作废部分开始	
	onQr_fpzf : function(data) {
	//调用市民卡撤销扣款
			var smk_cxxx = this.callSMKFPCanel(this.initDataId);
			var cxxx = null;
			if(smk_cxxx != "1"){
				if(smk_cxxx == "0"){
					return;
				}
				var outputStr = "CWM|YDM|SHBH|ZDBH|ZHLX|JYLX|WLKH|JYRQ|JYSJ|JYCKH|PCH|POSLSH|JYJE|KMHAM|SMKKH|ZHYE|XPYEDYXE";
				cxxx = this.outputStrToObj(outputStr, smk_cxxx);
			}
			
			//调用市民卡撤销扣款结束
			var inInputStrAcc=data.SRKH+"|000000|"+data.SMKLSH+"|"+data.SMKRZM+"|";
			var body={};
			body["FPHM"]=this.initDataId;
			body["YBXX"]=data;
			var ret=this.simpleDyyb("qxjs",body,"",1,inInputStrAcc);
			if(ret.code==1){//成功
			var ybxx={};
			ybxx["ZFYWZQH"]=ret.CS.BusiCycle;
			ybxx["ZFLSH"]=ret.ReceiverSerialNo;
			ybxx["MZXH"]=ret.CS.MZXH;
			var b={};
			b["YBXX"]=ybxx;
			b["FPHM"]=this.initDataId;
			b["cxxx"] = cxxx;
			
			var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "updateVoidInvoice",
								body : b
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if (r.json.msg) {
							Ext.Msg.alert("提示", r.json.msg);
							return;
						}
						this.opener.loadData();
						this.doCancel();
					}
			}else{//失败
			MyMessageTip.msg("提示", ret.outputStr, true);
			return;
			}
	},
	// 医保接口方法,门诊发票作废部分结束
	//*******************************************************
	//医保接口方法,入院登记部分开始			
	// 读卡返回
		onQr_rydj : function(data) {
			
		},
			// 医保入院登记
			doYbZydj : function(zyh) {
				var brxz = this.form.getForm().findField("BRXZ").getValue();
				var body = {}
					this.saveData.ZBBM = this.RYZD;
					body['DATA'] = this.saveData;
					body['YBXX'] = this.ybxx;
					
//					this.form.getForm().findField('HKDZ_QTDZ').setValue(Ext.encode(body));
//					this.form.el.unmask()
//					return;
					var reg = "YLLB";
					var  ret = this.simpleHZYB("rydj",body,reg,1);//入院登记
					if(ret.code == 0){//成功
						this.saveData.ZYLSH = ret.JZLSH;
						this.saveData.SRKH = this.ybxx.SRKH;
						this.saveData.GRBH = this.ybxx.GRBH;
						this.saveData.YYLSH = ret.YYJYLSH;
						this.saveData.YLLB = ret.outputStr.YLLB;//医疗类别(出院结算以此为准)
//						this.saveData.ZXJYLSH = ret.ZXJYLSH;
//						alert(Ext.encode(this.saveData))
						return true;
					} else {
						MyMessageTip.msg("提示", ret.outputStr, true);
						return false;
					}
			},
			doYbqxrydj : function() {
				var cz = {};
				cz["CZYWBH"] = "2210";// 冲正业务交易编码
				cz["CZYYJYLSH"] = this.saveData.YYLSH;// 被冲正交易医院交易流水号
				cz["CZYY"] = "1";// 冲正原因
				ret = this.simpleHZYB("czjy", cz, "", 1);
				if (ret.code == 0) {
					MyMessageTip.msg("提示", "医保入院出错,冲正成功!", true);
				} else {
					MyMessageTip.msg("提示",
							"医保入院出错,冲正失败:" + ret.outputStr + ", 请联系管理员!", false);
				}
			}
	// 医保接口方法,入院登记部分结束
	//*******************************************************
	//医保接口方法,取消入院登记和转换部分开始	
			,
			//读卡返回 自动调用病人结算信息
			onQr_qxrydj : function(data) {
				//以下代码用于判断读出来的卡与当前病人是否是同一个(仅供参考),可以根据需要判断
//			var sfzh =this.form.getForm().findField("SFZH").getValue();
//			if(sfzh!=data.GRSFH){
//				MyMessageTip.msg("提示", "读卡信息与入院登记病人不是同一个人,请确认", true);
//				return;
//			}
			this.ybkxx = data;
			},
			//自费转医保,成功返回true,失败返回false
			doZfzyb:function(body){
			var brxxData = this.data;
						brxxData['ZBBM'] = this.JBBM;
						brxxData['RYZD'] = this.JBMC;
						var body={};
						body["YBXX"] = this.ybxx;
						body['DATA'] = brxxData;
						var reg = "YLLB";
						var ret = this.simpleHZYB("rydj",body,reg,1);//入院登记
						if(ret.code == 0){//成功
							var ybgx={};//医保更新
							ybgx["TAG"]="1";//标志  暂时定义为 1是自费转市医保,2是市医保转自费,省医保部分后续添加
							ybgx["ZYLSH"]=ret.JZLSH;
							ybgx["SRKH"]=this.ybxx.SRKH;
							ybgx["YYLSH"]=ret.YYJYLSH;
							ybgx["GRBH"]=this.ybxx.GRBH;
							ybgx['YLLB']=ret.outputStr.YLLB;//医疗类别(出院结算以此为准)
							updateCs["YBGX"]=ybgx;			
						} else {
							MyMessageTip.msg("提示", ret.outputStr, true);
							this.form.el.unmask();
							return false;
						}		
			},
			//医保转自费,成功返回true,失败返回false
			doYbzzf:function(body){
			if (!this.ybxx) {
							MyMessageTip.msg("提示", "医保病人,未读卡,请先读卡", true);
							this.form.el.unmask();
							return;
						}
						var inInputStrAcc=this.ybxx.SRKH+"|000000|"+this.ybxx.SMKLSH+"|"+this.ybxx.SMKRZM+"|";
						var body = {};
						body["ZYH"] = this.initDataId;
						body["ZYLSH"] = this.data.ZYLSH;
						body["YYLSH"] = this.data.YYLSH;
						body["ZH"] = 1;//表示 是转换的病人取消入院登记
						var ret = this.simpleDyyb("qxdj", body, "", 2,
								inInputStrAcc);
						if (ret.code != 1) {// 失败
							MyMessageTip.msg("提示", ret.outputStr, true);
							this.form.el.unmask();
							return false;
						}
						var ybgx={};//医保更新
						ybgx["TAG"]="2";//标志  暂时定义为 1是自费转市医保,2是市医保转自费,省医保部分后续添加
						updateCs["YBGX"]=ybgx;
						return true
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
			}// 医保接口方法,取消入院登记和转换部分结束
	//*******************************************************
	//医保接口方法,住院结算部分开始
		,
			onQr_zyjs : function(data) {
				this.ybxx = data;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryZyhmByYbxx",
							GRBH : this.ybxx.GRBH
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					this.doNew();
					return;
				}
				var zyhm = this.form.form.getForm().findField("ZYHM");
				zyhm.setValue(ret.json.ZYHM);
				var data = {};
				data.key = zyhm.getName();
				data.value = zyhm.getValue();
				this.doQuery(data);
			},
			// 预结算
			doYjs : function() {
				if (!this.ybxx) {
						MyMessageTip.msg("提示", "医保病人,未读卡,请先读卡", true);
					}
					if (!this.querySFQD(2)) {// 判断是否已经签到
					return;
				}
				var body = {};
				body["ZYH"] = this.data.ZYH;
				body["GRBH"] = this.ybxx.GRBH;
				body["BLLX"] = this.ybxx.BLLX;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryHospitalPreSettlement",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return ;
				}
				$PhisActiveXObjectUtils.initHtmlElement();
				var o = $PhisActiveXObjectUtils.getObject();
				var inInputStrAcc = this.ybxx.SRKH + "|000000|"
						+ this.ybxx.SMKLSH + "|" + this.ybxx.SMKRZM + "|";
				var data = ret.json.body.SC;
				var count = data.length;
				var updateFyxh = new Array();
				var success = true;
				for (var i = 0; i < count; i++) {
					var d = data[i];
					res = o.invokeMCIF(d.TransType, d.ProcessingCode,
							d.HosCode, d.OPTRCode, d.BusiCycle, d.CenterCode,
							d.SenderSerialNo, d.InputStr, inInputStrAcc,
							d.ItemNumberIndicator);
					res = res.replace(/\"/g, "");
					json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
					if (json.resultCode != 0) {
						MyMessageTip.msg("提示", "医保上传出错" + json.outputStr, true);
						success = false;
						continue;
					}
					if(json.resultCode==0){
					updateFyxh.push(d.JLXH)
					}
				}
				if (updateFyxh.length > 0) {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "saveUploadHospitalCosts",
								body : updateFyxh
							});
					if (ret.code > 300) {
						MyMessageTip.msg("提示", "医保上传成功,本地更新失败,失败的记录序号:"
										+ Ext.encode(updateFyxh)
										+ "请记下序号并联系管理员", true);
						return;
					}
				}
				if(updateFyxh.length!=count){
				MyMessageTip.msg("提示", "有费用未上传成功" , true);
				return;
				}
//				if (!success) {
//					return null;
//				}
				data = ret.json.body.YJS;
				res = o.invokeMCIF(data.TransType, data.ProcessingCode,
						data.HosCode, data.OPTRCode, data.BusiCycle,
						data.CenterCode, data.SenderSerialNo, data.InputStr,
						inInputStrAcc, data.ItemNumberIndicator);
				res = res.replace(/\"/g, "");
				json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
				if (json.transType != parseInt(data.TransType) + 10
						|| json.resultCode != 0 || json.responseCode != "00") {
					MyMessageTip.msg("提示", "医保结算出错" + json.outputStr, true);
					return null;
				}
				var reg = "医疗费总额|个人自费金额|乙类药品自理|特检自理|特治自理|起付自负金额|起付标准帐户支付金额|起付标准现金支付金额|分段自理|起付以上帐户支付金额|起付以上现金支付金额|分段1自付|分段2自付|分段3自付|分段4自付|分段5自付|超过封顶线个人自付金额|本年帐户支出金额|历年帐户支出金额|统筹支付金额|个人现金支付金额|救助金支付金额|公务员补助支付金额|自理床位费|公务员本次进入门诊起伏线|公务员门诊起伏线历年账户支付|公务员门诊分段自负金额|单位性质|人员类别|人员性质|符合基本医疗保险费用|离休人员基金|退休门诊统筹基金|子女统筹基金|两费基金|离休家属基金|结算前帐户余额|结算前本年帐户余额|结算前历年帐户余额|结算后帐户余额|结算后本年帐户余额|结算后历年帐户余额|困难救助基金|企业在职门诊统筹基金|老居民基金|少年儿童基金|农民工基金支付|农民工累计缴费月数|家庭病床建床天数(符合医保范围内)|公费抗战配偶基金|公费解放配偶基金|两费抗战配偶基金|两费解放配偶基金|副省级基金|副市级基金|副厅级基金|副局级基金|副处以下基金|城区子女基金|城区公费离休基金|城区两费离休基金|城区号(六城区离休和子女)|新农合基金|体检基金|大学生基金|职工医保二级保健干部自负金额|劳模基金|历年账户支付自理|伤残基金支付自负现金|伤残基金支付自理现金|单病种医院承担费用|单病种标志|城乡居民定点标志|";
				var gh_yjxss = this.outputStrToMap(json.outputStr, reg);
				var body = {};
				body["JSCS"] = ret.json.body.JS;
				body["YJSXX"] = gh_yjxss;
				body["YBKXX"] = this.ybxx;
						var jsret = body;
						// jsret={"医疗费总额":100,"个人现金支付金额":100,"统筹支付金额":100}
						if (jsret == null) {
							return;
						}
						var updzfbl = phis.script.rmi.miniJsonRequestSync({
									serviceId : "hospitalPatientSelectionService",
									serviceAction : "updateSYBZfbl",
									ZYH : this.data.ZYH
								});
						if (updzfbl.code > 300) {
							this.processReturnMsg(updzfbl.code, updzfbl.msg);
							return;
						}
						this.body.YBFYZE=parseFloat(jsret.YJSXX.医疗费总额);
						this.body.TCJE = Math
								.abs((parseFloat(jsret.YJSXX.医疗费总额)
										- parseFloat(jsret.YJSXX.本年帐户支出金额)
										- parseFloat(jsret.YJSXX.历年帐户支出金额) - parseFloat(jsret.YJSXX.个人现金支付金额))
										.toFixed(2));
						// this.body.ZFHJ = jsret.YJSXX.个人现金支付金额;
						this.body.ZHZF = (parseFloat(jsret.YJSXX.本年帐户支出金额) + parseFloat(jsret.YJSXX.历年帐户支出金额))
								.toFixed(2);
						//ZFHJ改为个人现金，去掉帐户金额
						this.body.ZFHJ = (parseFloat(jsret.YJSXX.个人现金支付金额)).toFixed(2);
						// alert(Ext.encode(this.body))
						this.body.YBJSCS = jsret.JSCS;
						this.body.YBKXX = jsret.YBKXX;
						this.body.JYXH = this.form.data.ZYLSH;
						this.body.YYLSH = this.form.data.YYLSH;
						this.body.SHIYB = 1;
						this.showZyjsModule();
			},
			//住院结算
			doZyjs:function(data){
			var data = this.data.YBJSCS;
						$PhisActiveXObjectUtils.initHtmlElement();
						var o = $PhisActiveXObjectUtils.getObject();
						var ybkxx = this.data.YBKXX;
						var inInputStrAcc = ybkxx.SRKH + "|000000|"
								+ ybkxx.SMKLSH + "|" + ybkxx.SMKRZM + "|";
						data.InputStr = data.InputStr.replace("|000000|", "|"
										+ this.data.FPHM + "|");
						var res = o.invokeMCIF(data.TransType,
								data.ProcessingCode, data.HosCode,
								data.OPTRCode, data.BusiCycle, data.CenterCode,
								data.SenderSerialNo, data.InputStr,
								inInputStrAcc, data.ItemNumberIndicator);
						res = res.replace(/\"/g, "");
						var j = eval("(" + res.replace(/[\r\n]/g, "") + ")");
						if (j.transType == parseInt(data.TransType) + 10
								&& j.resultCode == 0 && j.responseCode == "00") {// 成功
							var reg = "YLFZE|GRZFJE|YLYPZL|TJZL|TZZL|QFZFJE|QFZHZF|QFXJZF|FDZL|YSZHZF|YSXJZF|FDZF1|FDZF2|FDZF3|FDZF4|FDZF5|CDGRZF|BNZHZF|LNZHZF|TCZF|GRXJZF|JZJZF|GWJJZF|ZLCWF|BCQF|QFLNZF|FDMZXJ|DWLX|RYLB|RYXZ|KLZJE|LXJJ|TXTCJJ|ZNTCJJ|LFJJ|LXJSJJ|QZHYE|QBNZHYE|QLNZHYE|HZHYE|HBNZHYE|HLNZHYE|KNJZJJ|ZZTCJJ|LNJMJJ|SNETJJ|NMGJJ|NMGLJJF|JTBC|GFKZJJ|GFJFJJ|LFKZJJ|LFJFJJ|FSZJJ|FSJJJ|FTJJJ|FJJJJ|FCJJJ|CQZNJJ|CQGFJJ|CQLFJJ|CQH|XNHJJ|TJJJ|DXSJJ|EJBJZF|LMJJ|LNZHZL|SCZF|SCZL|YYCD|DBBZ|CJDD|";
							var zy_jxss = this.outputStrToMap(j.outputStr, reg);
							zy_jxss["ZXLSH"] = j.receiverSerialNo;// 中心流水号
							zy_jxss["YLLB"] = "21";// 医疗类别
							// zy_jxss["JYXH"] = this.data.JYXH;// 门诊号
							zy_jxss["YYDM"] = data.HosCode;// 医院代码
							zy_jxss["CARDNO"] = ybkxx.SRKH;// 卡号
							zy_jxss["YYLSH"] = this.data.YYLSH;// 交易流水号
							zy_jxss["YWZQH"] = data.BusiCycle;// 业务周期号
							return zy_jxss;
						} else {
							MyMessageTip.msg("提示", "医保结算失败:" + j.outputStr,
									true);
							this.form.el.unmask()
							this.saving = false;
							return false;
						}
			},
			doZyjscz:function(){
				var b = {};
				b["YBXX"] = ybkxx;
				b["YLLB"] = zy_jxss.YLLB;
				b["DJH"] = this.data.FPHM;
				b["MZH"] = zy_jxss.JYXH;
				b["ZXLSH"] = j.receiverSerialNo;
				b["YYLSH"] = zy_jxss.YYLSH;
				var ret = this.simpleDyyb("zycwqxjs", b, "", 1, inInputStrAcc);
				if (ret.code != 1) {
					MyMessageTip.msg("提示", "医保结算出错,冲正失败:" + ret.outputStr + ", 请联系管理员",
							true);
				}
				var commitList = [];
				commitList.push(this.data.ZYH);
				this.requestServer('updateMedicareRemoveUpFYMX', commitList);
			},
			// 医保接口方法,住院结算部分结束
			// *******************************************************
			// 医保接口方法,住院发票作废部分开始
			// 发票作废
			onZyZfQr : function(data) {
				var inInputStrAcc = data.SRKH + "|000000|" + data.SMKLSH + "|"
						+ data.SMKRZM + "|";
				this.body["YBXX"] = data;
				var ret = this.simpleDyyb("zyqxjs", this.body, "", 2,
						inInputStrAcc);
				if (ret.code == 1) {// 成功
					var ybxx = {};
					ybxx["ZFYWZQH"] = ret.CS.BusiCycle;
					ybxx["ZFLSH"] = ret.ReceiverSerialNo;
					ybxx["MZXH"] = ret.CS.MZXH;
					ybxx["ZYH"] = this.data.ZYH;
					ybxx["FPHM"] = this.data.FPHM;
					this.body["YBXX"] = ybxx;
					util.rmi.jsonRequest({
								serviceId : "hospitalPatientSelectionService",
								serviceAction : "updateSettleAccounts",
								body : this.body
							}, function(code, msg, json) {
								this.panel.el.unmask()
								this.invalid = false
								if (code > 300) {
									Ext.Msg.alert("提示", "医保作废成功,本地作废失败:" + msg,
											function() {
												this.doNew()
											}, this);
									return
								}
								this.settlement(this);
							}, this);
				} else {// 失败
					MyMessageTip.msg("提示", ret.outputStr, true);
					return;
				}
			},
		// 医保接口方法,住院发票作废部分结束
		// *******************************************************
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	/**
	 * 基本的医保接口调用
	 * 
	 * @param {}
	 *            ffm 获取参数的后台方法名
	 * @param {}
	 *            body 传给后台的参数
	 * @param {}
	 *            reg 需要转换的医保返回的outputStr的格式
	 * @param {}
	 *            type 1门诊 2住院 需要在业务前判断下是否签到. 如果不需要 怎么不用传 返回map格式 code=1表示成功
	 *            code=2表示失败,outputStr
	 *            返回的参数,如果reg有传则返回map(如果成功的话),没传或者失败则直接返回医保返回的outputStr
	 */
	simpleDyyb : function(ffm, body, reg, type, inInputStrAcc) {
		var map_ret = {};
		if (type && type != null) {
			if (!this.querySFQD(type)) {
				map_ret["code"] = 2;
				map_ret["outputStr"] = "签到失败";
				return map_ret;
			}
		}
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : ffm,
					body : body
				});
		if (ret.code > 300) {
			map_ret["code"] = 2;
			map_ret["outputStr"] = ret.msg;
			return map_ret;
		}
		// 审批信息不为空时，将其加入到返回对象中
		if (ret.json.spxx) {
			map_ret["spxx"] = ret.json.spxx;
		}
		var data = ret.json.body;
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		if (!inInputStrAcc || inInputStrAcc == null) {
			inInputStrAcc = "";
		}
		// var
		// inInputStrAcc="K330100D156000005001D7BF4AD068C81|000000|000000000000372|A6801758|";//读卡读出来的信息
		// 保险卡号/医疗证号|保险卡密码|市民卡流水号|市民卡认证码|
		// 参数列表inTransType,inProcessingCode,inHosCode,inOPTRCode,inBusiCycle,inCenterCode,inSenderSerialNo,inInputStr,inInputStrAcc,inItemNumberIndicator
		var res = o.invokeMCIF(data.TransType, data.ProcessingCode,
				data.HosCode, data.OPTRCode, data.BusiCycle, data.CenterCode,
				data.SenderSerialNo, data.InputStr, inInputStrAcc,
				data.ItemNumberIndicator);
		res = res.replace(/\"/g, "");
		var json = eval("(" + res.replace(/[\r\n]/g, "") + ")");
		if (json.transType == parseInt(data.TransType) + 10
				&& json.resultCode == 0 && json.responseCode == "00") {// 成功
			map_ret["code"] = 1;
			if (reg != null && reg != "" && reg != undefined) {
				map_ret["outputStr"] = this.outputStrToMap(json.outputStr, reg);
			} else {
				map_ret["outputStr"] = json.outputStr;
			}
			map_ret["ReceiverSerialNo"] = json.receiverSerialNo;// 中心返回的中心流水号
			map_ret["BusiCycle"] = data.BusiCycle;// 业务周期号
			map_ret["CS"] = data;// 参数 后台查询返回给前台的参数
		} else {// 失败
			map_ret["code"] = 2;
			map_ret["outputStr"] = json.outputStr;
		}
		return map_ret;
	},
/**
	 * 调用医保接口
	 * 
	 * @param {}
	 *            data
	 * @param {}
	 *            reg
	 * @param {}
	 *            inInputStrAcc
	 * @return {}
	 */
	callYBJK : function(data, reg, inInputStrAcc) {
		var map_ret = {};
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		if (!inInputStrAcc || inInputStrAcc == null) {
			inInputStrAcc = "";
		}
		// var
		// inInputStrAcc="K330100D156000005001D7BF4AD068C81|000000|000000000000372|A6801758|";//读卡读出来的信息
		// 保险卡号/医疗证号|保险卡密码|市民卡流水号|市民卡认证码|
		// 参数列表inTransType,inProcessingCode,inHosCode,inOPTRCode,inBusiCycle,inCenterCode,inSenderSerialNo,inInputStr,inInputStrAcc,inItemNumberIndicator
		var res = o.invokeMCIF(data.TransType, data.ProcessingCode,
				data.HosCode, data.OPTRCode, data.BusiCycle, data.CenterCode,
				data.SenderSerialNo, data.InputStr, inInputStrAcc,
				data.ItemNumberIndicator);
		res = res.replace(/\"/g, "");
		var json = eval("(" + res.replace(/[\r\n]/g, "") + ")");

		if (json.transType == parseInt(data.TransType) + 10
				&& json.resultCode == 0 && json.responseCode == "00") {// 成功
			map_ret["code"] = 1;
			if (reg != null && reg != "" && reg != undefined) {
				map_ret["outputStr"] = this.outputStrToMap(json.outputStr, reg);
			} else {
				map_ret["outputStr"] = json.outputStr;
			}
			map_ret["ReceiverSerialNo"] = json.receiverSerialNo;// 中心返回的中心流水号
			// map_ret["BusiCycle"]=data.BusiCycle;//业务周期号
			map_ret["CS"] = data;// 参数 后台查询返回给前台的参数
		} else {// 失败
			map_ret["code"] = 2;
			map_ret["outputStr"] = json.outputStr;
		}
		return map_ret;
	},
	/**
	 * 查询是否签到,理论上 每次调用医保前都应该调用这个方法
	 * 
	 * @param {}
	 *            type 类别 1是门诊 2是住院
	 * @return {Boolean} 已签到返回true,未签到 或者失败 页面提示并返回false;
	 */
	querySFQD : function(type) {
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "querySFQD",
					type : type
				});
		if (ret.code == 200) {
			return true;
		} else {
			var data = ret.json.body;
			if (data == null) {
				this.processReturnMsg(ret.code, ret.msg);
				return false;
			}
			$PhisActiveXObjectUtils.initHtmlElement();
			var o = $PhisActiveXObjectUtils.getObject();
			var inInputStrAcc = "";
			var res = o.invokeMCIF(data.TransType, data.ProcessingCode,
					data.HosCode, data.OPTRCode, data.BusiCycle,
					data.CenterCode, data.SenderSerialNo, data.InputStr,
					inInputStrAcc, data.ItemNumberIndicator);
			res = res.replace(/\"/g, "");
			var json = eval("(" + res.replace(/[\r\n]/g, "") + ")");

			if (json.transType == parseInt(data.TransType) + 10
					&& json.resultCode == 0 && json.responseCode == "00") {
				var body = {};
				body["YWZQ"] = json.outputStr.split("|")[0];
				body["TYPE"] = type;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "saveSignIn",
							body : body
						});
				if (r.code > 300) {
					MyMessageTip.msg("提示", "医保签到成功,本地数据库保存失败,请记录以下数据:业务周期号 "
									+ body.YWZQ + " 类别 " + type + " 并联系管理员处理",
							true);
					return false;
				}
				return true;
			} else {
				MyMessageTip.msg("提示", json.outputStr, true);
				return false;
			}

		}
	},
	/**
	 * 向服务端请求数据，并返回请求结果
	 * 
	 * @param {}
	 *            ffm 方法名
	 * @param {}
	 *            body 请求数据
	 */
	requestServer : function(ffm, body) {
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : ffm,
					body : body
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return;
		}
		return ret;
	},
	simpleHZYB : function(ffm, body, reg, type) {
		var map_ret = {};
		if (type && type != null && type != "") {
			if (!this.queryNewSFQD(type)) {
				map_ret["code"] = 2;
				map_ret["outputStr"] = "签到失败";
				return map_ret;
			}
		}
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : ffm,
					body : body
				});
		if (ret.code > 300) {
			map_ret["code"] = 2;
			map_ret["outputStr"] = ret.msg;
			return map_ret;
		}
		var data = ret.json.body;
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		o.Hz_INIT();
		//业务编号^医疗机构编号^操作员编号^业务周期号^医院交易流水号^持卡类型^入参^联机标志^
		var res = o.Hz_BUSINESS(data.YWBH+"^"+data.YLJGBH+"^"+data.CZYBH+"^"+data.YWZQH+"^"+data.YYJYLSH+"^"+data.CKLX+"^"+data.RC+"^"+data.LJBZ+"^");
		var json = res.split("|");
		if(json[0] == 0){//成功		0|加载初始化函数BUSINESS_HANDLE失败
			//签到成功返回 	
			//中心交易流水号^联脱机标志^输出参数^配置信息错误提示^
			//0|20131128104632-00001-5505^^20131128104632-00001-00900000-1284|^0|||^
			var outputStr = res.substring(2,res.length);
			var sccs = outputStr.split("^")[2];
			map_ret["code"] = json[0];
			if (reg != null && reg != "" && reg != undefined) {
				//输出参数一条记录
				map_ret["outputStr"] = this.outputStrToMap(sccs.substring(0,sccs.length - 1), reg);
			} else {
				map_ret["outputStr"] = sccs; //输出参数多条(前台处理,小于30条)
			}
			map_ret["YWZQH"] = data.YWZQH;//业务周期号(退费)
			map_ret["YYJYLSH"] = data.YYJYLSH; //医院交易流水号(冲正,退费)
			map_ret["ZXJYLSH"] = outputStr.split("^")[0];//中心交易流水号 (中心报头返回)
			if(data.JZLSH != null && data.JZLSH != "" && data.JZLSH != undefined){
				map_ret["JZLSH"] = data.JZLSH;//就诊流水号(挂号流程,处方上传,预结算,结算,退号需要)
			}
		}else{// 失败
			map_ret["code"] = json[0];
			map_ret["outputStr"] = res;
			map_ret["YYJYLSH"] = data.YYJYLSH; //医院交易流水号(冲正,退费)
		}
		return map_ret;
	}
}
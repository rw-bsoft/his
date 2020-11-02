$package("phis.application.yb.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync", "phis.script.widgets.ymPrompt",
		"phis.script.widgets.PrintWin");

phis.application.yb.script.CommonMethod= {
	
		// 医保接口方法,挂号部分开始		
		doYbghdk:function(ghxx){
	       var ybModule = this.createModule("ybghModule","phis.application.yb.YB/YB/YB01");
			//ybModule.initPanel();
			var win = ybModule.getWin();	
			win.setTitle("韶关医保读卡");
			win.add(ybModule.initPanel());
			win.show();							
			ybModule.on("qr", this.onQr_mzghdk, this);
			ybModule.doNew();		
			this.ybModule = ybModule;
  },
	/*
	 * 读卡界面点击确认后回调方法
	 */
  	onQr_mzghdk : function(ybkxx) {
		this.ybkxx = ybkxx;
		this.opener.getBRXXByMZHM(ybkxx.MZHM);
	},
	/**
	 * 根据病人性质判断是否是医保性质,并返回医保类型. XZDL=1是医保,返回XZXL,0表示未设置
	 * 
	 * @param {}
	 *            brxz
	 * @return {}
	 */
	getYbbrxz : function(brxz) {
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "queryYbbrxz",
					body : brxz
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return null;
		}
		return ret.json.body;
	},
	/**
	 * 医保挂号预结算
	 */
	doYbghyjs : function() {		
		alert("公共医保预结算");		
		if (this.ybkxx == null) {
			return;
		}
		var body = {};
		body["ghxx"] = this.GHXX;// 当前挂号信息
		body["ybkxx"] = this.ybkxx;// 医保读卡信息.
		// 其他需要查询医保挂号登记,上传,预结算,结算需要的参数另行添加
		// ....
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
		// ..
		var ybData = {};// 将需要传到挂号界面的数据放到这个集合,包括医保卡信息,预结算返回的金额,医保结算的参数
		// ...
		// ybData["YJSXX"]=
		// ybData["JSCS"]=ybghcs.JSCS
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
	/*
	 * 打开结算找零前如果没有读卡判断病人是否是医保病人,如果是则不让结算 必须读完卡才能结算(根据实际需求确定是否需要该判断)
	 */
	getSfYb : function(brxz) {
		alert("取医保性质");
		var ybbrxz = this.getYbbrxz(brxz);// 查询当前病人信息 是否是医保
		if (ybbrxz == null) {
			alert("医保性质为空");
			return;
		}
		if (ybbrxz > 0) {
			alert("医保性质不为空");
			// 如果病人性质是医保,并且没读卡, 则不能完成(具体判断已当地医保流程而定)
			MyMessageTip.msg("提示", "医保病人未读卡,请先读卡", true);
			return;
		}
		return 1;
	},
	clearYbxx : function() {
		alert("公共父类方法");
		this.ybkxx = null;
	},
	// 医保接口方法 挂号部分结束
	// *******************************************
	// 医保接口方法,挂号退号部分开始
	doybghth : function() {
		alert("Commond医保功能完善中....");
		return;
		var ybModule = this.createModule("ybghthModule",
				"phis.application.yb.YB/YB/YB01");
		ybModule.initPanel();
		var win = ybModule.getWin();
		win.show();
		ybModule.on("qr", this.onQr_Ghth, this);
		ybModule.doNew();
		this.ybModule = ybModule;
	},
	onQr_Ghth : function(data) {
				// 这里判断读卡和退号的是否是同一个人. 根据具体需要判断
				// if(this.data.BRXM!=data.XM){
				// Ext.Msg.alert("提示","医保卡信息和挂号病人信息不符!");
				// return;
				// }
				this.saving = true
				this.form.el.mask("正在退号...", "x-mask-loading")
				var body = {};
				body.ybkxx = data;// 医保卡读出来的信息
				body.SBXH = this.SBXH;// 当前挂号记录的主键
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbthcs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				var ybghthcs = ret.json.body;
				//医保退号
				//....
				body.SBXH = this.SBXH;
				body.YBXX = {};
				// 将需要保存的医保信息放到body去
				// body.YBXX.SBHX=ybghthcs.SBXH
				// ...
				phis.script.rmi.jsonRequest({
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
				
			}
		// 医保接口方法,挂号退号部分结束
		//*******************************************************
		//医保接口方法,门诊收费部分开始
		,
			// 读卡返回 自动调用病人结算信息
			onQr_mzsfdk : function(data) {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryOutpatientAssociation",
							body : data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				if (ret.json.body.MZGL == 0) {
					this.doQx();
					return;
				}
				var f = this.formModule.form.getForm().findField("MZGL");
				f.setValue(ret.json.body.MZGL);
				this.formModule.doENTER(f, true, data);// 给全局变量this.ybkxx赋值的唯一路径
			},
			// 医保门诊预结算
			doYbmzyjs : function() {
				if (!this.ybkxx || this.ybkxx == null) {
					return;
				}
				var store = this.module.list.grid.getStore();
				var body = {};
				var n = store.getCount();
				var data = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
				}
				body["CFMX"] = data;
				body["GHGL"] = this.MZXX.GHGL;
				body["YBKXX"] = this.ybkxx;
				body["FPHM"] = this.formModule.form.getForm().findField("FPHM")
						.getValue();
				// 根据需要传需要的参数 如诊断等
				// ...
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbMzjscs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				// 医保 登记上传预结算代码
				// ....
				var jsData = {};
				// 以下的需要从预结算里面返回的值获取
				jsData["ZJJE"] = "";// 总金额
				jsData["ZFJE"] = "";// 本次结算需要排除医保部分自己还需要支付的现金部分
				jsData["JJZF"] = "";// 医保报销的钱,不包括医保卡里面扣除的钱 总金额-卡里面扣除的钱-自己需要掏的现金
				jsData["ZHZF"] = "";// 账户支付,医保卡里面扣除的钱,一般包括本年账户支付+历年账户支付
				jsData["jscs"] = ret.json.body.ybjscs;// 医保结算需要的参数
				jsData["ybkxx"] = this.ybkxx;
				// 可根据需要增加需要传的字段
				// ...
				var module = this.midiModules["jsModule"];
				if (!module) {
					module = this.createModule("jsModule", this.jsModule);
					this.midiModules["jsModule"] = module;
					var sm = module.initPanel();
					module.opener = this
					module.on("settlement", this.doQx, this);
				}
				var win = module.getWin();
				module.setData(data, this.MZXX, jsData);
				win.show();
			},
			//医保门诊结算
			doYbmzjs:function(){
			var jscs=this.jsData.jscs;//医保结算参数
			//医保结算代码
			//...
			body.jsData = {};//将需要保存到后台的医保信息放到该参数里
			phis.script.rmi.jsonRequest({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "saveOutpatientSettlement",
						body : body
					}, function(code, msg, json) {
						this.form.el.unmask()
						this.saving = false
						if (code > 300) {
							this.processReturnMsg(code, msg, this.saveToServer,
									[body]);
							// 医保结算成功,本地失败后 取消医保结算
							var b={};//将需要的参数放到这里
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : "medicareService",
										serviceAction : "queryMzqxjscs"
									});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, "取消结算参数查询失败,医保结算成功,本地结算失败 请联系管理员");//这里可以将可以用于手动取消门诊结算的参数显示出来
								return ;
							}
							//医保取消结算代码
							//...
							this.runing = false;		
							return
						}
						this.opener.opener.JSXX = this.MZXX;
						if(this.opener.opener.MZXX){
							this.opener.opener.MZXX.mxsave = false;
						}
						this.fireEvent("settlement", this);
						this.doConcel();
						if (ysk.getValue()) {
							this.opener.doPrintFp(json.FPHM);//病历页面结算用
							this.opener.opener.opener.cfList.fphm = json.FPHM
						}
					}, this)
			}
	// 医保接口方法,门诊结算部分结束
	//*******************************************************
	//医保接口方法,门诊发票作废部分开始	
			,
			// 发票作废读卡成功
			onQr_mzfpzf : function(data) {
				if (data == null) {
					return;
				}
				var body = {};// 需要的参数传入
				body["FPHM"] = this.initDataId;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbFpzfcs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				var qxcs = ret.json.body;
				// 医保取消结算代码
				// ...
				var ybxx = {};// 将需要用到的医保字段放入这里
				var b = {};
				b["YBXX"] = ybxx;
				b["FPHM"] = this.initDataId;
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
			},
	// 医保接口方法,门诊发票作废部分结束
	//*******************************************************
	//医保接口方法,入院登记部分开始	
	// 读卡返回
		onQr_rydj : function(data) {
			var f = this.form.getForm().findField("MZHM");
			f.setValue(data.MZHM);
			this.doYbkhEnter(f, data);
			this.form.getForm().findField("BRXZ").setValue();
		},
			// 医保入院登记
			doYbZydj : function(zyh) {
				var body = {};
				//将需要的参数放到body中
				//...
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbRydjcs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return false;
				}
				var rydjcs = ret.json.body;
				// 医保调用入院登记代码
				// ....
				//成功return true  失败return false
			},
			//医保入院登记成功,本地失败,医保取消入院登记
			doYbqxrydj:function(){
			//取消登记代码. 所有数据前台都有 故不用去后台查询.在this.savaData中
				//....
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
				//body["ZFZYB"]=1//自费转医保
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
				//如果失败 返回false
				//return false;
				//如果成功,更新zy_brry表
				//body["YBZZF"]=1//医保转自费
				//this.doUpdateRydj(body);
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
					this.processReturnMsg(ret.code, "医保入院登记成功,本地更新失败,请联系管理员手动删除记录,ZYH=:"+body.zyh);
					return;
				}
			}
	// 医保接口方法,取消入院登记和转换部分结束
	//*******************************************************
	//医保接口方法,住院结算部分开始
		,
			onQr_zyjs : function(data) {
				this.ybkxx = data;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryZyhmByYbkxx",
							body : this.ybkxx
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
			doYbzyYjs : function() {
				var body = {};
				body["ZYH"] = this.data.ZYH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbZyjscs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				//医保上传,预结算代码
				// var YBSCCS=ret.json.body.YBSCCS//医保上传参数
				// var YBYJSCS=ret.json.body.YBYJSCS//医保预结算参数
				// var YBJSCS=ret.json.body.YBJSCS//医保结算参数
				//...
				//注:如果费用是一条一条上传的,上传时缓存成功上传的费用主键并后台更新,如果有上传失败的 则不能结算.ZY_FYMX增加上传标志 字段自行增加方法自行增加
				this.body.TCJE="";//统筹金额 医保报销部分  总金额-医保本年历年账户支付-个人现金支付
				this.body.ZHZF ="";//账户支付, 本年账户支付加上历年账户支付
				this.body.ZFHJ = "";//自负金额,本年+历年+现金
				this.body.YBKXX=this.ybkxx;
				this.body.YBJSCS=ret.json.body.ybjscs;
				//其他需要的自行添加
				this.showZyjsModule();
			},
			//住院结算
			doYbzyjs:function(data){
			//如果有需要,这里可以增加医保结算金额和本地结算金额比较,如果不同则不能结算
			//...
			//医保结算代码
			//.....
			//如果成功
			//return 结算金额信息;
			//如果失败
			//打印错误信息,return false;
			},
			doYbzyjscx:function(){
			var b = {};
			//将需要的参数放到b中
			//...
			var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "queryYbZyqxjscs",
					body : b
				});
			if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return;
			}
			var qxcs = ret.json.body;
			//医保住院结算取消
			//..
			},
			// 医保接口方法,住院结算部分结束
			// *******************************************************
			//医保接口方法,住院发票作废部分开始
			// 发票作废
			onZyZfQr : function(data) {
				var body={}
				//需要的参数放到body中
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "queryYbzyzfcs",
					body : body
				});
				if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return;
				}
				//医保取消结算代码
				//...
				//如果失败
				//..
				//return;
				this.body["isYb"]=true;
				phis.script.rmi.jsonRequest({
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
				
			},
		// 医保接口方法,住院发票作废部分结束
		// *******************************************************
			
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
	}
}
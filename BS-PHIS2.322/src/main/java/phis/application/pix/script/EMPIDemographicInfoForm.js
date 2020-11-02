/**
 * 个人基本信息查询录入界面
 * 
 * @author 2048
 */
$package("phis.application.pix.script");
/**
 * 个人基本信息查询录入界面
 * 
 * @author tianj
 */

$import("phis.script.TableForm", "phis.script.SimpleList",
		"phis.script.ICCardField",
		"phis.application.pix.script.CombinationSelect",
		"phis.application.pix.script.ParentsQueryList",
		"phis.script.widgets.LookUpField");

phis.application.pix.script.EMPIDemographicInfoForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.pix.script.EMPIDemographicInfoForm.superclass.constructor
			.apply(this, [cfg]);
	this.queryInfo = {};
	this.modified = false;
	this.checked = true;
	this.queryServiceId = "empiService";
	this.queryServiceActioin = "advancedSearch";
	this.on("loadData", this.onLoadData, this);
	this.on("doNew", this.onDoNew, this)
}

Ext.extend(phis.application.pix.script.EMPIDemographicInfoForm,
		phis.script.TableForm, {
			onLoadData : function(entryName, body) {
				if (this.topClick) {
					this.topClick = false;
					return;
				}
				if (this.idCard) {
					this.form.getForm().findField("idCard")
							.setValue(this.idCard);
				}
				if (this.idCardInput) {
					this.form.getForm().findField("idCard").enable();
					if (this.idCard) {
						this.form.getForm().findField("idCard")
								.setValue(this.idCard);
					}
					this.idCard = null;
					this.idCardInput = false;
				}
				var idcardField = this.form.getForm().findField("idCard");
				if (!this.data["idCard"] || this.data["idCard"].length == 0) {
					// idcardField.enable();
					this.idCardValue = "noValue";
				} else {
					var birthDayField = this.form.getForm()
							.findField("birthday");
					var sexCodeField = this.form.getForm().findField("sexCode");
					this.idCardValue = "haveValue";
					birthDayField.disable();
					sexCodeField.disable();
					idcardField.disable();
				}
				var MZHMField = this.form.getForm().findField("MZHM");
				if (!this.data["MZHM"] || this.data["MZHM"].length == 0) {
					MZHMField.enable();
				} else {
					this.MZHM = MZHMField.getValue();
					MZHMField.disable();
				}
				this.fireEvent("loadFormData", this.idCardValue);

				// var insuranceCode = body.insuranceCode;
				// if (insuranceCode) {
				// var f = this.form.getForm().findField("insuranceType");
				// if (f) {
				// if (insuranceCode.key == "99") {
				// f.enable();
				// } else {
				// f.reset();
				// f.disable();
				// }
				// }
				// }
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
				this.loading = true

				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "empiService",
					serviceAction : "personLoad",
					pkey : this.initDataId,
					body : this.initDataBody,
					action : this.op, // 按钮事件
					module : this._mId
				});
				if (this.form && this.form.el) {
					this.form.el.unmask()
				}
				this.loading = false
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.loadData)
					return
				}
				if (ret.json.body) {
					if (!ret.json.body.MZHM) {
						ret.json.body.MZHM = this.form.getForm()
							.findField("MZHM").getValue();
					}
					this.doNew()
					this.initFormData(ret.json.body)
					this.fireEvent("loadData", this.entryName,
						ret.json.body);
				}
				if (this.op == 'create') {
					this.op = "update"
				}
			},
			focusToSaveBtn : function() {
				var btns = this.opener.tab.buttons;
				if (btns) {
					var n = btns.length
					for (var i = 0; i < n; i++) {
						var btn = btns[i]
						if (btn.cmd == "save") {
							if (btn.rendered) {
								btn.focus(true, 200);
								break;
							}
						}
					}
				}
			},
			onReady : function() {
				phis.application.pix.script.EMPIDemographicInfoForm.superclass.onReady
						.call(this);

				var form = this.form.getForm();
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];

					if (item.display && item.display < 2) {
						continue;
					}

					var field = form.findField(item.id);
					if (field) {
						field.on("change", function(f) {
							if (f.getName() == "birthday") {
								var age = this.getAgeFromServer(f.getValue());
								// 设置婚姻状况状态
								this.onBirthdayChange(age);
							}
							if (f.getName() == "age") {
								this.onAgeChange(f.getValue());
							}
						}, this)
						if (item.id == "idCard") {
							field.on("blur", this.onIdCardBlur, this);
							field.editable = true;
							field.on("lookup", this.onLookupParent, this);
						}
						if (item.id == "birthday") {
							var body = this.getAgeFromServer(field.getValue());
							// 设置婚姻状况状态
							field.on("select", function(f) {
								if (f.getName() == "birthday") {
									var body = this.getAgeFromServer(f
											.getValue());
									// 设置婚姻状况状态
									this.onBirthdayChange(body);
								}
							}, this);
						}
					}
				}
				var MZHM = form.findField("MZHM");

				var cardNo = form.findField("cardNo");
				if (MZHM) {
					// MZHM.on("blur", this.onMZHMBlur, this);
					MZHM.on("lookup", function() {
						MZHM.setDisabled(true);
						if (MZHM.getValue().trim() == "") {
							var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "empiService",
								serviceAction : "outPatientNumber"
							});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doInvalid);
							} else {
								MZHM.setValue(ret.json.MZHM);
								this.snap = this.getQueryInfoSnap();
							}
						}
					}, this)
					MZHM.on("clear", function() {
						MZHM.setValue();
						if (this.needsQuery()) {
							MZHM.setDisabled(false);
						} else {
							this.doNew();
							MZHM.setDisabled(false);
							this.serviceAction = "submitPerson";
							this.opener.serviceAction = "submitPerson";
							this.opener.empiId = null;
							this.opener.data = {};
							this.data = {};
							this.addSearchEventListeners();
							// MZHM.setValue(this.MZHM);
							// this.setMZHM(MZHM);
						}
					}, this)
					// MZHM.un("specialkey",this.onFieldSpecialkey,this)
					MZHM.on("specialkey", function(f, e) {
						var key = e.getKey()
						if (key == e.ENTER) {
							e.stopEvent();
							if (f.getValue() != "") {
								this.onMZHMFilled(MZHM);
							}
							if (f.validate()) {
								var field = form.findField("BRXZ");
								if (field) {
									field.focus();
								}
							}
						}
					}, this)
					var pdms = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "checkCardOrMZHM"
					});
					if (pdms.code > 300) {
						// this.processReturnMsg(pdms.code, r.msg,
						// this.onBeforeSave);
						// return;
					} else {
						if (pdms.json.cardOrMZHM == 2) {
							MZHM.on("change", function() {
								cardNo.setValue(MZHM.getValue());
							}, this)
						}
					}
				}
				// var insuranceCode = form.findField("insuranceCode");
				// if (insuranceCode) {
				// insuranceCode.on("select", this.onInsuranceCode, this);
				// insuranceCode.on("blur", this.onInsuranceCode, this)
				// }

				// remove by yangl 修正通过键盘向下箭头展开字典时只显示一个选项的问题
				// var maritalStatusCode = form.findField("maritalStatusCode")
				// maritalStatusCode.on("expand", function(combo) {
				// var tree = combo.tree;
				// tree.expandAll();
				// }, this);
				//
				// var educationCode = form.findField("educationCode")
				// educationCode.on("expand", function(combo) {
				// var tree = combo.tree;
				// tree.expandAll();
				// }, this);
			},
			// setMZHM : function(field) {
			// // alert(field.getValue())
			// var module = this;
			// if (!field.getValue()) {
			// // alert(this.MZHM+"aaaaaa")
			// var form = this.form.getForm();
			// field = form.findField("MZHM");
			// field.setValue(this.MZHM);
			// } else {
			// setTimeout(function() {
			// module.setMZHM(field)
			// }, "100");
			// }
			// },
			// onInsuranceCode : function(f) {
			// var form = this.form.getForm();
			// var insuranceType = form.findField("insuranceType");
			// if ("99" == f.getValue()) {
			// insuranceType.enable();
			// insuranceType.focus(false, 1);
			// } else {
			// insuranceType.reset();
			// insuranceType.disable();
			// }
			// },
			/**
			 * 
			 */
			getlimitAge : function() {
				if (!this.QYNLXZ) {
					var res = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicManageService",
						serviceAction : "loadSystemParams",
						body : {
							privates : ['QYNLXZ']
						}
					});
					this.QYNLXZ = res.json.body.QYNLXZ;
				}
				return this.QYNLXZ;
			},
			onAgeChange : function(age) {
				if (!isNaN(parseInt(age))) {
					var birthday = new Date();
					age = parseInt(age);
					var xznl = this.getlimitAge();
					if (xznl && xznl > 0) {
						if (age <= xznl) {
							MyMessageTip.msg('提示', '请输入出生年月获得准确年龄', true, 5);
							var ageField = this.form.getForm().findField("age");
							ageField.setValue('');
							this.form.getForm().findField("birthday").focus(
									true, true)
							return false;
						}
					}
					this.form.getForm().findField("age").setValue(age);
					var year = birthday.getFullYear() - age;
					var birthdayField = this.form.getForm()
							.findField("birthday");
					birthdayField.setValue(year + "-01-01");
					this.queryInfoFilled();
					var maritalStatusCode = this.form.getForm()
							.findField("maritalStatusCode");
					if (this.serviceAction == "updatePerson"
							&& maritalStatusCode.getValue()) {
						return;
					}
					this.snap = this.getQueryInfoSnap();
					if (age >= 28) {
						maritalStatusCode.setValue({
							key : "21",
							text : "初婚"
						})
					} else {
						maritalStatusCode.setValue({
							key : "10",
							text : "未婚"
						})
					}
					// // 设置职业相关
					// var workPlace =
					// this.form.getForm().findField("workPlace");
					// var workCode = this.form.getForm().findField("workCode");
					// if (age < 16) {
					// workPlace.disable();
					// workCode.disable();
					// } else {
					// workPlace.enable();
					// workCode.enable();
					// }
				}
				return true;
			},
			onInsuranceCode : function(f) {
				var form = this.form.getForm();
				var insuranceType = form.findField("insuranceType");
				if ("99" == f.getValue()) {
					insuranceType.enable();
					insuranceType.focus(false, 1);
				} else {
					insuranceType.reset();
					insuranceType.disable();
				}
			},
			onFieldSpecialkey : function(f, e) {
				var key = e.getKey();
				if (key == e.ENTER) {
					e.stopEvent();
					if (f.minChars) {
						minChars = f.minChars;
						f.minChars = 99;
						setTimeout(function() {
							f.minChars = minChars;
						}, 1000);
					}
					this.quickPickMCode(f);
					if (f.validate) {
						if (f.validate()) {
							this.focusFieldAfter(f.index);
							if (f.getName() == "idCard"
									|| f.getName() == "cardNo"
									|| f.getName() == "personName"
									|| f.getName() == "sexCode"
									|| f.getName() == "birthday") {
								this.onIdCardFilled(f);
							}
						} else {
							MyMessageTip.msg("提示", f.fieldLabel + ":"
									+ f.activeError, true)
						}
					}
				}
			},
			focusFieldAfter : function(index, delay) {
				var items = this.schema.items
				var form = this.form.getForm()
				for (var i = index + 1; i < items.length; i++) {
					var next = items[i]
					var field = form.findField(next.id)
					if (items[index] && items[index].id == 'birthday') {
						var f = form.findField('birthday');
						f.triggerBlur();
						// f.beforeBlur();
						// var age = this.getAgeFromServer(f.getValue());
						// this.onBirthdayChange(age);
					}
					// if (field && !field.disabled && next.xtype !=
					// "imagefield") {// add
					if (next.id == "sexCode") {
						field.focus(false, 100)
						return;
					}
					if (next.id == "cardNo" || next.id == "BRXZ"
							|| next.id == "personName" || next.id == "birthday" || next.id == "sexCode"
							|| next.id == "age" || next.id == 'address'
							|| next.id == 'workPlace') {
						// by
						// yangl
						// :跳过图片类型
						if (field.getValue() == "") {
							field.focus(false, 100)
							return;
						} else {
							continue;
						}
					}
				}
				var btns = this.opener.tab.buttons;
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
			},
			onLookupParent : function() {
				var lookView = this.midiModules["lookView"];
				if (!lookView) {
					lookView = new phis.application.pix.script.ParentsQueryList({
								width : 600,
								height : 300
							});
					lookView.on("idCardReturn", function(idcard) {
						this.queryInfo["idCard"] = idcard;
						var queryData = {};
						var cards = [];
						var card = {
							"certificateTypeCode" : "01",
							"certificateNo" : idcard
						};
						cards.push(card);
						queryData["certificates"] = cards;
						this.queryBy = "idCard";
						this.doQuery(queryData);
					}, this);
				}
				var win = lookView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onBirthdayChange : function(body) {
				var age = body.age;
				var ages = body.ages;
				if (age < 0) {
					return;
				}
				var maritalStatusCode = this.form.getForm()
						.findField("maritalStatusCode");
				var ageField = this.form.getForm().findField("age");
				ageField.setValue(ages);
				if (this.serviceAction == "updatePerson"
						&& maritalStatusCode.getValue()) {
					return;
				}

				if (age >= 28) {
					maritalStatusCode.setValue({
						key : "21",
						text : "初婚"
					})
				} else {
					maritalStatusCode.setValue({
						key : "10",
						text : "未婚"
					})
				}
				// // 设置职业相关
				// var workPlace = this.form.getForm().findField("workPlace");
				// var workCode = this.form.getForm().findField("workCode");
				// if (age < 16) {
				// workPlace.disable();
				// workCode.disable();
				// } else {
				// workPlace.enable();
				// workCode.enable();
				// }
			},

			getAgeFromServer : function(birthday) {
				if (!birthday || birthday == "") {
					return 0;
				}
				var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "publicService",
					serviceAction : "personAge",
					body : {
						birthday : birthday
					}
				});

				var age = 0;
				var body = null;
				if (result.json.body) {
					body = result.json.body
				}
				return body;

			},

			addSearchEventListeners : function() {
				var MZHM = this.form.getForm().findField("MZHM");
				var cardNo = this.form.getForm().findField("cardNo");
				var idCard = this.form.getForm().findField("idCard");
				var birthday = this.form.getForm().findField("birthday");
				var sexCode = this.form.getForm().findField("sexCode");
				var personName = this.form.getForm().findField("personName");
				cardNo.enable();
				MZHM.on("blur", this.onMZHMFilled, this);
				cardNo.on("blur", this.onCardNoFilled, this);
				idCard.on("blur", this.onIdCardFilled, this);
				birthday.on("blur", this.queryInfoFilled, this);
				sexCode.on("blur", this.queryInfoFilled, this);
				personName.on("blur", this.queryInfoFilled, this);
				//浦口增加二维码扫码功能 zhaojian 2017-12-15
				if(this.mainApp.deptId.indexOf("320111")==0){
					var smq =  this.form.getForm().findField("smq");
					smq.on("blur", this.onSmq, this);
				}
			},
			//浦口增加二维码扫码功能 zhaojian 2017-12-15
			onSmq : function(field, e) {
				var smq = field.getValue();
				if (!smq) {
					return;
				}
				this.form.getForm().findField("smq").setValue("");
				var _this = this;
				$.ajax({
					type : 'POST',
					url : "http://58.213.112.246/jsjkkjkjk/hospitaljkk/v2/user/analysis",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify({
						// 'appSign' : '',
						// 'timestamp' : '',
						// 'pack' : '',
						'qrCode' : smq
					}),
					success : function(data) {
						if (data) {
							var idcard = _this.form.getForm().findField("idCard");//身份证号
							idcard.focus();
							idcard.setValue(data.data.idNumber);
							_this.form.getForm().findField("smq").setValue(data.data.virtualCardNum);
							_this.form.getForm().findField("personName").setValue(data.data.realname);//姓名
							_this.form.getForm().findField("address").setValue(data.data.addr);//联系地址
							//_this.mainApp.virtualCardNum = data.data.virtualCardNum;							
							_this.onIdCardBlur(idcard);
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						alert("扫码失败：" + XmlHttpRequest.responseText);
					}
				});
			},

			checkIsChildren : function() {
				var personName = this.form.getForm().findField("personName")
						.getValue();
				if (!personName) {
					return;
				}
				var birthday = this.form.getForm().findField("birthday")
						.getValue();
				if (!birthday) {
					return;
				}
				var sexCode = this.form.getForm().findField("sexCode")
						.getValue();
				if (!sexCode) {
					return;
				}
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				currDate.setYear(currDate.getFullYear() - 4);
				if (currDate > Date.parseDate(birthday, "Y-m-d")) {
					return;
				}
				birthday = birthday.format("Y-m-d");
				this.form.el.mask("正在载入数据...", "x-mask-loading");
				var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "simpleQuery",
					schema : "phis.application.cic.schemas.MPI_DemographicInfo_CIC",
					cnd : [
							'and',
							['eq', ['$', 'personname'], ['s', personName]],
							['eq', ['$', 'sexCode'], ['s', sexCode]],
							[
									'eq',
									['tochar', ['$', 'a.birthday'],
											['s', 'yyyy-MM-dd']],
									['s', birthday]]]
				})
				this.form.el.unmask();
				if (result.json.totalCount > 0) {
					this.showDataInSelectView(result.json.body);
				}
			},

			onIdCardBlur : function(field) {
				var cardNo = field.getValue();
				if (!cardNo) {
					return;
				}
				cardNo = cardNo.replace(/(^\s*)|(\s*$)/g, "");
				field.setValue(cardNo);
				if (!field.validate()) {
					return;
				}
				field.setValue(field.getValue().toUpperCase());
				var info = this.getInfo(cardNo);
				var sex = info[1];
				var birthday = info[0];
				if (birthday) {
					var birthdayField = this.form.getForm()
							.findField("birthday");
					birthdayField.setValue(birthday);
					birthdayField.disable();
				}
				if (sex) {
					var sexCodeField = this.form.getForm().findField("sexCode");
					if (sex == 1) {
						sexCodeField.setValue({
							key : sex,
							text : "男"
						});
					} else {
						sexCodeField.setValue({
							key : sex,
							text : "女"
						});
					}
					sexCodeField.disable();
				}
				var age = this.getAgeFromServer(birthday);
				this.onBirthdayChange(age);
			},
			onMZHMFilled : function(field) {
				var value = field.getValue();
				if (value.trim().length == 0) {
					return;
				}
				if (this.queryInfo["MZHM"] == value) {
					return;
				}
				this.queryInfo["MZHM"] = value;
				var queryData = {};
				/*
				 * var cards = []; var card = { // "cardTypeCode" : "02", "MZHM" :
				 * value };
				 */
				// cards.push(card);
				queryData["MZHM"] = value;
				this.queryBy = "MZHM";
				this.doQuery(queryData);
			},
			onCardNoFilled : function(field) {
				var value = field.getValue();
				if (value.trim().length == 0) {
					return;
				}
				if (this.queryInfo["cardNo"] == value) {
					return;
				}
				this.queryInfo["cardNo"] = value;
				var queryData = {
					cardNo : value
				};
				// var cards = [];
				// var card = {
				// // "cardTypeCode" : "02",
				// "cardNo" : value
				// };
				// cards.push(card);
				// queryData["cards"] = cards;
				this.queryBy = "cardNo";
				this.doQuery(queryData);
			},

			onIdCardFilled : function(field) {
				if (this.serviceAction == "updatePerson") {
					return;
				}
				var idcard = this.form.getForm().findField("idCard");
				if (!idcard.validate()) {
					return;
				}
				var idCardNo = idcard.getValue();
				if (idCardNo.trim().length == 0) {
					return;
				}

				this.queryInfo["idCard"] = idCardNo
				var queryData = {};
				queryData["idCard"] = idCardNo;
				queryData["personName"] = this.form.getForm()
						.findField("personName").getValue();
				// var cards = [];
				// var card = {
				// "certificateTypeCode" : "01",
				// "certificateNo" : idCardNo
				// };
				// cards.push(card);
				// queryData["certificates"] = cards;
				this.queryBy = "idCard";
				this.doQuery(queryData);
			},

			removeSearchEventListeners : function() {
				var cardNo = this.form.getForm().findField("cardNo");
				var idCard = this.form.getForm().findField("idCard");
				var birthday = this.form.getForm().findField("birthday");
				var sexCode = this.form.getForm().findField("sexCode");
				var personName = this.form.getForm().findField("personName");
				cardNo.removeListener("blur", this.onCardNoFilled, this);
				idCard.removeListener("blur", this.onIdCardFilled, this);
				birthday.removeListener("blur", this.queryInfoFilled, this);
				sexCode.removeListener("blur", this.queryInfoFilled, this);
				personName.removeListener("blur", this.queryInfoFilled, this);
				cardNo.disable();
			},

			// 名字、性别、生日填都填写后触发查询。
			queryInfoFilled : function() {
				var personName = this.form.getForm().findField("personName");
				var personNameValue = personName.getValue();
				if (personNameValue.trim().length == 0) {
					return;
				}

				var sexCode = this.form.getForm().findField("sexCode");
				var sexCodeValue = sexCode.getValue();
				if (sexCodeValue.trim().length == 0) {
					return;
				}

				var birthday = this.form.getForm().findField("birthday");
				var birthdayValue = birthday.getValue();
				if (!birthdayValue) {
					return;
				}
				if (this.queryInfo) {
					if (this.queryInfo["personName"] == personNameValue
							&& this.queryInfo["sexCode"] == sexCodeValue
							&& this.queryInfo["birthday"] == birthdayValue
									.toString()) {
						return;
					}
					this.queryInfo["personName"] = personNameValue;
					this.queryInfo["sexCode"] = sexCodeValue;
					this.queryInfo["birthday"] = birthdayValue.toString();
				}

				var queryData = {};
				this.queryBy = "baseInfo";
				queryData["personName"] = personNameValue;
				queryData["sexCode"] = sexCodeValue;
				queryData["birthday"] = birthdayValue;
				this.doQuery(queryData);
			},

			// 每次获取一次查询条件的快照与上次的保存的快照进行对比
			// 决定是否进行查询。
			getQueryInfoSnap : function() {
				var form = this.form.getForm();
				var snap = {};
				snap["MZHM"] = form.findField("MZHM").getValue();
				snap["cardNo"] = form.findField("cardNo").getValue();
				snap["idCard"] = form.findField("idCard").getValue();
				snap["personName"] = form.findField("personName").getValue();
				snap["sexCode"] = form.findField("sexCode").getValue();
				snap["birthday"] = form.findField("birthday").getValue();
				return snap;
			},

			// 判断是否需要进行查询,EMPIInfoModule调用
			needsQuery : function() {
				if (this.serviceAction == "updatePerson") {
					return false;
				}

				var snap = this.getQueryInfoSnap();
				if (!this.snap) {
					return true;
				}
				if (snap["MZHM"] != this.snap["MZHM"]) {
					return true;
				}
				if (snap["idCard"] != ""
						&& snap["idCard"] != this.snap["idCard"]) {
					return true;
				}
				if (snap["cardNo"] != ""
						&& snap["cardNo"] != this.snap["cardNo"]) {
					return true;
				}
				if (snap["personName"] != this.snap["personName"]) {
					return true;
				}
				if ((snap["birthday"] + "") != (this.snap["birthday"] + "")) {
					return true
				}
				if (snap["sexCode"] != this.snap["sexCode"]) {
					return true;
				}
				return false;
			},

			// 判断姓名是否被修改,如果被修改通知EMPIInfoModule调用姓名修改服务。
			// needsUpdateName : function() {
			// var data = this.data;
			// var form = this.form.getForm();
			// var name = form.findField("personName").getValue();
			// if (name != data["personName"]) {
			// return true;
			// }
			// return false;
			// },

			// 判断内容是否改变决定是否执行UPDATE。EMPIInfoModule调用
			needsUpdate : function() {
				var data = this.data;
				var form = this.form.getForm();
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					if (item.display != null && item.display <= 1) {
						continue;
					}
					var fieldValue = form.findField(item.id).getValue();
					var dataValue = this.data[item.id];
					if (fieldValue == null || fieldValue.length == 0) {
						if (!dataValue || dataValue == "") {
							continue;
						}
						return true;
					}
					if (item.dic) {
						if (!dataValue) {
							return true;
						}
						if (fieldValue != dataValue) {
							if (fieldValue == "" && !dataValue.key) {
								continue;
							}
							return true;
						}
						continue;
					}
					if (item.type == "date") {
						var birthdayStr;
						if (fieldValue == "") {
							birthdayStr = null;
						} else {
							birthdayStr = fieldValue.format("Y-m-d");
						}
						if (birthdayStr != dataValue) {
							return true;
						}
						continue;
					}
					if (fieldValue != dataValue) {
						if (fieldValue == "" && dataValue == null) {
							continue;
						}
						return true;
					}
				}
				return false;
			},

			doQuery : function(queryData) {
				if (!this.needsQuery()) {
					this.opener.save = false;
					return;
				}
				this.idCard = queryData.idCard;
				// queryData["queryBy"] = this.queryBy;
				this.queried = true;
				// 保存查询信息快照
				this.snap = this.getQueryInfoSnap();

				this.form.el.mask("正在查询数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.queryServiceId,
					schema : "phis.application.cic.schemas.MPI_DemographicInfo_CIC",
					serviceAction : this.queryServiceActioin,
					body : queryData
				});
				this.form.el.unmask();
				if (ret.code == 403) {
					this.processReturnMsg(ret.code, ret.msg);
					this.opener.save = false;
					return;
				}
				if (ret.code == 900) {
					if (ret.json.body && ret.json.body.length > 0) {
						var url = ret.json.body[0]["url"];
						this.getWin().close();
						window.open(url);
						this.opener.save = false;
						return;
					}
				}
				if (ret.code == 750) {
					nameField = this.form.getForm().findField("personName");
					Ext.Msg.alert("提示", "身份证号码与名字不匹配!");
					nameField.reset();
					this.opener.save = false;
					return;
				}
				var data = ret.json["body"];
				if (!data || data.length == 0) {
					if (this.validate()) {
						this.hasQuery = true;
					}
					if (this.opener.save) {
						this.opener.save = false;
						this.opener.onBeforeSave();
					}
					return;
				}
				this.dataSource = ret.json.dataSource || "chis";
				if (data.length == 1) {
					// 如果数据是从pix服务器取得,只作为默认值填入。
					if (this.dataSource == "pix") {
						this.setDefaultData(data[0]);
						this.hasQuery = true;
						if (this.opener.save) {
							this.opener.save = false;
							this.opener.onBeforeSave();
						}
						return;
					}
					var empiId = data[0]["empiId"];
					var score = data[0]["score"];
					if (score == 1.0) {
						this.serviceAction = "updatePerson"
						this.fireEvent("gotEmpi", empiId)
						this.focusFieldAfter(-1, 0)
						this.hasQuery = true;
						if (this.opener.save) {
							this.opener.save = false;
							this.opener.onBeforeSave();
						}
					} else {
						this.opener.save = false;
						this.hasQuery = true;
						this.showDataInSelectView(data)
					}
				} else {
					this.opener.save = false;
					this.hasQuery = true;
					this.showDataInSelectView(data)
				}
			},

			setDefaultData : function(data) {
				var form = this.form.getForm()
				if (data.idCard) {
					form.findField("idCard").setDisabled(true);
					form.findField("age").setDisabled(true);
				}
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (!v) {
							continue;
						}
						if (it.dic) {
							var text = data[it.id + "_text"]
							v = {
								key : v,
								text : text
							}
						}
						f.setValue(v)
					}
				}
				Ext.apply(this.data, data);
				// 从PIX获取数据后补需要再做查询
				this.snap = this.getQueryInfoSnap();
			},

			initFormData : function(data) {
				// var photo = data["photo"];
				// if (!photo || '0' == photo) {
				// var idcard = data["idCard"];
				// data["photo"] = idcard;
				// }
				var form = this.form.getForm();
				if (data.MZHM) {
					this.serviceAction = "updatePerson";
					this.opener.serviceAction = "updatePerson";
					this.opener.empiId = data.empiId;
					this.opener.data = data;
					this.data = data;
				}
				if (data.idCard) {
					this.idCard = data.idCard;
					form.findField("idCard").setDisabled(true);
					form.findField("sexCode").setDisabled(true);
					form.findField("birthday").setDisabled(true);
					form.findField("age").setDisabled(true);
				} else {
					this.idCard = null;
				}
				var maritalStatusCode = form.findField("maritalStatusCode");

				if (data.body) {
					if (data.body.age >= 28) {
						maritalStatusCode.setValue({
							key : "21",
							text : "初婚"
						})
					} else {
						maritalStatusCode.setValue({
							key : "10",
							text : "未婚"
						})
					}
				}
				// // 设置职业相关
				// var workPlace = form.findField("workPlace");
				// var workCode = form.findField("workCode");
				// if (data.body.age < 16) {
				// workPlace.disable();
				// workCode.disable();
				// } else {
				// workPlace.enable();
				// workCode.enable();
				// }
				phis.application.pix.script.EMPIDemographicInfoForm.superclass.initFormData
						.call(this, data);
			},

			showDataInSelectView : function(data) {

				var idCard = this.form.getForm().findField("idCard").getValue();
				var IdCardValidate = this.form.getForm().findField("idCard")
						.validate();
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					// 如果身份证填写了人没查询到结果，然后使用基本信息查询到的结果列表中，
					// 带有身份证号的记录将被过滤掉。
					if (this.queryBy == "baseInfo" && idCard.length > 0
							&& IdCardValidate) {
						if (r.idCard && r.idCard.length > 0) {
							continue;
						}
					}

					var record = new Ext.data.Record(r);
					records.push(record);
				}

				if (records.length == 0) {
					return;
				}

				var empiIdSelectView = this.midiModules["empiIdSelectView"];
				if (!empiIdSelectView) {
					var empiIdSelectView = new phis.application.pix.script.CombinationSelect({
								entryName : this.entryName,
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人记录",
								width : 500,
								height : 300
							});
					empiIdSelectView.on("onSelect", function(r) {
						if (r.get("idCard") != '') {
							this.idCardInput = true;
						}
						if (this.dataSource == "pix") {
							this.setDefaultData(r.data);
							return;
						}
						this.serviceAction = "updatePerson";
						var empiId = r.get("empiId");
						if (!empiId) {
							empiId = r.get("mpiId");
						}
						this.fireEvent("gotEmpi", empiId);
						var empiField = this.form.getForm().findField("empiId");
						if (empiField) {
							empiField.setValue(empiId);
						}
					}, this);
				}
				empiIdSelectView.getWin().show();
				empiIdSelectView.setRecords(records);
			},
			// //联系地址只能输入汉字
			// checkAddress : function(address){
			// return (/[^\u4E00-\u9FA5]/g.test(address)) == true ? "联系地址只能输入汉字"
			// :"";
			// },
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {
					return "身份证号共有 15 码或18位";
				}
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					return "身份证除最后一位外，必须为数字！";
				}
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy)) {
					return "身份证输入错误！";
				}
				for (var i = 0, ret = 0; i < 17; i++) {
					ret += Ai.charAt(i) * Wi[i];
				}
				Ai += arrVerifyCode[ret %= 11];
				return pId.length == 18 && pId.toLowerCase() != Ai
						? "身份证输入错误！"
						: Ai;
			},

			// 判断时间是否合法
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			},

			getInfo : function(id) {
				// 根据身份证取 省份,生日，性别
				id = this.checkIdcard(id);
				var fid = id.substring(0, 16), lid = id.substring(17);
				if (isNaN(fid) || (isNaN(lid) && (lid != "x"))) {
					return [];
				}
				var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2";
				var birthday = new Date(id.slice(6, 10), id.slice(10, 12) - 1,
						id.slice(12, 14));
				return [birthday, sex];
			},
			// 默认病人性质(从系统参数表查)
			onDoNew : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "empiService",
					serviceAction : "queryNature"
				});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return;
				}
				var f = this.form.getForm().findField("BRXZ")
				f.getStore().on("load", function(store) {
					f.setValue(ret.json.brxz);
				}, this);
				if (f.getStore().getCount() > 0) {
					if (Ext.encode(ret.json.brxz) != "{}") {
						f.setValue(ret.json.brxz);
					}
				}
			},
			doNew : function() {
				phis.application.pix.script.EMPIDemographicInfoForm.superclass.doNew
						.call(this);
				var form = this.form.getForm();
				var MZHM = form.findField("MZHM");
				MZHM.setDisabled(true);
				if (MZHM.getValue().trim() == "") {
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "empiService",
						serviceAction : "outPatientNumber"
					});
					if (ret.code > 300) {
						this
								.processReturnMsg(ret.code, ret.msg,
										this.doInvalid);
					} else {
						MZHM.setValue(ret.json.MZHM);
						var cardNo = form.findField("cardNo");
						if (!this.cardNoSet) {
							var pdms = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "checkCardOrMZHM"
									// cardOrMZHM : data.cardOrMZHM
									});
							if (pdms.code > 300) {
								// this.processReturnMsg(pdms.code, r.msg,
								// this.onBeforeSave);
								// return;
							} else {
								if (pdms.json.cardOrMZHM == 2) {
									cardNo.setValue(ret.json.MZHM);
									this.cardNoSet = true;
								}
							}
						}
						this.snap = this.getQueryInfoSnap();
					}
				}
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent,
					labelSeparator : ":"
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					scope : this
				}
				if (it.onblur) {
					var func = eval("this." + it.onblur)
					if (typeof func == 'function') {
						Ext.apply(cfg.listeners, {
							blur : func
						})
					}
				}
				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = (it.editable == "true") ? true : false
				}
				if (it['not-null'] == "1" || it['not-null'] == "true") {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.regex = /(^\S+)/
					cfg.regexText = "前面不能有空格字符"
				}
				if (it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
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
					this.changeFieldCfg(it, cfg);
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				if (typeof(it.minValue) != 'undefined') {
					cfg.minValue = it.minValue;
				}
				if (it.xtype) {
					if (it.xtype == "htmleditor") {
						cfg.height = it.height || 200;
					}
					if (it.xtype == "textarea") {
						cfg.height = it.height || 65
					}
					if (it.xtype == "datefield"
							&& (it.type == "datetime" || it.type == "timestamp")) {
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
					}
					this.changeFieldCfg(it, cfg);
					return cfg;
				}
				//浦口增加二维码扫码功能 zhaojian 2017-12-15
				if (cfg.name == "smq") {
					cfg.emptyText = "请先使用鼠标点击此处再开始扫描二维码";
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Ymd'
						if (it.maxValue && typeof it.maxValue == 'string'
								&& it.maxValue.length > 10) {
							cfg.maxValue = it.maxValue.substring(0, 10);
						}
						if (it.minValue && typeof it.minValue == 'string'
								&& it.minValue.length > 10) {
							cfg.minValue = it.minValue.substring(0, 10);
						}
						break;
					case 'datetime' :
						cfg.xtype = 'datetimefield'
						cfg.emptyText = "请选择日期时间"
						cfg.format = 'Y-m-d H:i:s'
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						cfg.height = 180
						break;
				}
				this.changeFieldCfg(it, cfg);
				return cfg;
			}
		})
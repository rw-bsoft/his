/**
 * 个人基本信息查询录入界面
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizTableFormView", "app.modules.list.SimpleListView",
		"chis.script.ICCardField",
		"chis.application.mpi.script.CombinationSelect",
		"chis.application.mpi.script.ParentsQueryList");

chis.application.mpi.script.EMPIDemographicInfoForm = function(cfg) {
	chis.application.mpi.script.EMPIDemographicInfoForm.superclass.constructor
			.apply(this, [cfg]);
	this.queryInfo = {};
	this.modified = false;
	this.checked = true;
	this.loadServiceId = "chis.empiService";
	this.loadAction = "getDemographicInfo";
	this.queryServiceId = "chis.empiService";
	this.queryServiceActioin = "advancedSearch";
	this.showButtonOnTop = false;
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.mpi.script.EMPIDemographicInfoForm,
		chis.script.BizTableFormView, {
			onLoadData : function(entryName, body) {
				this.cards = body.cards;
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
					idcardField.enable();
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
				this.fireEvent("loadFormData", this.idCardValue);

				var insuranceCode = body.insuranceCode;
				if (insuranceCode) {
					var f = this.form.getForm().findField("insuranceType");
					if (f) {
						if (insuranceCode.key == "99") {
							f.enable();
						} else {
							f.reset();
							f.disable();
						}
					}
				}
			},

			onReady : function() {
				chis.application.mpi.script.EMPIDemographicInfoForm.superclass.onReady
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
										var age = this.getAgeFromServer(f
												.getValue());
										// 设置婚姻状况状态
										this.onBirthdayChange(age);
									}
								}, this)
						if (item.id == "idCard") {
							field.on("blur", this.onIdCardBlur, this);
							field.editable = true;
							field.on("lookup", this.onLookupParent, this);
						}
					}
				}

				var insuranceCode = form.findField("insuranceCode");
				if (insuranceCode) {
					insuranceCode.on("select", this.onInsuranceCode, this);
					insuranceCode.on("blur", this.onInsuranceCode, this)
				}
				var maritalStatusCode = form.findField("maritalStatusCode")
				maritalStatusCode.on("expand", function(combo) {
							var tree = combo.tree;
							tree.expandAll();
						}, this);

				var educationCode = form.findField("educationCode")
				educationCode.on("expand", function(combo) {
							var tree = combo.tree;
							tree.expandAll();
						}, this);
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
					this.quickPickMCode(f);
					this.focusFieldAfter(f.index);
					if (f.getName() == "idCard" || f.getName() == "cardNo"
							|| f.getName() == "personName"
							|| f.getName() == "sexCode"
							|| f.getName() == "birthday") {
						this.onIdCardFilled(f);
					}
				}
			},

			onLookupParent : function() {
				var lookView = this.midiModules["lookView"];
				if (!lookView) {
					lookView = new chis.application.mpi.script.ParentsQueryList(
							{
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

			onBirthdayChange : function(age) {
				if (age < 0) {
					return;
				}
				var maritalStatusCode = this.form.getForm()
						.findField("maritalStatusCode");
				if (this.serviceAction == "updatePerson"
						&& maritalStatusCode.getValue()) {
					return;
				}

				if (age >= 28) {
					maritalStatusCode.setValue({
								key : "21",
								text : "已婚"
							})
				} else {
					maritalStatusCode.setValue({
								key : "10",
								text : "未婚"
							})
				}
				// 设置职业相关
				var workPlace = this.form.getForm().findField("workPlace");
				var workCode = this.form.getForm().findField("workCode");
				if (age < 16) {
					workPlace.disable();
					workCode.disable();
				} else {
					workPlace.enable();
					workCode.enable();
				}
			},

			getAgeFromServer : function(birthday) {
				if (!birthday || birthday == "") {
					return 0;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "calculateAge",
							method : "execute",
							body : {
								birthday : birthday
							}
						});

				var age = 0;
				if (result.json.body) {
					var age = result.json.body.age
				}
				return age;
			},

			addSearchEventListeners : function() {
				var cardNo = this.form.getForm().findField("cardNo");
				var idCard = this.form.getForm().findField("idCard");
				var birthday = this.form.getForm().findField("birthday");
				var sexCode = this.form.getForm().findField("sexCode");
				var personName = this.form.getForm().findField("personName");
				cardNo.enable();
				cardNo.on("blur", this.onCardNoFilled, this);
				idCard.on("blur", this.onIdCardFilled, this);
				birthday.on("blur", this.queryInfoFilled, this);
				sexCode.on("blur", this.queryInfoFilled, this);
				personName.on("blur", this.queryInfoFilled, this);
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
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.simpleQuery",
					method : "execute",
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
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
				var birthdayField = this.form.getForm().findField("birthday");
				var sexCodeField = this.form.getForm().findField("sexCode");
				if (cardNo.trim().length == 0) {
					birthdayField.enable();
					sexCodeField.enable();
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

					birthdayField.setValue(birthday);
					birthdayField.disable();
				}
				if (sex) {

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
			needsUpdateName : function() {
				var data = this.data;
				var form = this.form.getForm();
				var name = form.findField("personName").getValue();
				if (name != data["personName"]) {
					return true;
				}
				return false;
			},

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
					return;
				}
				this.idCard = queryData.idCard;
				queryData["queryBy"] = this.queryBy;
				this.queried = true;
				// 保存查询信息快照
				this.snap = this.getQueryInfoSnap();

				this.form.el.mask("正在查询数据...", "x-mask-loading");
				util.rmi.jsonRequest({
					serviceId : this.queryServiceId,
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
					serviceAction : this.queryServiceActioin,
					method : "execute",
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (json.hasDisableRecord) {
						Ext.Msg.alert("提示", "卡号已经被挂失，或者已失效，或者已被注销!");
						return;
					}
					if (code == 403) {
						this.processReturnMsg(result.code, result.msg);
						return;
					}
					if (code == 900) {
						if (json.body && json.body.length > 0) {
							var url = json.body[0]["url"];
							this.getWin().close();
							window.open(url);
							return;
						}
					}
					if (code == 750) {
						nameField = this.form.getForm().findField("personName");
						Ext.Msg.alert("提示", "身份证号码与名字不匹配!");
						nameField.reset();
						return;
					}
					var data = json["body"];
					if (!data || data.length == 0) {
						return;
					}
					this.dataSource = json.dataSource || "chis";
					if (data.length == 1) {
						// 如果数据是从pix服务器取得,只作为默认值填入。
						if (this.dataSource == "pix") {
							this.setDefaultData(data[0]);
							this.cards = data[0].cards;
							return;
						}
						var empiId = data[0]["empiId"];
						this.cards = data[0].cards;
						var score = data[0]["score"];
						if (score == 1.0) {
							this.serviceAction = "updatePerson"
							this.fireEvent("gotEmpi", empiId)
							this.focusFieldAfter(-1, 0)
						} else {
							this.showDataInSelectView(data)
						}
					} else {
						this.showDataInSelectView(data)
					}
				}, this)// jsonRequest
			},

			setDefaultData : function(data) {
				var form = this.form.getForm()
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
				chis.application.mpi.script.EMPIDemographicInfoForm.superclass.initFormData
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
					var empiIdSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : this.entryName,
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人记录",
								width : 500,
								height : 300
							});

					empiIdSelectView.on("onSelect", function(r) {
						this.cards = r.data.cards;
						if (r.get("idCard") != '') {
							this.idCardInput = true;
						}
						if (this.dataSource == "pix") {
							this.setDefaultData(r.data);
							return;
						}
						this.serviceAction = "updatePerson";
						var empiId = r.get("empiId");
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
			}
		})
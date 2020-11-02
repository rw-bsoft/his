$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.form.TableFormView", "chis.script.util.Vtype")

chis.application.conf.script.admin.DemographicInfoFormView = function(cfg) {
	cfg.colCount = 3
	cfg.showButtonOnTop = true
	cfg.width = 800
	cfg.labelWidth = 80
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 170
	cfg.serviceId = "chis.empiService"
	chis.application.conf.script.admin.DemographicInfoFormView.superclass.constructor.apply(this,
			[cfg]);
	this.on("loadData", this.onLoadData, this)
	this.on("beforeSave", this.onBeforeSave, this)
}

Ext.extend(chis.application.conf.script.admin.DemographicInfoFormView,
		chis.script.app.modules.form.TableFormView, {
			onReady : function() {
				var idCard = this.form.getForm().findField("idCard")
				idCard.on("blur", this.onIdCard, this)
			},
			onIdCard : function() {
				var idCardField = this.form.getForm().findField("idCard")
				var cardNo = idCardField.getValue();
				if (!idCardField.validate() || cardNo == '') {
					return
				}

				var info = this.getInfo(cardNo);
				var sex = info[1];
				var birthday = info[0];
				if (birthday) {
					var birthdayField = this.form.getForm()
							.findField("birthday");
					birthdayField.setValue(birthday);
				}
				if (sex) {
					var sexCodeField = this.form.getForm().findField("sexCode");
					if (sex == 1)
						sexCodeField.setValue({
									key : sex,
									text : "男"
								});
					else
						sexCodeField.setValue({
									key : sex,
									text : "女"
								});
				}
				this.form.el.mask("正在载入数据...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							cnd : ['eq', ['$', 'idCard'], ['s', cardNo]],
							schema : 'MPI_DemographicInfo'
						})
				this.form.el.unmask()
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				if (result.json.totalCount == 0) {
					return
				} else {
					this.initDataId = result.json.body[0].empiId
					this.loadData()
				}

				// var age = this.getAgeFromServer(birthday);
				// this.onBirthdayChange(age);
			},
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18)
					return "身份证号共有 15 码或18位";
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai))
					return "身份证除最后一位外，必须为数字！";
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy))
					return "身份证输入错误！";
				for (var i = 0, ret = 0; i < 17; i++)
					ret += Ai.charAt(i) * Wi[i];
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
				id = this.checkIdcard(id)
				var fid = id.substring(0, 16), lid = id.substring(17);
				if (isNaN(fid) || (isNaN(lid) && (lid != "x")))
					return []
				var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2"
				var birthday = new Date(id.slice(6, 10), id.slice(10, 12) - 1,
						id.slice(12, 14))
				return [birthday, sex]
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.empiService",
							op : this.op,
							schema : this.entryName,
							body : saveData,
							serviceAction : "savePerson"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							// if (json.body) {
							// this.initFormData(json.body)
							// this.fireEvent("save", this.entryName, this.op,
							// json, this.data)
							// }
							this.fireEvent("save", this.entryName, this.op,
									json, this.data)
							this.op = "update"
						}, this)
				this.win.hide()
			},
			onLoadData : function() {
				this.form.getForm().findField("idCard").disable()
				if (this.form.getTopToolbar()) {
					this.form.getTopToolbar().items.item(1).disable()
				}
			},
			onBeforeSave : function(entryName, op, saveData) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							schema : "MPI_DemographicInfo",
							cnd : ['eq', ['$', 'cardNo'],
									['s', saveData.cardNo]]
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}

				if (result.json.totalCount > 0) {
					alert("证件号重复")
					return false;
				}
			}
		});
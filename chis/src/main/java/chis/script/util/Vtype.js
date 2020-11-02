$package("chis.script.util")

Ext.apply(Ext.form.VTypes, {
			"email" : function(v) {
				var email = /^(\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6}$/
				if (v.length == 0) {
					return true
				}
				return email.test(v)
			},
			"idCard" : function(pId) {
				if (pId.length == 0) {
					return true
				}
				// 19位的是没有身份证孩子用父母的身份证加字母a b 产生
				var tempId
				if (pId.length == 19) {
					tempId = pId.substring(0, 17)
					pId = tempId
				}
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {
					this.idCardText = "身份证号共有 15 码或18位"
					return false;
				}

				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					this.idCardText = "身份证除最后一位外，必须为数字！"
					return false
				}

				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getCurrentDate",
							method:"execute",
							info : "身份证查询service"
						})
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						result.json.body.currentDate, "Y-m-d");

				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !(function(dd, mm, yyyy) {
							if (mm == 2) {
								var leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy
										% 400 == 0));
								if (dd > 29 || (dd == 29 && !leap)) {
									return false;
								}
							}
							return true;

						})(dd, mm, yyyy)) {
					this.idCardText = "身份证输入错误！"
					return false;
				}
				for (var i = 0, ret = 0; i < 17; i++)
					ret += Ai.charAt(i) * Wi[i];
				Ai += arrVerifyCode[ret %= 11];
				if (pId.length == 18 && pId.toLowerCase() != Ai) {
					this.idCardText = "身份证输入错误！"
					return false
				} else {
					return true
				}
			},
			"childIdCard" : function(pId) {
				if (pId.length == 0) {
					return true
				}
				// 19位的是没有身份证孩子用父母的身份证加字母a b 产生
				var tempId
				if (pId.length == 19) {
					tempId = pId.substring(0, 17)
					pId = tempId
				}
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 18) {
					this.childIdCardText = "身份证号共有18位"
					return false;
				}

				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					this.childIdCardText = "身份证除最后一位外，必须为数字！"
					return false
				}

				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							method:"execute",
							serviceAction : "getCurrentDate"
						})
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						result.json.body.currentDate, "Y-m-d");

				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !(function(dd, mm, yyyy) {
							if (mm == 2) {
								var leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy
										% 400 == 0));
								if (dd > 29 || (dd == 29 && !leap)) {
									return false;
								}
							}
							return true;

						})(dd, mm, yyyy)) {
					this.childIdCardText = "身份证输入错误！"
					return false;
				}
				for (var i = 0, ret = 0; i < 17; i++)
					ret += Ai.charAt(i) * Wi[i];
				Ai += arrVerifyCode[ret %= 11];
				if (pId.length == 18 && pId.toLowerCase() != Ai) {
					this.childIdCardText = "身份证输入错误！"
					return false
				} else {
					return true
				}
			},
			"bp" : function(v) {
				var bp = /^([0-2]?[0-9]?[0-9]|300)\/([0-2]?[0-9]?[0-9]|300)$/;
				if (v.length == 0) {
					return true
				}
				var result = bp.test(v);
				if (result) {
					var sbp = Number(v.substring(0, v.indexOf("/")));
					var dbp = Number(v.substring(v.indexOf("/") + 1));
					if (sbp <= dbp) {
						this.bpText = "收缩压应该大于舒张压！";
						return false;
					} else {
						return true;
					}
				} else {
					this.bpText = "血压格式不正确[收缩压(0-300)/舒张压(0-300)]！";
					return false;
				}
			},
			"idCardText" : this.idCardText,
			"idCardMask" : /[0-9xXaAbB]/i,
			"childIdCardText" : this.childIdCardText,
			"childIdCardMask" : /[0-9xXaAbB]/i,
			"bpText" : this.bpText,
			"bpMask" : /[0-9\/]/i
		})

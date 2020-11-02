/**
 * 公共文件*HTML表单的方法
 * 
 * @author : zhouw
 */
$package("chis.script");

chis.script.HtmlCommonMethod = {
	// 获取对象
	getObj : function(id) {
		return document.getElementById(id + this.idPostfix)
	},
	// text类型的获取值
	getValueById : function(id) {
		return document.getElementById(id + this.idPostfix).value
	},
	// radio类型的获取值
	getRadioValue : function(id) {
		var v = document.getElementsByName(id);
		var le = v.length;
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				return v[i].value;
			}
		}
		return "";
	},
	getRadioValueMy : function(id) {
		var v = document.getElementsByName(id + this.idPostfix);
		var le = v.length;
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				return v[i].value;
			}
		}
		return "";
	},
	// checkBox类型的获取值
	getCheckBoxValues : function(id) {
		var v = document.getElementsByName(id);
		var le = v.length;
		var value = new Array();
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				value.push(v[i].value);
			}
		}
		return value;
	},
	// checkBox类型的获取值
	getCheckBoxValuesMy : function(id) {
		var v = document.getElementsByName(id+ this.idPostfix);
		var le = v.length;
		var value = new Array();
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				value.push(v[i].value);
			}
		}
		return value;
	},
	// text类型的赋值
	setValueById : function(id, v) {
		document.getElementById(id + this.idPostfix).value = v;
	},
	// radio类型的赋值
	setRadioValue : function(id, v) {
		var dom = document.getElementsByName(id);
		var le = dom.length;
		for (var i = 0; i < le; i++) {
			if (dom[i].value == v) {
				dom[i].checked = true;
				break;
			}
		}
	},// radio类型的赋值
	setRadioValueMy : function(id, v) {
		var dom = document.getElementsByName(id + this.idPostfix);
		var le = dom.length;
		for (var i = 0; i < le; i++) {
			if (dom[i].value == v) {
				dom[i].checked = true;
				break;
			}
		}
	},
	// checkBox类型的赋值
	setCheckBoxValues : function(id, v) {
		var ll = v.length;
		var dom = document.getElementsByName(id);
		var k = dom.length;
		// 下面代码是后台传值list或者string, 二选一 确定后台传值类型后 将另一个判断删掉
		if (typeof v == "object") {// 如果值是list类型
			var l = v.length;
			for (var i = 0; i < l; i++) {
				for (var j = 0; j < k; j++) {
					if (v[i] == dom[j].value) {
						dom[j].checked = true;
						break;
					}
				}
			}
		} else {
			if (v.indexOf(",") > -1) {// string ：1,2,1,1,3
				value = v.split(",");
				var l = value.length;
				for (var i = 0; i < l; i++) {
					for (var j = 0; j < k; j++) {
						if (value[i] == dom[j].value) {
							dom[j].checked = true;
							break;
						}
					}
				}
			} else if (ll > 0) {// string ：12113
				var r;
				for (var g = 0; g < ll; g++) {
					r = v.slice(g, g + 1);
					for (var y = 0; y < k; y++) {
						if (r == dom[y].value) {
							dom[y].checked = true;
							break;

						}
					}

				}

			}
		}
	},
	// checkBox类型的赋值
	setCheckBoxValuesMy : function(id, v) {
		var ll = v.length;
		var dom = document.getElementsByName(id+ this.idPostfix);
		var k = dom.length;
		// 下面代码是后台传值list或者string, 二选一 确定后台传值类型后 将另一个判断删掉
		if (typeof v == "object") {// 如果值是list类型
			var l = v.length;
			for (var i = 0; i < l; i++) {
				for (var j = 0; j < k; j++) {
					if (v[i] == dom[j].value) {
						dom[j].checked = true;
						break;
					}
				}
			}
		} else {
			if (v.indexOf(",") > -1) {// string ：1,2,1,1,3
				value = v.split(",");
				var l = value.length;
				for (var i = 0; i < l; i++) {
					for (var j = 0; j < k; j++) {
						if (value[i] == dom[j].value) {
							dom[j].checked = true;
							break;
						}
					}
				}
			} else if (ll > 0) {// string ：12113
				var r;
				for (var g = 0; g < ll; g++) {
					r = v.slice(g, g + 1);
					for (var y = 0; y < k; y++) {
						if (r == dom[y].value) {
							dom[y].checked = true;
							break;

						}
					}

				}

			}
		}
	},
	// 给单选框注册监听事件：单选框控制文本框
	// jsonAarray参数说明：[{
	// "name" : "apgarNew", --radio的name
	// "isRed":"yes"---yes 表示点击的时候清除css（红色）
	// "value":--radio的key 用来制定的那个key控制下面text可用或者不可用
	// "id" : [{"contro":"no",id:"apgar1"},{"contro":"no",id:"apgar5"}]--text的id
	// contro:yes表示可用，no 表示不可用
	// }];
	radioToInput : function(jsonAarray) {
		var len = jsonAarray.length;
		var name;
		var id;
		var value;
		var isRed;
		var _cfg = this;
		for (var i = 0; i < len; i++) {
			name = jsonAarray[i].name;
			id = jsonAarray[i].id;
			value = jsonAarray[i].value;
			isRed = jsonAarray[i].isRed;
			var obj = document.getElementsByName(name + this.idPostfix);
			var handleFun = function(objValue, name, id, value, isRed, _cfg) {
				return function() {
					_cfg
							.radioControText(objValue, name, id, value, isRed,
									_cfg);
				}
			}
			for (var j = 0; j < obj.length; j++) {
				var objValue = obj[j];
				if (window.attachEvent) {// IE的事件代码
					obj[j].attachEvent("onclick", handleFun(objValue.value,
									name, id, value, isRed, _cfg));
				} else {
					obj[j].addEventListener("click", handleFun(objValue.value,
									name, id, value, isRed, _cfg), false);
				}
			}
		}
	},
	radioControText : function(objValue, name, id, value, isRed, _cfg) {
		var contro;
		var ids;
		var t;

		if (objValue == value && id) {
			for (var i = 0; i < id.length; i++) {
				contro = id[i].contro;
				ids = id[i].id
				t = document.getElementById(ids + _cfg.idPostfix);
				if (contro == "yes") {
					t.disabled = false;
				} else {
					t.value = "";
					t.disabled = true;
				}
			}
		} else if (id) {
			for (var i = 0; i < id.length; i++) {
				t = document.getElementById(id[i].id + _cfg.idPostfix);
				t.value = "";
				t.disabled = true;
			}
		} else if (!id) {
			if (isRed == "yes") {
				this.reMoveClass(name);
			}

		}

	},
	// 设置标签中的文本是否红色显示
	setRedLabel : function(labels, _cfg) {
		for (var i = 0, len = labels.length; i < len; i++) {
			var labId = labels[i] + _cfg.idPostfix;
			var lab = document.getElementById(labId);
			if (lab) {
				lab.style.color = "#FF0000";
			}
		}
	},
	onIdCardBlur : function(id) {
		var r = this.getValueById(id);
		if (!r) {
			return false;
		}

		if (!this.checkIdcard(r, id)) {
			return false;
		};
	},
	checkIdcard : function(pId, id) {
		var falg = true;
		obj = document.getElementById(id + this.idPostfix);
		var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
		var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
		var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
		if (pId.length != 15 && pId.length != 18) {
			this.addClassMy(obj, "x-form-invalid");
			obj.title = "身份证号共有 15 码或18位";
			falg = false;
			return;
			// return "身份证号共有 15 码或18位";
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}

		var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0, 6)
				+ "19" + pId.slice(6, 16);
		if (!/^\d+$/.test(Ai)) {
			this.addClassMy(obj, "x-form-invalid");
			obj.title = "身份证除最后一位外，必须为数字！";
			falg = false;
			return;
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}
		var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
				.slice(12, 14);
		var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
				.getMonth(), day = d.getDate(), now = Date.parseDate(
				this.mainApp.serverDate, "Y-m-d");
		if (year != yyyy || mon + 1 != mm || day != dd || d > now
				|| now.getFullYear() - year > 110
				|| !this.isValidDate(dd, mm, yyyy)) {
			this.addClassMy(obj, "x-form-invalid");
			obj.title = "身份证输入错误！";
			falg = false;
			return;
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}
		for (var i = 0, ret = 0; i < 17; i++) {
			ret += Ai.charAt(i) * Wi[i];
		}
		Ai += arrVerifyCode[ret %= 11];
		return pId.length == 18 && pId.toLowerCase() != Ai ? "身份证输入错误！" : Ai;
		if (!flag) {
			return false;
		}
	},// 判断时间是否合法
	isValidDate : function(day, month, year) {
		if (month == 2) {
			var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
			if (day > 29 || (day == 29 && !leap)) {
				return false;
			}
		}
		return true;
	},
	hasClassMy : function(obj, cls) {// 判断对象obj是否有cls样式
		return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
	},
	addClassMy : function(obj, cls) {// 给对象obj增加cls样式
		if (!this.hasClass(obj, cls))
			obj.className += " " + cls;
	},
	removeClassMy : function(obj, cls) {// 移出对象obj的cls样式
		if (this.hasClass(obj, cls)) {
			var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
			obj.className = obj.className.replace(reg, ' ');
		}
	}
}
$package("app.desktop.plugins");
$import("util.rmi.miniJsonRequestSync");

app.desktop.plugins.Logon = function(config) {
	this.forConfig = false;
	this.deep = false;
	app.desktop.plugins.Logon.superclass.constructor.apply(this, [config]);
	
};

Ext.extend(app.desktop.plugins.Logon, app.desktop.Module, {

	getWin : function() {
		if (this.win) {
			return this.win;
		}
		var sureBtn = ClassLoader.stylesheetHome
				+ "app/desktop/images/homepage/sure.png";
		var cancelBtn = ClassLoader.stylesheetHome
				+ "app/desktop/images/homepage/cancel.png";
		var win = new Ext.Window({
			width : 400,
			height : 300,
			// autoWidth:true,
			frame : false,
			border : false,
			// autoHeight:true,
			closeAction : 'hide',
			resizable : false,
			modal : true,
			shim : true,
			closable : false,
			html : '<div id="logonWinDiv">'
					+ '<div style="padding-top:90px;padding-left:25px;">'
					+ '<table width="330" border="0" align="center" cellpadding="0" cellspacing="0">'
					+ '<tr>'
					+ '<td width="70" height="40" align="left" valign="middle">用户名：</td>'
					+ '<td colspan="2" align="left" valign="middle"><input type="text" name="logonuser" id="logonuser" style="width:260px;" disabled="true"/></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td height="40" align="left" valign="middle">密　码：</td>'
					+ '<td colspan="2" align="left" valign="middle"><input type="password" name="password" id="password" style="width:260px;" /></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td height="45" align="left" valign="middle">&nbsp;</td>'
					+ '<td width="80" align="left" valign="middle"><input type="checkbox" name="checkRole" id="checkRole" /><label style="padding-left:10px">切换角色</label></td>'
					+ '<td align="center" valign="middle"><select name="select" id="role" style="width:180px;" disabled="true">'
					+ '</select></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td colspan="3" height="40"></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td>&nbsp;</td>'
					+ '<td colspan="2" align="right" style="padding-right:20px;"><input type="image" src="'
					+ sureBtn
					+ '" name="button" id="logonIn" style="margin-right:40px;"/><input  type="image" src="'
					+ cancelBtn + '" name="button2" id="canel"/></td>'
					+ '</tr>' + '</table>' + '</div>' + '</div>'
		});

		win.doLayout();
		win.on("afterrender", this.initCmp, this);
		// win.on("close",this.doCanel,this);
		win.on("show", this.onWinShow, this);
		this.win = win;
		return win;
	},

	initCmp : function(win) {
		var user = Ext.fly("logonuser");
		if (this.mainApp) {
			this.uid = this.mainApp.uid;
		}
		if (this.uid) {
			user.dom.value = this.uid;
		}

		var pws = Ext.fly("password");
		pws.dom.value = "";
		pws.dom.focus();

		var roleKey = this.mainApp.deptId + "@" + this.mainApp.jobtitleId;
		this.oldRole = roleKey;
		var roleText = this.mainApp.jobtitle + "[" + this.mainApp.dept + "]";
		var r = document.createElement("option");
		r.value = roleKey;
		r.text = roleText;
		var roleEl = Ext.fly("role");
		roleEl.dom.options.add(r);
		roleEl.on("click", this.loadRole, this);

		var checkRoleEl = Ext.fly("checkRole");
		checkRoleEl.on("click", this.loadRole, this);

		var logon = Ext.fly("logonIn");
		logon.on("click", this.doLogon, this);
		var canel = Ext.fly("canel");
		canel.on("click", this.doCanel, this);
	},

	onWinShow : function() {
		var pws = Ext.fly("password");
		pws.dom.value = "";
		if (this.mainApp) {
			this.mainApp.desktop.fireEvent("winLock");
		}
	},

	loadRole : function() {
		var checkRoleVal = Ext.fly("checkRole").dom.checked;
		var roleDom = Ext.fly("role").dom;
		if (checkRoleVal) {
			roleDom.disabled = false;
			var uid = Ext.fly("logonuser").dom.value;
			var psw = Ext.fly("password").dom.value;
			if (!uid || !psw) {
				return;
			}
			if (this.allRole) {
				return;
			}
			var result = util.rmi.miniJsonRequestSync({
						serviceId : 'roleLocator',
						uid : uid,
						psw : psw
					});
			if (result.code == 200) {
				this.allRole = result.json.body;
				roleDom.options.length = 0;
				for (var i = 0; i < this.allRole.length; i++) {
					var r = document.createElement("option");
					r.value = this.allRole[i]['key'];
					r.text = this.allRole[i]['text'];
					roleDom.options.add(r);
				}
			}
		} else {
			roleDom.disabled = true;
		}
	},

	doLogon : function(replace) {
		var uidEle = Ext.fly("logonuser").dom
		var pwdEle = Ext.fly("password").dom
		var roleEle = Ext.fly("role").dom
		var uid = uidEle.value
		if (!uid) {
			alert("请输入用户名。");
			uidEle.focus()
			logoning = false;
			return;
		}
		var pwd = pwdEle.value
		if (!pwd) {
			alert("请输入密码。");
			pwdEle.focus()
			logoning = false;
			return;
		}
		var role = roleEle.value
		if (!role || roleEle.options.length == 0) {
			alert("用户名或密码不正确。");
			roleEle.focus()
			logoning = false;
			return;
		}
		
		var res = util.rmi.miniJsonRequestSync({
					serviceId : "logon",
					uid : uid,
					psw : pwd,
					urole : role,
					deep : false,
					forConfig : true,
					replace : replace || false
				})
		if (res.code == 505) {
			logoning = false;
			alert("请选择您要登录的角色.")
			pwdEle.focus()
			return
		}
		if (res.code == 530) {
			var c = confirm("该账号已在别处登录系统，是否继续?")
			if (c == true) {
				this.doLogon(true)
			}
			logoning = false;
			return
		}
		if (res.code == 501) {
			alert("用户名或密码错误...")
			logoning = false;
			return;
		}
		if (res.code == 504) {
			alert("该用户已被禁用，请联系网络管理员。")
			logoning = false;
			return;
		}
		if(role == this.oldRole){
		//@@如果是原来用户登陆，不重新加载主窗口
			this.win.hide();
			this.fireEvent("logonSuccess", res);
			return;
		}
		util.schema.clear();
		var body = Ext.get(document.body)
		body.update("") // clear everything
		body.removeClass("Mybody")

		var apps = res.json.body
		if (res.layoutCss) {
			$styleSheet(res.layoutCss)
		}
		if (!apps) {
			return
		}
		var appModules = []
		var appMenuItems = []
		var size = apps.length
		var myPage = []
		for (var i = 0; i < size; i++) {
			var ap = apps[i]
			if (!ap) {
				continue;
			}
			if (ap.type == "myPage") {
				myPage.push(ap)
			}
			var modules = ap.modules
			if (!modules) {
				continue;
			}
			for (var j = 0; j < modules.length; j++) {
				var module = modules[j]
				if (typeof module != "object") {
					continue;
				}
				module.type = "USER"
				appModules.push(module)
				appMenuItems.push({
							id : module.id,
							title : module.title,
							text : module.title,
							icon : module.icon
						})
			}
		}
		
		var bd = res.json
		var exContext = {
			childrenRegisterAge : bd.childrenRegisterAge,
			childrenDieAge : bd.childrenDieAge,
			oldPeopleAge : bd.oldPeopleAge,
			hypertensionMode : bd.hypertensionMode,
			diabetesMode : bd.diabetesMode,
			pregnantMode : bd.pregnantMode,
			oldPeopleMode : bd.oldPeopleMode,
			diabetesPrecedeDays : bd.diabetesPrecedeDays,
			diabetesDelayDays : bd.diabetesDelayDays,
			areaGridType : bd.areaGridType,
			areaGridShowType : bd.areaGridShowType,
			healthCheckType : bd.healthCheckType,
			hypertensionType : bd.hypertensionType,
			diabetesType : bd.diabetesType,
			psychosisType : bd.psychosisType,
			oldPeopleStartMonth : bd.oldPeopleStartMonth || '01',
			hypertensionStartMonth : bd.hypertensionStartMonth || '01',
			hypertensionRiskStartMonth : bd.hypertensionRiskStartMonth ||'01',
			diabetesStartMonth : bd.diabetesStartMonth || '01',
			diabetesRiskStartMonth : bd.diabetesRiskStartMonth || '01',
			psychosisStartMonth : bd.psychosisStartMonth || '01',
			tumourHighRiskStartMonth : bd.tumourHighRiskStartMonth || '01',
			tumourPatientVisitStartMonth : bd.tumourPatientVisitStartMonth || '01'
		};

		var mainCfg = {
			title : bd.title,
			uid : bd.userId,
			psw : pwd,
			serverDate : bd.serverDate,
			serverDateTime : bd.serverDateTime,
			uname : bd.userName,
			dept : bd.dept,
			deptId : bd.deptId,
			jobtitle : bd.jobtitle,
			jobtitleId : bd.jobtitleId,
			regionCode : bd.regionCode,
			regionText : bd.regionText,
			mapSign : bd.mapSign,
			topManage : bd.topManage,
			apps : apps,
			modules : appModules,
			myPage : myPage,
			manageRule : bd.manageRule,
			fds:bd.fds,
			phisActiveYW : bd.phisActiveYW,
			phisActive : bd.phisActive,
			exContext : exContext
		}
		// document.title = res.json.title
		var myApp = new app.viewport.App(mainCfg);
		// var cfg = {};
		// var guideModule = new app.biz.conf.guide.SystemGuideView(cfg);
		// var win = guideModule.getWin();
		// if (win) {
		// win.show();
		// }

	},

	doCanel : function() {
		if (this.mainApp) {
			this.mainApp.desktop.fireEvent("winUnlock");
		}
		this.fireEvent("logonCancel");
		window.location.reload();
		return true;
	}

});

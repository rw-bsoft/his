$package("phis.application.top.script")

$import("phis.application.top.script.PublicSwitchBaseList")
phis.application.top.script.DepartmentSwitchList = function(cfg) {
	// cfg.width = 500;
	// cfg.height = 300;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	cfg.autoLoadData = false;
	cfg.showWinOnly = true;
	cfg.modal = true;
	phis.application.top.script.DepartmentSwitchList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow);
}

Ext.extend(phis.application.top.script.DepartmentSwitchList,
		phis.application.top.script.PublicSwitchBaseList, {
			onWinShow : function() {
				this.initCnd = this.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.YGDM'], ['s', this.mainApp.uid]],
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]]],
						['eq', ['$', 'a.YWLB'], ['s', this.departmentType]]];
				this.refresh();
			},
			getTagName : function() {
				return this.departmentType == 4 ? "病区" : "科室";
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.MRBZ == 1) {

					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/yes.png'/>";
				} else {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/no.png'/>";
				}
			},
			onDblClick : function(grid, index, e) {
				// 选择当前药房
				var r = grid.getSelectionModel().getSelected();
				// 选择默认科室，直接隐藏窗口
				if (r.get("MRBZ") == 1) {
					this.getWin().hide();
					return;
				}
				// 发起请求，修改默认科室
				phis.script.rmi.jsonRequest({
					serviceId : "permissionsVerifyService",
					serviceAction : "saveDefaultDepartment",
					body : r.data
						// 增加module的id
					}, function(code, msg, json) {
					this.unmask()
					if (code > 200) {
						alert("设置" + this.getTagName() + "失败，请联系管理员!");
						return;
					} else {
						var ksxx = json.ksxx;
						var exCfg = this.mainApp.taskManager.tasks
									.item(userDomain)
						if (this.departmentType == '2') {
							if (exCfg) {
								exCfg.departmentId = ksxx.ID;
								exCfg.departmentName = ksxx.OFFICENAME;
								exCfg.reg_departmentId = ksxx.REG_KSDM;
								exCfg.reg_departmentName = ksxx.REG_KSMC;
							}
							this.mainApp['phis'].departmentId = ksxx.ID;
							this.mainApp.departmentName = ksxx.OFFICENAME;
							this.mainApp['phis'].reg_departmentId = ksxx.REG_KSDM;
							this.mainApp['phis'].reg_departmentName = ksxx.REG_KSMC;

							var img_root = ClassLoader.appRootOffsetPath
									+ "resources/css/app/desktop/images/";
							var reg_departmentName = "<img src='" + img_root
									+ "ks.png' border=0 />";
							reg_departmentName += this.mainApp['phis'].reg_departmentName
									? this.mainApp['phis'].reg_departmentName
									: "选择" + this.getTagName();
							if (this.opener && this.opener.initWelComePanel) {
								this.opener.initWelComePanel();
							}
						} else if (this.departmentType == '4') {
							if (exCfg) {
								exCfg.wardId = ksxx.REG_KSDM;
								exCfg.wardName = ksxx.REG_KSMC;
							}
							this.mainApp['phis'].wardId = ksxx.REG_KSDM;
							this.mainApp['phis'].wardName = ksxx.REG_KSMC;
							var img_root = ClassLoader.appRootOffsetPath
									+ "resources/css/app/desktop/images/";
							var wardName = "<img src='" + img_root
									+ "bq.png' border=0 />";
							wardName += this.mainApp['phis'].wardName
									? this.mainApp['phis'].wardName
									: "选择" + this.getTagName();
							if (this.opener && this.opener.initWelComePanel) {
								this.opener.initWelComePanel();
							}
						}
						this.getWin().hide();
						MyMessageTip.msg("提醒", this.getTagName() + "设置成功，当前"
										+ this.getTagName() + "为【"
										+ ksxx.REG_KSMC + "】", true);
						var length = this.opener.mainTab.items.length;
						for (var i = 0; i < length; i++) {
							var act = this.opener.mainTab.getItem(i);
							if (act && act._mId) {
								this.opener.mainTab.remove(act);
								i--;
								length--;
							}
						}
					}
				}, this)
			}
		})
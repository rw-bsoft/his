$package("phis.application.top.script")

$import("phis.application.top.script.PublicSwitchBaseList")

phis.application.top.script.MedicalSwitchList = function(cfg) {
	// cfg.width = 400;
	// cfg.height = 250;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	cfg.autoLoadData = false;
	cfg.showWinOnly = true;
	cfg.modal = true;
	phis.application.top.script.MedicalSwitchList.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow);
}

Ext.extend(phis.application.top.script.MedicalSwitchList,
		phis.application.top.script.PublicSwitchBaseList, {
			// expansion : function(cfg) {
			// cfg.viewConfig.emptyText = "<font color='red'
			// style='font-size:12'>对不起，您还未设置任何可切换"
			// + this.getTagName() + ",请与管理员联系!</font>";
			// },
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
				return "医技科室";
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.MRBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/yes.png' />";
				} else {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/no.png' />";
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
						if (exCfg) {
							exCfg.MedicalId = ksxx.ID;
							exCfg.MedicalName = ksxx.OFFICENAME;
						}
						this.mainApp['phis'].MedicalId = ksxx.ID;
						this.mainApp['phis'].MedicalName = ksxx.OFFICENAME;

						var img_root = ClassLoader.appRootOffsetPath
								+ "resources/css/app/desktop/images/";
						var MedicalName = "<img src='" + img_root
								+ "yj.png' border=0 />";
						MedicalName += this.mainApp['phis'].MedicalName
								? this.mainApp['phis'].MedicalName
								: "选择" + this.getTagName();
						if (this.opener && this.opener.initWelComePanel) {
							this.opener.initWelComePanel();
						}
						this.getWin().hide();
						MyMessageTip.msg("提醒", this.getTagName() + "设置成功，当前"
										+ this.getTagName() + "为【"
										+ ksxx.OFFICENAME + "】", true);
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
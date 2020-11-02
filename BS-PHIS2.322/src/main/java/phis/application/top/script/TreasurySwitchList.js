$package("phis.application.top.script")

$import("phis.application.top.script.PublicSwitchBaseList")

phis.application.top.script.TreasurySwitchList = function(cfg) {
	// cfg.width = 500;
	// cfg.height = 300;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	cfg.autoLoadData = false;
	cfg.showWinOnly = true;
	cfg.modal = true;
	phis.application.top.script.TreasurySwitchList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow);
}

Ext.extend(phis.application.top.script.TreasurySwitchList,
		phis.application.top.script.PublicSwitchBaseList, {
			expansion : function(cfg) {
				cfg.viewConfig.emptyText = "<font color='red' style='font-size:14'>对不起，您还未设置任何可切换库房,请与管理员联系!</font>";
			},
			onWinShow : function() {
				var cnd = ['eq', ['$', "b.KFZT"], ['i', 1]];
				this.requestData.cnd = [
						'and',
						[
								'and',
								[
										'and',
										['eq', ['$', 'a.YGDM'],
												['s', this.mainApp.uid]],
										['eq', ['$', 'a.JGID'],
												['s', this.mainApp['phisApp'].deptId]]],
								['eq', ['$', 'a.YWLB'],
										['s', this.departmentType]]], cnd];
				this.refresh();
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.MRBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/yes.png' />";
				} else {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/no.png'/>";
				}
			},
			onDblClick : function(grid, index, e) {
				// 选择当前药房
				var r = grid.getSelectionModel().getSelected();
				// 选择默认药房，直接隐藏窗口
				if (r.get("MRBZ") == 1) {
					this.getWin().hide();
					return;
				}
				// 发起请求，修改默认药房
				phis.script.rmi.jsonRequest({
					serviceId : "permissionsVerifyService",
					serviceAction : "saveDefaultDepartment",
					body : r.data
						// 增加module的id
					}, function(code, msg, json) {
					this.unmask()
					if (code > 200) {
						alert("设置库房失败，请联系管理员！");
						return;
					} else {
						var ksxx = json.ksxx;
						var exCfg = this.mainApp.taskManager.tasks
								.item(userDomain)
						if (exCfg) {
							exCfg.treasuryId = ksxx.KFXH;
							exCfg.treasuryName = ksxx.KFMC;
							exCfg.treasuryEjkf = ksxx.EJKF;
							exCfg.treasuryLbxh = ksxx.LBXH;
							exCfg.treasuryKflb = ksxx.KFLB;
							exCfg.treasuryKfzt = ksxx.KFZT;
							exCfg.treasuryKfzb = ksxx.KFZB;
							exCfg.treasuryGlkf = ksxx.GLKF;
							exCfg.treasuryWxkf = ksxx.WXKF;
							exCfg.treasuryCkfs = ksxx.CKFS;
							exCfg.treasuryCsbz = ksxx.CSBZ;
							exCfg.treasuryZjbz = ksxx.ZJBZ;
							exCfg.treasuryZjyf = ksxx.ZJYF;
							exCfg.treasuryHzpd = ksxx.HZPD;
							exCfg.treasuryPdzt = ksxx.PDZT;
						}
						this.mainApp['phis'].treasuryId = ksxx.KFXH;
						this.mainApp['phis'].treasuryName = ksxx.KFMC;
						this.mainApp['phis'].treasuryEjkf = ksxx.EJKF;
						this.mainApp['phis'].treasuryLbxh = ksxx.LBXH;
						this.mainApp['phis'].treasuryKflb = ksxx.KFLB;
						this.mainApp['phis'].treasuryKfzt = ksxx.KFZT;
						this.mainApp['phis'].treasuryKfzb = ksxx.KFZB;
						this.mainApp['phis'].treasuryGlkf = ksxx.GLKF;
						this.mainApp['phis'].treasuryWxkf = ksxx.WXKF;
						this.mainApp['phis'].treasuryCkfs = ksxx.CKFS;
						this.mainApp['phis'].treasuryCsbz = ksxx.CSBZ;
						this.mainApp['phis'].treasuryZjbz = ksxx.ZJBZ;
						this.mainApp['phis'].treasuryZjyf = ksxx.ZJYF;
						this.mainApp['phis'].treasuryHzpd = ksxx.HZPD;
						this.mainApp['phis'].treasuryPdzt = ksxx.PDZT;

						var img_root = ClassLoader.appRootOffsetPath
								+ "resources/css/app/desktop/images/";
						var treasuryName = "<img src='" + img_root
								+ "kf.png' border=0 />";
						treasuryName += this.mainApp['phis'].treasuryName
								? this.mainApp['phis'].treasuryName
								: "选择库房";
						var reg_treasuryName = "<img src='" + img_root
								+ "kf.png' border=0 />";
						if (this.opener && this.opener.initWelComePanel) {
							this.opener.initWelComePanel();
						}
						this.getWin().hide();

						MyMessageTip.msg("提醒", "库房设置成功，当前库房为【" + ksxx.KFMC
										+ "】", true);
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
$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.pub.PublicSwitchBaseList")

com.bsoft.phis.pub.WindowSwitchList = function(cfg) {
	// cfg.width = 500;
	// cfg.height = 300;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	cfg.autoLoadData = false;
	cfg.showWinOnly = true;
	cfg.modal = true;
	com.bsoft.phis.pub.WindowSwitchList.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow);
}

Ext.extend(com.bsoft.phis.pub.WindowSwitchList, com.bsoft.phis.pub.SimpleList,
		{
			expansion : function(cfg) {
				cfg.viewConfig.emptyText = "<font color='red' style='font-size:14'>对不起，您还未设置任何可切换窗口,请与管理员联系!</font>";
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
					+ "resources/phis/resources/images/yes.png'/>";
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
						alert("设置" + (this.departmentType == '1' ? "药房" : "药库")
								+ "失败，请联系管理员！");
						return;
					} else {
						if (this.departmentType == '1') {
							var ksxx = json.ksxx;
							this.mainApp['phis'].pharmacyId = ksxx.YFSB;
							this.mainApp['phis'].pharmacyName = ksxx.YFMC;
							var img_root = ClassLoader.appRootOffsetPath
									+ "resources/css/app/desktop/images/";
							var pharmacyName = "<img src='" + img_root
									+ "yf.png' border=0 />";
							pharmacyName += this.mainApp['phis'].pharmacyName
									? this.mainApp['phis'].pharmacyName
									: "选择药房";
							var reg_departmentName = "<img src='" + img_root
									+ "yf.png' border=0 />";
							document.getElementById("top_pharmacyName").innerHTML = pharmacyName;
							document.getElementById("mini_top_pharmacyName").innerHTML = pharmacyName;
							this.getWin().hide();

							MyMessageTip.msg("提醒", "药房设置成功，当前药房为【" + ksxx.YFMC
											+ "】", true);
						}
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
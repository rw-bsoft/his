/**
 * 采购入库左边list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleLeftList");

phis.application.sto.script.StorehouseUndeterminedCheckInList = function(cfg) {
	cfg.cnds = ['ne', ['$', 'RKPB'], ['i', 1]];
	phis.application.sto.script.StorehouseUndeterminedCheckInList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseUndeterminedCheckInList,
		phis.application.sto.script.StorehouseMySimpleLeftList, {
			lock : function(r) {
				var p = {};
				p.YWXH = '1023';
				p.SDXH = r.data.XTSB + '-' + r.data.RKFS + '-' + r.data.RKDH;
				return this.bclLock(p);
			},
			// 删除,退回操作
			unlock : function(r) {
				var p = {};
				p.YWXH = '1023';
				p.SDXH = r.data.XTSB + '-' + r.data.RKFS + '-' + r.data.RKDH;
				this.bclUnlock(p);
			},
			// 下拉框查询条件
			getDicFitle : function() {
				return [
						'and',
						['eq', ['$', 'item.properties.XTSB'],
								['l', this.mainApp['phis'].storehouseId]],
						['eq', ['$', 'item.properties.DYFS'], ['i', 1]]];
			},
			// 页面刷新
			doRefreshWin : function() {
				if (this.selectValue) {
					var addCnd = ['eq', ['$', 'RKFS'], ['i', this.selectValue]];
					this.requestData.cnd = ['and', addCnd, this.cnds];
					this.refresh();
					return;
				}
			},
			// 获取新打开界面参数
			getInitDataBody : function(r) {
				var initDataBody = {};
				initDataBody["XTSB"] = r.data.XTSB;
				initDataBody["RKFS"] = r.data.RKFS;
				initDataBody["RKDH"] = r.data.RKDH;
				return initDataBody;
			},
			doPrint : function() {
				var module = this.createModule("storehouseinprint",
						this.readRef)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
					return;
				}
				module.xtsb = r.data.XTSB;
				module.rkfs = r.data.RKFS;
				module.rkdh = r.data.RKDH;
				module.pwd = r.data.PWD;
				module.fdjs = r.data.FDJS;
				module.initPanel();
				module.doPrint();
			},
			doDownloads : function() {
				var module = this.createModule("downloads",this.downloadsRef);
				Ext.apply(module.exContext, this.exContext);;
				module.opener=this;
				var win = module.getWin();
				win.add(module.initPanel());
				win.show()
				win.center()
			}
		});
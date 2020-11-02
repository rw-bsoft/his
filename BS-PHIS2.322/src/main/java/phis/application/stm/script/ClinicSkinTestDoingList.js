$package("phis.application.stm.script")

$import("phis.script.SimpleList")

phis.application.stm.script.ClinicSkinTestDoingList = function(cfg) {
	cfg.initCnd = ['eq', ['$', 'a.WCBZ'], ['i', 9]];
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = false;
	phis.application.stm.script.ClinicSkinTestDoingList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadData, this)
}
Ext.extend(phis.application.stm.script.ClinicSkinTestDoingList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				delete cfg.tbar;
			},
			onReady : function() {
				phis.application.stm.script.ClinicSkinTestDoingList.superclass.onReady
						.call(this);
				this.startTiming();
			},
			startTiming : function() {
				var task = {
					run : function() {
						for (var i = 0; i < this.store.getCount(); i++) {
							var r = this.store.getAt(i);
							var curTime = r.get("PSSJ_Current") + 1;
							if (curTime == r.get("PSSJ_Total") * 60) {
								ymPrompt.alert({
											message : '病人[' + r.get("BRXM")
													+ ']的皮试时间已到,请及时处理!',
											title : '提醒-'
													+ Date.getServerDateTime(),
											winPos : 'rb',
											showMask : false,
											useSlide : true
										})
							}
							r.set("PSSJ_Current", curTime);
						}
					},
					interval : 1000,
					scope : this
				}
				var runner = new Ext.util.TaskRunner();
				runner.start(task);
				this.runner = runner;
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				this.fireEvent('skDblclick', r)
			},
			curentTime : function(v, p, r) {
				var fmtV;
				if (v >= 60) {
					fmtV = parseInt(v / 60) + '分' + v % 60 + "秒"
				} else {
					fmtV = v + "秒"
				}
				if (v >= (r.get("PSSJ_Total")) * 60) {
					p.style = 'color:#FF3467;font-weight:bold;text-align:right;font-size:14px;';
				} else {
					p.style = 'color:#00AA00;text-align:right;font-size:14px;';
				}
				return fmtV;
			},
			afterLoadData : function(store) {
				var now = Date.parseDate(Date.getServerDateTime(),
						'Y-m-d H:i:s').getTime();
				for (var i = 0; i < store.getCount(); i++) {
					store.getAt(i).set("PSSJ_Total",
							this.systemParams.PSSJ || 20);
					var pssj = Date.parseDate(store.getAt(i).get("PSSJ"),
							'Y-m-d H:i:s').getTime()
					// MyMessageTip.msg("提示", now + ":" + pssj)
					store.getAt(i).set("PSSJ_Current", (now - pssj) / 1000);
				}
				store.commitChanges();
			}
		});
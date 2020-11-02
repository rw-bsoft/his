$package("phis.application.hos.script");

$import("phis.script.SimpleModule");
phis.application.hos.script.HospitalAntibacterialAduitModule = function(cfg) {
	phis.application.hos.script.HospitalAntibacterialAduitModule.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(phis.application.hos.script.HospitalAntibacterialAduitModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				this.exContext = {};
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 80,
										items : this.getQueryForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRecordsList()
									}]
						});
				this.panel = panel;
				// this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				this.doQuery();
			},
			getQueryForm : function() {
				var module = this.createModule("refQueryForm",
						this.refQueryForm);
				module.opener = this.opener;
				this.form = module;
				var qForm = module.initPanel();
				this.qForm = qForm;
				return qForm;
			},
			getRecordsList : function() {
				var module = this.createModule("refAntibacterialAduisList",
						this.refAntibacterialAduisList);
				module.initCnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]],
								['eq', ['$', 'a.DJZT'], ['s', '1']]],
						['eq', ['$', 'a.JZLX'], ['i', '1']]];
				module.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]],
								['eq', ['$', 'a.DJZT'], ['s', '1']]],
						['eq', ['$', 'a.JZLX'], ['i', '1']]];
				module.opener = this;
				this.list = module;
				var grid = module.initPanel();
				this.grid = grid;
				return grid;
			},
			afterOpen : function() {
				this.doQuery();
			},
			doQuery : function() {
				var BRKS = this.qForm.getForm().findField("BRKS").getValue();
				var DJZT = this.qForm.getForm().findField("DJZT").getValue();
				var SPJG = this.qForm.getForm().findField("SPJG").getValue();
				var JZLX = this.list.JZLX || 1;
				var SQRQBegin, SQRQEnd;
				var fb = this.qForm.getForm().findField("SQRQBegin");
				if (fb) {
					SQRQBegin = fb.getValue();
				}
				var fe = this.qForm.getForm().findField("SQRQEnd");
				if (fe) {
					SQRQEnd = fe.getValue();
				}
				if (SQRQBegin && SQRQEnd) {
					if (Date.parseDate(SQRQBegin, "Y-m-d") > Date.parseDate(
							SQRQEnd, "Y-m-d")) {
						Ext.Msg.alert("提示ʾ", "开始时间不能大于结束时间!");
						return;
					}
				}
				var cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]],
								['eq', ['$', 'a.ZFBZ'], ['i', 0]]],
						['eq', ['$', 'a.JZLX'], ['i', JZLX]]];
				if (BRKS && JZLX == 1) {
					cnd = ['and', cnd, ['eq', ['$', 'a.BRKS'], ['s', BRKS]]];
				}
				if (DJZT) {
					cnd = ['and', cnd,
							['eq', ['$', 'a.DJZT'], ['i', parseInt(DJZT)]]];
				} else {
					// cnd = ['and', cnd, ['eq', ['$', 'a.DJZT'], ['i', 1]]];
				}
				if (SPJG) {
					cnd = ['and', cnd,
							['eq', ['$', 'a.SPJG'], ['i', parseInt(SPJG)]]];
				}
				if (SQRQBegin) {
					cnd = [
							'and',
							cnd,
							[
									'ge',
									['$', 'a.SQRQ'],
									['todate', ['s', SQRQBegin],
											['s', 'yyyy-mm-dd']]]];
				}
				if (SQRQEnd) {
					cnd = [
							'and',
							cnd,
							[
									'le',
									['$', 'a.SQRQ'],
									['todate', ['s', SQRQEnd],
											['s', 'yyyy-mm-dd']]]];
				}
				this.list.initCnd = cnd;
				this.list.requestData.cnd = cnd;
				this.list.loadData();
			},
			doReset : function() {
				this.qForm.getForm().reset();
				this.qForm.getForm().findField("DJZT").setValue("1");
			},
			doCancel : function() {
				this.opener.closeCurrentTab();
			}
		});
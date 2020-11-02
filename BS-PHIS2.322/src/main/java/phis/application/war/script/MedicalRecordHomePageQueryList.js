$package("phis.application.war.script");

$import("phis.script.SimpleList");

phis.application.war.script.MedicalRecordHomePageQueryList = function(cfg) {
cfg.enableRowBody = true;
	cfg.headerGroup = true;
	cfg.modal = true;
	this.serverParams = {
			serviceAction : cfg.serviceAction
		}
	phis.application.war.script.MedicalRecordHomePageQueryList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.war.script.MedicalRecordHomePageQueryList, phis.script.SimpleList, {
	initPanel : function(sc) {
		var grid = phis.application.war.script.MedicalRecordHomePageQueryList.superclass.initPanel
			.call(this, sc);
			var kssj = new Date();
				if (this.dateFieldks) {
					kssj = this.dateFieldks.getValue();
				};
				var jssj = new Date();
				if (this.dateFieldjs) {
					jssj = this.dateFieldjs.getValue() + " 23:59:59";
				};
				this.requestData.cnd = [
							'and',
							[
							'and',
							['ge', ['$', 'a.cyrq'],
									['todate', ['s',kssj],['s','YYYY-MM-DD']]],
									['or',['le', ['$', 'a.cyrq'],
											['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
							['eq', ['$', "a.jgid"],['$', '%user.manageUnit.id']]]];
							this.initCnd = [
								'and',
								[
								'and',
								['ge', ['$', 'a.cyrq'],
								 	['todate', ['s',kssj],['s','YYYY-MM-DD']]],
								 	['or',['le', ['$', 'a.cyrq'],
											['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
								['eq', ['$', "a.jgid"],['$', '%user.manageUnit.id']]]];
		return grid;
	},
	expansion : function(cfg) {
		this.dateFieldks = new Ext.ux.form.Spinner({
							name : 'storeDate',
							value : this.mainApp.serverDate,
							strategy : {
								xtype : "date"
							}
						});
				this.label = new Ext.form.Label({
							text : "-至-"
						});
				this.dateFieldjs = new Ext.ux.form.Spinner({
							name : 'storeDate',
							value : this.mainApp.serverDate,
							strategy : {
								xtype : "date"
							}
						});
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([ '出院时间',this.dateFieldks, '-', this.label, '-',
						this.dateFieldjs, tbar]);
	},
	doPrint:function(){
			   var kssj = new Date();
				if (this.dateFieldks) {
					kssj = this.dateFieldks.getValue();
				};
				var jssj = new Date();
				if (this.dateFieldjs) {
				jssj = this.dateFieldjs.getValue();
				};
				var pages="phis.prints.jrxml.MedicalRecordHomePageQuery";
				var url="resources/"+pages+".print?type=3&kssj="+kssj+"&jssj="+jssj;
				if(this.flag)
				{
					url+="&flag=1";
				}
				var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			doQuery : function() {
				var kssj = new Date();
				if (this.dateFieldks) {
					kssj = this.dateFieldks.getValue();
				};
				var jssj = new Date();
				if (this.dateFieldjs) {
					jssj = this.dateFieldjs.getValue() + " 23:59:59";
				};
					this.requestData.cnd = [
											'and',
											[
											'and',
											['ge', ['$', 'a.cyrq'],
													['todate', ['s',kssj],['s','YYYY-MM-DD']]],
													['or',['le', ['$', 'a.cyrq'],
															['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
											['eq', ['$', "a.jgid"],['$', '%user.manageUnit.id']]]];
								this.initCnd = [
												'and',
												[
												'and',
												['ge', ['$', 'a.cyrq'],
												 	['todate', ['s',kssj],['s','YYYY-MM-DD']]],
												 	['or',['le', ['$', 'a.cyrq'],
															['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]],['isNull',['$','a.JSSJ']]],
											['eq', ['$', "a.jgid"],['$', '%user.manageUnit.id']]]];
				this.loadData();
			}
});
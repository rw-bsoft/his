$package("phis.application.cic.script")

$import("phis.script.SimpleList","phis.script.widgets.Spinner","phis.prints.script.OutpatientWorkLogPrintView")

phis.application.cic.script.OutpatientWorkLogList = function(cfg) {
	cfg.enableRowBody = true;
	cfg.headerGroup = true;
	cfg.modal = true;
	this.serverParams = {
			serviceAction : cfg.serviceAction
		}
	phis.application.cic.script.OutpatientWorkLogList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cic.script.OutpatientWorkLogList, phis.script.SimpleList,
		{
			initPanel : function(sc) {
				// 判断是否是EMRView内部打开
				var grid = phis.application.cic.script.OutpatientWorkLogList.superclass.initPanel
						.call(this, sc);
// this.initCnd = ['and',['eq',['$','e.ZZBZ'],['s','1']],['eq', ['$', 'a.JGID'],
// ["$", "'" + this.mainApp['phisApp'].deptId + "'"]]]
// this.requestData.cnd = ['and',['eq',['$','e.ZZBZ'],['s','1']],['eq', ['$',
// 'a.JGID'],
// ["$", "'" + this.mainApp['phisApp'].deptId + "'"]]]
				
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
							['ge', ['$', 'a.KSSJ'],
									['todate', ['s',kssj],['s','YYYY-MM-DD']]],
									['le', ['$', 'a.KSSJ'],
											['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]],
							['eq', ['$', 'a.JGID'],
									["$", "'" + this.mainApp.phisApp.deptId + "'"]]]];
				this.initCnd = [
								'and',
								[
								'and',
								['ge', ['$', 'a.KSSJ'],
								 	['todate', ['s',kssj],['s','YYYY-MM-DD']]],
								 	['le', ['$', 'a.KSSJ'],
											['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]],
								['eq', ['$', 'a.JGID'],
										["$", "'" + this.mainApp.phisApp.deptId + "'"]]]];
				return grid;
			},
			jkjyRenderer : function(value, metaData, r) {
				if(value == "null"){
					return "";
				}
				return value;
			},
			expansion : function(cfg) {
				var radiogroup = [];
				var itemName = ['全院','本科室','本人'];
				this.gltj = 1;
				for (var i = 1; i < 4; i++) {
					radiogroup.push({
						xtype : "radio",
						checked : i == 1,
						boxLabel : itemName[i-1],
						inputValue : i,
						name : "gltj",
						listeners : {
							check : function(group,checked) {
								if (checked) {
									var gltj = group.inputValue;
									this.gltj = gltj;
									this.doQuery();
								}
							},
							scope : this
						}
					})
				}
				this.radiogroup = radiogroup;
				
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
				cfg.tbar.push([this.radiogroup, '-',this.dateFieldks, '-', this.label, '-',
						this.dateFieldjs, tbar]);
// cfg.tbar.push([this.dateFieldks, '-', this.label, '-',
// this.dateFieldjs, tbar]);
			},
			doPrint : function() {
				var kssj = new Date();
				if (this.dateFieldks) {
					kssj = this.dateFieldks.getValue();
				};
				var jssj = new Date();
				if (this.dateFieldjs) {
					jssj = this.dateFieldjs.getValue();
				};
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("JZXH"))
				}
				if(ids==""){
					MyMessageTip.msg('提示', '没有需要打印的记录!', true);
					return ;
				}
				var pWin = this.midiModules["OutpatientWorkLogPrintView"]
				var cfg = {
					requestData : ids,
					gltj : this.gltj,
					uid : this.mainApp.uid,
					departmentId : this.mainApp['phis'].departmentId,
					kssj : kssj,
					jssj : jssj					
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.OutpatientWorkLogPrintView(cfg)
				this.midiModules["OutpatientWorkLogPrintView"] = pWin
				pWin.getWin().show()
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
				if(this.gltj==1){
					this.requestData.cnd = [
											'and',
											[
											'and',
											['ge', ['$', 'a.KSSJ'],
													['todate', ['s',kssj],['s','YYYY-MM-DD']]],
													['le', ['$', 'a.KSSJ'],
															['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]],
											['eq', ['$', 'a.JGID'],
													["$", "'" + this.mainApp.phisApp.deptId + "'"]]]];
								this.initCnd = [
												'and',
												[
												'and',
												['ge', ['$', 'a.KSSJ'],
												 	['todate', ['s',kssj],['s','YYYY-MM-DD']]],
												 	['le', ['$', 'a.KSSJ'],
															['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]],
												['eq', ['$', 'a.JGID'],
														["$", "'" + this.mainApp.phisApp.deptId + "'"]]]];
				} else if(this.gltj==2){
					this.requestData.cnd = 
					['and',
						['and',	['ge', ['$', 'a.KSSJ'],['todate', ['s',kssj],['s','YYYY-MM-DD']]],
						['le', ['$', 'a.KSSJ'],
								['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
						['and', ['eq', ['$', 'a.JGID'],["$", "'" + this.mainApp.phisApp.deptId + "'"]],
								['eq', ['$', 'a.KSDM'],['$', "'" + this.mainApp['phis'].departmentId + "'"]]]
						
					];
					this.initCnd = 
					['and',
						['and',	['ge', ['$', 'a.KSSJ'],['todate', ['s',kssj],['s','YYYY-MM-DD']]],
						 ['le', ['$', 'a.KSSJ'],
								['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
						['and', ['eq', ['$', 'a.JGID'],["$", "'" + this.mainApp.phisApp.deptId + "'"]],
								['eq', ['$', 'a.KSDM'],['$', "'" + this.mainApp['phis'].departmentId + "'"]]]
						
					];
				} else if(this.gltj==3){
					this.requestData.cnd = 
					['and',
						['and',	['ge', ['$', 'a.KSSJ'],['todate', ['s',kssj],['s','YYYY-MM-DD']]],
						['le', ['$', 'a.KSSJ'],
								['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
						['and', ['eq', ['$', 'a.JGID'],["$", "'" + this.mainApp.phisApp.deptId + "'"]],
								['eq', ['$', 'a.YSDM'],['$', "'" + this.mainApp.uid + "'"]]]
						
					];
					this.initCnd = 
					['and',
						['and',	['ge', ['$', 'a.KSSJ'],['todate', ['s',kssj],['s','YYYY-MM-DD']]],
						['le', ['$', 'a.KSSJ'],
								['todate', ['s',jssj],['s','YYYY-MM-DD HH24:MI:SS']]]],
						['and', ['eq', ['$', 'a.JGID'],["$", "'" + this.mainApp.phisApp.deptId + "'"]],
								['eq', ['$', 'a.YSDM'],['$', "'" + this.mainApp.uid + "'"]]]
						
					];
				}
				
				this.loadData();
			},
			onRenderer : function(value, metaData, r) {
				var age;
				var aDate=new Date();
				var thisYear=parseInt(aDate.getFullYear());
				var thisMonth=parseInt(aDate.getMonth()+1);
				var brith=r.data.CSNY;
				if(brith){
				var brithy=parseInt(brith.substring(0,4));
				var brithm=parseInt(brith.substring(5,7));
			       	if(thisYear-brithy<3){
		           		if(thisMonth-brithm>=0){
		                	age = (thisYear-brithy)+'岁'+(thisMonth-brithm)+'个月'
		              	}else{
		            		age = thisYear-brithy-1+'岁'+(12-brithm+thisMonth)+'个月';
		              	}
			       	}else{
		            	if(thisMonth-brithm>=0){
		              		age = thisYear-brithy;
		              	} else {
		              		age = thisYear-brithy-1;
		              	}
			       	}
				}
				return age;
			}
		});
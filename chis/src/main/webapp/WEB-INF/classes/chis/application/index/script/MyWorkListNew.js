/**
 * 首页任务列表
 * 
 * @author : yaozh
 */
$package("chis.application.index.script");

$import("chis.script.BizSimpleListView");

chis.application.index.script.MyWorkListNew = function(cfg) {
	cfg.hideHeaders = true;
	chis.application.index.script.MyWorkListNew.superclass.constructor.apply(this, [cfg]);
	this.showButtonOnTop = false;
};

Ext.extend(chis.application.index.script.MyWorkListNew, chis.script.BizSimpleListView, {
	
	loadData : function(){
		chis.application.index.script.MyWorkListNew.superclass.loadData.call(this);
	},

			onDblClick : function() {
				var r = this.getSelectedRecord();
				var workType = r.get("workType");
				var module;
				var lo = {}
				var initCnd;
				switch (workType) {
					case "01" :
						//module = "chis.application.hr.HR/HR/B08";
						lo.moduleId = "A04_1"  
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "HR" 
						break;
					case "02" :
					    lo.moduleId = "A05"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "HY",
						initCnd = ['and',['eq',['$','d.manaUnitId'],['s',this.mainApp.deptId]],['eq',['$','d.workType'],['s','02']]]
						break;
					case "03" :
					    lo.moduleId = "R01"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "MOV"
						initCnd = this.getMovEhrInitCnd();
						break;
					case "04" :
						lo.moduleId = "R02"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "MOV"
						initCnd = this.getMovCdhInitCnd();
						break;
					case "05" :
						lo.moduleId = "R03"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "MOV"
						initCnd = this.getMovMhcInitCnd();
						break;
					case "06" :
						lo.moduleId = "R04"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "MOV"
						initCnd = this.getMovBatchInitCnd();
						break;
					case "07" :
						lo.moduleId = "R05"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "MOV"
						initCnd = this.getMovManageInfoInitCnd();
						break;
					case "08" :
						lo.moduleId = "A06_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "PSY"
						initCnd=['and',['eq',['$','a.status'],['s','0']],['eq',['$','d.workType'],['s','08']],['eq',['$','d.doctorId'],['s',this.mainApp.uid||'']]]
						break;
					case "09" :
						lo.moduleId = "A07_1"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "HR"
						break;
					case "10" :
						lo.moduleId = "DEF01_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "11" :
						lo.moduleId = "DEF02_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "12" :
						lo.moduleId = "DEF03_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "13" :
						lo.moduleId = "DEF01_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "14" :
						lo.moduleId = "DEF02_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "15" :
						lo.moduleId = "DEF03_1"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DEF"
						initCnd = this.getDefInitCnd(workType)
						break;
					case "16" :
						lo.moduleId = "HE02"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "HER"
						initCnd = this.getPlanExeInitCnd();
						break;
					case "17" :
						lo.moduleId = "D20_1"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "HR"
						initCnd = this.getInitCnd(workType,"healthCheck");
						break;
					case "18" :
					    lo.moduleId = "R04"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "WL" 
						break;
					case "19" :
					    lo.moduleId = "D22"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DBS"
							initCnd =	['and',["eq", ["$", "a.doctorId"], ["s", this.mainApp.uid]],["eq", ["$", "a.workType"], ["s", workType]]]
						break;
					case "20" :
					    lo.moduleId = "D23"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "DBS" 
						initCnd =	['and',["eq", ["$", "a.doctorId"], ["s", this.mainApp.uid]],["eq", ["$", "a.workType"], ["s", workType]]]
						break;
					case "21" :
					    lo.moduleId = "R04"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "WL" 
						break;
					case "22" :
					    lo.moduleId = "C22"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "HY" 
						initCnd =	['and',["eq", ["$", "a.doctorId"], ["s", this.mainApp.uid]],["eq", ["$", "a.workType"], ["s", workType]]]
						break;
					case "23" :
					    lo.moduleId = "C23"
						lo.appId = "chis.application.diseasemanage.DISEASEMANAGE"
						lo.catalogId = "HY" 
						break;
					case "25" :
					    lo.moduleId = "R04"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "WL" 
						break;
					case "26" :
					    lo.moduleId = "A01"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "WL" 
					    break;
					case "27" :
					    lo.moduleId = "A01"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "WL" 
						break;
					case "28" ://老年人体检跳转
					    lo.moduleId = "HC20"
						lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
						lo.catalogId = "HR" 
						break;
				}
				if (lo) {
					debugger;
					this.openWindow(lo, initCnd);
				}
			},
			openWindow : function(lo, initCnd) {
				debugger;
				this.fireEvent("openWin",lo, true, true, initCnd); 
			},
			getPlanExeInitCnd : function() {
				var cnd = [0];
				return cnd;
			},
			getInitCnd : function(workType,pkey){
			  		var df = "yyyy-MM-dd HH24:mi:ss";
			  		var keyCnd = ['eq', ['$', 't.recordId'], ['$', 'a.'+pkey]]
			  		var subCnd = ['and', 
								keyCnd,
								['eq', ['$', 't.workType'], ['s', workType]], 
								['eq', ['$', 't.doctorId'], ['$', '\'' + (this.mainApp.fd || this.mainApp.uid) + '\'']],
								['le',['$','t.beginDate'],['todate',['s',this.mainApp.serverDate],['s',df]]],
								['ge',['$','t.endDate'],['todate',['s',this.mainApp.serverDate],['s',df]]]
								];
				var cnd = ["exists", ["$", "chis.application.pub.schemas.PUB_WorkList t"], subCnd];
			  	return cnd;
			  },
			getMovEhrInitCnd : function(){
				var cnd = ['and',
								['eq', ['$', 'a.status'], ['s', '1']],
								['or',
									['and',
										['eq',['$','a.moveType'],['s','1']],
										['like',['$','a.sourceUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
									],
									['and',
										['eq',['$','a.moveType'],['s','2']],
										['like',['$','a.targetUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
									]
								]
							];
				return cnd;
			},
      
        getMovCdhInitCnd : function() {
	        var cnd = ['and',
	                ['eq', ['$', 'a.status'], ['s', '1']],
	                ['or',
	                  ['and',
	                    ['eq',['$','a.moveType'],['s','1']],
	                    ['like',['$','a.sourceManaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
	                  ],
	                  ['and',
	                    ['eq',['$','a.moveType'],['s','2']],
	                    ['like',['$','a.targetManaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
	                  ]
	                ]
	              ];
          return cnd;
      },
      
      getMovMhcInitCnd : function() {
          var cnd = ['and',
                  ['eq', ['$', 'a.status'], ['s', '1']],
                  ['or',
                    ['and',
                      ['eq',['$','a.moveType'],['s','1']],
                      ['like',['$','a.sourceManaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
                    ],
                    ['and',
                      ['eq',['$','a.moveType'],['s','2']],
                      ['like',['$','a.targetManaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
                    ]
                  ]
                ];
          return cnd;
      },
      
      getMovBatchInitCnd : function() {
          var cnd = ['and',
                  ['eq', ['$', 'a.status'], ['s', '1']],
                  ['like',['$','a.applyUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
                ];
          return cnd;
      },
      
       getMovManageInfoInitCnd : function() {
          var cnd = ['and',
                  ['eq', ['$', 'a.status'], ['s', '1']],
                  ['like',['$','a.applyUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
                ];
          return cnd;
      },
      getDefInitCnd:function(workType){
      	var df = "yyyy-MM-dd HH24:mi:ss";
      	var keyCnd = ['eq', ['$', 't.recordId'], ['$', 'a.id']]
		var subCnd = ['and', 
						keyCnd,
						['eq', ['$', 'workType'], ['s', workType]], 
						['eq', ['$', 't.doctorId'], ['$', '\'' + this.mainApp.uid + '\'']]
						['le',['$','t.beginDate'],['todate',['s',this.mainApp.serverDate],['s',df]]],
						['ge',['$','t.endDate'],['todate',['s',this.mainApp.serverDate],['s',df]]]
						];
		var cnd = ["exists", ["$", "chis.application.pub.schemas.PUB_WorkList t"], subCnd];
      	return cnd;
      }
});
$package("chis.application.scm.sr.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper","chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule")

chis.application.scm.sr.script.SrContractRecordListView = function(cfg) {
	cfg.autoLoadSchema=false;
//	cfg.autoLoadData=false;
//	cfg.disableContextMenu=true;
	cfg.selectFirst = true;
	chis.application.scm.sr.script.SrContractRecordListView.superclass.constructor
			.apply(this, [cfg]);	
}

Ext.extend(chis.application.scm.sr.script.SrContractRecordListView,
		chis.script.BizSimpleListView, {
	  initPanel : function(sc) {
	        if (this.grid) {
	            if (!this.isCombined) {
	                this.fireEvent("beforeAddToWin", this.grid)
	                this.addPanelToWin();
	            }
	            return this.grid;
	        }
	        var dic = this.CHISServiceDic;
    		if (!dic) {
    			dic = util.dictionary.DictionaryLoader.load({
    				id: 'chis.dictionary.CHISService'
    			})
    			this.CHISServiceDic = dic;
    		}
	        var schema = sc
	        if (!schema) {
	            var re = util.schema.loadSync(this.entryName)
	            if (re.code == 200) {
	                schema = re.schema;
	            } else {
	                this.processReturnMsg(re.code, re.msg, this.initPanel)
	                return;
	            }
	        }

	        var items = schema.items
	        if (!items) {
	            return;
	        }

	        this.store = this.getStore(items)
	        this.cm = new Ext.grid.ColumnModel(this.getCM(items))

	        var cfg = {
	            border : false,
	            store : this.store,
	            cm : this.cm,
	            height : this.height,
	            loadMask : {
	                msg : '正在加载数据...',
	                msgCls : 'x-mask-loading'
	            },
	            buttonAlign : 'center',
	            clicksToEdit : true,
	            frame : true,
	            plugins : this.rowExpander,
	            // stripeRows : true,
	            view : this.getGroupingView()
	        }

	        if (this.gridDDGroup) {
	            cfg.ddGroup = this.gridDDGroup;
	            cfg.enableDragDrop = true
	        }
	        var cndbars = this.getCndBar(items)
	        if (!this.disablePagingTbr) {
	            cfg.bbar = this.getPagingToolbar(this.store)
	        } else {
	            cfg.bbar = this.bbar
	        }
	        if (!this.showButtonOnPT) {
	            if (this.showButtonOnTop) {
	                cfg.tbar = (cndbars.concat(this.tbar || []))
	                    .concat(this.createButtons())
	            } else {
	                cfg.tbar = cndbars.concat(this.tbar || []);
	                cfg.buttons = this.createButtons()
	            }
	        }

	        this.grid = new this.gridCreator(cfg)
	        this.schema = schema;
	        this.grid.on("afterrender", this.onReady, this)
	        this.grid.on("contextmenu", function(e) {
	            e.stopEvent()
	        })
	        this.grid.on("rowcontextmenu", this.onContextMenu, this)
	        this.grid.on("rowdblclick", this.onDblClick, this)
	        this.grid.on("rowclick", this.onRowClick, this)
	        this.grid.on("keydown", function(e) {
	            if (e.getKey() == e.PAGEDOWN) {
	                e.stopEvent()
	                this.pagingToolbar.nextPage()
	                return
	            }
	            if (e.getKey() == e.PAGEUP) {
	                e.stopEvent()
	                this.pagingToolbar.prevPage()
	                return
	            }
	        }, this)

	        if (!this.isCombined) {
	            this.fireEvent("beforeAddToWin", this.grid)
	            this.addPanelToWin();
	        }
	        return this.grid;
	    },
	    getGroupingView : function() {
	        return new Ext.grid.GroupingView({
	            showGroupName : true,
	            enableNoGroups : false,
	            hideGroupedColumn : true,
	            enableGroupingMenu : false,
	            startCollapsed : true,//开始收起所有分组
	            columnsText : "表格字段",
	            groupByText : "使用当前字段进行分组",
	            showGroupsText : "表格分组",
	            groupTextTpl : "<table cellpadding='0' cellspacing='0'><tr>"
	            + "<td width='200px' style='max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>服务包名：{[values.rs[0].data.packageName]}</td>"
	            + "<td width='100px'>(共{[values.rs.length]}项)</td></tr></table>",
	            getRowClass : this.getRowClass
	        });
	    },

	    getStore : function(items) {
	        var o = this.getStoreFields(items);
	        var reader = new Ext.data.JsonReader({
	            root : "body",
	            totalProperty : "totalCount",
	            id : o.pkey,
	            fields : o.fields
	        });
	        var url = ClassLoader.serverAppUrl || "";
	        var proxy = new Ext.data.HttpProxy({
	            url : url + "*.jsonRequest",
	            method : "post",
	            jsonData : this.requestData
	        });
	        proxy.on("loadexception", function(proxy, o, response, arg, e) {
	            if (response.status == 200) {
	                var json = eval("(" + response.responseText+ ")");
	                if (json) {
	                    var code = json["x-response-code"];
	                    var msg = json["x-response-msg"];
	                    this.processReturnMsg(code, msg,this.refresh);
	                }
	            } else {
	                this.processReturnMsg(404, "ConnectionError",this.refresh);
	            }
	        }, this);
	        var store = new Ext.data.GroupingStore({
	            reader : reader,
	            proxy : proxy,
	            sortInfo : {
	                field : "taskId",
	                direction : "ASC"
	            },
	            groupField : "SPID"
	        });
	        store.on("load",this.onStoreLoadData,this);
	        store.on("beforeload", this.onStoreBeforeLoad, this);
	        return store;
	    },
	onRowClick : function(grid, rowIndex, e) {

		this.selectedIndex = rowIndex;
		var selectRecord = grid.store.getAt(rowIndex);
		this.empiId = selectRecord.get("empiId");
		var moduleAppId = selectRecord.get("moduleAppId");
		var dic = this.CHISServiceDic;
		var n = dic.wraper[moduleAppId];
/*		if(!n){
			return MyMessageTip.msg("提示","该项目无任务路径");
		}*/
        var ehrViewAppId = n.ref.split("/");
		var status = selectRecord.get("status");
		var taskCode = selectRecord.data.taskCode;
		this.activeTab = 0;
        var planId = "";
        var year = selectRecord.data.year;
		if(moduleAppId=="JSBSF_JSBFW"){
			var sorts = selectRecord.data.sort;
			var entryNames = "PUB_VisitPlan";
            var result = util.rmi.miniJsonRequestSync({
                serviceId: "chis.psychosisVisitService",
                serviceAction: "getPsychosisVistPlanList",
                method: "execute",
                entryNames: entryNames,
                empiId: this.empiId,
				sorts:sorts,
                year:year
            });
            if(result.code>300||!result.json.flag){
                MyMessageTip.msg("提示",result.msg , true);
                return;
			}
            planId = result.json.planId;

		}
		if(status&&status=="2"){
			MyMessageTip.msg("提示", "该居民已经解约！", true);
			return;
		}
		if(!moduleAppId){
			MyMessageTip.msg("提示", "请正确维护服务项目内容！", true);
			return;
		}
		var entryNames = "";
		for(var i=0;i<dic.items.length;i++){
			var item = dic.items[i];
			if(item.key==moduleAppId){
				entryNames = item.entryNames;
			}
		}
            var result = util.rmi.miniJsonRequestSync({
                serviceId: "chis.signContractRecordService",
                serviceAction: "checkSignContractService",
                method: "execute",
                entryNames: entryNames,
                empiId: this.empiId
            });
            if (result.json.tableAlias) {
                MyMessageTip.msg("提示", "请先完成" + result.json.tableAlias + "任务！", true);
                return;
            }
        this.moduleAppIdSort(this.empiId,moduleAppId);
        this.showEhrViewWin(ehrViewAppId[1],selectRecord.id,taskCode,planId);
        //var status=selectRecord.json.status;
        if(status=="3"){
        	//完成家庭医生任务
			this.updateTaskStatusToNotFinish(selectRecord);
        }
	},
	//暂时测试家医随访完成情况，一次按照第一次，第二次，第三次顺序完成任务
	moduleAppIdSort : function(empiId,moduleAppId){
		var store = this.grid.getStore();
		var items = store.data.items;
		var visitArray = [];
		var childVisitArray = [];
		for(var i=0;i<items.length;i++){
			var itemData = items[i].data;
			var sort = itemData.sort || 0;
			var taskId = itemData.taskId || "";
			var taskMap = {};
			var taskChildMap = {};
			//高血压，糖尿病，老年人，孕产妇随访
			if(((moduleAppId=="GXYSF_GXYFW"&&itemData.moduleAppId=="GXYSF_GXYFW")
				||(moduleAppId=="LNRSF_LNRFW"&&itemData.moduleAppId=="LNRSF_LNRFW")
				||(moduleAppId=="CHFS_YCFFW"&&itemData.moduleAppId=="CHFS_YCFFW")
				||(moduleAppId=="CJ_YCFFW"&&itemData.moduleAppId=="CJ_YCFFW")
				||(moduleAppId=="JTFS_ETFW"&&itemData.moduleAppId=="JTFS_ETFW")
				||(moduleAppId=="TNBSF_TNBFW"&&itemData.moduleAppId=="TNBSF_TNBFW"))
				&&itemData.empiId==empiId){
				taskMap.sort = sort;
				taskMap.taskId = taskId;
				visitArray.push(taskMap);
			}
			//儿童月龄
			if(((moduleAppId=="ETJKJC_ETFWONE"&&itemData.moduleAppId=="ETJKJC_ETFWONE")
			    ||(moduleAppId=="ETJKJC_ETFWTOW"&&moduleAppId=="ETJKJC_ETFWTOW")
			    ||(moduleAppId=="ETJKJC_ETFWTHREE"&&itemData.moduleAppId=="H_ETJKJC_ETFWTHREE99"))
			    &&itemData.empiId==empiId){
				taskChildMap.age = sort;
				taskChildMap.taskId = taskId;
					childVisitArray.push(taskChildMap);
				}
		}
		//根据配置进行排序
		for(var i=0;i<visitArray.length-1;i++){
			for(var j=0;j<visitArray.length-1-i;j++){
				if(visitArray[j].sort>visitArray[j+1].sort){
					var temp = visitArray[j];
					visitArray[j] = visitArray[j+1];
					visitArray[j+1] = temp;
				}
			}
		}
		if(visitArray){
			this.visitArray = visitArray;
		}
		if(childVisitArray){
			this.childVisitArray = childVisitArray;
		}
	},
	onStoreLoadData : function(store, records, ops) {
		chis.application.scm.sr.script.SrContractRecordListView.superclass.onStoreLoadData
				.call(this, store, records, ops);
		// for(var i = 0 ; i < records.length;i++){
		// 	var record = records[i]
		// 	record.data.ServiceTime = record.data.TOTSERVICETIMES - record.data.SERVICETIMES;
		// 	record.json.ServiceTime = record.data.ServiceTime;
		// }
		// store.each(function(r){
		// 	console.log(r)
		// 		r.data.ServiceTime = r.data.TOTSERVICETIMES - r.data.SERVICETIMES;
		// 		r.json.ServiceTime = r.data.ServiceTime;
		// 		r.set("ServiceTime",0)
		// })
		// this.grid.fireEvent("rowclick", this);
		// var girdcount = 0;
		// store.each(function(r) {
		// 	var status = r.get("status");
		// 	if (status==1) {
		// 		this.grid.getView().getRow(girdcount).style.backgroundColor = '#B3EE3A';
		// 	}else if(status==0){
		// 		this.grid.getView().getRow(girdcount).style.backgroundColor = '#FFBBFF';
		// 	}else if(status==3){
		// 		this.grid.getView().getRow(girdcount).style.backgroundColor = '#FFc400';
		// 	}else{
		// 		this.grid.getView().getRow(girdcount).style.backgroundColor = '#AAAAAA';
		// 	}
		// 	girdcount += 1;
		// }, this);
		// this.refresh();
	},
	showEhrViewWin : function(moduleAppId,taskId,taskCode,planId) {
		var cfg = {};
		cfg.closeNav = true;
		var visitModule = [moduleAppId];
		cfg.initModules = visitModule;
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["module"+moduleAppId];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["module"+moduleAppId] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.exContext.ids.visitArray = this.visitArray;
		module.exContext.ids.childVisitArray = this.childVisitArray;
		module.exContext.ids.taskId = taskId;
		module.exContext.ids.taskCode = taskCode;
		module.exContext.openerList = this;
		module.exContext.args.selectedPlanId = planId;
		module.getWin().show();
		
	},
	updateTaskStatusToNotFinish: function (selectRecord) {
        if (selectRecord.data.taskId) {
            util.rmi.jsonRequest({
                serviceId: "chis.signContractRecordService",
                method: "execute",
                serviceAction: "updateTaskStatus2",
                taskId: selectRecord.data.taskId
            }, function (code, msg, json) {
                if (code > 300) {
                    return;
                } else {
                    this.refresh();
                }
            }, this);
        }
    },
//	loadBagDetail : function(selectRecord) {
//		var me = this;
//		var SCID = selectRecord.get('SCID');
//		me.SCID = SCID;
//		var BagDetailList = this.opener['refSCMBagList_refModule'];
//		BagDetailList.requestData.cnd = ['and',
//				['eq', ['$', 'a.SCID'], ['s', SCID]]];
//		BagDetailList.refresh();
//	}
	doEditor : function () {
		var module = this.createModule("NewServiceForm", this.refModule);
		var record = this.getSelectedRecord();
		if(record.data.moduleAppId !="" || (record.data.TOTSERVICETIMES>0 && record.data.TOTSERVICETIMES<=record.data.SERVICETIMES)){
            MyMessageTip.msg("提示", "请选择可手动履约任务！", true);
			return;
		}
		module.selectedR = record;
		module.personInfo = this.opener.scpList.getSelectedRecord();
		module.on("afterSave", this.afterSave, this);
		module.initDataId = null;
		Ext.apply(module.exContext, this.exContext);
		module.exContext.control = {};
		var win = module.getWin();
		var x = (document.body.clientWidth - win.getWidth()) / 2;
		var y = (document.body.clientHeight - 400) / 2;
		win.setTitle(module.title)
		win.setPosition(x, y);
		win.add(module.initPanel())
		win.show();
		module.doNew();
		module.setDatas();
	},	
	afterSave : function(){
		this.refresh();
	},
	doQuery : function () {
		var module = this.createSimpleModule(
				"SrContractServiceRecordListView", this.refServiceRecord);
		module.selectedR = this.getSelectedRecord();
		module.personInfo = this.opener.scpList.getSelectedRecord();
		module.opener=this;
		var win = module.getWin();
		win.setTitle("履约记录查询");
		win.show();
        setTimeout(function(){
			module.loadData();
        }.bind(this),100);
	},
	doTnbscbg:function(){
		        var module = this.createSimpleModule(
				"SrContractServiceRecordListView", this.refServiceRecord);
		var selectedR = this.getSelectedRecord();	
		var data=selectedR.data;
		var empiId=data.empiId;
		debugger;
		var res = util.rmi.miniJsonRequestSync({
					              serviceId : "chis.signContractRecordService",
					              serviceAction : "getjmxx",
					              method: "execute",
					              body : {
						                      "empiid" :empiId
					                         }
				         });
		        var jmxx=res.json["jmxx"];
				 if(jmxx==null||jmxx=="undefind"){
		        	  MyMessageTip.msg("提示", "未查询到居民的门诊就诊号码", true);
		        	  return;
		        }
				var hzjzkh=jmxx.BRID;
				var hzsfzh=jmxx.idCard;
				var mzh=jmxx.MZHM;
				var yybh=data.createUnit;
				var ysbh=data.createUser;
	            this.tnbbfzscurl="http://10.2.200.202:8070/his/memberIntelligentResult.do?yybh="+yybh+"&ysbh="+ysbh+"&hzjzkh="+hzjzkh+"&hzsfzh="+hzsfzh+"&mzh="+mzh+"&zyh=&viewReportOnly=1";
                 window.open( this.tnbbfzscurl,this.panel);
	}
});
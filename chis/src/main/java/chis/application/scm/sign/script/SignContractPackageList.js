$package("chis.application.scm.sign.script")
$import("chis.script.SelectList")

chis.application.scm.sign.script.SignContractPackageList = function (cfg) {
    cfg.autoLoadData = true;
    cfg.pageSize = 500;
    chis.application.scm.sign.script.SignContractPackageList.superclass.constructor.apply(this, [cfg]);
    this.on("loadData", this.onLoadData, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}

Ext.extend(chis.application.scm.sign.script.SignContractPackageList, chis.script.SelectList, {
    initPanel: function (sc) {
        if (this.grid) {
            if (!this.isCombined) {
                this.fireEvent("beforeAddToWin", this.grid)
                this.addPanelToWin();
            }
            return this.grid;
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

        this.store = this.getStore(items);
        var sm = new Ext.grid.CheckboxSelectionModel({
            checkOnly: true,
            singleSelect: !this.mutiSelect
        })
        this.sm = sm;
        sm.on("rowselect", function (sm, rowIndex, record) {
	    	if(this.sciForm.form.getForm().findField("peopleFlag").value==""){
	    		MyMessageTip.msg('提示', '请选择人群分类！', true);
			    return;
	    	}
            var total = 0;
            var total1 = 0;
            var itsto = this.grid.store.data.items
            var items = sm.selections.items;
            if (items.length > 0) {
                var item = items[items.length - 1]
                var groupId = item._groupId;
                for (var i = 0; i < itsto.length; i++) {
                    if (groupId == itsto[i]._groupId) {
                        total++
                    }
                }
                for (var i = 0; i < items.length; i++) {
                    if (groupId == items[i]._groupId) {
                        total1++
                    }
                }
                var id = item.data.SPID;
                if (total == total1) {
                    if(document.getElementById(id))document.getElementById(id).checked = true;
                } else {
                    if(document.getElementById(id))document.getElementById(id).checked = false;
                }
            }
            if (this.mutiSelect) {
                this.selects[record.id] = record
            } else {
                this.singleSelect = record
            }
            this.genTotalCost(sm);
            this.synchSelectZtItems(sm,record);
        }, this)
        sm.on("rowdeselect", function (sm, rowIndex, record) {
            var id = record.data.SPID;
            document.getElementById(id).checked = false;

            if (this.mutiSelect) {
                delete this.selects[record.id]
            } else {
                this.singleSelect = {}
            }
            this.genTotalCost(sm);
            this.synchSelectZtItems(sm,record);
        }, this)
        this.cm = new Ext.grid.ColumnModel(this.getCM(items, this.sm))
        var cfg = {
            border: false,
            store: this.store,
            cm: this.cm,
            sm: this.sm,
            height: this.height,
            loadMask: {
                msg: '正在加载数据...',
                msgCls: 'x-mask-loading'
            },
            buttonAlign: 'center',
            clicksToEdit: true,
            frame: true,
            plugins: this.rowExpander,
            // stripeRows : true,
            view: this.getGroupingView()
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
                cfg.tbar = cndbars.concat(this.tbar || [])
                cfg.buttons = this.createButtons()
            }
        }
        this.grid = new this.gridCreator(cfg)
        this.schema = schema;
        this.grid.on("afterrender", this.onReady, this)
        if (!this.isCombined) {
            this.fireEvent("beforeAddToWin", this.grid)
            this.addPanelToWin();
        }
		this.grid.on("afteredit", this.afterCellEdit, this);
		this.grid.on("beforeedit", this.beforeCellEdit, this);
		var lab = new Ext.form.Label({
					html : "<div id='TOP_SHOW' align='center' style='color:blue'>共选择0个服务包0个组套0项，合计总金额:￥0.00</div>"
				});
		this.pagingToolbar.items.insert(0, "lab", lab);
        return this.grid;
    },
    genTotalCost : function(sm){
    	var items = sm.selections.items;
        if(items.length>0){
        	var SPIDS = "";
        	var ztmcs = "";//组套名称
        	var isUserPrice_SPIDS = "";
        	var totalCost = 0;
	        for (var i = 0; i < items.length; i++) {
	        	if(SPIDS.indexOf(items[i].data.SPID)==-1){
	        		SPIDS += items[i].data.SPID+",";
	        	}
	            if (items[i].data.isUsePrice==0) {
	                totalCost = parseFloat(totalCost) + parseFloat(items[i].data.realPrice)*parseFloat(((items[i].data.SERVICETIMES==null || items[i].data.SERVICETIMES=="")?0:items[i].data.SERVICETIMES));
	            }
	        	if(items[i].data.isUsePrice==1 && isUserPrice_SPIDS.indexOf(items[i].data.SPID)==-1){
	                totalCost = parseFloat(totalCost) + parseFloat(items[i].data.spRealPrice);
	        		isUserPrice_SPIDS += items[i].data.SPID+",";
	        	}
        		if(items[i].data.ztmc!="" && ztmcs.indexOf(items[i].data.SPID+items[i].data.ztmc)==-1){
        			ztmcs += items[i].data.SPID+items[i].data.ztmc+",";
        		}
	        }
	        var spcount = (SPIDS==""?0:(SPIDS.split(',').length-1));
	        var ztcount =  (ztmcs==""?0:(ztmcs.split(',').length-1));
        	document.getElementById("TOP_SHOW").innerHTML = "共选择"+spcount+"个服务包"+ztcount+"个组套"+items.length+"项，合计总金额:" + totalCost.toFixed(2);
        	this.sciForm.signServicePackages = SPIDS.substring(0,SPIDS.length-1);
        }else{
        	document.getElementById("TOP_SHOW").innerHTML = "共选择0个服务包0个组套0项，合计总金额:0.00";
        	this.sciForm.signServicePackages = "";
        }
    },
    //同步勾选组套下所有服务项
    synchSelectZtItems : function(sm,record){
    	if(!record.data.ztmc || record.data.ztmc==""){
    		return;
    	}
    	var isExistSelectedZtItem = false;//已勾选服务项中是否存在同一组套下的其他服务项
    	var isSelected_record = false; //当前记录行是否被勾选
        var itemIds = "";
    	var items = sm.selections.items;
        if(items.length>0){
	        for (var i = 0; i < items.length; i++) {
	        	if(record.data.SPID == items[i].data.SPID && record.data.SPIID != items[i].data.SPIID && record.data.ztmc == items[i].data.ztmc){
	        		isExistSelectedZtItem = true;
	        		itemIds += items[i].data.SPID + items[i].data.SPIID + ",";
	        	}
	        	if(record.data.SPID == items[i].data.SPID && record.data.SPIID == items[i].data.SPIID){
	        		isSelected_record = true;
	        	}
	        }
        }
        if(isSelected_record && !isExistSelectedZtItem){
        	//查找当前服务包下同一组套下其他服务项，并勾选
	    	var its = sm.grid.store.data.items;
	        for (var i = 0; i < its.length; i++) {
/*	            if (record.data.SPID == its[i].data.SPID && record.data.SPIID != its[i].data.SPIID && record.data.ztmc == its[i].data.ztmc) {
					sm.grid.getSelectionModel().selectRow(i,[true]);
	            }*/
	            if (record.data.SPID == its[i].data.SPID &&itemIds.indexOf(its[i].data.SPID + its[i].data.SPIID + ",") == -1 && record.data.ztmc == its[i].data.ztmc) {
					sm.grid.getSelectionModel().selectRow(i,[true]);
	            }
	        }
        }
        if(!isSelected_record && isExistSelectedZtItem){
        	//查找当前服务包下同一组套下其他服务项，并取消勾选
	    	var its = sm.grid.store.data.items;
	        for (var i = 0; i < its.length; i++) {
/*	            if (record.data.SPID == its[i].data.SPID && record.data.SPIID != its[i].data.SPIID && record.data.ztmc == its[i].data.ztmc) {
					sm.grid.getSelectionModel().deselectRow(i);
	            }*/
	            if (record.data.SPID == its[i].data.SPID &&itemIds.indexOf(its[i].data.SPID + its[i].data.SPIID + ",") > -1 && record.data.ztmc == its[i].data.ztmc) {
					sm.grid.getSelectionModel().deselectRow(i);
	            }
	        }
        }
    },
    onReady: function () {
        var id = "";
        var _this = this;
        //初始化
        var flag = false;
        
        this.store.on('load', function (store, records) {
        	_this.grid.view.collapseAllGroups();
            store.each(function (r) {
                if (!flag) {
                    _this.getIntendedPopulationMap(_this);
                }
                flag = true;
                var cbox = Ext.get(r.data.SPID);
                //var cbox = Ext.get(r.data.itemCode);
                if (id && id == cbox.id) {
                    return;
                }
/*                if (r.data.itemCode == "203062279") {
                    cbox.dom.checked = false;
                    cbox.dom.disabled = false;
                }*/
                id = cbox.id;
                cbox.on("change", function (checkBox) {
			    	if(_this.sciForm.form.getForm().findField("peopleFlag").value==""){
			    		MyMessageTip.msg('提示', '请选择人群分类！', true);
					    return;
			    	}
                    if (checkBox.target.checked) {
                    	//服务包名称
                    	var packagename = r.data.packageName;
                    	debugger;
                    	if(packagename=="基本服务包"||packagename=="基本服务包（不含基本医疗服务）"||packagename=="基本服务包(减免)"){
                    		var packagename =_this.sciForm.form.getForm().findField("signServicePackages").value;
                	    	if(packagename=="基本服务包"||packagename=="基本服务包（不含基本医疗服务）"||packagename=="基本服务包(减免)"){
                	    		MyMessageTip.msg('提示', '该居民已签约 "'+packagename+'" 请确认是否需要重复签约基本服务包', true);
                	    	}
                    	}
                        //人群分类
                        var intendedPopulation = _this.intendedPopulationMap[checkBox.target.id];
                        var pData = _this.exContext.args.pData || _this.exContext.empiData;
                        //年龄
                        var age = pData.age;
                        //性别
                        var sexCode = "";
                        if (pData.sexCode.key) {
                            sexCode = pData.sexCode.key;
                        } else {
                            sexCode = pData.sexCode;
                        }
//                        if (intendedPopulation == "04") {
//                            //高血压签约包限制
//                        }
//                        if (intendedPopulation == "05") {
//                            //糖尿病签约包限制
//                        }
//                        if (intendedPopulation == "02") {
//                            if (age > 6) {
//                                MyMessageTip.msg('提示', '当前居民年龄不适合签当前签约包', true);
//                                cbox.dom.checked = false;
//                                return;
//                            }
//                        }
//                        if (intendedPopulation == "01") {
//                            if (sexCode == "1") {
//                                MyMessageTip.msg('提示', '当前居民性别不适合签当前签约包', true);
//                                cbox.dom.checked = false;
//                                return;
//                            }
//                        }
//                        if (intendedPopulation == "03") {
//                            if (age < 60) {
//                                MyMessageTip.msg('提示', '当前居民年龄不适合签当前签约包', true);
//                                cbox.dom.checked = false;
//                                return;
//                            }
//                        }

                        var rows = [];
                        var its = _this.sm.grid.store.data.items;
                        for (var i = 0; i < its.length; i++) {
                            if (cbox.id == its[i].data.SPID) {
                                _this.selects[its[i].data.SPIID] = its[i].data
                                rows.push(i)
                            }
                        }
                        _this.sm.grid.getSelectionModel().selectRows(rows, [true]);
                    } else {
                        var its = _this.sm.grid.store.data.items;
                        for (var i = 0; i < its.length; i++) {
                            if (cbox.id == its[i].data.SPID) {
                                _this.sm.grid.getSelectionModel().deselectRow(i)
                            }
                        }
                    }
                });
            }, this);
	        //组套名称设置为红色
	        for (var i = 0; i < records.length; i++) {
	        	if(records[i].data.ztmc != ""){
	            	records[i].set("ztmc", "<font style='color:red'>"+records[i].data.ztmc+"</font>");
	        	}
	        	//基本服务包收费项目不收费显示
	            if(records[i].data.packageName=="基本服务包"||records[i].data.packageName=="基本服务包（不含基本医疗服务）"||records[i].data.packageName=="基本服务包(减免)"){
              	  if(records[i].data.discount != null){
              		records[i].set("discount", null);
              		records[i].set("price", 0);
              		records[i].set("realPrice", 0);
    	        	}
                } 
	        }
        });
    }
    ,
//获取包Id与人群字段对应参数
    getIntendedPopulationMap: function (_this) {
        var map = {};
        var spid = "";
        for (var i = 0; i < _this.store.data.items.length; i++) {
            var data = _this.store.data.items[i].data;
            if (spid && spid == data.SPID) {
                continue;
            }
            spid = data.SPID;
            map[spid] = data.intendedPopulation;
        }
        _this.intendedPopulationMap = map;
    }
    ,
    getGroupingView: function () {
        return new Ext.grid.GroupingView({
            showGroupName: true,
            enableNoGroups: false,
            hideGroupedColumn: true,
            enableGroupingMenu: false,
            startCollapsed: true,//开始收起所有分组
            columnsText: "表格字段",
            groupByText: "使用当前字段进行分组",
            showGroupsText: "表格分组",
            groupTextTpl: "<table cellpadding='0' cellspacing='0'><tr><td width='10px'> <input type='checkbox' id='{[values.rs[0].data.SPID]}' name='SPID'/></td>"
                + "<td width='180px' style='max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.packageName]}</td>"
                // + "<td width='75px' style='max-width:75px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.realPrice]}</td>"
                // + "<td width='75px' style='max-width:75px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.serviceTimes]}</td>"
                + "<td width='70px' style='max-width:150px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.packageIntendedPopulation_text]}</td>"
                + "<td width='20px' style='max-width:500px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.intro]}</td>"
                + "<td width='10px' style='max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>{[values.rs[0].data.remark]}</td>"
                + "<td width='100px'>(共{[values.rs.length]}项)</td></tr></table>",
            getRowClass: this.getRowClass
        });
    }
    ,
    getStore: function (items) {
        var o = this.getStoreFields(items);
        var reader = new Ext.data.JsonReader({
            root: "body",
            totalProperty: "totalCount",
            id: o.pkey,
            fields: o.fields
        });
        var url = ClassLoader.serverAppUrl || "";
        var proxy = new Ext.data.HttpProxy({
            url: url + "*.jsonRequest",
            method: "post",
            jsonData: this.requestData
        });
        proxy.on("loadexception", function (proxy, o, response, arg, e) {
            if (response.status == 200) {
                var json = eval("(" + response.responseText + ")");
                if (json) {
                    var code = json["x-response-code"];
                    var msg = json["x-response-msg"];
                    this.processReturnMsg(code, msg, this.refresh);
                }
            } else {
                this.processReturnMsg(404, "ConnectionError", this.refresh);
            }
        }, this);
        var store = new Ext.data.GroupingStore({
            reader: reader,
            proxy: proxy,
/*            sortInfo: {
                field: "SPID",
                direction: "DESC"
            },*/
            groupField: "SPID"
        });
        //store.on("load",this.onStoreLoadData,this);
        store.on("beforeload", this.onStoreBeforeLoad, this);
        return store;
    },
    onLoadData: function (store) {
        //加载出访签约记录所包括的包
        util.rmi.jsonRequest({
            serviceId: "chis.signContractRecordService",
            method: "execute",
            serviceAction: "loadTheSignContractPackage",
            body: {"SCID": this.exContext.args.SCID || ''}
        }, function (code, msg, json) {
            if (code > 300) {
                return;
            }
            if (json.body) {
                this.rs = json.body;
                 // 2019-07-29 zhaojian 此处停顿一秒后进行数据对比并勾选已签约项目
                setTimeout(function(){
                  var items = this.store.data.items;
                  for (var i = 0, len = this.rs.length; i < len; i++) {
                      var r = this.rs[i];
                      var SPIID = r.SPIID;
                      for (var j = 0; j < items.length; j++) {	  
                          if (SPIID == items[j].data.SPIID) {
                              this.grid.getSelectionModel().selectRow(j, [true])
                          }
                         //基本服务包收费项目不收费显示
                          if(items[j].data.packageName=="基本服务包"||items[j].data.packageName=="基本服务包（不含基本医疗服务）"||items[j].data.packageName=="基本服务包(减免)"){
                        	  if(items[j].data.discount != null){
                        		items[j].set("discount", null);
                        		items[j].set("price", 0);
                        		items[j].set("realPrice", 0);
              	        	}
                          }  
                          
                      }          
                  }
        		}.bind(this),1000);
            }
        }, this);
    },
    doSave: function () {
    	var idcard ="";
    	var age = 0;
    	idcard = this.exContext.empiData.idCard;
    	age = this.exContext.empiData.age;
		var res = util.rmi.miniJsonRequestSync({
			serviceId : "chis.signContractRecordService",
			serviceAction : "loadSignedRecord",
			method:"execute",
			body : {
				empiid : this.exContext.empiData.empiId
			}
		});
		if (res.code > 300) {
			return false;
		} else {
        	if(res.json && res.json.body && res.json.body.length>0){
        		var qyxx ="";
				var enddate;
        		var sdate;
				debugger;
        		for(var i=0;i<res.json.body.length;i++){
        			qyxx+=(i+1)+". "+res.json.body[i].ORGANIZNAME+"（"+res.json.body[i].NAME+"于"+res.json.body[i].CREATEDATE+"录入）<br>";
					enddate = Date.parseDate(res.json.body[i].ENDDATE, "Y-m-d");
        			sdate = Date.parseDate(res.json.body[i].SDATE, "Y-m-d");
        		}
				debugger;
        		if(qyxx != "" && enddate>sdate){
					debugger;
        	    	var packages = util.rmi.miniJsonRequestSync({
        				serviceId : "chis.signContractRecordService",
        				serviceAction : "getSigPack",
        				method:"execute",
        				body : {
        					"empiid" : this.exContext.empiData.empiId
        				}
        			});
        	    	var qypack = packages.json.body.SIGNSERVICEPACKAGES.split(",");
        	    	var items = this.sm.selections.items;
				    for (var i = 0; i < qypack.length; i++) {
//        				 if(qypack[i].data.packageName=="基本服务包"||qypack[i].data.packageName=="基本服务包(减免)"){
//        					 MyMessageTip.msg("该病人已在如下单位签约过，11个月内不能重复签约：", qyxx+qypack, true);
//           				     return;
//     		        	} 
        				 
        				 for(var j = 0; j < items.length; j++){
        					 if(items[j].data.packageName==qypack[i]){
        						MyMessageTip.msg("该病人已在如下单位签约过，11个月内不能重复签约：", qyxx+qypack, true);
                 				return; 
        					 }
        				 }
        			 }
					
        		}
        	}
		}
    	if(age>6 && idcard==""){
            return MyMessageTip.msg("提示","身份证号不能为空，请实名签约！",true);
    	}
    	if(this.sciForm.form.getForm().findField("peopleFlag").value==""){
    		MyMessageTip.msg('提示', '请选择人群分类！', true);
		    return;
    	}
        //先勾选某服务包下的一个或多个服务项之后，再全选该服务包，存入this.selects的数据对象不一致，导致之前已勾选的服务项的r.data取不到，实际上r对象就是要取的r.data
        var saveData = [];
        for (var id in this.selects) {
            var r = this.selects[id]
            if(r.data==undefined){
            	saveData.push(r);
            }else{
            	saveData.push(r.data);
            }
        }
        this.fireEvent("saveList", saveData);
    },
	doRqflQuery : function(selectvalue){
		debugger;
		if(this.initCnd==undefined){
			this.initCnd=this.requestData.cnd;
		}else{
			this.requestData.cnd = this.initCnd;
		}
		var excludeArr = ["20","21","22"];
		if(selectvalue.indexOf(",")!=-1){
			var temptnd = "";
			var arr = selectvalue.split(",");	
			for(var element in excludeArr){
				arr.remove(excludeArr[element]);
			}
			for(var i=0;i<arr.length;i++){
				if(excludeArr.indexOf(arr[i]) == -1){
					if(this.requestData.cnd.indexOf(["like", ["$", "c.intendedPopulation"], ["s", "%"+arr[i]+"%"]]) > -1){
						continue;
					}
					if(i==0){
						temptnd = ["like", ["$", "c.intendedPopulation"], ["s", "%"+arr[i]+"%"]];
					}else{
						temptnd = ["or",temptnd,["like", ["$", "c.intendedPopulation"], ["s", "%"+arr[i]+"%"]]];
					}
				}
			}
			if(arr.length > 0){
				this.requestData.cnd = this.requestData.cnd.length==0 ? ["or",temptnd] : ["and",this.requestData.cnd,["or",temptnd]];
			}
		}else{
			if(excludeArr.indexOf(selectvalue) == -1){
				this.requestData.cnd = this.requestData.cnd.length==0 ? ["like", ["$", "c.intendedPopulation"], ["s", "%"+selectvalue+"%"]] : ["and",this.requestData.cnd,["like", ["$", "c.intendedPopulation"], ["s", "%"+selectvalue+"%"]]];
			}
		}
		document.getElementById("TOP_SHOW").innerHTML = "共选择0个服务包0个组套0项，合计总金额:0.00";
        for (var id in this.selects) {
            delete this.selects[id];
    	}
		this.refresh();
	},
    doStop: function () {
		this.fireEvent("stopList");
    },
    doPrintProcotol: function () {
        // alert("健康检查打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
        /*if (this.data) {
            this.empiIdCheck = this.data.empiId;
        } else {
            return;
        }*/
        //协议书新
    	 var records = this.getSelectedRecords();
    	 records[0].data.remark;
		 var list=[];
    	 for(var i = 0; i <records.length; i = i + 1){
			 list.push(records[i].data.remark);
	 	 if(records[i].data.remark==-1){
      list.push(records[i].data.remark);
     }
	
   }
   var arr=list;
   var hash=[];
  for (var i = 0; i < arr.length; i++) {
     if(hash.indexOf(arr[i])==-1){
      hash.push(arr[i]);
     }
  }
  var zj= ["1","2","3"];
  var zj1= ["1", "2"];
  var zj2= ["1", "3"];
  var zj3= ["2", "3"];
  var zj4= ["1"];
  var zj5= ["2"];
  var zj6= ["3"];
 // alert(zj.sort().toString()== hash.sort().toString())
  debugger;
  if(zj.sort().toString()== hash.sort().toString()){
  	 var url = "resources/chis.prints.template.familyContractBaseLs1.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj.sort().toString()
  }else if(zj1.sort().toString()== hash.sort().toString()){
  	 var url = "resources/chis.prints.template.familyContractBaseLs2.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj1.sort().toString()
  }else if(zj2.sort().toString()== hash.sort().toString()){
  	var url = "resources/chis.prints.template.familyContractBaseLs3.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj2.sort().toString()
  }else if(zj3.sort().toString()== hash.sort().toString()){
  	var url = "resources/chis.prints.template.familyContractBaseLs4.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj3.sort().toString()
  }else if(zj4.sort().toString()== hash.sort().toString()){
  	var url = "resources/chis.prints.template.familyContractBaseLs5.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj4.sort().toString()
  }else if(zj5.sort().toString()== hash.sort().toString()){
  	var url = "resources/chis.prints.template.familyContractBaseLs.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj5.sort().toString()
  }else if(zj6.sort().toString()== hash.sort().toString()){
  	var url = "resources/chis.prints.template.familyContractBaseLs6.print?type=" + 1
            + "&SCID=" + this.exContext.args.SCID +"&empiId=" + this.exContext.ids.empiId + "&zj=" +zj6.sort().toString()
  }
      //  var url = "resources/chis.prints.template.familyContractBaseLs.print?type=" + 1
        //    + "&SCID=" + this.exContext.args.SCID
        url += "&temp=" + new Date().getTime()
        var win = window
            .open(
                url,
                "",
                "height="
                + (screen.height - 100)
                + ", width="
                + (screen.width - 10)
                + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
        if (Ext.isIE6) {
            win.print()
        } else {
            win.onload = function () {
                win.print()
            }
        }
    },
    copyOldValues: function(){
        var oldValues = this.getSelectedRecords();
        // console.log(oldValues);
        // for(var r in oldValues){
        //     oldValues[r].id = null;
        //     // oldValues[r].data._opStatus = "create";
        // }
        // this.clearSelect();
        // this.grid.getSelectionModel().selectRecords(oldValues)
        // console.log(this.getSelectedRecords())
        return oldValues;
    },
	onBeforeCellEdit : function(it, record, field, value) {
		if (record.data.ztmc != "") {
            record.data.serviceTimes_beforEdit = record.data.SERVICETIMES;
		}
	},
	onAfterCellEdit : function(it, record, field, value) {
		if(record.data.SERVICETIMES<=0){
            MyMessageTip.msg('提示', '服务次数必须输入正整数！', true);
            record.set("SERVICETIMES", record.data.serviceTimes_beforEdit);
            return;
		}
		if (record.data.ztmc != "" && record.data.serviceTimes_beforEdit != record.data.SERVICETIMES) {
			if(record.data.serviceTimes_beforEdit == record.data.SERVICETIMES){
				return;
			}
			//计算倍数
			var multiple = 1;
			if((record.data.serviceTimes_beforEdit < record.data.SERVICETIMES && record.data.SERVICETIMES%record.data.serviceTimes_beforEdit != 0)
			||(record.data.serviceTimes_beforEdit > record.data.SERVICETIMES && record.data.serviceTimes_beforEdit%record.data.SERVICETIMES != 0)){
                MyMessageTip.msg('提示', '组套服务项服务次数必须按倍数修改！', true);
                record.set("SERVICETIMES", record.data.serviceTimes_beforEdit);
                return;
			}
			multiple = record.data.serviceTimes_beforEdit < record.data.SERVICETIMES ? record.data.SERVICETIMES/record.data.serviceTimes_beforEdit : record.data.serviceTimes_beforEdit/record.data.SERVICETIMES;
            //次数变小时需遍历当前服务包下同一组套中所有服务项的服务次数校验是否全部符合成倍数修改
	    	var its = this.sm.grid.store.data.items;
	    	if(record.data.serviceTimes_beforEdit > record.data.SERVICETIMES){
		        for (var i = 0; i < its.length; i++) {
		            if (record.data.SPID == its[i].data.SPID && record.data.SPIID != its[i].data.SPIID && record.data.ztmc == its[i].data.ztmc && its[i].data.SERVICETIMES%multiple != 0) {
		                MyMessageTip.msg('提示', '组套服务项服务次数必须按倍数修改！', true);
                		record.set("SERVICETIMES", record.data.serviceTimes_beforEdit);
		                return;
		            }
		        }
	    	}
	    	//遍历当前服务包下同一组套中所有服务项，批量修改服务次数
	        for (var i = 0; i < its.length; i++) {
	            if (record.data.SPID == its[i].data.SPID && record.data.SPIID != its[i].data.SPIID && record.data.ztmc == its[i].data.ztmc) {
	            	its[i].set("SERVICETIMES", record.data.serviceTimes_beforEdit < record.data.SERVICETIMES ? its[i].data.SERVICETIMES*multiple:its[i].data.SERVICETIMES/multiple);
	            }
	        }
		}
		if (this.sm.selections.contains(record)) {
            this.genTotalCost(this.sm);
		}
	},
	validateSelectedServicePackages : function() {
		var items = this.sm.selections.items;
        if(items.length>0){
        	var JBSPNames = "";//基本服务包名称
	        var JBSPCount = 0;//基本服务包个数
        	var isExistJBSPNoyl = false;//是否存在基本服务包(不含基本医疗服务)
        	var isExistZZSP = false;//是否存在增值服务包
	        for (var i = 0; i < items.length; i++) {
	        	if(items[i].data.packageName.indexOf("基本服务包") >= 0 && JBSPNames.indexOf(items[i].data.packageName+",") == -1){
	        		JBSPNames += items[i].data.packageName+",";
	        	}
	        	if(items[i].data.packageName== "基本服务包（不含基本医疗服务）"){
	        		isExistJBSPNoyl = true;
	        	}
	        	if(items[i].data.packageName.indexOf("基本服务包") == -1 && items[i].data.packageName.indexOf("糖尿病并发症初级筛查包") == -1){
	        		isExistZZSP = true;
	        	}
	        }
	        JBSPCount = (JBSPNames==""?0:(JBSPNames.split(',').length-1));
        	if(!isExistZZSP && JBSPCount>1){
	            MyMessageTip.msg("提示", "基本服务包只能任选其一！", true);
	            return false;
        	}
            if (!this.opener.opener.isSupplementary && isExistZZSP && (isExistJBSPNoyl || JBSPCount != 1)) {
	            MyMessageTip.msg("提示", "签约个性化服务包的同时只能签约基本服务包或基本服务包(减免)，任选其一！", true);
	            return false;
            }
            return true;
        }
	}
})
;
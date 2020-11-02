$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.FaultyDetailList = function(cfg) {
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	cfg.remoteUrl = "Faulty";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{WZMC}</td>';
	phis.application.sup.script.FaultyDetailList.superclass.constructor
			.apply(this, [cfg])
			
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sup.script.FaultyDetailList,
		phis.script.EditorList, {
			delIds:[],
			addIds:[],
			editRecords:[],
			num:0,
			initPanel:function(sc){
				var grid = phis.application.sup.script.FaultyDetailList.superclass.initPanel.call(this,sc)
				grid.getStore().on("load",this.fillTJSL,this);
				grid.getStore().on("remove",function(store,record,index){
					this.delIds.push(record.get("JLXH"));
				},this);
				return grid;
			},
			fillTJSL:function(store,records,options){//
				this.grid.el.mask();
				var bsfs=this.opener.form.form.getForm().findField("BSFS").getValue();
				var bsks=this.opener.form.form.getForm().findField("BSKS").getValue();
				Ext.each(records,function(record){
					var zbxh=record.get("ZBXH");
					var zblb=Ext.getCmp("faultyStautsRadio").getValue().inputValue;
					var body={"KCXH":record.get("KCXH"),"BSFS":bsfs,"BSKS":bsks,"ZBXH":record.get("ZBXH"),"ZBLB":zblb,"WZXH":record.get("WZXH")};
					var r1 = phis.script.rmi.miniJsonRequestSync({
						serviceId : "faultyService",
						serviceAction : "getTjslByWzzd",
						body : body,
						op : this.op
					});
					record.set("TJSL",r1.json.TJSL);
					
					//资产编号回显
					var body={"ZBXH":record.get("ZBXH")};
					var r2 = phis.script.rmi.miniJsonRequestSync({
						serviceId : "faultyService",
						serviceAction : "getZCBH",
						body : body,
						op : this.op
					});
					record.set("FRDB",r2.json.ZCBH);
				},this);
				this.grid.el.unmask();
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mats',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mtssearch'
						}, [{
									name : 'JLXH'
								}, {
									name : 'KSDM'
								}, {
									name : 'KFXH'
								}, {
									name : 'ZBLB'
								}, {
									name : 'WZXH'
								}, {
									name : 'CJXH'
								}, {
									name : 'SCRQ'
								}, {
                                    name : 'SXRQ'
                                }, {
                                    name : 'WZPH'
                                }, {
                                    name : 'WZSL'
                                },{
                                    name : 'WZJG'
                                },{
                                    name : 'WZJE'
                                },{
                                    name : 'KCXH'
                                },{
                                    name : 'WZMC'
                                },{
                                    name : 'WZGG'
                                },{
                                    name : 'WZDW'
                                },{
                                    name : 'PYDM'
                                },{
                                    name : 'WBDM'
                                },{
                                    name : 'JXDM'
                                },{
                                    name : 'QTDM'
                                },{
                                    name : 'GLFS'
                                },{
                                    name : 'ZRSL'
                                },{
                                    name : 'SBBH'
                                },{
                                    name : 'XZBZ'
                                }]);
			},
			setKSZCInfo:function(record){
				var store = this.grid.getStore();
				for(var i=0;i<store.getCount();i++){
					var newrecord=store.getAt(i);
					if(record.get("GLFS")==3){
						if(record.get("JLXH")==newrecord.get("ZBXH")){
							MyMessageTip.msg("提示", "已经存在明细列表中", true);
							return false;
						}
					}else{
						if(record.get("KCXH")==newrecord.get("KCXH")){
							MyMessageTip.msg("提示", "已经存在明细列表中", true);
							return false;
						}
					}
				}
				var rowItem=new store.recordType({});
				store.add(rowItem)
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('WZMC', record.get("WZMC"));
                if(record.get("WZGG")){
                    rowItem.set('WZGG', record.get("WZGG"));
                }
				rowItem.set('WZDW', record.get("WZDW"));
                if(record.get("WZJG")){
                  rowItem.set('WZJG', record.get("WZJG"));  
                }
                rowItem.set('CJXH', record.get("CJXH"));//厂家序号
                rowItem.set('CJMC', record.get("CJMC"));//
                rowItem.set('WZSL', record.get("WZSL"));//物资数量
                
                rowItem.set('WZSL', record.get("WZSL"));
                rowItem.set('TJSL', record.get("WZSL"));
                rowItem.set('WZJG', record.get("WZJG"));//物资价格
                rowItem.set('WZJE', record.get("WZSL")*record.get("WZJG"));//物资金额
                
                rowItem.set('WZPH', record.get("WZPH"));//物资批号
                rowItem.set('SCRQ', record.get("SCRQ"));//生产日期
                rowItem.set('SXRQ', record.get("SXRQ"));//失效日期
                
                rowItem.set('FRDB', record.get("WZBH"));//物资编号
                //库存序号
                rowItem.set('KCXH', record.get("KCXH"));//库存序号
                //账簿序号
                var bsfs=this.opener.form.form.getForm().findField("BSFS").getValue();
				var bsks=this.opener.form.form.getForm().findField("BSKS").getValue();
                
                	if(record.get("GLFS")=='3'){
                		if(Number(record.get('ZBXH'))!=0){
                			rowItem.set('ZBXH',Number(record.get('ZBXH')));
                		}
                	}
                //rowItem.setValue(record.get("WZMC"));
                	this.num=1;
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var wzxh = record.get("WZXH");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("WZXH") == wzxh && i != row) {
						MyMessageTip.msg("提示", "该物资已存在,请修改此物资", true);
						return;
					}
				}
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('WZMC', record.get("WZMC"));
                if(record.get("WZGG")){
                    rowItem.set('WZGG', record.get("WZGG"));
                }
				rowItem.set('WZDW', record.get("WZDW"));
                if(record.get("WZJG")){
                  rowItem.set('WZJG', record.get("WZJG"));  
                }
                rowItem.set('CJXH', record.get("CJXH"));//厂家序号
                rowItem.set('CJMC', record.get("CJMC"));//
                rowItem.set('WZSL', record.get("WZSL"));//物资数量
                
                rowItem.set('WZSL', record.get("WZSL"));
                rowItem.set('TJSL', record.get("WZSL"));
                rowItem.set('WZJG', record.get("WZJG"));//物资价格
                rowItem.set('WZJE', record.get("WZSL")*record.get("WZJG"));//物资金额
                
                rowItem.set('WZPH', record.get("WZPH"));//物资批号
                rowItem.set('SCRQ', record.get("SCRQ"));//生产日期
                rowItem.set('SXRQ', record.get("SXRQ"));//失效日期
                
                //库存序号
                rowItem.set('KCXH', record.get("KCXH"));//库存序号
                //var lzfs=this.faultyForm.getForm().findField("LZFS").getValue();
                
                var bsfs=this.opener.form.form.getForm().findField("BSFS").getValue();
				var bsks=this.opener.form.form.getForm().findField("BSKS").getValue();
                if(bsfs==0){//在库
                	if(record.get("GLFS")=='3'){
                	  rowItem.set('ZBXH',record.get('JLXH'));//账簿序号
                    }else{
                    	rowItem.set('ZBXH',0);//账簿序号
                    }
                }else if(bsfs==1){
                	
                }
                
                obj.setValue(record.get("WZMC"));
                obj.triggerBlur();
              	this.remoteDic.lastQuery = "";
              	this.num=1;
			},
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			doCreate : function(item, e) {
				if(!this.opener.listIsEmpty()){
					return;
				}
				phis.application.sup.script.FaultyDetailList.superclass.doCreate.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
				this.num=1;
			},
			onReady : function() {
				phis.application.sup.script.FaultyDetailList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
				// this.grid.on("keypress", this.onKeyPress, this)
			},
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "WZSL" || it.id == "WZJG") {
					var wzje = 0;
					if (record.get("WZSL")!= 0 && (record.get("WZSL") == null || record.get("WZSL") == ""
							|| record.get("WZJG") == null
							|| record.get("WZJG") == "")) {
						return;
					}
					wzje = (Number(record.get("WZSL")) * Number(record.get("WZJG"))).toFixed(2);
					record.set("WZJE", wzje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					this.num=1;
					// this.calculatEmount();
				}

				// 判断退回数量是否大于可退数量
				if (it.id == "WZSL") {
					if ((Number(record.get("WZSL"))) > (Number(record
							.get("KTSL")))) {
						MyMessageTip.msg("提示", "退回数量不能大于可退数量", true);
					}
				}
			}
		})
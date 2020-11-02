$package("phis.application.hos.script");

$import("phis.script.SimpleModule");
$import("phis.script.util.FileIoUtil");

phis.application.hos.script.YbFeeUploadModule = function(cfg) {
	phis.application.hos.script.YbFeeUploadModule.superclass.constructor
			.apply(this, [cfg]);
	this.bq = 0;
}
Ext.extend(phis.application.hos.script.YbFeeUploadModule,
		phis.script.SimpleModule, {
			// 页面初始化
			initPanel : function(sc) {
//				if (!this.mainApp['phis'].wardId) {
//					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
//					return;
//				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : this.leftTitle,
										region : 'west',
										width : 380,
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : this.rightTitle,
										region : 'center',
										items : this.getRList()
									}],
							tbar : this.getTbar(),
							bbar : this.getBbar()
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refLList);
				this.leftList.on("selectRecord",this.onSelectRecord,this);
				return this.leftList.initPanel();
			},
			// 获取右边的list
			getRList : function() {
				this.rightList = this.createModule("rightList", this.refRList);
				this.rightList.on("afterLoadData",this.onAfterLoadData,this)
				return this.rightList.initPanel();
			},
			// 左边选中刷新右边
			onSelectRecord:function(){
				var records=this.leftList.getSelectedRecords();
				this.rightList.clear();
				var length=records.length;
				if(length==0||!this.cnd){
					return;
				}
				var zyhs=new Array();
				for(var i=0;i<length;i++){
					var r=records[i];
					zyhs.push(r.get("ZYH"));
				}
				
				var bq = "0";
				if(this.BQKScombox){
					bq = this.BQKScombox.getValue();
				}
				var al_zyh = "0";// 病人住院号
				if(this.zyhmText){
					al_zyh=this.zyhmText.getValue();
				}
				var scbz = "0";
				if(this.checkBox){
					scbz=this.checkBox.checked?"1":"0";
				}
				var ldt_fyrq = null;//费用开始日期
				if (this.startDate) {
//					ldt_fyrq = new Date(this.startDate.getValue())
//							.format("Y-m-d");
					ldt_fyrq = this.startDate.getValue();
				}
				var ldt_fyrq2 = null;// 费用截止日期
				if (this.endDate) {
//					ldt_fyrq2 = new Date(this.endDate.getValue())
//							.format("Y-m-d");
					ldt_fyrq2 = this.endDate.getValue();
				}
				if (ldt_fyrq!=null && ldt_fyrq2 != null && new Date(ldt_fyrq) > new Date(ldt_fyrq2)) {
					MyMessageTip.msg("提示", "计费开始日期不能大于截止日期!", true);
//					if (this.startDate) {
//						this.startDate.setValue(this.mainApp.serverDate);
//					}
					return;
				}
				this.cnd=bq + "#" + al_zyh + "#" + scbz + "#" + ldt_fyrq + "#" + ldt_fyrq2;
				this.panel.el.mask("正在查询数据...","x-mask-loading")
				this.rightList.requestData.cnd=this.cnd;
				this.rightList.requestData.ZYHS=zyhs;
				this.rightList.loadData();
			},
			onAfterLoadData:function(){
				this.panel.el.unmask()
			},
			// 刷新
			doRefresh : function() {
				this.leftList.clear();
				this.rightList.clear();
				var bq = "0";
				if(this.BQKScombox){
					bq = this.BQKScombox.getValue();
				}
				var ldt_fyrq = null;//费用开始日期
				if (this.startDate) {
//					ldt_fyrq = new Date(this.startDate.getValue())
//							.format("Y-m-d");
					ldt_fyrq = this.startDate.getValue();
				}
				var ldt_fyrq2 = null;// 费用截止日期
				if (this.endDate) {
//					ldt_fyrq2 = new Date(this.endDate.getValue())
//							.format("Y-m-d");
					ldt_fyrq2 = this.endDate.getValue();
				}
				if (ldt_fyrq!=null && ldt_fyrq2 != null && new Date(ldt_fyrq) > new Date(ldt_fyrq2)) {
					MyMessageTip.msg("提示", "计费开始日期不能大于截止日期!", true);
//					if (this.startDate) {
//						this.startDate.setValue(this.mainApp.serverDate);
//					}
					return;
				}
				var al_zyh = "0";// 病人住院号
				if(this.zyhmText){
					al_zyh=this.zyhmText.getValue();
				}
				var scbz = "0";
				if(this.checkBox){
					scbz=this.checkBox.checked?"1":"0";
				}
				this.cnd=bq + "#" + al_zyh + "#" + scbz + "#" + ldt_fyrq + "#" + ldt_fyrq2;
//				alert("ldt_fyrq:"+ldt_fyrq+";ldt_fyrq2:"+ldt_fyrq2);
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceQuery
						});
				if (r.code == 600) {
					MyMessageTip.msg("提示", "提交天数超过最大天数控制!", true);
					return;
				}
				this.leftList.requestData.cnd=this.cnd;
				this.leftList.loadData();
			},
			getTbar : function() {
				var bqLa = new Ext.form.Label({
							text : "病区"
						});
						// 科室
				var BQKSdic = {
						"id" : "phis.dictionary.department_bq",
						"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
						"src" : "YF_FYJL_LSCX.FYBQ",
						"width" : 130,
						"emptyText" : '全部'
					};
				var BQKScombox = util.dictionary.SimpleDicFactory
						.createDic(BQKSdic);
				this.BQKScombox = BQKScombox;
				BQKScombox.on("select", this.onBqSelect, this);
				BQKScombox.store.on("load", this.bqDicLoad);
				//住院号码
				var zyhmlable = new Ext.form.Label({
							text : "住院号码"
						}) 
				var zyhmText = new Ext.form.TextField({width : 100});
				zyhmText.on("specialkey", this.doRefresh, this);
				this.zyhmText = zyhmText;
				var checkBox = new Ext.form.Checkbox({boxLabel:"重新上传"});
				checkBox.on("check",this.onCheck,this);
				this.checkBox = checkBox;
				// 定义开始时间
				var nowDate=new Date();
				var startDateValue=new Date(nowDate.getFullYear(),nowDate.getMonth(),nowDate.getDate());
				this.startDateValue = startDateValue;
				var startDate=new phis.script.widgets.DateTimeField({
					name : 'startDate',
					width : 145,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					value:startDateValue,
					emptyText : '开始时间',
					readOnly : true
				});
				this.startDate=startDate;
				//住院号码
				var lable = new Ext.form.Label({
							text : "到"
						}) 
				// 定义结束时间
				var endDate=new phis.script.widgets.DateTimeField({
					name : 'dateTo',
					value : nowDate,
					width : 145,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间',
					readOnly : true
				});
				this.endDate=endDate;
				return [bqLa, BQKScombox, zyhmlable, zyhmText,checkBox,startDate,lable,endDate]
						.concat(this.createButtons());
			},
			getBbar : function() {
//				downloadXmlFile("c:\\njyb\\test.xml","hello word");
//				readXmlFile("c:\\njyb\test.xml");
			},
			bqDicLoad : function() {
				var data = {
					"key" : "0",
					"text" : "全部"
				};
				var r = new Ext.data.Record(data);
				this.insert(0, r);
			},
			onBqSelect : function(bqDic) {
				bqDic.emptyText = "";
				this.bq = bqDic.value;
				this.doRefresh();
			},
			onCheck : function(){
				if(!this.checkBox.checked){
					this.startDate.setReadOnly(true);
					this.endDate.setReadOnly(true);
				}else{
					this.startDate.setReadOnly(false);
					this.endDate.setReadOnly(false);
				}
				this.onSelectRecord();
			},
			doConfirm : function() {
				var rs=this.leftList.getSelectedRecords();
				if(rs.length==0){
				MyMessageTip.msg("提示","请先选择记录",true)
				return ;
				}
				var records=this.rightList.getSelectedRecords();
				var length=records.length;
				if(length==0){
					MyMessageTip.msg("提示","请先选择记录",true)
					return;
				}
				this.confirm();
			},
			afterOpen : function() {
				if (this.panel) {
//					if (this.dateField) {
//						this.dateField.setValue(this.mainApp.serverDate);
//					}
					this.doRefresh();
				}
			},
			confirm:function(){
				var leftRecords=this.leftList.getSelectedRecords();
				var zyhs=new Array();
				for(var i=0;i<leftRecords.length;i++){
					var r=leftRecords[i];
					zyhs.push(r.get("ZYH"));
				}
				var al_zyh = "0";// 病人住院号
				var rightRecords=this.rightList.getSelectedRecords();
				var length=rightRecords.length;
				if(length==0){
					MyMessageTip.msg("提示","请先选择记录",true)
					return;
				}
				var jlxhs=new Array();
				for(var i=0;i<length;i++){
					var r=rightRecords[i];
					jlxhs.push(r.get("JLXH"));
				}
				Ext.Msg.confirm("请确认", "是否上传选中费用记录?", function(btn) {
					if (btn == 'yes') {
						var data = {
							"ZYHS" : zyhs,
							"JLXHS": jlxhs
						};
						this.panel.el.mask("正在查询数据...","x-mask-loading")
						phis.script.rmi.jsonRequest({
									serviceId : this.serviceId,
									serviceAction : this.serviceActionSave,
									body : data
								}, function(code, msg, json) {
									this.panel.el.unmask()
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										return;
									}
//									alert(json.xmlStr);
									downloadXmlFile("c:\\njyb\\zyfymx.xml",json.xmlStr);
									this.onSelectRecord();
								}, this)
					}
				}, this);
				return;
			},
			doClose:function(){
				this.getWin().hide();
			}
		});
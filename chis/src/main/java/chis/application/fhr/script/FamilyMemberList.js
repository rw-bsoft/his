$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView");

chis.application.fhr.script.FamilyMemberList = function(cfg) {
	cfg.westWidth = 150;
	chis.application.fhr.script.FamilyMemberList.superclass.constructor.apply(
			this, [ cfg ]);
	this.requestData.queryCndsType = "1";
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.fhr.script.FamilyMemberList,chis.script.BizSimpleListView,
		{
			loadData : function() {
				if (this.initDataId != this.exContext.args.initDataId) {
					this.initCnd = ['and',['eq',[ '$', 'a.familyId' ],['s',this.exContext.args.initDataId ] ],
										[ 'eq', [ '$', 'a.status' ], [ 's', '0' ] ] ];
					this.requestData.cnd = this.initCnd;
					this.initDataId = this.exContext.args.initDataId
					// 清理查询条件
					this.cndFldCombox.setValue();
					this.cndField.setValue();
				}
				chis.application.fhr.script.FamilyMemberList.superclass.loadData.call(this);
			},

			doReadMember : function(item, e) {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				$import("chis.script.EHRView");
				var module = this.midiModules["FamilyMember_EHRView"];
				if (!module) {
					module = new chis.script.EHRView({
						initModules : [ 'B_01', 'B_02', 'B_03', 'B_04',
								'B_05' ],
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp
					});
					this.midiModules["FamilyMember_EHRView"] = module;
					module.on("save", this.refresh, this);
				} else {
					module.actionName = "EHR_HealthRecord";
					module.exContext.ids["empiId"] = empiId;
					module.refresh();
				}
				module.getWin().show();
			},

			onDblClick : function(grid, index, e) {
				this.doReadMember();
			},
			onSave : function(entryName, op, json, rec) {
				this.refresh();
			},
			// 删除家庭成员
			doRemove : function() {
				var _cfg = this;
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var data = {};
				data["phrId"] = r["phrId"]
				this.familyIdIs = r["familyId"];
				var empiId = r.get("empiId");
				var vCount = this.store.getCount();

				// 查询家庭签约，删除的人是否是家庭代表
				var dataFCR = {};
				dataFCR["familyId"] = this.initDataId;
				var v = this.isFCRepre(dataFCR);
				if (v == empiId && vCount != 1) {
					function callback(id) {
						if (id == "yes") {
							_cfg.fireEvent("changeTo");
							return;
						}
					}
					Ext.MessageBox.confirm("选择框","该成员为家庭签约代表，需要先走变更流程，是否变更签约代表？", callback);
				} else {
					function callback(id) {
						if (id == "yes") {
							_cfg.removeById(data);
							_cfg.refresh();
							_cfg.fireEvent("winShow1");
						}
					}
					Ext.MessageBox.confirm("选择框", "是否删除成员?", callback);
				}
			},
			removeById : function(data) {
				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.familyRecordService1",
					serviceAction : "removeFamily",
					method : "execute",
					body : data,
					op : this.op
				}, this)
				var resBody = result.json.body;
				if (resBody && resBody.flage == 1) {
					this.flagEmpty = true;
				}
				this.fireEvent("remove", this.entryName, this.op,
						result, this.data);
			},// 判断是否是签约代表
			isFCRepre : function(data) {

				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.familyRecordService1",
					serviceAction : "getFcRepre",
					method : "execute",
					body : data,
					op : this.op
				}, this)

				var resBody = result.json.body;
				return resBody;

			},
			//签约
			doSign : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var familyId = r.get('familyId');
				var dt = {};
				dt["familyId"] = familyId
				// 判断是否有户主，取户主的empiId
				var empiId = this.isOwnerName(dt);
				if (empiId == null) {
					Ext.Msg.alert("提示", "没有找到该家庭的户主相关信息！");
					return;
				}
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['JY_01']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["FamilyRecordList_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.args={initDataId:familyId};	
					module.on("save", this.onSave, this)
					this.midiModules["FamilyRecordList_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.exContext.args={initDataId:familyId};
					module.refresh()
				}
				module.getWin().show()

			},
			isOwnerName : function(data) {
				var result = util.rmi.miniJsonRequestSync({// 同步方法
					serviceId : "chis.familyRecordService",
					serviceAction : "isOwnerName",
					method : "execute",
					body : data
				})
				if (result.code != 200) {
					Ext.Msg.alert("提示", result.json.msg);
					return null;
				}
				if (result.json.body == null) {
					return;
				}
				return result.json.body.empiId;

			}
		})

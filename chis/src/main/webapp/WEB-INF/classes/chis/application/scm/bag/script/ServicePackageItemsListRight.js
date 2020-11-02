$package("chis.application.scm.bag.script")
$import("chis.script.BizEditorListView")

chis.application.scm.bag.script.ServicePackageItemsListRight = function(cfg) {
	cfg.pageSize = 100;
	chis.application.scm.bag.script.ServicePackageItemsListRight.superclass.constructor
			.apply(this, [cfg]);
	this.requestData.cnd=['eq',['$','a.SPID'],['s',this.SPID||"null"]];
	this.on("loadData", this.getLeftCnd, this)
}

Ext.extend(chis.application.scm.bag.script.ServicePackageItemsListRight,
		chis.script.BizEditorListView, {
			loadData : function() {
				chis.application.scm.bag.script.ServicePackageItemsListRight.superclass.loadData
						.call(this);
			},
			onDblClick : function(grid, index, e) {
				var r = this.grid.store.getAt(index);
				if (!r) {
					return
				}

				r.data.isNew = 0;
				r.data.LOGOFF && r.data.LOGOFF == 1 ? this.doLogOn(r) : this
						.doLogOff(r)
			},
			doLogOn : function(r) {
				util.rmi.jsonRequest({
							serviceId : "chis.signContractRecordService",
							method : "execute",
							serviceAction : "logOnPackagetoServiceItem",
							body : r.data
						}, function(code, msg, json) {
							if (code > 300) {
								return
							} else {
								this.refresh()
							}
						}, this);
			},
			doLogOff : function(r) {
				util.rmi.jsonRequest({
							serviceId : "chis.signContractRecordService",
							method : "execute",
							serviceAction : "logOffPackagetoServiceItem",
							body : r.data
						}, function(code, msg, json) {
							if (code > 300) {
								return
							} else {
								this.refresh()
							}
						}, this);
			},
			doClear : function(r) {
				util.rmi.jsonRequest({
							serviceId : "chis.signContractRecordService",
							method : "execute",
							serviceAction : "logOffPackagetoServiceItem",
							body : r.data
						}, function(code, msg, json) {
							if (code > 300) {
								return
							}
						}, this);
				this.store.remove(r)
			},
			addCol : function(r) {
				r.data.serviceTimes = 1;// 业务暂时默认全为1
				r.data.isNew = 1;
				r.data.SPID = this.SPID
				r.data.LOGOFF = 0;
				this.store.add(r)
			},
			getLeftCnd : function() {
				var records = this.getStoreData();
				var itemCodes = []

				for (i in records) {
					var record = records[i]
					if (record.itemCode)
						itemCodes.push(record.itemCode)
				}
				if (itemCodes.length > 0)
					// this.opener.leftList.requestData.cnd = ['and',
					// 		['eq', ['$', 'a.itemType'], ['s', '4']],
					// 		['notin', ['$', 'a.itemCode'], itemCodes]];
				this.opener.leftList.requestData.cnd =
							['notin', ['$', 'a.itemCode'], itemCodes];
				// else
				// 	this.opener.leftList.requestData.cnd = ['eq',
				// 			['$', 'a.itemType'], ['s', '4']];
				this.opener.leftList.loadData();

			},
			doSave : function() {
                if(!this.SPID || this.SPID == "null" || this.SPID == "")
                    return MyMessageTip.msg("提示", "请先填写保存服务包信息！", true);
				var records = this.getStoreData();
				util.rmi.jsonRequest({
							serviceId : "chis.signContractRecordService",
							method : "execute",
							serviceAction : "savePackagetoServiceItem",
							body : records
						}, function(code, msg, json) {
							if (code > 300) {
								return
							} else {
								this.refresh()
							}

						}, this);
				for (i in records) {
					var record = records[i]
					if (record.isNew)
						record.isNew = 0;
				}

			},
			getStoreData : function() {
				var count = this.store.getCount();
				var lst = [];
				for (var i = 0; i < count; i++) {
					var record = this.store.getAt(i);
					lst.push(record.data);
				}
				return lst;
			}
		});
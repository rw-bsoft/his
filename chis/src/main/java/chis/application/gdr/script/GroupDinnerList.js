$package('chis.application.gdr.script')

$import('chis.script.BizSimpleListView')

chis.application.gdr.script.GroupDinnerList = function(cfg) {
	chis.application.gdr.script.GroupDinnerList.superclass.constructor.apply(this, [cfg]);
	this.xy = [300, 150];
}

Ext.extend(chis.application.gdr.script.GroupDinnerList, chis.script.BizSimpleListView, {

			/**
			 * 发送请求去后台执行删除数据操作，增加参数serviceAction，指定调用服务中的哪个方法，并将发送后台数据的参数名称改为body，与其他函数一致
			 */
			processRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var removeRequest = this.getRemoveRequest(r); // ** 获取删除条件数据
				if (!this.fireEvent("beforeRemove", this.entryName,
						removeRequest)) {
					return;
				}
				this.mask("正在删除数据...");
				util.rmi.jsonRequest({
							serviceId : "chis.groupDinnerService",
							serviceAction : "removeGroupDinnerRecord",
							method:"execute",
							schema : this.entryName,
							body : removeRequest
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.store.remove(r);
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
							} else {
								this.processReturnMsg(code, msg, this.doRemove);
							}
						}, this);
			},

			/**
			 * 获取删除操作的请求数据
			 * 
			 * @param {}
			 *            r
			 * @return {}
			 */
			getRemoveRequest : function(r) {
				return {
					pkey : r.id
				};
			}

		})
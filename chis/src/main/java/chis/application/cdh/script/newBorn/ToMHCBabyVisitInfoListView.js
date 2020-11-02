/**
 * 新生儿随访的基本信息列表
 * 
 * @author :zhouw
 */
$package("chis.application.cdh.script")

$import("chis.script.BizSimpleListView")

chis.application.cdh.script.newBorn.ToMHCBabyVisitInfoListView = function(cfg) {
	cfg.pageSize = -1;
	cfg.autoLoadData = true// 是否自动加载数据
	chis.application.cdh.script.newBorn.ToMHCBabyVisitInfoListView.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;
}
Ext.extend(chis.application.cdh.script.newBorn.ToMHCBabyVisitInfoListView,
		chis.script.BizSimpleListView, {
			loadData : function() {
				if (this.selectId) {
					// 查询条件优先级：出生证号>身份证>母亲身份证,条件满足就return
					if (this.selectId.certificateNo) {// certificateNo
						this.requestData.cnd = this.requestData.cnd
								|| ["eq", ["$", "certificateNo"],
										["s", this.selectId.certificateNo]];
					} else if (this.selectId.idCard) {// babyIdCard
						this.requestData.cnd = this.requestData.cnd
								|| ["eq", ["$", "babyIdCard"],
										["s", this.selectId.idCard]];
					} else if (this.selectId.relativeIdCard) {// motherCardNo
						this.requestData.cnd = this.requestData.cnd
								|| ["eq", ["$", "motherCardNo"],
										["s", this.selectId.relativeIdCard]];
										
					} else {
						this.requestData.cnd = this.requestData.cnd
								|| ["eq", ["$", "babyId"], ["s", '']];
					}
				} else {
					this.requestData.cnd = this.requestData.cnd
							|| ["eq", ["$", "babyId"], ["s", '']];
							
				}
				chis.application.cdh.script.newBorn.ToMHCBabyVisitInfoListView.superclass.loadData
						.call(this);

			}

		});
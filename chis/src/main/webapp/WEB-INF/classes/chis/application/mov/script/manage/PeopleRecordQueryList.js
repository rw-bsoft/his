/**
 * 公共查询列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.manage")
$import("chis.script.BizSelectListView")
chis.application.mov.script.manage.PeopleRecordQueryList = function(cfg) {
    cfg.modal = true;
	chis.application.mov.script.manage.PeopleRecordQueryList.superclass.constructor.apply(this,
			[cfg]);
	this.width = 830
	this.title = "迁移人员档案列表"
	this.enableCnd = false;
	this.on("winShow", this.onWinShow, this);
	this.on("select", this.onRecordSelect, this);
};
Ext.extend(chis.application.mov.script.manage.PeopleRecordQueryList, chis.script.BizSelectListView,
		{

			onWinShow : function() {
				this.loadData()
			},

			onRecordSelect : function(selectedRecord) {
				if (selectedRecord) {
					this.win.hide();
					this.fireEvent("recordSelected", selectedRecord);
				}
			}

		})
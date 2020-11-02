<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.njjb.NJJB" name="南京金保配置管理">
	<catagory id="NJJB" name="南京金保">
		<module id="NJJB01" name="南京金保设置" script="phis.script.TabModule">
			<action id="njjbcsh" viewType="list" name="南京金保设置" ref="phis.application.njjb.NJJB/NJJB/NJJB0101"/>
		</module>
		<module id="NJJB0101" name="南京金保设置-list" type="1" script="phis.application.njjb.script.NjjbbasemessageList">
			<properties>
				<p name="entryName">phis.application.njjb.schemas.NJJB_QD</p>
				<p name="initCnd">['eq',['$','a.USERID'],["$",'%user.userId']]</p>
			</properties>
			<action id="initialization" name="初始化" iconCls="arrow-up" />
			<action id="sign" name="签到" iconCls="arrow-up" />
			<action id="checkout" name="签退" iconCls="arrow-down" />
		</module>
	</catagory>
</application>
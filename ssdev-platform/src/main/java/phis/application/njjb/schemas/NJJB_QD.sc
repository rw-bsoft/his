<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry alias="南京金保-签到" entityName="NJJB_QD" sort="">
	<item alias="业务周期号" id="YWZQH" width="260" not-null="true" pkey="true" type="string" />
	<item alias="用户编码" id="USERID" length="30" not-null="true" type="string"/>
	<item alias="签到时间" id="QDSJ" width="200" type="timestamp"/>
	<item alias="签退时间" id="QTSJ" width="200" type="timestamp"/>
	<item alias="状态" id="STATUS" length="30" type="string">
		<dic>
  		<item key="0" text="正常"/>
  		<item key="1" text="签退"/>
  		</dic>
	</item>
	<item alias="所在机构" id="JGID" length="30" type="string" width="200">
		<dic id="phis.dictionary.manageUnit" />
	</item>
</entry>
